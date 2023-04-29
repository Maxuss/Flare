package space.maxus.flare.item;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Data
public class StillItemProvider implements ItemProvider {
    private final @Nullable ItemStack stack;

    @Override
    public ItemStack provide() {
        return stack;
    }
}
