package flare;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.complex.Composition;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

public class MockFC extends FunctionComposable<Material> {
    private final ReactiveState<Button> button = useState(null);

    public MockFC(Material props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        var counter = useState(0);
        var item = Reactive.item(
                counter,
                count -> Items.withMeta(this.props,
                        meta -> meta.displayName(FlareUtil.text("<gold>You clicked <light_purple>%s".formatted(count)))
                ));
        return Composition.list(
                Placeholder.of(ItemProvider.still(new ItemStack(Material.EMERALD)))
                        .inside(Rect.of(Slot.ROW_ONE_SLOT_FOUR, Slot.ROW_FOUR_SLOT_FIVE)),
                Button.create(
                        item,
                        Button.ClickHandler.cancelling((btn, e) -> counter.set(counter.get() + 1))
                ).into(button).inside(Rect.of(Slot.ROW_ONE_SLOT_ONE, Slot.ROW_THREE_SLOT_THREE)),
                Placeholder.of(ItemProvider.still(new ItemStack(Material.DIAMOND)))
                        .inside(Rect.of(Slot.ROW_ONE_SLOT_SIX, Slot.ROW_FIVE_SLOT_SIX))
        );
    }

    @Override
    public boolean handleLeftClick(@NotNull InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Click LEFT"));
        return true;
    }

    @Override
    public boolean handleRightClick(@NotNull InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Click RIGHT"));
        return true;
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Click GENERIC"));
    }


    @Override
    public boolean handleShiftFrom(@NotNull InventoryClickEvent e) {
        // only in shift click we set button to different
        Button btn = button.get();
        btn.setDisabled(!btn.isDisabled());
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Shift FROM"));
        return true;
    }

    @Override
    public boolean handleShiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Shift INTO"));
        return true;
    }
}
