package de.lightfall.core.bungee;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;

public class MainBungee extends Plugin {

    private LuckPerms luckperms;

    @Override
    public void onEnable() {
        this.luckperms = LuckPermsProvider.get();
    }
}
