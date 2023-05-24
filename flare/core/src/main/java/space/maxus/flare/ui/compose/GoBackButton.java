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

/**
 * A button that takes player to previously opened frame. Only renders if it exists.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#gobackbutton">Go Back Button</a>
 */
public interface GoBackButton extends Disable, Composable, Configurable<GoBackButton> {
    /**
     * Returns the default builder for go back button
     * @param frame The previous frame
     * @return The default builder for go back button
     */
    static ItemStackBuilder goBackItemBuilder(@Nullable Frame frame) {
        return Items.builder(Material.ARROW)
                .name("<gray>Go Back <dark_gray>[◀]")
                .branch(frame == null,
                        bld -> bld.addLoreLine("Back to <dark_gray>unknown"),
                        bld -> bld.addLoreLine("Back to <green>%s".formatted(Objects.requireNonNull(frame).getTitle()))
                )
                .hideAllFlags();
    }

    /**
     * Returns a default go back item
     * @param player Player for whom to build this button
     * @return Default go back button
     */
    @Contract(pure = true)
    static @NotNull ItemProvider goBackItem(HumanEntity player) {
        return () -> goBackItemBuilder(PlayerFrameStateManager.peekPrevious(player)).build();
    }

    /**
     * Returns a default go back item
     * @return Default go back item
     */
    @Contract(" -> new")
    static @NotNull GoBackButton create() {
        return new GoBackButtonImpl(null, false);
    }
}
