package terralyze.playerfile.model

enum class FileType(val type: Int) {
    None(0),
    Map(1),
    World(2),
    Player(3)
}