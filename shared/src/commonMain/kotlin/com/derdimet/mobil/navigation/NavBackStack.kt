package com.derdimet.mobil.navigation

/** Wasm uyumlu hafif geri yığını — Navigation Compose geçişi için temel. */
class NavBackStack<T>(initial: T) {
    private val stack = mutableListOf(initial)

    val current: T get() = stack.last()

    fun navigate(route: T) {
        stack.add(route)
    }

    fun pop(): Boolean {
        if (stack.size <= 1) return false
        stack.removeAt(stack.lastIndex)
        return true
    }

    fun replace(route: T) {
        if (stack.isEmpty()) {
            stack.add(route)
        } else {
            stack[stack.lastIndex] = route
        }
    }

    fun reset(route: T) {
        stack.clear()
        stack.add(route)
    }
}
