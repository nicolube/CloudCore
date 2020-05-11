package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ReloadDocument;
import de.lightfall.core.api.channelhandeler.documents.ReloadType;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("core")
@CommandPermission("core")
public class CoreCommand extends BaseCommand {

    private final MainBungee plugin;

    public CoreCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("core.reload")
    public class ReloadSubCommand extends BaseCommand {

        @CommandPermission("core.reload.messages")
        @Subcommand("messages")
        public void onMessage(CommandSender sender) {
            ChannelHandler.send(new ReloadDocument(ReloadType.MESSAGES));
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_CORE_RELOAD_MESSAGES);
        }
    }

    @Subcommand("killtask")
    @CommandCompletion("@taskGroup")
    @CommandPermission("core.killtask")
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
