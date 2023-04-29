package flare;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.Stacks;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.util.FlareUtil;

public class MockFC extends FunctionComposable<Material> {
    public MockFC(Material props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        var counter = useState(0);
        var item = Reactive.item(
                counter,
                count -> Stacks.withMeta(this.props,
                        meta -> meta.displayName(FlareUtil.text("<gold>You clicked <light_purple>%s".formatted(count)))
                ));
        return Button.create(
                item,
                Button.ClickHandler.cancelling((btn, e) -> counter.set(counter.get() + 1))
        ).bindState(counter);
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
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Shift FROM"));
        return true;
    }

    @Override
    public boolean handleShiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(FlareUtil.text("<red>Shift INTO"));
        return true;
    }
}
