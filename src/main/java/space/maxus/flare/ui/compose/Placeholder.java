package space.maxus.flare.ui.compose;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.space.Slot;

public interface Placeholder extends Composable {
    @NotNull ItemProvider getPlaceholder();

    @Override
    default ItemStack renderAt(Slot slot) {
        return getPlaceholder().provide();
    }

    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemProvider provider) {
        return new PlaceholderImpl(provider);
    }
}
