package com.buinak

import java.util.ArrayList

fun main(args: Array<String>) {
    val path = InputHandler.getFilePath()
    val depth = InputHandler.getDepth()
    val list = InputHandler.getCharacterList()
    val printAll = InputHandler.getPrintAllTries()

    var charactersString = "Characters for usage: "
    list.forEach { charactersString += "$it, " }
    println(charactersString)
    val amount = Math.pow(list.size.toDouble(), depth.toDouble()).toLong()
//    if (amount >= 100000000) {
        BruteforcerListless(path, depth, list, true, printAll).start()
//    } else {
//        Bruteforcer(path, list, depth, true, printAll).start()
//    }
}
