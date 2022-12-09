package com.github.allybe.statsshow.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

import java.io.*;
import java.util.Map;

import org.json.*;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class statsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            playerSentHandler(sender, command, label, args);
        } else {
            sender.sendMessage("This command can only be ran by a player");
        }

        return true;
    }

    public void playerSentHandler(CommandSender sender, Command command, String label, String[] args) {

        String rawStatsJSON = "";
        String requestedStats = "minecraft:wheat_seeds";
        Player player = (Player) sender;

        switch (args.length){
            case 0:
                // No arguments
                try {
                    rawStatsJSON = getPlayerStatsFile(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                // One argument
                requestedStats = args[0];
                try {
                    rawStatsJSON = getPlayerStatsFile(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                // Two arguments
                requestedStats = args[0];
                player = Bukkit.getPlayer(args[1]);
                try {
                    rawStatsJSON = getPlayerStatsFile(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        if (player == null || rawStatsJSON == "") {
            sender.sendMessage("Player not found, or player does not have a stats file");
            return;
        }

        sendStats(sender, rawStatsJSON, requestedStats);
    }

    public void sendStats(CommandSender sender, String playerStatsRaw, String requestedStats) {
        JSONObject playerStats = parseJSON(playerStatsRaw);

        try {
            Bukkit.createBlockData(requestedStats);
        } catch (IllegalArgumentException e) {

        }

        try {
            Map stats = (Map) playerStats.get("stats");

            Map mined = (Map) stats.get("minecraft:mined");
            Map picked_up = (Map) stats.get("minecraft:picked_up");
            Map dropped = (Map) stats.get("minecraft:dropped");
            Map used = (Map) stats.get("minecraft:used");

            sender.sendMessage("You have picked up " + picked_up.get(requestedStats) + " " + Bukkit.createBlockData(requestedStats).getMaterial().name().toLowerCase());
            sender.sendMessage("You have dropped " + dropped.get(requestedStats) + " " +Bukkit.createBlockData(requestedStats).getMaterial().name().toLowerCase());
            sender.sendMessage("You have used " + used.get(requestedStats) + " " + Bukkit.createBlockData(requestedStats).getMaterial().name().toLowerCase());

            if (Bukkit.createBlockData(requestedStats).getMaterial().isBlock()) {
                sender.sendMessage("You have mined " + mined.get(requestedStats) + " " + Bukkit.createBlockData(requestedStats).getMaterial().name().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public JSONObject parseJSON (String jsonString){
        Object playerStatsObject;

        try {
            playerStatsObject = new JSONParser().parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        
        return (JSONObject) playerStatsObject;
    }

    public String getPlayerStatsFile (Player player) throws IOException {

        String playerUUID = player.getUniqueId().toString();
        String filePath = player.getWorld().getWorldFolder().getPath() + "/stats/" + playerUUID + ".json";

        FileReader playerStatsFile = new FileReader(new File(filePath));

        BufferedReader br = new BufferedReader(playerStatsFile);

        String line;
        String fullFile = "";

        while ((line = br.readLine()) != null) {
            fullFile += line;
        }


        return fullFile;
    }

}



