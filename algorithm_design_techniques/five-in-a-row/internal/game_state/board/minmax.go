package board

import (
	"fmt"
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
const DEPTH_LEVEL = 5
const MAX_PLAYS = 10

func (b *Board) NextMove(color Color) (bestCoordinate Coordinate, noMovesLeft bool) {

	bestScoreCoordinate := ScoreCoordinate{Score: math.MinInt}
	possiblePlays := b.getSortedPossibleScorePlays(color)
	if len(possiblePlays) == 0 {
		return bestCoordinate, true
	}
	if possiblePlays[0].Score >= WIN_SCORE {
		return possiblePlays[0].Coordinate, false
	}
	for _, coord := range possiblePlays {
		newBoard := b.copy()

		if err := newBoard.Place(coord.Coordinate, color); err != nil {
			log.Println(err)
			continue
		}
		score := newBoard.minMax(DEPTH_LEVEL, math.MinInt, math.MaxInt, false, color)
		if score > bestScoreCoordinate.Score {
			bestScoreCoordinate = ScoreCoordinate{coord.Coordinate, score}
		}
		if score >= WIN_SCORE {
			return coord.Coordinate, false
		}
	}

	return bestScoreCoordinate.Coordinate, false
}
func oppColor(color Color) Color {
	if color == WHITE {
		return BLACK
	}
	return WHITE
}

// minMax returns the score of the best path down the tree
func (b *Board) minMax(depth, alpha, beta int, isMax bool, maxColor Color) int {
	if depth == 0 {
		return b.GetScore(maxColor)
	}

	if isMax {
		best := math.MinInt
		plays := b.getSortedPossibleScorePlays(maxColor)
		for _, coord := range plays {
			newBoard := b.copy()
			if err := newBoard.Place(coord.Coordinate, maxColor); err != nil {
				log.Println(err)
				continue
			}

			if boardScore := newBoard.GetScore(maxColor); newBoard.won(boardScore) {
				return boardScore
			}
			score := newBoard.minMax(depth-1, alpha, beta, false, maxColor)
			// log.Printf("white score: %d", score)
			best = max(best, score)
			alpha = max(best, alpha)
			if beta <= alpha { // prune
				break
			}
		}
		return best
	} else {
		best := math.MaxInt
		plays := b.getSortedPossibleScorePlays(oppColor(maxColor))
		for _, coord := range plays {
			newBoard := b.copy()
			if err := newBoard.Place(coord.Coordinate, oppColor(maxColor)); err != nil {
				log.Println(err)
				continue
			}
			if boardScore := newBoard.GetScore(maxColor); newBoard.won(boardScore) {
				return boardScore
			}
			score := newBoard.minMax(depth-1, alpha, beta, true, maxColor)
			// log.Printf("black score: %d", score)
			best = min(best, score)
			beta = min(best, beta)
			if beta <= alpha { // prune
				break
			}
		}
		return best
	}
}

func (b *Board) GetScore(maxColor Color) int {
	maxScore := 0
	minScore := 0

	directions := []Coordinate{
		{0, 1},  // horizontal
		{1, 0},  // vertical
		{1, -1}, // diagonal left
		{1, 1},  // diagonal right
	}

	for row := range SIZE {
		for col := range SIZE {
			for _, dir := range directions {
				maxScore += scoreLine(b, row, col, dir, maxColor)
				minScore += scoreLine(b, row, col, dir, oppColor(maxColor))
				if maxScore >= WIN_SCORE {
					return WIN_SCORE
				}
				if minScore >= WIN_SCORE {
					return -WIN_SCORE
				}
			}
		}
	}

	return maxScore - minScore
}

func (b *Board) won(score int) bool {
	return score >= WIN_SCORE || score <= -WIN_SCORE
}

func scoreLine(b *Board, row, col int, dir Coordinate, color Color) int {
	oppColor := BLACK
	if color == BLACK {
		oppColor = WHITE
	}
	count := 0
	for i := range 5 {
		r := row + dir.Row*i
		c := col + dir.Column*i
		if r < 0 || r >= SIZE || c < 0 || c >= SIZE {
			return 0
		}
		if b.Grid[r][c] == oppColor {
			return 0
		}
		if b.Grid[r][c] == color {
			count++
		}
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

// we bucket sort by scores, prioritizing our own score highest and then blocking the opponent
func (b *Board) getSortedPossibleScorePlays(color Color) []ScoreCoordinate {
	placedPositions := b.getAllPlacedPositions()
	visited := make(map[Coordinate]struct{})
	buckets := make([][]ScoreCoordinate, 8)

	for coord := range placedPositions {
		for dy := -1; dy <= 1; dy++ {
			for dx := -1; dx <= 1; dx++ {
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
				if _, occupied := placedPositions[neighbor]; occupied {
					continue
				}
				if _, seen := visited[neighbor]; seen {
					continue
				}
				visited[neighbor] = struct{}{}
				myScore, oppScore, err := b.ScorePlace(neighbor, color)
				if err != nil {
					log.Println(err)
					continue
				}
				score := max(myScore, oppScore)
				scoreCoordinate := ScoreCoordinate{neighbor, score}
				switch {
				case myScore >= WIN_SCORE:
					return []ScoreCoordinate{scoreCoordinate}
				case oppScore >= WIN_SCORE:
					buckets[7] = append(buckets[7], scoreCoordinate)
				case myScore >= IN_A_ROW_FOUR:
					buckets[6] = append(buckets[6], scoreCoordinate)
				case oppScore >= IN_A_ROW_FOUR:
					buckets[5] = append(buckets[5], scoreCoordinate)
				case myScore >= IN_A_ROW_THREE:
					buckets[4] = append(buckets[4], scoreCoordinate)
				case oppScore >= IN_A_ROW_THREE:
					buckets[3] = append(buckets[3], scoreCoordinate)
				case myScore >= IN_A_ROW_TWO:
					buckets[2] = append(buckets[2], scoreCoordinate)
				case oppScore >= IN_A_ROW_TWO:
					buckets[1] = append(buckets[1], scoreCoordinate)
				default:
					buckets[0] = append(buckets[0], scoreCoordinate)
				}
			}
		}
	}
	result := []ScoreCoordinate{}
	for i := len(buckets) - 1; i >= 0; i-- {
		result = append(result, buckets[i]...)
		if len(result) >= MAX_PLAYS {
			return result[0:MAX_PLAYS]
		}
	}
	return result
}

func (b *Board) copy() *Board {
	newBoard := &Board{}
	newBoard.Grid = b.Grid // this copies the array by value, it is not a slice as size is constant
	return newBoard
}
func (b *Board) countDirection(start Coordinate, dRow, dCol int, color Color) int {
	count := 0
	curr := Coordinate{Row: start.Row + dRow, Column: start.Column + dCol}
	for {
		if err := curr.Validate(); err != nil {
			break
		}
		if b.Grid[curr.Row][curr.Column] != color {
			break
		}
		count++
		curr.Row += dRow
		curr.Column += dCol
	}
	return count
}
func (b *Board) ScorePlace(toPlace Coordinate, color Color) (myScore, oppScore int, err error) {
	opponentColor := BLACK
	if color == BLACK {
		opponentColor = WHITE
	}
	if err := b.validateCoordinates(toPlace); err != nil {
		return 0, 0, err
	}

	directions := [][2]int{
		{0, 1},  // horizontal
		{1, 0},  // vertical
		{1, 1},  // diagonal down-right
		{1, -1}, // diagonal down-left
	}

	myBest := 0
	oppBest := 0
	for _, d := range directions {
		dRow, dCol := d[0], d[1]
		myCount := 1 // the placed piece itself
		oppCount := 1
		myCount += b.countDirection(toPlace, dRow, dCol, color)            // forward
		myCount += b.countDirection(toPlace, -dRow, -dCol, color)          // backward
		oppCount += b.countDirection(toPlace, dRow, dCol, opponentColor)   // forward
		oppCount += b.countDirection(toPlace, -dRow, -dCol, opponentColor) // backward
		if myCount > myBest {
			myBest = myCount
		}
		if oppCount > oppBest {
			oppBest = oppCount
		}
	}

	switch myBest {
	case 5:
		myScore = WIN_SCORE
	case 4:
		myScore = IN_A_ROW_FOUR
	case 3:
		myScore = IN_A_ROW_THREE
	case 2:
		myScore = IN_A_ROW_TWO
	default:
		myScore = TIE
	}

	switch oppBest {
	case 5:
		oppScore = WIN_SCORE
	case 4:
		oppScore = IN_A_ROW_FOUR
	case 3:
		oppScore = IN_A_ROW_THREE
	case 2:
		oppScore = IN_A_ROW_TWO
	default:
		oppScore = TIE
	}
	return myScore, oppScore, nil
}
func (c *Coordinate) Validate() error {

	humanIndexRow := c.Row + 1
	humanIndexCol := c.Column + 1
	if humanIndexRow < 1 || humanIndexRow > SIZE {
		return fmt.Errorf("Row needs to be between 1 and %d", SIZE)
	}
	if humanIndexCol < 1 || humanIndexCol > SIZE {
		return fmt.Errorf("Column needs to be between 1 and %d", SIZE)
	}
	return nil
}
