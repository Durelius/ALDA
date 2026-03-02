//Wilhelm Durelius widu7139
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

        Node(T data) {
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
        private Node<T> from;
        private Node<T> to;
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
    public boolean connect(T first, T second, int cost) {
        if (cost <= 0)
            return false;
        Node<T> firstNode = nodes.get(first);
        Node<T> secondNode = nodes.get(second);
        if (firstNode == null || secondNode == null)
            return false;

        Edge<T> firstExist = firstNode.edges.stream()
                .filter(e -> e.to.equals(secondNode))
                .findFirst()
                .orElse(null);

        Edge<T> secondExist = secondNode.edges.stream()
                .filter(e -> e.to.equals(firstNode))
                .findFirst()
                .orElse(null);

        if (firstExist != null && secondExist != null) {
            firstExist.cost = cost;
            secondExist.cost = cost;
            return true;
        }

        var firstEdge = new Edge<T>(cost, firstNode, secondNode);
        var secondEdge = new Edge<T>(cost, secondNode, firstNode);
        firstNode.edges.add(firstEdge);
        secondNode.edges.add(secondEdge);
        return true;

    }

    @Override
    public boolean isConnected(T first, T second) {
        var path = depthFirstSearch(first, second);
        return path != null && path.size() > 0;
    }

    @Override
    public int getCost(T first, T second) {
        Node<T> firstNode = nodes.get(first);
        Node<T> secondNode = nodes.get(second);
        if (firstNode == null || secondNode == null)
            return -1;
        for (Edge<T> edge : firstNode.edges) {
            if (edge.to.equals(secondNode)) {
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
