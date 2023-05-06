package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import space.maxus.flare.Flare;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.Slideshow;
import space.maxus.flare.ui.frames.PaginatedFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

import java.util.List;

public class MockPagedFrame extends PaginatedFrame {
    @Override
    public void init() {
        useTabulation(Rect.of(Slot.ROW_THREE_SLOT_THREE, Slot.ROW_THREE_SLOT_SIX));

        pagination.composeShared(Placeholder.of(Items.builder(Material.GRAY_STAINED_GLASS_PANE)).inside(Slot.ALL));

        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 1"));
            page.compose(Slideshow
                    .create(
                            List.of(
                                    Items.builder(Material.DIAMOND_SWORD),
                                    Items.builder(Material.DIAMOND_SHOVEL),
                                    Items.builder(Material.DIAMOND_AXE),
                                    Items.builder(Material.DIAMOND_BLOCK)
                            ),
                            10
                    )
                    .configure(slides -> slides.itemState().subscribe(newItem -> Flare.LOGGER.info("SWITCHING 1")))
                    .inside(Slot.ROW_ONE_SLOT_FIVE)
            );
            page.compose(Button
                    .create(
                            Items.builder(Material.DIAMOND),
                            Button.ClickHandler.cancelling((btn, e) -> this.switchFrame(new MockFrame((Player) e.getWhoClicked())))
                    )
                    .inside(Slot.ROW_SIX_SLOT_FIVE)
            );
        });

        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 2"));
            page.compose(Slideshow
                    .create(
                            List.of(
                                    Items.builder(Material.IRON_SWORD),
                                    Items.builder(Material.IRON_SHOVEL),
                                    Items.builder(Material.IRON_AXE),
                                    Items.builder(Material.IRON_BLOCK)
                            ),
                            10
                    )
                    .configure(slides -> slides.itemState().subscribe(newItem -> Flare.LOGGER.info("SWITCHING 2")))
                    .inside(Slot.ROW_ONE_SLOT_FIVE)
            );
        });

        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 3"));
            page.compose(Slideshow
                    .create(
                            List.of(
                                    Items.builder(Material.GOLDEN_SWORD),
                                    Items.builder(Material.GOLDEN_SHOVEL),
                                    Items.builder(Material.GOLDEN_AXE),
                                    Items.builder(Material.GOLD_BLOCK)
                            ),
                            10
                    )
                    .configure(slides -> slides.itemState().subscribe(newItem -> Flare.LOGGER.info("SWITCHING 3")))
                    .inside(Slot.ROW_ONE_SLOT_FIVE)
            );
        });

        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 4"));
            page.compose(Slideshow
                    .create(
                            List.of(
                                    Items.builder(Material.NETHERITE_SWORD),
                                    Items.builder(Material.NETHERITE_SHOVEL),
                                    Items.builder(Material.NETHERITE_AXE),
                                    Items.builder(Material.NETHERITE_BLOCK)
                            ),
                            10
                    )
                    .configure(slides -> slides.itemState().subscribe(newItem -> Flare.LOGGER.info("SWITCHING 4")))
                    .inside(Slot.ROW_ONE_SLOT_FIVE)
            );
        });
    }
}
