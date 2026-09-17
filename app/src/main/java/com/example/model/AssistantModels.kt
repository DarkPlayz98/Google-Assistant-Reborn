package com.example.model

import java.util.UUID

enum class MessageSender {
    USER,
    ASSISTANT
}

data class AssistantMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val spokenText: String = text,
    val timestamp: Long = System.currentTimeMillis(),
    val card: AssistantCardData? = null,
    val suggestionChips: List<String> = emptyList()
)

sealed interface AssistantCardData {
    data class Weather(
        val city: String = "Current Location",
        val tempC: Int = 22,
        val tempF: Int = 72,
        val condition: String = "Partly Cloudy",
        val conditionIcon: String = "⛅",
        val highTemp: Int = 26,
        val lowTemp: Int = 16,
        val humidity: Int = 54,
        val windSpeedKmh: Int = 14,
        val hourlyForecast: List<HourlyWeather> = listOf(
            HourlyWeather("Now", 22, "⛅"),
            HourlyWeather("11 AM", 23, "☀️"),
            HourlyWeather("12 PM", 25, "☀️"),
            HourlyWeather("1 PM", 26, "⛅"),
            HourlyWeather("2 PM", 25, "🌤️"),
            HourlyWeather("3 PM", 24, "🌧️"),
            HourlyWeather("4 PM", 22, "⛅"),
            HourlyWeather("5 PM", 20, "🌤️")
        ),
        val weeklyForecast: List<DailyWeather> = listOf(
            DailyWeather("Today", "⛅", 26, 16),
            DailyWeather("Thu", "☀️", 27, 17),
            DailyWeather("Fri", "🌧️", 21, 15),
            DailyWeather("Sat", "⛅", 24, 16),
            DailyWeather("Sun", "☀️", 28, 18)
        )
    ) : AssistantCardData

    data class Timer(
        val timerId: String = UUID.randomUUID().toString(),
        val label: String = "Timer",
        val totalSeconds: Int = 300,
        val remainingSeconds: Int = 300,
        val isRunning: Boolean = false,
        val isCompleted: Boolean = false
    ) : AssistantCardData

    data class Alarm(
        val alarmId: String = UUID.randomUUID().toString(),
        val timeFormatted: String = "07:00 AM",
        val label: String = "Morning Alarm",
        val isEnabled: Boolean = true,
        val days: String = "Everyday"
    ) : AssistantCardData

    data class MathResult(
        val expression: String,
        val result: String,
        val category: String = "Calculation"
    ) : AssistantCardData

    data class UnitConversion(
        val fromValue: Double,
        val fromUnit: String,
        val toValue: Double,
        val toUnit: String,
        val formula: String
    ) : AssistantCardData

    data class DailyBriefing(
        val greeting: String,
        val dateText: String,
        val weatherSummary: String,
        val quote: String,
        val quoteAuthor: String,
        val reminderSummary: String,
        val batteryLevel: Int
    ) : AssistantCardData

    data class DeviceControl(
        val flashlightOn: Boolean = false,
        val volumePercent: Int = 65,
        val batteryPercent: Int = 85,
        val isCharging: Boolean = false
    ) : AssistantCardData

    data class CoinFlip(
        val isHeads: Boolean,
        val flipCount: Int = 1
    ) : AssistantCardData

    data class DiceRoll(
        val rollValue: Int,
        val sides: Int = 6,
        val rollCount: Int = 1
    ) : AssistantCardData

    data class RockPaperScissors(
        val userChoice: String,
        val assistantChoice: String,
        val outcome: String // "You win!", "I win!", "It's a tie!"
    ) : AssistantCardData

    data class Joke(
        val setup: String,
        val punchline: String
    ) : AssistantCardData

    data class NoteReminder(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val items: List<String> = emptyList(),
        val timeLabel: String = "Today"
    ) : AssistantCardData

    data class WebSearch(
        val query: String,
        val url: String,
        val snippet: String
    ) : AssistantCardData

    data class CapabilitiesHelp(
        val categories: List<HelpCategory>
    ) : AssistantCardData

    data class CrystalBall(
        val question: String,
        val answer: String
    ) : AssistantCardData

    data class AnimalSound(
        val animal: String,
        val emoji: String,
        val soundText: String
    ) : AssistantCardData

    data class DefaultAssistantSetting(
        val isSupported: Boolean = true,
        val instructions: String = "Set this app as your default digital assistant app so you can launch it anytime with home button long-press or swipe-up gesture."
    ) : AssistantCardData
}

data class HourlyWeather(
    val time: String,
    val tempC: Int,
    val icon: String
)

data class DailyWeather(
    val day: String,
    val icon: String,
    val highC: Int,
    val lowC: Int
)

data class HelpCategory(
    val title: String,
    val icon: String,
    val sampleQueries: List<String>
)

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isDone: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
