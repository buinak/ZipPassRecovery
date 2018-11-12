package com.buinak

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
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

    private val availableProcessors = Runtime.getRuntime().availableProcessors()
    private val threadNumber: Int

    init {
        var mostOptimal: Int =
            if (availableProcessors >= allowedCharacters.size) allowedCharacters.size else availableProcessors

        var bestRemainder = allowedCharacters.size % mostOptimal
        for (i in mostOptimal downTo (availableProcessors / 2)){
            if (allowedCharacters.size % i < bestRemainder){
                bestRemainder = allowedCharacters.size % i
                mostOptimal = i
            }
        }
        threadNumber = if (availableProcessors > 4) mostOptimal else availableProcessors
    }

    fun start() {
        var iterations: BigInteger = BigInteger.valueOf(allowedCharacters.size.toLong())
        for (i in 1..depth) {
            println("Amount of iterations for $depth depth = $iterations")
            total += BigInteger.valueOf(iterations.toLong())
            iterations *= BigInteger.valueOf(allowedCharacters.size.toLong())
        }
        println("Will be allocating $threadNumber threads with a step = ${allowedCharacters.size / threadNumber}")
        val estimatedSeconds = total / BigInteger.valueOf(6500)
        val estimatedHours = estimatedSeconds.toFloat() / 3600F
        println("ETA to finish worst-case given 6500 iterations per second: $estimatedSeconds seconds," +
                " $estimatedHours hours")
        println("Input enter to start, any to exit. ")
        if (readLine() != "") return

        if (depth >= 1) bruteforceOne()
        if (depth >= 2) bruteforceTwo()
        if (depth >= 3) bruteforceThree()
        if (depth >= 4) bruteforceFour()
        if (depth >= 5) bruteforceFive()
        if (depth >= 6) bruteforceSix()
        if (depth >= 7) bruteforceSeven()
        if (depth >= 8) bruteforceEight()

        while (true) {
            if (countIterations) {
                val average = iteration.get() / ++seconds
                var percentage = (iteration.toFloat() / (total).toFloat()).toDouble() * 100
                val remaining = (total - BigInteger.valueOf(iteration.get().toLong()))
                val remainingInSeconds = remaining.toLong() / average
                if (percentage < 0.01) percentage = 0.01
                println(
                    "$iteration/${total} (${percentage.toString().substring(
                        0,
                        4
                    )})% passwords tried after $seconds seconds. On average $average per second." +
                            " ETA to finish = ${remainingInSeconds + 1} seconds"
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
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    if (printTried) print(" ${allowedCharacters[index]} ")
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
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        iteration.getAndIncrement()
                        val string = allowedCharacters[index].toString() + allowedCharacters[secondIndex].toString()
                        if (printTried) print(" $string ")
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
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            iteration.getAndIncrement()
                            val string = allowedCharacters[index].toString() +
                                    allowedCharacters[secondIndex].toString() +
                                    allowedCharacters[thirdIndex].toString()
                            if (printTried) print(" $string ")
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
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                iteration.getAndIncrement()
                                val string = allowedCharacters[index].toString() +
                                        allowedCharacters[secondIndex].toString() +
                                        allowedCharacters[thirdIndex].toString() +
                                        allowedCharacters[fourthIndex].toString()
                                if (printTried) print(" $string ")
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
    private fun bruteforceFive() {
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    iteration.getAndIncrement()
                                    val string = allowedCharacters[index].toString() +
                                            allowedCharacters[secondIndex].toString() +
                                            allowedCharacters[thirdIndex].toString() +
                                            allowedCharacters[fourthIndex].toString() +
                                            allowedCharacters[fifthIndex].toString()
                                    if (printTried) print(" $string ")
                                    if (verifier.verify(string)) {
                                        println("Password for file at $path === $string")
                                        Runtime.getRuntime().exit(0)
                                    }
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
    private fun bruteforceSix() {
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        iteration.getAndIncrement()
                                        val string = allowedCharacters[index].toString() +
                                                allowedCharacters[secondIndex].toString() +
                                                allowedCharacters[thirdIndex].toString() +
                                                allowedCharacters[fourthIndex].toString() +
                                                allowedCharacters[fifthIndex].toString() +
                                                allowedCharacters[sixthIndex].toString()
                                        if (printTried) print(" $string ")
                                        if (verifier.verify(string)) {
                                            println("Password for file at $path === $string")
                                            Runtime.getRuntime().exit(0)
                                        }
                                    }
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
    private fun bruteforceSeven() {
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        for (seventhIndex in 0 until allowedCharacters.size) {
                                            iteration.getAndIncrement()
                                            val string = allowedCharacters[index].toString() +
                                                    allowedCharacters[secondIndex].toString() +
                                                    allowedCharacters[thirdIndex].toString() +
                                                    allowedCharacters[fourthIndex].toString() +
                                                    allowedCharacters[fifthIndex].toString() +
                                                    allowedCharacters[sixthIndex].toString() +
                                                    allowedCharacters[seventhIndex].toString()
                                            if (printTried) print(" $string ")
                                            if (verifier.verify(string)) {
                                                println("Password for file at $path === $string")
                                                Runtime.getRuntime().exit(0)
                                            }
                                        }
                                    }
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
    private fun bruteforceEight() {
        for (i in 1..threadNumber) {
            val range = getSubrange(i)
            Completable.fromAction {
                val verifier = Verifier(path)
                for (index in range) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        for (seventhIndex in 0 until allowedCharacters.size) {
                                            for (eighthIndex in 0 until allowedCharacters.size) {
                                                iteration.getAndIncrement()
                                                val string = allowedCharacters[index].toString() +
                                                        allowedCharacters[secondIndex].toString() +
                                                        allowedCharacters[thirdIndex].toString() +
                                                        allowedCharacters[fourthIndex].toString() +
                                                        allowedCharacters[fifthIndex].toString() +
                                                        allowedCharacters[sixthIndex].toString() +
                                                        allowedCharacters[seventhIndex].toString() +
                                                        allowedCharacters[eighthIndex].toString()
                                                if (printTried) print(" $string ")
                                                if (verifier.verify(string)) {
                                                    println("Password for file at $path === $string")
                                                    Runtime.getRuntime().exit(0)
                                                }
                                            }
                                        }
                                    }
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
        val step = if ((allowedCharacters.size / threadNumber) < 0) 1 else (allowedCharacters.size / threadNumber)
        return if (i != threadNumber) {
            (step * (i - 1))..(step * i)
        } else {
            (step * (i - 1)) until allowedCharacters.size
        }
    }

    private fun balancedStepList(countOfSteps: Int, totalCount: Int): List<Int> {
        val remainer = totalCount % countOfSteps
        var unbalancedStep = totalCount / countOfSteps
        val list = ArrayList<Int>(countOfSteps)
        if (unbalancedStep > 0) list.fill(unbalancedStep) else for (i in 1..countOfSteps) list.add(1)


        return list
    }
}