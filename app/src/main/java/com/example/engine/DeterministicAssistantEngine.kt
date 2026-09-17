package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*
import kotlin.random.Random

object DeterministicAssistantEngine {

    private val JOKES = listOf(
        Pair("Why don't scientists trust atoms?", "Because they make up everything!"),
        Pair("What do you call fake spaghetti?", "An impasta!"),
        Pair("Why did the scarecrow win an award?", "Because he was outstanding in his field!"),
        Pair("How does a penguin build its house?", "Igloos it together!"),
        Pair("Why do we tell actors to 'break a leg'?", "Because every play has a cast!"),
        Pair("What do you call cheese that isn't yours?", "Nacho cheese!"),
        Pair("Why did the bicycle fall over?", "Because it was two-tired!"),
        Pair("What did one wall say to the other wall?", "I'll meet you at the corner!"),
        Pair("Why can't your hand be 12 inches long?", "Because then it would be a foot!"),
        Pair("How do you organize a space party?", "You planet!")
    )

    private val RIDDLES = listOf(
        Pair("What has keys, but no locks; space, but no room; and you can enter, but never leave?", "A keyboard!"),
        Pair("What gets wetter the more it dries?", "A towel!"),
        Pair("What has to be broken before you can use it?", "An egg!"),
        Pair("I have branches, but no fruit, trunk or leaves. What am I?", "A bank!"),
        Pair("What can you catch, but not throw?", "A cold!")
    )

    private val QUOTES = listOf(
        Pair("The secret of getting ahead is getting started.", "Mark Twain"),
        Pair("It always seems impossible until it's done.", "Nelson Mandela"),
        Pair("Simplicity is the soul of efficiency.", "Austin Freeman"),
        Pair("Action is the foundational key to all success.", "Pablo Picasso"),
        Pair("Do what you can, with what you have, where you are.", "Theodore Roosevelt")
    )

    private val MAGIC_8_BALL = listOf(
        "It is certain.",
        "Without a doubt.",
        "You may rely on it.",
        "Most likely.",
        "Outlook good.",
        "Signs point to yes.",
        "Reply hazy, try again.",
        "Ask again later.",
        "Better not tell you now.",
        "Cannot predict now.",
        "Don't count on it.",
        "My reply is no.",
        "My sources say no.",
        "Outlook not so good.",
        "Very doubtful."
    )

    private val ANIMAL_SOUNDS = mapOf(
        "dog" to Triple("Dog", "🐶", "Woof woof!"),
        "cat" to Triple("Cat", "🐱", "Meow meow!"),
        "cow" to Triple("Cow", "🐮", "Moooo!"),
        "duck" to Triple("Duck", "🦆", "Quack quack!"),
        "lion" to Triple("Lion", "🦁", "Roaaaar!"),
        "sheep" to Triple("Sheep", "🐑", "Baaa baaa!"),
        "rooster" to Triple("Rooster", "🐓", "Cock-a-doodle-doo!"),
        "frog" to Triple("Frog", "🐸", "Ribbit ribbit!"),
        "elephant" to Triple("Elephant", "🐘", "Pawoo toot!"),
        "owl" to Triple("Owl", "🦉", "Hoo hoo!")
    )

