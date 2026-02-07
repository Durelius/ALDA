
package alda.theme5;

//DHeap class
//
//CONSTRUCTION: with optional capacity (that defaults to 100)
//            or an array containing initial items
//
//******************PUBLIC OPERATIONS*********************
//void insert( x )       --> Insert x
//Comparable deleteMin( )--> Return and remove smallest item
//Comparable findMin( )  --> Return smallest item
//boolean isEmpty( )     --> Return true if empty; else false
//void makeEmpty( )      --> Remove all items
//******************ERRORS********************************
//Throws UnderflowException as appropriate

/**
 * Implements a DHeap.
 * Note that all "matching" is based on the compareTo method.
 * 
 * @author Wilhelm Durelius
 */
public class DHeap<AnyType extends Comparable<? super AnyType>> {
  /**
   * Construct the DHeap.
   */
  public DHeap() {
    this(DEFAULT_CHILDREN);
  }

  /**
   * Construct the DHeap.
   * 
   * @param d the amount of children for each node.
   */
  @SuppressWarnings("unchecked")
  public DHeap(int d) throws IllegalArgumentException {
    if (d < 2)
      throw new IllegalArgumentException();
    currentSize = 0;
    this.d = d;
    array = (AnyType[]) new Comparable[DEFAULT_CAPACITY + 1];
  }

  /**
   * Construct the DHeap given an array of items.
   */
  public DHeap(AnyType[] items) {
    this(items, DEFAULT_CHILDREN);
  }

  /**
   * Construct the DHeap given an array of items and custom children size.
   */
  @SuppressWarnings("unchecked")
  public DHeap(AnyType[] items, int d) {
    this.d = d;
    currentSize = items.length;
    array = (AnyType[]) new Comparable[(currentSize + 2) * 11 / 10];

    int i = 1;
    for (AnyType item : items)
      array[i++] = item;
    buildHeap();
  }

  /**
   * Insert into the priority queue, maintaining heap order.
   * Duplicates are allowed.
   * 
   * @param x the item to insert.
   */
  public void insert(AnyType x) {
    if (currentSize == array.length - 1)
      enlargeArray(array.length * 2 + 1);
    // Percolate up
    for (var any : array) {
      System.out.println(any);
    }
    int hole = ++currentSize;
    for (array[0] = x; hole > 1 && x.compareTo(array[dMath(hole)]) < 0; hole = dMath(hole))
      array[hole] = array[dMath(hole)];
    array[hole] = x;
  }

  private int dMath(int hole) {
    var res = (hole - 2) / d + 1;
    return res;
  }

  @SuppressWarnings("unchecked")
  private void enlargeArray(int newSize) {
    AnyType[] old = array;
    array = (AnyType[]) new Comparable[newSize];
    for (int i = 0; i < old.length; i++)
      array[i] = old[i];
  }

  /**
   * Find the smallest item in the priority queue.
   * 
   * @return the smallest item, or throw an UnderflowException if empty.
   */
  public AnyType findMin() {
    if (isEmpty())
      throw new UnderflowException();
    return array[1];
  }

  /**
   * Remove the smallest item from the priority queue.
   * 
   * @return the smallest item, or throw an UnderflowException if empty.
   */
  public AnyType deleteMin() {
    if (isEmpty())
      throw new UnderflowException();

    AnyType minItem = findMin();
    array[1] = array[currentSize--];
    percolateDown(1);

    return minItem;
  }

  /**
   * Establish heap order property from an arbitrary
   * arrangement of items. Runs in linear time.
   */
  private void buildHeap() {
    for (int i = currentSize / d; i > 0; i--)
      percolateDown(i);
  }

  /**
   * Test if the priority queue is logically empty.
   * 
   * @return true if empty, false otherwise.
   */
  public boolean isEmpty() {
    return currentSize == 0;
  }

  /**
   * Make the priority queue logically empty.
   */
  public void makeEmpty() {
    currentSize = 0;
  }

  private static final int DEFAULT_CAPACITY = 10;
  private static final int DEFAULT_CHILDREN = 2;

  private int currentSize; // Number of elements in heap
  private int d; // Number of elements in heap
  private AnyType[] array; // The heap array

  /**
   * Internal method to percolate down in the heap.
   * 
   * @param hole the index at which the percolate begins.
   */
  private void percolateDown(int hole) {
    int child;
    AnyType tmp = array[hole];

    for (; hole * d <= currentSize; hole = child) {
      child = hole * d;
      if (child != currentSize &&
          array[child + 1].compareTo(array[child]) < 0)
        child++;
      if (array[child].compareTo(tmp) < 0)
        array[hole] = array[child];
      else
        break;
    }
    array[hole] = tmp;
  }

  public int size() {
    return currentSize;
  }

  AnyType get(int index) {
    return array[index];
  }

  public int parentIndex(int index) throws IllegalArgumentException {
    if (index <= 1)
      throw new IllegalArgumentException();
    return (index - 2) / d + 1;
  }

  public int firstChildIndex(int index) throws IllegalArgumentException {
    if (index == 0) {
      throw new IllegalArgumentException();
    }
    return d * (index - 1) + 2;
  }

  // Test program
  public static void main(String[] args) {
    int numItems = 10000;
    BinaryHeap<Integer> h = new BinaryHeap<>();
    int i = 37;

    for (i = 37; i != 0; i = (i + 37) % numItems)
      h.insert(i);
    for (i = 1; i < numItems; i++)
      if (h.deleteMin() != i)
        System.out.println("Oops! " + i);
  }
}
