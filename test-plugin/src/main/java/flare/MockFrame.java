package flare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

public class MockFrame extends SimpleFrame {
    @Override
    public void init() {
        this.compose(
                Rect.of(
                        Slot.ROW_ONE_SLOT_TWO,
                        Slot.ROW_FIVE_SLOT_EIGHT
                ),
                Placeholder.of(ItemProvider.still(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)))
        );
    }
}
