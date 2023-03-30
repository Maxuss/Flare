package flare;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Util;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.text.ReactiveComponent;

public class TestPlugin extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        mutableState.set("Nothing");
    }

    private static final ReactiveState<String> mutableState = new ReactiveState<>();
    private static final ReactiveComponent<String> reactiveComponent = Reactive.text(mutableState, mod -> Util.text("<gold>This is <light_purple>" + mod));
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if("component".equals(command.getName())) {
            sender.sendMessage(reactiveComponent);
        } else if("modify".equals(command.getName())) {
            mutableState.set(args[0]);
        }
        return true;
    }
}