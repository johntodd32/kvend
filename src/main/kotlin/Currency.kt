package org.jtodd.kvend.vend

/**
 * Specifications from the US Mint:
 * https://www.usmint.gov/learn/coin-and-medal-programs/coin-specifications
 */
interface Coin {
    val diameter: Int
    val thickness: Int
    val mass: Int
    val edge: String
    val monetaryValue: Int
}

class Dollar : Coin {
    override val diameter = Dollar.diameter
    override val thickness = Dollar.thickness
    override val mass = Dollar.mass
    override val edge = Dollar.edge
    override val monetaryValue = 100
    companion object : Coin {
        override val diameter = 2649
        override val thickness = 200
        override val mass = 8100
        override val edge = "Edge-Lettering"
        override val monetaryValue = 100
    }
}

class HalfDollar : Coin {
    override val diameter = HalfDollar.diameter
    override val thickness = HalfDollar.thickness
    override val mass = HalfDollar.mass
    override val edge = HalfDollar.edge
    override val monetaryValue = HalfDollar.monetaryValue
    companion object : Coin {
        override val diameter = 3061
        override val thickness = 215
        override val mass = 11340
        override val edge = "Reeded"
        override val monetaryValue = 50
    }
}

class Quarter : Coin {
    override val diameter = Quarter.diameter
    override val thickness = Quarter.thickness
    override val mass = Quarter.mass
    override val edge = Quarter.edge
    override val monetaryValue = Quarter.monetaryValue
    companion object : Coin {
        override val diameter = 2426
        override val thickness = 175
        override val mass = 5670
        override val edge = "Reeded"
        override val monetaryValue = 25
    }
}

class Dime : Coin {
    override val diameter = Dime.diameter
    override val thickness = Dime.thickness
    override val mass = Dime.mass
    override val edge = Dime.edge
    override val monetaryValue = Dime.monetaryValue
    companion object : Coin {
        override val diameter = 1791
        override val thickness = 135
        override val mass = 2268
        override val edge = "Reeded"
        override val monetaryValue = 10
    }
}

class Nickel : Coin {
    override val diameter = Nickel.diameter
    override val thickness = Nickel.thickness
    override val mass = Nickel.mass
    override val edge = Nickel.edge
    override val monetaryValue = Nickel.monetaryValue
    companion object : Coin {
        override val diameter = 2121
        override val thickness = 195
        override val mass = 5000
        override val edge = "Plain"
        override val monetaryValue = 5
    }
}

class Penny : Coin {
    override val diameter = Penny.diameter
    override val thickness = Penny.thickness
    override val mass = Penny.mass
    override val edge = Penny.edge
    override val monetaryValue = Penny.monetaryValue
    companion object : Coin {
        override val diameter = 1905
        override val thickness = 152
        override val mass = 2500
        override val edge = "Plain"
        override val monetaryValue = 1
    }
}

class Slug(diameter: Int, thickness: Int, mass: Int, edge: String) : Coin {
    override val diameter = diameter
    override val thickness = thickness
    override val mass = mass
    override val edge = edge
    override val monetaryValue = 0
}