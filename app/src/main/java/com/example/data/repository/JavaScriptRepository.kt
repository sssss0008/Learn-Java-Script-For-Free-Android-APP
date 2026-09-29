package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JavaScriptRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("learn_javascript_prefs", Context.MODE_PRIVATE)

  private val _userProfile = MutableStateFlow(loadProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _notes = MutableStateFlow(loadNotes())
  val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

  private val _snippets = MutableStateFlow(loadSnippets())
  val snippets: StateFlow<List<SnippetItem>> = _snippets.asStateFlow()

  private fun loadProfile(): UserProfile {
    val name = prefs.getString("user_name", "Awiskar Student") ?: "Awiskar Student"
    val experience = prefs.getString("experience", "Beginner") ?: "Beginner"
    val goals = prefs.getStringSet("goals", setOf("Learn JavaScript from zero", "Build projects"))?.toList()
      ?: listOf("Learn JavaScript from zero")
    val completedLessons = prefs.getStringSet("completed_lessons", setOf("m1_l1")) ?: setOf("m1_l1")
    val completedChallenges = prefs.getStringSet("completed_challenges", emptySet()) ?: emptySet()
    val completedProjects = prefs.getStringSet("completed_projects", emptySet()) ?: emptySet()
    val streak = prefs.getInt("streak_days", 4)
    val currentLesson = prefs.getString("current_lesson_id", "m1_l2") ?: "m1_l2"
    val dailyGoal = prefs.getInt("daily_goal", 20)
    val bookmarks = prefs.getStringSet("bookmarks", setOf("ref_map", "m6_l1")) ?: setOf("ref_map")
    val isOnboarded = prefs.getBoolean("is_onboarded", false)

    return UserProfile(
      name = name,
      experience = experience,
      learningGoals = goals,
      completedLessonIds = completedLessons,
      completedChallengeIds = completedChallenges,
      completedProjectIds = completedProjects,
      streakDays = streak,
      currentLessonId = currentLesson,
      dailyGoalMinutes = dailyGoal,
      bookmarkedIds = bookmarks,
      isOnboardingCompleted = isOnboarded
    )
  }

  fun completeOnboarding(name: String, experience: String, goals: List<String>) {
    prefs.edit()
      .putString("user_name", name.ifBlank { "JavaScript Learner" })
      .putString("experience", experience)
      .putStringSet("goals", goals.toSet())
      .putBoolean("is_onboarded", true)
      .apply()
    _userProfile.value = _userProfile.value.copy(
      name = name.ifBlank { "JavaScript Learner" },
      experience = experience,
      learningGoals = goals,
      isOnboardingCompleted = true
    )
  }

  fun markLessonCompleted(lessonId: String) {
    val updated = _userProfile.value.completedLessonIds + lessonId
    prefs.edit().putStringSet("completed_lessons", updated).apply()
    _userProfile.value = _userProfile.value.copy(completedLessonIds = updated)
  }

  fun markChallengeCompleted(challengeId: String) {
    val updated = _userProfile.value.completedChallengeIds + challengeId
    prefs.edit().putStringSet("completed_challenges", updated).apply()
    _userProfile.value = _userProfile.value.copy(completedChallengeIds = updated)
  }

  fun markProjectCompleted(projectId: String) {
    val updated = _userProfile.value.completedProjectIds + projectId
    prefs.edit().putStringSet("completed_projects", updated).apply()
    _userProfile.value = _userProfile.value.copy(completedProjectIds = updated)
  }

  fun toggleBookmark(id: String) {
    val current = _userProfile.value.bookmarkedIds
    val updated = if (current.contains(id)) current - id else current + id
    prefs.edit().putStringSet("bookmarks", updated).apply()
    _userProfile.value = _userProfile.value.copy(bookmarkedIds = updated)
  }

  fun updateDailyGoal(minutes: Int) {
    prefs.edit().putInt("daily_goal", minutes).apply()
    _userProfile.value = _userProfile.value.copy(dailyGoalMinutes = minutes)
  }

  fun resetAllProgress() {
    prefs.edit().clear().apply()
    _userProfile.value = UserProfile(isOnboardingCompleted = false)
    _notes.value = emptyList()
  }

  fun addNote(lessonId: String, lessonTitle: String, content: String) {
    val newNote = NoteItem(
      id = "note_${System.currentTimeMillis()}",
      lessonId = lessonId,
      lessonTitle = lessonTitle,
      content = content
    )
    val list = listOf(newNote) + _notes.value
    _notes.value = list
    saveNotes(list)
  }

  fun deleteNote(noteId: String) {
    val list = _notes.value.filterNot { it.id == noteId }
    _notes.value = list
    saveNotes(list)
  }

  private fun saveNotes(notes: List<NoteItem>) {
    val set = notes.map { "${it.id}:::${it.lessonId}:::${it.lessonTitle}:::${it.content}" }.toSet()
    prefs.edit().putStringSet("user_notes", set).apply()
  }

  private fun loadNotes(): List<NoteItem> {
    val set = prefs.getStringSet("user_notes", emptySet()) ?: emptySet()
    return set.mapNotNull { item ->
      val parts = item.split(":::")
      if (parts.size >= 4) {
        NoteItem(parts[0], parts[1], parts[2], parts[3])
      } else null
    }.sortedByDescending { it.timestamp }
  }

  private fun loadSnippets(): List<SnippetItem> {
    return listOf(
      SnippetItem("snip_debounce", "Universal Debounce Function", "Performance", """
function debounce(fn, delayMs = 300) {
  let timer;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delayMs);
  };
}
      """.trimIndent(), "Delays execution until user pauses typing or clicking for delayMs."),
      SnippetItem("snip_curry", "Function Currying Helper", "Functional", """
function curry(fn) {
  return function curried(...args) {
    if (args.length >= fn.length) {
      return fn.apply(this, args);
    }
    return (...nextArgs) => curried.apply(this, args.concat(nextArgs));
  };
}
      """.trimIndent(), "Converts a multi-argument function into a series of single-argument calls."),
      SnippetItem("snip_fetch", "Async Fetch with Error Boundary", "Async", """
async function safeFetch(url) {
  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error('HTTP Error: ' + res.status);
    return await res.json();
  } catch (err) {
    console.error('Fetch Failed:', err.message);
    return null;
  }
}
      """.trimIndent(), "Clean async/await HTTP pattern with defensive status checking.")
    )
  }

  // --- Curriculum: 29 Comprehensive Levels ---
  val modules: List<Module> = listOf(
    Module("m1", 1, "JavaScript Introduction", "Engines, browser runtime, statements & the console", "JS", listOf(
      Lesson(
        "m1_l1", "m1", "What is JavaScript & Where It Runs",
        "Understand the V8 engine, browser environment, and JavaScript execution.",
        6,
        "JavaScript is a high-level, single-threaded, interpreted or just-in-time (JIT) compiled programming language that powers dynamic behavior across browsers and servers (Node.js).",
        "HTML defines webpage structure and CSS provides styling. JavaScript brings websites to life with calculations, user interactions, APIs, and real-time state.",
        "The JavaScript engine (such as Google Chrome's V8) parses source code into an Abstract Syntax Tree (AST), compiles it into bytecode, and optimizes hot code paths during execution.",
        "console.log('Hello from JavaScript!');\nlet developer = 'Awiskar';\nconsole.log('Welcome to masterclass by ' + developer + '!');",
        "Hello from JavaScript!\nWelcome to masterclass by Awiskar!",
        VisualizerType.VARIABLES,
        "Confusing JavaScript with Java. They are entirely different languages with distinct syntax and paradigms.",
        "Interview Question: Is JavaScript compiled or interpreted? (Answer: Modern JS engines use Just-In-Time (JIT) compilation!).",
        PracticeQuestion("Where does JavaScript run in modern computing?", listOf("Only in Internet Explorer", "In browsers and Node.js runtimes", "Only inside CSS files", "Directly on CPU hardware without an engine"), 1, "JavaScript runs inside web browser engines (V8, SpiderMonkey, WebKit) and standalone runtimes like Node.js and Deno.")
      ),
      Lesson(
        "m1_l2", "m1", "Statements, Expressions & Console",
        "Master console methods, comments, semicolons, and syntax fundamentals.",
        5,
        "An expression produces a value (e.g. 5 + 3, 'hello'). A statement performs an action (e.g. variable declaration, if branch, loop).",
        "Allows developers to write structured instructions and output telemetry, debugging logs, warnings, and performance measurements.",
        "The JS runtime evaluates statements line by line, executing expressions and maintaining memory state.",
        "// Expression:\nconst total = 100 * 1.15;\nconsole.log('Total with tax:', total);\nconsole.warn('Low battery warning');\nconsole.info('System initialized');",
        "Total with tax: 115",
        null,
        "Writing expressions where statements are expected, or forgetting that expressions return values.",
        "Interview Question: What is the difference between an expression and a statement?",
        PracticeQuestion("Which of the following is an expression in JavaScript?", listOf("if (x > 10) {}", "let x = 5;", "40 + 2", "for (let i=0; i<5; i++) {}"), 2, "40 + 2 resolves to the value 42, making it an expression.")
      )
    )),

    Module("m2", 2, "Variables & Data Types", "let, const, var, primitives vs objects", "VAR", listOf(
      Lesson(
        "m2_l1", "m2", "let, const, and var",
        "Explore modern variable declarations, block scope, and mutability.",
        8,
        "Variables store references to data values in memory. 'const' declares block-scoped immutable bindings, 'let' declares block-scoped reassignable variables, and 'var' is legacy function-scoped.",
        "Prevent accidental global variable pollution and bugs caused by variable hoisting and reassignment.",
        "Memory slots are allocated in the lexical environment. Attempting to access let/const before declaration throws a ReferenceError due to the Temporal Dead Zone (TDZ).",
        "const appName = 'Learn JavaScript';\nlet lessonNumber = 2;\nlessonNumber = lessonNumber + 1;\nconsole.log(appName + ' | Lesson: ' + lessonNumber);",
        "Learn JavaScript | Lesson: 3",
        VisualizerType.VARIABLES,
        "Using 'var' in modern projects, or trying to reassign a 'const' variable (throws TypeError).",
        "Interview Question: What is the Temporal Dead Zone (TDZ)?",
        PracticeQuestion("What happens if you reassign a const variable?", listOf("It silently creates a new variable", "It throws a TypeError at runtime", "It converts into a string", "It works normally"), 1, "const bindings cannot be reassigned; doing so throws a TypeError.")
      ),
      Lesson(
        "m2_l2", "m2", "JavaScript's 8 Data Types",
        "String, Number, BigInt, Boolean, Undefined, Null, Symbol, and Object.",
        8,
        "JavaScript has 7 primitive types (immutable and passed by value) and 1 non-primitive type: Object (passed by reference).",
        "Allows representing numbers, text, flags, unique identifiers, missing states, and structured compound data.",
        "Primitives are stored directly on the stack or in optimized memory. Objects reside on the heap and variables hold memory addresses.",
        "console.log(typeof 'JavaScript'); // string\nconsole.log(typeof 42);           // number\nconsole.log(typeof true);         // boolean\nconsole.log(typeof undefined);    // undefined\nconsole.log(typeof null);         // object (historical quirk!)\nconsole.log(typeof Symbol('id')); // symbol",
        "string\nnumber\nboolean\nundefined\nobject\nsymbol",
        VisualizerType.VARIABLES,
        "Expecting typeof null to return 'null'. It returns 'object' due to a famous legacy 31-year-old bug in JS engines.",
        "Interview Question: What are the primitive data types in JavaScript?",
        PracticeQuestion("Why does typeof null return 'object'?", listOf("null is an instance of Object class", "It is an intentional ECMAScript feature", "It is a historical bug in the original JS engine", "Because null has properties"), 2, "In the original JS implementation, values were represented with type tags; null had tag 000, matching object.")
      )
    )),

    Module("m3", 3, "Operators & Expressions", "Arithmetic, comparison, logical, nullish & ternary", "OP", listOf(
      Lesson(
        "m3_l1", "m3", "Strict Equality (===) vs Loose (==)",
        "Master type coercion and clean comparison operations.",
        6,
        "=== checks both value and type without coercion. == performs implicit type conversion before checking equality.",
        "Implicit coercion leads to notorious bugs (e.g. '' == 0 is true, '0' == 0 is true, but '' == '0' is false!).",
        "Strict equality directly compares type tags and binary representations without invoking internal ToPrimitive conversion algorithms.",
        "console.log(5 === '5'); // false (type mismatch)\nconsole.log(5 == '5');  // true (string coerced to number)\nconsole.log(null === undefined); // false\nconsole.log(null == undefined);  // true",
        "false\ntrue\nfalse\ntrue",
        null,
        "Using loose equality == and expecting type safety. Always prefer strict ===.",
        "Interview Question: How does == coerce operands compared to ===?",
        PracticeQuestion("What does [ ] == ! [ ] evaluate to in JavaScript?", listOf("false", "true", "TypeError", "undefined"), 1, "![] becomes false, and [] coerces to '' which coerces to 0 == 0, yielding true! This is why === is preferred.")
      ),
      Lesson(
        "m3_l2", "m3", "Nullish Coalescing (??) & Optional Chaining (?.)",
        "Modern ES2020 features for safe navigation and default values.",
        7,
        "?? returns right-hand operand only when left is null or undefined (unlike || which treats 0, false, '' as falsy). ?. safely navigates nested properties.",
        "Prevents 'TypeError: Cannot read properties of undefined' crashes in modern APIs.",
        "Short-circuits evaluation if the antecedent reference is null or undefined.",
        "const user = { profile: { name: 'Awiskar' }, score: 0 };\nconst score = user.score ?? 10; // 0, not 10!\nconst city = user.address?.city ?? 'Kathmandu';\nconsole.log('Score:', score);\nconsole.log('City:', city);",
        "Score: 0\nCity: Kathmandu",
        null,
        "Using || instead of ?? when 0 or false are valid, intended values.",
        "Interview Question: Difference between || (logical OR) and ?? (nullish coalescing)?",
        PracticeQuestion("What does (0 ?? 42) return?", listOf("42", "0", "null", "undefined"), 1, "0 is defined (not null/undefined), so ?? returns 0. (|| would have returned 42).")
      )
    )),

    Module("m4", 4, "Conditions & Control Flow", "if, else, switch, and ternary decision logic", "IF", listOf(
      Lesson(
        "m4_l1", "m4", "Decision Flow & Conditionals",
        "Build branching logic using if, else if, else, and switch statements.",
        6,
        "Control structures direct the code execution path based on truthy or falsy boolean tests.",
        "Allows apps to adapt to user input, authentication states, and dynamic data.",
        "The CPU branch predictor routes execution down the matching block while skipping other branches.",
        "function getGrade(score) {\n  if (score >= 90) return 'A+';\n  else if (score >= 80) return 'A';\n  else if (score >= 70) return 'B';\n  else return 'C';\n}\nconsole.log('Grade for 88:', getGrade(88));",
        "Grade for 88: A",
        null,
        "Forgetting break statements in switch cases, causing unintended fall-through.",
        "Interview Question: Which values are falsy in JavaScript?",
        PracticeQuestion("Which of these is NOT falsy in JavaScript?", listOf("0", "'' (empty string)", "[] (empty array)", "NaN"), 2, "Empty arrays and empty objects are objects, and all objects are truthy in JavaScript!")
      )
    )),

    Module("m5", 5, "Loops & Iteration", "for, while, for..of, and for..in", "LOOP", listOf(
      Lesson(
        "m5_l1", "m5", "Iterating with for and for..of",
        "Execute code repeatedly across sequences, arrays, and iterables.",
        7,
        "for loops count with indices; for..of directly reads iterable values (arrays, strings, sets); for..in iterates object keys.",
        "Automates repeated tasks, data processing, and rendering lists.",
        "Uses the Symbol.iterator protocol under the hood for clean traversal without manual index management.",
        "const frameworks = ['Vanilla JS', 'React', 'Vue', 'Node'];\nfor (const fw of frameworks) {\n  console.log('Mastering:', fw);\n}",
        "Mastering: Vanilla JS\nMastering: React\nMastering: Vue\nMastering: Node",
        null,
        "Using for..in on arrays (which iterates string indices and prototype keys instead of values).",
        "Interview Question: What is the difference between for..of and for..in?",
        PracticeQuestion("What does for..of iterate over?", listOf("Object enumerable keys", "Iterable values (arrays, strings, sets)", "Class names", "HTML tag names"), 1, "for..of iterates over values of any object that implements Symbol.iterator.")
      )
    )),

    Module("m6", 6, "Functions & Arrow Syntax", "Declarations, expressions, arrow functions, and return values", "FN", listOf(
      Lesson(
        "m6_l1", "m6", "Functions & Lexical 'this'",
        "Master parameter passing, return values, and arrow functions.",
        8,
        "Functions are first-class citizens in JavaScript: they can be assigned to variables, passed as arguments, and returned from other functions.",
        "Encapsulates reusable business logic and enables functional programming paradigms.",
        "When called, an Execution Context is pushed onto the Call Stack with its own Lexical Environment.",
        "// Arrow function with implicit return:\nconst multiply = (a, b) => a * b;\n// Default parameters:\nconst greet = (name = 'Developer') => 'Welcome, ' + name + '!';\nconsole.log(greet('Awiskar'));\nconsole.log('5 * 6 =', multiply(5, 6));",
        "Welcome, Awiskar!\n5 * 6 = 30",
        VisualizerType.CALL_STACK,
        "Arrow functions do not bind their own 'this', 'arguments', or 'super'. They retain lexical 'this'.",
        "Interview Question: Why can't arrow functions be used as constructors?",
        PracticeQuestion("What does an arrow function inherit for its 'this' value?", listOf("Global window object always", "The object calling the method", "Lexical 'this' from its enclosing scope", "null"), 2, "Arrow functions do not have their own this binding; they capture this lexically from outer scope.")
      )
    )),

    Module("m7", 7, "Arrays & Modern Methods", "map, filter, reduce, find, slice, splice", "ARR", listOf(
      Lesson(
        "m7_l1", "m7", "Array Transformation: map, filter, reduce",
        "The holy trinity of functional JavaScript data processing.",
        10,
        "map transforms every item; filter keeps items that pass a predicate; reduce folds an entire array into a single accumulated value.",
        "Enables clean, declarative, chainable data pipelines without mutable for loop state.",
        "Higher-order functions accept a callback and apply it sequentially to each index, returning new arrays without mutating the original.",
        "const numbers = [1, 2, 3, 4, 5, 6];\nconst evensDoubled = numbers\n  .filter(n => n % 2 === 0)\n  .map(n => n * 2);\nconsole.log('Evens doubled:', evensDoubled);\nconst sum = numbers.reduce((acc, curr) => acc + curr, 0);\nconsole.log('Total Sum:', sum);",
        "Evens doubled: [4, 8, 12]\nTotal Sum: 21",
        VisualizerType.ARRAY_METHODS,
        "Forgetting to provide an initial value to reduce(), which causes crashes on empty arrays.",
        "Interview Question: Does Array.prototype.map mutate the original array? (Answer: No, it returns a new array).",
        PracticeQuestion("What is returned by [1, 2, 3].map(x => x * 10)?", listOf("[1, 2, 3]", "[10, 20, 30]", "30", "undefined"), 1, "map returns a brand new array with each element multiplied by 10.")
      )
    )),

    Module("m8", 8, "Strings & Template Literals", "Methods, multi-line formatting, regex basics", "STR", listOf(
      Lesson(
        "m8_l1", "m8", "String Manipulation & Interpolation",
        "Slice, includes, replaceAll, split, join, and backtick template literals.",
        6,
        "Strings are immutable sequences of UTF-16 code units. Template literals allow multi-line strings and interpolation.",
        "Clean text manipulation, URL construction, and dynamic markup generation.",
        "String methods return new strings rather than altering the original string in memory.",
        "const framework = 'JavaScript is Amazing';\nconsole.log(framework.includes('Script')); // true\nconsole.log(framework.slice(0, 10));      // JavaScript\nconsole.log(framework.toUpperCase());",
        "true\nJavaScript\nJAVASCRIPT IS AMAZING",
        null,
        "Attempting to modify a string index directly like str[0] = 'X' (fails silently in non-strict mode).",
        "Interview Question: Why are primitive strings immutable in JavaScript?",
        PracticeQuestion("What does 'hello world'.split(' ') return?", listOf("'helloworld'", "['hello', 'world']", "['h', 'e', 'l', 'l', 'o']", "Error"), 1, "split(' ') divides the string by spaces into an array of substrings.")
      )
    )),

    Module("m9", 9, "Objects & Destructuring", "Key-value pairs, nested structures, spread & rest", "OBJ", listOf(
      Lesson(
        "m9_l1", "m9", "Object Destructuring & Spread Operator",
        "Extract properties effortlessly and clone/merge object payloads.",
        8,
        "Destructuring unpacks values from arrays or properties from objects into distinct variables. Spread (...) copies enumerable properties.",
        "Reduces repetitive code when dealing with API responses, configuration objects, and props.",
        "Performs shallow copies of properties. Nested objects still share references.",
        "const user = { id: 101, username: 'awiskar', role: 'admin', location: 'NP' };\nconst { username, role } = user;\nconsole.log('User: ' + username + ' (' + role + ')');\nconst updatedUser = { ...user, role: 'lead-architect', verified: true };\nconsole.log('Updated:', updatedUser.role);",
        "User: awiskar (admin)\nUpdated: lead-architect",
        VisualizerType.VARIABLES,
        "Thinking spread {...obj} performs a deep copy. It only shallowly copies the top level.",
        "Interview Question: How do you achieve a true deep clone in modern JavaScript? (Answer: structuredClone(obj)!).",
        PracticeQuestion("What operator is used to shallowly copy or merge objects?", listOf("&", "...", "->", "::"), 1, "The spread operator (...) expands object key-value pairs into a new object.")
      )
    )),

    Module("m10", 10, "Scope & The Call Stack", "Global, function, block scope & stack frames", "STACK", listOf(
      Lesson(
        "m10_l1", "m10", "Understanding the Call Stack",
        "How JavaScript tracks where it is in code execution using LIFO stack frames.",
        8,
        "The Call Stack is a LIFO (Last In, First Out) data structure that keeps track of function execution contexts.",
        "Enables JavaScript's single thread to execute nested function calls and return to the exact calling site.",
        "When a function is called, a stack frame is pushed. When it returns, the frame pops off the stack.",
        "function first() {\n  console.log('Inside first');\n  second();\n  console.log('Finishing first');\n}\nfunction second() {\n  console.log('Inside second');\n}\nfirst();",
        "Inside first\nInside second\nFinishing first",
        VisualizerType.CALL_STACK,
        "Unbounded recursion causing 'Maximum call stack size exceeded' (Stack Overflow).",
        "Interview Question: What is the Call Stack and what causes a stack overflow?",
        PracticeQuestion("Which data structure governs function execution in JavaScript engines?", listOf("FIFO Queue", "LIFO Stack", "Binary Tree", "Hash Map"), 1, "The Call Stack uses Last-In-First-Out (LIFO) order.")
      )
    )),

    Module("m11", 11, "DOM Manipulation", "Document Object Model, selecting, styling & creating nodes", "DOM", listOf(
      Lesson(
        "m11_l1", "m11", "Selecting and Mutating DOM Elements",
        "Bridge JavaScript logic with live browser webpage markup.",
        8,
        "The DOM is a tree representation of HTML documents created by the browser engine.",
        "Enables JavaScript to update content, change styles, add animations, and handle user interactions.",
        "The browser engine exposes the 'document' global object and nodes with properties like textContent, innerHTML, classList.",
        "// Conceptual DOM demonstration:\nconst simulatedDoc = {\n  title: 'Learn JavaScript',\n  nodes: ['#header', '#button', '#output'],\n  querySelector(sel) { return 'Found node: ' + sel; }\n};\nconsole.log(simulatedDoc.querySelector('#button'));",
        "Found node #button",
        VisualizerType.DOM_EVENTS,
        "Using innerHTML with untrusted user input, which opens up Cross-Site Scripting (XSS) vulnerabilities.",
        "Interview Question: What is the difference between textContent and innerHTML?",
        PracticeQuestion("Why is textContent preferred over innerHTML for setting plain text?", listOf("It is faster and immune to XSS injection", "It only works on buttons", "It allows inserting script tags", "It reloads the page"), 0, "textContent treats input strictly as plain text, preventing HTML injection attacks.")
      )
    )),

    Module("m12", 12, "Events & Propagation", "Listeners, Event Object, Bubbling & Capturing", "EVT", listOf(
      Lesson(
        "m12_l1", "m12", "Event Bubbling vs Capturing",
        "How user interactions travel through the DOM hierarchy.",
        9,
        "Events first capture down from Window to the Target, then bubble up from Target back to Window.",
        "Allows event delegation: placing one listener on a parent instead of hundreds on children.",
        "Calling event.stopPropagation() prevents the event from continuing its journey along the tree.",
        "console.log('Event Phase 1: Capturing (Root -> Target)');\nconsole.log('Event Phase 2: At Target');\nconsole.log('Event Phase 3: Bubbling (Target -> Root)');",
        "Event Phase 1: Capturing (Root -> Target)\nEvent Phase 2: At Target\nEvent Phase 3: Bubbling (Target -> Root)",
        VisualizerType.DOM_EVENTS,
        "Not knowing that by default, addEventListener listens during the bubbling phase unless { capture: true } is passed.",
        "Interview Question: What is event delegation and why is it beneficial?",
        PracticeQuestion("In which direction does event bubbling travel?", listOf("From root down to target", "From target element up to document root", "Horizontally across sibling nodes", "Randomly"), 1, "Bubbling propagates upward from the clicked target element toward the document root.")
      )
    )),

    Module("m13", 13, "Browser APIs & Storage", "localStorage, sessionStorage, and timers", "API", listOf(
      Lesson(
        "m13_l1", "m13", "Web Storage: Local vs Session",
        "Persist key-value data across page reloads and user sessions.",
        7,
        "localStorage persists indefinitely; sessionStorage clears when the browser tab closes. Both store string keys and values (up to ~5MB).",
        "Stores user preferences, dark mode toggles, offline tokens, and form drafts.",
        "Synchronous disk-backed key-value store tied to the webpage's origin (protocol + host + port).",
        "const settings = { theme: 'dark', fontSize: 16 };\nconst serialized = JSON.stringify(settings);\nconsole.log('Stored JSON:', serialized);\nconst parsed = JSON.parse(serialized);\nconsole.log('Retrieved theme:', parsed.theme);",
        "Stored JSON: {\"theme\":\"dark\",\"fontSize\":16}\nRetrieved theme: dark",
        null,
        "Storing sensitive JWTs or passwords in localStorage (accessible via XSS). Storing raw objects without JSON.stringify (saves as '[object Object]').",
        "Interview Question: What is the storage limit difference between cookies and localStorage?",
        PracticeQuestion("What happens if you store an object directly into localStorage without JSON.stringify?", listOf("It throws an error", "It is stored as '[object Object]'", "It automatically serializes", "It becomes null"), 1, "localStorage converts values to strings; objects convert to '[object Object]'.")
      )
    )),

    Module("m14", 14, "JSON & Serialization", "JSON.stringify, JSON.parse, and data interchange", "JSON", listOf(
      Lesson(
        "m14_l1", "m14", "Working with JSON",
        "The universal language of web APIs and client-server communication.",
        6,
        "JSON (JavaScript Object Notation) is a lightweight text-based data format based on JavaScript object syntax.",
        "Enables standardized communication between frontends, backends, and databases.",
        "Functions, symbols, and undefined are omitted by JSON.stringify.",
        "const payload = {\n  course: 'Learn JavaScript',\n  instructor: 'Awiskar Acharya',\n  modules: 29,\n  free: true\n};\nconst jsonStr = JSON.stringify(payload, null, 2);\nconsole.log('Serialized:', jsonStr);",
        "Serialized: {\n  \"course\": \"Learn JavaScript\",\n  \"instructor\": \"Awiskar Acharya\",\n  \"modules\": 29,\n  \"free\": true\n}",
        null,
        "JSON property names must be enclosed in double quotes (\"\"), not single quotes or unquoted.",
        "Interview Question: What happens to functions when an object is stringified with JSON.stringify?",
        PracticeQuestion("Which of these types is NOT supported in standard JSON?", listOf("Array", "String", "Function", "Boolean"), 2, "Functions cannot be represented in valid JSON.")
      )
    )),

    Module("m15", 15, "Error Handling & Debugging", "try, catch, finally, and custom Errors", "ERR", listOf(
      Lesson(
        "m15_l1", "m15", "Defensive Coding with try..catch",
        "Gracefully recover from runtime errors without crashing the app.",
        7,
        "try wraps risky code; catch handles any thrown exceptions; finally runs regardless of outcome.",
        "Prevents unhandled promise rejections and keeps the user interface responsive during network or parsing failures.",
        "Control transfers immediately to the nearest matching catch block up the call stack.",
        "function parseUser(json) {\n  try {\n    return JSON.parse(json);\n  } catch (err) {\n    console.error('Handled Error:', err.message);\n    return { fallback: true };\n  } finally {\n    console.log('Cleanup completed');\n  }\n}\nconsole.log(parseUser('{ malformed: true '));",
        "Handled Error: Unexpected token m in JSON at position 2\nCleanup completed\n{ fallback: true }",
        null,
        "Empty catch blocks that swallow errors silently, making debugging impossible.",
        "Interview Question: What is the purpose of the 'finally' block?",
        PracticeQuestion("When does the finally block execute in a try..catch structure?", listOf("Only when an error occurs", "Only when no error occurs", "Always, whether an error occurred or not", "Only during debugging"), 2, "The finally block is guaranteed to run after try and catch have finished.")
      )
    )),

    Module("m16", 16, "Asynchronous JavaScript", "Callbacks, event-driven async & concurrency", "ASYNC", listOf(
      Lesson(
        "m16_l1", "m16", "Synchronous vs Asynchronous",
        "How single-threaded JavaScript handles non-blocking operations.",
        8,
        "Synchronous code runs sequentially, blocking further lines until finished. Asynchronous code schedules work and yields the thread.",
        "Keeps the browser 60fps UI responsive while waiting for network responses, timers, or disk I/O.",
        "The browser Web API environment handles background tasks and pushes callbacks to queues when ready.",
        "console.log('1: First');\nsetTimeout(() => {\n  console.log('2: Async Timer finished');\n}, 0);\nconsole.log('3: Synchronous End');",
        "1: First\n3: Synchronous End\n2: Async Timer finished",
        VisualizerType.EVENT_LOOP,
        "Assuming setTimeout(fn, 0) runs immediately. It must wait until the Call Stack is completely clear!",
        "Interview Question: Why does setTimeout with 0ms delay execute after synchronous code?",
        PracticeQuestion("What does setTimeout(fn, 0) actually do?", listOf("Runs fn on another CPU thread instantly", "Pushes fn to the Task Queue to run after stack clears", "Cancels any other running code", "Halts the browser"), 1, "It registers the callback with Web APIs, which places it on the Task Queue once the current stack finishes.")
      )
    )),

    Module("m17", 17, "Promises & Microtasks", "Pending, Fulfilled, Rejected states and chaining", "PROM", listOf(
      Lesson(
        "m17_l1", "m17", "Promise Lifecycle & Chaining",
        "Escape callback hell with composable asynchronous values.",
        9,
        "A Promise is an object representing eventual completion (or failure) of an asynchronous operation. States: Pending, Fulfilled, Rejected.",
        "Provides predictable error propagation and composable chaining (.then, .catch, .finally).",
        "Promise resolution callbacks are placed on the high-priority Microtask Queue, executing before standard setTimeout task callbacks.",
        "const fetchData = (shouldSucceed) => new Promise((resolve, reject) => {\n  if (shouldSucceed) resolve('Data loaded successfully!');\n  else reject(new Error('Network offline'));\n});\nfetchData(true)\n  .then(res => console.log('Resolved:', res))\n  .catch(err => console.error(err.message));",
        "Resolved: Data loaded successfully!",
        VisualizerType.PROMISES,
        "Forgetting to return a promise in .then() chain, breaking the sequence.",
        "Interview Question: What is the priority difference between Microtasks and Macrotasks?",
        PracticeQuestion("Which queue has higher priority in the Event Loop?", listOf("Callback/Task Queue (Macrotasks)", "Microtask Queue (Promises)", "They have identical priority", "Animation frames only"), 1, "Microtasks (Promise callbacks, queueMicrotask) run immediately after the current script, before Macrotasks.")
      )
    )),

    Module("m18", 18, "Async / Await Syntax", "Modern readable asynchronous code with try/catch", "AWAIT", listOf(
      Lesson(
        "m18_l1", "m18", "Mastering Async/Await",
        "Write asynchronous code that looks and behaves like synchronous code.",
        8,
        "async functions return Promises. 'await' pauses function execution until the promise settles, unwrapping its value.",
        "Eliminates nested .then() chains and allows standard try/catch error handling.",
        "Under the hood, async/await is syntactic sugar over Promises and generator functions.",
        "async function getDeveloperGreeting() {\n  const message = await Promise.resolve('Hello, Master JavaScript!');\n  return message.toUpperCase();\n}\ngetDeveloperGreeting().then(console.log);",
        "HELLO, MASTER JAVASCRIPT!",
        VisualizerType.PROMISES,
        "Awaiting independent operations sequentially in a loop instead of in parallel with Promise.all().",
        "Interview Question: What happens if an awaited promise rejects without try/catch?",
        PracticeQuestion("What does an async function always return?", listOf("A Promise", "undefined", "A callback", "A generator"), 0, "Any function marked with the async keyword automatically wraps its return value in a Promise.")
      )
    )),

    Module("m19", 19, "Fetch API & Networking", "HTTP requests, headers, status codes, and JSON", "NET", listOf(
      Lesson(
        "m19_l1", "m19", "Making REST API Requests",
        "Send GET/POST requests, parse response bodies, and handle network errors.",
        9,
        "The fetch() global method initiates asynchronous network requests to web servers.",
        "Enables frontends to load live database records, submit forms, and sync state in real time.",
        "fetch() returns a Promise resolving to a Response object. Calling .json() reads the stream and parses JSON.",
        "// Simulated safe API fetch:\nconst mockApi = async (endpoint) => {\n  console.log('[HTTP GET] -> ' + endpoint);\n  return { status: 200, data: { user: 'Awiskar', role: 'Engineer' } };\n};\nconst result = await mockApi('/api/user/1');\nconsole.log('User fetched:', result.data.user);",
        "[HTTP GET] -> /api/user/1\nUser fetched: Awiskar",
        null,
        "Thinking fetch() rejects on HTTP 404 or 500 errors. It only rejects on network failures! Always check response.ok.",
        "Interview Question: Does fetch reject on a 404 Not Found error?",
        PracticeQuestion("How do you verify if a fetch request succeeded?", listOf("If the promise doesn't reject, it succeeded", "Check response.ok (status 200-299)", "By counting response characters", "By checking document.title"), 1, "fetch() resolves normally on HTTP 404/500; you must check response.ok or response.status.")
      )
    )),

    Module("m20", 20, "ES Modules", "import, export, default vs named exports", "MOD", listOf(
      Lesson(
        "m20_l1", "m20", "Modular Code Organization",
        "Split code into maintainable, isolated, reusable files.",
        7,
        "ES Modules (ESM) use export to expose functions and import to consume them in other files.",
        "Avoids global namespace conflicts and enables bundlers (Vite, Webpack) to perform tree-shaking.",
        "Modules run in strict mode by default and maintain their own file-level scope.",
        "// mathUtils.js conceptual ESM:\nconst add = (a, b) => a + b;\nconst multiply = (a, b) => a * b;\nconsole.log('Exporting add & multiply');\nconsole.log('add(10, 20) =', add(10, 20));",
        "Exporting add & multiply\nadd(10, 20) = 30",
        null,
        "Mixing up default imports with named { curly brace } imports.",
        "Interview Question: What is tree-shaking in modern bundlers?",
        PracticeQuestion("Which syntax imports a named export?", listOf("import myModule from './mod'", "import { myFunc } from './mod'", "require('./mod')", "include './mod'"), 1, "Named exports are imported using curly braces: import { myFunc } from './mod'.")
      )
    )),

    Module("m21", 21, "Classes & OOP", "Classes, constructors, inheritance, and getters/setters", "OOP", listOf(
      Lesson(
        "m21_l1", "m21", "Class Syntax & Inheritance",
        "Object-oriented programming using clean ES6 class syntax.",
        8,
        "Classes are templates for creating objects, encapsulating data and methods. 'extends' creates child subclasses.",
        "Organizes domain models, game entities, and complex business data structures.",
        "Classes in JavaScript are syntactical sugar over the prototype-based inheritance model.",
        "class Course {\n  constructor(title, creator) {\n    this.title = title;\n    this.creator = creator;\n  }\n  getInfo() {\n    return this.title + ' by ' + this.creator;\n  }\n}\nconst jsCourse = new Course('Learn JavaScript', 'Awiskar Acharya');\nconsole.log(jsCourse.getInfo());",
        "Learn JavaScript by Awiskar Acharya",
        null,
        "Calling a class constructor without the 'new' keyword (throws TypeError).",
        "Interview Question: Are JavaScript classes true OOP classes or prototype-based?",
        PracticeQuestion("What keyword is used to call a parent class constructor?", listOf("parent()", "super()", "base()", "this.parent()"), 1, "super() invokes the parent class constructor and must be called before accessing 'this'.")
      )
    )),

    Module("m22", 22, "Prototypes & Inheritance", "The prototype chain, __proto__, and Object.create", "PROTO", listOf(
      Lesson(
        "m22_l1", "m22", "The Prototype Chain",
        "How objects share methods and properties through delegation.",
        10,
        "Every JavaScript object has an internal link to another object called its prototype. Properties are searched up the chain until reaching null.",
        "Enables memory-efficient method sharing across thousands of instances.",
        "When property lookup fails on an object, the engine checks obj.__proto__, then obj.__proto__.__proto__, ending at Object.prototype.",
        "const animal = { eats: true };\nconst rabbit = Object.create(animal);\nrabbit.jumps = true;\nconsole.log('Rabbit jumps:', rabbit.jumps);\nconsole.log('Rabbit eats (inherited):', rabbit.eats);\nconsole.log(Object.getPrototypeOf(rabbit) === animal);",
        "Rabbit jumps: true\nRabbit eats (inherited): true\ntrue",
        VisualizerType.PROTOTYPE,
        "Polluting Object.prototype directly, which introduces bugs into all objects across the application.",
        "Interview Question: What is the end of the prototype chain?",
        PracticeQuestion("What does Object.prototype.__proto__ equal?", listOf("Object", "undefined", "null", "NaN"), 2, "null marks the ultimate end of every prototype chain in JavaScript.")
      )
    )),

    Module("m23", 23, "Closures & Lexical Scope", "Enclosed variables, data privacy, and factories", "CLOS", listOf(
      Lesson(
        "m23_l1", "m23", "Understanding Closures",
        "The superpower of functions remembering their birth scope.",
        10,
        "A closure is the combination of a function bundled together with references to its surrounding lexical environment.",
        "Enables private variables, module patterns, function factories, and memoization.",
        "Even after an outer function completes and leaves the call stack, inner functions retain access to variables in the heap.",
        "function createCounter(initialValue = 0) {\n  let count = initialValue;\n  return {\n    increment: () => ++count,\n    decrement: () => --count,\n    getCount: () => count\n  };\n}\nconst counter = createCounter(10);\ncounter.increment();\ncounter.increment();\nconsole.log('Counter value:', counter.getCount());",
        "Counter value: 12",
        VisualizerType.CLOSURE,
        "Creating closures inside high-frequency loops without caution, causing unintended memory retention.",
        "Interview Question: How do closures enable data privacy in JavaScript?",
        PracticeQuestion("Why can the inner function in a closure access outer variables after the outer function returned?", listOf("Because variables are copied into global memory", "Because the inner function retains a lexical reference to the environment", "It is done by the garbage collector", "It requires async/await"), 1, "The engine keeps the lexical environment alive on the heap as long as the inner function holds a reference.")
      )
    )),

    Module("m24", 24, "Advanced JavaScript Patterns", "Currying, memoization, hoisting, and event loop", "ADV", listOf(
      Lesson(
        "m24_l1", "m24", "Currying & Function Composition",
        "Transform multi-argument functions into unary pipelines.",
        9,
        "Currying transforms a function f(a, b, c) into callable f(a)(b)(c).",
        "Enables partial application, configuring reusable functions, and functional pipelines.",
        "Each nested function captures previously passed arguments via closure until arity is satisfied.",
        "const multiply = a => b => a * b;\nconst double = multiply(2);\nconst triple = multiply(3);\nconsole.log('Double 5:', double(5));\nconsole.log('Triple 5:', triple(5));",
        "Double 5: 10\nTriple 5: 15",
        null,
        "Overcomplicating simple utility functions with excessive levels of currying.",
        "Interview Question: What is function currying and what problem does it solve?",
        PracticeQuestion("What does a curried function return when called with fewer than its required arguments?", listOf("A TypeError", "Another function expecting the remaining arguments", "undefined", "null"), 1, "Currying returns a new function waiting for the next parameters.")
      )
    )),

    Module("m25", 25, "Testing & Quality Assurance", "Unit tests, assertions, and test runners", "TEST", listOf(
      Lesson(
        "m25_l1", "m25", "Writing Rock-Solid Unit Tests",
        "Test cases, assertions, edge cases, and test-driven development.",
        8,
        "Unit tests verify that individual functions behave correctly given inputs, expected outputs, and edge cases.",
        "Prevents regressions, improves software design, and gives developers confidence when refactoring.",
        "Test runners execute assertions (expect(actual).toBe(expected)) and report pass/fail metrics.",
        "function sum(a, b) {\n  if (typeof a !== 'number' || typeof b !== 'number') throw new TypeError('Numbers only');\n  return a + b;\n}\nconsole.log('Test 1 (Positive):', sum(2, 3) === 5 ? 'PASS' : 'FAIL');\nconsole.log('Test 2 (Negative):', sum(-2, 2) === 0 ? 'PASS' : 'FAIL');",
        "Test 1 (Positive): PASS\nTest 2 (Negative): PASS",
        null,
        "Only testing happy paths while ignoring null, undefined, empty strings, and boundary values.",
        "Interview Question: What is the difference between unit testing and integration testing?",
        PracticeQuestion("What does a unit test primarily test?", listOf("The entire cloud architecture", "A single isolated function or component", "CSS color contrast", "Database hard drive speed"), 1, "Unit tests focus on isolated, individual units of code such as functions.")
      )
    )),

    Module("m26", 26, "Performance Optimization", "Debounce, throttle, DOM batching, and memory", "PERF", listOf(
      Lesson(
        "m26_l1", "m26", "Debounce vs Throttle",
        "Optimize rapid events such as search inputs, scrolls, and window resizing.",
        9,
        "Debounce delays execution until an inactivity period elapses. Throttle limits execution to at most once per fixed time interval.",
        "Prevents freezing the browser UI and firing hundreds of unnecessary API requests per second.",
        "Uses timers (setTimeout) and closures to cancel or space out invocations.",
        "console.log('Debounce: Waits for user pause (e.g. search autocomplete)');\nconsole.log('Throttle: Enforces maximum firing rate (e.g. scroll listener)');",
        "Debounce: Waits for user pause (e.g. search autocomplete)\nThrottle: Enforces maximum firing rate (e.g. scroll listener)",
        VisualizerType.DEBOUNCE_THROTTLE,
        "Using debounce on scroll position tracking when continuous periodic updates are needed (use throttle instead).",
        "Interview Question: When would you use throttle over debounce?",
        PracticeQuestion("Which technique waits until the user stops typing for 300ms before sending a search query?", listOf("Throttle", "Debounce", "Memoization", "Currying"), 1, "Debounce waits for a quiet window of silence before executing.")
      )
    )),

    Module("m27", 27, "Security Basics", "XSS prevention, sanitization, and safe DOM manipulation", "SEC", listOf(
      Lesson(
        "m27_l1", "m27", "Preventing Cross-Site Scripting (XSS)",
        "Write secure frontends that defend against malicious script injection.",
        8,
        "XSS occurs when attackers inject executable client-side scripts into web applications viewed by other users.",
        "Protects user session cookies, personal identity data, and prevents unauthorized API actions.",
        "Sanitize all untrusted inputs, use textContent instead of innerHTML, and implement Content Security Policy (CSP).",
        "// Safe text assignment:\nconst userInput = '<script>alert(\"hack\")</script>';\nconsole.log('Unsafe insertion would execute script!');\nconsole.log('Safe textContent displays as plain text:', userInput);",
        "Unsafe insertion would execute script!\nSafe textContent displays as plain text: <script>alert(\"hack\")</script>",
        null,
        "Trusting client-side validation alone or blindly rendering server HTML strings via innerHTML.",
        "Interview Question: What is Stored XSS vs Reflected XSS?",
        PracticeQuestion("Which DOM property prevents script execution by treating content strictly as plain text?", listOf("innerHTML", "textContent", "outerHTML", "eval"), 1, "textContent treats string data as literal text, preventing HTML and script tags from executing.")
      )
    )),

    Module("m28", 28, "Real-World Projects", "Build Counter, To-Do, Quiz, Weather, and Kanban Apps", "PROJ", listOf(
      Lesson(
        "m28_l1", "m28", "Project Architecture & State Management",
        "How professional developers architect modular JavaScript web applications.",
        10,
        "Structured software separates State (data), Actions (logic/events), and View (UI rendering).",
        "Prevents spaghetti code and makes applications easy to test, debug, and scale.",
        "State changes trigger pure render functions that update the DOM efficiently.",
        "console.log('Project Engine Initialized');\nconsole.log('Ready to build 6 real-world applications in the Projects Center!');",
        "Project Engine Initialized\nReady to build 6 real-world applications in the Projects Center!",
        null,
        "Tightly coupling DOM event listeners with data storage operations in single giant functions.",
        "Interview Question: What is the Model-View-Controller (MVC) pattern in frontend development?",
        PracticeQuestion("What should be separated from DOM rendering logic?", listOf("Application State and Business Logic", "JavaScript and HTML files only", "Variables and functions", "Fonts and colors"), 0, "Keeping state and logic decoupled from DOM rendering makes code modular and testable.")
      )
    )),

    Module("m29", 29, "Interview Mastery", "50+ High-frequency interview challenges and mock tests", "INT", listOf(
      Lesson(
        "m29_l1", "m29", "JavaScript Interview Strategy",
        "Mastering the technical phone screen, live coding, and system design.",
        10,
        "Interviewers evaluate problem-solving clarity, edge-case consideration, communication, and deep engine knowledge.",
        "Landing top engineering roles at leading tech companies worldwide.",
        "Think aloud, clarify constraints, write clean code, and analyze time/space Big-O complexity.",
        "console.log('Prepare for technical interviews in the Interview Center');\nconsole.log('Topics: Closures, Event Loop, Prototypes, Array Algorithms');",
        "Prepare for technical interviews in the Interview Center\nTopics: Closures, Event Loop, Prototypes, Array Algorithms",
        null,
        "Jumping straight into code without clarifying input types or edge cases with the interviewer.",
        "Interview Question: How do you communicate trade-offs when optimizing an algorithm?",
        PracticeQuestion("What should you always do before writing code in a technical interview?", listOf("Start typing immediately without talking", "Clarify requirements and edge cases with the interviewer", "Guess the optimal Big-O in silence", "Ask for the answer"), 1, "Clarifying constraints demonstrates professional communication and prevents costly misunderstandings.")
      )
    ))
  )

  // --- Coding Challenges ---
  val challenges: List<Challenge> = listOf(
    Challenge(
      id = "ch_avg",
      title = "Calculate Array Average",
      difficulty = "Beginner",
      category = "Arrays",
      description = "Write a function `calculateAverage(numbers)` that takes an array of numbers and returns their average. If the array is empty, return 0.",
      examples = listOf(
        "calculateAverage([10, 20, 30, 40]) -> 25",
        "calculateAverage([5]) -> 5",
        "calculateAverage([]) -> 0"
      ),
      constraints = listOf("0 <= numbers.length <= 1000", "Numbers can be positive, negative, or decimals"),
      hints = listOf(
        "Check if the array length is 0 first to avoid dividing by zero.",
        "Use the array reduce() method to find the total sum.",
        "Divide the sum by numbers.length and return the result."
      ),
      initialCode = """
function calculateAverage(numbers) {
  // Your code here
  
}
      """.trimIndent(),
      testCases = listOf(
        ChallengeTestCase("Basic array [10, 20, 30, 40]", "calculateAverage([10, 20, 30, 40])", "25"),
        ChallengeTestCase("Single element [5]", "calculateAverage([5])", "5"),
        ChallengeTestCase("Empty array []", "calculateAverage([])", "0"),
        ChallengeTestCase("Decimals [2.5, 7.5]", "calculateAverage([2.5, 7.5])", "5")
      ),
      solutionCode = """
function calculateAverage(numbers) {
  if (!numbers || numbers.length === 0) return 0;
  const sum = numbers.reduce((acc, curr) => acc + curr, 0);
  return sum / numbers.length;
}
      """.trimIndent()
    ),

    Challenge(
      id = "ch_palindrome",
      title = "Valid Palindrome",
      difficulty = "Beginner",
      category = "Strings",
      description = "Write a function `isPalindrome(str)` that checks whether a given string reads the same forwards and backwards. Ignore casing.",
      examples = listOf(
        "isPalindrome('Racecar') -> true",
        "isPalindrome('hello') -> false",
        "isPalindrome('Madam') -> true"
      ),
      constraints = listOf("Input is a non-null string"),
      hints = listOf(
        "Convert the string to lowercase first using toLowerCase().",
        "Reverse the string by splitting into an array, reversing, and joining.",
        "Compare the original cleaned string with the reversed string."
      ),
      initialCode = """
function isPalindrome(str) {
  // Your code here
  
}
      """.trimIndent(),
      testCases = listOf(
        ChallengeTestCase("Racecar case insensitive", "isPalindrome('Racecar')", "true"),
        ChallengeTestCase("hello not palindrome", "isPalindrome('hello')", "false"),
        ChallengeTestCase("Single character 'a'", "isPalindrome('a')", "true"),
        ChallengeTestCase("Kayak", "isPalindrome('Kayak')", "true")
      ),
      solutionCode = """
function isPalindrome(str) {
  const clean = str.toLowerCase();
  const reversed = clean.split('').reverse().join('');
  return clean === reversed;
}
      """.trimIndent()
    ),

    Challenge(
      id = "ch_flatten",
      title = "Deep Flatten Array",
      difficulty = "Intermediate",
      category = "Arrays",
      description = "Write a function `deepFlatten(arr)` that flattens a nested array of arbitrary depth into a single flat array without using Array.prototype.flat.",
      examples = listOf(
        "deepFlatten([1, [2, [3, [4]]]]) -> [1, 2, 3, 4]",
        "deepFlatten([[1, 2], [3, 4]]) -> [1, 2, 3, 4]"
      ),
      constraints = listOf("Arbitrary nesting depth"),
      hints = listOf(
        "Use recursion or a stack.",
        "Iterate over items with for..of or reduce().",
        "If an element is Array.isArray(item), recursively call deepFlatten(item)."
      ),
      initialCode = """
function deepFlatten(arr) {
  // Your code here
  
}
      """.trimIndent(),
      testCases = listOf(
        ChallengeTestCase("Deep nesting", "JSON.stringify(deepFlatten([1, [2, [3, [4]]]]))", "[1,2,3,4]"),
        ChallengeTestCase("Multiple siblings", "JSON.stringify(deepFlatten([[1, 2], [3, 4]]))", "[1,2,3,4]"),
        ChallengeTestCase("Already flat", "JSON.stringify(deepFlatten([1, 2, 3]))", "[1,2,3]")
      ),
      solutionCode = """
function deepFlatten(arr) {
  return arr.reduce((acc, curr) => {
    return acc.concat(Array.isArray(curr) ? deepFlatten(curr) : curr);
  }, []);
}
      """.trimIndent()
    ),

    Challenge(
      id = "ch_memoize",
      title = "Implement Memoize",
      difficulty = "Advanced",
      category = "Closures",
      description = "Write a higher-order function `memoize(fn)` that returns a memoized version of `fn`, caching results based on serialized arguments.",
      examples = listOf(
        "const memoSquare = memoize(x => x * x);\nmemoSquare(4); // 16 (computed)\nmemoSquare(4); // 16 (returned from cache)"
      ),
      constraints = listOf("Handle any number of serializable arguments"),
      hints = listOf(
        "Create a cache object or Map inside the outer closure.",
        "Serialize arguments using JSON.stringify(args) as the cache key.",
        "If key exists in cache, return it; otherwise invoke fn and store."
      ),
      initialCode = """
function memoize(fn) {
  // Your code here
  
}
      """.trimIndent(),
      testCases = listOf(
        ChallengeTestCase("Squares 5", "(function() { var calls = 0; var f = memoize(function(x) { calls++; return x*x; }); f(5); f(5); return calls === 1 ? 'true' : 'false'; })()", "true"),
        ChallengeTestCase("Returns correct value", "(function() { var f = memoize(function(a, b) { return a + b; }); return String(f(10, 20)); })()", "30")
      ),
      solutionCode = """
function memoize(fn) {
  const cache = new Map();
  return function(...args) {
    const key = JSON.stringify(args);
    if (cache.has(key)) return cache.get(key);
    const result = fn.apply(this, args);
    cache.set(key, result);
    return result;
  };
}
      """.trimIndent()
    )
  )

  // --- Real-World Projects ---
  val projects: List<ProjectItem> = listOf(
    ProjectItem(
      id = "proj_counter",
      title = "Interactive Counter with LocalStorage",
      category = "Beginner",
      difficulty = "Easy",
      description = "Build a responsive counter with increment, decrement, reset, step configuration, and persistence.",
      concepts = listOf("DOM Manipulation", "EventListeners", "Variables", "LocalStorage"),
      requirements = listOf(
        "Display current count centered in bold text",
        "Buttons for + (increment), - (decrement), and Reset",
        "Input field to change step size (e.g. +5)",
        "Persist count in localStorage so it survives reload"
      ),
      initialCode = """
let count = 0;
function increment(step = 1) {
  count += step;
  render();
}
function decrement(step = 1) {
  count -= step;
  render();
}
function reset() {
  count = 0;
  render();
}
function render() {
  console.log('Current Count:', count);
}
increment(5);
increment(5);
decrement(2);
      """.trimIndent(),
      solutionCode = """
// Production counter with localStorage:
class CounterApp {
  constructor() {
    this.count = parseInt(localStorage.getItem('js_count') || '0', 10);
  }
  update(delta) {
    this.count += delta;
    localStorage.setItem('js_count', String(this.count));
    return this.count;
  }
  reset() {
    this.count = 0;
    localStorage.setItem('js_count', '0');
    return this.count;
  }
}
      """.trimIndent(),
      checklist = listOf(
        "Initial count loads from storage",
        "Increment button updates state",
        "Decrement button updates state",
        "Reset button zeroes count",
        "Persists state across reloads"
      ),
      simulatedHtml = "<div class='card'><h2 id='countDisplay'>Count: 8</h2><button>+ Increment</button> <button>- Decrement</button> <button>Reset</button></div>"
    ),

    ProjectItem(
      id = "proj_todo",
      title = "Advanced To-Do Task Manager",
      category = "Beginner",
      difficulty = "Medium",
      description = "A feature-packed Task Manager with filtering (All/Active/Completed), task deletion, and persistence.",
      concepts = listOf("Arrays", "Objects", "Array Methods (filter, map)", "DOM", "LocalStorage"),
      requirements = listOf(
        "Add task with title and priority",
        "Toggle task completion with checkbox",
        "Filter by All, Active, Completed",
        "Delete task with confirmation",
        "Display active task count badge"
      ),
      initialCode = """
const todos = [];
function addTodo(title, priority = 'normal') {
  todos.push({ id: Date.now(), title, completed: false, priority });
  console.log('Tasks total:', todos.length);
}
function toggleTodo(id) {
  const task = todos.find(t => t.id === id);
  if (task) task.completed = !task.completed;
}
addTodo('Master Closures in JavaScript', 'high');
addTodo('Complete Event Loop Visualizer', 'medium');
console.log('Current tasks:', todos.map(t => t.title).join(', '));
      """.trimIndent(),
      solutionCode = """
class TodoManager {
  constructor() {
    this.tasks = [];
  }
  add(title, priority) {
    this.tasks.push({ id: Date.now(), title, completed: false, priority });
  }
  toggle(id) {
    const t = this.tasks.find(x => x.id === id);
    if (t) t.completed = !t.completed;
  }
  getActive() {
    return this.tasks.filter(t => !t.completed);
  }
}
      """.trimIndent(),
      checklist = listOf(
        "Add task functionality",
        "Toggle completion status",
        "Filter active vs completed",
        "Delete task item",
        "Local storage sync"
      ),
      simulatedHtml = "<div class='todo-box'><h3>My JavaScript Tasks (2 active)</h3><ul><li>☑ Master Closures</li><li>☐ Complete Event Loop</li></ul></div>"
    ),

    ProjectItem(
      id = "proj_weather",
      title = "Weather Dashboard API Client",
      category = "Intermediate",
      difficulty = "Medium",
      description = "Fetch live weather data from OpenWeather API with error handling, loading states, and temperature conversion.",
      concepts = listOf("Fetch API", "Async/Await", "Promises", "Error Handling", "DOM"),
      requirements = listOf(
        "Input field to search city",
        "Display loading skeleton while fetching",
        "Show temperature, humidity, wind speed, condition icon",
        "Toggle Celsius vs Fahrenheit",
        "Graceful error card for invalid city"
      ),
      initialCode = """
async function fetchCityWeather(city) {
  console.log('Fetching weather for:', city);
  // Simulating async network call
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        city,
        tempC: 24,
        condition: 'Sunny & Clear',
        humidity: 45
      });
    }, 500);
  });
}
fetchCityWeather('Kathmandu').then(data => {
  console.log('Weather in ' + data.city + ': ' + data.tempC + '°C (' + data.condition + ')');
});
      """.trimIndent(),
      solutionCode = """
async function getWeather(city) {
  try {
    const res = await fetch('https://api.weather.mock/v1?city=' + city);
    if (!res.ok) throw new Error('City not found');
    return await res.json();
  } catch (err) {
    console.error('Weather error:', err.message);
    throw err;
  }
}
      """.trimIndent(),
      checklist = listOf(
        "Search city input",
        "Async fetch request",
        "Loading indicators",
        "Temperature conversion (C/F)",
        "Defensive error handling"
      ),
      simulatedHtml = "<div class='weather-card'><h2>Kathmandu</h2><h1>24°C</h1><p>Sunny & Clear • Humidity 45%</p></div>"
    ),

    ProjectItem(
      id = "proj_kanban",
      title = "Drag & Drop Kanban Board",
      category = "Advanced",
      difficulty = "Hard",
      description = "Full Kanban board with To Do, In Progress, and Done columns with drag-and-drop state persistence.",
      concepts = listOf("Drag and Drop API", "Complex State", "Custom Events", "Modular OOP"),
      requirements = listOf(
        "3 dynamic columns: To Do, In Progress, Done",
        "Add cards to any column",
        "Drag cards seamlessly between columns",
        "Local storage state backup",
        "Search filter across all columns"
      ),
      initialCode = """
const columns = {
  todo: ['Review JavaScript Closures', 'Build Call Stack visualizer'],
  inProgress: ['Practice map, filter, reduce'],
  done: ['Complete JavaScript Intro']
};
function moveCard(card, fromCol, toCol) {
  columns[fromCol] = columns[fromCol].filter(c => c !== card);
  columns[toCol].push(card);
  console.log('Moved ' + card + ' -> ' + toCol);
}
moveCard('Practice map, filter, reduce', 'inProgress', 'done');
console.log('Done column:', columns.done.join(', '));
      """.trimIndent(),
      solutionCode = """
class KanbanBoard {
  constructor() {
    this.state = { todo: [], inProgress: [], done: [] };
  }
  move(item, from, to) {
    this.state[from] = this.state[from].filter(x => x !== item);
    this.state[to].push(item);
  }
}
      """.trimIndent(),
      checklist = listOf(
        "Render 3 columns",
        "Create custom task card",
        "Drag and drop reordering",
        "Move between columns",
        "Persistent state storage"
      ),
      simulatedHtml = "<div class='kanban-row'><div class='col'><h4>To Do (2)</h4></div><div class='col'><h4>In Progress (0)</h4></div><div class='col'><h4>Done (2)</h4></div></div>"
    )
  )

  // --- Interview Questions Library ---
  val interviewQuestions: List<InterviewItem> = listOf(
    InterviewItem(
      id = "int_closure",
      category = "Closures",
      difficulty = "Intermediate",
      question = "What is a Closure in JavaScript, and what are its practical use cases?",
      thinkPrompt = "Think about lexical scope, outer functions returning inner functions, and memory retention.",
      hint = "A closure retains access to its parent scope even after the parent function has finished executing.",
      answer = """
A closure is the bundle of a function combined with references to its lexical environment. In JavaScript, every inner function has access to the outer function's variables.

Practical use cases:
1. Data Privacy / Encapsulation: Creating private variables that cannot be accessed directly from the outside.
2. Function Factories: Functions that return customized functions (e.g. multiplier, prefixer).
3. Memoization / Caching: Storing expensive calculation results in an enclosed cache Map.
4. Currying and partial application.
      """.trimIndent(),
      codeExample = """
function createSecretVault(secret) {
  return {
    getSecret: (key) => key === '1234' ? secret : 'Access Denied',
    changeSecret: (key, newSecret) => {
      if (key === '1234') secret = newSecret;
    }
  };
}
const vault = createSecretVault('Awiskar2026');
console.log(vault.getSecret('1234')); // 'Awiskar2026'
console.log(vault.secret);           // undefined (private!)
      """.trimIndent(),
      followUp = "What memory issues can arise if closures are created improperly in long-running applications?"
    ),

    InterviewItem(
      id = "int_event_loop",
      category = "Event Loop",
      difficulty = "Advanced",
      question = "Explain how the JavaScript Event Loop works with Call Stack, Web APIs, Microtasks, and Macrotasks.",
      thinkPrompt = "Follow the execution order of synchronous code, setTimeout, and Promise.then().",
      hint = "Microtasks (Promises) always drain completely before the next Macrotask (setTimeout) runs.",
      answer = """
JavaScript is single-threaded. Concurrency is handled by the browser/engine runtime through the Event Loop:

1. Call Stack: Synchronous code executes here in LIFO order.
2. Web APIs: Background operations (timers, fetch, DOM events) run in the browser background.
3. Microtask Queue: Holds Promise callbacks (.then, .catch, .finally) and queueMicrotask.
4. Macrotask Queue (Task Queue): Holds setTimeout, setInterval, setImmediate, and I/O callbacks.

Execution Rule:
- Once the Call Stack is empty, the Event Loop checks the Microtask Queue and runs ALL available microtasks until empty.
- Then, the Event Loop takes the oldest Macrotask from the Task Queue and pushes it to the Call Stack.
- After that macrotask completes, it once again drains all microtasks before picking the next macrotask!
      """.trimIndent(),
      codeExample = """
console.log('1: Sync');
setTimeout(() => console.log('4: Macro Task (setTimeout)'), 0);
Promise.resolve().then(() => console.log('3: Micro Task (Promise)'));
console.log('2: Sync');
// Output Order: 1 -> 2 -> 3 -> 4
      """.trimIndent(),
      followUp = "What happens if a microtask continuously schedules another microtask inside itself?"
    ),

    InterviewItem(
      id = "int_hoisting",
      category = "Fundamentals",
      difficulty = "Beginner",
      question = "What is Hoisting in JavaScript, and how do var, let, const, and function declarations differ?",
      thinkPrompt = "What does the engine do during the creation phase before executing code?",
      hint = "Function declarations are hoisted with their definitions; var is hoisted as undefined; let/const are in the TDZ.",
      answer = """
Hoisting is JavaScript's default behavior of moving declarations to the top of their enclosing scope during the compilation/creation phase.

Differences:
- function declaration: Entirely hoisted (both identifier and body). Can be invoked before its written line.
- var: Identifier is hoisted and initialized with 'undefined'. Accessing it early returns undefined.
- let and const: Hoisted to the top of their block, but NOT initialized. They remain in the Temporal Dead Zone (TDZ) until their declaration line is executed; accessing them early throws a ReferenceError.
      """.trimIndent(),
      codeExample = """
console.log(hoistedVar); // undefined
var hoistedVar = 42;

try {
  console.log(notHoisted); // ReferenceError (TDZ)
  let notHoisted = 100;
} catch (e) {
  console.log('Caught TDZ Error:', e.message);
}
      """.trimIndent(),
      followUp = "Why was the Temporal Dead Zone introduced in ES6 instead of letting let/const behave like var?"
    ),

    InterviewItem(
      id = "int_prototype",
      category = "Prototypes",
      difficulty = "Intermediate",
      question = "Explain Prototypal Inheritance vs Classical Inheritance.",
      thinkPrompt = "Think about Object.create(), delegation, and memory footprint.",
      hint = "In JS, objects inherit directly from other objects without requiring abstract blueprint classes.",
      answer = """
In Classical Inheritance (Java, C++), classes are blueprints that instantiate separate object copies with their own duplicate method instances.

In Prototypal Inheritance (JavaScript):
- Objects inherit directly from other objects.
- Methods are delegated up the prototype chain (__proto__) rather than copied.
- Memory efficient: Thousands of instances share the exact same function reference defined on their prototype.
- Classes in ES6 are purely syntactical sugar over prototypes.
      """.trimIndent(),
      codeExample = """
function Person(name) {
  this.name = name;
}
Person.prototype.sayHi = function() {
  return 'Hi, I am ' + this.name;
};
const p1 = new Person('Awiskar');
const p2 = new Person('Student');
console.log(p1.sayHi === p2.sayHi); // true (shared reference!)
      """.trimIndent(),
      followUp = "How does property shadowing work when an object defines a property with the same name as one on its prototype?"
    )
  )

  // --- Reference Library ---
  val referenceItems: List<ReferenceItem> = listOf(
    ReferenceItem(
      "ref_map", "Array.prototype.map()", "Arrays",
      "arr.map(callback(currentValue, index, array))",
      "Creates a new array populated with the results of calling a provided function on every element in the calling array.",
      "const numbers = [1, 4, 9];\nconst roots = numbers.map(Math.sqrt);\nconsole.log(roots); // [1, 2, 3]",
      "A new Array with each element being the result of the callback function.",
      "Does not mutate original array. If you do not use the returned array, use forEach or for..of instead."
    ),
    ReferenceItem(
      "ref_filter", "Array.prototype.filter()", "Arrays",
      "arr.filter(callback(element, index, array))",
      "Creates a shallow copy of a portion of a given array, filtered down to just the elements from the given array that pass the test.",
      "const words = ['spray', 'limit', 'elite', 'exuberant'];\nconst result = words.filter(word => word.length > 6);\nconsole.log(result); // ['exuberant']",
      "A shallow copy of a portion of the given array containing elements that pass the test.",
      "Callback must return a truthy value to keep the element, or a falsy value to exclude it."
    ),
    ReferenceItem(
      "ref_reduce", "Array.prototype.reduce()", "Arrays",
      "arr.reduce(callback(accumulator, currentValue, index, array), initialValue)",
      "Executes a user-supplied 'reducer' callback function on each element of the array, resulting in a single output value.",
      "const array1 = [1, 2, 3, 4];\nconst initialValue = 0;\nconst sumWithInitial = array1.reduce(\n  (acc, curr) => acc + curr,\n  initialValue\n);\nconsole.log(sumWithInitial); // 10",
      "The value that results from the completed reduction across the entire array.",
      "Always provide initialValue! Calling reduce() on an empty array without an initialValue throws a TypeError."
    ),
    ReferenceItem(
      "ref_promise_all", "Promise.all()", "Async",
      "Promise.all(iterable)",
      "Takes an iterable of promises and returns a single Promise that resolves to an array of the results of the input promises.",
      "const p1 = Promise.resolve(3);\nconst p2 = 42;\nconst p3 = new Promise(res => setTimeout(res, 100, 'foo'));\nPromise.all([p1, p2, p3]).then(console.log); // [3, 42, 'foo']",
      "A pending Promise that resolves when all input promises have resolved, or rejects immediately when ANY input promise rejects.",
      "Fails fast: if one promise rejects, the entire Promise.all rejects immediately."
    ),
    ReferenceItem(
      "ref_object_entries", "Object.entries()", "Objects",
      "Object.entries(obj)",
      "Returns an array of a given object's own enumerable string-keyed property [key, value] pairs.",
      "const user = { a: 'somestring', b: 42 };\nfor (const [key, value] of Object.entries(user)) {\n  console.log(key + ': ' + value);\n}",
      "An array of the given object's own enumerable string-keyed property [key, value] pairs.",
      "Order of keys matches order provided by iterating over property values manually."
    )
  )

  // --- Glossary ---
  val glossary: Map<String, String> = mapOf(
    "Closure" to "A function bundled with its lexical environment, allowing it to access outer variables even after the outer function has finished executing.",
    "Hoisting" to "The JavaScript engine behavior where variable and function declarations are moved to memory before code execution begins.",
    "Temporal Dead Zone (TDZ)" to "The period between entering block scope and the variable declaration line where accessing let or const throws a ReferenceError.",
    "Call Stack" to "A LIFO (Last-In-First-Out) stack data structure used by the JavaScript engine to keep track of execution contexts and function calls.",
    "Event Loop" to "The engine mechanism that constantly monitors the Call Stack and queues, moving microtasks and macrotasks onto the stack when it becomes idle.",
    "Microtask" to "A high-priority asynchronous task (Promises, queueMicrotask) that runs immediately after the current synchronous script finishes.",
    "Macrotask" to "A standard task queue item (setTimeout, setInterval, I/O) that runs after all pending microtasks have been drained.",
    "Prototype Chain" to "The sequence of linked prototype objects inspected when searching for an object property until reaching null.",
    "First-Class Functions" to "Functions in JavaScript are treated as values: they can be assigned to variables, passed into arguments, and returned from other functions.",
    "Debouncing" to "A rate-limiting technique that delays function execution until a specified quiet period of inactivity has elapsed.",
    "Throttling" to "A rate-limiting technique that enforces a maximum frequency at which a function can be invoked over time."
  )

  fun getModule(moduleId: String): Module? = modules.find { it.id == moduleId }

  fun getLesson(lessonId: String): Lesson? =
    modules.flatMap { it.lessons }.find { it.id == lessonId }

  fun getChallenge(challengeId: String): Challenge? =
    challenges.find { it.id == challengeId }

  fun getProject(projectId: String): ProjectItem? =
    projects.find { it.id == projectId }

  fun getAchievements(): List<AchievementItem> {
    val completedLessons = _userProfile.value.completedLessonIds.size
    val completedChallenges = _userProfile.value.completedChallengeIds.size
    val completedProjects = _userProfile.value.completedProjectIds.size

    return listOf(
      AchievementItem(
        "ach_first_code", "First Code",
        "Run your first JavaScript program", "🚀",
        isUnlocked = true, progressPercent = 100
      ),
      AchievementItem(
        "ach_fn_builder", "Function Builder",
        "Complete Functions & Arrow Syntax module", "⚡",
        isUnlocked = _userProfile.value.completedLessonIds.contains("m6_l1"),
        progressPercent = if (_userProfile.value.completedLessonIds.contains("m6_l1")) 100 else 50
      ),
      AchievementItem(
        "ach_dom_explorer", "DOM Explorer",
        "Complete the DOM Manipulation module", "🌐",
        isUnlocked = _userProfile.value.completedLessonIds.contains("m11_l1"),
        progressPercent = if (_userProfile.value.completedLessonIds.contains("m11_l1")) 100 else 40
      ),
      AchievementItem(
        "ach_async_master", "Async Master",
        "Master Promises and Async/Await", "⏳",
        isUnlocked = _userProfile.value.completedLessonIds.contains("m17_l1"),
        progressPercent = if (_userProfile.value.completedLessonIds.contains("m17_l1")) 100 else 30
      ),
      AchievementItem(
        "ach_debugger", "Bug Hunter",
        "Solve 5 coding challenges with passing test suites", "🐞",
        isUnlocked = completedChallenges >= 2,
        progressPercent = (completedChallenges * 50).coerceAtMost(100)
      ),
      AchievementItem(
        "ach_project_builder", "Project Architect",
        "Build a full interactive JavaScript project", "🛠️",
        isUnlocked = completedProjects >= 1,
        progressPercent = (completedProjects * 100).coerceAtMost(100)
      ),
      AchievementItem(
        "ach_js_master", "JavaScript Master",
        "Master the complete 29-level curriculum by Awiskar Acharya", "🏆",
        isUnlocked = completedLessons >= 10,
        progressPercent = ((completedLessons.toFloat() / 29f) * 100).toInt()
      )
    )
  }
}
