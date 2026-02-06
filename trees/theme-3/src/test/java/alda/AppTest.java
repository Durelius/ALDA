package alda;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class AppTest {

  private BinarySearchTree<Integer> tree;

  @BeforeEach
  void setUp() {
    tree = new BinarySearchTree<>();
  }

  @Test
  void testAddAndSize() {
    assertEquals(0, tree.size());

    assertTrue(tree.add(5));
    assertEquals(1, tree.size());

    assertTrue(tree.add(3));
    assertTrue(tree.add(7));
    assertEquals(3, tree.size());

    // duplicate adds
    assertFalse(tree.add(5));
    assertFalse(tree.add(3));
    assertEquals(3, tree.size());

    assertEquals("[3, 5, 7]", tree.toString());
  }

  @Test
  void testRemoveExistingElements() {
    tree.add(5);
    tree.add(3);
    tree.add(7);
    tree.add(1);
    tree.add(9);

    assertEquals(5, tree.size());

    assertTrue(tree.remove(3));
    assertEquals(4, tree.size());
    assertEquals("[1, 5, 7, 9]", tree.toString());

    assertTrue(tree.remove(5)); // remove root
    assertEquals(3, tree.size());
    assertEquals("[1, 7, 9]", tree.toString());

    assertTrue(tree.remove(1)); // remove leaf
    assertEquals(2, tree.size());
    assertEquals("[7, 9]", tree.toString());
  }

  @Test
  void testRemoveNonExistingElements() {
    tree.add(2);
    tree.add(4);
    tree.add(6);

    assertEquals(3, tree.size());

    assertFalse(tree.remove(1));
    assertFalse(tree.remove(3));
    assertFalse(tree.remove(10));

    // size and structure unchanged
    assertEquals(3, tree.size());
    assertEquals("[2, 4, 6]", tree.toString());
  }

  @Test
  void testContains() {
    tree.add(10);
    tree.add(5);
    tree.add(15);

    assertTrue(tree.contains(10));
    assertTrue(tree.contains(5));
    assertTrue(tree.contains(15));

    assertFalse(tree.contains(0));
    assertFalse(tree.contains(7));
    assertFalse(tree.contains(20));
  }

  @Test
  void testRemoveUntilEmpty() {
    tree.add(1);
    tree.add(2);
    tree.add(3);

    assertEquals(3, tree.size());

    assertTrue(tree.remove(1));
    assertTrue(tree.remove(2));
    assertTrue(tree.remove(3));

    assertEquals(0, tree.size());
    assertEquals("[]", tree.toString());

    // removing from empty tree
    assertFalse(tree.remove(1));
    assertEquals(0, tree.size());
  }

  @Test
  void testAddRemoveInterleaved() {
    assertTrue(tree.add(4));
    assertTrue(tree.add(2));
    assertTrue(tree.add(6));
    assertEquals("[2, 4, 6]", tree.toString());

    assertTrue(tree.remove(2));
    assertEquals("[4, 6]", tree.toString());

    assertTrue(tree.add(1));
    assertTrue(tree.add(3));
    assertEquals("[1, 3, 4, 6]", tree.toString());

    assertFalse(tree.add(4)); // duplicate
    assertEquals(4, tree.size());
  }
}
