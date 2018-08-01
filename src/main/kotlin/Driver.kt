package org.jtodd.kvend.vend

import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

class Driver(input: InputStream, private val output: PrintStream) {

    private val scanner = Scanner(input)
    private val stock: Map<Product, Int> = Product.values().associate { it to 10 }
    private val bank: Map<Coin, Int> = VendingMachine.acceptableCoins.associate { it to 10 }
    private val machine = VendingMachine(stock, bank)

    fun run() {
        displayOptions(output)
        var pair = getOption(scanner)
        var command = pair.component1()
        var argument = pair.component2()
        while (command != 'X') {
            when (command) {
                'D' -> output.println(machine.display())
                'I' -> {
                    when {
                        argument == "P" -> machine.accept(Denominations.Penny)
                        argument == "N" -> machine.accept(Denominations.Nickel)
                        argument == "D" -> machine.accept(Denominations.Dime)
                        argument == "Q" -> machine.accept(Denominations.Quarter)
                        argument == "H" -> machine.accept(Denominations.HalfDollar)
                        argument == "L" -> machine.accept(Denominations.Dollar)
                        argument.startsWith("S") -> {
                            val (diameter, mass) = argument.substring(1).split(",")
                            machine.accept(Slug((diameter.toFloat() * 10000).toInt(), (mass.toFloat() * 10000).toInt()))
                        }
                        else -> output.println("Unrecognized coin")
                    }
                }
                'S' -> {
                    when(argument) {
                        "1" -> machine.buy(Product.Chips)
                        "2" -> machine.buy(Product.Candy)
                        "3" -> machine.buy(Product.Cola)
                        else -> output.println("Unrecognized product")
                    }
                }
                'C' -> machine.cancel()
                else -> {
                    output.println("Unrecognized command")
                }
            }
            displayOptions(output)
            pair = getOption(scanner)
            command = pair.component1()
            argument = pair.component2()
        }
    }

    private fun displayOptions(output: PrintStream) {
        output.println("D: Check display")
        output.println("I[PNDQHL(S%.4f,%.4f)]: Insert (penny, nickel, dollar, quarter, half-dollar, dollar, slug(diameter, mass)")
        output.println("S[123]: Select (1 = chips, 2 = candy, 3 = soda")
        output.println("C: Cancel purchase")
        output.println("X: Exit")
        output.println()
        output.print("Choice: ")
    }

    private fun getOption(scanner: Scanner): Pair<Char, String> {
        val choice: String = scanner.nextLine().toUpperCase()
        val option: Char = choice[0]
        val arg: String = choice.substring(1)
        return option to arg
    }
}

fun main(args: Array<String>) {
    Driver(System.`in`, System.out).run()
}