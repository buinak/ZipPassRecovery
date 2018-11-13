package com.buinak

import java.util.ArrayList

fun main(args: Array<String>) {
    val path = InputHandler.getFilePath()
    val depth = InputHandler.getDepth()
    val list = InputHandler.getCharacterList()
    val printAll = InputHandler.getPrintAllTries()

    var charactersString = "Characters for usage: "
    list.forEach { charactersString += "$it, " }
    println(charactersString.trimEnd(','))
    val amount = Math.pow(list.size.toDouble(), depth.toDouble()).toLong()

    //as opposed to the "normal" implementation, this one
    //does not generate a list of passwords beforehand
    BruteforcerListless(path, depth, list, true, printAll).start()
}
