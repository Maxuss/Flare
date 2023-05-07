package flare;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.complex.Composition;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.Base64;
import java.util.Random;

public class HasherFC extends FunctionComposable<Void> {
    private static final Random random = new Random();
    private final ReactiveState<byte[]> rawBytes = useState(new byte[16]);
    private final Computable<byte[], ItemStack> stackComputable = useMemo(rawBytes, bytes -> {
        String newName = "<gold>HASH: %s".formatted(Hashing.sha512().hashBytes(bytes).toString());
        return Items.withMeta(Material.EMERALD, meta -> {
            meta.displayName(FlareUtil.text(newName));
            Items.loreMeta("<gray>Original bytes: <yellow>%s".formatted(Base64.getEncoder().encodeToString(bytes))).accept(meta);
        });
    });

    public HasherFC() {
        super(null);
    }

    @Override
    public @NotNull Composable compose() {
        return Composition.of(
                Placeholder.of(Reactive.item(rawBytes, stackComputable)).inside(Slot.ROW_ONE_SLOT_ONE),
                Button.of(ItemProvider.still(new ItemStack(Material.DIAMOND_SWORD)), (btn, e) -> {
                    byte[] bytes = new byte[16];
                    random.nextBytes(bytes);
                    rawBytes.set(bytes);
                    return true;
                }).inside(Slot.ROW_TWO_SLOT_ONE)
        );
    }
}
