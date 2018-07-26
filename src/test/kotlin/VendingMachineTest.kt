package org.jtodd.kvend.vend

import kotlin.test.assertEquals
import org.junit.Test as test

class VendingMachineTest() {
    @test fun `when created display shows a message`() {
        val machine: VendingMachine = VendingMachine()
        assertEquals("INSERT COIN", machine.display())
    }
}