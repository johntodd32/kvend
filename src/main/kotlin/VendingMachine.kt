package org.jtodd.kvend.vend

class VendingMachine(stock: Map<Product, Int>) {

    init {
        if (stock.values.any { it < 0 }) {
            throw IllegalArgumentException("No product can have a negative stock")
        }
    }

    private val _stock = stock.toMutableMap()
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
        } else if (!_stock.containsKey(product) || _stock[product]!! < 1) {
            setDisplayWithLifetime("SOLD OUT", 1)
        } else {
            _coinReturn.addAll(makeChange(_acceptedValue - product.price))
            _acceptedValue = 0
            _stock[product] = _stock[product]!! - 1
            setDisplayWithLifetime("THANK YOU", 1)
        }
    }

    fun coinReturn() = _coinReturn.toList()

    fun cancel() {
        _coinReturn.addAll(makeChange(_acceptedValue))
        _acceptedValue = 0
    }

    fun makeChange(amount: Int): List<Coin> {
        val returnList = mutableListOf<Coin>()
        var _amount = amount

        fun makeChangeForCoin(amount: Int, coin: Coin, list: MutableList<Coin>): Int {
            var __amount = amount
            while (__amount >= coin.monetaryValue) {
                list.add(Coin.copy(coin))
                __amount -= coin.monetaryValue
            }
            return __amount
        }

        for (c in listOf(Quarter, Dime, Nickel)) {
            _amount = makeChangeForCoin(_amount, c, returnList)
        }

        return returnList.toList()
    }

    companion object {
        fun formatValue(acceptedValue: Int): String {
            val dollars = acceptedValue / 100
            val cents = acceptedValue % 100
            return "\$$dollars.${"%02d".format(cents)}"
        }

        fun matchCoins(c1: Coin, c2: Coin): Boolean {
            return c1.diameter  == c2.diameter &&
                   c1.thickness == c2.thickness &&
                   c1.mass      == c2.mass &&
                   c1.edge      == c2.edge
        }
    }
}