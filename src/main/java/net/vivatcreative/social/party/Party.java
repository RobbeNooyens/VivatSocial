package net.vivatcreative.social.party;

import net.vivatcreative.core.messages.Message;
import net.vivatcreative.core.messages.VivatMessage;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.managers.PartyManager;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    private UUID owner;
    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> partyChat = new HashSet<>();

    public Party(UUID owner){
        this.owner = owner;
    }

    public void addMember(UUID member){
        members.add(member);
        broadcastToParty(SocialMessage.JOINED_PARTY, "%player%", Bukkit.getPlayer(member).getName());
    }

    public boolean isOwner(UUID player){
        return owner.equals(player);
    }

    private void removeMember(UUID member){
        broadcastToParty(SocialMessage.LEFT_PARTY, "%player%", Bukkit.getPlayer(member).getName());
        members.remove(member);
    }

    public void kickPlayer(UUID player){
        if(isOwner(player)) {
            if (members.size() == 0) {
                PartyManager.disbandParty(this);
            } else {
                setOwner(members.iterator().next());
                members.remove(player);
            }
            return;
        }
        removeMember(player);
    }

    public void setOwner(UUID newOwner){
        members.add(owner);
        members.remove(newOwner);
        owner = newOwner;
        broadcastToParty(SocialMessage.PARTY_OWNER_CHANGED, "%owner%", Bukkit.getPlayer(newOwner).getName());
    }

    public UUID getOwner(){
        return owner;
    }

    public Set<UUID> getMembers(){
        return members;
    }

    public boolean isMember(UUID player){
        return members.contains(player);
    }

    public void togglePartyChat(UUID player){
        if(isOwner(player) || isMember(player)) {
            if (hasPartyChatEnabled(player))
                partyChat.remove(player);
            else
                partyChat.add(player);
        }
    }

    public boolean hasPartyChatEnabled(UUID player){
        return partyChat.contains(player);
    }

    public void broadcastToParty(VivatMessage message, String... placeholders){
        members.forEach(uuid -> Message.send(Bukkit.getPlayer(uuid), message,placeholders));
        Message.send(Bukkit.getPlayer(owner), message, placeholders);
    }

}
