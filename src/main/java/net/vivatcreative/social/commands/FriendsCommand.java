package net.vivatcreative.social.commands;

import net.vivatcreative.core.messages.CoreMessage;
import net.vivatcreative.core.messages.Message;
import net.vivatcreative.core.utils.PlayerUtil;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.managers.FriendsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FriendsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return Message.send(sender, CoreMessage.SHOULD_BE_PLAYER);
        Player p = (Player) sender;

        if(args.length == 0)
            return Message.send(sender, SocialMessage.FRIENDS_HELP);

        OfflinePlayer target = null;
        if(args.length >= 2)
            target = PlayerUtil.getOfflinePlayer(args[1]);

        switch (args[0].toLowerCase()) {
            case "add":
                if(target == null)
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                FriendsManager.addFriend(p, target);
                return Message.send(sender, SocialMessage.FRIEND_ADDED, "%player%", target.getName());
            case "remove":
                if(target == null)
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                FriendsManager.removeFriend(p, target);
                return Message.send(sender, SocialMessage.FRIEND_REMOVED, "%player%", target.getName());
            case "list":
                String[] friends = friendListToString(FriendsManager.getFriends(p));
                return Message.send(sender, SocialMessage.FRIEND_LIST, "%online%", friends[0], "%offline%", friends[1]);
            case "teleport":
                if(target == null || !target.isOnline())
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                if(FriendsManager.isFriend(target, p))
                    p.teleport((Player) target);
                else
                    return Message.send(sender, SocialMessage.NOT_ADDED_TO_FRIENDLIST, "%player%", target.getName());
                return true;
            default:
                return Message.send(sender, SocialMessage.FRIENDS_HELP);
        }
    }

    private String[] friendListToString(List<UUID> friends){
        final String[] output = new String[2];
        StringBuilder onlineFriends = new StringBuilder();
        StringBuilder offlineFriends = new StringBuilder();
        friends.forEach((uuid -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if(player.isOnline())
                onlineFriends.append("&a").append(player.getName()).append("&7, ");
            else
                offlineFriends.append("&c").append(player.getName()).append("&7, ");
        }));
        if(onlineFriends.length() > 1)
            onlineFriends.replace(onlineFriends.length() - 2, onlineFriends.length() - 1, "");
        if(offlineFriends.length() > 1)
            offlineFriends.replace(offlineFriends.length() - 2, offlineFriends.length() - 1, "");
        output[0] = onlineFriends.toString();
        output[1] = offlineFriends.toString();
        return output;
    }
}
