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
        machine = VendingMachine(defaultStock, defaultBank)
    }

    @test
    fun `when created display shows a message`() {
        assertEquals(VendingMachine.INSERT_COIN, machine.display())
    }

    @test
    fun `cannot be initiated with negative stock for a product`() {
        try {
            VendingMachine(mapOf(Product.Cola to 1, Product.Candy to -1), defaultBank)
            fail("Can not create vending machine with negative stock")
        } catch (e: IllegalArgumentException) {}
    }

    @test
    fun `cannot be initiated with negative count for a coin`() {
        try {
            VendingMachine(defaultStock, mapOf(Denominations.Quarter to 1, Denominations.Dime to -1))
            fail("Can not create vending machine with negative bank")
        } catch (e: IllegalArgumentException) {}
    }

    @test
    fun `display amounts correctly`() {
        assertEquals("$0.15", VendingMachine.formatValue(15), "Wrong format")
        assertEquals("$1.00", VendingMachine.formatValue(100), "Wrong format")
    }

    @test
    fun `can accept coins and update the display`() {
        val d = Denominations.Dime
        val n = Denominations.Nickel
        machine.accept(d)
        machine.accept(n)
        assertEquals("$0.15", machine.display())
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
        assertEquals("$0.40", machine.display())
        assertEquals(listOf(p, s), machine.coinReturn())
    }

    @test
    fun `test matchCoins`() {
        val q1: Coin = Denominations.Quarter
        val n: Coin = Denominations.Nickel
        val s: Coin = Slug(2121, 5000)
        assertTrue(VendingMachine.matchCoins(q1, Denominations.Quarter))
        assertFalse(VendingMachine.matchCoins(Denominations.Quarter, n))
        assertFalse(VendingMachine.matchCoins(q1, s))
        assertTrue(VendingMachine.matchCoins(s, n))
    }

    @test
    fun `when enough money deposited and product selected display message and update accepted value`() {
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.buy(Product.Chips)
        assertEquals(VendingMachine.THANK_YOU, machine.display(), "Error on purchase")
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Error on display reset")
    }

    @test
    fun `when more than enough money deposited and product selected display message set value to zero and make change`() {
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Nickel)
        machine.buy(Product.Chips)
        assertEquals(VendingMachine.THANK_YOU, machine.display(), "Error on purchase")
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Error on display reset")
        assertTrue(compareCoinLists(listOf(Denominations.Nickel), machine.coinReturn()), "Extra money not returned in coin return")
    }

    @test
    fun `when insufficient money deposited and a product selected display price then display accepted amount`() {
        machine.buy(Product.Cola)
        assertEquals("PRICE: $1.00", machine.display(), "Error on purchase")
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Error on display reset")
    }

    @test
    fun `set display with lifetime shows the message the right number of times then switches back to default`() {
        val testMessage = "Test"
        val testCount = 2

        machine.setDisplayWithLifetime(testMessage, testCount)
        for (i in 1 .. testCount) {
            assertEquals(testMessage, machine.display(), "Wrong test message")
        }
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Error on display reset")
    }

    @test
    fun `when no money deposited coin return does nothing`() {
        machine.cancel()
        assertEquals(listOf(), machine.coinReturn(), "Incorrect coin return")
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Wrong message displayed")
    }

    @test
    fun `coin return returns all money deposited`() {
        machine.accept(Denominations.Dime)
        machine.cancel()
        assertTrue(compareCoinLists(listOf(Denominations.Dime), machine.coinReturn()), "Incorrect coin return")
        assertEquals(VendingMachine.INSERT_COIN, machine.display(), "Wrong message displayed")
    }

    @test
    fun `vending machine makes optimal return list when cancel is pushed`() {
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Nickel)
        assertTrue(compareCoinLists(listOf(Denominations.Quarter), machine.coinReturn()), "Incorrect coin return")
    }

    @test
    fun `can not sell an item that is out of stock`() {
        val machine = VendingMachine(mapOf(Product.Candy to 0), defaultBank)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Quarter)
        machine.accept(Denominations.Dime)
        machine.accept(Denominations.Nickel)
        machine.buy(Product.Candy)
        assertEquals(VendingMachine.SOLD_OUT, machine.display(), "Incorrect message when product out of stock")
        assertEquals("$0.65", machine.display(), "Wrong message displayed")
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
        assertEquals(VendingMachine.SOLD_OUT, machine.display(), "Incorrect message when product out of stock")
        assertEquals("$0.50", machine.display(), "Wrong message displayed")
    }

    @test
    fun `displays different message if it cannot make change`() {
        val machine = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10, Denominations.Dime to 10, Denominations.Nickel to 0))
        assertEquals(VendingMachine.EXACT_CHANGE_ONLY, machine.display())
    }

    @test
    fun `detect if any coin is depleted in bank`() {
        val vm1 = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10, Denominations.Dime to 10, Denominations.Nickel to 0))
        assertFalse(vm1.canMakeChange(), "Machine with no nickels is not able to make change")
        val vm2 = VendingMachine(defaultStock, mapOf(Denominations.Quarter to 10))
        assertFalse(vm2.canMakeChange(), "Machine with no dimes or nickels is not able to make change")
        val vm3 = VendingMachine(defaultStock, VendingMachine.acceptableCoins.associate { it to 1 } )
        assertTrue(vm3.canMakeChange(), "Machine with quarters, dimes, and nickels can make change")
    }

    fun compareCoinLists(l1: List<Coin>, l2: List<Coin>): Boolean {
        val l1Sorted = l1.sortedBy { it -> it.monetaryValue }
        val l2Sorted = l2.sortedBy { it -> it.monetaryValue }
        return l1Sorted.zip(l2Sorted).all { (c1, c2) -> VendingMachine.matchCoins(c1, c2) }
    }
}