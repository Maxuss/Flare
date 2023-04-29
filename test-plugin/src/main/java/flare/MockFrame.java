package flare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Stacks;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

public class MockFrame extends SimpleFrame {
    @Override
    public void init() {
        this.compose(
                Placeholder.of(
                        ItemProvider.still(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                )
                        .inside(Rect.of(
                                Slot.ROW_TWO_SLOT_TWO,
                                Slot.ROW_FIVE_SLOT_EIGHT
                        ))
        );


        ReactiveState<Integer> counter = useState(0); // creates a new reactive state
        ItemProvider item = Reactive.item(
                counter, // the bound state
                count -> Stacks.withMeta(Material.values()[counter.get() + 10],
                        meta -> meta.displayName(FlareUtil.text("<gold>You clicked <light_purple>%s".formatted(count)))
                )); // called on change

        this.compose(
                Button.create(
                        item, // the item display
                        Button.ClickHandler.cancelling((btn, e) -> counter.set(counter.get() + 1)) // triggered on click
                )
                        .bindState(counter) // binds the state, so button re-renders on change
                        .inside(Slot.ROW_FOUR_SLOT_FIVE) // places button in slot
        );
    }
}
