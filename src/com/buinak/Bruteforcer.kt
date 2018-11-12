package com.buinak

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class Bruteforcer(
    val path: String,
    private val characters: List<Char>,
    val depth: Int = 1,
    private val countIterations: Boolean = false
) {

    private var finished = false
    private var seconds = 0
    private var iteration: AtomicInteger = AtomicInteger(0)

    fun start() {

        println("Creating a list of possible passwords for $depth depth..")
        val passwords = getAllPossiblePasswords(depth)
        println("Maximum amount of iterations = ${passwords.size}")

        val cpuCount = Runtime.getRuntime().availableProcessors()
        println("Allocating $cpuCount threads..")
        for (i in 1..cpuCount) {
            val subSize = passwords.size / cpuCount
            val subRange = if (i != cpuCount) {
                (subSize * (i - 1))..(subSize * i)
            } else {
                (subSize * (i - 1)) until passwords.size
            }

            Completable.fromAction {
                for (index in subRange) {
                    iteration.getAndIncrement()
                    if (Verifier.verify(path, passwords[index])) {
                        finished = true
                        println("Password to the zip archive at $path === ${passwords[index]}")
                        Runtime.getRuntime().exit(0)
                    }
                }
            }   .subscribeOn(Schedulers.computation())
                .subscribe {}
        }

        //print the number of iterations on the main thread
        while (true){
            val actSec = ++seconds
            val average = iteration.get() / actSec
            println("$iteration passwords tried after $actSec seconds. On average $average per second.")
            Thread.sleep(1000)
            if (finished) break
        }
        readLine()
    }

    private fun countIterations() {
        Observable.interval(1, TimeUnit.SECONDS)
            .filter { !finished }
            .subscribe { second ->
                val actSec = second + 1
                val average = iteration.get() / actSec
                println("Passwords tried after $actSec seconds = $iteration. On average, that is $average per second.")
            }
    }

    private fun getAllPossiblePasswords(depth: Int): ArrayList<String> {
        if (depth < 1) return ArrayList()

        var resultList = ArrayList<String>()
        if (depth == 1) {
            characters.forEach { resultList.add(it) }
            println("Created a list for $depth depth")
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
