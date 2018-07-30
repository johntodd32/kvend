package org.jtodd.kvend.vend

interface Product {
    val price: Int
}

enum class ProductImpl : Product {
    Cola{ override val price = 100 },
    Chips{ override val price = 50 },
    Candy{ override val price = 65 }
}