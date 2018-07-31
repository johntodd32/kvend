package org.jtodd.kvend.vend

import org.junit.Before
import org.junit.Test as test

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class VendingMachineTest {

    val defaultStock: Map<Product, Int> = mapOf(ProductImpl.Cola to 10, ProductImpl.Chips to 10, ProductImpl.Candy to 10)
    val defaultBank: Map<Coin, Int> = mapOf(Quarter to 10, Dime to 10, Nickel to 10)
    lateinit var machine: VendingMachine

    @Before
    fun setUp() {
        machine = VendingMachine(defaultStock, defaultBank)
    }

    @test
    fun `when created display shows a message`() {
        assertEquals("INSERT COIN", machine.display())
    }

    @test
    fun `cannot be initiated with negative stock for a product`() {
        try {
            VendingMachine(mapOf(ProductImpl.Cola to 1, ProductImpl.Candy to -1), defaultBank)
            fail("Can not create vending machine with negative stock")
        } catch (e: IllegalArgumentException) {}
    }

    @test
    fun `cannot be initiated with negative count for a coin`() {
        try {
            VendingMachine(defaultStock, mapOf(Quarter to 1, Dime to -1))
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
        val d = Dime()
        val n = Nickel()
        machine.accept(d)
        machine.accept(n)
        assertEquals("$0.15", machine.display())
    }

    @test
    fun `only nickels, dimes, and quarters are valid inputs`() {
        val p = Penny()
        val s = Slug(1, 1, 1, "rough")
        machine.accept(Dime())
        machine.accept(Nickel())
        machine.accept(Quarter())
        machine.accept(p)
        machine.accept(s)
        assertEquals("$0.40", machine.display())
        assertEquals(listOf(p, s), machine.coinReturn())
    }

    @test
    fun `test matchCoins`() {
        val q1: Coin = Quarter()
        val n: Coin = Nickel()
        val s: Coin = Slug(2121, 195, 5000, "Plain")
        assertTrue(VendingMachine.matchCoins(q1, Quarter))
        assertFalse(VendingMachine.matchCoins(Quarter, n))
        assertFalse(VendingMachine.matchCoins(q1, s))
        assertTrue(VendingMachine.matchCoins(s, n))
    }

    @test
    fun `when enough money deposited and product selected display message and update accepted value`() {
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.buy(ProductImpl.Chips)
        assertEquals("THANK YOU", machine.display(), "Error on purchase")
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
    }

    @test
    fun `when more than enough money deposited and product selected display message set value to zero and make change`() {
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.accept(Nickel())
        machine.buy(ProductImpl.Chips)
        assertEquals("THANK YOU", machine.display(), "Error on purchase")
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
        assertTrue(compareCoinLists(listOf(Nickel()), machine.coinReturn()), "Extra money not returned in coin return")
    }

    @test
    fun `when insufficient money deposited and a product selected display price then display accepted amount`() {
        machine.buy(ProductImpl.Cola)
        assertEquals("PRICE: $1.00", machine.display(), "Error on purchase")
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
    }

    @test
    fun `set display with lifetime shows the message the right number of times then switches back to default`() {
        val testMessage = "Test"
        val testCount = 2

        machine.setDisplayWithLifetime(testMessage, testCount)
        for (i in 1 .. testCount) {
            assertEquals(testMessage, machine.display(), "Wrong test message")
        }
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
    }

    @test
    fun `when no money deposited coin return does nothing`() {
        machine.cancel()
        assertEquals(listOf(), machine.coinReturn(), "Incorrect coin return")
        assertEquals("INSERT COIN", machine.display(), "Wrong message displayed")
    }

    @test
    fun `coin return returns all money deposited`() {
        machine.accept(Dime())
        machine.cancel()
        assertTrue(compareCoinLists(listOf(Dime()), machine.coinReturn()), "Incorrect coin return")
        assertEquals("INSERT COIN", machine.display(), "Wrong message displayed")
    }

    @test
    fun `vending machine makes optimal return list when cancel is pushed`() {
        machine.accept(Dime())
        machine.accept(Dime())
        machine.accept(Nickel())
        assertTrue(compareCoinLists(listOf(Quarter()), machine.coinReturn()), "Incorrect coin return")
    }

    @test
    fun `can not sell an item that is out of stock`() {
        val machine = VendingMachine(mapOf(ProductImpl.Candy to 0), defaultBank)
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.accept(Dime())
        machine.accept(Nickel())
        machine.buy(ProductImpl.Candy)
        assertEquals("SOLD OUT", machine.display(), "Incorrect message when product out of stock")
        assertEquals("$0.65", machine.display(), "Wrong message displayed")
    }

    @test
    fun `can sell product until it is depleted but no more`() {
        val machine = VendingMachine(mapOf(ProductImpl.Chips to 1), defaultBank)
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.buy(ProductImpl.Chips)
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.buy(ProductImpl.Chips)
        assertEquals("SOLD OUT", machine.display(), "Incorrect message when product out of stock")
        assertEquals("$0.50", machine.display(), "Wrong message displayed")
    }

    fun compareCoinLists(l1: List<Coin>, l2: List<Coin>) =
            l1.zip(l2).all { (c1, c2) -> VendingMachine.matchCoins(c1, c2) }
}