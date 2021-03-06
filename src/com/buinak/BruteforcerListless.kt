package com.buinak

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

/**
 * Finds the password for an encrypted zip archive using exhaustive search.
 *
 * Time complexity is O(n^l) where n is the number of characters used in the
 * search and l is the length of the password.
 *
 * Space complexity is constant.
 * The algorithm does not use data structures in order to function.
 */
class BruteforcerListless(
    val path: String,
    val depth: Int = 1,
    val allowedCharacters: List<Char>,
    private val countIterations: Boolean = false,
    private val printTried: Boolean = false,
    private val distributeThreads: Boolean = false
) {
    private var finished = false
    private var seconds = 0
    private var iteration: AtomicInteger = AtomicInteger(0)
    private var total: BigInteger = BigInteger.valueOf(0)
    private var currentDepth: Int = 0
    private val threadNumber: Int
    private val depthFinishedCount = Collections.synchronizedMap(HashMap<Int, Int>())

    /**
     * Gets the maximum number of threads for allocation.
     */
    init {
        threadNumber = if (Runtime.getRuntime().availableProcessors() > allowedCharacters.size) {
            allowedCharacters.size
        } else {
            Runtime.getRuntime().availableProcessors()
        }
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
        println(
            "ETA to finish worst-case given 6500 iterations per second: $estimatedSeconds seconds," +
                    " $estimatedHours hours"
        )
        println("Distributed sets of characters among $threadNumber cores: ")
        val sublists = getSublists(threadNumber)
        sublists.forEach { println("Thread ${sublists.indexOf(it) + 1} - $it") }

        println("Input enter to start, any to exit. ")
        if (readLine() != "") return
        if (distributeThreads) {
            if (depth >= 1) bruteforceOne()
            if (depth >= 2) bruteforceTwo()
            if (depth >= 3) bruteforceThree()
            if (depth >= 4) bruteforceFour()
            if (depth >= 5) bruteforceFive()
            if (depth >= 6) bruteforceSix()
            if (depth >= 7) bruteforceSeven()
            if (depth >= 8) bruteforceEight()
            if (depth >= 8) bruteforceNine()
        } else if (depth >= 1) startExhaustiveSearch(1)


        //Counts iterations on the main thread, outputting the statistics every second.
        var lastIterations = 0
        while (true) {
            if (countIterations) {
                val average = iteration.get() / ++seconds
                if (average == 0) {
                    Thread.sleep(1000)
                    continue
                }
                var lastSecond: Int = 0
                if (lastIterations == 0) {
                    lastIterations = iteration.get()
                    lastSecond = lastIterations
                } else {
                    lastSecond = iteration.get() - lastIterations
                    lastIterations = iteration.get()
                }
                var percentage = (iteration.toFloat() / (total).toFloat()).toDouble() * 100
                val remaining = (total - BigInteger.valueOf(iteration.get().toLong()))
                val remainingInSeconds = remaining.toLong() / average
                if (percentage < 0.01) percentage = 0.01
                print(
                    "$iteration/${total} (${percentage.toString().substring(
                        0,
                        4
                    )})% passwords tried after $seconds seconds. $lastSecond iterations in last second." +
                            " ETA to finish = ${remainingInSeconds + 1} seconds. "

                )
                if (currentDepth != 0) print("Current depth = $currentDepth")
                println()
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

    private fun startExhaustiveSearch(depth: Int) {
        currentDepth = depth
        when (depth) {
            1 -> bruteforceOne()
            2 -> bruteforceTwo()
            3 -> bruteforceThree()
            4 -> bruteforceFour()
            5 -> bruteforceFive()
            6 -> bruteforceSix()
            7 -> bruteforceSeven()
            8 -> bruteforceEight()
            9 -> bruteforceNine()
        }
    }

    private fun finishedExhaustiveSearch(depth: Int){
        var count = depthFinishedCount.getOrDefault(depth, 0)
        if (count == 0) depthFinishedCount[depth] = 1 else depthFinishedCount[depth] = ++count
        if (count == threadNumber){
            println("Finished searching at depth = $depth")
            if (depth < this.depth) startExhaustiveSearch(depth + 1)
        }
    }

    private fun bruteforceOne() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    if (printTried) print(" $character ")
                    iteration.getAndIncrement()
                    if (verifier.verify(character.toString())) {
                        println("Password for file at $path === $character")
                        Runtime.getRuntime().exit(0)
                    }
                }
                finishedExhaustiveSearch(1)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceTwo() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        iteration.getAndIncrement()
                        val builder = StringBuilder()
                        builder.append(character)
                            .append(allowedCharacters[secondIndex])
                        val string = builder.toString()
                        if (printTried) print(" $string ")
                        if (verifier.verify(string)) {
                            println("Password for file at $path === $string")
                            Runtime.getRuntime().exit(0)
                        }
                    }
                }
                finishedExhaustiveSearch(2)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceThree() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            iteration.getAndIncrement()
                            val builder = StringBuilder()
                            builder.append(character)
                                .append(allowedCharacters[secondIndex])
                                .append(allowedCharacters[thirdIndex])
                            val string = builder.toString()
                            if (printTried) print(" $string ")
                            if (verifier.verify(string)) {
                                println("Password for file at $path === $string")
                                Runtime.getRuntime().exit(0)
                            }
                        }
                    }
                }
                finishedExhaustiveSearch(3)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceFour() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                iteration.getAndIncrement()
                                val builder = StringBuilder()
                                builder.append(character)
                                    .append(allowedCharacters[secondIndex])
                                    .append(allowedCharacters[thirdIndex])
                                    .append(allowedCharacters[fourthIndex])
                                val string = builder.toString()
                                if (printTried) print(" $string ")
                                if (verifier.verify(string)) {
                                    println("Password for file at $path === $string")
                                    Runtime.getRuntime().exit(0)
                                }
                            }
                        }
                    }
                }
                finishedExhaustiveSearch(4)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceFive() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    iteration.getAndIncrement()
                                    val builder = StringBuilder()
                                    builder.append(character)
                                        .append(allowedCharacters[secondIndex])
                                        .append(allowedCharacters[thirdIndex])
                                        .append(allowedCharacters[fourthIndex])
                                        .append(allowedCharacters[fifthIndex])
                                    val string = builder.toString()
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
                finishedExhaustiveSearch(5)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceSix() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        iteration.getAndIncrement()
                                        val builder = StringBuilder()
                                        builder.append(character)
                                            .append(allowedCharacters[secondIndex])
                                            .append(allowedCharacters[thirdIndex])
                                            .append(allowedCharacters[fourthIndex])
                                            .append(allowedCharacters[fifthIndex])
                                            .append(allowedCharacters[sixthIndex])
                                        val string = builder.toString()
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
                finishedExhaustiveSearch(6)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceSeven() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        for (seventhIndex in 0 until allowedCharacters.size) {
                                            iteration.getAndIncrement()
                                            val builder = StringBuilder()
                                            builder.append(character)
                                                .append(allowedCharacters[secondIndex])
                                                .append(allowedCharacters[thirdIndex])
                                                .append(allowedCharacters[fourthIndex])
                                                .append(allowedCharacters[fifthIndex])
                                                .append(allowedCharacters[sixthIndex])
                                                .append(allowedCharacters[seventhIndex])
                                            val string = builder.toString()
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
                finishedExhaustiveSearch(7)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceEight() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        for (seventhIndex in 0 until allowedCharacters.size) {
                                            for (eighthIndex in 0 until allowedCharacters.size) {
                                                iteration.getAndIncrement()
                                                val builder = StringBuilder()
                                                builder.append(character)
                                                    .append(allowedCharacters[secondIndex])
                                                    .append(allowedCharacters[thirdIndex])
                                                    .append(allowedCharacters[fourthIndex])
                                                    .append(allowedCharacters[fifthIndex])
                                                    .append(allowedCharacters[sixthIndex])
                                                    .append(allowedCharacters[seventhIndex])
                                                    .append(allowedCharacters[eighthIndex])
                                                val string = builder.toString()
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
                finishedExhaustiveSearch(8)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun bruteforceNine() {
        val sublist = getSublists(threadNumber)
        for (list in sublist) {
            Completable.fromAction {
                val verifier = Verifier(path)
                for (character in list) {
                    for (secondIndex in 0 until allowedCharacters.size) {
                        for (thirdIndex in 0 until allowedCharacters.size) {
                            for (fourthIndex in 0 until allowedCharacters.size) {
                                for (fifthIndex in 0 until allowedCharacters.size) {
                                    for (sixthIndex in 0 until allowedCharacters.size) {
                                        for (seventhIndex in 0 until allowedCharacters.size) {
                                            for (eighthIndex in 0 until allowedCharacters.size) {
                                                for (ninthIndex in 0 until allowedCharacters.size) {
                                                    iteration.getAndIncrement()
                                                    val builder = StringBuilder()
                                                    builder.append(character)
                                                        .append(allowedCharacters[secondIndex])
                                                        .append(allowedCharacters[thirdIndex])
                                                        .append(allowedCharacters[fourthIndex])
                                                        .append(allowedCharacters[fifthIndex])
                                                        .append(allowedCharacters[sixthIndex])
                                                        .append(allowedCharacters[seventhIndex])
                                                        .append(allowedCharacters[eighthIndex])
                                                        .append(allowedCharacters[ninthIndex])
                                                    val string = builder.toString()
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
                finishedExhaustiveSearch(9)
            }
                .subscribeOn(Schedulers.computation())
                .subscribe { }
        }
    }

    private fun getSublists(threadNumber: Int): List<List<Char>> {
        val resultList = ArrayList<ArrayList<Char>>()
        val step = allowedCharacters.size / threadNumber
        for (i in 0 until threadNumber) {
            val newList = allowedCharacters.subList(i * step, (i + 1) * step)
            resultList.add(ArrayList(newList))
        }

        if (allowedCharacters.size % threadNumber != 0) {
            val lastIndex = threadNumber * step
            var j = 0
            for (i in lastIndex until allowedCharacters.size) {
                resultList[j].add(allowedCharacters[i])
                j++
            }
        }

        return resultList
    }
}