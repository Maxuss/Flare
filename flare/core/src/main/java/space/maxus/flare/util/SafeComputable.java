package space.maxus.flare.util;

import lombok.Data;
import lombok.experimental.StandardException;
import org.apache.commons.lang3.concurrent.Computable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;

public interface SafeComputable<I, O> extends Computable<I, O> {
    @Contract("_ -> new")
    static <I, O> @NotNull SafeComputable<I, O> wrap(Computable<I, O> c) {
        return new Wrapper<>(c);
    }

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

    @StandardException
    final class ComputeInterruptionException extends RuntimeException {

    }
}
