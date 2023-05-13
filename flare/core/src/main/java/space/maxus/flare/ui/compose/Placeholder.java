package space.maxus.flare.ui.compose;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;

public interface Placeholder extends ProviderRendered {
    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemProvider provider) {
        return new PlaceholderImpl(provider);
    }

    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemStack still) {
        return new PlaceholderImpl(ItemProvider.still(still));
    }
}
