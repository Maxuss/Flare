package space.maxus.flare.util;

import lombok.Data;
import lombok.experimental.StandardException;
import org.apache.commons.lang3.concurrent.Computable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;

/**
 * Represents a computable that handles any errors inside itself
 * @param <I> Input type of computable
 * @param <O> Output type of computable
 */
public interface SafeComputable<I, O> extends Computable<I, O> {
    @Contract("_ -> new")
    static <I, O> @NotNull SafeComputable<I, O> wrap(Computable<I, O> c) {
        return new Wrapper<>(c);
    }

    /**
     * Safely computes the input, handling any errors inside itself, only throwing a {@link ComputeInterruptionException} if the thread is interrupted
     * @param input Input value
     * @return Output value
     */
    O safeCompute(I input);

    @Override
    default O compute(I arg) {
        return safeCompute(arg);
    }

    @Data
    final class Wrapper<I, O> implements SafeComputable<I, O> {
        private final Computable<I, O> computable;

        @Override
        public O safeCompute(I input) {
            try {
                return computable.compute(input);
            } catch (InterruptedException e) {
                Flare.logger().error("Thread was interrupted while calling SafeComputable!", e);
                if (e.getCause() instanceof ThreadDeath) {
                    Thread.currentThread().interrupt();
                }
                throw new ComputeInterruptionException(e);
            }
        }
    }

    /**
     * Thrown when error occurs inside SafeComputable
     */
    @StandardException
    final class ComputeInterruptionException extends RuntimeException {

    }
}
