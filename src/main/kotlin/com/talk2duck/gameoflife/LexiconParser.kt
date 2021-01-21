package com.talk2duck.gameoflife

import com.talk2duck.gameoflife.LexiconParser.parseLexicon
import java.net.URL
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

sealed class Content
data class Cell(val x: Int, val y: Int)
data class Text(val text: String) : Content()
data class Board(val width: Int = 0, val height: Int = 0, val populatedCells: Set<Cell> = emptySet()) : Content()
data class Term(val name: String, val content: List<Content> = emptyList())
data class Lexicon(val terms: Map<String, Term>)


/**
 * Parses lexicon text file and returns it in json format
 */
object LexiconParser {

    fun parseLexicon(lexiconFile: URL) : Lexicon {
        val text = lexiconFile.openStream().bufferedReader().use { it.readText() }
        val split = text.split("\n\r\n").filter { it.startsWith(":") }
        val terms = split.map(this::parseTerm).map { it.name to it }.toMap()
        return Lexicon(terms)
    }

    private fun parseTerm(term: String): Term {
        val split = term.split("\n")
        val items = mutableListOf<Content>()
        val boardItems = mutableListOf<String>()

        val compile = Pattern.compile(":(.+?):", DOTALL)
        val matcher = compile.matcher(split.first())


        matcher.find()
        val name = matcher.group(1)
        val description = split.first().replace(":$name:", "").trim()
        items.add(Text(description))

        for (line in split.drop(1)) {

            val charsInLine = line.trim().groupBy { it }.keys
            if (charsInLine.all { it in setOf('.', '*', 'b', 'a') }) {
                boardItems.add(line.trim().replace('a', '*').replace('b', '*'))
            } else {
                if (boardItems.isNotEmpty()) {
                    items.add(parseBoard(boardItems.joinToString("\n") { it.trim() }))
                    boardItems.clear()
                }
                items.add(Text(line.trim()))
            }
        }

        if (boardItems.isNotEmpty()) {
            items.add(parseBoard(boardItems.joinToString("\n") { it.trim() }))
        }

        return Term(name, items)
    }




    private fun parseBoard(boardAscii: String): Board {
        val split = boardAscii.split('\n').map(String::trim).filterNot(String::isEmpty)
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
        return Board(columns, rows, cells)
    }
}

fun main() {
    println(parseLexicon(LexiconParser.javaClass.getResource("/website/lexicon-no-wrap.txt")))
}
