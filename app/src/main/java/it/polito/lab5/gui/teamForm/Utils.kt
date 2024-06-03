package it.polito.lab5.gui.teamForm

fun getMonogramText(name: String): Pair<String,String> {
    val words = name.split(" ")

    return when(words.size) {
        1 -> { words[0] to " "}
        2 -> { words[0] to words[1].ifEmpty { " " } }
        else -> { words[0] to words[1] }
    }
}