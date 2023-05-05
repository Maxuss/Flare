package space.maxus.flare.ui.compose;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

import java.util.Objects;

public interface GoBackButton extends Disable, Composable, Configurable<GoBackButton> {
    static ItemStackBuilder goBackItemBuilder(@Nullable Frame frame) {
        return Items.builder(Material.ARROW)
                .name("<gray>Go Back <dark_gray>[◀]")
                .branch(frame == null,
                        bld -> bld.addLoreLine("Back to <dark_gray>unknown"),
                        bld -> bld.addLoreLine("Back to <green>%s".formatted(Objects.requireNonNull(frame).getTitle()))
                )
                .hideAllFlags();
    }

    @Contract(pure = true)
    static @NotNull ItemProvider goBackItem(HumanEntity player) {
        return () -> goBackItemBuilder(PlayerFrameStateManager.peekPrevious(player)).build();
    }

    @Contract(" -> new")
    static @NotNull GoBackButton create() {
        return new GoBackButtonImpl(null, false);
    }
}
