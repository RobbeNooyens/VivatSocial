package net.vivatcreative.social.managers;

import net.vivatcreative.core.database.MySQLDatabase;
import net.vivatcreative.core.utils.Logger;
import net.vivatcreative.core.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.*;

public class FriendsManager {
    private static final FriendsManager INSTANCE = new FriendsManager();
    private final Map<UUID, List<UUID>> friends = new HashMap<>();

    private FriendsManager(){}

    public static void loadPlayer(OfflinePlayer player){
        MySQLDatabase.query("SELECT `friends` FROM vivat_users WHERE `uuid` = '%s'", resultSet -> {
            try {
                if(!resultSet.next()) return;
                final List<UUID> friendList = new ArrayList<>();
                if(resultSet.getString("friends") != null) {
                    String[] friendsArray = resultSet.getString("friends").split("_");
                    for (String uuid : friendsArray)
                        friendList.add(PlayerUtil.getOfflinePlayer(uuid).getUniqueId());
                }
                INSTANCE.friends.put(player.getUniqueId(), friendList);
            } catch (SQLException e) {
                Logger.exception(e);
            }
        }, player.getUniqueId().toString());
    }

    public static List<UUID> getFriends(OfflinePlayer player){
        if(!INSTANCE.friends.containsKey(player.getUniqueId()))
            loadPlayer(player);
        List<UUID> friendList = INSTANCE.friends.get(player.getUniqueId());
        if(!player.isOnline())
            unload(player);
        return friendList;
    }

    public static void removeFriend(OfflinePlayer player, OfflinePlayer friend){
        if(!INSTANCE.friends.containsKey(player.getUniqueId()))
            loadPlayer(player);
        INSTANCE.friends.get(player.getUniqueId()).remove(friend.getUniqueId());
        INSTANCE.saveFriends(player);
        if(!player.isOnline())
            unload(player);
    }

    public static void addFriend(OfflinePlayer player, OfflinePlayer friend){
        if(!INSTANCE.friends.containsKey(player.getUniqueId()))
            loadPlayer(player);
        if(INSTANCE.friends.get(player.getUniqueId()).size() < 20) {
            INSTANCE.friends.get(player.getUniqueId()).add(friend.getUniqueId());
            INSTANCE.saveFriends(player);
        }
        if(!player.isOnline())
            unload(player);
    }

    public static void unload(OfflinePlayer player){
        INSTANCE.friends.remove(player.getUniqueId());
    }

    public static boolean isFriend(OfflinePlayer player, OfflinePlayer target){
        if(INSTANCE.friends.get(player.getUniqueId()) == null) return false;
        return INSTANCE.friends.get(player.getUniqueId()).contains(target.getUniqueId());
    }

    private void saveFriends(OfflinePlayer player){
        StringBuilder builder = new StringBuilder("");
        friends.get(player.getUniqueId()).forEach((f)->builder.append(Bukkit.getOfflinePlayer(f).getUniqueId().toString()).append("_"));
        if(builder.length() > 0)
            builder.deleteCharAt(builder.length()-1);
        MySQLDatabase.update("UPDATE vivat_users SET `friends` = '%s' WHERE `uuid` = '%s'", builder.toString(), player.getUniqueId().toString());
    }
}
