package space.maxus.flare.ui;

import lombok.SneakyThrows;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;

public interface Composable {
    Frame root();
    void injectRoot(Frame root);

    ItemStack renderAt(Slot slot);

    default @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        return new PackedComposable(space, this);
    }

    default boolean rightClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean leftClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean shiftFrom(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default void click(@NotNull InventoryClickEvent e) {
        // No extra calculations there
    }

    default boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        // No extra calculations there
        return true;
    }

    default void markDirty() {
        root().markDirty();
    }

    default Composable bindState(ReactiveState<?> state) {
        state.subscribe(v -> markDirty());
        return this;
    }

    @SneakyThrows
    default <T> @NotNull T context() {
        return root().context();
    }

    default <T> @Nullable T contextOrNull() {
        return root().contextOrNull();
    }
}
