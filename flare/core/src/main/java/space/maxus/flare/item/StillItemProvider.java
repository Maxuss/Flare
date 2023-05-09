package space.maxus.flare.item;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An item provider that constantly returns the same stack.
 * <br>
 * <b>NOTE:</b> the stack returned is <b>not</b> cloned.
 */
@Data
public class StillItemProvider implements ItemProvider {
    private final @Nullable ItemStack stack;

    @Override
    public ItemStack provide() {
        return stack;
    }
}
