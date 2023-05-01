package space.maxus.flare.ui.compose;

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
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactiveSubscriber;
import space.maxus.flare.util.SimpleInvBoundPrompt;
import space.maxus.flare.util.ValidatingInvBoundPrompt;
import space.maxus.flare.util.Validator;

public interface TextInput extends ProviderRendered, Configurable<TextInput> {
    @Contract("_ -> new")
    static @NotNull TextInput text(ItemProvider provider) {
        return new TextInputImpl(provider, false);
    }

    @Contract("_, _ -> new")
    static @NotNull TextInput text(ItemProvider provider, boolean disabled) {
        return new TextInputImpl(provider, disabled);
    }

    static @NotNull Builder builder(@NotNull ItemProvider provider) {
        return new TextInputImpl.Builder(provider);
    }

    default boolean isDisabled() {
        return disabledState().get();
    }

    default void setDisabled(boolean disabled) {
        disabledState().set(disabled);
    }

    default boolean isNotDisabled() {
        return !disabledState().get();
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

    ReactiveState<Boolean> disabledState();

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
