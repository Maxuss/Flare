package space.maxus.flare.text;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Producer<I, O> {
    @Nullable O produce(@Nullable I input);
}
