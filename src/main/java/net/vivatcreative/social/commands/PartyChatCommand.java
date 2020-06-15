package net.vivatcreative.social.commands;

import net.vivatcreative.core.messages.CoreMessage;
import net.vivatcreative.core.messages.Message;
import net.vivatcreative.social.api.SocialMessage;
import net.vivatcreative.social.managers.PartyManager;
import net.vivatcreative.social.party.Party;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return Message.send(sender, CoreMessage.SHOULD_BE_PLAYER);
        Player p = (Player) sender;
        Party party = PartyManager.getCurrentParty(p.getUniqueId());
        if(party == null)
            return Message.send(sender, SocialMessage.NOT_IN_PARTY);
        StringBuilder message = new StringBuilder();
        for (String arg : args)
            message.append(arg).append(" ");
        if(message.length() > 0)
            message.deleteCharAt(message.length()-1);
        party.broadcastToParty(SocialMessage.PARTY_CHAT_FORMAT, "%player%", p.getName(), "%message%", message.toString());
        return true;
    }
}
