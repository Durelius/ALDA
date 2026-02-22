package alda.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

class MyUndirectedGraph<T> implements UndirectedGraph<T> {
    private Map<T, Node<T>> nodes;

    private static class Node<T> {
        private T data;
        private ArrayList<Edge<T>> edges;

        public Node(T data) {
            this.data = data;
            this.edges = new ArrayList<>();
        }

        public T getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Node<?> other))
                return false;
            return Objects.equals(data, other.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }
    }

    private static class Edge<T> {
        public final Node<T> from;
        public final Node<T> to;
        private int cost;

        Edge(int cost, Node<T> from, Node<T> to) {
            this.cost = cost;
            this.from = from;
            this.to = to;
        }

        public int getCost() {
            return cost;
        }

    }

    MyUndirectedGraph() {
        nodes = new HashMap<>();
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public int getNumberOfEdges() {
        int size = 0;

        for (var entry : nodes.entrySet()) {
            var v = entry.getValue();
            size += v.edges.size();
        }
        return size / 2;
    }

    @Override
    public boolean add(T newNode) {
        var node = new Node<T>(newNode);
        return nodes.putIfAbsent(newNode, node) == null;
    }

    @Override
    public boolean connect(T data1, T data2, int cost) {
        if (cost <= 0)
            return false;
        Node<T> node1 = nodes.get(data1);
        Node<T> node2 = nodes.get(data2);
        if (node1 == null || node2 == null)
            return false;

        Edge<T> existing1 = node1.edges.stream()
                .filter(e -> e.to.equals(node2))
                .findFirst()
                .orElse(null);

        Edge<T> existing2 = node2.edges.stream()
                .filter(e -> e.to.equals(node1))
                .findFirst()
                .orElse(null);

        if (existing1 != null && existing2 != null) {
            existing1.cost = cost;
            existing2.cost = cost;
            return true;
        }

        var edge1 = new Edge<T>(cost, node1, node2);
        var edge2 = new Edge<T>(cost, node2, node1);
        node1.edges.add(edge1);
        node2.edges.add(edge2);
        return true;

    }

    @Override
    public boolean isConnected(T node1, T node2) {
        var path = depthFirstSearch(node1, node2);
        return path != null && path.size() > 0;
    }

    @Override
    public int getCost(T node1, T node2) {
        Node<T> n1 = nodes.get(node1);
        Node<T> n2 = nodes.get(node2);
        if (n1 == null || n2 == null)
            return -1;
        for (Edge<T> edge : n1.edges) {
            if (edge.to.equals(n2)) {
                return edge.getCost();
            }
        }
        return -1;
    }

    @Override
    public List<T> depthFirstSearch(T start, T end) {
        Set<Node<T>> visited = new HashSet<>();
        List<Node<T>> path = new ArrayList<>();
        Node<T> startNode = nodes.get(start);
        Node<T> endNode = nodes.get(end);
        var empty = new ArrayList<T>();
        if (startNode == null || endNode == null) {
            return empty;
        }

        if (dfsHelper(startNode, endNode, visited, path)) {
            return path.stream().map(Node<T>::getData).toList();
        }

        return empty;
    }

    @Override
    public List<T> breadthFirstSearch(T start, T end) {
        Node<T> startNode = nodes.get(start);
        Node<T> endNode = nodes.get(end);

        if (startNode == null || endNode == null)
            return Collections.emptyList();

        Queue<Node<T>> queue = new LinkedList<>();
        Map<Node<T>, Node<T>> parent = new HashMap<>();
        Set<Node<T>> visited = new HashSet<>();

        queue.add(startNode);
        visited.add(startNode);
        parent.put(startNode, null);

        while (!queue.isEmpty()) {
            Node<T> current = queue.poll();

            if (current.equals(endNode))
                break;

            for (Edge<T> edge : current.edges) {
                Node<T> neighbor = edge.to;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<T> path = new ArrayList<>();
        if (!parent.containsKey(endNode))
            return path; // No path found

        for (Node<T> at = endNode; at != null; at = parent.get(at)) {
            path.add(at.getData());
        }

        Collections.reverse(path);
        return path;
    }

    @Override
    public UndirectedGraph<T> minimumSpanningTree() {
        if (nodes.isEmpty())
            return new MyUndirectedGraph<>();
        MyUndirectedGraph<T> minTree = new MyUndirectedGraph<>();
        nodes.values().forEach(n -> minTree.add(n.getData()));
        Set<Node<T>> visited = new HashSet<>();
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost));
        Node<T> startNode = nodes.values().iterator().next();
        visited.add(startNode);
        for (Edge<T> e : startNode.edges) {
            priorityQueue.add(e);
        }
        while (!priorityQueue.isEmpty()) {
            Edge<T> edge = priorityQueue.poll();
            Node<T> from = edge.from;
            Node<T> to = edge.to;

            Node<T> newNode = null;
            Node<T> knownNode = null;
            if (visited.contains(from) && !visited.contains(to)) {
                newNode = to;
                knownNode = from;
            } else if (!visited.contains(from) && visited.contains(to)) {
                newNode = from;
                knownNode = to;
            } else {
                continue;
            }

            minTree.connect(knownNode.getData(), newNode.getData(), edge.getCost());
            visited.add(newNode);
            for (Edge<T> e : newNode.edges) {
                if (!visited.contains(e.to)) {
                    priorityQueue.add(e);
                }
            }
        }
        return minTree;
    }

    // helpers

    private boolean dfsHelper(
            Node<T> node,
            Node<T> target,
            Set<Node<T>> visited,
            List<Node<T>> path) {

        visited.add(node);
        path.add(node);
        if (node.equals(target)) {
            return true;
        }

        for (Edge<T> edge : node.edges) {
            if (!visited.contains(edge.to)) {
                if (dfsHelper(edge.to, target, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1); // backtrack
        return false;
    }

}
