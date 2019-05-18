import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;


class Vertex {
    private int vert, x, y;
    private int dist;

    Vertex(int vert, int x, int y) {
        this.vert = vert;
        this.x = x;
        this.y = y;
        dist = 0;
    }

    public int getVertex() {
        return vert;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.valueOf(dist);
    }
}

public class MapRoute {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();

        ArrayList<ArrayList<Vertex>> vertexes = new ArrayList<ArrayList<Vertex>>();

        for (int i = 0; i <= n - 1; i++) {
            ArrayList<Vertex> inner = new ArrayList<>();
            for (int j = 0; j <= n - 1; j++) {
                inner.add(new Vertex(in.nextInt(), i, j));
            }
            vertexes.add(inner);
        }

        Dijkstra(vertexes, n);

//        System.out.println(vertexes.get(n - 1).get(n - 1).getDist());
        System.out.println(vertexes);
    }

    private static void Dijkstra(ArrayList<ArrayList<Vertex>> vertexes, int n) {
        PriorityQueue<Vertex> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.getDist(), a.getDist()));
        vertexes.get(0).get(0).setDist(vertexes.get(0).get(0).getVertex());
        queue.add(vertexes.get(0).get(0));
        while(!queue.isEmpty()) {
            Vertex v = queue.poll();
            int x = v.getX();
            int y = v.getY();
            if (x != n - 1) {
                Vertex u = vertexes.get(x + 1).get(y);
                if (Relax(v, u, u.getVertex())) {
                    queue.add(u);
                }
            }
            if (x != 0) {
                Vertex u = vertexes.get(x - 1).get(y);
                if (Relax(v, u, u.getVertex())) {
                    queue.add(u);
                }
            }
            if (y != n - 1) {
                Vertex u = vertexes.get(x).get(y + 1);
                if (Relax(v, u, u.getVertex())) {
                    queue.add(u);
                }
            }
            if (y != 0) {
                Vertex u = vertexes.get(x).get(y - 1);
                if (Relax(v, u, u.getVertex())) {
                    queue.add(u);
                }
            }
        }
    }

    private static boolean Relax(Vertex u, Vertex v, int w) {
        System.out.println(u.getDist());
        System.out.println(v.getDist());
        System.out.println(w);
        boolean changed = u.getDist() + w > v.getDist();
        if (changed) {
            v.setDist(u.getDist() + w);
        }
        return changed;
    }
}