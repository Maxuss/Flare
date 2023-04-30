package space.maxus.flare.ui.compose;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.util.Validator;

@ToString
@EqualsAndHashCode(callSuper = true)
final class TextInputImpl extends RootReferencing implements TextInput {
    @Getter
    private final ItemProvider provider;
    private final ReactiveState<String> textState;
    private final ReactiveState<String> promptState;
    private final ReactiveState<Boolean> disabledState;
    @Getter
    private final @Nullable Validator validator;

    TextInputImpl(ItemProvider provider, boolean disabled) {
        this(provider, disabled, "", "<yellow>Input text:", null);
    }

    TextInputImpl(ItemProvider provider, boolean disabled, String initialText, String prompt, @Nullable Validator validator) {
        this.provider = provider;
        this.textState = new ComposableReactiveState<>(initialText, this);
        this.promptState = new ComposableReactiveState<>(prompt, this);
        this.disabledState = new ComposableReactiveState<>(disabled, this);
        this.validator = validator;
    }

    @Override
    public ReactiveState<String> onTextChange() {
        return textState;
    }

    @Override
    public ReactiveState<String> promptState() {
        return promptState;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @RequiredArgsConstructor
    static final class Builder implements TextInput.Builder {
        private final @NotNull ItemProvider provider;
        private @NotNull String prompt = "<yellow>Input text";
        private @NotNull String initialText = "";
        private boolean disabled = false;
        private @Nullable Validator validator = null;

        @Override
        public TextInput build() {
            return new TextInputImpl(provider, disabled, initialText, prompt, validator);
        }

        @Override
        public TextInput.Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @Override
        public TextInput.Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        @Override
        public TextInput.Builder initialText(String initialText) {
            this.initialText = initialText;
            return this;
        }

        @Override
        public TextInput.Builder validate(Validator validator) {
            this.validator = validator;
            return this;
        }
    }
}
