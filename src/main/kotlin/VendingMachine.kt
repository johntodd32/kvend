package org.jtodd.kvend.vend

class VendingMachine {

    private var acceptedValue = 0

    fun display(): String = if (acceptedValue == 0) "INSERT COIN" else formatValue(acceptedValue)

    fun formatValue(acceptedValue: Int): String {
        val dollars = acceptedValue / 100
        val cents = acceptedValue % 100
        return "\$$dollars.$cents"
    }

    fun accept(coin: Coin) {
        acceptedValue += coin.monetaryValue
    }
}