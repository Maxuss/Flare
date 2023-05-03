package space.maxus.flare.item;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.util.FlareUtil;

import java.util.function.Consumer;

@UtilityClass
@Getter
@Slf4j
public class Items {
    public <T extends ItemMeta> Consumer<T> loreMeta(String lore) {
        return meta -> applyLore(meta, lore);
    }    @Getter
    private final ItemStack genericErrorItem = head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBlZTI4YjNkZjBkMDI1MGUyNDE2ZTJhNjJkN2RkY2Y5ZjJjOWNjODIzNjkwNDQ2OWZhMWY5MWYyYTk1OTVmZiJ9fX0=",
            loreMeta("An <red>unknown error</red> occurred when building this item.")
    );

    public ItemStack head(String skin) {
        return head(skin, meta -> {
        });
    }

    public ItemStack head(String skin, Consumer<SkullMeta> configurator) {
        return Items.<SkullMeta>withTypedMeta(Material.PLAYER_HEAD, meta -> {
            meta.setPlayerProfile(FlareUtil.createProfile(skin));
            configurator.accept(meta);
        });
    }

    @SuppressWarnings("unchecked")
    public <M extends ItemMeta> ItemStack withTypedMeta(Material material, @NotNull Consumer<M> configurator) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        try {
            configurator.accept((M) meta);
        } catch (ClassCastException e) {
            log.error("ItemMeta of type {} does not match expected type of {}", meta.getClass().getName(), new TypeToken<M>() {
            }.getType().getTypeName());
            return genericErrorItem;
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack withMeta(Material material, @NotNull Consumer<ItemMeta> configurator) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        configurator.accept(meta);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack withLore(Material material, @NotNull String lore) {
        return withMeta(material, meta -> applyLore(meta, lore));
    }

    public ItemStack withName(Material material, @NotNull String name) {
        return withMeta(material, meta -> meta.displayName(FlareUtil.text(name)));
    }

    public ItemStackBuilder builder(Material material) {
        return new ItemStackBuilder(material);
    }

    private void applyLore(@NotNull ItemMeta meta, String lore) {
        meta.lore(FlareUtil.partitionString(lore).stream().map(part -> FlareUtil.text("<gray>%s".formatted(part))).toList());
    }


}
