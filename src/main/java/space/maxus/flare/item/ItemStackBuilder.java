package space.maxus.flare.item;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.util.FlareUtil;

import java.util.function.Consumer;

public class ItemStackBuilder implements ItemProvider {
    private final ItemStack stack;

    public ItemStackBuilder(Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemStackBuilder type(Material type) {
        this.stack.setType(type);
        return this;
    }

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
        this.stack.editMeta(Items.loreMeta(lore));
        return this;
    }

    public ItemStackBuilder name(@NotNull String name) {
        this.stack.editMeta(meta -> meta.displayName(FlareUtil.text(name)));
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
        if(condition) {
            ifTrue.accept(this);
        } else {
            ifFalse.accept(this);
        }
        return this;
    }

    public ItemStackBuilder branch(boolean condition, Consumer<ItemStackBuilder> ifTrue) {
        if(condition)
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
