package main

import (
	gamestate "github.com/durelius/five-in-a-row.git/internal/game_state"
)

func main() {
	game := gamestate.New()
	game.PrintBoard()
	game.GameLoop()
}
