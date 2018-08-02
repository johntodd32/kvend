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
                           |I[PNDQHL(S%.4f,%.4f)]: Insert (penny, nickel, dollar, quarter, half-dollar, dollar, slug(diameter, mass)
                           |S[123]: Select (1 = chips, 2 = candy, 3 = soda
                           |C: Cancel purchase
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

        assertEquals(expected = expectedSplash, actual = baos.toString(), message = "Wrong splash generated")
    }

    @test
    fun `prints warning when an unrecognized command is entered`() {
        val input = "Y${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(actual = baos.toString().contains("Unrecognized command"), message = "User should be warned when an unrecognized command is entered")
    }

    @test
    fun `gets machine display`() {
        val input = "D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(actual = baos.toString().contains(VendingMachine.INSERT_COIN), message = "Machine should return display when requested")
    }

    @test
    fun `can buy product`() {
        val input = "S1${ls}D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(actual = baos.toString().contains(VendingMachine.formatPrice(Product.Chips.price)), message = "Machine should respond to purchases")
    }

    @test
    fun `can insert coins`() {
        val input = "IQ${ls}ID${ls}D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(actual = baos.toString().contains("$0.35"), message = "Machine should accept coins")
    }

    @test
    fun `can cancel a purchase`() {
        val input = "IQ${ls}D${ls}C${ls}D${ls}X".byteInputStream()

        Driver(input, output).run()

        assertTrue(actual = baos.toString().contains("$0.25"), message = "Machine should accept coins")
        assertTrue(actual = baos.toString().contains(VendingMachine.INSERT_COIN), message = "Cancel should reset display")
    }
}