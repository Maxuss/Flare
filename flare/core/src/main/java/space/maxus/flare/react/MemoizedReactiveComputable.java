package space.maxus.flare.react;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.jetbrains.annotations.Nullable;

/**
 * A `Computable`, that uses a `Memoizer` to cache its results based on reactive state.
 * @param <I> The reactive state type.
 * @param <O> The computed result type.
 */
@RequiredArgsConstructor
public class MemoizedReactiveComputable<I, O> implements Computable<I, O>, ReactiveSubscriber<I> {
    private final Memoizer<I, O> delegate;
    @Getter
    private @Nullable O currentValue;

    @Override
    public O compute(I arg) throws InterruptedException {
        return delegate.compute(arg);
    }

    @Override
    public void onStateChange(@Nullable I state) throws ReactiveException {
        try {
            this.currentValue = this.compute(state);
        } catch (InterruptedException e) {
            if (e.getCause() instanceof ThreadDeath) {
                Thread.currentThread().interrupt();
            } else {
                throw new ReactiveException("Error while computing reactive state", e);
            }
        }
    }
}
