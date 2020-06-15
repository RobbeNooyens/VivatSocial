package net.vivatcreative.social;

import net.vivatcreative.core.connections.ConnectionManager;
import net.vivatcreative.core.files.FileManager;
import net.vivatcreative.core.messages.MessageHelper;
import net.vivatcreative.social.api.SocialConnection;
import net.vivatcreative.social.commands.FriendsCommand;
import net.vivatcreative.social.commands.PartyChatCommand;
import net.vivatcreative.social.commands.PartyCommand;
import net.vivatcreative.social.listeners.PlayerChatListener;
import net.vivatcreative.social.listeners.PlayerJoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class VivatSocial extends JavaPlugin {

    @Override
    public void onEnable() {
        SocialConnection connection = new SocialConnection();
        connection.registerCommand("friends", new FriendsCommand());
        connection.registerCommand("party", new PartyCommand());
        connection.registerCommand("partychat", new PartyChatCommand());
        ConnectionManager.register(connection);

        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);

        MessageHelper.register(FileManager.getFile(this, "messages.yml", false));
    }

    public static VivatSocial getInstance() {
        return JavaPlugin.getPlugin(VivatSocial.class);
    }

}
