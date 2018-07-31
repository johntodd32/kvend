package org.jtodd.kvend.vend

import java.io.ByteArrayOutputStream
import java.io.PrintStream

import kotlin.test.assertEquals
import kotlin.test.assertTrue

import org.junit.Before as before
import org.junit.Test as test

class DriverTest {

    val ls = System.getProperty("line.separator")
    val expectedSplash = """D: Check display
                           |I[PNDQHD(S%.4f,%.4f)]: Insert (penny, nickel, dollar, quarter, half-dollar, dollar, slug(diameter, mass)
                           |S[123]: Select (1 = chips, 2 = candy, 3 = soda
                           |C: Cancel purchase
                           |R: Check for change
                           |P: Check for product
                           |X: Exit
                           |
                           |Choice: """.trimMargin("|")
    lateinit var baos: ByteArrayOutputStream
    lateinit var output: PrintStream

    @before
    fun setUp() {
        baos = ByteArrayOutputStream()
        output = PrintStream(baos)
    }

    @test
    fun `prints options upon startup`() {
        val input = "X".byteInputStream()

        Driver(input, output).run()

        assertEquals(expectedSplash, baos.toString(), "Wrong splash generated")
    }

    @test
    fun `prints warning when an unrecognized command is entered`() {
        val input = "Y${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(baos.toString().contains("Unrecognized command"), "User should be warned when an unrecognized command is entered")
    }

    @test
    fun `gets machine display`() {
        val input = "D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(baos.toString().contains(VendingMachine.INSERT_COIN), "Machine should return display when requested")
    }

    @test
    fun `can buy product`() {
        val input = "S1${ls}D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(baos.toString().contains(VendingMachine.formatPrice(Product.Chips.price)), "Machine should respond to purchases")
    }
}