package space.maxus.flare.ui.compose;

import lombok.Data;
import space.maxus.flare.item.ItemProvider;

@Data
final class PlaceholderImpl implements Placeholder {
    private final ItemProvider placeholder;
}
