package me.lasillje.chestfinder.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.lasillje.chestfinder.ChestFinder;
import me.lasillje.chestfinder.util.ChestUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandFindChest implements CommandExecutor, TabCompleter {
	
	private static HashMap<Player, List<Block>> chestListPlayers = new HashMap<Player, List<Block>>();
	private final String PERM_DENIED = ChatColor.RED + "Sorry, you have no permission to use this command.";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Can't execute this command as console.");
			return true;
		}
		
		Player player = (Player) sender;
	
		if(args.length == 0) {
			if(player.hasPermission("chestfinder.help")) {
				sender.sendMessage(ChatColor.YELLOW + "/findchest <radius>" + ChatColor.GREEN + " - Finds all chests within the specified radius");
				sender.sendMessage(ChatColor.YELLOW + "/findchest list" + ChatColor.GREEN + " - Lists found chests.");
				sender.sendMessage(ChatColor.YELLOW + "/findchest open <index>" + ChatColor.GREEN + " - Opens the inventory of the specified chest");
				return true;
			}
			sender.sendMessage(PERM_DENIED);
		} else
			
		if(args.length == 1) {
			
			switch(args[0]) {
			
			case "list": {
				if(player.hasPermission("chestfinder.list")) {
					listChests(player);
					return true;
				}
				sender.sendMessage(PERM_DENIED);
				break;
			}
			
			default: {
				if(player.hasPermission("chestfinder.find")) {
					findChests(player,args[0]);
					return true;
				}
				sender.sendMessage(PERM_DENIED);
				break;
			}
			
			}
			
		} else
			
		if(args.length == 2) {
			
			switch(args[0]) {
			
			case "open": {
				if(player.hasPermission("chestfinder.open")) {
					if(StringUtils.isNumeric(args[1])) {
						int index = Integer.valueOf(args[1]);
						openChest(player,index);
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "Please enter a number.");
						return true;
					}
				}
				sender.sendMessage(PERM_DENIED);
				break;
			}
			
			default: {
				player.sendMessage(ChatColor.RED + "/findchest open <index>");
				break;
			}
			
			}
			
		}
		
		return true;
	}
	
	/*
	 * Function to list chests again
	 * @param player Command sender
	 */
	private void listChests(Player player) {
		
		List<Block> chests = chestListPlayers.get(player);
		
		if(chests == null || chests.isEmpty()) {
			player.sendMessage(ChatColor.RED + "There are no chests listed for you.");
			return;
		}
		
		int index = 0;
		
		player.sendMessage(ChatColor.GOLD + "Click on a line to teleport to that chest!");
		
		for(Block block : chests) {
			
			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			
			TextComponent coords = new TextComponent(ChatColor.GREEN + "Chest" + ChestUtils.formatNumber(ChatColor.YELLOW, index)
				+ ChatColor.GREEN + " found at: " + ChatColor.YELLOW + "" + x + "," + y + "," + z);
			
			coords.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName() + " " + x + " " + y + " " + z));
			player.spigot().sendMessage(coords);
			index++;
		}
	}
	
	/*
	 * Function to find and list chests
	 * @param player Command sender
	 * @param sRadius Radius as string
	 */
	private void findChests(Player player, String sRadius) {
		
		if(chestListPlayers.containsKey(player) ) {
			chestListPlayers.remove(player);
		}
		
		if(StringUtils.isNumeric(sRadius)) {
			
			int radius = Integer.valueOf(sRadius);
			int index = 0;
			
			if(radius > ChestFinder.PLUGIN.getConfig().getInt("maxradius")) {
				player.sendMessage(ChatColor.RED + "Radius is too high!");
				return;
			}
			
			List<Block> chests = ChestUtils.getNearbyChests(player.getLocation(), radius);
			
			if(chests.isEmpty()) {
				player.sendMessage(ChatColor.RED + "No chests found!");
				return;
			}
			
			player.sendMessage(ChatColor.GOLD + "Click on a line to teleport to that chest!");
			
			for(Block block : chests) {
				
				int x = block.getX();
				int y = block.getY();
				int z = block.getZ();
				
				TextComponent coords = new TextComponent(ChatColor.GREEN + "Chest" + ChestUtils.formatNumber(ChatColor.YELLOW, index)
					+ ChatColor.GREEN + " found at: " + ChatColor.YELLOW + "" + x + "," + y + "," + z);
				
				coords.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName() + " " + x + " " + y + " " + z));
				player.spigot().sendMessage(coords);
				index++;
			}
			
			chestListPlayers.put(player, chests);

		} else {
			player.sendMessage(ChatColor.RED + "Please enter a number!");
			return;
		}
		
	}
	
	/*
	 * Function to open the inventory of specified chest
	 * @param player Command sender
	 * @param index Index of the to-be-opened chest
	 */
	private void openChest(Player player, int index) {
	
		List<Block> chests = chestListPlayers.get(player);
		
		if(chests == null || chests.isEmpty()) {
			player.sendMessage(ChatColor.RED + "There are no chests listed for you.");
			return;
		}
		
		if(index > chests.size()-1|| index < 0) {
			player.sendMessage(ChatColor.RED + "Please enter a valid index.");
			return;
		}
		
		Chest chest = (Chest) chests.get(index).getState();
		player.openInventory(chest.getInventory());
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();
		
		if(args.length == 1) {
			
			if(sender.hasPermission("chestfinder.list")) {
				commands.add("list");
			}
			
			if(sender.hasPermission("chestfinder.open")) {
				commands.add("open");
			}
			
			StringUtil.copyPartialMatches(args[0], commands, completions);
		}
		
		Collections.sort(completions);
		return completions;
	
	}
}
