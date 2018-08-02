package org.jtodd.kvend.vend

import org.junit.Before
import org.junit.Test as test

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class VendingMachineTest {

    val defaultStock: Map<Product, Int> = Product.values().associate { it to 10 }
    val defaultBank: Map<Coin, Int> = VendingMachine.acceptableCoins.associate { it to 10 }
    lateinit var machine: VendingMachine

    @Before
    fun setUp() {
        machine = VendingMachine(stock = defaultStock, bank = defaultBank)
    }

    @test
    fun `when created display shows a message`() {
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Wrong default display")
    }

    @test
    fun `cannot be initiated with negative stock for a product`() {
        try {
            VendingMachine(stock = mapOf(Product.Cola to 1, Product.Candy to -1), bank = defaultBank)
            fail("Can not create vending machine with negative stock")
        } catch (e: IllegalArgumentException) {}
    }

    @test
    fun `cannot be initiated with negative count for a coin`() {
        try {
            VendingMachine(stock = defaultStock, bank = mapOf(Denominations.Quarter to 1, Denominations.Dime to -1))
            fail("Can not create vending machine with negative bank")
        } catch (e: IllegalArgumentException) {}
    }

    @test
    fun `display amounts correctly`() {
        assertEquals(expected = "$0.15", actual = VendingMachine.formatValue(15), message = "Wrong format")
        assertEquals(expected = "$1.00", actual = VendingMachine.formatValue(100), message = "Wrong format")
    }

    @test
    fun `can accept coins and update the display`() {
        val d = Denominations.Dime
        val n = Denominations.Nickel
        machine.accept(d)
        machine.accept(n)
        assertEquals(expected = "$0.15", actual = machine.display(), message = "Wrong display after coin insert")
    }

    @test
    fun `only nickels, dimes, and quarters are valid inputs`() {
        val p = Denominations.Penny
        val s = Slug(1, 1)
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Nickel)
        machine.accept(Denominations.Quarter)
        machine.accept(p)
        machine.accept(s)
        assertEquals(expected = "$0.40", actual = machine.display(), message = "Wrong display after some coins inserted")
        assertEquals(expected = listOf(p, s), actual = machine.coinReturn(), message = "Wrong coin list returned")
    }

    @test
    fun `test matchCoins`() {
        val q1: Coin = Denominations.Quarter
        val n: Coin = Denominations.Nickel
        val s: Coin = Slug(2121, 5000)
        assertTrue(actual = VendingMachine.matchCoins(q1, Denominations.Quarter), message = "Should have matched quarter to quarter")
        assertFalse(actual = VendingMachine.matchCoins(Denominations.Quarter, n), message = "Should not have matched quarter to nickel")
        assertFalse(actual = VendingMachine.matchCoins(q1, s), message = "Should not have matched quarter to slug")
        assertTrue(actual = VendingMachine.matchCoins(s, n), message = "Should have matched nickel to a well-crafted slug")
    }

    @test
    fun `when enough money deposited and product selected display message and update accepted value`() {
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.buy(Product.Chips)
        assertEquals(expected = VendingMachine.THANK_YOU, actual = machine.display(), message = "Error on purchase")
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Error on display reset")
    }

    @test
    fun `when more than enough money deposited and product selected display message set value to zero and make change`() {
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Nickel)
        machine.buy(Product.Chips)
        assertEquals(expected = VendingMachine.THANK_YOU, actual = machine.display(), message = "Error on purchase")
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Error on display reset")
        assertTrue(actual = Companion.compareCoinLists(listOf(Denominations.Nickel), machine.coinReturn()), message = "Extra money not returned in coin return")
    }

    @test
    fun `when insufficient money deposited and a product selected display price then display accepted amount`() {
        machine.buy(Product.Cola)
        assertEquals(expected = "PRICE: $1.00", actual = machine.display(), message = "Error on purchase")
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Error on display reset")
    }

    @test
    fun `set display with lifetime shows the message the right number of times then switches back to default`() {
        val testMessage = "Test"
        val testCount = 2

        machine.setDisplayWithLifetime(testMessage, testCount)
        for (i in 1 .. testCount) {
            assertEquals(expected = testMessage, actual = machine.display(), message = "Wrong test message")
        }
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Error on display reset")
    }

    @test
    fun `when no money deposited coin return does nothing`() {
        machine.cancel()
        assertEquals(expected = listOf(), actual = machine.coinReturn(), message = "Incorrect coin return")
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Wrong message displayed")
    }

    @test
    fun `coin return returns all money deposited`() {
        machine.accept(Denominations.Dime)
        machine.cancel()
        assertTrue(actual = Companion.compareCoinLists(listOf(Denominations.Dime), machine.coinReturn()), message = "Incorrect coin return")
        assertEquals(expected = VendingMachine.INSERT_COIN, actual = machine.display(), message = "Wrong message displayed")
    }

    @test
    fun `vending machine makes optimal return list when cancel is pushed`() {
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Nickel)
        assertTrue(actual = Companion.compareCoinLists(listOf(Denominations.Quarter), machine.coinReturn()), message = "Incorrect coin return")
    }

    @test
    fun `can not sell an item that is out of stock`() {
        val machine = VendingMachine(mapOf(Product.Candy to 0), defaultBank)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Nickel)
        machine.buy(Product.Candy)
        assertEquals(expected = VendingMachine.SOLD_OUT, actual = machine.display(), message = "Incorrect message when product out of stock")
        assertEquals(expected = "$0.65", actual = machine.display(), message = "Wrong message displayed")
    }

    @test
    fun `can sell product until it is depleted but no more`() {
        val machine = VendingMachine(mapOf(Product.Chips to 1), defaultBank)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.buy(Product.Chips)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.buy(Product.Chips)
        assertEquals(expected = VendingMachine.SOLD_OUT, actual = machine.display(), message = "Incorrect message when product out of stock")
        assertEquals(expected = "$0.50", actual = machine.display(), message = "Wrong message displayed")
    }

    @test
    fun `displays different message if it cannot make change`() {
        val machine = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10, Denominations.Dime to 10, Denominations.Nickel to 0))
        assertEquals(VendingMachine.EXACT_CHANGE_ONLY, machine.display())
    }

    @test
    fun `detect if any coin is depleted in bank`() {
        val vm1 = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10, Denominations.Dime to 10, Denominations.Nickel to 0))
        assertFalse(actual = vm1.canMakeChange(), message = "Machine with no nickels is not able to make change")
        val vm2 = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10))
        assertFalse(actual = vm2.canMakeChange(), message = "Machine with no dimes or nickels is not able to make change")
        val vm3 = VendingMachine(defaultStock, VendingMachine.acceptableCoins.associate { it to 1 } )
        assertTrue(actual = vm3.canMakeChange(), message = "Machine with quarters, dimes, and nickels can make change")
    }

    companion object {
        fun compareCoinLists(l1: List<Coin>, l2: List<Coin>): Boolean {
            val l1Sorted = l1.sortedBy { it -> it.monetaryValue }
            val l2Sorted = l2.sortedBy { it -> it.monetaryValue }
            return l1Sorted.zip(l2Sorted).all { (c1, c2) -> VendingMachine.matchCoins(c1, c2) }
        }
    }
}