package com.techullurgy.retest

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

sealed class Piece(
    @DrawableRes val icon: Int,
    val pieceColor: Color,
    private val pieceIndex: Int
) {
    val content = @Composable {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            painter = painterResource(icon),
            contentDescription = "",
            tint = pieceColor
        )
    }

    protected abstract fun getAvailableMovesAt(board: List<Piece?>): List<Int>

    protected fun getVerticalMoves(index: Int, board: List<Piece?>): List<Int> {
        val indices = mutableListOf<Int>()
        val currentRow = index.row
        val currentColumn = index.column

        var tempRow = currentRow + 1
        while((tempRow in 0..7)) {
            if(board.canPlace(tempRow, currentColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn currentColumn)
                if(!board.isEmptyCell(tempRow, currentColumn)) break
            } else {
                break
            }
            tempRow++
        }
        tempRow = currentRow - 1
        while((tempRow in 0..7)) {
            if(board.canPlace(tempRow, currentColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn currentColumn)
                if(!board.isEmptyCell(tempRow, currentColumn)) break
            } else {
                break
            }
            tempRow--
        }
        return indices
    }

    protected fun getHorizontalMoves(index: Int, board: List<Piece?>): List<Int> {
        val indices = mutableListOf<Int>()
        val currentRow = index.row
        val currentColumn = index.column

        var tempColumn = currentColumn + 1
        while((tempColumn in 0..7)) {
            if(board.canPlace(currentRow, tempColumn, pieceColor)) {
                indices.add(currentRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(currentRow, tempColumn)) break
            } else {
                break
            }
            tempColumn++
        }
        tempColumn = currentColumn - 1
        while((tempColumn in 0..7)) {
            if(board.canPlace(currentRow, tempColumn, pieceColor)) {
                indices.add(currentRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(currentRow, tempColumn)) break
            } else {
                break
            }
            tempColumn--
        }
        return indices
    }

    protected fun getDiagonalMoves(index: Int, board: List<Piece?>): List<Int> {
        val indices = mutableListOf<Int>()

        val currentRow = index.row
        val currentColumn = index.column

        var tempRow = currentRow-1
        var tempColumn = currentColumn+1

        while(tempRow in 0..7 && tempColumn in 0..7) {
            if(board.canPlace(tempRow, tempColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(tempRow, tempColumn)) break
            } else {
                break
            }
            tempRow--
            tempColumn++
        }

        tempRow = currentRow+1
        tempColumn = currentColumn-1

        while(tempRow in 0..7 && tempColumn in 0..7) {
            if(board.canPlace(tempRow, tempColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(tempRow, tempColumn)) break
            } else {
                break
            }
            tempRow++
            tempColumn--
        }

        return indices
    }

    protected fun getAntiDiagonalMoves(index: Int, board: List<Piece?>): List<Int> {
        val indices = mutableListOf<Int>()

        val currentRow = index.row
        val currentColumn = index.column

        var tempRow = currentRow-1
        var tempColumn = currentColumn-1

        while(tempRow in 0..7 && tempColumn in 0..7) {
            if(board.canPlace(tempRow, tempColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(tempRow, tempColumn)) break
            } else {
                break
            }
            tempRow--
            tempColumn--
        }

        tempRow = currentRow+1
        tempColumn = currentColumn+1

        while(tempRow in 0..7 && tempColumn in 0..7) {
            if(board.canPlace(tempRow, tempColumn, pieceColor)) {
                indices.add(tempRow rowAndColumn tempColumn)
                if(!board.isEmptyCell(tempRow, tempColumn)) break
            } else {
                break
            }
            tempRow++
            tempColumn++
        }

        return indices
    }

    protected fun getLMoves(index: Int, board: List<Piece?>): List<Int> {
        val indices = mutableListOf<Int>()

        val currentRow = index.row
        val currentColumn = index.column

        var tempRow = currentRow-2
        var tempColumn = currentColumn-1
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow-2
        tempColumn = currentColumn+1
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow+2
        tempColumn = currentColumn-1
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow+2
        tempColumn = currentColumn+1
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow-1
        tempColumn = currentColumn-2
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow-1
        tempColumn = currentColumn+2
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow+1
        tempColumn = currentColumn-2
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        tempRow = currentRow+1
        tempColumn = currentColumn+2
        if(tempRow in 0..7 && tempColumn in 0..7 && board.canPlace(tempRow, tempColumn, pieceColor)) {
            indices.add(tempRow rowAndColumn tempColumn)
        }

        return indices
    }

    fun getAvailableIndices(board: List<Piece?>): List<Int> {
        return getAvailableMovesAt(board).filter {
            val piece = this
            val newBoard = board.toMutableList().apply {
                this[pieceIndex] = null
                this[it] = changeIndex(piece, it)
            }
            !isKingInCheck(newBoard)
        }
    }

    private fun isKingInCheck(board: List<Piece?>): Boolean {
        val kingPosition = board.filterIsInstance<King>().first { it.color == pieceColor }.index
        board.filterNotNull().filter { it.pieceColor != pieceColor }.forEach {
            if(it.getAvailableMovesAt(board).contains(kingPosition)) return true
        }
        return false
    }

    companion object {
        protected fun changeIndex(piece: Piece?, index: Int): Piece? {
            return when(piece) {
                is Bishop -> piece.copy(index = index)
                is King -> piece.copy(index = index)
                is Knight -> piece.copy(index = index)
                is Pawn -> piece.copy(index = index)
                is Queen -> piece.copy(index = index)
                is Rook -> piece.copy(index = index)
                else -> piece
            }
        }
    }
    
    data class Pawn(
        val index: Int,
        val color: Color,
        val isFirstMoveDone: Boolean = false,
    ): Piece(R.drawable.pawn_svgrepo_com, color, index) {
        private val direction: Int
            get() = if(pieceColor == Color.White) 1 else -1

        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            val result = mutableListOf<Int>()

            val currentRow = index.row
            val currentColumn = index.column

            val nextRow = currentRow + direction
            val nextColumns = listOf(currentColumn-1, currentColumn, currentColumn+1)
                .filter { it in 0..7 }
                .filter {
                    if(it != currentColumn) {
                        // Opposite piece is available in diagonal
                        !board.isEmptyCell(nextRow, it) && board.canPlace(nextRow, it, pieceColor)
                    } else {
                        board.isEmptyCell(nextRow, it)
                    }
                }

            if(nextRow in 0..7) {
                nextColumns.forEach {
                    result.add(nextRow rowAndColumn it)
                }
            }

            if(!isFirstMoveDone) {
                val twoStepIndex = (currentRow + 2*direction) rowAndColumn currentColumn
                if(board.isEmptyCell(twoStepIndex.row, twoStepIndex.column))
                result.add(twoStepIndex)
            }

            return result
        }
    }

    data class King(
        val index: Int,
        val color: Color,
    ): Piece(R.drawable.king_svgrepo_com, color, index) {
        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            val result = mutableListOf<Int>()

            val currentRow = index.row
            val currentColumn = index.column

            for (row in currentRow-1..currentRow+1) {
                if(row in 0..7) {
                    for(column in currentColumn-1..currentColumn+1) {
                        if(column in 0..7) {
                            val i = row rowAndColumn column
                            // Either no piece OR Opposite color
                            if(board.canPlace(row, column, pieceColor)) {
                                result.add(i)
                            }
                        }
                    }
                }
            }

            return result
        }
    }

    data class Queen(
        val index: Int,
        val color: Color,
    ): Piece(R.drawable.queen_svgrepo_com, color, index) {
        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            return getVerticalMoves(index, board) + getHorizontalMoves(index, board) + getDiagonalMoves(index, board) + getAntiDiagonalMoves(index, board)
        }
    }

    data class Bishop(
        val index: Int,
        val color: Color,
    ): Piece(R.drawable.bishop_svgrepo_com, color, index) {
        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            return getDiagonalMoves(index, board) + getAntiDiagonalMoves(index, board)
        }
    }

    data class Knight(
        val index: Int,
        val color: Color,
    ): Piece(R.drawable.knight_svgrepo_com, color, index) {
        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            return getLMoves(index, board)
        }
    }

    data class Rook(
        val index: Int,
        val color: Color,
    ): Piece(R.drawable.rook_svgrepo_com, color, index) {
        override fun getAvailableMovesAt(board: List<Piece?>): List<Int> {
            return getVerticalMoves(index, board) + getHorizontalMoves(index, board)
        }
    }
}

