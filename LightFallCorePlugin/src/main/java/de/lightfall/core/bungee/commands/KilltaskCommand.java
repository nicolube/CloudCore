package de.lightfall.core.bungee.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.function.Consumer;

public class KilltaskCommand extends Command {
    public KilltaskCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cloudnet.killtask")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Dazu hast du keine Rechte!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Bitte nutze: /killtask <Tast>"));
            return;
        }
        CloudNetDriver.getInstance().getCloudServicesByGroupAsync(args[0]).onComplete(new Consumer<Collection<ServiceInfoSnapshot>>() {
            @Override
            public void accept(Collection<ServiceInfoSnapshot> serviceInfoSnapshots) {
                if (serviceInfoSnapshots.isEmpty()) {
                    sender.sendMessage(new TextComponent("§cEs sind keine Server dieser Gruppe online!"));
                    return;
                }
                for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                    CloudNetDriver.getInstance().getCloudServiceProvider(serviceInfoSnapshot).stopAsync();
                }
            }
        });
    }
}
