package gamestate

import (
	"bufio"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/durelius/five-in-a-row.git/internal/game_state/board"
)

type GameState struct {
	Board      board.Board
	PlayerTurn bool
}

func New() *GameState {
	fmt.Println("Welcome to five in a row! Initial board below.")
	return &GameState{Board: *board.New(), PlayerTurn: true}
}

func (gs *GameState) PrintBoard() {
	gs.Board.Print()
}
func (gs *GameState) GameLoop() {
	for {
		if gs.PlayerTurn {
		playerTurn:
			coordinate, err := readMove()
			//player always starts and is always black
			if valErr := gs.Board.Place(coordinate, board.BLACK); err != nil || valErr != nil {
				if err == nil {
					err = valErr
				}
				fmt.Println(err.Error())
				goto playerTurn
			}
			gs.Board.PrintPlay(coordinate, board.BLACK)
			if gs.WinTurn() {
				gs.PrintBoard()
				fmt.Println("player won!")
				break
			}
			gs.FlipTurn()
		} else {
		computerTurn:
			coordinate, noPlays := gs.Board.NextMove()
			if noPlays {
				fmt.Println("No moves next to play, quitting. We'll call it a draw")
				break
			}
			//computer is always white
			if err := gs.Board.Place(coordinate, board.WHITE); err != nil {
				goto computerTurn
			}
			gs.Board.PrintPlay(coordinate, board.WHITE)
			if gs.WinTurn() {
				fmt.Println("computer won!")
				break
			}
			gs.FlipTurn()

		}
		gs.Board.Print()
	}
}
func (gs *GameState) WinTurn() bool {
	score := gs.Board.GetScore()
	return score == board.WIN_SCORE || -score == board.WIN_SCORE
}
func (gs *GameState) FlipTurn() {
	gs.PlayerTurn = !gs.PlayerTurn
}
func RandomBoardCoordinate() (coordinate board.Coordinate) {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	return board.Coordinate{Row: r.Intn(board.SIZE), Column: r.Intn(board.SIZE)}
}

func readMove() (board.Coordinate, error) {
	reader := bufio.NewReader(os.Stdin)

	fmt.Print("Enter move (row col): ")
	input, err := reader.ReadString('\n')
	if err != nil {
		return board.Coordinate{}, err
	}

	input = strings.TrimSpace(input)
	parts := strings.Fields(input)

	if len(parts) != 2 {
		return board.Coordinate{}, fmt.Errorf("please enter exactly two numbers")
	}

	row, err := strconv.Atoi(parts[0])
	if err != nil {
		return board.Coordinate{}, fmt.Errorf("invalid row number")
	}

	col, err := strconv.Atoi(parts[1])
	if err != nil {
		return board.Coordinate{}, fmt.Errorf("invalid column number")
	}

	return board.Coordinate{Row: row - 1, Column: col - 1}, nil // convert to 0-based index
}
