package net.vivatcreative.social.managers;

import net.vivatcreative.core.messages.Message;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.party.Party;
import org.bukkit.Bukkit;

import java.util.*;

public class PartyInviteManager {
    private static final PartyInviteManager INSTANCE = new PartyInviteManager();
    private final Map<UUID, Party> invites = new HashMap<>();

    private PartyInviteManager(){}

    public static void invite(UUID player, Party party){
        INSTANCE.invites.put(player, party);
    }

    public static boolean hasPendingInvite(UUID player){
        return INSTANCE.invites.keySet().contains(player);
    }

    public static void accept(UUID player) {
        Party party = INSTANCE.invites.get(player);
        if(party == null) return;
        party.addMember(player);
        INSTANCE.invites.remove(player);
    }

    public static void deny(UUID player) {
        INSTANCE.invites.remove(player);
    }


}
