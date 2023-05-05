package space.maxus.flare.ui.compose;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.PlayerFrameStateManager;
import space.maxus.flare.ui.space.Slot;

@ToString @EqualsAndHashCode(callSuper = true)
final class GoBackButtonImpl extends RootReferencing implements GoBackButton {
    private final ReactiveState<Boolean> disabled;
    private final @Nullable ItemProvider itemProvider;

    GoBackButtonImpl(@Nullable ItemProvider item, boolean disabled) {
        this.disabled = new ReactiveState<>(disabled);
        this.itemProvider = item;
    }

    @Override
    public @Nullable ItemStack renderAt(Slot slot) {
        HumanEntity player = root().getHolder().getInventoryNoRender().getViewers().stream().findFirst().orElse(null);
        if(player == null) // the frame was the first to be opened
            return null;
        ItemStack rendered = itemProvider == null ? GoBackButton.goBackItem(player).provide() : itemProvider.provide();
        return PlayerFrameStateManager.peekPrevious(player) == null ? null : rendered;
    }

    @Override
    public GoBackButton configure(Configurator<GoBackButton> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabled;
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        e.setCancelled(true);
        root().goBack(e.getWhoClicked());
    }
}