data class Chess (
    val board: SnapshotStateList<Piece?> = List(8 * 8) {
        when(it.row) {
            0 -> {
                when(it.column) {
                    0,7 -> Piece.Rook(it, Color.White)
                    1,6 -> Piece.Knight(it, Color.White)
                    2,5 -> Piece.Bishop(it, Color.White)
                    3 -> Piece.Queen(it, Color.White)
                    4 -> Piece.King(it, Color.White)
                    else -> null
                }
            }
            1 -> { Piece.Pawn(it, Color.White)}
            6 -> { Piece.Pawn(it, Color.Black)}
            7 -> {
                when(it.column) {
                    0,7 -> Piece.Rook(it, Color.Black)
                    1,6 -> Piece.Knight(it, Color.Black)
                    2,5 -> Piece.Bishop(it, Color.Black)
                    3 -> Piece.King(it, Color.Black)
                    4 -> Piece.Queen(it, Color.Black)
                    else -> null
                }
            }
            else -> null
        }
    }.toMutableStateList(),
) {
    val availableCellsForSelectedPiece: SnapshotStateList<Int> = mutableStateListOf()
    var selectedIndex by mutableIntStateOf(-1)

    var oppositeKingIndexOnCheck by mutableIntStateOf(-1)
        private set

    fun move(to: Int) {
        if(selectedIndex == -1) return

        val from = selectedIndex
        val newPiece = when(val currentPiece = board[from]!!) {
            is Piece.Bishop -> currentPiece.copy(index = to)
            is Piece.King -> currentPiece.copy(index = to)
            is Piece.Knight -> currentPiece.copy(index = to)
            is Piece.Pawn -> currentPiece.copy(index = to, isFirstMoveDone = true)
            is Piece.Queen -> currentPiece.copy(index = to)
            is Piece.Rook -> currentPiece.copy(index = to)
        }

        board[from] = null
        board[to] = newPiece
        selectedIndex = -1
        availableCellsForSelectedPiece.clear()

        val oppositeColor = board.filterNotNull().map { it.pieceColor }.first { it != newPiece.pieceColor }
        oppositeKingIndexOnCheck = checkForOppositeKingInCheck(oppositeColor)
        if(oppositeKingIndexOnCheck != -1) {
            if(checkForOppositeKingCheckMate(oppositeColor)) {
                oppositeKingIndexOnCheck = -5
            }
        }
    }

    private fun checkForOppositeKingInCheck(oppositeColor: Color): Int {
        val oppositeKingPosition = board.filterIsInstance<Piece.King>().first { it.color == oppositeColor }.index

        board.filterNotNull().filter { it.pieceColor != oppositeColor }
            .forEach {
                if(it.getAvailableIndices(board).contains(oppositeKingPosition)) return oppositeKingPosition
            }

        return -1
    }

    private fun checkForOppositeKingCheckMate(oppositeColor: Color): Boolean {
        board.filterNotNull().filter { it.pieceColor == oppositeColor }
            .forEach {
                if(it.getAvailableIndices(board).isNotEmpty()) return false
            }

        return true
    }
}

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier
) {
    val chess = remember { Chess() }

    Column {
        if(chess.oppositeKingIndexOnCheck == -5) {
            Text("Check Mate", color = Color.White)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = modifier
        ) {
            itemsIndexed(chess.board) { index, item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            color = if(chess.oppositeKingIndexOnCheck == index) {
                                Color.Red
                            } else if(chess.availableCellsForSelectedPiece.contains(index)) {
                                Color.Magenta
                            } else if (index.row % 2 == 0) {
                                if (index % 2 == 0) {
                                    Color(0xff6681b2)
                                } else {
                                    Color(0xffb0c2e1)
                                }
                            } else {
                                if (index % 2 == 0) {
                                    Color(0xffb0c2e1)
                                } else {
                                    Color(0xff6681b2)
                                }
                            }
                        )
                        .clickable {
                            if(chess.selectedIndex != -1) {
                                if(chess.availableCellsForSelectedPiece.contains(index)) {
                                    chess.move(index)
                                    return@clickable
                                }
                            }
                            if(chess.selectedIndex == index) {
                                chess.availableCellsForSelectedPiece.clear()
                                chess.selectedIndex = -1
                                return@clickable
                            }

                            if(!chess.board.isEmptyCell(index.row, index.column)) {
                                chess.selectedIndex = index
                                chess.availableCellsForSelectedPiece.apply {
                                    clear()
                                    addAll(chess.board[index]!!.getAvailableIndices(chess.board))
                                }
                            } else {
                                chess.availableCellsForSelectedPiece.clear()
                                chess.selectedIndex = -1
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    item?.content?.let { it() }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChessPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ChessBoard()
    }
}

val Int.row get() = this / 8
val Int.column get() = this % 8

infix fun Int.rowAndColumn(other: Int) = this * 8 + other

private fun List<Piece?>.isEmptyCell(row: Int, column: Int): Boolean {
    val index = row rowAndColumn column
    return this[index] == null
}

fun List<Piece?>.canPlace(row: Int, column: Int, color: Color): Boolean {
    val index = row rowAndColumn column
    return isEmptyCell(row, column) || this[index]?.pieceColor != color
}