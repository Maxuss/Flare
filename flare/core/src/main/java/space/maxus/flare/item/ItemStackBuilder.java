package space.maxus.flare.item;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.util.FlareUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An ItemStack Builder, which is a wrapper around an ItemStack that contains multiple convenience methods
 * for constructing ItemStacks.
 */
public class ItemStackBuilder implements ItemProvider {
    private final ItemStack stack;
    private final @Nullable Player player;

    /**
     * Constructs a new ItemStackBuilder for the given Material.
     * @param material Material of the Item
     * @param player Used for PlaceholderAPI resolution. May be null.
     */
    public ItemStackBuilder(Material material, @Nullable Player player) {
        this.stack = new ItemStack(material);
        this.player = player;
    }

    /**
     * Changes item type without fully wiping its meta
     * @param type New item type
     * @return This builder
     */
    public ItemStackBuilder type(Material type) {
        this.stack.setType(type);
        return this;
    }

    /**
     * Sets the amount of items in this stack
     * @param count New item count. Must be within 0 to 128, otherwise Bukkit errors may arise
     * @return This builder
     */
    public ItemStackBuilder count(@IntRange(from = 0, to = 128) int count) {
        this.stack.setAmount(count);
        return this;
    }

    /**
     * Edits meta of this item
     * @param meta Consumer that edits meta of the item
     * @return This builder
     */
    public ItemStackBuilder editMeta(Consumer<ItemMeta> meta) {
        this.stack.editMeta(meta);
        return this;
    }

    /**
     * Edits meta of this item, using a generic type {@link M}
     * @param meta Consumer that edits meta of the item
     * @return This builder
     * @param <M> The type of meta.
     */
    public <M extends ItemMeta> ItemStackBuilder editTypedMeta(Consumer<M> meta) {
        this.stack.editMeta(FlareUtil.genericClass(), meta);
        return this;
    }

    /**
     * Adds all {@link ItemFlag}s to this item, essentially hiding all extra data from lore.
     * @return This builder
     */
    public ItemStackBuilder hideAllFlags() {
        this.stack.editMeta(meta -> meta.addItemFlags(ItemFlag.values()));
        return this;
    }

    /**
     * Adds certain provided item flags to this item
     * @param flags Flags to be added
     * @return This builder
     */
    public ItemStackBuilder hideFlags(ItemFlag... flags) {
        this.stack.editMeta(meta -> meta.addItemFlags(flags));
        return this;
    }

    /**
     * <b>Sets</b> lore of the item to the provided lore via {@link Items#loreMeta)}
     * @param lore Lore to be set
     * @return This builder
     */
    public ItemStackBuilder lore(@NotNull String lore) {
        this.stack.editMeta(Items.loreMeta(lore, player));
        return this;
    }

    /**
     * <b>Sets</b> this item's lore to provided component list
     * @param lore Lore to be set
     * @return This builder
     */
    public ItemStackBuilder lore(@NotNull List<Component> lore) {
        this.stack.editMeta(meta -> meta.lore(lore));
        return this;
    }

    /**
     * <b>Adds</b> provided lore to this item's lore via {@link FlareUtil#partitionString(String)}
     * @param lore Lore to be added to this item
     * @return This builder
     */
    public ItemStackBuilder addLore(@NotNull String lore) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.addAll(FlareUtil.partitionString(lore).stream().map(st -> FlareUtil.text("<gray>%s".formatted(st), player)).toList());
            meta.lore(currentLore);
        });
        return this;
    }

    /**
     * <b>Adds</b> all components to this item lore
     * @param lore Lore to be added to this item
     * @return This builder
     */
    public ItemStackBuilder addLore(@NotNull List<Component> lore) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.addAll(lore);
            meta.lore(currentLore);
        });
        return this;
    }

    /**
     * <b>Adds</b> a <b>single lore line</b> to this item lore
     * @param line Lore line to be added
     * @return This builder
     */
    public ItemStackBuilder addLoreLine(@NotNull String line) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.add(FlareUtil.text("<gray>%s".formatted(line), player));
            meta.lore(currentLore);
        });
        return this;
    }

    /**
     * <b>Adds</b> a <b>single lore line</b> to this item lore
     * @param line Lore line to be added
     * @return This builder
     */
    public ItemStackBuilder addLoreLine(@NotNull Component line) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.add(line);
            meta.lore(currentLore);
        });
        return this;
    }

    /**
     * Adds an empty line to this lore
     * @return This builder
     */
    public ItemStackBuilder padLore() {
        return addLoreLine(Component.empty());
    }

    /**
     * Sets this item name to provided name
     * @param name Name to be set
     * @return This builder
     */
    public ItemStackBuilder name(@NotNull String name) {
        this.stack.editMeta(meta -> meta.displayName(FlareUtil.text(name, player)));
        return this;
    }

    /**
     * Adds enchantment glint to this item.
     * <br>
     * You might want to call {@link #hideAllFlags()} or {@link #hideFlags(ItemFlag...)} after this to hide enchantment info
     * @return This builder
     */
    public ItemStackBuilder glint() {
        this.stack.editMeta(meta -> meta.addEnchant(Enchantment.DEPTH_STRIDER, 1, true));
        return this;
    }

    /**
     * Sets this item's head skin to the provided skin. <b>Will fail</b> if the item is not of type {@link Material#PLAYER_HEAD}
     * @param skin Base64 data of head skin. See {@link Items#head(String)} for more info
     * @return This builder
     */
    public ItemStackBuilder headSkin(@Nullable String skin) {
        Validate.isTrue(this.stack.getType() == Material.PLAYER_HEAD, "Tried to set head skin on item of type %s".formatted(this.stack.getType()));
        this.stack.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(FlareUtil.createProfile(skin)));
        return this;
    }

    /**
     * A utility method that branches configuration of this builder based on condition.
     * @param condition Condition to be checked
     * @param ifTrue Executed if condition is true
     * @param ifFalse Executed if condition is false
     * @return This builder
     */
    public ItemStackBuilder branch(boolean condition, Consumer<ItemStackBuilder> ifTrue, Consumer<ItemStackBuilder> ifFalse) {
        if (condition) {
            ifTrue.accept(this);
        } else {
            ifFalse.accept(this);
        }
        return this;
    }

    /**
     * A utility method that branches configuration of this builder based on condition.
     * @param condition Condition to be checked
     * @param ifTrue Executed if condition is true
     * @return This builder
     */
    public ItemStackBuilder branch(boolean condition, Consumer<ItemStackBuilder> ifTrue) {
        if (condition)
            ifTrue.accept(this);
        return this;
    }

    /**
     * Finishes this builder and returns <b>clone</b> of inner stack
     * @return Clone of inner stack
     */
    public ItemStack build() {
        return stack.clone();
    }

    @Override
    public ItemStack provide() {
        return stack;
    }
}
