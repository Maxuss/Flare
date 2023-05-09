package space.maxus.flare.item;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
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

    public ItemStackBuilder editMeta(Consumer<ItemMeta> meta) {
        this.stack.editMeta(meta);
        return this;
    }

    public <M extends ItemMeta> ItemStackBuilder editTypedMeta(Consumer<M> meta) {
        this.stack.editMeta(FlareUtil.genericClass(), meta);
        return this;
    }

    public ItemStackBuilder hideAllFlags() {
        this.stack.editMeta(meta -> meta.addItemFlags(ItemFlag.values()));
        return this;
    }

    public ItemStackBuilder hideFlags(ItemFlag... flags) {
        this.stack.editMeta(meta -> meta.addItemFlags(flags));
        return this;
    }

    public ItemStackBuilder lore(@NotNull String lore) {
        this.stack.editMeta(Items.loreMeta(lore, player));
        return this;
    }

    public ItemStackBuilder lore(@NotNull List<Component> lore) {
        this.stack.editMeta(meta -> meta.lore(lore));
        return this;
    }

    public ItemStackBuilder addLore(@NotNull String lore) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.addAll(FlareUtil.partitionString(lore).stream().map(st -> FlareUtil.text("<gray>%s".formatted(st), player)).toList());
            meta.lore(currentLore);
        });
        return this;
    }

    public ItemStackBuilder addLore(@NotNull List<Component> lore) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.addAll(lore);
            meta.lore(currentLore);
        });
        return this;
    }

    public ItemStackBuilder addLoreLine(@NotNull String line) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.add(FlareUtil.text("<gray>%s".formatted(line), player));
            meta.lore(currentLore);
        });
        return this;
    }

    public ItemStackBuilder addLoreLine(@NotNull Component line) {
        this.stack.editMeta(meta -> {
            List<Component> currentLore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());
            currentLore.add(line);
            meta.lore(currentLore);
        });
        return this;
    }

    public ItemStackBuilder padLore() {
        return addLoreLine(Component.empty());
    }

    public ItemStackBuilder name(@NotNull String name) {
        this.stack.editMeta(meta -> meta.displayName(FlareUtil.text(name, player)));
        return this;
    }

    public ItemStackBuilder glint() {
        this.stack.editMeta(meta -> meta.addEnchant(Enchantment.DEPTH_STRIDER, 1, true));
        return this;
    }

    public ItemStackBuilder headSkin(@Nullable String skin) {
        Validate.isTrue(this.stack.getType() == Material.PLAYER_HEAD, "Tried to set head skin on item of type %s".formatted(this.stack.getType()));
        this.stack.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(FlareUtil.createProfile(skin)));
        return this;
    }

    public ItemStackBuilder branch(boolean condition, Consumer<ItemStackBuilder> ifTrue, Consumer<ItemStackBuilder> ifFalse) {
        if (condition) {
            ifTrue.accept(this);
        } else {
            ifFalse.accept(this);
        }
        return this;
    }

    public ItemStackBuilder branch(boolean condition, Consumer<ItemStackBuilder> ifTrue) {
        if (condition)
            ifTrue.accept(this);
        return this;
    }

    public ItemStack build() {
        return stack.clone();
    }

    @Override
    public ItemStack provide() {
        return stack;
    }
}
