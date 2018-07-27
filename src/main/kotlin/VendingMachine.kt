package org.jtodd.kvend.vend

class VendingMachine {

    private var _acceptedValue = 0
    private val _coinReturn = mutableListOf<Coin>()
    private val refNickel = Nickel()
    private val refDime = Dime()
    private val refQuarter = Quarter()

    fun display(): String = if (_acceptedValue == 0) "INSERT COIN" else formatValue(_acceptedValue)

    fun formatValue(acceptedValue: Int): String {
        val dollars = acceptedValue / 100
        val cents = acceptedValue % 100
        return "\$$dollars.$cents"
    }

    fun accept(coin: Coin) {
        if (matchCoins(coin, refNickel) || matchCoins(coin, refDime) || matchCoins(coin, refQuarter)) {
            _acceptedValue += coin.monetaryValue
        } else {
            _coinReturn.add(coin)
        }
    }

    fun coinReturn() = _coinReturn.toList()

    companion object {
        fun matchCoins(c1: Coin, c2: Coin): Boolean {
            return c1.diameter  == c2.diameter &&
                   c1.thickness == c2.thickness &&
                   c1.mass      == c2.mass &&
                   c1.edge      == c2.edge
        }
    }
}