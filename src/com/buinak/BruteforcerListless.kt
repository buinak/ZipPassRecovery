package com.buinak

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

class BruteforcerListless(
    val path: String,
    val depth: Int = 1,
    val allowedCharacters: List<Char>,
    private val countIterations: Boolean = false,
    private val printTried: Boolean = false
) {
    private var finished = false
    private var seconds = 0
    private var iteration: AtomicInteger = AtomicInteger(0)
    private var total: BigInteger = BigInteger.valueOf(0)


    fun start() {
        var iterations: BigInteger = BigInteger.valueOf(allowedCharacters.size.toLong())
        for (i in 1..depth) {
            println("Amount of iterations for $depth depth = $iterations")
            total += BigInteger.valueOf(iterations.toLong())
            iterations *= BigInteger.valueOf(allowedCharacters.size.toLong())
        }
        if (depth >= 1) bruteforceOne()
        if (depth >= 2) bruteforceTwo()
        if (depth >= 3) bruteforceThree()
        if (depth >= 4) bruteforceFour()

        while (true) {
            if (countIterations) {
                val average = iteration.get() / ++seconds
                var percentage = (iteration.toFloat() / (total).toFloat()).toDouble() * 100
                if (percentage < 0.01) percentage = 0.01
                println(
                    "$iteration/${total} (${percentage.toString().substring(
                        0,
                        4
                    )})% passwords tried after $seconds seconds. On average $average per second."
                )
            }
            Thread.sleep(1000)
            if (total - BigInteger.valueOf(iteration.get().toLong()) <= BigInteger.valueOf(0)) {
                println()
                println("Could not find a password, quitting..")
                return
            }
            if (finished) break
        }
    }

    private fun bruteforceOne() {
        for (i in 1..10) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    iteration.getAndIncrement()
                    if (verifier.verify(allowedCharacters[index].toString())) {
                        println("Password for file at $path === ${allowedCharacters[index]}")
                        Runtime.getRuntime().exit(0)
                    }
                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceTwo() {
        for (i in 1..10) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                range.forEach { firstIndex ->
                    val firstChar = allowedCharacters[firstIndex]
                    allowedCharacters.forEach { secondChar ->
                        iteration.getAndIncrement()
                        val string = firstChar.toString() + secondChar.toString()
                        if (verifier.verify(string)) {
                            println("Password for file at $path === $string")
                            Runtime.getRuntime().exit(0)
                        }
                    }
                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceThree() {
        for (i in 1..10) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                range.forEach { firstIndex ->
                    val firstChar = allowedCharacters[firstIndex]
                    allowedCharacters.forEach { secondChar ->
                        allowedCharacters.forEach { thirdChar ->
                            iteration.getAndIncrement()
                            val string = firstChar.toString() + secondChar.toString() + thirdChar.toString()
                            if (verifier.verify(string)) {
                                println("Password for file at $path === $string")
                                Runtime.getRuntime().exit(0)
                            }
                        }
                    }

                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceFour() {
        for (i in 1..10) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                range.forEach { firstIndex ->
                    val firstChar = allowedCharacters[firstIndex]
                    allowedCharacters.forEach { secondChar ->
                        allowedCharacters.forEach { thirdChar ->
                            allowedCharacters.forEach { fourthChar ->
                                iteration.getAndIncrement()
                                val string = firstChar.toString() +
                                        secondChar.toString() +
                                        thirdChar.toString() +
                                        fourthChar.toString()
                                if (verifier.verify(string)) {
                                    println("Password for file at $path === $string")
                                    Runtime.getRuntime().exit(0)
                                }
                            }

                        }
                    }

                }
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun getSubrange(i: Int): IntRange {
        val step = allowedCharacters.size / 10
        return if (i != 10) {
            (step * (i - 1))..(step * i)
        } else {
            (step * (i - 1)) until allowedCharacters.size
        }
    }
}