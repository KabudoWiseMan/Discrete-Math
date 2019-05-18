/**
 * Created by vsevolodmolchanov on 23.03.17.
 */
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

class Value {
    private Vertex v;
    private int weight;

    public Value(Vertex v, int w) {
        this.v = v;
        weight = w;
    }

    public Vertex getV() {
        return v;
    }

    public int getWeight() {
        return weight;
    }

}

class Vertex {
    private int index, key;
    private ArrayList<Value> value;

    public Vertex() {
        value = new ArrayList<>();
        index = -1;
        key = 0;
    }

    public void updateVertex(int i, int k) {
        index = i;
        key = k;
    }

    public int getIndex() {
        return index;
    }

    public void addIndex() {
        index = -2;
    }

    public int getKey() {
        return key;
    }

    public void addValue(Value v) {
        value.add(v);
    }

    public ArrayList<Value> getValue() {
        return value;
    }
}

public class Prim {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();

        int length = 0;

        ArrayList<Vertex> vertexes = new ArrayList<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.getKey(), b.getKey()));

        for(int i = 0; i <= n - 1; i++) {
            vertexes.add(new Vertex());
        }

        for(int i = 0; i <= m - 1; i++) {
            int u = in.nextInt();
            int v = in.nextInt();
            int len = in.nextInt();
            vertexes.get(u).addValue(new Value(vertexes.get(v), len));
            vertexes.get(v).addValue(new Value(vertexes.get(u), len));
        }

        queue.add(vertexes.get(0));

        while(!queue.isEmpty()) {
            Vertex v = queue.poll();
            v.addIndex();
            for(Value va : v.getValue()) {
                Vertex u = va.getV();
                if(u.getIndex() == -1) {
                    u.updateVertex(1, va.getWeight());
                    queue.add(u);
                } else if(u.getIndex() != -2 && va.getWeight() < u.getKey()) {
                    queue.remove(u);
                    u.updateVertex(1, va.getWeight());
                    queue.add(u);
                }
            }
            length += v.getKey();
        }

        System.out.println(length);
    }
}
