package org.jtodd.kvend.vend

class VendingMachine {

    private var _acceptedValue = 0
    private val _coinReturn = mutableListOf<Coin>()
    private var _tempMessage = ""
    private var _lifetime = 0

    fun display(): String {
        if (_lifetime > 0) {
            --_lifetime
            return _tempMessage
        } else if (_acceptedValue == 0) {
            return "INSERT COIN"
        } else {
            return formatValue(_acceptedValue)
        }
    }

    fun formatValue(acceptedValue: Int): String {
        val dollars = acceptedValue / 100
        val cents = acceptedValue % 100
        return "\$$dollars.${"%02d".format(cents)}"
    }

    fun accept(coin: Coin) {
        if (matchCoins(coin, Nickel) || matchCoins(coin, Dime) || matchCoins(coin, Quarter)) {
            _acceptedValue += coin.monetaryValue
        } else {
            _coinReturn.add(coin)
        }
    }

    fun setDisplayWithLifetime(message: String, lifetime: Int) {
        _tempMessage = message
        _lifetime = lifetime
    }

    fun buy(product: Product) {
        if (product.price > _acceptedValue) {
            setDisplayWithLifetime("PRICE: ${formatValue(product.price)}", 1)
        } else {
            _acceptedValue = 0
            setDisplayWithLifetime("THANK YOU", 1)
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