package board

import (
	"errors"
	"fmt"
	"log"
	"strings"
)

const SIZE = 15

type Color int
type Row [SIZE]Color

type Board struct {
	Grid [SIZE]Row
}

const (
	EMPTY Color = iota
	WHITE
	BLACK
)

type Coordinate struct {
	Row    int
	Column int
}
type ScoreCoordinate struct {
	Coordinate
	Score int
}

func New() *Board {
	return &Board{}
}

func (b *Board) Print() {
	var header strings.Builder

	header.WriteString("   ")

	for i := 1; i <= SIZE; i++ {
		fmt.Fprintf(&header, "%2d ", i)
	}

	fmt.Println(header.String())

	for i := range SIZE {
		var row strings.Builder

		fmt.Fprintf(&row, "%2d ", i+1)

		for j := range SIZE {
			fmt.Fprintf(&row, " %s ", b.Grid[i][j].String())
		}

		fmt.Println(row.String())
	}
}
func (b *Board) validateCoordinates(coordinate Coordinate) error {
	humanIndexRow := coordinate.Row + 1
	humanIndexCol := coordinate.Column + 1
	if humanIndexRow < 1 || humanIndexRow > SIZE {
		return fmt.Errorf("Row needs to be between 1 and %d", SIZE)
	}
	if humanIndexCol < 1 || humanIndexCol > SIZE {
		return fmt.Errorf("Column needs to be between 1 and %d", SIZE)
	}

	if b.GetCoordinateState(coordinate) != EMPTY {
		return fmt.Errorf("Position not empty")
	}

	return nil
}
func (b *Board) GetCoordinateState(coordinate Coordinate) Color {
	return b.Grid[coordinate.Row][coordinate.Column]
}
func (b *Board) SetCoordinateState(coordinate Coordinate, state Color) {
	b.Grid[coordinate.Row][coordinate.Column] = state
}

func (b *Board) PrintPlay(coordinate Coordinate, brick Color) {
	var whoPlaced string

	switch brick {
	case BLACK:
		whoPlaced = "Black (person)"
	case WHITE:
		whoPlaced = "White (computer)"
	default:
		log.Fatal("Incorrect use of placing")
	}

	fmt.Printf("%s placed row %d, col %d \n", whoPlaced, coordinate.Row+1, coordinate.Column+1)
}
func (b *Board) Place(coordinate Coordinate, brick Color) error {
	if err := b.validateCoordinates(coordinate); err != nil {
		return err
	}
	b.SetCoordinateState(coordinate, brick)
	return nil

}
func (ps *Color) String() string {
	switch *ps {
	case EMPTY:
		return "."
	case WHITE:
		return "w"
	case BLACK:
		return "b"
	default:
		log.Fatal(errors.New("Unexpected positionstate value"))
		return ""
	}

}
