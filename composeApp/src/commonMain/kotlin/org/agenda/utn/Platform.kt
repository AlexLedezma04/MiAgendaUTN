package org.agenda.utn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform