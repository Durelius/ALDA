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
func NextRandomConnectedCoordinate(prev board.Coordinate) board.Coordinate {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))

	// all 8 neighbors, shuffled
	deltas := [][2]int{
		{-1, -1}, {-1, 0}, {-1, 1},
		{0, -1}, {0, 1},
		{1, -1}, {1, 0}, {1, 1},
	}
	r.Shuffle(len(deltas), func(i, j int) {
		deltas[i], deltas[j] = deltas[j], deltas[i]
	})

	for _, d := range deltas {
		candidate := board.Coordinate{
			Row:    prev.Row + d[0],
			Column: prev.Column + d[1],
		}
		if candidate.Row >= 0 && candidate.Row < board.SIZE &&
			candidate.Column >= 0 && candidate.Column < board.SIZE {
			if err := candidate.Validate(); err == nil {
				return candidate
			}
		}
	}

	// fallback: We are entirely surrounded and can't go anywhere, return new coordinate
	return RandomBoardCoordinate()
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
