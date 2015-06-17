package net.cubespace.geSuit.moderation.commands;

import java.net.InetAddress;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.cubespace.geSuit.core.Global;
import net.cubespace.geSuit.core.GlobalPlayer;
import net.cubespace.geSuit.core.commands.Command;
import net.cubespace.geSuit.core.commands.CommandPriority;
import net.cubespace.geSuit.core.commands.Optional;
import net.cubespace.geSuit.core.commands.Varargs;
import net.cubespace.geSuit.core.objects.BanInfo;
import net.cubespace.geSuit.core.objects.DateDiff;
import net.cubespace.geSuit.core.objects.Result;
import net.cubespace.geSuit.core.util.Utilities;
import net.cubespace.geSuit.remote.moderation.BanActions;

public class BanCommands {
    private BanActions actions;
    
    public BanCommands(BanActions actions) {
        this.actions = actions;
    }
    
    @Command(name="ban", async=true, aliases={"db","banplayer"}, permission="gesuit.bans.command.ban", usage="/<command> <player> [reason]")
    public void ban(CommandSender sender, String playerName, @Optional @Varargs String reason) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("ban.unknown-player", "player", playerName));
        }
        
        Result result = actions.ban(player, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="ipban", async=true, aliases={"dbip","banip"}, permission="gesuit.bans.command.ipban", usage="/<command> <ip> [reason]")
    @CommandPriority(2)
    public void ipBan(CommandSender sender, InetAddress ip, @Optional @Varargs String reason) {
        Result result = actions.ban(ip, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="ipban", async=true, aliases={"dbip","banip"}, permission="gesuit.bans.command.ipban", usage="/<command> <player> [reason]")
    @CommandPriority(1)
    public void ipBan(CommandSender sender, String playerName, @Optional @Varargs String reason) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("ban.unknown-player", "player", playerName));
        }
        
        Result result = actions.ipban(player, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="tempbanip", async=true, aliases={"tbanip","dtbip"}, permission="gesuit.bans.command.tempbanip", usage="/<command> <ip> <time> [reason]")
    public void tempBan(CommandSender sender, InetAddress ip, DateDiff date, @Optional @Varargs String reason) {
        Result result = actions.banUntil(ip, reason, date.fromNow(), sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="tempban", async=true, aliases={"tban","bant","bantemp","dtb"}, permission="gesuit.bans.command.tempban", usage="/<command> <player> <time> [reason]")
    public void tempBan(CommandSender sender, String playerName, DateDiff date, @Optional @Varargs String reason) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("ban.unknown-player", "player", playerName));
        }
        
        Result result = actions.banUntil(player, reason, date.fromNow(), sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="unban", async=true, aliases={"dub","uban", "reoveban", "pardon"}, permission="gesuit.bans.command.unban", usage="/<command> <player> [reason]")
    public void unban(CommandSender sender, String playerName, @Optional @Varargs String reason) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("unban.unknown-player", "player", playerName));
        }
        
        Result result = actions.unban(player, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="unbanip", async=true, aliases={"ipunban","unipban", "ipsafe", "safeip", "pardonip", "dubip"}, permission="gesuit.bans.command.ipban", usage="/<command> <player> [reason]")
    @CommandPriority(1)
    public void unbanIp(CommandSender sender, String playerName, @Optional @Varargs String reason) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("unban.unknown-player", "player", playerName));
        }
        
        Result result = actions.ipunban(player, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="unbanip", async=true, aliases={"ipunban","unipban", "ipsafe", "safeip", "pardonip", "dubip"}, permission="gesuit.bans.command.ipban", usage="/<command> <player> [reason]")
    @CommandPriority(2)
    public void unbanIp(CommandSender sender, InetAddress ip, @Optional @Varargs String reason) {
        Result result = actions.unban(ip, reason, sender.getName(), (sender instanceof Player ? ((Player)sender).getUniqueId() : null));
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }
    
    @Command(name="banhistory", async=true, permission="gesuit.bans.command.banhistory", usage="/<command> <player>")
    public void banHistory(CommandSender sender, String playerName) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("player.unknown", "player", playerName));
        }
        
        List<BanInfo<GlobalPlayer>> history = actions.getHistory(player);
        
        sender.sendMessage(ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + player.getDisplayName() + "'s Ban History" + ChatColor.DARK_AQUA + " --------");
        
        if (history.isEmpty()) {
            sender.sendMessage(ChatColor.RED + " No bans on record");
            return;
        }
        
        for (BanInfo<GlobalPlayer> ban : Lists.reverse(history)) {
            StringBuilder builder = new StringBuilder();
            if (ban.isUnban()) {
                builder.append(ChatColor.GREEN);
            } else {
                builder.append(ChatColor.RED);
            }
            
            builder.append(Utilities.formatDate(ban.getDate()));
            builder.append(' ');
            builder.append(ChatColor.GRAY);
            
            if (ban.isUnban()) {
                builder.append("Unbanned by ");
                builder.append(ChatColor.YELLOW);
                builder.append(ban.getBannedBy());
                if (ban.getReason() != null) {
                    builder.append("\n t");
                    builder.append(ChatColor.GRAY);
                    builder.append("Reason: ");
                    builder.append(ChatColor.ITALIC);
                    builder.append(ban.getReason());
                }
            } else {
                if (ban.isTemporary()) {
                    builder.append("Temp Banned by ");
                    builder.append(ChatColor.YELLOW);
                    builder.append(ban.getBannedBy());
                    
                    builder.append(ChatColor.GRAY);
                    builder.append(" until ");
                    builder.append(ChatColor.YELLOW);
                    builder.append(Utilities.formatDate(ban.getUntil()));
                    
                    builder.append(ChatColor.GRAY);
                    builder.append(" (");
                    builder.append(new DateDiff(ban.getUntil() - ban.getDate()).toString());
                    builder.append(')');
                } else {
                    builder.append("Banned by ");
                    builder.append(ChatColor.YELLOW);
                    builder.append(ban.getBannedBy());
                }
                
                if (ban.getReason() != null) {
                    builder.append("\n ");
                    builder.append(ChatColor.GRAY);
                    builder.append("Reason: ");
                    builder.append(ChatColor.ITALIC);
                    builder.append(ban.getReason());
                }
            }
            
            sender.sendMessage(builder.toString().split("\n"));
        }
    }
    
    @Command(name="checkban", async=true, aliases={"lookupban","baninfo"}, permission="gesuit.bans.command.checkban", usage="/<command> <player>")
    public void checkBan(CommandSender sender, String playerName) {
        GlobalPlayer player = Utilities.getPlayerAdvanced(playerName);
        
        if (player == null) {
            throw new IllegalArgumentException(Global.getMessages().get("player.unknown", "player", playerName));
        }
        
        BanInfo<InetAddress> ipBan = (player.getAddress() != null ? actions.getBan(player.getAddress()) : null);
        BanInfo<GlobalPlayer> playerBan = player.getBanInfo();
        
        if (ipBan == null && playerBan == null) {
            sender.sendMessage(Global.getMessages().get("ban.not-banned"));
            return;
        }
        
        sender.sendMessage(ChatColor.DARK_AQUA + "-------- " + ChatColor.RED + "Ban Info" + ChatColor.DARK_AQUA + " --------");
        if (player.hasNickname()) {
            sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.AQUA + player.getDisplayName() + " (" + player.getName() + ")");
        } else {
            sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.AQUA + player.getName());
        }
        
        sender.sendMessage(ChatColor.RED + "UUID: " + ChatColor.AQUA + player.getUniqueId().toString());
        
        if (playerBan != null) {
            sender.sendMessage(ChatColor.RED + "-- Name Banned --");
            
            sender.sendMessage(ChatColor.RED + "Reason: " + ChatColor.AQUA + playerBan.getReason());
            sender.sendMessage(ChatColor.RED + "Date: " + ChatColor.AQUA + Utilities.formatDate(playerBan.getDate()));
            if (playerBan.isTemporary()) {
                sender.sendMessage(ChatColor.RED + "Until: " + ChatColor.AQUA + Utilities.formatDate(playerBan.getUntil()) + " (" + new DateDiff(System.currentTimeMillis() - playerBan.getUntil()).toString() + ")");
            }
            sender.sendMessage(ChatColor.RED + "By: " + ChatColor.AQUA + playerBan.getBannedBy());
        }
        
        if (ipBan != null) {
            sender.sendMessage(ChatColor.RED + "-- IP Banned --");
            
            sender.sendMessage(ChatColor.RED + "IP: " + ChatColor.AQUA + ipBan.getWho().getHostAddress());
            sender.sendMessage(ChatColor.RED + "Reason: " + ChatColor.AQUA + ipBan.getReason());
            sender.sendMessage(ChatColor.RED + "Date: " + ChatColor.AQUA + Utilities.formatDate(ipBan.getDate()));
            if (ipBan.isTemporary()) {
                sender.sendMessage(ChatColor.RED + "Until: " + ChatColor.AQUA + Utilities.formatDate(ipBan.getUntil()) + " (" + new DateDiff(System.currentTimeMillis() - ipBan.getUntil()).toString() + ")");
            }
            sender.sendMessage(ChatColor.RED + "By: " + ChatColor.AQUA + ipBan.getBannedBy());
        }
    }
}
