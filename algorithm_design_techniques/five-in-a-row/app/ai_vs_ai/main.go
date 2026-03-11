package main

import (
	"log"
	"time"

	gamestate "github.com/durelius/five-in-a-row.git/internal/game_state"
	"github.com/durelius/five-in-a-row.git/internal/game_state/board"
)

func main() {

	gs := gamestate.New()
	maxMoves := 500
	moves := 0
	center := board.Coordinate{Row: board.SIZE / 2, Column: board.SIZE / 2}
	_ = gs.Board.Place(center, board.BLACK)
	gs.FlipTurn()
gameLoop:
	for moves < maxMoves {
		if gs.BlackTurn {
			log.Println("computer thinking...")
			start := time.Now()
			coordinate, noPlays := gs.Board.NextMove(board.BLACK)
			if noPlays {
				log.Println("No moves next to play, quitting. We'll call it a draw")
				break gameLoop
			}
			since := time.Since(start)
			log.Printf("Computer finished thinking, took %v", since)
			gs.PrintBoard()
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				log.Printf("Computer placing failed, %v", err)
				return
			}
			moves++
			if gs.WinTurn(board.BLACK) {
				gs.PrintBoard()
				log.Println("AI black won!")
				return
			}
			gs.FlipTurn()
		} else {
			log.Println("computer thinking...")
			start := time.Now()
			coordinate, noPlays := gs.Board.NextMove(board.WHITE)
			if noPlays {
				log.Println("No moves next to play, quitting. We'll call it a draw")
				break gameLoop
			}
			since := time.Since(start)
			log.Printf("Computer finished thinking, took %v", since)
			gs.PrintBoard()
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				log.Printf("Computer placing failed, %v", err)
				return
			}
			moves++
			if gs.WinTurn(board.WHITE) {
				gs.PrintBoard()
				log.Println("AI white won!")
				break gameLoop
			}
			gs.FlipTurn()

		}
	}
}
