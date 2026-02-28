package alda.t9;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanCoder {

    private static class Node implements Comparable<Node> {
        Node(char character, int frequency) {
            this.frequency = frequency;
            this.character = character;
        }

        Node(int frequency) {
            this.character = null;
            this.frequency = frequency;
        }

        public void IncFrequency() {
            frequency++;
        }

        @Override
        public int compareTo(Node other) {
            if (this.character == null && other.character != null)
                return 1;
            if (this.character != null && other.character == null)
                return -1;
            if (this.character != null && other.character != null) {
                int cmp = Character.compare(this.character, other.character);
                if (cmp != 0)
                    return cmp;
            }
            return Integer.compare(this.frequency, other.frequency);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Node))
                return false;

            Node other = (Node) obj;

            return this.frequency == other.frequency &&
                    Objects.equals(this.character, other.character);
        }

        public int hashCode() {
            return Objects.hash(character, frequency);
        }

        private int frequency;
        private final Character character;
        private Node left;
        private Node right;
    }

    public EncodedMessage<?, ?> encode(String msg) {
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> {
            if (a.frequency < b.frequency)
                return -1;
            return 1;
        });
        HashMap<Character, Node> map = new HashMap<>();
        for (int i = 0; i < msg.length(); i++) {
            var character = msg.charAt(i);
            var node = map.get(character);
            if (node != null)
                node.IncFrequency();
            else
                map.put(character, new Node(character, 1));
        }
        for (var entry : map.entrySet()) {
            pq.add(entry.getValue());
        }
        while (pq.size() > 1) {
            var firstMin = pq.poll();
            var secondMin = pq.poll();

            var newNode = new Node(firstMin.frequency + secondMin.frequency);
            newNode.left = firstMin;
            newNode.right = secondMin;
            pq.add(newNode);
        }
        var root = pq.poll();
        HashMap<Character, String> headerMap = new HashMap<>();
        fillHeaderMap(root, headerMap, "");
        String encodedMsg = encode(headerMap, msg);

        return new EncodedMessage<Node, String>(root, encodedMsg);
    }

    private void fillHeaderMap(Node root, HashMap<Character, String> headerMap, String curr) {
        if (root == null)
            return;

        if (root.left == null && root.right == null) {
            headerMap.put(root.character, curr);
            return;
        }

        fillHeaderMap(root.left, headerMap, curr + '0');
        fillHeaderMap(root.right, headerMap, curr + '1');
    }

    // private void decodeTree(Node root, Node node, Queue<Character> encoded,
    // StringBuilder decoded) {
    // if (root == null || node == null)
    // return;
    //
    // if (node.left == null && node.right == null) {
    // decoded.append(node.character);
    // if (!encoded.isEmpty())
    // decodeTree(root, root, encoded, decoded);
    // return;
    // }
    // if (encoded.isEmpty())
    // return;
    // var curr = encoded.poll();
    // if (curr == null)
    // return;
    //
    // decodeTree(root, curr == '0' ? node.left : node.right, encoded, decoded);
    // }
    //
    
    private String decodeTree(Node root, String encoded) {
        StringBuilder decoded = new StringBuilder();
        Node current = root;
        for (int i = 0; i < encoded.length(); i++) {
            char bit = encoded.charAt(i);
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                decoded.append(current.character);
                current = root;
            }
        }
        return decoded.toString();
    }

    private String encode(HashMap<Character, String> headerMap, String raw) {
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            encoded.append(headerMap.get(raw.charAt(i)));
        }
        return encoded.toString();
    }

    public String decode(EncodedMessage<?, ?> msg) {
        var header = (Node) msg.header;
        String message = (String) msg.message;

        return decodeTree(header, message);
    }
}
