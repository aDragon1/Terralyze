package self.adragon.terralyze

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform