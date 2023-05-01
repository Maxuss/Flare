package flare;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.extra.Checkbox;
import space.maxus.flare.ui.compose.extra.Selection;
import space.maxus.flare.ui.frames.ParamFrame;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.Arrays;
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
                        .inside(Slot.ROW_FIVE_SLOT_ONE)
        );
        this.compose(
                Selection
                        .create(Arrays.stream(ExampleEnumeration.values()).toList(), ExampleEnumeration::toString)
                        .configure(select -> select.selectedState().subscribe(now -> {
                            assert now != null;
                            ctx.player.sendMessage(FlareUtil.text("<green>Selected: %s!".formatted(now)));
                        }))
                        .inside(Slot.ROW_FIVE_SLOT_TWO)
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
