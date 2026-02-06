package alda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Random;

/**
 * @author henrikbe
 * @version JUnit 5
 */
public class HashTableTest {

  private void testFunctionality(ProbingHashTable<Integer> sut) {
    final int MAX_VALUE = 500;

    Random rnd = new Random();
    HashSet<Integer> oracle = new HashSet<>();

    for (int n = 0; n < 5000; n++) {
      assertEquals(oracle.size(), sut.size());

      int in = rnd.nextInt(MAX_VALUE);
      assertEquals(oracle.add(in), sut.insert(in));

      int check = rnd.nextInt(MAX_VALUE);
      assertEquals(oracle.contains(check), sut.contains(check));

      do {
        int out = rnd.nextInt(MAX_VALUE);
        assertEquals(oracle.remove(out), sut.remove(out));
      } while (rnd.nextBoolean());
    }
  }

  @Test
  public void testFunctionalityOfQuadraticProbingHashTable() {
    testFunctionality(new QuadraticProbingHashTable<>());
  }

  @Test
  public void testFunctionalityOfLinearProbingHashTable() {
    testFunctionality(new LinearProbingHashTable<>());
  }

  @Test
  public void testFunctionalityOfDoubleHashingProbingHashTable() {
    testFunctionality(new DoubleHashingProbingHashTable<>());
  }

  private static class HashItem {
    private final int hashCode;
    private int value;

    public HashItem(int hashCode, int value) {
      this.hashCode = hashCode;
      this.value = value;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return String.format("%d", value);
    }
  }

  public void testClustering(ProbingHashTable<HashItem> sut, String expected) {
    for (int n = 1; n <= 40; n++) {
      sut.insert(new HashItem(5, n));
    }
    // Ta bort kommentaren om du vill se hur datat fördelar sig
    // System.out.println(sut);

    // Enkelt men klumpigt och väldigt känsligt sätt att kontrollera att saker
    // hamnar på rätt ställe
    assertEquals(expected, sut.toString());
  }

  @Test
  public void testClusteringOfQuadraticProbingHashTable() {
    testClustering(new QuadraticProbingHashTable<>(),
        "[null, 21, null, null, 11, 1, 2, null, null, 3, null, 40, null, null, 4, null, null, null, 36, 33, null, 5, null, null, 26, 12, 19, 28, 16, 24, 6, null, null, null, null, 39, null, null, 30, null, null, 7, 22, null, null, null, null, null, 13, null, 35, null, null, null, 8, null, null, 32, null, 17, null, 38, null, 20, null, null, null, null, null, 9, null, null, null, 14, null, 27, 25, null, null, null, null, null, 29, null, 34, 23, 10, null, null, 37, null, null, 18, null, null, null, null, 31, null, null, 15]");
  }

  @Test
  public void testClusteringOfLinearProbingHashTable() {
    testClustering(new LinearProbingHashTable<>(),
        "[null, null, null, null, null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
  }

  @Test
  public void testClusteringOfDoubleHashingProbingHashTable() {
    testClustering(new DoubleHashingProbingHashTable<>(),
        "[24, null, 35, null, null, 1, null, 12, null, 23, null, 34, null, null, null, null, 11, null, 22, null, 33, null, null, null, null, 10, null, 21, null, 32, null, null, null, null, 9, null, 20, null, 31, null, null, null, null, 8, null, 19, null, 30, null, null, null, null, 7, null, 18, null, 29, null, 40, null, null, 6, null, 17, null, 28, null, 39, null, null, 5, null, 16, null, 27, null, 38, null, null, 4, null, 15, null, 26, null, 37, null, null, 3, null, 14, null, 25, null, 36, null, null, 2, null, 13, null]");
  }

  @Test
  public void testBookAsHashKey() {
    Book secondEdition = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss",
        "0321373197", "Long string containg entire book content.", 550);
    Book secondEditionSecondCopy = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss",
        "0321373197", "Long string containg entire book content.", 550);
    Book thirdEdition = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss", "0273752111",
        "Long string containg entire book content.", 650);
    Book thirdEditionSecondCopy = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss",
        "0273752111", "Long string containg entire book content.", 650);

    ProbingHashTable<Book> books = new QuadraticProbingHashTable<>();

    assertTrue(books.insert(secondEdition));
    assertTrue(books.insert(thirdEdition));

    assertTrue(books.contains(secondEditionSecondCopy));
    assertTrue(books.contains(thirdEditionSecondCopy));

    thirdEdition.setPrice(675);
    assertTrue(books.contains(thirdEdition));

  }

  @Test
  public void testMutatedPrice() {
    Book b = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss", "0321373197",
        "Long string containg entire book content.", 550);
    Book b2 = new Book("Data Structures and Algorithm Analysis in Java", "Mark Allen Weiss", "0321373197",
        "Long string containg entire book content.", 550);
    int h1 = b.hashCode();
    b.setPrice(600);
    int h2 = b.hashCode();
    assertEquals(h1, h2);
    assertEquals(b, b2);
  }

}
