package flare;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.util.FlareUtil;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.text.ReactiveComponent;

public class TestPlugin extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        mutableState.set("Nothing");
    }

    private static final ReactiveState<String> mutableState = new ReactiveState<>();
    private static final ReactiveComponent<String> reactiveComponent = Reactive.text(mutableState, mod -> FlareUtil.text("<gold>This is <light_purple>" + mod));
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if("component".equals(command.getName())) {
            sender.sendMessage(reactiveComponent);
        } else if("modify".equals(command.getName())) {
            mutableState.set(args[0]);
        } else if("open".equals(command.getName()) && args[0].equals("1")) {
            Flare.open(new MockFrame((Player) sender), (Player) sender);
        } else if("open".equals(command.getName()) && args[0].equals("2")) {
            Flare.open(new MockPagedFrame(), (Player) sender);
        } else if("open".equals(command.getName()) && args[0].equals("3")) {
            Flare.open(new DocFrame(), (Player) sender);
        } else if("open".equals(command.getName()) && args[0].equals("4")) {
            Flare.open(new ParamDocFrame(new ParamDocFrame.MyProps("maxus", "This is a frame!")), (Player) sender);
        } else if("open".equals(command.getName()) && args[0].equals("5")) {
            Flare.open(new DocPagedFrame(), (Player) sender);
        } else if("open".equals(command.getName()) && args[0].equals("6")) {
            Flare.open(new DocShowcaseFrame(), (Player) sender);
        }
        return true;
    }
}