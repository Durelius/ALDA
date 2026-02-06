package alda;

import java.util.Arrays;

/**
 * Detta är QuadraticProbingHashTable från boken med probing-metoden utbruten så
 * att den enkelt kan bytas ut, och main-metoden ersatt med JUnit-test. För att
 * probing-metoden ska fungera har vi också infört en metod som returnerar
 * värdet på en given plats i arrayen. Denna skulle normalt inte vara en del av
 * hashtabellen. Vi har också ändrat några skyddsnivåer.
 * 
 * Probing table implementation of hash tables. Note that all "matching" is
 * based on the equals method.
 * 
 * @author Mark Allen Weiss
 */
public abstract class ProbingHashTable<AnyType> {
  /**
   * Construct the hash table.
   */
  public ProbingHashTable() {
    this(DEFAULT_TABLE_SIZE);
  }

  /**
   * Construct the hash table.
   * 
   * @param size the approximate initial size.
   */
  public ProbingHashTable(int size) {
    if (size < MINIMUM_TABLE_SIZE)
      size = MINIMUM_TABLE_SIZE;
    allocateArray(size);
    doClear();
  }

  /**
   * Insert into the hash table. If the item is already present, do nothing.
   * 
   * @param x the item to insert.
   */
  public boolean insert(AnyType x) {
    // Insert x as active
    int currentPos = findPos(x);
    if (isActive(currentPos))
      return false;

    if (array[currentPos] == null)
      ++occupied;
    array[currentPos] = new HashEntry<>(x, true);
    theSize++;

    // Rehash; see Section 5.5
    if (occupied > array.length / 2)
      rehash();

    return true;
  }

  /**
   * Expand the hash table.
   */
  private void rehash() {
    HashEntry<AnyType>[] oldArray = array;

    // Create a new double-sized, empty table
    allocateArray(2 * oldArray.length);
    occupied = 0;
    theSize = 0;

    // Copy table over
    for (HashEntry<AnyType> entry : oldArray)
      if (entry != null && entry.isActive)
        insert(entry.element);
  }

  protected boolean continueProbing(int currentPos, AnyType x) {
    return array[currentPos] != null && !array[currentPos].element.equals(x);
  }

  /**
   * Method that performs probing resolution.
   * 
   * @param x the item to search for.
   * @return the position where the search terminates.
   */
  protected abstract int findPos(AnyType x);

  /**
   * Remove from the hash table.
   * 
   * @param x the item to remove.
   * @return true if item removed
   */
  public boolean remove(AnyType x) {
    int currentPos = findPos(x);
    if (isActive(currentPos)) {
      array[currentPos].isActive = false;
      theSize--;
      return true;
    } else
      return false;
  }

  /**
   * Get current size.
   * 
   * @return the size.
   */
  public int size() {
    return theSize;
  }

  /**
   * Get length of internal table.
   * 
   * @return the size.
   */
  public int capacity() {
    return array.length;
  }

  /**
   * Find an item in the hash table.
   * 
   * @param x the item to search for.
   * @return the matching item.
   */
  public boolean contains(AnyType x) {
    int currentPos = findPos(x);
    return isActive(currentPos);
  }

  /**
   * Return true if currentPos exists and is active.
   * 
   * @param currentPos the result of a call to findPos.
   * @return true if currentPos is active.
   */
  private boolean isActive(int currentPos) {
    return array[currentPos] != null && array[currentPos].isActive;
  }

  /**
   * Make the hash table logically empty.
   */
  public void makeEmpty() {
    doClear();
  }

  private void doClear() {
    occupied = 0;
    for (int i = 0; i < array.length; i++)
      array[i] = null;
  }

  protected int myhash(AnyType x) {
    int hashVal = x.hashCode();

    hashVal %= array.length;
    if (hashVal < 0)
      hashVal += array.length;

    return hashVal;
  }

  @Override
  public String toString() {
    return Arrays.toString(array);
  }

  private static class HashEntry<AnyType> {
    public AnyType element; // the element
    public boolean isActive; // false if marked deleted

    public HashEntry(AnyType e) {
      this(e, true);
    }

    public HashEntry(AnyType e, boolean i) {
      element = e;
      isActive = i;
    }

    @Override
    public String toString() {
      return isActive ? element.toString() : "inactive";
    }
  }

  private static final int DEFAULT_TABLE_SIZE = 101;
  private static final int MINIMUM_TABLE_SIZE = 5;

  private HashEntry<AnyType>[] array; // The array of elements
  private int occupied; // The number of occupied cells
  private int theSize; // Current size

  /**
   * Internal method to allocate array.
   * 
   * @param arraySize the size of the array.
   */
  private void allocateArray(int arraySize) {
    array = new HashEntry[nextPrime(arraySize)];
  }

  /**
   * Internal method to find a prime number at least as large as n.
   * 
   * @param n the starting number (must be positive).
   * @return a prime number larger than or equal to n.
   */
  private static int nextPrime(int n) {
    if (n % 2 == 0)
      n++;

    for (; !isPrime(n); n += 2)
      ;

    return n;
  }

  /**
   * Internal method to test if a number is prime. Not an efficient algorithm.
   * 
   * @param n the number to test.
   * @return the result of the test.
   */
  protected static final boolean isPrime(int n) {
    if (n == 2 || n == 3)
      return true;

    if (n == 1 || n % 2 == 0)
      return false;

    for (int i = 3; i * i <= n; i += 2)
      if (n % i == 0)
        return false;

    return true;
  }

}
