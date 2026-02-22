
// Ändra inte på paketet
package alda.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author henrikbe
 * @version JUnit 5
 */
public class SmallTest {

    private UndirectedGraph<String> graph = new MyUndirectedGraph<>();

    private static final String[] STANDARD_NODES = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };

    private void add(String... nodes) {
        for (String node : nodes) {
            assertTrue(graph.add(node), "Unable to add node " + node);
        }
    }

    private void connect(String node1, String node2, int cost) {
        assertTrue(graph.connect(node1, node2, cost));
        assertEquals(cost, graph.getCost(node1, node2));
        assertEquals(cost, graph.getCost(node2, node1));
    }

    private void addExampleNodes() {
        add(STANDARD_NODES);
    }

    @Test
    public void simpleTest() {
        createExampleGraph();
    }

    private void createExampleGraph() {
        addExampleNodes();

        connect("A", "A", 1);
        connect("A", "G", 3);
        connect("G", "B", 28);
        connect("B", "F", 5);
        connect("F", "F", 3);
        connect("F", "H", 1);
        connect("H", "D", 1);
        connect("H", "I", 3);
        connect("D", "I", 1);
        connect("B", "D", 2);
        connect("B", "C", 3);
        connect("C", "D", 5);
        connect("E", "C", 2);
        connect("E", "D", 2);
        connect("J", "D", 5);

    }
}
