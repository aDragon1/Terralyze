package self.adragon.terralyze.ui

actual fun resourceFileName(url: String) = url.replace("%", "%25")