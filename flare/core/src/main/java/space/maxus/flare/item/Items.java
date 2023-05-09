package space.maxus.flare.item;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.util.FlareUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * A utility class for manipulations with items inside Flare
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@UtilityClass
@Getter
@Slf4j
public class Items {
    /**
     * Returns a consumer that will set the lore of item
     * @param lore Lore to be transformed into components via {@link FlareUtil#partitionString(String)}
     * @return A consumer that takes item meta and applies lore to it
     * @param <T> Type of item meta
     */
    public <T extends ItemMeta> Consumer<T> loreMeta(String lore) {
        return meta -> applyLore(meta, lore);
    }

    /**
     * Returns a consumer that will set the lore of item
     * @param lore Lore to be transformed into components via {@link FlareUtil#partitionString(String)}
     * @param player Player used for PlaceholderAPI Placeholder resolution
     * @return A consumer that takes item meta and applies lore to it
     * @param <T> Type of item meta
     */
    public <T extends ItemMeta> Consumer<T> loreMeta(String lore, @Nullable Player player) {
        return meta -> applyLore(meta, lore, player);
    }

    /**
     * Constructs a new player head item with provided skin Base64 string.
     * <br>
     * Skin can be taken from <a href="https://minecraft-heads.com/">Minecraft Heads</a>, <a href="https://crafatar.com/">Crafatar</a> or any other API.
     * @param skin Base64 skin string to be applied to player
     * @return Constructed ItemStack
     */
    public ItemStack head(String skin) {
        return head(skin, meta -> { });
    }

    /**
     * Constructs a new player head item with provided skin Base64 string and additional meta.
     * <br>
     * Skin can be taken from <a href="https://minecraft-heads.com/">Minecraft Heads</a>, <a href="https://crafatar.com/">Crafatar</a> or any other API.
     * @param skin Base64 skin string to be applied to player
     * @param configurator Consumer that applies extra data to item meta
     * @return Constructed ItemStack
     */
    public ItemStack head(String skin, Consumer<SkullMeta> configurator) {
        return Items.<SkullMeta>withTypedMeta(Material.PLAYER_HEAD, meta -> {
            meta.setPlayerProfile(FlareUtil.createProfile(skin));
            configurator.accept(meta);
        });
    }

    /**
     * Constructs a new item with provided material and meta of certain type.
     * <br>
     * <b>NOTE:</b> will return {@link Items#genericErrorItem} if meta is not of expected type.
     * @param material Material of the item
     * @param configurator Consumer that takes ItemMeta of type {@link M} and applies extra data to it
     * @return A new ItemStack with provided material and meta.
     * @param <M> Type of meta to be applied. Must match meta of provided material
     */
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

    /**
     * Constructs a new item with provided material and meta.
     * @param material Material of the item
     * @param configurator Consumer that takes ItemMeta and applies extra data to it
     * @return A new ItemStack with provided material and meta.
     */
    public ItemStack withMeta(Material material, @NotNull Consumer<ItemMeta> configurator) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        configurator.accept(meta);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * A generic error item returned when an error happens while building an item.
     * @deprecated Scheduled to be removed when local error handling is implemented (approx. 1.1.0)
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "1.1.0")
    @Deprecated(since = "0.9.0", forRemoval = true)
    @Getter
    public final ItemStack genericErrorItem = head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBlZTI4YjNkZjBkMDI1MGUyNDE2ZTJhNjJkN2RkY2Y5ZjJjOWNjODIzNjkwNDQ2OWZhMWY5MWYyYTk1OTVmZiJ9fX0=",
            loreMeta("An <red>unknown error</red> occurred when building this item.")
    );

    /**
     * Constructs a new item stack with provided lore
     * @param material Material of the item
     * @param lore Lore to be applied to item via {@link Items#loreMeta(String)}
     * @return A new item stack with provided lore
     */
    public ItemStack withLore(Material material, @NotNull String lore) {
        return withMeta(material, meta -> applyLore(meta, lore));
    }

    /**
     * Constructs a new item stack with provided lore
     * @param material Material of the item
     * @param name Name to be applied to item
     * @return A new item stack with provided lore
     */
    public ItemStack withName(Material material, @NotNull String name) {
        return withName(material, name, null);
    }

    /**
     * Constructs a new item stack with provided lore
     * @param material Material of the item
     * @param name Name to be applied to item
     * @param player Player used for PlaceholderAPI placeholder resolution
     * @return A new item stack with provided lore
     */
    public ItemStack withName(Material material, @NotNull String name, @Nullable Player player) {
        return withMeta(material, meta -> meta.displayName(FlareUtil.text(name, player)));
    }

    /**
     * Constructs a new item stack builder for provided item type
     * @param material Material of the item
     * @return A new item stack builder
     */
    public ItemStackBuilder builder(Material material) {
        return new ItemStackBuilder(material, null);
    }

    /**
     * Constructs a new item stack builder for provided item type with PlaceholderAPI placeholder support
     * @param material Material of the item
     * @param player Player to be used for PlaceholderAPI placeholder resolution
     * @return A new item stack builder
     */
    public ItemStackBuilder builder(Material material, @Nullable Player player) {
        return new ItemStackBuilder(material, player);
    }

    /**
     * Applies lore to provided item meta
     * @param meta ItemMeta to be modified with lore
     * @param lore Lore to be applied to item via {@link FlareUtil#partitionString(String)}
     */
    private void applyLore(@NotNull ItemMeta meta, String lore) {
        applyLore(meta, lore, null);
    }

    /**
     * Applies lore to provided item meta
     * @param meta ItemMeta to be modified with lore
     * @param lore Lore to be applied to item via {@link FlareUtil#partitionString(String)}
     * @param player Player used for PlaceholderAPI placeholder resolution
     */
    private void applyLore(@NotNull ItemMeta meta, String lore, @Nullable Player player) {
        if (lore.isEmpty())
            return;
        meta.lore(FlareUtil.partitionString(lore).stream().map(part -> FlareUtil.text("<gray>%s".formatted(part), player)).filter(cmp -> cmp != Component.empty()).toList());
    }


    /**
     * Constructs an ItemStack with empty name and lore of provided Material
     * @param material Material of the ItemStack to be constructed
     * @return Constructed ItemStack
     */
    public static ItemStack empty(Material material) {
        return Items.builder(material).name("").lore(List.of()).hideAllFlags().build();
    }

    /**
     * Constructs an empty ItemStack of type {@link Material#GRAY_STAINED_GLASS_PANE}. Can be useful for creating backgrounds
     * @return Empty ItemStack of type {@link Material#GRAY_STAINED_GLASS_PANE}
     */
    public static ItemStack empty() {
        return empty(Material.GRAY_STAINED_GLASS_PANE);
    }
}
