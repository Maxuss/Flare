package space.maxus.flare.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;
import java.util.stream.Collectors;

public class ClickHandler implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!(e.getInventory().getHolder() instanceof ReactiveInventoryHolder flare))
            return;
        Frame frame = flare.getFrame();

        if(e.isShiftClick() && e.getClickedInventory() != e.getView().getTopInventory()) {
            // we just shift clicked to get items inside the inventory
            if(e.getCurrentItem() == null) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(frame.fireShiftRequest(e.getCurrentItem(), e));
        }
        if(e.getClickedInventory() != e.getView().getTopInventory()) {
            return;
        }
        if(e.getClick().isCreativeAction())
            e.setCancelled(true);
        frame.fireGenericClick(Slot.ofRaw(e.getSlot()), e);
        if(e.isShiftClick()) {
            e.setCancelled(frame.fireShiftClick(Slot.ofRaw(e.getSlot()), e));
        } else if(e.isLeftClick()) {
            e.setCancelled(frame.fireLeftClick(Slot.ofRaw(e.getSlot()), e));
        } else if(e.isRightClick()) {
            e.setCancelled(frame.fireRightClick(Slot.ofRaw(e.getSlot()), e));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(e.getInventory() != e.getView().getTopInventory() || !(e.getInventory().getHolder() instanceof ReactiveInventoryHolder flare))
            return;
        Frame frame = flare.getFrame();
        e.setCancelled(frame.fireDrag(
                e.getNewItems().entrySet().stream()
                        .map(each ->
                                Map.entry(Slot.ofRaw(each.getKey()), each.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                e));
    }
}
