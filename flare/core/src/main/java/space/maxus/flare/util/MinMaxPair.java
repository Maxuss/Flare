package space.maxus.flare.util;

import lombok.Data;

/**
 * A pair which sorts two values by their ordering via Comparable
 * @param <A> Comparable element
 */
@Data
public class MinMaxPair<A extends Comparable<A>> {
    /**
     * The smaller value
     */
    private final A min;
    /**
     * The bigger value
     */
    private final A max;

    /**
     * Constructs a min-max pair of two unordered elements
     * @param a First value
     * @param b Second value
     */
    public MinMaxPair(A a, A b) {
        int comparisonResult = a.compareTo(b);
        this.min = comparisonResult < 0 ? a : b;
        this.max = comparisonResult < 0 ? b : a;
    }
}
