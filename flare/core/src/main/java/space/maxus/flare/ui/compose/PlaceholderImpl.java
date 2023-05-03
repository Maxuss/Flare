package space.maxus.flare.ui.compose;

import lombok.Data;
import lombok.EqualsAndHashCode;
import space.maxus.flare.item.ItemProvider;

@EqualsAndHashCode(callSuper = true)
@Data
final class PlaceholderImpl extends RootReferencing implements Placeholder {
    private final ItemProvider provider;
}
