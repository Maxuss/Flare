package space.maxus.flare.ui.compose;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.space.Slot;

/**
 * An interface for components that are implicitly rendered by an item provider
 */
public interface ProviderRendered extends Composable {
    /**
     * Gets the provider for rendering
     * @return The provider for rendering
     */
    @NotNull ItemProvider getProvider();

    @Override
    default ItemStack renderAt(Slot slot) {
        return getProvider().provide();
    }
}
