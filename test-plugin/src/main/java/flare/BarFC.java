package flare;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.complex.Composition;
import space.maxus.flare.ui.compose.extra.ProgressBar;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

public class BarFC extends FunctionComposable<Float> {
    public BarFC(Float props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        ReactiveState<Float> bar = useState(props);
        return Composition.list(
                Button.create(
                        ItemProvider.still(Items.withName(Material.DIAMOND, "Click to increment")),
                        Button.ClickHandler.cancelling((btn, e) -> bar.set(bar.get() >= 1f ? 0f : bar.get() + .05f))
                ).inside(Slot.ROW_ONE_SLOT_NINE),
                ProgressBar
                        .create(
                                ProgressBar.fullProvider(bar, Material.GREEN_STAINED_GLASS_PANE, true),
                                ProgressBar.emptyProvider(bar, Material.RED_STAINED_GLASS_PANE, true)
                        )
                        .configure(b -> b.progressState().connect(bar))
                        .inside(Rect.of(Slot.ROW_TWO_SLOT_ONE, Slot.ROW_TWO_SLOT_NINE))
        );
    }
}
