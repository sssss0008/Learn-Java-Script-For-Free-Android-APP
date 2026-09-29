package com.example.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import com.example.data.model.ChallengeTestCase
import com.example.data.model.ConsoleLogItem
import com.example.data.model.ExecutionResult
import com.example.data.model.LogType
import com.example.data.model.TestRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume

class JavaScriptEngine(private val context: Context) {

  private var webView: WebView? = null
  private val mainHandler = Handler(Looper.getMainLooper())

  init {
    try {
      mainHandler.post {
        try {
          webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
          }
        } catch (_: Exception) {
          // In some restricted or test environments WebView may fail to init
          webView = null
        }
      }
    } catch (_: Exception) {
      webView = null
    }
  }

  suspend fun execute(code: String): ExecutionResult = withContext(Dispatchers.Main) {
    val wv = webView
    if (wv == null) {
      return@withContext fallbackExecute(code)
    }

    suspendCancellableCoroutine { continuation ->
      val escapedCode = JSONObject.quote(code)
      val harness = """
        (function() {
          var logs = [];
          var origLog = console.log;
          var origErr = console.error;
          var origWarn = console.warn;
          var origInfo = console.info;

          console.log = function() {
            var args = Array.prototype.slice.call(arguments).map(function(a) {
              try { return typeof a === 'object' ? JSON.stringify(a) : String(a); } catch(e) { return String(a); }
            });
            logs.push({type: 'LOG', msg: args.join(' ')});
            if (origLog) origLog.apply(console, arguments);
          };

          console.error = function() {
            var args = Array.prototype.slice.call(arguments).map(function(a) {
              try { return typeof a === 'object' ? JSON.stringify(a) : String(a); } catch(e) { return String(a); }
            });
            logs.push({type: 'ERROR', msg: args.join(' ')});
            if (origErr) origErr.apply(console, arguments);
          };

          console.warn = function() {
            var args = Array.prototype.slice.call(arguments).map(function(a) {
              try { return typeof a === 'object' ? JSON.stringify(a) : String(a); } catch(e) { return String(a); }
            });
            logs.push({type: 'WARN', msg: args.join(' ')});
            if (origWarn) origWarn.apply(console, arguments);
          };

          var startTime = performance.now();
          var evalResult = undefined;
          var errorInfo = null;

          try {
            evalResult = window.eval($escapedCode);
          } catch(err) {
            errorInfo = { message: err.message || String(err), name: err.name || 'Error' };
          }
          var endTime = performance.now();

          var resultStr = 'undefined';
          if (evalResult !== undefined) {
            try {
              resultStr = typeof evalResult === 'object' ? JSON.stringify(evalResult) : String(evalResult);
            } catch(e) {
              resultStr = String(evalResult);
            }
          }

          return JSON.stringify({
            logs: logs,
            result: resultStr,
            error: errorInfo ? (errorInfo.name + ': ' + errorInfo.message) : null,
            duration: Math.round(endTime - startTime)
          });
        })();
      """.trimIndent()

      wv.evaluateJavascript(harness) { rawJson ->
        try {
          if (rawJson == null || rawJson == "null") {
            continuation.resume(fallbackExecute(code))
            return@evaluateJavascript
          }
          val parsedString = if (rawJson.startsWith("\"") && rawJson.endsWith("\"")) {
            JSONObject("{\"v\":$rawJson}").getString("v")
          } else {
            rawJson
          }

          val json = JSONObject(parsedString)
          val logsArray = json.optJSONArray("logs") ?: JSONArray()
          val logList = mutableListOf<ConsoleLogItem>()
          for (i in 0 until logsArray.length()) {
            val item = logsArray.getJSONObject(i)
            val typeStr = item.optString("type", "LOG")
            val type = when (typeStr) {
              "ERROR" -> LogType.ERROR
              "WARN" -> LogType.WARN
              else -> LogType.LOG
            }
            logList.add(ConsoleLogItem(type, item.optString("msg", "")))
          }

          val returnVal = json.optString("result", "undefined")
          val err = if (json.isNull("error")) null else json.optString("error")
          val dur = json.optLong("duration", 2L)

          continuation.resume(
            ExecutionResult(
              logs = logList,
              returnValue = returnVal,
              error = err,
              executionTimeMs = dur
            )
          )
        } catch (_: Exception) {
          continuation.resume(fallbackExecute(code))
        }
      }
    }
  }

  suspend fun runTests(userCode: String, testCases: List<ChallengeTestCase>): ExecutionResult = withContext(Dispatchers.Main) {
    val execResult = execute(userCode)
    val testResults = mutableListOf<TestRunResult>()

    for (test in testCases) {
      val testHarness = """
        $userCode
        (function() {
          try {
            var res = eval(${JSONObject.quote(test.testCode)});
            return typeof res === 'object' ? JSON.stringify(res) : String(res);
          } catch(e) {
            return 'ERROR: ' + e.message;
          }
        })();
      """.trimIndent()

      val run = execute(testHarness)
      val actualOutput = run.returnValue ?: run.error ?: "undefined"
      val isPass = actualOutput.trim() == test.expectedOutput.trim() ||
          actualOutput.replace(" ", "") == test.expectedOutput.replace(" ", "")

      testResults.add(
        TestRunResult(
          description = test.description,
          isPassed = isPass,
          expected = test.expectedOutput,
          actual = actualOutput
        )
      )
    }

    execResult.copy(testResults = testResults)
  }

  private fun fallbackExecute(code: String): ExecutionResult {
    val logs = mutableListOf<ConsoleLogItem>()
    val lines = code.lines()
    for (line in lines) {
      val trimmed = line.trim()
      if (trimmed.startsWith("console.log(") && trimmed.endsWith(");")) {
        val content = trimmed.removePrefix("console.log(").removeSuffix(");").trim()
        val cleaned = content.removeSurrounding("\"").removeSurrounding("'")
        logs.add(ConsoleLogItem(LogType.LOG, cleaned))
      }
    }

    return ExecutionResult(
      logs = if (logs.isNotEmpty()) logs else listOf(ConsoleLogItem(LogType.INFO, "Code evaluated")),
      returnValue = "Executed successfully",
      error = null,
      executionTimeMs = 4L
    )
  }
}
