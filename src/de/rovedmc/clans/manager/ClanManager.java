package de.rovedmc.clans.manager;

import de.rovedmc.clans.util.MySQL;
import net.md_5.bungee.api.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClanManager {

    //Clans

    public static void createClan(String ClanName, String ClanTag, String CreatorUUID) {
        long time = System.currentTimeMillis();
        MySQL.update("INSERT INTO Clans(ClanName, ClanTag, Creator, Leader, Mods, Member, CreateDate, Group1Prefix, Group2Prefix, Group3Prefix) VALUES" +
                " ('" + ClanName + "','" + ClanTag + "','" + CreatorUUID + "','" + CreatorUUID + ";','',''.,'" + time +"','§4Leader','§cModerator','§9Member')");
    }

    public static void deleteClan(String ClanName){
        MySQL.update("DELETE From Clans WHERE ClanName='" + ClanName + "'");
    }

    public static void addMemberToClan(String ClanName, String Rank, String UUID) {
        String Raw  = getMemberListRaw(ClanName, Rank) + UUID + ";";
        MySQL.update("UPDATE Clans SET " + Rank + "='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static void setPrefixToGroup(String ClanName, String What, String Prefix) {
        Prefix = ChatColor.translateAlternateColorCodes('&', Prefix);
        MySQL.update("UPDATE Clans SET Group" + What + "Prefix='" + Prefix + "' WHERE ClanName='" + ClanName + "'");
    }

    public static String getPrefixFromGroup(String ClanName, String What){
        return  get("Group" + What + "Prefix", "Clans", "ClanName", ClanName);
    }

    public static void rmvMemberFromClan(String ClanName, String Rank, String UUID) {
        String Raw  = getMemberListRaw(ClanName, Rank).replace(UUID + ";", "");
        MySQL.update("UPDATE Clans SET " + Rank + "='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static String getMemberListRaw(String ClanName,String What) {
        return get(What, "Clans", "ClanName", ClanName);
    }

    public static List<String> getMemberList(String ClanName, String What) {
        String Raw = getMemberListRaw(ClanName, What);
        List<String> list = new ArrayList<>();
        if (Raw.isEmpty()) return list;
        for (String split : Raw.split(";")) {
            list.add(split);
        }
        return list;
    }

    public static List<String> getAllMembers(String ClanName){
        List<String> members = new ArrayList<>();
        for (String uuids : getMemberList(ClanName, "Leader")) {
            members.add(uuids);
        }
        for (String uuids : getMemberList(ClanName, "Mods")) {
            members.add(uuids);
        }
        for (String uuids : getMemberList(ClanName, "Member")) {
            members.add(uuids);
        }
        return members;
    }

    public static boolean existClanName(String ClanName) {
        ResultSet rs = MySQL.query("SELECT ClanName FROM " + "Clans" + " WHERE ClanName='" + ClanName + "'");
        try {
            if (rs.next()) {
                return rs.getString("ClanName") != null;
            }
            rs.close();
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean existClanTag(String ClanTag) {
        ResultSet rs = MySQL.query("SELECT ClanTag FROM " + "Clans" + " WHERE ClanTag='" + ClanTag + "'");
        try {
            if (rs.next()) {
                return rs.getString("ClanTag") != null;
            }
            rs.close();
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //ClanPlayer

    public static void managePlayer(String UUID, String Name){
        if (!existPlayer(UUID)){
            MySQL.update("INSERT INTO ClanPlayer(UUID, Name, ClanName, ClanRank, RequestList, IgnoreList, AllowChat, AllowRequest) VALUES" +
                    " ('" + UUID + "','" + Name + "','','','','')");
        } else {
            if (!Name.equals(getNameByUUID(UUID))){
                MySQL.update("UPDATE ClanPlayer SET Name='" + Name + "' WHERE UUID='" + UUID + "'");
            }
        }
    }


    public static void setSettingTpPlayer(String UUID, String SettingKategorie, String Setting) {
        MySQL.update("UPDATE ClanPlayer SET " + SettingKategorie + "='" + Setting + "' WHERE UUID='" + UUID + "'");
    }

    public static void setClanNameToPlayer(String UUID, String ClanName) {
        MySQL.update("UPDATE ClanPlayer SET ClanName='" + ClanName + "' WHERE UUID='" + UUID + "'");
    }

    public static void setRankToPlayer(String UUID, String Rank) {
        MySQL.update("UPDATE Rank SET Rank='" + Rank + "' WHERE UUID='" + UUID + "'");
    }

    public static void addRequestToPlayer(String UUID, String ClanName){
        String Raw = getPlayerListRaw(UUID, "RequestList") + ClanName + ";";
        MySQL.update("UPDATE ClanPlayer SET RequestList='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static void rmvRequestFromPlayer(String UUID, String ClanName){
        String Raw = getPlayerListRaw(UUID, "RequestList").replace(ClanName + ";", "");
        MySQL.update("UPDATE ClanPlayer SET RequestList='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static void addIgnoreToPlayer(String UUID, String ClanName){
        String Raw = getPlayerListRaw(UUID, "IgnoreList") + ClanName + ";";
        MySQL.update("UPDATE ClanPlayer SET IgnoreList='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static void rmvIgnoreFromPlayer(String UUID, String ClanName){
        String Raw = getPlayerListRaw(UUID, "IgnoreList").replace(ClanName + ";", "");
        MySQL.update("UPDATE ClanPlayer SET IgnoreList='" + Raw + "' WHERE UUID='" + UUID + "'");
    }

    public static String getUUIDByName(String Name){
        return get("UUID", "ClanPlayer", "Name", Name);
    }

    public static String getNameByUUID(String UUID){
        return get("Name", "ClanPlayer", "UUID", UUID);
    }

    public static String getSettingByUUID(String UUID, String SettingKategorie){
        return get(SettingKategorie, "ClanPlayer", "UUID", UUID);
    }

    public static String getRankByUUID(String UUID){
        return get("Rank", "ClanPlayer", "UUID", UUID);
    }

    public static String getClanNameByUUID(String UUID){
        return get("ClanName", "ClanPlayer", "UUID", UUID);
    }

    public static String getPlayerListRaw(String UUID,String What) {
        return get(What, "ClanPlayer", "UUID", UUID);
    }

    public static List<String> getPlayerList(String UUID, String What) {
        String Raw = getPlayerListRaw(UUID, What);
        List<String> list = new ArrayList<>();
        if (Raw.isEmpty()) return list;
        for (String split : Raw.split(";")) {
            list.add(split);
        }
        return list;
    }

    public static boolean existPlayer(String UUID) {
        ResultSet rs = MySQL.query("SELECT UUID FROM " + "ClanPlayer" + " WHERE UUID='" + UUID + "'");
        try {
            if (rs.next()) {
                return rs.getString("UUID") != null;
            }
            rs.close();
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //Main

    public static String get(String Select, String Database, String Where, String Whereresult) {
        ResultSet rs = MySQL
                .query("SELECT " + Select + " FROM " + Database + " WHERE " + Where + "='" + Whereresult + "'");
        try {
            if (rs.next()) {
                String result = rs.getString(Select);
                return result;
            }
        } catch (SQLException ex) {
            return "ERROR";
        }
        return "ERROR";
    }


}
