package space.maxus.flare.ui.compose;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;

/**
 * A placeholder is a slot that can not be interacted with.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#placeholder">Placeholder</a>
 */
public interface Placeholder extends ProviderRendered {
    /**
     * Constructs a new placeholder for an item provider
     * @param provider The item provider
     * @return A new placeholder with item provider
     */
    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemProvider provider) {
        return new PlaceholderImpl(provider);
    }

    /**
     * Constructs a new placeholder with a still item provider of this item
     * @param still Item to be used
     * @return A new placeholder with item
     */
    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemStack still) {
        return new PlaceholderImpl(ItemProvider.still(still));
    }
}
