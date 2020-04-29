package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;


@CommandAlias("killtask")
@CommandPermission("cloudnet.killtask")
public class KillTaskCommand extends BaseCommand {
    public KillTaskCommand() {
    }

    @Default
    @CommandCompletion("@taskGroup")
    @Syntax("<taskGroup>")
    public void onDefault(CommandSender sender, GroupConfiguration groupConfiguration) {
        if (!sender.hasPermission("cloudnet.killtask")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Dazu hast du keine Rechte!"));
            return;
        }
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesByGroupAsync(groupConfiguration.getName()).onComplete(serviceInfoSnapshots -> {
            if (serviceInfoSnapshots.isEmpty()) {
                sender.sendMessage(new TextComponent("§cEs sind keine Server dieser Gruppe online!"));
                return;
            }
            for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                CloudNetDriver.getInstance().getCloudServiceProvider(serviceInfoSnapshot).stopAsync();
                sender.sendMessage(new TextComponent("§7Gruppe wird gestoppt!"));
            }
        });
    }
}
