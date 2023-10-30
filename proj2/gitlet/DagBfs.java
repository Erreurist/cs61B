package gitlet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DagBfs {
    static class Graph {
        int vertices;
        List<Integer>[] adjList;

        Graph(int vertices) {
            this.vertices = vertices;
            adjList = new ArrayList[vertices];
            for (int i = 0; i < vertices; i++) {
                adjList[i] = new ArrayList<>();
            }
        }

        public void addEdge(int source, int dest) {
            adjList[source].add(dest);
        }

        public boolean[] bfsTraversal(int startVertex) {
            boolean[] visited = new boolean[vertices];
            boolean[] res = new boolean[vertices];
            Queue<Integer> queue = new LinkedList<>();

            visited[startVertex] = true;
            queue.offer(startVertex);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                res[current] = true;

                for (int neighbor : adjList[current]) {
                    res[neighbor] = true;
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.offer(neighbor);
                    }
                }
            }
            res[startVertex] = true;
            return res;
        }

        public int findSplit(int startVertex, boolean[] reachable) {
            boolean[] visited = new boolean[vertices];
            Queue<Integer> queue = new LinkedList<>();

            visited[startVertex] = true;
            queue.offer(startVertex);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                if (reachable[current]) {
                    return current;
                }

                for (int neighbor : adjList[current]) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.offer(neighbor);
                    }
                }
            }
            return -1;
        }
    }

}
