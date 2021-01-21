package com.talk2duck.gameoflife

import com.talk2duck.gameoflife.LexiconParser.parseLexicon
import java.net.URL

data class Cell(val x: Int, val y: Int)
data class Board(val populatedCells: Set<Cell> = emptySet())


/**
 * Parses lexicon text file and returns it in json format
 */
object LexiconParser {

    fun parseLexicon(lexiconFile: URL) {
        val text = lexiconFile.openStream().bufferedReader().use { it.readText() }
        println("text = ${text}")

    }

    fun asciiToBoard(ascii: String): Board {
        val split = ascii.split('\n').map(String::trim).filterNot(String::isEmpty)
        val columns = split[0].length
        val rows = split.size
        val rowDelta = columns / 2
        val columnDelta = rows / 2

        val cells = mutableSetOf<Cell>()

        for (x in 0 until columns) {
            for (y in 0 until rows) {
                if (split[y][x] == '*') cells.add(Cell(-rowDelta + x, -columnDelta + y))
            }
        }
        return Board(cells)
    }
}

fun main() {
    parseLexicon(LexiconParser.javaClass.getResource("/website/lexicon.txt"))
}
