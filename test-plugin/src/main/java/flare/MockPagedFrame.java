package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import space.maxus.flare.Flare;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.extra.Slideshow;
import space.maxus.flare.ui.frames.PaginatingFrame;
import space.maxus.flare.ui.page.PageFrame;
import space.maxus.flare.ui.space.Slot;

import java.util.List;

public class MockPagedFrame extends PaginatingFrame {
    @Override
    public void init() {
        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 1"));
            page.compose(Placeholder.of(Items.builder(Material.GRAY_STAINED_GLASS_PANE)).inside(Slot.ALL));
            page.compose(Button
                    .builder(Items.builder(Material.ARROW))
                    .onClick(Button.ClickHandler.cancelling((btn, e) -> pagination.previousPage((Player) e.getWhoClicked())))
                    .build()
                    .inside(Slot.ROW_ONE_SLOT_ONE)
            );
            page.compose(Button
                    .builder(Items.builder(Material.ARROW))
                    .onClick(Button.ClickHandler.cancelling((btn, e) -> pagination.nextPage((Player) e.getWhoClicked())))
                    .build()
                    .inside(Slot.ROW_ONE_SLOT_NINE)
            );
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
        });

        createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 2"));
            page.compose(Placeholder.of(Items.builder(Material.YELLOW_STAINED_GLASS_PANE)).inside(Slot.ALL));
            setupMockPage(page);
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

        pagination.createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 3"));
            page.compose(Placeholder.of(Items.builder(Material.GREEN_STAINED_GLASS_PANE)).inside(Slot.ALL));
            setupMockPage(page);
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

        pagination.createPage(page ->
        {
            page.cancellingOnLeftClick((slot, e) -> Flare.LOGGER.info("LEFT 4"));
            page.compose(Placeholder.of(Items.builder(Material.BLUE_STAINED_GLASS_PANE)).inside(Slot.ALL));
            setupMockPage(page);
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

    private void setupMockPage(PageFrame page) {
        page.compose(Button
                .builder(Items.builder(Material.ARROW))
                .onClick(Button.ClickHandler.cancelling((btn, e) -> pagination.previousPage((Player) e.getWhoClicked())))
                .build()
                .inside(Slot.ROW_ONE_SLOT_ONE)
        );
        page.compose(Button
                .builder(Items.builder(Material.ARROW))
                .onClick(Button.ClickHandler.cancelling((btn, e) -> pagination.nextPage((Player) e.getWhoClicked())))
                .build()
                .inside(Slot.ROW_ONE_SLOT_NINE)
        );
    }
}
