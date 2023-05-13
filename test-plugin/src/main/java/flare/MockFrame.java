package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.compose.*;
import space.maxus.flare.ui.compose.complex.Modals;
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
                        .of(Arrays.stream(ExampleEnumeration.values()).toList(), ExampleEnumeration::toString)
                        .configure(select -> select.selectedState().subscribe(now -> {
                            assert now != null;
                            ctx.player.sendMessage(FlareUtil.text("<green>Selected: %s!".formatted(now)));
                        }))
                        .inside(Slot.ROW_FOUR_SLOT_EIGHT)
        );
        this.compose(new BarFC(.2f).inside(Rect.of(Slot.ROW_FIVE_SLOT_ONE, Slot.ROW_SIX_SLOT_NINE)));
        this.compose(
                Slideshow.of(
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
                Modals.YesNoModal.builder()
                        .name("Yes/No Modal")
                        .onAccept(() -> getViewer().sendMessage(FlareUtil.text("<gold>Accepted!")))
                        .onDecline(() -> getViewer().sendMessage(FlareUtil.text("<gold>Declined!")))
                        .declineName("Decline This")
                        .acceptName("Accept This")
                        .extraInformation("Click button to the right to accept, or click the button on the left to decline.")
                        .description("Try testing yes/no stuff")
                        .build()
                        .inside(Slot.ROW_ONE_SLOT_TWO)
        );
        this.compose(
                ContainerSlot.builder()
                        .onPut((item, e) -> Flare.logger().info("PUTTING {}", item))
                        .onTake((item, e) -> Flare.logger().info("TAKING {}", item))
                        .inside(Slot.ROW_ONE_SLOT_THREE)
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
