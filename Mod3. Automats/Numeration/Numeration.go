package main

import "fmt"

var (
	n, m, q0, index int
	D, canonD [][]int
	F, canonF [][]string
	mark []bool
	signals []int
)

func print() {
	fmt.Printf("%d\n%d\n%d\n", index, m, 0)
	for i := 0; i <= index - 1; i++ {
		for j := 0; j <= m - 1; j++ {
			fmt.Printf("%d ", canonD[i][j])
		}
		fmt.Printf("\n")
	}
	for i := 0; i <= index - 1; i++ {
		for j := 0; j <= m - 1; j++ {
			fmt.Printf("%s ", canonF[i][j])
		}
		fmt.Printf("\n")
	}
}

func DFS(q int) {
	mark[q] = true
	signals[q] = index
	index++
	for i := 0; i <= m - 1; i++ {
		if !mark[D[q][i]] {
			DFS(D[q][i])
		}
	}
}

func canonization() {
	DFS(q0)
	for i := 0; i <= n - 1; i++ {
		for j := 0; j <= m - 1; j++ {
			canonD[signals[i]][j] = signals[D[i][j]]
		}
	}
	for i := 0; i <= n - 1; i++ {
		for j := 0; j <= m - 1; j++ {
			canonF[signals[i]][j] = F[i][j]
		}
	}
}

func main()  {
	fmt.Scan(&n, &m, &q0)
	D, canonD = make([][]int, n), make([][]int, n)
	F, canonF = make([][]string, n), make([][]string, n)
	for i := 0; i <= n - 1; i++ {
		D[i], canonD[i] = make([]int, m), make([]int, m)
		for j := 0; j <= m - 1; j++ {
			fmt.Scan(&D[i][j])
		}
	}
	for i := 0; i <= n - 1; i++ {
		F[i], canonF[i] = make([]string, m), make([]string, m)
		for j := 0; j <= m - 1; j++ {
			fmt.Scan(&F[i][j])
		}
	}

	mark = make([]bool, n)
	signals = make([]int, n)
	for i := range mark {
		mark[i] = false
	}

	canonization()

	print()
}