package flare;

import org.bukkit.Material;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.PaginatedFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

public class DocPagedFrame extends PaginatedFrame {
    @Override
    public void init() {
        usePaginationDisplay(Rect.of(Slot.ROW_ONE_SLOT_FOUR, Slot.ROW_ONE_SLOT_SEVEN));

        createPage("First page", page -> {
            // (0)
            page.compose(Placeholder
                    .of(Items.empty(Material.BLACK_STAINED_GLASS_PANE))
                    .inside(Slot.ROW_FOUR)
            );
        });
        createPage("Second page", page -> page.compose(Placeholder
                .of(Items.empty(Material.GREEN_STAINED_GLASS_PANE))
                .inside(Slot.ROW_FIVE)
        ));
        createPage("Third page", page -> page.compose(Placeholder
                .of(Items.empty(Material.YELLOW_STAINED_GLASS_PANE))
                .inside(Slot.ROW_SIX)
        ));
    }
}
