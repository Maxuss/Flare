package flare;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.Placeholder;

public class DocFC extends FunctionComposable<DocFC.Props> {

    public DocFC(Props props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        return Placeholder.of(Items
                .builder(Material.GOLD_INGOT)
                .name(props.name)
                .lore(props.lore)
        );
    }

    public record Props(String name, String lore) { }
}
