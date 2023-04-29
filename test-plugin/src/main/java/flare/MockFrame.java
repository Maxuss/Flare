package flare;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

import java.time.Instant;
import java.util.Date;

public class MockFrame extends SimpleFrame {
    @Override
    public void init() {
        this.useContext(new Context("Hello World", Date.from(Instant.now())));

        this.compose(
                Placeholder.of(
                        ItemProvider.still(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                ).inside(Slot.ALL)
        );

        this.compose(
                new MockFC(Material.GOLD_INGOT)
                        .inside(Rect.of(Slot.ROW_TWO_SLOT_TWO, Slot.ROW_FIVE_SLOT_EIGHT))
        );
    }

    record Context(String value, Date today) {

    }
}
