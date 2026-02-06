/*
 * You are encouraged to use packages for the assignments, but it's not a requirement.
 * However, when submitting any code in ilearn through VPL you *MUST* remove the package.
 */
package alda;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.*;

/**
 * Denna klass testar bara de funktionella kraven. Eventuellt kommer det att
 * tillkomma test på en del av de icke-funktionella. Det kan alltså vara värt
 * att kontrollera om det kommit uppdaterade testfall någon dag innan deadline.
 * 
 * @author henrikbe
 * @version JUnit 5
 */
public class BinarySearchTreeTest {

  private BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();

  @Test
  public void testEmptyTree() {
    tree.clear();
    assertEquals(0, tree.size());
    assertEquals(-1, tree.depth());
    assertEquals("[]", tree.toString());
  }

  @BeforeEach
  public void setUp() throws Exception {
    assertTrue(tree.add(5));
    assertTrue(tree.add(4));
    assertTrue(tree.add(2));
    assertTrue(tree.add(3));
    assertTrue(tree.add(6));
    assertTrue(tree.add(1));
  }

  @Test
  public void testAddUnique() {
    for (int n = 1; n <= 6; n++) {
      assertTrue(tree.contains(n));
    }
  }

  @Test
  public void testSize() {
    assertEquals(6, tree.size());
  }

  @Test
  public void testDepth() {
    assertEquals(3, tree.depth());
  }

  @Test
  public void testToString() {
    assertEquals("[1, 2, 3, 4, 5, 6]", tree.toString());
  }

  @Test
  public void testAddDuplicates() {
    for (int n = 1; n <= 6; n += 2)
      assertFalse(tree.add(n));
  }

  @Test
  public void testRemoveExistingLeaf() {
    assertTrue(tree.remove(1));
    assertEquals(5, tree.size());
    assertEquals("[2, 3, 4, 5, 6]", tree.toString());
  }

  @Test
  public void testRemoveExistingMiddleItemWithEmptyRightChild() {
    assertTrue(tree.remove(4));
    assertEquals(5, tree.size());
    assertEquals("[1, 2, 3, 5, 6]", tree.toString());
  }

  @Test
  public void testRemoveExistingMiddleItemWithEmptyLeftChild() {
    assertTrue(tree.add(7));
    assertTrue(tree.remove(6));
    assertEquals(6, tree.size());
    assertEquals("[1, 2, 3, 4, 5, 7]", tree.toString());
  }

  @Test
  public void testRemoveExistingMiddleItemWithTwoChildren() {
    assertTrue(tree.remove(2));
    assertEquals(5, tree.size());
    assertEquals("[1, 3, 4, 5, 6]", tree.toString());
  }

  @Test
  public void testRemoveRoot() {
    assertTrue(tree.remove(5));
    assertEquals(5, tree.size());
    assertEquals("[1, 2, 3, 4, 6]", tree.toString());
  }

  @Test
  public void testRandomAddAndRemove() {
    Random rnd = new Random(10);

    SortedSet<Integer> oracle = new TreeSet<Integer>();
    for (int n = 1; n <= 6; n++)
      oracle.add(n);

    for (int n = 0; n < 10000; n++) {
      int toAdd = rnd.nextInt(10);
      assertEquals(oracle.add(toAdd), tree.add(toAdd));
      int toRemove = rnd.nextInt(10);

      System.out.println("Oracle size: " + oracle.size());
      System.out.println("Tree size: " + tree.size());
      System.out.println("Oracle: " + oracle.toString());
      System.out.println("Tree: " + tree.toString());
      System.out.println("To remove: " + toRemove);
      assertEquals(oracle.remove(toRemove), tree.remove(toRemove));
      int checkExists = rnd.nextInt(10);
      assertEquals(oracle.contains(checkExists), tree.contains(checkExists));
      assertEquals(oracle.size(), tree.size());
      assertEquals(oracle.toString(), tree.toString());
    }
  }

  @Test
  public void testOtherType() {
    BinarySearchTree<String> stringTree = new BinarySearchTree<String>();
    stringTree.add("D");
    stringTree.add("A");
    stringTree.add("C");
    stringTree.add("A");
    stringTree.add("B");
    assertEquals(4, stringTree.size());
    assertTrue(stringTree.contains("C"));
    stringTree.remove("C");
    assertFalse(stringTree.contains("C"));
  }

}
