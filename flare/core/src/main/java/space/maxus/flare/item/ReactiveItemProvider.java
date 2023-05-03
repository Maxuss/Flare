package space.maxus.flare.item;

import lombok.Data;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.react.ReactiveSubscriber;

@Data
public class ReactiveItemProvider<V> implements ItemProvider, ReactiveSubscriber<V> {
    private final Computable<@Nullable V, @Nullable ItemStack> computable;
    private @Nullable ItemStack current = null;

    @Override
    public @Nullable ItemStack provide() {
        return current;
    }

    @Override
    public void onStateChange(@Nullable V state) {
        try {
            this.current = computable.compute(state);
        } catch (InterruptedException e) {
            if (e.getCause() instanceof ThreadDeath)
                Thread.currentThread().interrupt();
            else {
                this.current = Items.getGenericErrorItem();
                Flare.LOGGER.error("Error while computing reactive item", e);
            }
        }
    }
}
