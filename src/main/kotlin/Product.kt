package org.jtodd.kvend.vend

interface Product {
    val price: Int
}

class Cola : Product {
    override val price = 100
}

class Chips : Product {
    override val price = 50
}

class Candy : Product {
    override val price = 65
}