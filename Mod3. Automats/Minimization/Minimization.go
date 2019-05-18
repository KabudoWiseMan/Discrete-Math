package main

import "fmt"

type Q struct {
	i, depth, parent int
}

var (
	n, M, q0, index int
	D, D_ [][]int
	F, F_ [][]string
	q, q_, pi []Q
	mark, in []bool
	signals []int
	alphabet = []string {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
		"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}
)

func print() {
	fmt.Print("digraph {\n\trankdir = LR\n\tdummy [label = \"\", shape = none]\n")
	for i := 0; i <= n - 1; i++ {
		if in[i] && signals[i] != -1 {
			fmt.Printf("\t%d [shape = circle]\n", signals[i])
		}
	}
	fmt.Printf("\tdummy -> %d\n", 0)
	for i := 0; i <= n - 1; i++ {
		if !in[i] {
			continue
		}
		for j := 0; j <= M - 1; j++ {
			if in[D_[i][j]] && signals[i] != -1 {
				fmt.Printf("\t%d -> %d [label = \"%s(%s)\"]\n", signals[i], signals[D_[i][j]], alphabet[j], F_[i][j])
			}
		}
	}
	fmt.Print("}")
}

func Union(x int, y int){
	rootX := Find(x)
	rootY := Find(y)
	if q[rootX].depth < q[rootY].depth {
		q[rootX].parent = rootY
	} else {
		q[rootY].parent = rootX
		if q[rootX].depth == q[rootY].depth && q[rootX] != q[rootY] {
			q[rootX].depth++
		}
	}
}

func Find(x int) (root int){
	if q[x].parent == x {
		root = x
	} else {
		q[x].parent = Find(q[x].parent)
		root = q[x].parent
	}
	return
}

func Split1() (m int){
	m = n
	for i := range q {
		q[i].parent = i
		q[i].depth = 0
	}

	for i := range q {
		for j := i; j <= n - 1; j++ {
			if Find(i) != Find(j) {
				eq := true
				for x := 0; x <= M - 1; x++ {
					if F[q[i].i][x] != F[q[j].i][x] {
						eq = false
						break
					}
				}
				if eq {
					Union(i, j)
					m--
				}
			}
		}
	}

	for i := range q {
		pi[q[i].i] = q[Find(i)]
	}

	return
}

func Split() (m int){
	m = n
	for i := range q {
		q[i].parent = i
		q[i].depth = 0
	}

	for i := range q {
		for j := i; j <= n - 1; j++ {
			if Find(i) != Find(j) && pi[q[i].i] == pi[q[j].i] {
				eq := true
				for x := 0; x <= M - 1; x++ {
					w1, w2 := D[q[i].i][x], D[q[j].i][x]
					if pi[w1] != pi[w2] {
						eq = false
						break
					}
				}
				if eq {
					Union(i, j)
					m--
				}
			}
		}
	}

	for i := range q {
		pi[q[i].i] = q[Find(i)]
	}

	return
}

func AufenkampHohn() {
	m := Split1()
	for {
		m_ := Split()
		if m == m_ {
			break
		}
		m = m_
	}

	count := 0
	for i := range q {
		q__ := pi[q[i].i]
		if !in[q__.i] {
			in[q__.i] = true
			count++
			for x := 0; x <= M - 1; x++ {
				D_[q__.i][x] = pi[D[q[i].i][x]].i
				F_[q__.i][x] = F[q[i].i][x]
			}
			q0 = pi[q0].i
		}
	}
}

func DFS(q int) {
	mark[q] = true
	signals[q] = index
	index++
	for i := 0; i <= M - 1; i++ {
		if !mark[D_[q][i]] && in[D_[q][i]]{
			DFS(D_[q][i])
		}
	}
}

func main()  {
	fmt.Scan(&n, &M, &q0)
	D, D_ = make([][]int, n), make([][]int, n)
	F, F_ = make([][]string, n), make([][]string, n)
	q, q_, pi = make([]Q, n), make([]Q, n), make([]Q, n)
	for i := 0; i <= n - 1; i++ {
		q[i].i = i
		D[i], D_[i] = make([]int, M), make([]int, M)
		for j := 0; j <= M - 1; j++ {
			fmt.Scan(&D[i][j])
		}
	}
	for i := 0; i <= n - 1; i++ {
		F[i], F_[i] = make([]string, M), make([]string, M)
		for j := 0; j <= M - 1; j++ {
			fmt.Scan(&F[i][j])
		}
	}

	mark, in = make([]bool, n), make([]bool, n)
	signals = make([]int, n)
	for i := range mark {
		mark[i] = false
		in[i] = false
		signals[i] = -1
	}

	AufenkampHohn()

	DFS(q0)

	print()
}