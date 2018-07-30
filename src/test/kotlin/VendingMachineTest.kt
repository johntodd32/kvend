package org.jtodd.kvend.vend

import org.junit.Before
import org.junit.Test as test

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VendingMachineTest {

    lateinit var machine: VendingMachine

    @Before
    fun setUp() {
        machine = VendingMachine()
    }

    @test
    fun `when created display shows a message`() {
        assertEquals("INSERT COIN", machine.display())
    }

    @test
    fun `display amounts correctly`() {
        assertEquals("$0.15", machine.formatValue(15), "Wrong format")
        assertEquals("$1.00", machine.formatValue(100), "Wrong format")
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
        machine.buy(Chips())
        assertEquals("THANK YOU", machine.display(), "Error on purchase")
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
    }

    @test
    fun `when more than enough money deposited and product selected display message set value to zero and make change`() {
        machine.accept(Quarter())
        machine.accept(Quarter())
        machine.accept(Nickel())
        machine.buy(Chips())
        assertEquals("THANK YOU", machine.display(), "Error on purchase")
        assertEquals("INSERT COIN", machine.display(), "Error on display reset")
        assertTrue(compareCoinLists(listOf(Nickel()), machine.coinReturn()), "Extra money not returned in coin return")
    }

    @test
    fun `when insufficient money deposited and a product selected display price then display accepted amount`() {
        machine.buy(Cola())
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

    fun compareCoinLists(l1: List<Coin>, l2: List<Coin>) =
            l1.zip(l2).all { (c1, c2) -> VendingMachine.matchCoins(c1, c2) }
}