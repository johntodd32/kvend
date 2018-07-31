package org.jtodd.kvend.vend

class VendingMachine(stock: Map<Product, Int>, bank: Map<Coin, Int>) {

    init {
        if (stock.values.any { it < 0 }) {
            throw IllegalArgumentException("No product can have a negative stock")
        }
        if (bank.values.any { it < 0 }) {
            throw IllegalArgumentException("No coin in the bank can have a negative count")
        }
    }

    private val _stock = stock.toMutableMap()
    private val _bank = bank.toMutableMap()
    private val acceptedCoinList = mutableListOf<Coin>()
    private val _acceptedValue
        get() = acceptedCoinList.map { it -> it.monetaryValue }.sum()
    private val _coinReturn = mutableListOf<Coin>()
    private var _tempMessage = ""
    private var _lifetime = 0

    fun display(): String {
        if (_lifetime > 0) {
            --_lifetime
            return _tempMessage
        } else if (!canMakeChange()) {
            return EXACT_CHANGE_ONLY
        } else if (_acceptedValue == 0) {
            return INSERT_COIN
        } else {
            return formatValue(_acceptedValue)
        }
    }

    fun accept(coin: Coin) {
        if (matchCoins(coin, CoinImpl.Nickel) || matchCoins(coin, CoinImpl.Dime) || matchCoins(coin, CoinImpl.Quarter)) {
            acceptedCoinList.add(coin)
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
            setDisplayWithLifetime(formatPrice(product.price), 1)
        } else if (!_stock.containsKey(product) || _stock[product]!! < 1) {
            setDisplayWithLifetime(SOLD_OUT, 1)
        } else {
            acceptedCoinList.forEach { it -> _bank[it] = _bank[it]!! + 1 }
            acceptedCoinList.clear()
            _coinReturn.addAll(makeChange(_acceptedValue - product.price))
            _stock[product] = _stock[product]!! - 1
            setDisplayWithLifetime(THANK_YOU, 1)
        }
    }

    fun coinReturn() = _coinReturn.toList()

    fun cancel() {
        _coinReturn.addAll(acceptedCoinList)
        acceptedCoinList.clear()
    }

    fun canMakeChange() =
            listOf(CoinImpl.Quarter, CoinImpl.Dime, CoinImpl.Nickel).map { _bank.getOrDefault(it, 0) }.none { it < 1 }

    fun makeChange(amount: Int): List<Coin> {
        val returnList = mutableListOf<Coin>()
        var _amount = amount

        fun makeChangeForCoin(amount: Int, coin: Coin, list: MutableList<Coin>): Int {
            var __amount = amount
            while (__amount >= coin.monetaryValue) {
                list.add(coin)
                _bank[coin] = _bank[coin]!! - 1
                __amount -= coin.monetaryValue
            }
            return __amount
        }

        for (c in listOf(CoinImpl.Quarter, CoinImpl.Dime, CoinImpl.Nickel)) {
            _amount = makeChangeForCoin(_amount, c, returnList)
        }

        return returnList.toList()
    }

    companion object {
        val INSERT_COIN = "INSERT COIN"
        val EXACT_CHANGE_ONLY = "EXACT CHANGE ONLY"
        val SOLD_OUT = "SOLD OUT"
        val THANK_YOU = "THANK YOU"
        val PRICE = "PRICE"

        fun formatPrice(price: Int): String = "$PRICE: ${formatValue(price)}"

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