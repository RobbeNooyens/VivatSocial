package net.vivatcreative.social.managers;

import net.vivatcreative.social.party.Party;

import java.util.*;

public class PartyManager {

    private static final PartyManager INSTANCE = new PartyManager();
    private final Set<Party> parties = new HashSet<>();

    private PartyManager(){}

    public static void createParty(UUID owner){
        Party party = new Party(owner);
        INSTANCE.parties.add(party);
    }

    public static void disbandParty(Party party){
        INSTANCE.parties.remove(party);
    }

    public static Party getCurrentParty(UUID player){
        for(Party party: INSTANCE.parties) {
            if(party.getMembers().contains(player) || party.getOwner().equals(player))
                return party;
        }
        return null;
    }

}
