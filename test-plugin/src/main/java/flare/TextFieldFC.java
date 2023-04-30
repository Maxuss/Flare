package flare;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ReactiveItemProvider;
import space.maxus.flare.item.Stacks;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.TextInput;

public class TextFieldFC extends FunctionComposable<String> {
    public TextFieldFC(String props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        ReactiveItemProvider<String> item = ItemProvider.reactive(str -> Stacks.withName(Material.YELLOW_STAINED_GLASS_PANE, "Entered: %s".formatted(str)));
        return TextInput
                .builder(item)
                .prompt("<gold>%s".formatted(props))
                .build()
                .configure(txt -> txt.textState().subscribeUpdate(item));
    }
}
