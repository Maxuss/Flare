package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;

public interface Placeholder extends ProviderRendered {
    @Contract("_ -> new")
    static @NotNull Placeholder of(ItemProvider provider) {
        return new PlaceholderImpl(provider);
    }
}
