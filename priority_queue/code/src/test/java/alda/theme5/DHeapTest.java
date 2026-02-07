
package alda.theme5;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author henrikbe
 * @version JUnit 5
 */
public class DHeapTest {

  private DHeap<Integer> heap = new DHeap<Integer>(4);

  /**
   * Detta test kontrollerar att er kod implementerar en fungerande prioritetskö.
   * Detta test ska naturligtvis fungera.
   */
  @Test
  public void testFunctionality() {
    Random rnd = new Random();
    PriorityQueue<Integer> oracle = new PriorityQueue<Integer>();

    assertEquals(oracle.isEmpty(), heap.isEmpty());

    for (int n = 0; n < 1000; n++) {
      int tal = rnd.nextInt(1000);
      heap.insert(tal);
      oracle.add(tal);

      while (!heap.isEmpty() && rnd.nextBoolean()) {
        assertEquals(oracle.poll(), heap.deleteMin());
      }

      assertEquals(oracle.isEmpty(), heap.isEmpty());
    }
  }

  /**
   * Er heap ska ha två konstruktorer: en som inte tar några parametrar och som
   * skapar en vanlig binär heap, och en som tar ett argument: d. Observera att
   * detta betyder att ni måste förändra de konstruktorer som finns i
   * ursprungskoden eftersom det redan finns en konstruktor som tar ett
   * heltalsargument.
   * 
   * Den tredje konstruktorn i orginalversionen får ni göra som ni vill med, den
   * är inte viktig för uppgiften, så plocka bort den om ni inte absolut vill fixa
   * den också.
   */
  @Test
  public void testConstructors() {
    heap = new DHeap<Integer>(); // Skapar en binär heap
    heap = new DHeap<Integer>(2); // Skapar ytterligare en binär heap
    heap = new DHeap<Integer>(3); // Skapar en 3-heap, dvs en heap där varje
                                  // nod har 3 barn
  }

  @Test
  public void testTooSmallD() {
    assertThrows(IllegalArgumentException.class, () -> {
      new DHeap<Integer>(1);
    });
  }

  /**
   * Detta test förutsätter att ni lägger till en metod i heapen för att räkna ut
   * index för en nods förälder. Detta är inte nödvändigt för att lösa uppgiften,
   * så om ni vill kan ni strunta i testet. Det är dock inget vi rekommenderar
   * eftersom metoden gör problemet lättare att lösa.
   */
  @Test
  public void testParentIndex() {
    assertEquals(5, heap.parentIndex(18));
    assertEquals(5, heap.parentIndex(21));
    assertEquals(6, heap.parentIndex(22));
    assertEquals(2, heap.parentIndex(6));
    assertEquals(1, heap.parentIndex(2));
    heap = new DHeap<Integer>();
    assertEquals(1, heap.parentIndex(2));
    assertEquals(1, heap.parentIndex(3));
    assertEquals(4, heap.parentIndex(8));
    assertEquals(4, heap.parentIndex(9));
    assertEquals(6, heap.parentIndex(12));
    assertEquals(6, heap.parentIndex(13));
    heap = new DHeap<Integer>(3);
    assertEquals(6, heap.parentIndex(17));
    assertEquals(3, heap.parentIndex(9));
    assertEquals(4, heap.parentIndex(13));
    assertEquals(1, heap.parentIndex(3));
  }

  /**
   * Även detta test förutsätter att ni gör en metod för att räkna ut förälderns
   * index och kan ignorerars om ni inte gör det.
   */
  @Test
  public void testTooLowParentIndex() {
    assertThrows(IllegalArgumentException.class, () -> {
      heap.parentIndex(1);
    });
  }

  /**
   * Detta test förutsätter att ni lägger till en metod i heapen för att räkna ut
   * index för en nods första barn. Detta är inte nödvändigt för att lösa
   * uppgiften, så om ni vill kan ni strunta i testet. Det är dock inget vi
   * rekommenderar eftersom metoden gör problemet lättare att lösa.
   */
  @Test
  public void testFirstChildIndex() {
    assertEquals(2, heap.firstChildIndex(1));
    assertEquals(6, heap.firstChildIndex(2));
    assertEquals(18, heap.firstChildIndex(5));
    assertEquals(22, heap.firstChildIndex(6));
    assertEquals(26, heap.firstChildIndex(7));
    heap = new DHeap<Integer>();
    assertEquals(2, heap.firstChildIndex(1));
    assertEquals(4, heap.firstChildIndex(2));
    assertEquals(6, heap.firstChildIndex(3));
    assertEquals(8, heap.firstChildIndex(4));
    heap = new DHeap<Integer>(3);
    assertEquals(2, heap.firstChildIndex(1));
    assertEquals(5, heap.firstChildIndex(2));
    assertEquals(11, heap.firstChildIndex(4));
    assertEquals(17, heap.firstChildIndex(6));

  }

  /**
   * Även detta test förutsätter att ni gör en metod för att räkna ut det första
   * barnets index och kan ignoreras om ni inte gör det.
   */
  @Test
  public void testTooLowChildIndex() {
    assertThrows(IllegalArgumentException.class, () -> {
      heap.firstChildIndex(0);
    });
  }

  private void testValues(Integer... expected) {
    assertEquals(expected.length, heap.size());
    for (int n = 0; n < expected.length; n++)
      assertEquals(expected[n], heap.get(n + 1));
  }

  /**
   * Detta test kräver att arrayen i heapen görs tillgänglig via ett par metoder.
   * Metoden size är inte mycket att säga om, den borde antagligen funnits i
   * orginalet. get däremot bryter inkapslingen och är bara till för att vi ska
   * kunna testa. Detta är naturligtvis förkastligt, men ibland nödvändigt. Den
   * högsta skyddsnivån (=bästa) get kan ha är deafult-nivån. Detta test måste
   * fungera, så ni måste lägga till metoderna:
   * 
   * public int size(){ return currentSize; }
   * 
   * AnyType get(int index){ return array[index]; }
   */
  @Test
  public void testContent() {
    testValues();
    heap.insert(17);
    testValues(17);
    heap.insert(23);
    testValues(17, 23);
    heap.insert(5);
    testValues(5, 23, 17);
    heap.insert(12);
    testValues(5, 23, 17, 12);
    heap.insert(100);
    heap.insert(51);
    heap.insert(52);
    testValues(5, 23, 17, 12, 100, 51, 52);
    heap.insert(4);
    testValues(4, 5, 17, 12, 100, 51, 52, 23);
    heap.insert(70);
    testValues(4, 5, 17, 12, 100, 51, 52, 23, 70);
    heap.insert(10);
    testValues(4, 5, 10, 12, 100, 51, 52, 23, 70, 17);
    heap.insert(1);
    testValues(1, 5, 4, 12, 100, 51, 52, 23, 70, 17, 10);

    System.err.println(heap);
    assertEquals(1, (int) heap.deleteMin());
    System.err.println(heap);
    testValues(4, 5, 10, 12, 100, 51, 52, 23, 70, 17);
    assertEquals(4, (int) heap.deleteMin());
    testValues(5, 17, 10, 12, 100, 51, 52, 23, 70);
    assertEquals(5, (int) heap.deleteMin());
    testValues(10, 17, 70, 12, 100, 51, 52, 23);
  }

}