    fun processQuery(
        rawQuery: String,
        currentNotes: List<NoteItem> = emptyList(),
        isFlashlightOn: Boolean = false
    ): AssistantMessage {
        val query = rawQuery.trim().lowercase(Locale.ROOT)

        // 1. Greetings & Identity
        if (query.matches(Regex(".*\\b(hello|hi|hey|good morning|good afternoon|good evening|howdy|what's up|yo)\\b.*")) &&
            !query.contains("joke") && !query.contains("timer") && !query.contains("weather")) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val timeGreeting = when {
                hour in 4..11 -> "Good morning!"
                hour in 12..16 -> "Good afternoon!"
                hour in 17..21 -> "Good evening!"
                else -> "Hello there!"
            }
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "$timeGreeting How can I help you today?",
                spokenText = "$timeGreeting How can I help you today?",
                suggestionChips = listOf("What's the weather?", "Set a 5m timer", "Daily briefing", "Tell me a joke")
            )
        }

        if (query.contains("who are you") || query.contains("what are you") || query.contains("what is your name")) {
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "I'm your Google Assistant, classic edition! I work completely offline with zero AI to help you manage timers, check weather, do calculations, flip coins, and keep your day organized.",
                spokenText = "I'm your Google Assistant, classic edition! Running fast and private with zero AI.",
                suggestionChips = listOf("What can you do?", "Daily briefing", "Flip a coin", "Set a timer")
            )
        }

        if (query.contains("what can you do") || query == "help" || query == "commands" || query.contains("how to use")) {
            return generateCapabilitiesHelp()
        }

        // Default Assistant / Settings
        if (query.contains("default assistant") || query.contains("select assistant") || query.contains("set default") || query.contains("default app") || query.contains("assistant app") || query.contains("voice assistant setting")) {
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "Here's how to select and set this app as your default digital assistant.",
                spokenText = "You can set this app as your default digital assistant in Android settings.",
                card = AssistantCardData.DefaultAssistantSetting(),
                suggestionChips = listOf("Daily briefing", "What's the weather?", "Set a 5m timer")
            )
        }

        // 2. Weather
        if (query.contains("weather") || query.contains("forecast") || query.contains("temperature") || query.contains("is it raining")) {
            return generateWeatherResponse(query)
        }

        // 3. Timers
        if (query.contains("timer") || query.contains("countdown") || query.contains("stopwatch")) {
            return handleTimerQuery(query)
        }

        // 4. Alarms & Reminders & Notes
        if (query.contains("alarm")) {
            return handleAlarmQuery(query)
        }

        if (query.contains("remind") || query.contains("reminder") || query.contains("note") || query.contains("shopping list") || query.contains("todo")) {
            return handleReminderNoteQuery(rawQuery, currentNotes)
        }

        // 5. Daily Briefing / "My Day" / "Good morning"
        if (query.contains("daily briefing") || query.contains("my day") || query.contains("today summary") || query.contains("briefing")) {
            return generateDailyBriefing(currentNotes)
        }

        // 6. Math & Calculations
        val mathResult = tryEvaluateMath(query)
        if (mathResult != null) {
            return mathResult
        }

        // 7. Unit & Currency Conversions
        val conversionResult = tryEvaluateConversion(query)
        if (conversionResult != null) {
            return conversionResult
        }

        // 8. Device Controls (Flashlight, Volume, Battery)
        if (query.contains("flashlight") || query.contains("torch") || query.contains("volume") || query.contains("battery")) {
            return handleDeviceControlQuery(query, isFlashlightOn)
        }

        // 9. Easter Eggs & Games
        // Coin Flip
        if (query.contains("flip a coin") || query.contains("coin flip") || query.contains("toss a coin") || query == "heads or tails") {
            val isHeads = Random.nextBoolean()
            val side = if (isHeads) "Heads" else "Tails"
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "It's $side!",
                spokenText = "It landed on $side!",
                card = AssistantCardData.CoinFlip(isHeads = isHeads, flipCount = 1),
                suggestionChips = listOf("Flip again", "Roll a dice", "Rock paper scissors")
            )
        }

        // Dice Roll
        if (query.contains("roll a dice") || query.contains("roll dice") || query.contains("roll a die") || query.contains("dice")) {
            val roll = Random.nextInt(1, 7)
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "You rolled a $roll!",
                spokenText = "You rolled a $roll!",
                card = AssistantCardData.DiceRoll(rollValue = roll, sides = 6),
                suggestionChips = listOf("Roll again", "Flip a coin", "Tell a joke")
            )
        }

        // Rock Paper Scissors
        if (query.contains("rock paper scissors") || query == "rock" || query == "paper" || query == "scissors") {
            return handleRps(query)
        }

        // Jokes & Riddles
        if (query.contains("joke") || query.contains("make me laugh") || query.contains("funny")) {
            val joke = JOKES.random()
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "${joke.first}\n\n${joke.second}",
                spokenText = "${joke.first} ... ${joke.second}",
                card = AssistantCardData.Joke(setup = joke.first, punchline = joke.second),
                suggestionChips = listOf("Another joke", "Tell me a riddle", "Flip a coin")
            )
        }

        if (query.contains("riddle")) {
            val riddle = RIDDLES.random()
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "Riddle: ${riddle.first}\n\nAnswer: ${riddle.second}",
                spokenText = "${riddle.first} The answer is: ${riddle.second}",
                card = AssistantCardData.Joke(setup = riddle.first, punchline = riddle.second),
                suggestionChips = listOf("Another riddle", "Tell a joke", "Daily briefing")
            )
        }

        // Crystal Ball / 8 Ball
        if (query.contains("magic 8 ball") || query.contains("crystal ball") || query.startsWith("will i") || query.startsWith("should i") || query.startsWith("is it going to")) {
            val ans = MAGIC_8_BALL.random()
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "🎱 The Magic 8-Ball says: \"$ans\"",
                spokenText = "The 8 ball says: $ans",
                card = AssistantCardData.CrystalBall(question = rawQuery, answer = ans),
                suggestionChips = listOf("Ask another question", "Flip a coin", "Roll a dice")
            )
        }

        // Animal sounds
        for ((key, triple) in ANIMAL_SOUNDS) {
            if (query.contains(key)) {
                return AssistantMessage(
                    sender = MessageSender.ASSISTANT,
                    text = "${triple.second} The ${triple.first} goes: \"${triple.third}\"",
                    spokenText = "The ${triple.first} goes ${triple.third}",
                    card = AssistantCardData.AnimalSound(animal = triple.first, emoji = triple.second, soundText = triple.third),
                    suggestionChips = listOf("What does a lion say?", "What does a dog say?", "Tell a joke")
                )
            }
        }

        // Time / Date
        if (query.contains("what time is it") || query.contains("current time") || query == "time") {
            val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "It is currently $time.",
                spokenText = "It is $time.",
                suggestionChips = listOf("What's the date?", "Set a timer", "What's the weather?")
            )
        }

        if (query.contains("what day is it") || query.contains("today's date") || query.contains("what is the date")) {
            val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "Today is $date.",
                spokenText = "Today is $date.",
                suggestionChips = listOf("Daily briefing", "What's the weather?", "Set a reminder")
            )
        }

        // 10. Direct Web Search fallback / Google Search Card
        val searchUrl = "https://www.google.com/search?q=" + java.net.URLEncoder.encode(rawQuery, "UTF-8")
        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Here is what I found for \"$rawQuery\":",
            spokenText = "Here are the search results for $rawQuery.",
            card = AssistantCardData.WebSearch(
                query = rawQuery,
                url = searchUrl,
                snippet = "Tap below to open Google Search in your browser for detailed results."
            ),
            suggestionChips = listOf("What's the weather?", "Set a 5m timer", "Calculate 25 * 4", "Tell a joke")
        )
    }

    private fun generateWeatherResponse(query: String): AssistantMessage {
        val city = if (query.contains(" in ")) {
            query.substringAfter(" in ").trim().replace("?", "").replaceFirstChar { it.uppercase() }
        } else {
            "New York"
        }

        val weatherData = AssistantCardData.Weather(
            city = city,
            tempC = 22,
            tempF = 72,
            condition = "Partly Cloudy",
            conditionIcon = "⛅",
            highTemp = 25,
            lowTemp = 16,
            humidity = 58,
            windSpeedKmh = 14
        )

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Right now in $city, it's 22°C (72°F) and Partly Cloudy. Today's high is 25°C and low is 16°C.",
            spokenText = "Right now in $city, it is 22 degrees and partly cloudy.",
            card = weatherData,
            suggestionChips = listOf("Weather in London", "Weather in Tokyo", "Daily briefing", "Set a timer")
        )
    }

    private fun handleTimerQuery(query: String): AssistantMessage {
        var minutes = 5
        var seconds = 0
        var label = "Timer"

        val minMatch = Regex("(\\d+)\\s*(?:min|minute|minutes|m)").find(query)
        val secMatch = Regex("(\\d+)\\s*(?:sec|second|seconds|s)").find(query)

        if (minMatch != null) {
            minutes = minMatch.groupValues[1].toIntOrNull() ?: 5
        } else {
            val numMatch = Regex("\\b(\\d+)\\b").find(query)
            if (numMatch != null) {
                minutes = numMatch.groupValues[1].toIntOrNull() ?: 5
            }
        }

        if (secMatch != null) {
            seconds = secMatch.groupValues[1].toIntOrNull() ?: 0
        }

        val totalSecs = (minutes * 60) + seconds
        val timerCard = AssistantCardData.Timer(
            label = "$minutes min timer",
            totalSeconds = if (totalSecs > 0) totalSecs else 300,
            remainingSeconds = if (totalSecs > 0) totalSecs else 300,
            isRunning = true
        )

        val durationDesc = if (minutes > 0 && seconds > 0) "$minutes minutes and $seconds seconds"
        else if (minutes > 0) "$minutes minutes" else "$seconds seconds"

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Starting your timer for $durationDesc.",
            spokenText = "Starting a timer for $durationDesc.",
            card = timerCard,
            suggestionChips = listOf("1 min timer", "10 min timer", "Stop timer", "What's the weather?")
        )
    }

    private fun handleAlarmQuery(query: String): AssistantMessage {
        val timeMatch = Regex("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?").find(query)
        val timeFormatted = if (timeMatch != null) {
            val hour = timeMatch.groupValues[1].toIntOrNull() ?: 7
            val min = timeMatch.groupValues[2].toIntOrNull() ?: 0
            val ampm = timeMatch.groupValues[3].ifEmpty { "AM" }.uppercase()
            String.format(Locale.US, "%02d:%02d %s", hour, min, ampm)
        } else {
            "07:00 AM"
        }

        val alarmCard = AssistantCardData.Alarm(
            timeFormatted = timeFormatted,
            label = "Alarm",
            isEnabled = true,
            days = "Everyday"
        )

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Alarm set for $timeFormatted.",
            spokenText = "Your alarm is set for $timeFormatted.",
            card = alarmCard,
            suggestionChips = listOf("Set alarm for 8:00 AM", "Set 5m timer", "Daily briefing")
        )
    }

    private fun handleReminderNoteQuery(rawQuery: String, currentNotes: List<NoteItem>): AssistantMessage {
        val cleanText = rawQuery
            .replace(Regex("(?i)^(remind me to|add|note|put|create reminder to|new note)\\s*"), "")
            .replace(Regex("(?i)\\s*(to my shopping list|to my list|to notes|to reminders)$"), "")
            .trim()

        val title = if (cleanText.isNotBlank()) cleanText.replaceFirstChar { it.uppercase() } else "Buy groceries"
        val items = if (currentNotes.isNotEmpty()) currentNotes.map { it.text } else listOf(title, "Call doctor", "Drink 2L water")

        val noteCard = AssistantCardData.NoteReminder(
            title = "Notes & Reminders",
            items = if (items.contains(title)) items else listOf(title) + items,
            timeLabel = "Today"
        )

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Added \"$title\" to your reminders.",
            spokenText = "I've added $title to your reminders.",
            card = noteCard,
            suggestionChips = listOf("Show my list", "Daily briefing", "Set a timer")
        )
    }

    private fun generateDailyBriefing(notes: List<NoteItem>): AssistantMessage {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour in 4..11 -> "Good morning"
            hour in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        val dateText = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
        val quote = QUOTES.random()

        val briefing = AssistantCardData.DailyBriefing(
            greeting = greeting,
            dateText = dateText,
            weatherSummary = "22°C, Partly Cloudy with sunshine in the afternoon",
            quote = quote.first,
            quoteAuthor = quote.second,
            reminderSummary = if (notes.isNotEmpty()) "${notes.size} active tasks on your list" else "No pressing reminders",
            batteryLevel = 88
        )

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "$greeting! Today is $dateText. The weather is 22°C and partly cloudy. You have ${briefing.reminderSummary}.",
            spokenText = "$greeting! Today is $dateText. Expect partly cloudy skies and 22 degrees.",
            card = briefing,
            suggestionChips = listOf("What's the weather?", "Set a 5m timer", "Tell a joke", "Flip a coin")
        )
    }

    private fun tryEvaluateMath(query: String): AssistantMessage? {
        // Percentage: "25% of 80" or "what is 15% of 200"
        val percentMatch = Regex("(?:what is\\s*)?([0-9.]+)\\s*%\\s*of\\s*([0-9.]+)").find(query)
        if (percentMatch != null) {
            val p = percentMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val n = percentMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val res = (p / 100.0) * n
            val resFormatted = if (res % 1.0 == 0.0) res.toInt().toString() else String.format(Locale.US, "%.2f", res)
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "$p% of $n is $resFormatted",
                spokenText = "$p percent of $n is $resFormatted",
                card = AssistantCardData.MathResult(expression = "$p% × $n", result = resFormatted, category = "Percentage"),
                suggestionChips = listOf("Calculate 50 * 12", "Square root of 144", "Unit converter")
            )
        }

        // Square root: "sqrt of 144" or "square root of 81"
        val sqrtMatch = Regex("(?:sqrt|square root)(?: of)?\\s*([0-9.]+)").find(query)
        if (sqrtMatch != null) {
            val num = sqrtMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val res = sqrt(num)
            val resFormatted = if (res % 1.0 == 0.0) res.toInt().toString() else String.format(Locale.US, "%.4f", res)
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "The square root of $num is $resFormatted",
                spokenText = "The square root of $num is $resFormatted",
                card = AssistantCardData.MathResult(expression = "√$num", result = resFormatted, category = "Square Root"),
                suggestionChips = listOf("15 * 15", "Flip a coin", "Weather")
            )
        }

        // Basic arithmetic: "12 + 45", "100 / 4", "50 * 8", "150 - 32"
        val arithMatch = Regex("(?:what is\\s*|calculate\\s*)?([0-9.]+)\\s*([+\\-*/x×÷^])\\s*([0-9.]+)").find(query)
        if (arithMatch != null) {
            val a = arithMatch.groupValues[1].toDoubleOrNull() ?: return null
            val op = arithMatch.groupValues[2]
            val b = arithMatch.groupValues[3].toDoubleOrNull() ?: return null

            val res = when (op) {
                "+", "plus" -> a + b
                "-", "minus" -> a - b
                "*", "x", "×", "times", "multiplied by" -> a * b
                "/", "÷", "divided by" -> if (b != 0.0) a / b else Double.NaN
                "^" -> a.pow(b)
                else -> 0.0
            }

            if (res.isNaN()) {
                return AssistantMessage(
                    sender = MessageSender.ASSISTANT,
                    text = "Cannot divide by zero.",
                    spokenText = "Cannot divide by zero.",
                    card = AssistantCardData.MathResult(expression = "$a / $b", result = "Undefined")
                )
            }

            val resFormatted = if (res % 1.0 == 0.0) res.toLong().toString() else String.format(Locale.US, "%.4f", res).trimEnd('0').trimEnd('.')
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "$a $op $b = $resFormatted",
                spokenText = "The answer is $resFormatted",
                card = AssistantCardData.MathResult(expression = "$a $op $b", result = resFormatted, category = "Arithmetic"),
                suggestionChips = listOf("Calculate 18% of 75", "Square root of 256", "Daily briefing")
            )
        }

        return null
    }

    private fun tryEvaluateConversion(query: String): AssistantMessage? {
        val convMatch = Regex("([0-9.]+)\\s*([a-zA-Z]+)\\s*(?:in|to|into)\\s*([a-zA-Z]+)").find(query) ?: return null
        val value = convMatch.groupValues[1].toDoubleOrNull() ?: return null
        val fromUnit = convMatch.groupValues[2].lowercase(Locale.ROOT)
        val toUnit = convMatch.groupValues[3].lowercase(Locale.ROOT)

        var converted = 0.0
        var formula = ""
        var valid = true

        when {
            // Temperature
            (fromUnit.startsWith("f") || fromUnit.contains("fahrenheit")) && (toUnit.startsWith("c") || toUnit.contains("celsius")) -> {
                converted = (value - 32) * 5.0 / 9.0
                formula = "($value - 32) × 5/9"
            }
            (fromUnit.startsWith("c") || fromUnit.contains("celsius")) && (toUnit.startsWith("f") || toUnit.contains("fahrenheit")) -> {
                converted = (value * 9.0 / 5.0) + 32
                formula = "($value × 9/5) + 32"
            }
            // Length
            fromUnit in listOf("km", "kilometer", "kilometers") && toUnit in listOf("mi", "mile", "miles") -> {
                converted = value * 0.621371
                formula = "$value × 0.6214"
            }
            fromUnit in listOf("mi", "mile", "miles") && toUnit in listOf("km", "kilometer", "kilometers") -> {
                converted = value * 1.60934
                formula = "$value × 1.6093"
            }
            fromUnit in listOf("m", "meter", "meters") && toUnit in listOf("ft", "feet", "foot") -> {
                converted = value * 3.28084
                formula = "$value × 3.2808"
            }
            fromUnit in listOf("ft", "feet", "foot") && toUnit in listOf("m", "meter", "meters") -> {
                converted = value * 0.3048
                formula = "$value × 0.3048"
            }
            fromUnit in listOf("cm", "centimeter", "centimeters") && toUnit in listOf("in", "inch", "inches") -> {
                converted = value * 0.393701
                formula = "$value × 0.3937"
            }
            fromUnit in listOf("in", "inch", "inches") && toUnit in listOf("cm", "centimeter", "centimeters") -> {
                converted = value * 2.54
                formula = "$value × 2.54"
            }
            // Weight
            fromUnit in listOf("kg", "kilogram", "kilograms") && toUnit in listOf("lbs", "lb", "pound", "pounds") -> {
                converted = value * 2.20462
                formula = "$value × 2.2046"
            }
            fromUnit in listOf("lbs", "lb", "pound", "pounds") && toUnit in listOf("kg", "kilogram", "kilograms") -> {
                converted = value * 0.453592
                formula = "$value × 0.4536"
            }
            // Currency (approx offline rates)
            fromUnit == "usd" && toUnit == "eur" -> {
                converted = value * 0.92
                formula = "$value × 0.92 (EUR/USD)"
            }
            fromUnit == "eur" && toUnit == "usd" -> {
                converted = value * 1.09
                formula = "$value × 1.09 (USD/EUR)"
            }
            fromUnit == "usd" && toUnit == "gbp" -> {
                converted = value * 0.78
                formula = "$value × 0.78 (GBP/USD)"
            }
            fromUnit == "usd" && toUnit == "jpy" -> {
                converted = value * 152.0
                formula = "$value × 152.0 (JPY/USD)"
            }
            fromUnit == "usd" && toUnit == "inr" -> {
                converted = value * 83.5
                formula = "$value × 83.5 (INR/USD)"
            }
            else -> valid = false
        }

        if (!valid) return null

        val formattedResult = String.format(Locale.US, "%.2f", converted)
        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "$value $fromUnit is equal to $formattedResult $toUnit.",
            spokenText = "$value $fromUnit is $formattedResult $toUnit.",
            card = AssistantCardData.UnitConversion(
                fromValue = value,
                fromUnit = fromUnit.uppercase(Locale.ROOT),
                toValue = converted,
                toUnit = toUnit.uppercase(Locale.ROOT),
                formula = formula
            ),
            suggestionChips = listOf("Convert 100 USD to EUR", "50 miles in km", "100 F in C")
        )
    }

    private fun handleDeviceControlQuery(query: String, isFlashlightOn: Boolean): AssistantMessage {
        if (query.contains("flashlight") || query.contains("torch")) {
            val newState = if (query.contains("on") || query.contains("enable")) true
            else if (query.contains("off") || query.contains("disable")) false
            else !isFlashlightOn

            val stateText = if (newState) "turned ON" else "turned OFF"
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "Flashlight is now $stateText.",
                spokenText = "Flashlight $stateText.",
                card = AssistantCardData.DeviceControl(flashlightOn = newState, volumePercent = 70, batteryPercent = 85),
                suggestionChips = listOf(if (newState) "Turn off flashlight" else "Turn on flashlight", "Volume 50%", "Daily briefing")
            )
        }

        if (query.contains("battery")) {
            return AssistantMessage(
                sender = MessageSender.ASSISTANT,
                text = "Battery level is at 85% and discharging normally.",
                spokenText = "Battery level is at 85 percent.",
                card = AssistantCardData.DeviceControl(flashlightOn = isFlashlightOn, volumePercent = 70, batteryPercent = 85),
                suggestionChips = listOf("Daily briefing", "What's the weather?", "Set a timer")
            )
        }

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Device controls ready.",
            card = AssistantCardData.DeviceControl(flashlightOn = isFlashlightOn, volumePercent = 70, batteryPercent = 85),
            suggestionChips = listOf("Flashlight on", "Battery level", "Set a timer")
        )
    }

    private fun handleRps(query: String): AssistantMessage {
        val userChoice = when {
            query.contains("rock") -> "Rock"
            query.contains("paper") -> "Paper"
            query.contains("scissors") -> "Scissors"
            else -> "Rock"
        }
        val options = listOf("Rock", "Paper", "Scissors")
        val assistantChoice = options.random()

        val outcome = when {
            userChoice == assistantChoice -> "It's a tie!"
            (userChoice == "Rock" && assistantChoice == "Scissors") ||
            (userChoice == "Paper" && assistantChoice == "Rock") ||
            (userChoice == "Scissors" && assistantChoice == "Paper") -> "You win! 🎉"
            else -> "I win! 🤖"
        }

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "You chose $userChoice, I chose $assistantChoice.\n$outcome",
            spokenText = "You chose $userChoice, I chose $assistantChoice. $outcome",
            card = AssistantCardData.RockPaperScissors(userChoice = userChoice, assistantChoice = assistantChoice, outcome = outcome),
            suggestionChips = listOf("Play Rock", "Play Paper", "Play Scissors", "Flip a coin")
        )
    }

    private fun generateCapabilitiesHelp(): AssistantMessage {
        val categories = listOf(
            HelpCategory("Voice & Quick Actions", "🎙️", listOf("Daily briefing", "What time is it?", "What's the date?")),
            HelpCategory("Weather & Forecasts", "⛅", listOf("What's the weather?", "Weather in London", "Is it raining?")),
            HelpCategory("Timers & Alarms", "⏰", listOf("Set a 5 minute timer", "10 minute timer", "Set alarm for 7:30 AM")),
            HelpCategory("Math & Conversions", "📐", listOf("What is 15 * 84", "25% of 80", "100 USD to EUR", "50 miles in km")),
            HelpCategory("Notes & Reminders", "📝", listOf("Remind me to buy milk", "Add coffee to shopping list", "Show my lists")),
            HelpCategory("Device Toggles", "🔦", listOf("Turn on flashlight", "Turn off flashlight", "Battery level")),
            HelpCategory("Games & Fun", "🎲", listOf("Flip a coin", "Roll a dice", "Tell me a joke", "Magic 8 ball", "Rock paper scissors"))
        )

        return AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Here are things you can ask me to do without any internet or AI:",
            spokenText = "Here are commands you can use anytime offline.",
            card = AssistantCardData.CapabilitiesHelp(categories = categories),
            suggestionChips = listOf("What's the weather?", "Set 5m timer", "Daily briefing", "Flip a coin")
        )
    }
}
