package space.maxus.flare.ui;

import lombok.Data;
import space.maxus.flare.ui.space.ComposableSpace;

@Data
public class PackedComposable {
    private final ComposableSpace space;
    private final Composable composable;
}
