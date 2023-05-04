package flare;

import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.TextInput;
import space.maxus.flare.util.Validators;

public class TextFieldFC extends FunctionComposable<String> {
    public TextFieldFC(String props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        return TextInput
                .builder("Input number", null)
                .prompt("<gold>%s".formatted(props))
                .validate(Validators.INTEGER)
                .build();
    }
}
