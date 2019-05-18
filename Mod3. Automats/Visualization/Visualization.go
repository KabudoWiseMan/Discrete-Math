package main

import "fmt"

var (
	n, m, q0 int
	D, canonD [][]int
	F, canonF [][]string
	alphabet = []string {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
						"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}
)

func print() {
	fmt.Print("digraph {\n\trankdir = LR\n\tdummy [label = \"\", shape = none]\n")
	for i := 0; i <= n - 1; i++ {
		fmt.Printf("\t%d [shape = circle]\n", i)
	}
	fmt.Printf("\tdummy -> %d\n", q0)
	for i := 0; i <= n - 1; i++ {
		for j := 0; j <= m - 1; j++ {
			fmt.Printf("\t%d -> %d [label = \"%s(%s)\"]\n", i, D[i][j], alphabet[j], F[i][j])
		}
	}
	fmt.Print("}")
}

func main()  {
	fmt.Scan(&n, &m, &q0)
	D = make([][]int, n)
	F = make([][]string, n)
	for i := 0; i <= n - 1; i++ {
		D[i] = make([]int, m)
		for j := 0; j <= m - 1; j++ {
			fmt.Scan(&D[i][j])
		}
	}
	for i := 0; i <= n - 1; i++ {
		F[i] = make([]string, m)
		for j := 0; j <= m - 1; j++ {
			fmt.Scan(&F[i][j])
		}
	}

	print()
}