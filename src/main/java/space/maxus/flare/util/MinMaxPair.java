package space.maxus.flare.util;

import lombok.Data;

@Data
public class MinMaxPair<A extends Comparable<A>> {
    private final A min;
    private final A max;

    public MinMaxPair(A a, A b) {
        this.min = a.compareTo(b) < 0 ? a : b;
        this.max = a.compareTo(b) < 0 ? b : a;
    }
}
