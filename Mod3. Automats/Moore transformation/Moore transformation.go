package main

import "fmt"

type State struct {
	q int
	y string
}

type Pair struct {
	q State
	x string
}

var (
	k1, k2, n int
	X, Y []string
	Q []State
	Q_ map[State]int
	D [][]int
	F [][]string
	D_ map[Pair]State
)

func print()  {
	fmt.Print("digraph {\n\trankdir = LR\n")
	for i := range Q {
		fmt.Printf("\t%d [label = \"(%d,%s)\"]\n", i, Q[i].q, Q[i].y)
	}
	for i := range Q {
		for j := range X {
			var p Pair
			p.q = Q[i]
			p.x = X[j]
			v, ok := D_[p]
			if ok {
				fmt.Printf("\t%d -> %d [label = \"%s\"]\n", i, Q_[v], X[j])
			}
		}
	}
	fmt.Print("}")
}

func transformation()  {
	for i := 0; i <= n - 1; i++ {
		for j := 0; j <= k2 - 1; j++ {
			t := false
			for k := 0; k <= n - 1; k++ {
				for f := 0; f <= k1 - 1; f++ {
					if D[k][f] == i && F[k][f] == Y[j] {
						var q State
						q.q = i
						q.y = Y[j]
						Q = append(Q, q)
						t = true
						if t {
							break
						}
					}
				}
				if t {
					break
				}
			}
		}
	}
	D_ = make(map[Pair]State)
	Q_ = make(map[State]int)
	for i := range Q {
		Q_[Q[i]] = i
		for j := range X {
			var (
				p Pair
				q State
			)
			p.q = Q[i]
			p.x = X[j]
			q.q = D[Q[i].q][j]
			q.y = F[Q[i].q][j]
			D_[p] = q
		}
	}
}

func scan() {
	fmt.Scan(&k1)
	X = make([]string, k1)
	for i := range X {
		fmt.Scan(&X[i])
	}
	fmt.Scan(&k2)
	Y = make([]string, k2)
	for i := range Y {
		fmt.Scan(&Y[i])
	}
	fmt.Scan(&n)
	D, F = make([][]int, n), make([][]string, n)
	for i := range D {
		D[i] = make([]int, k1)
		for j := range D[i] {
			fmt.Scan(&D[i][j])
		}
	}
	for i := range F {
		F[i] = make([]string, k1)
		for j := range F[i] {
			fmt.Scan(&F[i][j])
		}
	}
}

func main()  {
	scan()
	transformation()
	print()
}