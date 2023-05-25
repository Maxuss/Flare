package space.maxus.flare.extern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.util.FlareUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FlareCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("flare.open") && !sender.hasPermission("flare.reload")) {
            sender.sendMessage(Objects.requireNonNullElse(command.permissionMessage(), FlareUtil.text("<red>Insufficient permissions!")));
            return true;
        }
        if(args.length == 0) {
            sendCommandUsage(sender);
            return true;
        }
        String commandKind = args[0];
        if(commandKind.equals("reload") && sender.hasPermission("flare.reload")) {
            // TODO: reload frames here
            Flare.sendFlareMessage(sender, "<yellow>Frame registry reloaded!");
            return true;
        } else if(commandKind.equals("open") && sender.hasPermission("flare.open")) {
            if(args.length < 2) {
                sendCommandUsage(sender);
                return true;
            } else {
                String frameId = args[1];
                Frame frameOrNull = Flare.getFrameRegistry().getFrame(frameId);
                if(frameOrNull == null) {
                    Flare.sendFlareMessage(sender, "<red>Frame with id <yellow>%s</yellow> does not exist!".formatted(frameId));
                } else {
                    if(args.length == 2 && !(sender instanceof Player)) {
                        Flare.sendFlareMessage(sender, "<red>Can not use <yellow>/flare open</yellow> as a console without specifying player!");
                        return true;
                    }
                    Player player = args.length > 2 ? Bukkit.getPlayer(args[2]) : (Player) sender;
                    if(player == null) {
                        Flare.sendFlareMessage(sender, "<red>Invalid player! Could not find player <yellow>%s</yellow>!".formatted(args[2]));
                        return true;
                    }
                    Flare.open(frameOrNull, player);
                }
                return true;
            }
        }
        sendCommandUsage(sender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                if(sender.hasPermission("flare.open") || sender.hasPermission("flare.reload"))
                    return Stream.of("open", "reload").filter(it -> it.startsWith(args[0])).toList();
                return Collections.emptyList();
            }
            case 2 -> {
                if (args[0].equals("open") && sender.hasPermission("flare.open"))
                    return Flare.getFrameRegistry().getFrameIds().stream().filter(it -> it.startsWith(args[1])).toList();
                return Collections.emptyList();
            }
            case 3 -> {
                if (args[0].equals("open") && sender.hasPermission("flare.open")) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.startsWith(args[2])).toList();
                }
                return Collections.emptyList();
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }

    private void sendCommandUsage(@NotNull CommandSender sender) {
        Flare.sendFlareMessage(sender, "<red>Invalid command usage.");
        if(sender.hasPermission("flare.open")) {
            Flare.sendFlareMessage(sender, "<gradient:#fc4016:#fc8028>/flare open [frame_id] (player)<white> - Opens frame with certain ID to a certain player");
        }
        if(sender.hasPermission("flare.reload")) {
            Flare.sendFlareMessage(sender, "<gradient:#fc4016:#fc8028>/flare reload<white> - Reloads all Flare frame configurations");
        }
    }
}
