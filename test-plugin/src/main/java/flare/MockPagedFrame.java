package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.Slideshow;
import space.maxus.flare.ui.compose.complex.PaginationDisplay;
import space.maxus.flare.ui.frames.PaginatedFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

import java.util.List;

public class MockPagedFrame extends PaginatedFrame {
    @Override
    public void init() {
        usePaginationDisplay(
                Rect.of(Slot.ROW_THREE_SLOT_THREE, Slot.ROW_THREE_SLOT_SEVEN),
                () -> PaginationDisplay
                        .builder(pagination)
                        .backButton(PaginationDisplay.arrowBackwardButton(false).type(Material.ARROW))
                        .forwardButton(PaginationDisplay.arrowForwardButton(false).type(Material.ARROW))
                        .selectedPage(pair -> PaginationDisplay.pageNumber(pair.getValue(), pair.getKey(), true, getViewer()).type(Material.GREEN_STAINED_GLASS_PANE).build())
                        .unselectedPage(pair -> PaginationDisplay.pageNumber(pair.getValue(), pair.getKey(), false, getViewer()).type(Material.BLACK_STAINED_GLASS_PANE).build())
                        .build()

        );

        pagination.composeShared(Placeholder.of(Items.builder(Material.GRAY_STAINED_GLASS_PANE)).inside(Slot.ALL));

        createPage("Hello, %player_name%!", page ->
        {
            page.compose(Slideshow
                    .of(
                            List.of(
                                    Items.builder(Material.DIAMOND_SWORD),
                                    Items.builder(Material.DIAMOND_SHOVEL),
                                    Items.builder(Material.DIAMOND_AXE),
                                    Items.builder(Material.DIAMOND_BLOCK)
                            ),
                            10
                    )
                    .inside(Slot.ROW_ONE_SLOT_FIVE)
            );
            page.compose(Button
                    .of(
                            Items.builder(Material.DIAMOND),
                            Button.ClickHandler.cancelling((btn, e) -> this.switchFrame(new MockFrame((Player) e.getWhoClicked())))
                    )
                    .inside(Slot.ROW_SIX_SLOT_FIVE)
            );
        });

        createPage("Another page", page ->
                page.compose(Slideshow
                        .of(
                                List.of(
                                        Items.builder(Material.IRON_SWORD),
                                        Items.builder(Material.IRON_SHOVEL),
                                        Items.builder(Material.IRON_AXE),
                                        Items.builder(Material.IRON_BLOCK)
                                ),
                                10
                        )
                        .inside(Slot.ROW_ONE_SLOT_FIVE)
                ));

        createPage("Third Title", page ->
                page.compose(Slideshow
                        .of(
                                List.of(
                                        Items.builder(Material.GOLDEN_SWORD),
                                        Items.builder(Material.GOLDEN_SHOVEL),
                                        Items.builder(Material.GOLDEN_AXE),
                                        Items.builder(Material.GOLD_BLOCK)
                                ),
                                10
                        )
                        .inside(Slot.ROW_ONE_SLOT_FIVE)
                ));

        createPage("Final page", page ->
                page.compose(Slideshow
                        .of(
                                List.of(
                                        Items.builder(Material.NETHERITE_SWORD),
                                        Items.builder(Material.NETHERITE_SHOVEL),
                                        Items.builder(Material.NETHERITE_AXE),
                                        Items.builder(Material.NETHERITE_BLOCK)
                                ),
                                10
                        )
                        .inside(Slot.ROW_ONE_SLOT_FIVE)
                ));
    }
}
