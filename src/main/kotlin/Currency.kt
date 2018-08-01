package org.jtodd.kvend.vend

/**
 * Specifications from the US Mint:
 * https://www.usmint.gov/learn/coin-and-medal-programs/coin-specifications
 */
interface Coin {
    val diameter: Int
    val mass: Int
    val monetaryValue: Int
}

enum class Denominations : Coin {
    Dollar {
        override val diameter = 2649
        override val mass = 8100
        override val monetaryValue = 100
    },
    HalfDollar {
        override val diameter = 3061
        override val mass = 11340
        override val monetaryValue = 50
    },
    Quarter {
        override val diameter = 2426
        override val mass = 5670
        override val monetaryValue = 25
    },
    Dime {
        override val diameter = 1791
        override val mass = 2268
        override val monetaryValue = 10
    },
    Nickel {
        override val diameter = 2121
        override val mass = 5000
        override val monetaryValue = 5
    },
    Penny {
        override val diameter = 1905
        override val mass = 2500
        override val monetaryValue = 1
    }
}

class Slug(override val diameter: Int, override val mass: Int) : Coin {
    override val monetaryValue = 0
}