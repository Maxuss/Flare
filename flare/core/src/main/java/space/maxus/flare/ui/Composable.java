package space.maxus.flare.ui;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;

/**
 * Represents a Component that can be rendered inside an inventory.
 * <br />
 * If you wish to implement your own composable, it is <b>not recommended</b> to implement
 * this interface directly, instead, have a look at {@link space.maxus.flare.ui.compose.FunctionComposable}
 */
public interface Composable extends ComposableLike {
    /**
     * Returns the frame this component belongs to
     * @return The frame this component belongs to
     */
    Frame root();

    /**
     * Injects frame into this component, the frame can then be retrieved from {@link #root()}
     * @param root Frame to be injected
     * @apiNote This method is only used internally by Flare.
     */
    @ApiStatus.Internal
    void injectRoot(Frame root);

    /**
     * Renders this component at the provided slot.
     * @param slot Slot to render at
     * @return The rendered item stack
     */
    ItemStack renderAt(Slot slot);

    /**
     * Destroys this composable and its underlying resources. Overridden by implementors.
     */
    @ApiStatus.OverrideOnly
    default void destroy() {
        // No destruction logic by default
    }

    /**
     * Restores this composable to its previous state. Overridden by implementors.
     */
    @ApiStatus.OverrideOnly
    default void restore() {
        // No state restoration logic by default
    }

    /**
     * Fits this composable inside the provided space, essentially returning a pair of this component and the space in the form of a {@link PackedComposable}.
     * @param space Space to fit inside
     * @return A pair of this component and the space in the form of a {@link PackedComposable}
     */
    @Override
    default @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        return new PackedComposable(space, this);
    }

    /**
     * Called when this component is <b>right clicked</b> inside interface
     * @param e The click event
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    default boolean rightClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    /**
     * Called when this component is <b>left clicked</b> inside interface
     * @param e The click event
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    default boolean leftClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    /**
     * Called when this component is <b>shift clicked</b> inside interface
     * @param e The click event
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    default boolean shiftFrom(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    /**
     * Called when this component is <b>attempted to put items into</b> by shift click. This means that a player clicked on an item inside <em>their</em> inventory,
     * and Flare attempts to fit item inside this composable.
     * @param e The click event
     * @param stack Stack that is attempted to be moved.
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    default boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    /**
     * Called each time this item is clicked, no matter if it is a right click, left click, middle click, etc.
     * @param e The click event
     */
    @ApiStatus.OverrideOnly
    default void click(@NotNull InventoryClickEvent e) {
        // No extra calculations there
    }

    /**
     * Called when items are dragged across this composable inside interface.
     * @param newItems Items that are dragged inside this composable's space
     * @param e The drag event
     * @return True if the event should be cancelled, false otherwise.
     * @apiNote This method is currently experimental and may not work correctly
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Experimental
    default boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        // No extra calculations there
        return true;
    }

    /**
     * Marks this component dirty, meaning its area will be redrawn next tick.
     */
    default void markDirty() {
        root().markDirty(this);
    }

    /**
     * Binds provided ReactiveState to this Composable. This means that every time the ReactiveState changes
     * its value, this component will be marked dirty.
     * <p>
     * Not to be confused with {@link #into(ReactiveState)}.
     *
     * @param state The state to be bound
     * @return This composable
     */
    default Composable bind(ReactiveState<?> state) {
        state.subscribe(v -> markDirty());
        return this;
    }

    /**
     * Sets provided ReactiveState value to this Composable reference.
     * <p>
     * Not to be confused with {@link #bind(ReactiveState)}.
     *
     * @param state The state to be set
     * @param <T>   The generic type of this composable
     * @return This composable
     */
    @SuppressWarnings("unchecked")
    default <T extends Composable> Composable into(ReactiveState<T> state) {
        state.set((T) this);
        return this;
    }

    /**
     * Attempts to get context of this composable's frame. Throws an exception if it is null.
     * @return Context of provided type
     * @param <T> Expected type of context
     */
    @SneakyThrows
    default <T> @NotNull T context() {
        return root().context();
    }

    /**
     * Attempts to get context of this composable's frame.
     * @return Context of provided type or null.
     * @param <T> Expected type of context
     */
    default <T> @Nullable T contextOrNull() {
        return root().contextOrNull();
    }

    /**
     * Gets the current viewer of this composable
     * @return Current viewer of this composable
     */
    default Player viewer() {
        return root().getViewer();
    }

    @Override
    default Composable asComposable() {
        return this;
    }
}
