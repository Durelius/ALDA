package alda;

import java.util.*;

/**
 * Comprehensive stress test for Book.hashCode() implementation.
 * Evaluates hash quality through collision analysis, distribution uniformity,
 * and statistical tests.
 */
public class BookHashTest {
  private static final int BOOK_COUNT = 20_000;
  private static final int DISTRIBUTION_BUCKETS = 100;
  private static final Random RNG = new Random(42); // Deterministic for reproducibility

  public static void main(String[] args) {
    System.out.println("Generating " + BOOK_COUNT + " book instances...\n");

    List<Book> books = generateBooks();
    analyzeHashQuality(books);
  }

  /**
   * Generates a diverse set of Book instances with realistic variation patterns.
   * Includes intentional duplicates to test hashCode/equals contract.
   */
  private static List<Book> generateBooks() {
    List<Book> books = new ArrayList<>(BOOK_COUNT);

    // Generate unique books (85% of total)
    int uniqueCount = (int) (BOOK_COUNT * 0.85);
    for (int i = 0; i < uniqueCount; i++) {
      String title = generateTitle(i);
      String author = generateAuthor(i);
      String isbn = generateISBN(i);
      String content = generateContent(i);
      int price = generatePrice(i);

      books.add(new Book(title, author, isbn, content, price));
    }

    // Add exact duplicates (15% of total) to test hashCode/equals contract
    int duplicateCount = BOOK_COUNT - uniqueCount;
    for (int i = 0; i < duplicateCount; i++) {
      // Pick a random existing book to duplicate
      int sourceIndex = i % uniqueCount;
      Book original = books.get(sourceIndex);

      // Create exact duplicate with same field values
      Book duplicate = new Book(
          original.getTitle().toString(),
          original.getAuthor().toString(),
          original.getIsbn().toString(),
          original.getContent().toString(),
          original.getPrice());

      books.add(duplicate);
    }

    return books;
  }

  private static String generateTitle(int i) {
    // Mix of repeated and unique titles to test collision handling
    int titleVariant = i % 500;
    return "The Book of " + titleVariant + " Chapters";
  }

  private static String generateAuthor(int i) {
    // Fewer author variants than titles (realistic)
    int authorVariant = i % 200;
    String[] firstNames = { "John", "Jane", "Robert", "Maria", "David" };
    String[] lastNames = { "Smith", "Johnson", "Williams", "Brown", "Jones" };

    int firstIdx = authorVariant % firstNames.length;
    int lastIdx = (authorVariant / firstNames.length) % lastNames.length;
    int suffix = authorVariant / (firstNames.length * lastNames.length);

    return firstNames[firstIdx] + " " + lastNames[lastIdx] +
        (suffix > 0 ? " " + suffix : "");
  }

  private static String generateISBN(int i) {
    // Generate valid ISBN-10 with proper check digit
    long base = 1000000000L + (i % 8999999999L);
    String nineDigits = String.format("%09d", base % 1000000000);

    int sum = 0;
    for (int pos = 0; pos < 9; pos++) {
      int digit = nineDigits.charAt(pos) - '0';
      sum += digit * (10 - pos);
    }

    int checkDigit = (11 - (sum % 11)) % 11;
    String check = (checkDigit == 10) ? "X" : String.valueOf(checkDigit);

    return nineDigits + check;
  }

  private static String generateContent(int i) {
    // Content varies less frequently (books share common content patterns)
    int contentVariant = i % 100;
    return "Content block " + contentVariant + " with filler text and data.";
  }

  private static int generatePrice(int i) {
    // Realistic price distribution
    return 50 + (i % 150) * 5; // Prices from 50 to 800 in increments of 5
  }

  /**
   * Performs comprehensive hash quality analysis.
   */
  private static void analyzeHashQuality(List<Book> books) {
    // Collect all hash codes
    Map<Integer, List<Book>> hashToBooksMap = new HashMap<>();
    int[] hashCodes = new int[books.size()];

    for (int i = 0; i < books.size(); i++) {
      Book book = books.get(i);
      int hash = book.hashCode();
      hashCodes[i] = hash;

      hashToBooksMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(book);
    }

    // Analysis
    printHashCodeEqualsContract(books);
    printCollisionAnalysis(hashToBooksMap, books.size());
    printDistributionAnalysis(hashCodes);
    printUniformityTest(hashCodes);
    printWorstCollisions(hashToBooksMap);
  }

