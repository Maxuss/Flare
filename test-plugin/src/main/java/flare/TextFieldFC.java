package flare;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ReactiveItemProvider;
import space.maxus.flare.item.Items;
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
        ReactiveItemProvider<String> item = ItemProvider.reactiveBuild(str ->
                Items
                        .builder(Material.YELLOW_STAINED_GLASS_PANE)
                        .name("Entered %s".formatted(str))
                        .branch(str == null || str.isEmpty(),
                                b -> b.name("<yellow>No text entered").lore("Click to enter text"),
                                b -> b.name("<green>%s".formatted(str))
                        )
        );
        return TextInput
                .builder(item)
                .prompt("<gold>%s".formatted(props))
                .validate(Validators.INTEGER)
                .build()
                .configure(txt -> txt.onTextChange().subscribeUpdate(item));
    }
}
