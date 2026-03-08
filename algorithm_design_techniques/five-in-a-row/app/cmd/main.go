package main

import (
	"fmt"

	gamestate "github.com/durelius/five-in-a-row.git/internal/game_state"
)

func main() {
	game := gamestate.New()
	fmt.Println("Welcome to five in a row! Initial board below.")
	game.PrintBoard()
	game.GameLoop()

}
