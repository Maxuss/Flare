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
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactiveSubscriber;
import space.maxus.flare.util.SimpleInvBoundPrompt;

public sealed interface TextInput extends ProviderRendered, Configurable<TextInput> permits TextInputImpl {
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
    default boolean isNotDisabled() {
        return !disabledState().get();
    }
    default void setDisabled(boolean disabled) {
        disabledState().set(disabled);
    }
    default @NotNull String getText() {
        return textState().get();
    }
    default void setText(@NotNull String text) {
        textState().set(text);
    }
    default @NotNull String getPrompt() {
        return promptState().get();
    }
    default void setPrompt(String prompt) {
        promptState().set(prompt);
    }

    ReactiveState<String> textState();
    ReactiveState<String> promptState();
    ReactiveState<Boolean> disabledState();

    @Override
    default TextInput configure(@NotNull Configurator<TextInput> configurator) {
        configurator.configure(this);
        return this;
    }

    default void onTextChange(ReactiveSubscriber<@NotNull String> change) {
        this.textState().subscribe(change);
    }

    @Override
    default void click(@NotNull InventoryClickEvent e) {
        if(isDisabled())
            return;
        Inventory current = e.getInventory();
        Prompt prompt = new SimpleInvBoundPrompt(getPrompt(), textState(), current);
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
    }
}
