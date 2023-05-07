package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.*;
import space.maxus.flare.ui.compose.complex.Modal;
import space.maxus.flare.ui.frames.ParamFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MockFrame extends ParamFrame<Player> {
    protected MockFrame(@NotNull Player params) {
        super(params);
    }

    @Override
    public void preInit(Player params) {
        this.useContext(new Context(params));
    }

    @Override
    public void init() {
        Context ctx = Objects.requireNonNull(contextOrNull());

        this.compose(
                Placeholder.of(
                        ItemProvider.still(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                ).inside(Slot.ALL)
        );

        this.compose(new HasherFC().inside(Rect.of(Slot.ROW_TWO_SLOT_TWO, Slot.ROW_THREE_SLOT_TWO)));
        this.compose(new HasherFC().inside(Rect.of(Slot.ROW_TWO_SLOT_FOUR, Slot.ROW_THREE_SLOT_FOUR)));
        this.compose(new HasherFC().inside(Rect.of(Slot.ROW_TWO_SLOT_SIX, Slot.ROW_THREE_SLOT_SIX)));
        this.compose(new HasherFC().inside(Rect.of(Slot.ROW_TWO_SLOT_EIGHT, Slot.ROW_THREE_SLOT_EIGHT)));
        this.compose(new TextFieldFC("Input a number: ").inside(Rect.of(Slot.ROW_FOUR_SLOT_ONE, Slot.ROW_FOUR_SLOT_TWO)));
        this.compose(
                Button
                        .checkbox()
                        .checkedItem(Checkbox.checkedItem("Click me", "This is a <green>checked</green> checkbox"))
                        .uncheckedItem(Checkbox.uncheckedItem("Click me", "This is an <red>unchecked</red> checkbox"))
                        .build()
                        .configure(check -> check.checkedState().subscribe(now -> ctx.player.sendMessage(FlareUtil.text("<green>Toggled!"))))
                        .inside(Slot.ROW_FOUR_SLOT_SEVEN)
        );
        this.compose(
                Selection
                        .create(Arrays.stream(ExampleEnumeration.values()).toList(), ExampleEnumeration::toString)
                        .configure(select -> select.selectedState().subscribe(now -> {
                            assert now != null;
                            ctx.player.sendMessage(FlareUtil.text("<green>Selected: %s!".formatted(now)));
                        }))
                        .inside(Slot.ROW_FOUR_SLOT_EIGHT)
        );
        this.compose(new BarFC(.2f).inside(Rect.of(Slot.ROW_FIVE_SLOT_ONE, Slot.ROW_SIX_SLOT_NINE)));
        this.compose(
                Slideshow.create(
                        List.of(
                                Items.builder(Material.DIAMOND_SWORD),
                                Items.builder(Material.DIAMOND_PICKAXE),
                                Items.builder(Material.DIAMOND_AXE),
                                Items.builder(Material.DIAMOND_HOE),
                                Items.builder(Material.DIAMOND_SHOVEL)
                        ),
                        20
                ).inside(Slot.ROW_FIVE_SLOT_ONE)
        );
        this.compose(GoBackButton.create().inside(Slot.ROW_ONE_SLOT_ONE));
        this.compose(
                Modal.builder(Items.builder(Material.EMERALD))
                        .dimensions(Dimensions.THREE_BY_NINE)
                        .initializer(modal -> {
                            modal.compose(
                                    Placeholder.of(Items.builder(Material.GRAY_STAINED_GLASS_PANE)).inside(Slot.ALL)
                            );
                            modal.compose(
                                    GoBackButton.create().inside(Slot.ROW_THREE_SLOT_FIVE)
                            );
                            modal.compose(
                                    Button.create(
                                            Items.builder(Material.DIAMOND_BLOCK),
                                            Button.ClickHandler.cancelling((btn, e) -> e.getWhoClicked().sendMessage(FlareUtil.text("hi")))
                                    ).inside(Slot.ROW_TWO_SLOT_FIVE)
                            );
                            modal.compose(
                                    Slideshow.create(
                                            List.of(
                                                    Items.builder(Material.DIAMOND_SWORD),
                                                    Items.builder(Material.DIAMOND_PICKAXE),
                                                    Items.builder(Material.DIAMOND_AXE),
                                                    Items.builder(Material.DIAMOND_HOE),
                                                    Items.builder(Material.DIAMOND_SHOVEL)
                                            ),
                                            20
                                    ).inside(Slot.ROW_ONE_SLOT_FOUR)
                            );
                            modal.compose(Placeholder.of(Items.builder(Material.EMERALD).name("You can't see me!")).inside(Slot.ROW_SIX_SLOT_EIGHT));
                            modal.compose(
                                    Button.create(Items.builder(Material.EMERALD_BLOCK), Button.ClickHandler.cancelling((btn, e) -> e.getWhoClicked().closeInventory())).inside(Slot.ROW_ONE_SLOT_NINE)
                            );
                        })
                        .build()
                        .inside(Slot.ROW_ONE_SLOT_TWO)
        );
    }

    public record Context(Player player) {

    }

    enum ExampleEnumeration {
        VALUE_ONE,
        VALUE_TWO,
        VALUE_THREE,
        VALUE_FOUR,

        ;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
