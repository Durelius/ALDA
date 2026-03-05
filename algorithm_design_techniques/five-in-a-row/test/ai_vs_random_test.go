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
gameLoop:
	for moves < maxMoves {
		if gs.PlayerTurn {
			randomCount := 0
		playerTurn:
			if randomCount > 100 {
				t.Error("Random did 100 placings but failed to find an empty spot")
				break gameLoop
			}
			coordinate := gamestate.RandomBoardCoordinate()
			//player always starts and is always black
			if err := gs.Board.Place(coordinate, board.BLACK); err != nil {
				randomCount++
				log.Printf("randomCount: %v", randomCount)
				goto playerTurn
			}
			moves++
			if gs.WinTurn() {
				t.Error("Random won")
				break gameLoop
			}
			gs.FlipTurn()
		} else {
			log.Println("computer thinking...")
			start := time.Now()
			coordinate, noPlays := gs.Board.NextMove()
			if noPlays {
				t.Error("No moves next to play, quitting. We'll call it a draw")
				break gameLoop
			}
			since := time.Since(start)
			log.Printf("Computer finished thinking, took %v", since)
			//computer is always white
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				t.Errorf("Computer placing failed, %v", err)
			}
			moves++
			if gs.WinTurn() {
				gs.PrintBoard()
				log.Println("computer won!")
				break gameLoop
			}
			gs.FlipTurn()

		}
	}
}
