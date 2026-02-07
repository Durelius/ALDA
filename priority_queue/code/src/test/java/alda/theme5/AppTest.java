package alda.theme5;

import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for a 1-indexed MIN D-Heap.
 * Index 0 is unused.
 */
@Disabled
public class AppTest {

  /**
   * BASIC INVARIANT:
   * After inserts, findMin() must return the smallest element.
   *
   * Catches:
   * - Broken percolate-up
   * - Wrong parent index calculation
   * - findMin() not reading index 1
   */
  @Test
  void insertShouldUpdateMin() {
    var heap = new DHeap<Integer>(3);

    heap.insert(10);
    heap.insert(4);
    heap.insert(7);
    heap.insert(1);

    int min = heap.findMin();

    assertEquals(1, min,
        "findMin() returned " + min +
            ", expected 1 after inserting [10, 4, 7, 1]");
  }

  /**
   * MOST IMPORTANT TEST.
   * Insert many values, then delete all.
   * Values must come out in strictly increasing order.
   *
   * Catches:
   * - Wrong child index math (very common in d-heaps)
   * - Broken percolate-down
   * - Last element not moved to root
   * - Heap corruption after deleteMin
   */
  @Test
  void deleteMinShouldReturnSortedOrder() {
    var heap = new DHeap<Integer>(3);

    for (int i = 1; i < 200; i++) {
      heap.insert(i);
    }

    int initialSize = heap.size();

    for (int expected = 1; expected < 200; expected++) {
      int actual = heap.deleteMin();

      assertEquals(expected, actual,
          "deleteMin() returned " + actual +
              ", expected " + expected +
              " (heap size before delete was " +
              (initialSize - expected + 1) + ")");
    }

    assertTrue(heap.isEmpty(),
        "Heap should be empty after deleting all elements");
  }

  /**
   * MIXED OPERATIONS.
   * Insert and delete interleaved.
   *
   * Catches:
   * - Heap only working for bulk insert → bulk delete
   * - Percolate logic assuming a full tree
   * - State corruption after deleteMin
   */
  @Test
  void mixedInsertAndDeleteShouldWork() {
    var heap = new DHeap<Integer>(3);

    heap.insert(5);
    heap.insert(3);
    heap.insert(8);

    assertEquals(3, heap.deleteMin(),
        "Expected min to be 3 after inserting [5, 3, 8]");

    heap.insert(1);
    heap.insert(4);

    assertEquals(1, heap.deleteMin(), "Expected min to be 1");
    assertEquals(4, heap.deleteMin(), "Expected min to be 4");
    assertEquals(5, heap.deleteMin(), "Expected min to be 5");
  }

  /**
   * DUPLICATES.
   *
   * Catches:
   * - Using < instead of <= in comparisons
   * - Infinite percolation loops
   * - Skipping children with equal values
   */
  @Test
  void heapShouldHandleDuplicateValues() {
    var heap = new DHeap<Integer>(3);

    heap.insert(5);
    heap.insert(5);
    heap.insert(5);
    heap.insert(5);

    for (int i = 1; i <= 4; i++) {
      int val = heap.deleteMin();
      assertEquals(5, val,
          "Expected duplicate value 5, but got " +
              val + " on deletion " + i);
    }

    assertTrue(heap.isEmpty(),
        "Heap should be empty after deleting all duplicates");
  }

  /**
   * SIZE ACCOUNTING.
   *
   * Catches:
   * - Forgetting to decrement size
   * - Incrementing size at wrong time
   * - Ghost elements left in array
   */
  @Test
  void sizeShouldTrackCorrectly() {
    var heap = new DHeap<Integer>(3);

    assertEquals(0, heap.size(),
        "New heap should have size 0");

    heap.insert(1);
    heap.insert(2);
    heap.insert(3);

    assertEquals(3, heap.size(),
        "Size should be 3 after 3 inserts");

    heap.deleteMin();

    assertEquals(2, heap.size(),
        "Size should be 2 after one deleteMin");
  }

  /**
   * EMPTY HEAP BEHAVIOR.
   *
   * Catches:
   * - Returning null
   * - Accessing index 1 when empty
   * - Silent undefined behavior
   */
  @Test
  void deleteMinOnEmptyHeapShouldThrow() {
    var heap = new DHeap<Integer>(3);

    assertThrows(UnderflowException.class,
        heap::deleteMin,
        "deleteMin() on empty heap should throw NoSuchElementException");
  }

  /**
   * REFERENCE TEST (GOD MODE).
   * Compares behavior against Java's PriorityQueue.
   *
   * Catches:
   * - Any subtle ordering bug
   * - Any edge case not covered above
   */
  @Test
  void randomOperationsShouldMatchPriorityQueue() {
    var heap = new DHeap<Integer>(3);
    var pq = new PriorityQueue<Integer>();
    var rand = new Random(42);

    for (int i = 0; i < 1000; i++) {
      int val = rand.nextInt(1000);
      heap.insert(val);
      pq.add(val);
    }

    while (!pq.isEmpty()) {
      int expected = pq.poll();
      int actual = heap.deleteMin();

      assertEquals(expected, actual,
          "Mismatch vs PriorityQueue: expected " +
              expected + " but got " + actual);
    }

    assertTrue(heap.isEmpty(),
        "Heap should be empty after draining via random test");
  }
}
