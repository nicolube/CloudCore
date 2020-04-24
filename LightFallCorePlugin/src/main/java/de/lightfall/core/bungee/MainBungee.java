package de.lightfall.core.bungee;

import de.lightfall.core.Util;
import de.lightfall.core.bungee.usermanager.BungeeUserManager;
import net.md_5.bungee.api.plugin.Plugin;


public class MainBungee extends Plugin {

    private BungeeUserManager usermanager;

    @Override
    public void onEnable() {
        getLogger().info(Util.getLogo());
        this.usermanager = new BungeeUserManager(this);
        getProxy().getPluginManager().registerListener(this, this.usermanager);
        
    }
}
