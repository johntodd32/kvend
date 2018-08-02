package org.jtodd.kvend.vend

class VendingMachine(stock: Map<Product, Int>, bank: Map<Coin, Int>) {

    private val stock = stock.toMutableMap()
    private val bank: MutableMap<Coin, Int> = acceptableCoins.associate { it to 0 }.toMutableMap()
    private val acceptedCoinList = mutableListOf<Coin>()
    private val acceptedValue
        get() = acceptedCoinList.map { it -> it.monetaryValue }.sum()
    private val coinReturn = mutableListOf<Coin>()
    private var tempMessage = ""
    private var lifetime = 0

    init {
        if (stock.values.any { it < 0 }) {
            throw IllegalArgumentException("No product can have a negative stock")
        }
        if (bank.values.any { it < 0 }) {
            throw IllegalArgumentException("No coin in the bank can have a negative count")
        }
        bank.filter { (k, v) -> k in acceptableCoins }.forEach { (k, v) -> this.bank[k] = v }
    }

    fun display(): String {
        if (lifetime > 0) {
            --lifetime
            return tempMessage
        } else if (!canMakeChange()) {
            return EXACT_CHANGE_ONLY
        } else if (acceptedValue == 0) {
            return INSERT_COIN
        } else {
            return formatValue(acceptedValue)
        }
    }

    fun accept(coin: Coin) {
        if (acceptableCoins.any { matchCoins(coin, it) } ) {
            acceptedCoinList.add(coin)
        } else {
            coinReturn.add(coin)
        }
    }

    fun setDisplayWithLifetime(message: String, lifetime: Int) {
        tempMessage = message
        this.lifetime = lifetime
    }

    fun buy(product: Product) {
        if (product.price > acceptedValue) {
            setDisplayWithLifetime(message = formatPrice(product.price), lifetime = 1)
        } else if (!stock.containsKey(product) || stock[product]!! < 1) {
            setDisplayWithLifetime(message = SOLD_OUT, lifetime = 1)
        } else {
            acceptedCoinList.forEach { it -> bank[it] = bank[it]!! + 1 }
            acceptedCoinList.clear()
            coinReturn.addAll(makeChange(amount = acceptedValue - product.price))
            stock[product] = stock[product]!! - 1
            setDisplayWithLifetime(message = THANK_YOU, lifetime = 1)
        }
    }

    fun coinReturn() = coinReturn.toList()

    fun cancel() {
        coinReturn.addAll(acceptedCoinList)
        acceptedCoinList.clear()
    }

    fun canMakeChange() =
            bank.values.none { it < 1 }

    fun makeChange(amount: Int): List<Coin> {
        val returnList = mutableListOf<Coin>()
        var _amount = amount

        fun makeChangeForCoin(amount: Int, coin: Coin, list: MutableList<Coin>): Int {
            var __amount = amount
            while (__amount >= coin.monetaryValue) {
                list.add(coin)
                bank[coin] = bank[coin]!! - 1
                __amount -= coin.monetaryValue
            }
            return __amount
        }

        for (c in acceptableCoins) {
            _amount = makeChangeForCoin(_amount, c, returnList)
        }

        return returnList.toList()
    }

    companion object {
        val acceptableCoins = listOf(Denominations.Quarter, Denominations.Dime, Denominations.Nickel)
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
                   c1.mass      == c2.mass
        }
    }
}