  /**
   * Validates the hashCode/equals contract:
   * - If a.equals(b), then a.hashCode() == b.hashCode()
   * - Tests with intentional duplicates
   */
  private static void printHashCodeEqualsContract(List<Book> books) {
    System.out.println("╔════════════════════════════════════════════╗");
    System.out.println("║      HASHCODE/EQUALS CONTRACT TEST         ║");
    System.out.println("╚════════════════════════════════════════════╝");

    int duplicatePairs = 0;
    int contractViolations = 0;

    // Find all duplicate pairs
    for (int i = 0; i < books.size(); i++) {
      for (int j = i + 1; j < books.size(); j++) {
        Book a = books.get(i);
        Book b = books.get(j);

        if (a.equals(b)) {
          duplicatePairs++;

          // Check if hash codes match
          if (a.hashCode() != b.hashCode()) {
            contractViolations++;

            if (contractViolations <= 3) {
              System.out.println("\n⚠️  CONTRACT VIOLATION FOUND:");
              System.out.println("  Book A: " + a);
              System.out.println("  Book B: " + b);
              System.out.println("  equals: true");
              System.out.printf("  hashCode A: %d, hashCode B: %d\n",
                  a.hashCode(), b.hashCode());
            }
          }
        }
      }
    }

    System.out.println("Equal pairs found      : " + duplicatePairs);
    System.out.println("Contract violations    : " + contractViolations);

    if (contractViolations == 0) {
      System.out.println("Result                 : ✓ PASS - Contract upheld!");
    } else {
      System.out.println("Result                 : ✗ FAIL - Contract violated!");
      System.out.println("\n⚠️  Critical issue: hashCode() implementation is broken!");
    }
    System.out.println();
  }

