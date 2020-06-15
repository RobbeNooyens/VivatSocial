package net.vivatcreative.social.listeners;

import net.vivatcreative.core.messages.Message;
import net.vivatcreative.core.utils.PlayerUtil;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.managers.PartyManager;
import net.vivatcreative.social.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        Party party = PartyManager.getCurrentParty(p.getUniqueId());
        if(party == null) return;
        if(!party.hasPartyChatEnabled(p.getUniqueId())) return;
        e.setCancelled(true);
        party.broadcastToParty(SocialMessage.PARTY_CHAT_FORMAT, "%player%", p.getName(), "%message%", e.getMessage());
    }
}
