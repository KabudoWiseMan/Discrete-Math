package main

import "fmt"

var time, count = 1, 1

type vertex struct {
	v int
	next *vertex
}

type graph struct {
	next *vertex
	T1, comp, low int
}

type stack struct {
	a []int
	cap, top int;
}

func InitStack(s *stack, n int) {
	s.a = make([]int, n)
	s.cap = n
	s.top = 0
}

func Push(s *stack, x int) {
	s.a[s.top] = x;
	s.top++
}

func Pop(s *stack) int {
	s.top--
	return s.a[s.top]
}

func Tarjan(G []graph, n int) {
	for v := 0; v <= n - 1; v++ {
		G[v].T1, G[v].comp = 0, 0
	}
	var s stack
	InitStack(&s, n)
	for v := 0; v <= n - 1; v++ {
		if G[v].T1 == 0 {
			VisitVertex_Tarjan(G, v, &s)
		}
	}
}

func VisitVertex_Tarjan(G []graph, v int, s *stack) {
	G[v].T1, G[v].low = time, time
	time++
	Push(s, v)
	t := G[v].next
	for t != nil {
		if G[t.v].T1 == 0 {
			VisitVertex_Tarjan(G, t.v, s)
		}
		if (G[t.v].comp == 0) && (G[v].low > G[t.v].low) {
			G[v].low = G[t.v].low
		}
		t = t.next
	}

	if G[v].T1 == G[v].low {
		for {
			u := Pop(s)
			G[u].comp = count
			if u == v {
				break
			}
		}
		count++
	}
}

func base(G []graph, n int) {
	components := make([][]int, count)
	for i := 0; i <= n - 1; i++ {
		components[G[i].comp] = append(components[G[i].comp], i)
	}

	mark := make([][]uint, count)
	flag := make([]byte, count)
	for i := 0; i <= count - 1; i++ {
		mark[i] = make([]uint, count)
		flag[i] = 0
	}

	for i := 0; i <= n - 1; i++ {
		t := G[i].next
		for t != nil {
			if (G[i].comp != G[t.v].comp) && (mark[G[i].comp - 1][G[t.v].comp - 1] != 1) {
				mark[G[i].comp - 1][G[t.v].comp - 1], flag[G[t.v].comp] = 1, 1
			}
			t = t.next
		}
	}

	for i := 1; i <= count - 1; i++ {
		if flag[i] == 0 {
			fmt.Printf("%d\n", components[i][0])
		}
	}
}

func main() {
	var n, m int
	fmt.Scanf("%d\n", &n)
	fmt.Scanf("%d\n", &m)
	graph := make([]graph, n)

	for i := 0; i <= m - 1; i++ {
		var u, v int
		fmt.Scanf("%d %d\n", &u, &v)
		if graph[u].next == nil {
			graph[u].next = new(vertex)
			graph[u].next.v = v
			graph[u].next.next = nil
		} else {
			t := graph[u].next
			for t.next != nil {
				t = t.next
			}
			t.next = new(vertex)
			t.next.v = v
			t.next.next = nil
		}
	}

	Tarjan(graph, n)

	base(graph, n)

}