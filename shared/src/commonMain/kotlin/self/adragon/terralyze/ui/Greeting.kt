package self.adragon.terralyze.ui

import self.adragon.terralyze.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}