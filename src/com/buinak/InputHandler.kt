package com.buinak

import java.io.File
import java.util.ArrayList

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

    fun getCharacterList(): List<Char> {
        println("Enter input options in one line. Default is -n -l Input options are:")
        println("-n to include digits")
        println("-l to include letters")
        println("-cl to include capital letters")
        println("-s to include special symbols")
        println("-all to include all")
        return initialiseCharacterList(readLine()!!)
    }

    fun getPrintAllTries(): Boolean{
        println("Would you like all the tried passwords printed? Y/N (default: N)")
        val appropriate = setOf("Y", "y", "N", "n", "")
        var input = readLine()
        while (input !in appropriate){
            println("Invalid input. Would you like all the tried passwords printed? Y/N (default: N)")
            input = readLine()
        }
        return when (input){
            in setOf("Y", "y") -> true
            else -> false
        }
    }

    fun getDistrubuteThreads(): Boolean{
        println("Would you like all the depth searches concurrent or consequent? Y/N (default: N for consequent)")
        val appropriate = setOf("Y", "y", "N", "n", "")
        var input = readLine()
        while (input !in appropriate){
            println("Would you like all the depth searches concurrent or consequent? Y/N (default: N for consequent)")
            input = readLine()
        }
        return when (input){
            in setOf("Y", "y") -> true
            else -> false
        }
    }

    private fun initialiseCharacterList(options: String): List<Char> {
        val newOptions = when (options){
            "" -> "-n -l"
            "-all" -> "-n-l-cl-s"
            else -> options
        }
        val list = ArrayList<Char>()
        //adding lower case chars
        if (newOptions.contains("-l")) {
            for (i in 97..122) list.add(i)
        }
        //digits
        if (newOptions.contains("-n")) {
            for (i in 48..57) list.add(i)
        }
        //capital letters
        if (newOptions.contains("-cl")) {
            for (i in 65..90) list.add(i)
        }
        //symbols
        if (newOptions.contains("-s")) {
            for (i in 33..47) list.add(i)
            for (i in 91..96) list.add(i)
            for (i in 123..126) list.add(i)
        }
        return list
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

fun ArrayList<Char>.add(char: Int) {
    this.add(char.toChar())
}