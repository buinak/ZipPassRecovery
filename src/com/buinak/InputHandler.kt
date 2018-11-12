package com.buinak

import java.io.File

object InputHandler {
    fun getFilePath(): String{
        println("Enter file path, please.")
        var path = readLine()!!
        while (!File(path).exists()){
            println("Wrong file path, please enter it again.")
            path = readLine()!!
        }
        return path
    }

    fun getOptions(): String {
        println("Enter input options in one line. Input options are:")
        println("-n to include digits")
        println("-l to include letters")
        println("-cl to include capital letters")
        println("-s to include special symbols")
        return readLine()!!
    }

    fun getDepth(): Int {
        println("Specify search depth. For depth = 1, only passwords that are one character long will be tried.")
        var input = readLine()!!
        while (input.filter { it.isDigit() }.length != input.length){
            println("Incorrect number, please specify a number.")
            input = readLine()!!
        }
        var depthNumber = input.toInt()
        if (depthNumber > 10) println("Maximum depth is 10").also { return 10 }
        return depthNumber
    }

}