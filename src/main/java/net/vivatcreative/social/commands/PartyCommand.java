package net.vivatcreative.social.commands;

import net.vivatcreative.core.messages.CoreMessage;
import net.vivatcreative.core.messages.Message;
import net.vivatcreative.core.utils.PlayerUtil;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.api.SocialPermission;
import net.vivatcreative.social.managers.PartyInviteManager;
import net.vivatcreative.social.managers.PartyManager;
import net.vivatcreative.social.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return Message.send(sender, CoreMessage.SHOULD_BE_PLAYER);
        Player p = (Player) sender;

        if(args.length == 0)
            return Message.send(sender, SocialMessage.PARTY_HELP);

        Party party;
        OfflinePlayer target;
        switch (args[0].toLowerCase()) {
            case "create":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party != null)
                    return Message.send(sender, SocialMessage.ALREADY_IN_PARTY);
                if(!SocialPermission.CREATE_PARTY.hasAndWarn(p)) return true;
                PartyManager.createParty(p.getUniqueId());
                return Message.send(sender, SocialMessage.CREATED_PARTY);
            case "disband":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null)
                    return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                if(!party.isOwner(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NOT_PARTYOWNER);
                PartyManager.disbandParty(party);
                return Message.send(sender, SocialMessage.DISBANDED_PARTY);
            case "invite":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null)
                    return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                if(!party.isOwner(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NOT_PARTYOWNER);
                if(args.length == 1)
                    return Message.send(sender, CoreMessage.COMMAND_USAGE, "%usage%", "/party invite <player>");
                target = PlayerUtil.getOfflinePlayer(args[1]);
                if(target == null || !target.isOnline())
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                Party targetParty = PartyManager.getCurrentParty(target.getUniqueId());
                if(targetParty != null)
                    return Message.send(sender, SocialMessage.PLAYER_ALRADY_IN_PARTY);
                if(PartyInviteManager.hasPendingInvite(target.getUniqueId()))
                    return Message.send(sender, SocialMessage.PLAYER_HAS_PENDING_INVITE);
                PartyInviteManager.invite(target.getUniqueId(), party);
                Message.send(sender, SocialMessage.INVITE_SENT);
                return Message.send((Player) target, SocialMessage.INVITE_RECEIVED, "%player%", sender.getName());
            case "accept":
                if(!PartyInviteManager.hasPendingInvite(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NO_PENDING_INVITE);
                PartyInviteManager.accept(p.getUniqueId());
                return Message.send(sender, SocialMessage.PLAYER_ACCEPTED_INVITE);
            case "deny":
                if(!PartyInviteManager.hasPendingInvite(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NO_PENDING_INVITE);
                PartyInviteManager.deny(p.getUniqueId());
                return Message.send(sender, SocialMessage.PLAYER_DENIED_INVITE);
            case "leave":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null) return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                party.kickPlayer(p.getUniqueId());
                return true;
            case "kick":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null)
                    return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                if(!party.isOwner(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NOT_PARTYOWNER);
                if(args.length == 1)
                    return Message.send(sender, CoreMessage.COMMAND_USAGE, "%usage%", "/party setowner <player>");
                target = PlayerUtil.getOfflinePlayer(args[1]);
                if(target == null || !target.isOnline())
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                party.kickPlayer(target.getUniqueId());
                return true;
            case "list":
            case "members":
            case "info":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null)
                    return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                String owner = Bukkit.getPlayer(party.getOwner()).getName();
                String members = getMembers(party);
                return Message.send(sender, SocialMessage.PARTY_LIST, "%owner%", owner, "%members%", members);
            case "setowner":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null)
                    return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                if(!party.isOwner(p.getUniqueId()))
                    return Message.send(sender, SocialMessage.NOT_PARTYOWNER);
                if(args.length == 1)
                    return Message.send(sender, CoreMessage.COMMAND_USAGE, "%usage%", "/party setowner <player>");
                target = PlayerUtil.getOfflinePlayer(args[1]);
                if(target == null || !target.isOnline())
                    return Message.send(sender, CoreMessage.TARGET_NOT_FOUND);
                if(!party.isMember(target.getUniqueId()))
                    return Message.send(sender, SocialMessage.PLAYER_NOT_MEMBER);
                party.setOwner(target.getUniqueId());
                return true;
            case "chat":
                party = PartyManager.getCurrentParty(p.getUniqueId());
                if(party == null) return Message.send(sender, SocialMessage.NOT_IN_PARTY);
                party.togglePartyChat(p.getUniqueId());
                return Message.send(sender, party.hasPartyChatEnabled(p.getUniqueId()) ? SocialMessage.PARTY_CHAT_ENABLED : SocialMessage.PARTY_CHAT_DISABLED);
            default:
                return Message.send(sender, SocialMessage.PARTY_HELP);
        }
    }

    private String getMembers(Party party){
        Set<UUID> uuids = party.getMembers();
        StringBuilder builder = new StringBuilder();
        uuids.forEach(uuid -> builder.append(Bukkit.getPlayer(uuid).getName()).append(", "));
        if(builder.length() > 1)
            builder.replace(builder.length()-2, builder.length()-1, "");
        return builder.toString();
    }
}
