package main

import (
	"fmt"
	"sort"
)

var (
	n, m int
	D [][]elem
	X []string
	F []int
	D_ [][]*([]int)
	Q, F_ []*([]int)
	vertexes [][]int
	acceptOrReject []bool
	D__ [][]int
)

type elem struct {
	q int
	symbol string
}

type stack struct {
	a []*[]int
	cap, top int;
}

func InitStack(s *stack, n int) {
	s.a = make([]*[]int, n)
	s.cap = n
	s.top = 0
}

func StackEmpty(s *stack) bool {
	empty := s.top == 0
	return empty
}

func Push(s *stack, x *([]int)) {
	s.a[s.top] = x;
	s.top++
}

func Pop(s *stack) *([]int) {
	s.top--
	return s.a[s.top]
}

func print() {
	fmt.Print("digraph {\n\trankdir = LR\n\tdummy [label = \"\", shape = none]\n")
	for i := 0; i <= len(vertexes) - 1; i++ {
		if acceptOrReject[i] {
			fmt.Printf("\t%v [label = \"%v\", shape = doublecircle]\n", i, vertexes[i])
		} else {
			fmt.Printf("\t%v [label = \"%v\", shape = circle]\n", i, vertexes[i])
		}
	}
	fmt.Print("\tdummy -> 0\n")
	for i := 0; i <= len(D__) - 1; i++ {
		set := make(map[int]int)
		count := 0
		for j := 0; j <= len(X) - 1; j++ {
			_, ok := set[D__[i][j]]
			if !ok {
				set[D__[i][j]] = count
				fmt.Printf("\t%d -> %d [label = \"%s", i, D__[i][j], X[j])
				for z := j + 1; z <= len(X) - 1; z++ {
					if D__[i][z] == D__[i][j] {
						fmt.Printf(", %v", X[z])
					}
				}
				fmt.Print("\"]\n")
				count++
			}
		}
	}
	fmt.Print("}\n")
}

func DFS(q int, C *[]int, set map[int]bool) {
	_, ok := set[q]
	if !ok {
		set[q] = true
		*C = append(*C, q)
		for i := 0; i <= len(D[q]) - 1; i++ {
			if D[q][i].symbol == "lambda" {
				DFS(D[q][i].q, C, set)
			}
		}
	}
}

func Closure(z []int) *([]int) {
	var C []int
	set := make(map[int]bool)
	for q := 0; q <= len(z) - 1; q++ {
		DFS(z[q], &C, set)
	}
	sort.Ints(C)
	return &C
}

func Det(q int) {
	qAr := []int{q}
	q0 := Closure(qAr)

	Q, D_, F_, vertexes = make([]*[]int, 1), make([][]*[]int, 1), make([]*[]int, 0), make([][]int, 0)
	Q[0] = q0
	var m []*([]int)

	var s stack
	InitStack(&s, 100)
	Push(&s, q0)

	index := 0
	for {
		if StackEmpty(&s) {
			break
		}
		z := Pop(&s)
		for u := 0; u <= len(*z) - 1; u++ {
			if F[(*z)[u]] == 1 {
				F_ = append(F_, z)
				break
			}
		}

		for a := 0; a <= len(X) - 1; a++ {
			uAr := make([]int, 0)
			for i := 0; i <= len(*z) - 1; i++ {
				for j := 0; j <= len(D[(*z)[i]]) - 1; j++ {
					if D[(*z)[i]][j].symbol == X[a] {
						uAr = append(uAr, D[(*z)[i]][j].q)
					}
				}
			}
			z1 := Closure(uAr)

			flag := true
			for i := 0; i <= len(Q) - 1; i++ {
				if len(*(Q[i])) == len(*z1) {
					flag2 := true
					for j := 0; j <= len(*z1) - 1; j++ {
						if (*z1)[j] != (*Q[i])[j] {
							flag2 = false
							break
						}
					}
					if flag2 {
						z1 = Q[i]
						flag = false
						break
					}
				}
			}
			if flag {
				Q = append(Q, z1)
				var u []*([]int)
				D_ = append(D_, u)
				Push(&s, z1)
			}

			if a == 0 {
				flag = true
				for i := 0; i <= len(F_) - 1; i++ {
					if F_[i] == z {
						flag = false
						break
					}
				}
				m = append(m, z)
				vertexes = append(vertexes, *z)
				if flag {
					acceptOrReject = append(acceptOrReject, false)
				} else {
					acceptOrReject = append(acceptOrReject, true)
				}
				index++
			}
			D_[index - 1] = append(D_[index - 1], z1)
		}
	}

	D__ = make([][]int, len(D_))
	for i := 0; i <= len(D_) - 1; i++ {
		D__[i] = make([]int, len(X))
		for j := 0; j <= len(X) - 1; j++ {
			var k int
			for k = 0; k <= len(m) - 1; k++ {
				if m[k] == D_[i][j] {
					break
				}
			}
			D__[i][j] = k
		}
	}
}

func main() {
	fmt.Scan(&n, &m)
	D = make([][]elem, n)
	set := make(map[string]bool)
	for i := 0; i <= m - 1; i++ {
		var (
			q2 elem
			q int
		)
		fmt.Scan(&q, &q2.q, &q2.symbol)
		D[q] = append(D[q], q2)
		_, ok := set[q2.symbol]
		if !ok && q2.symbol != "lambda" {
			set[q2.symbol] = true
			X = append(X, q2.symbol)
		}
	}

	F = make([]int, n)
	for i := 0; i <= n - 1; i++ {
		fmt.Scan(&F[i])
	}

	var q0 int
	fmt.Scan(&q0)

	Det(q0)

	print()
}