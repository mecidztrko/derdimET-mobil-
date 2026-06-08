package com.derdimet.mobil.model

object DemoAccounts {
    fun email(role: UserRole): String = when (role) {
        UserRole.MEAT_BUYER -> "buyer1@derdimet.local"
        UserRole.ANIMAL_SELLER -> "seller1@derdimet.local"
        UserRole.SLAUGHTERHOUSE -> "slaughterhouse1@derdimet.local"
        UserRole.ADMIN -> "admin@derdimet.local"
    }

    const val PASSWORD = "123456"
}
