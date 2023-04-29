package space.maxus.flare.ui.compose;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.space.Slot;

public interface ProviderRendered extends Composable {
    @NotNull ItemProvider getProvider();

    @Override
    default ItemStack renderAt(Slot slot) {
        return getProvider().provide();
    }
}
