package space.maxus.flare.ui.compose;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactiveSubscriber;
import space.maxus.flare.util.SimpleInvBoundPrompt;
import space.maxus.flare.util.ValidatingInvBoundPrompt;
import space.maxus.flare.util.Validator;

public interface TextInput extends Disable, ProviderRendered, Configurable<TextInput> {
    static @NotNull ItemStackBuilder inputItemBuilder(@NotNull String currentText, String name, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin(
                        currentText.length() == 0 ?
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY4ZmRlMDNmZWY3ZTcxYmQ0OTk2YWM2NWU5ZDE5MzBhNjJiOGZjYWUxZDU1ZTg0ZDUxYzRhNDkxYzJkODY0ZCJ9fX0=" :
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWFmMmIwZDUwM2FjYTFiOWZmODgxNTYwYjM4ZDk5NGM2ODdlNzAxZGU1ZjNmMTQ0ZmVlZmY1Y2NjNGQ2MTkxOCJ9fX0="
                )
                .name("<gray>%s <dark_gray>%s[⌘]".formatted(name, currentText.length() == 0 ? "(empty) " : ""))
                .branch(
                        currentText.length() == 0,
                        builder -> builder.lore(description),
                        builder -> builder.addLoreLine("Current value: <green>%s".formatted(currentText)).addLore(description)
                )
                .addLoreLine(currentText.length() == 0 ? "<dark_gray>Click to input text" : "<dark_gray>Click to edit text")
                .hideAllFlags()
                ;
    }

    static @NotNull ItemProvider inputItem(@NotNull ReactiveState<String> text, String name, String description) {
        return Reactive.item(text, newText -> inputItemBuilder(newText == null ? "" : newText, name, description).build());
    }

    @Contract("_ -> new")
    static @NotNull TextInput text(ItemProvider provider) {
        return new TextInputImpl(provider, false);
    }

    @Contract("_, _ -> new")
    static @NotNull TextInput text(ItemProvider provider, boolean disabled) {
        return new TextInputImpl(provider, disabled);
    }

    static @NotNull Builder builder(@Nullable ItemProvider provider) {
        return new TextInputImpl.Builder(provider, null, null);
    }
    static @NotNull Builder builder(@Nullable String name, @Nullable String description) {
        return new TextInputImpl.Builder(null, name, description);
    }
    static @NotNull Builder builder() {
        return new TextInputImpl.Builder(null, "Text Input", "");
    }
    default @NotNull String getText() {
        return onTextChange().get();
    }

    default void setText(@NotNull String text) {
        onTextChange().set(text);
    }

    default @NotNull String getPrompt() {
        return promptState().get();
    }

    default void setPrompt(String prompt) {
        promptState().set(prompt);
    }

    ReactiveState<String> onTextChange();

    ReactiveState<String> promptState();
    @Nullable Validator getValidator();

    @Override
    default TextInput configure(@NotNull Configurator<TextInput> configurator) {
        configurator.configure(this);
        return this;
    }

    default void onTextChange(ReactiveSubscriber<@NotNull String> change) {
        this.onTextChange().subscribe(change);
    }

    @Override
    default void click(@NotNull InventoryClickEvent e) {
        if (isDisabled())
            return;
        Inventory current = e.getInventory();
        Prompt prompt = getValidator() == null ? new SimpleInvBoundPrompt(getPrompt(), onTextChange(), current) : new ValidatingInvBoundPrompt(getValidator(), getPrompt(), onTextChange(), current);
        Player conversible = (Player) e.getWhoClicked();
        conversible.closeInventory(InventoryCloseEvent.Reason.TELEPORT);
        Conversation conv =
                new ConversationFactory(Flare.getHook())
                        .withLocalEcho(false)
                        .withFirstPrompt(prompt)
                        .buildConversation(conversible);
        conv.begin();
    }

    interface Builder {
        TextInput build();

        Builder disabled(boolean disabled);

        Builder prompt(String prompt);

        Builder initialText(String initialText);

        Builder validate(Validator validator);
    }
}
