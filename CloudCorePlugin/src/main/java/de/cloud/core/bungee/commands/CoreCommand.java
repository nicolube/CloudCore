package de.cloud.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.ReloadDocument;
import de.cloud.core.api.channelhandeler.documents.ReloadType;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.bungee.MainBungee;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("core")
@CommandPermission("core.admin")
public class CoreCommand extends BaseCommand {

    private final MainBungee plugin;

    public CoreCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("core.admin.reload")
    public static class ReloadSubCommand extends BaseCommand {

        @CommandPermission("core.reload.messages")
        @Subcommand("messages")
        public void onMessage(CommandSender sender) {
            ChannelHandler.send(new ReloadDocument(ReloadType.MESSAGES));
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_CORE_RELOAD_MESSAGES);
        }
    }

    @Subcommand("killtask")
    @CommandCompletion("@taskGroup")
    @CommandPermission("core.admin.killtask")
    @Syntax("<taskGroup>")
    public void onDefault(CommandSender sender, GroupConfiguration groupConfiguration) {
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesByGroupAsync(groupConfiguration.getName()).onComplete(serviceInfoSnapshots -> {
            if (serviceInfoSnapshots.isEmpty()) {
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_NO_GROUP);
                return;
            }
            for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                CloudNetDriver.getInstance().getCloudServiceProvider(serviceInfoSnapshot).deleteAsync();
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_STOPPED);
            }
        });
    }
}
