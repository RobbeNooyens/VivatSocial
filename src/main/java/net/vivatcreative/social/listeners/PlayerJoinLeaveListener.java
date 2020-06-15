package net.vivatcreative.social.listeners;

import net.vivatcreative.social.managers.PartyInviteManager;
import net.vivatcreative.social.managers.PartyManager;
import net.vivatcreative.social.party.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    /*@EventHandler
    public void onJoin(PlayerJoinEvent e){

    }*/

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        PartyInviteManager.deny(e.getPlayer().getUniqueId());
        Party party = PartyManager.getCurrentParty(e.getPlayer().getUniqueId());
        if(party != null)
            party.kickPlayer(e.getPlayer().getUniqueId());
    }

}
