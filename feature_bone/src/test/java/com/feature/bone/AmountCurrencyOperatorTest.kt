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
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var usdAmount10: AmountCurrency
    private lateinit var usdAmount5: AmountCurrency
    private lateinit var usdAmountZero: AmountCurrency
    private lateinit var eurAmount5: AmountCurrency

    @BeforeEach
    fun setUp() {
        usdAmount10 = AmountCurrency(Currency.USD, 10.0f)
        usdAmount5 = AmountCurrency(Currency.USD, 5.0f)
        usdAmountZero = AmountCurrency(Currency.USD, 0.0f)
        eurAmount5 = AmountCurrency(Currency.EUR, 5.0f)
    }

    @Test
    fun `plus operator should add amounts correctly for same currency`() {
        val result = usdAmount10 + usdAmount5

        assertEquals(Currency.USD, result.currency)
        assertEquals(15.0f, result.amount, 0.001f)
    }

    @Test
    fun `plus operator should throw exception for different currencies`() {
        val exception = assertThrows<IllegalArgumentException> {
            usdAmount10 + eurAmount5
        }
        assertEquals("Currencies must match for arithmetic operations", exception.message)
    }

    @Test
    fun `minus operator should subtract amounts correctly`() {
        val usdAmount3 = AmountCurrency(Currency.USD, 3.0f)
        val result = usdAmount10 - usdAmount3

        assertEquals(Currency.USD, result.currency)
        assertEquals(7.0f, result.amount, 0.001f)
    }

    @Test
    fun `times operator should multiply amounts correctly`() {
        val usdAmount2_5 = AmountCurrency(Currency.USD, 2.5f)
        val result = usdAmount10 * usdAmount2_5

        assertEquals(Currency.USD, result.currency)
        assertEquals(25.0f, result.amount, 0.001f)
    }

    @Test
    fun `div operator should divide amounts correctly`() {
        val usdAmount15 = AmountCurrency(Currency.USD, 15.0f)
        val usdAmount3 = AmountCurrency(Currency.USD, 3.0f)
        val result = usdAmount15 / usdAmount3

        assertEquals(Currency.USD, result.currency)
        assertEquals(5.0f, result.amount, 0.001f)
    }

    @Test
    fun `div operator should throw exception when dividing by zero`() {
        val exception = assertThrows<IllegalArgumentException> {
            usdAmount10 / usdAmountZero
        }
        assertEquals("Cannot divide by zero", exception.message)
    }

    @Test
    fun `operations with numbers should work correctly`() {
        assertEquals(15.0f, (usdAmount10 + 5).amount, 0.001f)
        assertEquals(5.0f, (usdAmount10 - 5).amount, 0.001f)
        assertEquals(20.0f, (usdAmount10 * 2).amount, 0.001f)
        assertEquals(5.0f, (usdAmount10 / 2).amount, 0.001f)
    }

    @Test
    fun `number operations should work correctly`() {
        assertEquals(15.0f, (5 + usdAmount10).amount, 0.001f)
        assertEquals(5.0f, (15 - usdAmount10).amount, 0.001f)
        assertEquals(20.0f, (2 * usdAmount10).amount, 0.001f)
        assertEquals(2.0f, (20 / usdAmount10).amount, 0.001f)
    }

    @Test
    fun `division by zero with numbers should throw exception`() {
        assertThrows<IllegalArgumentException> {
            20 / usdAmountZero
        }

        assertThrows<IllegalArgumentException> {
            usdAmountZero / 0
        }
    }

    @Test
    fun `unary operators should work correctly`() {
        assertEquals(-10.0f, (-usdAmount10).amount, 0.001f)
        assertEquals(10.0f, (+usdAmount10).amount, 0.001f)
        assertSame(usdAmount10, +usdAmount10) // unaryPlus должен возвращать тот же объект
    }

    @Test
    fun `comparison operators should work correctly`() {
        val usdAmount10Copy = AmountCurrency(Currency.USD, 10.0f)

        assertTrue(usdAmount10 > usdAmount5)
        assertTrue(usdAmount5 < usdAmount10)
        assertEquals(usdAmount10, usdAmount10Copy)
        assertEquals(0, usdAmount10.compareTo(usdAmount10Copy))
        assertTrue(usdAmount10.compareTo(usdAmount5) > 0)
        assertTrue(usdAmount5.compareTo(usdAmount10) < 0)
    }

    @Test
    fun `comparison should throw exception for different currencies`() {
        val exception = assertThrows<IllegalArgumentException> {
            usdAmount10.compareTo(eurAmount5)
        }
        assertEquals("Currencies must match for comparison", exception.message)
    }

    @Test
    fun `operations with negative numbers should work correctly`() {
        val usdAmountNegative = AmountCurrency(Currency.USD, -10.0f)
        val result = usdAmountNegative + 5

        assertEquals(-5.0f, result.amount, 0.001f)
    }

    @Test
    fun `operations should preserve currency properties`() {
        val result = usdAmount10 * 2

        assertEquals(Currency.USD, result.currency)
        assertEquals('$', result.iconChar)
        assertEquals(Color(0xFF4CAF50), result.color)
    }

    @Test
    fun `operations with decimal precision should work correctly`() {
        val usd1 = AmountCurrency(Currency.USD, 0.1f)
        val usd2 = AmountCurrency(Currency.USD, 0.2f)
        val result = usd1 + usd2

        assertEquals(0.3f, result.amount, 0.001f)
    }

    @Test
    fun `operations with very small numbers should work correctly`() {
        val usdAmountSmall1 = AmountCurrency(Currency.USD, 0.001f)
        val usdAmountSmall2 = AmountCurrency(Currency.USD, 0.002f)
        val result = usdAmountSmall1 + usdAmountSmall2

        assertEquals(0.003f, result.amount, 0.0001f)
    }

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun `currency properties should be initialized correctly`(currency: Currency) {
        val amount = 100.0f
        val amountCurrency = AmountCurrency(currency, amount)

        assertEquals(currency, amountCurrency.currency)
        assertEquals(amount, amountCurrency.amount)

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

            else -> {
                assertNotNull(amountCurrency.iconChar)
                assertNotNull(amountCurrency.color)
            }
        }
    }

    @Test
    fun `operations should maintain immutability`() {
        val result = usdAmount10 + 5

        assertEquals(10.0f, usdAmount10.amount)
        assertEquals(15.0f, result.amount)
        assertNotSame(usdAmount10, result)
    }

    @Test
    fun `copy method should work correctly`() {
        val copy = usdAmount10.copy(amount = 20.0f)

        assertEquals(Currency.USD, copy.currency)
        assertEquals(20.0f, copy.amount)
        assertEquals(10.0f, usdAmount10.amount) // оригинал не изменился
    }
}