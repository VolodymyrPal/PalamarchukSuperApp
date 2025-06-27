package com.feature.bone

import androidx.compose.ui.graphics.Color
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.compareTo
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.div
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.minus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.plus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.times
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.unaryMinus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.unaryPlus
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AmountCurrencyOperatorTest {

    @Test
    fun `plus operator adds amounts correctly for same currency`() {
        val usd1 = AmountCurrency(Currency.USD, 10.0f)
        val usd2 = AmountCurrency(Currency.USD, 5.0f)
        val result = usd1 + usd2

        assertEquals(Currency.USD, result.currency)
        assertEquals(15.0f, result.amount, 0.001f)
    }

    @Test
    fun `plus operator throws exception for different currencies`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)
        val eur = AmountCurrency(Currency.EUR, 5.0f)

        val exception = assertThrows<IllegalArgumentException> {
            usd + eur
        }
        assertEquals("Currencies must match for arithmetic operations", exception.message)
    }

    @Test
    fun `minus operator subtracts amounts correctly`() {
        val usd1 = AmountCurrency(Currency.USD, 10.0f)
        val usd2 = AmountCurrency(Currency.USD, 3.0f)
        val result = usd1 - usd2

        assertEquals(Currency.USD, result.currency)
        assertEquals(7.0f, result.amount, 0.001f)
    }

    @Test
    fun `times operator multiplies amounts correctly`() {
        val usd1 = AmountCurrency(Currency.USD, 10.0f)
        val usd2 = AmountCurrency(Currency.USD, 2.5f)
        val result = usd1 * usd2

        assertEquals(Currency.USD, result.currency)
        assertEquals(25.0f, result.amount, 0.001f)
    }

    @Test
    fun `div operator divides amounts correctly`() {
        val usd1 = AmountCurrency(Currency.USD, 15.0f)
        val usd2 = AmountCurrency(Currency.USD, 3.0f)
        val result = usd1 / usd2

        assertEquals(Currency.USD, result.currency)
        assertEquals(5.0f, result.amount, 0.001f)
    }

    @Test
    fun `div operator throws exception when dividing by zero`() {
        val usd1 = AmountCurrency(Currency.USD, 10.0f)
        val usdZero = AmountCurrency(Currency.USD, 0.0f)

        val exception = assertThrows<IllegalArgumentException> {
            usd1 / usdZero
        }
        assertEquals("Cannot divide by zero", exception.message)
    }

    // ОПЕРАЦИИ С ЧИСЛАМИ
    @Test
    fun `operations with numbers work correctly`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)

        assertEquals(15.0f, (usd + 5).amount, 0.001f)
        assertEquals(5.0f, (usd - 5).amount, 0.001f)
        assertEquals(20.0f, (usd * 2).amount, 0.001f)
        assertEquals(5.0f, (usd / 2).amount, 0.001f)
    }

    @Test
    fun `number operations work correctly`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)

        assertEquals(15.0f, (5 + usd).amount, 0.001f)
        assertEquals(5.0f, (15 - usd).amount, 0.001f)
        assertEquals(20.0f, (2 * usd).amount, 0.001f)
        assertEquals(2.0f, (20 / usd).amount, 0.001f)
    }

    @Test
    fun `division by zero with numbers throws exception`() {
        val usdZero = AmountCurrency(Currency.USD, 0.0f)

        assertThrows<IllegalArgumentException> {
            20 / usdZero
        }

        assertThrows<IllegalArgumentException> {
            usdZero / 0
        }
    }

    // УНАРНЫЕ ОПЕРАТОРЫ
    @Test
    fun `unary operators work correctly`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)

        assertEquals(-10.0f, (-usd).amount, 0.001f)
        assertEquals(10.0f, (+usd).amount, 0.001f)
        assertSame(usd, +usd) // unaryPlus должен возвращать тот же объект
    }

    // СРАВНЕНИЯ
    @Test
    fun `comparison operators work correctly`() {
        val usd10 = AmountCurrency(Currency.USD, 10.0f)
        val usd5 = AmountCurrency(Currency.USD, 5.0f)
        val usd10Copy = AmountCurrency(Currency.USD, 10.0f)

        assertTrue(usd10 > usd5)
        assertTrue(usd5 < usd10)
        assertEquals(usd10, usd10Copy)
        assertEquals(0, usd10.compareTo(usd10Copy))
        assertTrue(usd10.compareTo(usd5) > 0)
        assertTrue(usd5.compareTo(usd10) < 0)
    }

    @Test
    fun `comparison throws exception for different currencies`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)
        val eur = AmountCurrency(Currency.EUR, 5.0f)

        val exception = assertThrows<IllegalArgumentException> {
            usd.compareTo(eur)
        }
        assertEquals("Currencies must match for comparison", exception.message)
    }

    // EDGE CASES
    @Test
    fun `operations with negative numbers work correctly`() {
        val usd = AmountCurrency(Currency.USD, -10.0f)
        val result = usd + 5

        assertEquals(-5.0f, result.amount, 0.001f)
    }

    @Test
    fun `operations preserve currency properties`() {
        val usd = AmountCurrency(Currency.USD, 10.0f)
        val result = usd * 2

        // Проверяем, что все вычисляемые свойства сохранились
        assertEquals(Currency.USD, result.currency)
        assertEquals('$', result.iconChar)
        assertEquals(Color(0xFF4CAF50), result.color)
    }

    @Test
    fun `operations with decimal precision`() {
        val usd1 = AmountCurrency(Currency.USD, 0.1f)
        val usd2 = AmountCurrency(Currency.USD, 0.2f)
        val result = usd1 + usd2

        // Float precision может быть проблемой, поэтому используем delta
        assertEquals(0.3f, result.amount, 0.001f)
    }

    @Test
    fun `operations with very small numbers`() {
        val usd1 = AmountCurrency(Currency.USD, 0.001f)
        val usd2 = AmountCurrency(Currency.USD, 0.002f)
        val result = usd1 + usd2

        assertEquals(0.003f, result.amount, 0.0001f)
    }

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun `currency properties are initialized correctly`(currency: Currency) {
        val amount = 100.0f
        val amountCurrency = AmountCurrency(currency, amount)

        assertEquals(currency, amountCurrency.currency)
        assertEquals(amount, amountCurrency.amount)

        // Проверяем соответствие свойств для каждой валюты
        when (currency) {
            Currency.USD -> {
                assertEquals('$', amountCurrency.iconChar)
                assertEquals(Color(0xFF4CAF50), amountCurrency.color)
                assertEquals(R.drawable.usd_sign, amountCurrency.iconResource)
                assertEquals(R.string.usd, amountCurrency.currencyTextRes)
                assertEquals(R.drawable.usa, amountCurrency.currencyCountry)
            }

            Currency.EUR -> {
                assertEquals('€', amountCurrency.iconChar)
                assertEquals(Color(0xFF2196F3), amountCurrency.color)
                assertEquals(R.drawable.euro_sign, amountCurrency.iconResource)
                assertEquals(R.string.eur, amountCurrency.currencyTextRes)
                assertEquals(R.drawable.europe, amountCurrency.currencyCountry)
            }
            // ... остальные валюты
            else -> {
                // Можно добавить общие проверки для всех валют
                assertNotNull(amountCurrency.iconChar)
                assertNotNull(amountCurrency.color)
            }
        }
    }

    @Test
    fun `operations maintain immutability`() {
        val original = AmountCurrency(Currency.USD, 10.0f)
        val result = original + 5

        // Оригинальный объект не должен измениться
        assertEquals(10.0f, original.amount)
        assertEquals(15.0f, result.amount)
        assertNotSame(original, result)
    }

    @Test
    fun `copy method works correctly`() {
        val original = AmountCurrency(Currency.USD, 10.0f)
        val copy = original.copy(amount = 20.0f)

        assertEquals(Currency.USD, copy.currency)
        assertEquals(20.0f, copy.amount)
        assertEquals(10.0f, original.amount) // оригинал не изменился
    }
}