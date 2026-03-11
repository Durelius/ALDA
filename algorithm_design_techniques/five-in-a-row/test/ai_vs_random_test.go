package ai_vs_random_test

import (
	"log"
	"testing"
	"time"

	gamestate "github.com/durelius/five-in-a-row.git/internal/game_state"
	"github.com/durelius/five-in-a-row.git/internal/game_state/board"
)

func TestAIVersesRandom(t *testing.T) {

	gs := gamestate.New()
	maxMoves := 500
	moves := 0
	coordinate := gamestate.RandomBoardCoordinate()
	_ = gs.Board.Place(coordinate, board.BLACK)
	gs.FlipTurn()
	lastRandomPlay := coordinate
gameLoop:
	for moves < maxMoves {
		if gs.BlackTurn {
			randomCount := 0
		playerTurn:
			if randomCount > 100 {
				t.Error("Random did 100 placings but failed to find an empty spot")
				break gameLoop
			}
			coordinate = gamestate.NextRandomConnectedCoordinate(lastRandomPlay)
			lastRandomPlay = coordinate

			//player always starts and is always black
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				randomCount++
				log.Printf("randomCount: %v", randomCount)
				goto playerTurn
			}
			moves++
			if gs.WinTurn(board.BLACK) {
				t.Error("Random won")
				gs.PrintBoard()
				break gameLoop
			}
			gs.FlipTurn()
		} else {
			log.Println("computer thinking...")
			start := time.Now()
			coordinate, noPlays := gs.Board.NextMove(board.WHITE)
			if noPlays {
				t.Error("No moves next to play, quitting. We'll call it a draw")
				break gameLoop
			}
			since := time.Since(start)
			log.Printf("Computer finished thinking, took %v", since)
			gs.PrintBoard()
			//computer is always white
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				t.Errorf("Computer placing failed, %v", err)
			}
			moves++
			if gs.WinTurn(board.WHITE) {
				gs.PrintBoard()
				log.Println("AI won!")
				break gameLoop
			}
			gs.FlipTurn()

		}
	}
}
func TestAIVSHorizontal(t *testing.T) {
	gs := gamestate.New()
	maxMoves := 500
	moves := 0
	col := 0

gameLoop:
	for moves < maxMoves {
		if gs.BlackTurn {
		playerTurn:
			coordinate := board.Coordinate{Row: 0, Column: col}
			col++
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				if col >= board.SIZE {
					t.Error("Ran out of horizontal columns")
					break gameLoop
				}
				goto playerTurn
			}
			moves++
			if gs.WinTurn(board.BLACK) {
				t.Error("Horizontal player won")
				gs.PrintBoard()
				break gameLoop
			}
			gs.FlipTurn()
		} else {
			coordinate, noPlays := gs.Board.NextMove(board.WHITE)
			if noPlays {
				t.Error("No moves left, draw")
				break gameLoop
			}
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				t.Errorf("Computer placing failed: %v", err)
			}
			moves++
			if gs.WinTurn(board.WHITE) {
				break gameLoop
			}
			gs.FlipTurn()
		}
	}
}

func TestAIVSVertical(t *testing.T) {
	gs := gamestate.New()
	maxMoves := 500
	moves := 0
	row := 0

gameLoop:
	for moves < maxMoves {
		if gs.BlackTurn {
		playerTurn:
			coordinate := board.Coordinate{Row: row, Column: 0}
			row++
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				if row >= board.SIZE {
					t.Error("Ran out of vertical rows")
					break gameLoop
				}
				goto playerTurn
			}
			moves++
			if gs.WinTurn(board.BLACK) {
				t.Error("Vertical player won")
				gs.PrintBoard()
				break gameLoop
			}
			gs.FlipTurn()
		} else {
			coordinate, noPlays := gs.Board.NextMove(board.WHITE)
			if noPlays {
				t.Error("No moves left, draw")
				break gameLoop
			}
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				t.Errorf("Computer placing failed: %v", err)
			}
			moves++
			if gs.WinTurn(board.WHITE) {
				break gameLoop
			}
			gs.FlipTurn()
		}
	}
}

func TestAIVSDiagonal(t *testing.T) {
	gs := gamestate.New()
	maxMoves := 500
	moves := 0
	step := 0

gameLoop:
	for moves < maxMoves {
		if gs.BlackTurn {
		playerTurn:
			coordinate := board.Coordinate{Row: step, Column: step}
			step++
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				if step >= board.SIZE {
					t.Error("Ran out of diagonal steps")
					break gameLoop
				}
				goto playerTurn
			}
			moves++
			if gs.WinTurn(board.BLACK) {
				t.Error("Diagonal player won")
				gs.PrintBoard()
				break gameLoop
			}
			gs.FlipTurn()
		} else {
			coordinate, noPlays := gs.Board.NextMove(board.WHITE)
			if noPlays {
				t.Error("No moves left, draw")
				break gameLoop
			}
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				t.Errorf("Computer placing failed: %v", err)
			}
			moves++
			if gs.WinTurn(board.WHITE) {
				break gameLoop
			}
			gs.FlipTurn()
		}
	}
}
