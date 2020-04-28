package de.rovedmc.clans.command;

import de.rovedmc.clans.Clans;
import de.rovedmc.clans.manager.ClanManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class ClanCommand extends Command {

    public static ArrayList<ProxiedPlayer> DeletePlayer = new ArrayList<>();
    public static ArrayList<ProxiedPlayer> LeavePlayer = new ArrayList<>();

    public static HashMap<ProxiedPlayer, String> KickPlayerName = new HashMap<>();

    public ClanCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String UUID = player.getUniqueId().toString();
        if (!ClanManager.existPlayer(UUID)) {
            player.sendMessage(Clans.Prefix + "§cBei deinem Datenbankeintrag gab es einen fehler!");
            return;
        }
        if (args.length == 0){
            sendHelp(player, 1); return;
        } else if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 2) {
                String ClanName = args[1];
                String ClanTag = args[2];
                if (ClanName.length() < 1 || ClanName.length() > 16){
                    player.sendMessage(Clans.Prefix + "§cDer §4ClanName §cmuss zwischen §e1 §cund §e16 Zeichen §clang sein!");
                    return;
                }
                if (!ClanManager.getClanNameByUUID(UUID).isEmpty()){
                    player.sendMessage(Clans.Prefix + "§cDu bist bereits in einem Clan!");
                    return;
                }
                if (ClanManager.existClanName(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDer ClanName '§e" + ClanName + "§c' ist bereits vergeben!");
                    return;
                }
                if (ClanManager.existClanTag(ClanTag)) {
                    player.sendMessage(Clans.Prefix + "§cDer ClanTag '§e" + ClanTag + "§c' ist bereits vergeben!");
                    return;
                }
                ClanManager.createClan(ClanName, ClanTag, UUID);
                player.sendMessage(Clans.Prefix + "§7Du hast den Clan §a" + ClanName + "§8[§e#" + ClanTag + "§8] §7erstellt!");
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 1){
                if (DeletePlayer.contains(player)) {
                    player.sendMessage(Clans.Prefix + "§c/clan delete confirm!");
                    return;
                }
                if (ClanManager.getClanNameByUUID(UUID).isEmpty()){
                    player.sendMessage(Clans.Prefix + "§cDu bist in keinem Clan!");
                    return;
                }
                if (!ClanManager.getRankByUUID(UUID).equals("Leader")) {
                    player.sendMessage(Clans.Prefix + "§cNur ein Leader darf den Clan auflösen!");
                    return;
                }
                DeletePlayer.add(player);
                player.sendMessage(Clans.Prefix + "§7Bist du sicher das du den Clan auflösen willst?");
                player.sendMessage(Clans.Prefix + "§a/clan delete confirm!");
            } else if (args.length == 2){
                if (!DeletePlayer.contains(player)) {
                    player.sendMessage(Clans.Prefix + "§c/clan delete!");
                    return;
                }
                if (ClanManager.getClanNameByUUID(UUID).isEmpty()){
                    player.sendMessage(Clans.Prefix + "§cDu bist in keinem Clan!");
                    return;
                }
                String ClanName = ClanManager.getClanNameByUUID(UUID);
                if (!ClanManager.getRankByUUID(UUID).equals("Leader")) {
                    player.sendMessage(Clans.Prefix + "§cNur ein Leader darf den Clan auflösen!");
                    return;
                }
                DeletePlayer.remove(player);
                for (String members : ClanManager.getAllMembers(ClanName)) {
                    ClanManager.setClanNameToPlayer(members, "");
                    ClanManager.setRankToPlayer(members, "");
                    if (ProxyServer.getInstance().getPlayer(members) != null){
                        ProxiedPlayer targets = ProxyServer.getInstance().getPlayer(members);
                        if (targets != player) {
                            targets.sendMessage(Clans.Prefix + "§cDein Clan wurde gerade aufgelöst!");
                        }
                    }
                    ClanManager.deleteClan(ClanName);
                    player.sendMessage(Clans.Prefix + "§7Du hast deinen Clan gelöscht!");
                }
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (args.length == 1) {
                if (LeavePlayer.contains(player)) {
                    player.sendMessage(Clans.Prefix + "§c/clan leave confirm!");
                    return;
                }
                if (ClanManager.getClanNameByUUID(UUID).isEmpty()){
                    player.sendMessage(Clans.Prefix + "§cDu bist in keinem Clan!");
                    return;
                }
                String ClanName = ClanManager.getClanNameByUUID(UUID);
                if (ClanManager.getRankByUUID(UUID).equals("Leader") && ClanManager.getMemberList(ClanName, "Leader").size() == 1) {
                    player.sendMessage(Clans.Prefix + "§cDu kannst als letzter Leader eines Clans nicht einfach den CLan verlassen!");
                    return;
                }
                LeavePlayer.add(player);
                player.sendMessage(Clans.Prefix + "§7Bist du sicher das du den Clan verlassen willst?");
                player.sendMessage(Clans.Prefix + "§a/clan leave confirm!");
            } else if (args.length == 2) {
                if (LeavePlayer.contains(player)) {
                    player.sendMessage(Clans.Prefix + "§c/clan leave!");
                    return;
                }
                if (ClanManager.getClanNameByUUID(UUID).isEmpty()){
                    player.sendMessage(Clans.Prefix + "§cDu bist in keinem Clan!");
                    return;
                }
                String ClanName = ClanManager.getClanNameByUUID(UUID);
                String Rank = ClanManager.getRankByUUID(UUID);
                if (Rank.equals("Leader") && ClanManager.getMemberList(ClanName, "Leader").size() == 1) {
                    player.sendMessage(Clans.Prefix + "§cDu kannst als letzter Leader eines Clans nicht einfach den CLan verlassen!");
                    return;
                }

                LeavePlayer.remove(player);
                ClanManager.setClanNameToPlayer(UUID, "");
                ClanManager.setRankToPlayer(UUID, "");
                ClanManager.rmvMemberFromClan(ClanName, Rank, UUID);
                for (String members : ClanManager.getAllMembers(ClanName)) {
                    if (ProxyServer.getInstance().getPlayer(members) != null) {
                        ProxiedPlayer targets = ProxyServer.getInstance().getPlayer(members);
                        if (targets != player) {
                            targets.sendMessage(Clans.Prefix + "§cDer Spieler §a" + player.getName() + " §7hat den Clan verlassen!");
                        }
                    }
                }
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("ignore")) {
            if (args.length == 2){
                String ClanName = args[1];
                if (!ClanManager.existClanName(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDer Clan existiert nicht!");
                    return;
                }
                if (ClanManager.getPlayerList(UUID, "IgnoreList").contains(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDu ignoriers diesen Clan bereits!");
                    return;
                }
                ClanManager.addIgnoreToPlayer(UUID, ClanName);
                player.sendMessage(Clans.Prefix + "§7Du ignorierst nun den Clan §a" + ClanName + "§7!");
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("unignore")) {
            if (args.length == 2){
                String ClanName = args[1];
                if (!ClanManager.existClanName(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDer Clan existiert nicht!");
                    return;
                }
                if (!ClanManager.getPlayerList(UUID, "IgnoreList").contains(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDu ignoriers diesen Clan nicht!");
                    return;
                }
                ClanManager.rmvIgnoreFromPlayer(UUID, ClanName);
                player.sendMessage(Clans.Prefix + "§7Du ignorierst nun den Clan §a" + ClanName + " §7nicht mehr!");
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 2){
                String ClanName = args[1];
                if (!ClanManager.getClanNameByUUID(UUID).isEmpty()) {
                    player.sendMessage(Clans.Prefix + "§cDu bist bereits in einem Clan!");
                    return;
                }
                if (!ClanManager.getPlayerList(UUID, "RequestList").contains(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDu hast von diesem Clan keine Einladung erhalten!");
                    return;
                }
                if (ClanManager.getAllMembers(ClanName).size() >= Clans.MaxClanMember) {
                    player.sendMessage(Clans.Prefix + "§cDer Clan hat bereits zu viele Mitglieder!");
                    return;
                }
                for (String members : ClanManager.getAllMembers(ClanName)) {
                    if (ProxyServer.getInstance().getPlayer(members) != null) {
                        ProxiedPlayer targets = ProxyServer.getInstance().getPlayer(members);
                        if (targets != player) {
                            targets.sendMessage(Clans.Prefix + "§cDer Spieler §a" + player.getName() + " §7ist dem Clan beigetreten!");
                        }
                    }
                }
                ClanManager.rmvRequestFromPlayer(UUID, ClanName);
                ClanManager.setClanNameToPlayer(UUID, ClanName);
                ClanManager.setRankToPlayer(UUID, "Member");
                ClanManager.addMemberToClan(ClanName, "Member", UUID);
                player.sendMessage(Clans.Prefix + "§7Du bist nun im Clan §e" + ClanName + "§7!");
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("deny")) {
            if (args.length == 2){
                String ClanName = args[1];
                if (!ClanManager.getPlayerList(UUID, "RequestList").contains(ClanName)) {
                    player.sendMessage(Clans.Prefix + "§cDu hast von diesem Clan keine Einladung erhalten!");
                    return;
                }
                ClanManager.rmvRequestFromPlayer(UUID, ClanName);
                player.sendMessage(Clans.Prefix + "§7Du hast die Einladung von §e" + ClanName + " §7abgelehnt!");
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("2")){
            if (args.length == 1) {
                sendHelp(player, 2);
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("toggle")) {
            if (args.length == 1){
                 String AllowRequest = ClanManager.getSettingByUUID(UUID, "AllowRequest");
                 if (AllowRequest.equals("true")) {
                     ClanManager.setSettingTpPlayer(UUID, "AllowRequest", "false");
                     player.sendMessage(Clans.Prefix + "§7Du empfängst nun keine Claneinladungen mehr!");
                 } else {
                     ClanManager.setSettingTpPlayer(UUID, "AllowRequest", "true");
                     player.sendMessage(Clans.Prefix + "§7Du empfängst nun Claneinladungen!");
                 }
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("togglemessage")) {
            if (args.length == 1) {
                String AllowMessage = ClanManager.getSettingByUUID(UUID, "AllowMessage");
                if (AllowMessage.equals("true")) {
                    ClanManager.setSettingTpPlayer(UUID, "AllowMessage", "false");
                    player.sendMessage(Clans.Prefix + "§7Du empfängst nun keine ClanChatNachrichten mehr!");
                } else {
                    ClanManager.setSettingTpPlayer(UUID, "AllowMessage", "true");
                    player.sendMessage(Clans.Prefix + "§7Du empfängst nun ClanChatNachrichten!");
                }
            } else {
                sendHelp(player, 1); return;
            }
        } else if (args[0].equalsIgnoreCase("kick")) {

        }
    }

    public static void sendHelp(ProxiedPlayer player, int page){
        player.sendMessage("");
        player.sendMessage(Clans.Prefix  + "§7Help §8(§eSeite " + page + "§8):");
        player.sendMessage("");
        if (page == 1){
            player.sendMessage("§e/clan create <ClanName> <ClanTag> §7Erstelle einen Clan");
            player.sendMessage("§e/clan delete §7Lösche einen Clan");
            player.sendMessage("§e/clan leave §7Verlasse einen Clan");
            player.sendMessage("§e/clan ignore <ClanName> §7Ignoriere einen Clan");
            player.sendMessage("§e/clan unignore <ClanName> §7Ignoriere einen Clan nicht mehr");
            player.sendMessage("§e/clan accept <ClanName> §7Akzeptiere eine CLaneinladung");
            player.sendMessage("§e/clan deny <ClanName> §7Lehne eine CLaneinladung ab");
            player.sendMessage("§e/clan 2 §7Zeige die 2. Helpseite");
            player.sendMessage("");
        } else if (page == 2){
            player.sendMessage("§e/clan toggle §7Stelle Claneinladungen an/aus");
            player.sendMessage("§e/clan toggleMessage §7Stelle den ClanChat für dich an/aus");
            player.sendMessage("§e/clan kick <Name> §7Kicke einen Spieler aus dem Clan");
            player.sendMessage("§e/clan invite <Name> §7Lade einen Spieler in den Clan ein!");
            player.sendMessage("§e/clan rename <ClanName> <ClanTag> §7Ändere den Namen & Tag vom Clan");
            player.sendMessage("§e/clan info §7Zeige die Info dienes Clans");
            player.sendMessage("§e/clan uinfo §7Zeige die ClanInfo über den Spielernamen");
            player.sendMessage("§e/clan ninfo §7Zeige die ClanInfo über den ClanName");
            player.sendMessage("§e/clan tinfo §7Zeige die ClanInfo über den ClanTag");
            player.sendMessage("§e/cc <Message> §7Schreibe eine Nachricht in den ClanCHat");
            player.sendMessage("§e/clan §7Zeige die 1. Helpseite");
            player.sendMessage("");
        }
    }

}
