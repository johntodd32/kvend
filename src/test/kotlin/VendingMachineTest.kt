package org.jtodd.kvend.vend

import kotlin.test.assertEquals
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
}