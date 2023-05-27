package space.maxus.flare.extern.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Slot;

@EqualsAndHashCode(callSuper = true)
@Data @AllArgsConstructor
public class ConfigFrame extends SimpleFrame {
    private ConfigLayout layout;

    @Override
    public void init() {
        layout.getComposed().forEach(this::compose);
    }

    @Override
    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        try {
            layout.getGenericClick().invoke(this, slot, e);
        } catch (Throwable ex) {
            Flare.logger().error("Failed to invoke generic click on frame {}! Error: {}", layout.getMetadata().id(), ex);
        }
    }

    @Override
    public boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        try {
            return (boolean) layout.getLeftClick().invoke(this, slot, e);
        } catch (Throwable ex) {
            Flare.logger().error("Failed to invoke generic click on frame {}! Error: {}", layout.getMetadata().id(), ex);
            return true;
        }
    }

    @Override
    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        try {
            return (boolean) layout.getRightClick().invoke(this, slot, e);
        } catch (Throwable ex) {
            Flare.logger().error("Failed to invoke generic click on frame {}! Error: {}", layout.getMetadata().id(), ex);
            return true;
        }
    }

    @Override
    public boolean shiftRequest(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        try {
            return (boolean) layout.getShiftInside().invoke(this, stack, e);
        } catch (Throwable ex) {
            Flare.logger().error("Failed to invoke generic click on frame {}! Error: {}", layout.getMetadata().id(), ex);
            return true;
        }
    }

    @Override
    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        try {
            return (boolean) layout.getShiftFrom().invoke(this, slot, e);
        } catch (Throwable ex) {
            Flare.logger().error("Failed to invoke generic click on frame {}! Error: {}", layout.getMetadata().id(), ex);
            return true;
        }
    }
}
