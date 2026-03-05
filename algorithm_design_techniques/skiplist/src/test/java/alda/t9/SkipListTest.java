package alda.t9;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.*;

import org.junit.jupiter.api.*;

public class SkipListTest {

    private static final long TIMEOUT_MILLIS_FOR_SINGLE_OPERATION = 50;
    private static final int DEFAULT_UPPER_LIMIT_TO_RANDOM_NUMBER = 100;

    private static final int A_NUMBER = 123;

    private static final Random RND = new Random();

    private SkipList<Integer> list = new SkipList<>();
    private SortedSet<Integer> used = new TreeSet<>();

    private Integer randomUnusedNumber() {
        return randomUnusedNumber(DEFAULT_UPPER_LIMIT_TO_RANDOM_NUMBER);
    }

    private Integer randomUnusedNumber(int upperLimit) {
        while (true) {
            Integer rnd = RND.nextInt(upperLimit);
            if (!used.contains(rnd))
                return rnd;
        }
    }

    private void execute(Runnable r) {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS_FOR_SINGLE_OPERATION), () -> {
            r.run();
        });
    }

    private <T extends Comparable<T>> void assertSizeIs(SkipList<T> list, int expected) {
        execute(() -> {
            assertEquals(expected, list.size(), list.toString());
        });
    }

    private void assertSizeAndContentMatches() {
        assertSizeIs(list, used.size());

        for (Integer number : used) {
            assertContains(list, number);
        }

        for (int n = 0; n < 5; n++) {
            assertDoesNotContains(list, randomUnusedNumber());
        }
    }

    private <T extends Comparable<T>> void assertContains(SkipList<T> list, T expected) {
        execute(() -> {
            assertTrue(list.contains(expected), "Looking for %s in %s".formatted(expected, list));
        });
    }

    private <T extends Comparable<T>> void assertDoesNotContains(SkipList<T> list, T expected) {
        execute(() -> {
            assertFalse(list.contains(expected), "Looking for %s in %s".formatted(expected, list));
        });
    }

    private void addRandomNumbers(int count) {
        addRandomNumbers(count, Math.max(DEFAULT_UPPER_LIMIT_TO_RANDOM_NUMBER, count * 5));
    }

    private void addRandomNumbers(int count, int upperLimit) {
        for (int n = 0; n < count; n++) {
            Integer number = randomUnusedNumber(upperLimit);
            addNonExisting(list, number);
            used.add(number);
        }
    }

    private <T extends Comparable<T>> void addNonExisting(SkipList<T> list, T value) {
        assertDoesNotContains(list, value);
        execute(() -> {
            list.add(value);
        });
        assertContains(list, value);
    }

    private void removeExistingNumber(Integer number) {
        removeExisting(list, number);
        used.remove(number);
    }

    private void removeRandomExistingNumber(int count) {
        for (int i = 0; i < count; i++) {
            Integer number = used.stream().skip(RND.nextInt(used.size())).findFirst().get();
            removeExistingNumber(number);
        }
    }

    private void removeNonExistingNumber(Integer number) {
        removeNonExisting(list, number);
    }

    private <T extends Comparable<T>> void removeExisting(SkipList<T> list, T value) {
        String before = list.toString();
        assertContains(list, value);
        execute(() -> {
            assertTrue(list.remove(value), "Removing %s from %s".formatted(value, before));
        });
        assertDoesNotContains(list, value);
    }

    private <T extends Comparable<T>> void removeNonExisting(SkipList<T> list, T value) {
        String before = list.toString();
        assertDoesNotContains(list, value);
        execute(() -> {
            assertFalse(list.remove(value), "Removing %s from %s".formatted(value, before));
        });
        assertDoesNotContains(list, value);
    }

    @Test
    public void emptyList() {
        assertSizeIs(list, 0);
        removeNonExistingNumber(A_NUMBER);
    }

    @Test
    public void addOneNumber() {
        addNonExisting(list, A_NUMBER);
    }

    @Test
    public void addMultipleNumbers() {
        addRandomNumbers(3);
        assertSizeAndContentMatches();
        addRandomNumbers(3);
        assertSizeAndContentMatches();
    }

    @Test
    public void addAndRemove() {
        addRandomNumbers(5);
        assertSizeAndContentMatches();

        removeRandomExistingNumber(2);
        assertSizeAndContentMatches();

        addRandomNumbers(5);
        assertSizeAndContentMatches();

        removeRandomExistingNumber(3);
        assertSizeAndContentMatches();
    }

    @Test
    public void removeNonExistent() {
        addRandomNumbers(5);
        removeNonExistingNumber(randomUnusedNumber());
    }

    /**
     * Detta test försöker kontrollera att operationerna på listan går i O(N log N)
     * genom att genomföra väldigt många operationer. Max-tiden är lite av en
     * gissning och kan behöva ändras för din dator.
     */
    @Test
    public void operationsRunInLogN() {
        final int NUMBERS_TO_TEST = 200_000;
        final long TIMEOUT_MILLIS = 1000;

        List<Integer> numbers = new LinkedList<>();

        for (int i = 0; i < NUMBERS_TO_TEST; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            for (Integer i : numbers) {
                list.add(i);
            }
        }, "Managed to create %d nodes, before running out of time".formatted(used.size()));

        assertEquals(NUMBERS_TO_TEST, list.size());

        // Remove half the numbers
        numbers.removeIf(n -> n % 2 == 0);
        Collections.shuffle(numbers);

        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            for (Integer i : numbers) {
                list.remove(i);
            }
        }, "Managed to remove %d nodes, before running out of time".formatted(NUMBERS_TO_TEST - used.size()));

    }

    @Test
    public void randomOperations() {
        while (used.size() < 1000) {
            if (RND.nextBoolean()) {
                addRandomNumbers(1, 100_000);
            } else if (used.size() > 0 && RND.nextBoolean()) {
                removeRandomExistingNumber(1);
            }
        }
    }

    /**
     * Detta test försöker testa den slumpmässiga fördelningen av nodernas nivåer. I
     * och med att denna är slumpmässig finns det en risk att testet misslyckas även
     * om fördelningen är korrekt. Det kan alltså vara nödvändigt att köra testet
     * flera gånger.
     *
     * Det nuvarande testet kontrollerar bara antalet av varje typ av höjd, inte
     * distributionen.
     */
    @RepeatedTest(5)
    public void distributionOfLevels() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            addRandomNumbers(1000);

            int[] levels = new int[10];
            for (int idx = 0; idx < 1000; idx++) {
                int level = list.levelOfNode(idx);
                if (level < 1)
                    throw new IllegalStateException("A node can't have less than one level");
                if (level < levels.length) {
                    levels[level - 1]++;
                }
            }

            for (int i = 0, total = 1000, expected = 500; i < 5; total -= levels[i], i++, expected = total / 2) {
                double min = Math.min(expected * 0.75, expected - 5);
                double max = Math.max(expected * 1.25, expected + 5);

                assertTrue(levels[i] > min,
                        "%d nodes with %d levels is well below the %d expected for a list with 1000 elements"
                                .formatted(levels[i], i, expected));
                assertTrue(levels[i] < max,
                        "%d nodes with %d levels is well above the %d expected for a list with 1000 elements"
                                .formatted(levels[i], i, expected));
            }
        });
    }

    @Test
    public void listOfCharacter() {
        SkipList<Character> chars = new SkipList<>();
        addNonExisting(chars, 'B');
        addNonExisting(chars, 'A');
        removeExisting(chars, 'B');
        removeNonExisting(chars, 'B');
    }

    @Test
    public void listOfString() {
        SkipList<String> strings = new SkipList<>();
        addNonExisting(strings, "second");
        addNonExisting(strings, "first");
        addNonExisting(strings, "third");
        removeExisting(strings, "second");
        removeNonExisting(strings, "second");
    }

}
