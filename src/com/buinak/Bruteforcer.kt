package com.buinak

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicInteger

class Bruteforcer(
    val path: String,
    private val characters: List<Char>,
    val depth: Int = 1,
    private val countIterations: Boolean = false,
    private val printTried: Boolean = false
) {

    private var finished = false
    private var seconds = 0
    private var iteration: AtomicInteger = AtomicInteger(0)

    fun start() {

        println("Creating a list of possible passwords for depth = $depth..")
        val passwords = getAllPossiblePasswords(depth)
        println("Maximum amount of iterations = ${passwords.size}")
        val secondsMax = passwords.size / 5000
        println("------------")
        println("Assuming the speed of 5000 iterations per second, ")
        println("The worst case scenario will take $secondsMax seconds, or ${secondsMax.toFloat() / 3600} hours.")
        println("------------")

        val coreCount = (Runtime.getRuntime().availableProcessors()) * 2
        println("Allocating $coreCount threads..")
        for (coreIndex in 1..coreCount) {
            subpartitionAndLaunchAsyncSearch(passwords, coreCount, coreIndex)
        }

        //print the number of iterations on the main thread
        while (true) {
            if (countIterations) {
                val average = iteration.get() / ++seconds
                var percentage = (iteration.toFloat() / (passwords.size).toFloat()).toDouble() * 100
                if (percentage < 0.01) percentage = 0.01
                println(
                    "$iteration/${passwords.size} (${percentage.toString().substring(
                        0,
                        4
                    )})% passwords tried after $seconds seconds. On average $average per second."
                )
            }
            Thread.sleep(1000)
            if (iteration.get() > passwords.size) {
                println()
                println("Could not find a password, quitting..")
                return
            }
            if (finished) break
        }
    }

    private fun subpartitionAndLaunchAsyncSearch(
        passwords: ArrayList<String>,
        cpuCount: Int,
        i: Int
    ) {
        val subSize = passwords.size / cpuCount
        val subRange = if (i != cpuCount) {
            (subSize * (i - 1))..(subSize * i)
        } else {
            (subSize * (i - 1)) until passwords.size
        }
        when (printTried) {
            true -> startSearchInRangeWithPrinting(subRange, passwords)
            false -> startSearchInRange(subRange, passwords)
        }
    }

    private fun startSearchInRange(
        subRange: IntRange,
        passwords: ArrayList<String>
    ) {
        Completable.fromAction {
            val verifier = Verifier(path)
            for (index in subRange) {
                iteration.getAndIncrement()
                if (verifier.verify(passwords[index])) {
                    finished = true
                    println("Password to the zip archive at $path === ${passwords[index]}")
                    Runtime.getRuntime().exit(0)
                }
            }
        }.subscribeOn(Schedulers.computation())
            .subscribe {}
    }

    private fun startSearchInRangeWithPrinting(
        subRange: IntRange,
        passwords: ArrayList<String>
    ) {
        Completable.fromAction {
            val verifier = Verifier(path)
            for (index in subRange) {
                var ite = iteration.getAndIncrement()
                if (ite % 20 == 0) println()
                print(" ${passwords[index]} ")
                if (verifier.verify(passwords[index])) {
                    finished = true
                    println("Password to the zip archive at $path === ${passwords[index]}")
                    Runtime.getRuntime().exit(0)
                }
            }
        }.subscribeOn(Schedulers.computation())
            .subscribe {}
    }

    private fun getAllPossiblePasswords(depth: Int): ArrayList<String> {
        if (depth < 1) return ArrayList()

        var resultList = ArrayList<String>()
        if (depth == 1) {
            characters.forEach { resultList.add(it) }
            println("Created a list for $depth depth, size = ${resultList.size}")
            return resultList
        }

        resultList = getAllPossiblePasswords(depth - 1)
        resultList.filter { it.length == depth - 1 }.forEach { password ->
            characters.forEach { character ->
                resultList.add(password + character.toString())
            }
        }

        println("Created a list for $depth depth, size = ${resultList.size}")
        return resultList
    }
}

private fun ArrayList<String>.add(char: Char) {
    this.add(char.toString())
}
