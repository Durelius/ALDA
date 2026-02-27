package alda.t9.redblack;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        for (int i = 1; i <= 10; i++) {
            tree.insert(i);
        }
        tree.remove(3);
    }
}