  /**
   * Analyzes hash collisions.
   */
  private static void printCollisionAnalysis(
      Map<Integer, List<Book>> hashToBooksMap,
      int totalBooks) {

    System.out.println("╔════════════════════════════════════════════╗");
    System.out.println("║        COLLISION ANALYSIS                  ║");
    System.out.println("╚════════════════════════════════════════════╝");

    int uniqueHashes = hashToBooksMap.size();
    int totalCollisions = totalBooks - uniqueHashes;

    // Count buckets by size
    Map<Integer, Integer> bucketSizeDistribution = new HashMap<>();
    int maxBucketSize = 0;

    for (List<Book> bucket : hashToBooksMap.values()) {
      int size = bucket.size();
      maxBucketSize = Math.max(maxBucketSize, size);
      bucketSizeDistribution.merge(size, 1, Integer::sum);
    }

    System.out.println("Total books            : " + totalBooks);
    System.out.println("Unique hash codes      : " + uniqueHashes);
    System.out.println("Total collisions       : " + totalCollisions);
    System.out.printf("Collision rate         : %.2f%%\n",
        (100.0 * totalCollisions) / totalBooks);
    System.out.println("Max bucket size        : " + maxBucketSize);
    System.out.printf("Load factor (ideal=1)  : %.2f\n\n",
        (double) totalBooks / uniqueHashes);

    // Show bucket size distribution
    System.out.println("Bucket size distribution:");
    List<Integer> sizes = new ArrayList<>(bucketSizeDistribution.keySet());
    Collections.sort(sizes);

    for (int size : sizes) {
      int count = bucketSizeDistribution.get(size);
      System.out.printf("  Size %2d: %5d buckets", size, count);
      if (size > 1) {
        System.out.printf(" (%d collisions)", count * (size - 1));
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Analyzes distribution across hash space using modulo buckets.
   */
  private static void printDistributionAnalysis(int[] hashCodes) {
    System.out.println("╔════════════════════════════════════════════╗");
    System.out.println("║        DISTRIBUTION UNIFORMITY             ║");
    System.out.println("╚════════════════════════════════════════════╝");

    int[] buckets = new int[DISTRIBUTION_BUCKETS];

    for (int hash : hashCodes) {
      // Use Math.floorMod to handle negative hash codes correctly
      int bucket = Math.floorMod(hash, DISTRIBUTION_BUCKETS);
      buckets[bucket]++;
    }

    // Calculate statistics
    double mean = (double) hashCodes.length / DISTRIBUTION_BUCKETS;
    double variance = 0;
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;

    for (int count : buckets) {
      variance += (count - mean) * (count - mean);
      min = Math.min(min, count);
      max = Math.max(max, count);
    }
    variance /= DISTRIBUTION_BUCKETS;
    double stdDev = Math.sqrt(variance);

    System.out.printf("Expected per bucket    : %.1f\n", mean);
    System.out.printf("Actual range           : %d - %d\n", min, max);
    System.out.printf("Standard deviation     : %.2f\n", stdDev);
    System.out.printf("Coefficient of var.    : %.2f%% (lower is better)\n\n",
        (stdDev / mean) * 100);

    // Visual distribution graph
    System.out.println("Distribution graph (each bucket = " +
        DISTRIBUTION_BUCKETS + " slots):");
    printDistributionGraph(buckets, mean);
    System.out.println();
  }

  /**
   * Prints a visual graph of distribution.
   */
  private static void printDistributionGraph(int[] buckets, double mean) {
    int maxWidth = 60;
    int maxCount = Arrays.stream(buckets).max().orElse(1);

    for (int i = 0; i < buckets.length; i++) {
      int count = buckets[i];
      int barLength = (int) ((double) count / maxCount * maxWidth);

      // Color code based on deviation from mean
      String marker;
      double deviation = Math.abs(count - mean) / mean;
      if (deviation > 0.3) {
        marker = "!"; // Significant deviation
      } else if (deviation > 0.15) {
        marker = "*"; // Moderate deviation
      } else {
        marker = "="; // Good distribution
      }

      System.out.printf("%3d │%s%s (%d)\n",
          i,
          marker.repeat(barLength),
          " ".repeat(maxWidth - barLength),
          count);
    }
  }

  /**
   * Performs chi-squared test for uniformity.
   */
  private static void printUniformityTest(int[] hashCodes) {
    System.out.println("╔════════════════════════════════════════════╗");
    System.out.println("║        CHI-SQUARED UNIFORMITY TEST         ║");
    System.out.println("╚════════════════════════════════════════════╝");

    int[] buckets = new int[DISTRIBUTION_BUCKETS];
    for (int hash : hashCodes) {
      buckets[Math.floorMod(hash, DISTRIBUTION_BUCKETS)]++;
    }

    double expected = (double) hashCodes.length / DISTRIBUTION_BUCKETS;
    double chiSquared = 0;

    for (int observed : buckets) {
      double diff = observed - expected;
      chiSquared += (diff * diff) / expected;
    }

    // Degrees of freedom = buckets - 1
    int degreesOfFreedom = DISTRIBUTION_BUCKETS - 1;

    System.out.printf("Chi-squared statistic  : %.2f\n", chiSquared);
    System.out.printf("Degrees of freedom     : %d\n", degreesOfFreedom);
    System.out.printf("Expected (if uniform)  : ~%.1f\n", (double) degreesOfFreedom);

    // Rough interpretation (for df=99, critical value at p=0.05 is ~124)
    if (chiSquared < degreesOfFreedom * 1.3) {
      System.out.println("Result                 : GOOD - Distribution is uniform");
    } else if (chiSquared < degreesOfFreedom * 1.5) {
      System.out.println("Result                 : FAIR - Some clustering present");
    } else {
      System.out.println("Result                 : POOR - Significant clustering");
    }
    System.out.println();
  }

  /**
   * Shows examples of worst hash collisions for debugging.
   */
  private static void printWorstCollisions(Map<Integer, List<Book>> hashToBooksMap) {
    System.out.println("╔════════════════════════════════════════════╗");
    System.out.println("║        WORST COLLISION EXAMPLES            ║");
    System.out.println("╚════════════════════════════════════════════╝");

    // Find top 3 worst collision buckets
    List<Map.Entry<Integer, List<Book>>> entries = new ArrayList<>(hashToBooksMap.entrySet());

    entries.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

    int samplesToShow = Math.min(3, entries.size());

    for (int i = 0; i < samplesToShow; i++) {
      Map.Entry<Integer, List<Book>> entry = entries.get(i);
      List<Book> bucket = entry.getValue();

      if (bucket.size() <= 1) {
        System.out.println("No significant collisions found!");
        break;
      }

      System.out.printf("\nCollision #%d: Hash %d (%d books)\n",
          i + 1, entry.getKey(), bucket.size());

      // Show first few books in collision
      int samplesToDisplay = Math.min(3, bucket.size());
      for (int j = 0; j < samplesToDisplay; j++) {
        Book book = bucket.get(j);
        System.out.printf("  Book %d: %s by %s (ISBN: %s)\n",
            j + 1,
            truncate(book.getTitle().toString(), 30),
            truncate(book.getAuthor().toString(), 20),
            book.getIsbn());
      }

      if (bucket.size() > samplesToDisplay) {
        System.out.printf("  ... and %d more\n",
            bucket.size() - samplesToDisplay);
      }
    }

    if (samplesToShow == 0 || entries.get(0).getValue().size() == 1) {
      System.out.println("Perfect hash function - no collisions!");
    }
  }

  private static String truncate(String str, int maxLength) {
    if (str == null)
      return "null";
    if (str.length() <= maxLength)
      return str;
    return str.substring(0, maxLength - 3) + "...";
  }
}
