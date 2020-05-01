package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.lightfall.core.api.message.CoreMessageKeys;
import net.md_5.bungee.api.CommandSender;


@CommandAlias("killtask")
@CommandPermission("cloudnet.killtask")
public class KillTaskCommand extends BaseCommand {
    public KillTaskCommand() {
    }

    @Default
    @CommandCompletion("@taskGroup")
    @Syntax("<taskGroup>")
    public void onDefault(CommandSender sender, GroupConfiguration groupConfiguration) {
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesByGroupAsync(groupConfiguration.getName()).onComplete(serviceInfoSnapshots -> {
            if (serviceInfoSnapshots.isEmpty()) {
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_NO_GROUP);
                return;
            }
            for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                CloudNetDriver.getInstance().getCloudServiceProvider(serviceInfoSnapshot).stopAsync();
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_STOPPED);
            }
        });
    }
}
