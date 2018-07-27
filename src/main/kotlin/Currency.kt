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
    override val diameter = 2649
    override val thickness = 200
    override val mass = 8100
    override val edge = "Edge-Lettering"
    override val monetaryValue = 100
    companion object
}

class HalfDollar : Coin {
    override val diameter = 3061
    override val thickness = 215
    override val mass = 11340
    override val edge = "Reeded"
    override val monetaryValue = 50
    companion object
}

class Quarter : Coin {
    override val diameter = 2426
    override val thickness = 175
    override val mass = 5670
    override val edge = "Reeded"
    override val monetaryValue = 25
    companion object
}

class Dime : Coin {
    override val diameter = 1791
    override val thickness = 135
    override val mass = 2268
    override val edge = "Reeded"
    override val monetaryValue = 10
    companion object
}

class Nickel : Coin {
    override val diameter = 2121
    override val thickness = 195
    override val mass = 5000
    override val edge = "Plain"
    override val monetaryValue = 5
    companion object
}

class Penny : Coin {
    override val diameter = 1905
    override val thickness = 152
    override val mass = 2500
    override val edge = "Plain"
    override val monetaryValue = 1
    companion object
}

class Slug(diameter: Int, thickness: Int, mass: Int, edge: String) : Coin {
    override val diameter = diameter
    override val thickness = thickness
    override val mass = mass
    override val edge = edge
    override val monetaryValue = 0
}