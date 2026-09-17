package com.example

import com.example.engine.DeterministicAssistantEngine
import com.example.model.AssistantCardData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWeatherQuery() {
        val res = DeterministicAssistantEngine.processQuery("what's the weather in Tokyo")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.Weather)
        assertEquals("Tokyo", (res.card as AssistantCardData.Weather).city)
    }

    @Test
    fun testMathCalculation() {
        val res = DeterministicAssistantEngine.processQuery("what is 15 * 8")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.MathResult)
        assertEquals("120", (res.card as AssistantCardData.MathResult).result)
    }

    @Test
    fun testPercentageCalculation() {
        val res = DeterministicAssistantEngine.processQuery("25% of 80")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.MathResult)
        assertEquals("20", (res.card as AssistantCardData.MathResult).result)
    }

    @Test
    fun testUnitConversion() {
        val res = DeterministicAssistantEngine.processQuery("convert 100 USD to EUR")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.UnitConversion)
    }

    @Test
    fun testCoinFlip() {
        val res = DeterministicAssistantEngine.processQuery("flip a coin")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.CoinFlip)
    }

    @Test
    fun testDefaultAssistantQuery() {
        val res = DeterministicAssistantEngine.processQuery("select default assistant")
        assertNotNull(res.card)
        assertTrue(res.card is AssistantCardData.DefaultAssistantSetting)
    }
}

