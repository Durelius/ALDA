package alda.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MyUndirectedGraph<T> implements UndirectedGraph<T> {
    private ArrayList<Node<T>> nodes;

    private static class Node<T> {
        private T data;
        private ArrayList<Edge<T>> edges;

        public Node(T data) {
            this.data = data;
            this.edges = new ArrayList<>();
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
        nodes = new ArrayList<>();
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public int getNumberOfEdges() {
        int size = 0;
        for (Node<T> node : nodes) {
            size += node.edges.size();
        }
        return size / 2;
    }

    @Override
    public boolean add(T newNode) {
        var node = new Node<T>(newNode);
        return nodes.add(node);
    }

    @Override
    public boolean connect(T data1, T data2, int cost) {
        Node<T> node1 = null;
        Node<T> node2 = null;
        for (Node<T> node : nodes) {
            if (node1 == null && node.data.equals(data1)) {
                node1 = node;
            } else if (node2 == null && node.data.equals(data2)) {
                node2 = node;
            }
        }
        if (node1 != null && node2 != null) {
            var edge1 = new Edge<T>(cost, node1, node2);
            var edge2 = new Edge<T>(cost, node2, node1);
            node1.edges.add(edge2);
            node2.edges.add(edge1);
            return true;
        }
        return false;
    }

    @Override
    public boolean isConnected(T node1, T node2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isConnected'");
    }

    @Override
    public int getCost(T node1, T node2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCost'");
    }

    @Override
    public List<T> depthFirstSearch(T start, T end) {
        Set<Node<T>> visited = new HashSet<>();
        List<T> result = new ArrayList<>();
        Node<T> startNode = null;
        Node<T> endNode = null;
        for (Node<T> node : nodes) {
            if (startNode == null && node.data.equals(start)) {
                startNode = node;
            }
            if (endNode == null && node.data.equals(end)) {
                endNode = node;
            }
        }
        if (startNode == null || endNode == null) {
            return null;
        }

        dfsRec(startNode, endNode, visited, result);

        return result;
    }

    @Override
    public List<T> breadthFirstSearch(T start, T end) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'breadthFirstSearch'");
    }

    @Override
    public UndirectedGraph<T> minimumSpanningTree() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'minimumSpanningTree'");
    }

    private void dfsRec(Node<T> start, Node<T> end, Set<Node<T>> visited, List<T> result) {
        visited.add(start);
        result.add(start.data);

        if (start.equals(end)) {
            return;
        }

        for (Edge<T> edge : start.edges) {

            if (!visited.contains(edge.to)) {
                dfsRec(edge.to, end, visited, result);
            }
        }
    }

}
