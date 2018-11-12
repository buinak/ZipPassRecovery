package com.buinak

import java.util.ArrayList

fun main(args: Array<String>) {
    val path = InputHandler.getFilePath()
    val depth = InputHandler.getDepth()

    val list = initialiseCharacterList(InputHandler.getOptions())
    var charactersString = "Characters for usage: "
    list.forEach { charactersString+= "$it, " }
    println(charactersString)

    Bruteforcer(path, list, depth, true).start()
}

private fun initialiseCharacterList(options: String): List<Char> {
    val list = ArrayList<Char>()
    //adding lower case chars
    if (options.contains("-l")) {
        for (i in 97..122) list.add(i)
    }
    //digits
    if (options.contains("-n")) {
        for (i in 48..57) list.add(i)
    }
    //capital letters
    if (options.contains("-cl")) {
        for (i in 65..90) list.add(i)
    }
    //symbols
    if (options.contains("-s")) {
        for (i in 33..47) list.add(i)
        for (i in 91..96) list.add(i)
        for (i in 123..126) list.add(i)
    }
    return list
}

private fun ArrayList<Char>.add(char: Int){
    this.add(char.toChar())
}
