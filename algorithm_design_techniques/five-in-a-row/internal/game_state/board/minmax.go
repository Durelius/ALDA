package board

import (
	"log"
	"math"
)

const (
	WIN_SCORE      = 1_000_000
	IN_A_ROW_FOUR  = 10_000
	IN_A_ROW_THREE = 1_000
	IN_A_ROW_TWO   = 100
	TIE            = 0
)
const DEPTH_LEVEL = 3

func (b *Board) NextMove() (bestCoordinate Coordinate, noMovesLeft bool) {

	highestScore := math.MinInt
	possiblePlays := b.getSortedPlays()
	if len(possiblePlays) == 0 {
		return bestCoordinate, true
	}
	for _, coord := range possiblePlays {
		newBoard := b.copy()

		if err := newBoard.Place(coord, WHITE); err != nil {
			log.Println(err)
			continue
		}
		score := newBoard.minMax(DEPTH_LEVEL, math.MinInt, math.MaxInt, false)
		if score > highestScore {
			highestScore = score
			bestCoordinate = coord
		}
		if score == WIN_SCORE || score == -WIN_SCORE {
			return bestCoordinate, false
		}
	}

	return bestCoordinate, false
}

func (b *Board) minMax(depth, alpha, beta int, isMax bool) int {
	score := b.GetScore()
	if depth == 0 || b.won(score) {
		return score // base case – score the position
	}

	if isMax {
		best := math.MinInt
		for _, coord := range b.getSortedPlays() {
			newBoard := b.copy()
			if err := newBoard.Place(coord, WHITE); err != nil {
				log.Println(err)
				continue
			}
			score := newBoard.minMax(depth-1, alpha, beta, false)
			best = max(best, score)
			alpha = max(best, alpha)
			if beta <= alpha { // prune
				break
			}
		}
		return best
	} else {
		best := math.MaxInt
		for _, coord := range b.getSortedPlays() {
			newBoard := b.copy()
			if err := newBoard.Place(coord, BLACK); err != nil {
				log.Println(err)
				continue
			}
			score := newBoard.minMax(depth-1, alpha, beta, true)
			best = min(best, score)
			beta = min(best, beta)
			if beta <= alpha { // prune
				break
			}
		}
		return best
	}
}
func (b *Board) GetScore() int {
	computerScore := 0
	playerScore := 0

	directions := []Coordinate{
		{0, 1},  // horizontal
		{1, 0},  // vertical
		{1, 1},  // diagonal right
		{1, -1}, // diagonal left
	}

	for row := range SIZE {
		for col := range SIZE {
			for _, dir := range directions {
				computerScore += scoreLine(b, row, col, dir, WHITE)
				playerScore += scoreLine(b, row, col, dir, BLACK)
			}
		}
	}

	return computerScore - playerScore
}
func (b *Board) won(score int) bool {
	return score >= WIN_SCORE || score <= -WIN_SCORE

}
func scoreLine(b *Board, row, col int, dir Coordinate, color Color) int {
	count := 0
	for i := range 5 {
		r := row + dir.Row*i
		c := col + dir.Column*i
		if r < 0 || r >= SIZE || c < 0 || c >= SIZE {
			return 0
		}
		if b.Grid[r][c] != color {
			return 0
		}
		count++
	}

	switch count {
	case 5:
		return WIN_SCORE
	case 4:
		return IN_A_ROW_FOUR
	case 3:
		return IN_A_ROW_THREE
	case 2:
		return IN_A_ROW_TWO
	default:
		return TIE
	}
}
func (b *Board) getAllPlacedPositions() map[Coordinate]Color {
	placedPositions := make(map[Coordinate]Color)
	for rowIndex := range SIZE {
		for colIndex := range SIZE {
			if b.Grid[rowIndex][colIndex] != EMPTY {
				coordinate := Coordinate{Row: rowIndex, Column: colIndex}
				placedPositions[coordinate] = b.Grid[rowIndex][colIndex]
			}
		}
	}
	return placedPositions

}
func (b *Board) getPossiblePlays() map[Coordinate]struct{} {
	placedPositions := b.getAllPlacedPositions()
	possiblePlays := make(map[Coordinate]struct{})

	for coord := range placedPositions {
		for dy := -2; dy <= 2; dy++ {
			for dx := -2; dx <= 2; dx++ {
				if dy == 0 && dx == 0 {
					continue
				}
				neighbor := Coordinate{
					Row:    coord.Row + dy,
					Column: coord.Column + dx,
				}
				if neighbor.Row < 0 || neighbor.Row >= SIZE ||
					neighbor.Column < 0 || neighbor.Column >= SIZE {
					continue
				}
				if _, occupied := placedPositions[neighbor]; !occupied {
					possiblePlays[neighbor] = struct{}{}
				}
			}
		}
	}
	return possiblePlays
}

// getSortedPlays returns a sorted list of possible plays with bucket sort,
// prioritizing coordinates that gives a higher score
func (b *Board) getSortedPlays() []Coordinate {
	possiblePlays := b.getPossiblePlays()
	buckets := make([][]Coordinate, 5)

	for coord := range possiblePlays {
		newBoard := b.copy()
		newBoard.Place(coord, WHITE)
		score := newBoard.GetScore()

		switch {
		case score >= WIN_SCORE:
			buckets[4] = append(buckets[4], coord)
		case score >= IN_A_ROW_FOUR:
			buckets[3] = append(buckets[3], coord)
		case score >= IN_A_ROW_THREE:
			buckets[2] = append(buckets[2], coord)
		case score >= IN_A_ROW_TWO:
			buckets[1] = append(buckets[1], coord)
		default:
			buckets[0] = append(buckets[0], coord)
		}
	}

	result := make([]Coordinate, 0, len(possiblePlays))
	for i := len(buckets) - 1; i >= 0; i-- {
		result = append(result, buckets[i]...)
	}
	return result
}
func (b *Board) copy() *Board {
	newBoard := &Board{}
	newBoard.Grid = b.Grid // in Go this copies the array by value!
	return newBoard
}
