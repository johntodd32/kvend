package org.jtodd.kvend.vend

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test as test

class VendingMachineTest {

    @test
    fun `when created display shows a message`() {
        val machine = VendingMachine()
        assertEquals("INSERT COIN", machine.display())
    }

    @test
    fun `can accept coins and update the display`() {
        val machine = VendingMachine()
        val d = Dime()
        val n = Nickel()
        machine.accept(d)
        machine.accept(n)
        assertEquals("$0.15", machine.display())
    }

    @test
    fun `only nickels, dimes, and quarters are valid inputs`() {
        val machine = VendingMachine()
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
}