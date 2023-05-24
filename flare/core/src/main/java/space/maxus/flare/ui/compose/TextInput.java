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
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableLike;
import space.maxus.flare.util.SimpleInvBoundPrompt;
import space.maxus.flare.util.ValidatingInvBoundPrompt;
import space.maxus.flare.util.Validator;

/**
 * A text input is a component that can handle user input.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#textinput">Text Input</a>
 */
public interface TextInput extends Disable, ProviderRendered, Configurable<TextInput> {
    /**
     * Returns an item builder for a text input
     * @param currentText Current text value
     * @param name Extra message in item name
     * @param description Description in item lore
     * @return An item builder for a text input
     */
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

    /**
     * Returns an item provider for a text input
     * @param text Reactive state of the text value
     * @param name Extra message in item name
     * @param description Description in item lore
     * @return An item builder for a text input
     */
    static @NotNull ItemProvider inputItem(@NotNull ReactiveState<String> text, String name, String description) {
        return Reactive.item(text, newText -> inputItemBuilder(newText == null ? "" : newText, name, description).build());
    }

    /**
     * Constructs a new text input with an item provider
     * @param provider The item provider to use
     * @return A new text input
     */
    @Contract("_ -> new")
    static @NotNull TextInput of(ItemProvider provider) {
        return new TextInputImpl(provider, false);
    }

    /**
     * Constructs a new text input with an item provider
     * @param provider The item provider to use
     * @param disabled Whether the text input should be disabled
     * @return A new text input
     */
    @Contract("_, _ -> new")
    static @NotNull TextInput of(ItemProvider provider, boolean disabled) {
        return new TextInputImpl(provider, disabled);
    }

    /**
     * Constructs a new text input builder
     * @param provider Item provider to use
     * @return A new text input builder
     */
    static @NotNull Builder builder(@Nullable ItemProvider provider) {
        return new TextInputImpl.Builder(provider, null, null);
    }

    /**
     * Constructs a new text input builder
     * @param name Extra message in item name
     * @param description Extra message in item lore
     * @return A new text input builder
     */
    static @NotNull Builder builder(@Nullable String name, @Nullable String description) {
        return new TextInputImpl.Builder(null, name, description);
    }

    /**
     * Constructs a new text input builder
     * @return A new text input builder
     */
    static @NotNull Builder builder() {
        return new TextInputImpl.Builder(null, "Text Input", "");
    }

    /**
     * Returns current text value
     * @return Current text value
     */
    default @NotNull String getText() {
        return textState().get();
    }

    /**
     * Sets current text
     * @param text Text to be set
     */
    default void setText(@NotNull String text) {
        textState().set(text);
    }

    /**
     * Returns the default prompt
     * @return The default prompt
     */
    default @NotNull String getPrompt() {
        return promptState().get();
    }

    /**
     * Sets the default prompt
     * @param prompt The prompt to set
     */
    default void setPrompt(String prompt) {
        promptState().set(prompt);
    }

    /**
     * Returns the input text reactive state
     * @return The input text reactive state
     */
    ReactiveState<String> textState();

    /**
     * Returns the prompt reactive state
     * @return The input text reactive state
     */
    ReactiveState<String> promptState();

    /**
     * Returns the input validator
     * @return The input validator
     */
    @Nullable Validator getValidator();

    @Override
    default TextInput configure(@NotNull Configurator<TextInput> configurator) {
        configurator.configure(this);
        return this;
    }

    /**
     * Adds a handler to text change
     * @param change Handler for the text change
     */
    default void onTextChange(ReactiveSubscriber<@NotNull String> change) {
        this.textState().subscribe(change);
    }

    @Override
    default void click(@NotNull InventoryClickEvent e) {
        if (isDisabled())
            return;
        Inventory current = e.getInventory();
        Prompt prompt = getValidator() == null ? new SimpleInvBoundPrompt(getPrompt(), textState(), current) : new ValidatingInvBoundPrompt(getValidator(), getPrompt(), textState(), current);
        Player conversible = (Player) e.getWhoClicked();
        conversible.closeInventory(InventoryCloseEvent.Reason.TELEPORT);
        Conversation conv =
                new ConversationFactory(Flare.getInstance())
                        .withLocalEcho(false)
                        .withFirstPrompt(prompt)
                        .buildConversation(conversible);
        conv.begin();
    }

    /**
     * The builder for text input
     */
    interface Builder extends ComposableLike {
        /**
         * Builds this text input
         * @return This text input
         */
        TextInput build();

        /**
         * Sets the disabled state
         * @param disabled The disabled state
         * @return This builder
         */
        Builder disabled(boolean disabled);

        /**
         * Sets the default prompt
         * @param prompt Prompt to be set
         * @return This builder
         */
        Builder prompt(String prompt);

        /**
         * Sets the initial text value (defaults to empty string)
         * @param initialText The initial text value
         * @return This builder
         */
        Builder initialText(String initialText);

        /**
         * Adds a validator for text input
         * @param validator Validator to be added
         * @return This builder
         * @see Validator
         * @see space.maxus.flare.util.Validators
         */
        Builder validate(Validator validator);

        @Override
        default Composable asComposable() {
            return build();
        }
    }
}
