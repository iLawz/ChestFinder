package me.lasillje.chestfinder.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import net.md_5.bungee.api.ChatColor;

public class ChestUtils {

	/*
	 * Function to find all chests within the radius
	 * @param location Center location
	 * @param radius Radius of the to be searched area
	 * @return List containing all found chests
	 */
	public static List<Block> getNearbyChests(Location location, int radius) {
		
        List<Block> chests = new ArrayList<>();
        
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                	
                	if(location.getWorld().getBlockAt(x,y,z).getType() == Material.CHEST) {
                		chests.add(location.getWorld().getBlockAt(x, y, z));
                	}
                	
                }
            }
        }
        return chests;
    }
	
	/*
	 * Formats a number to be within brackets, saves some space
	 * @param color ChatColor of the specified number
	 * @param num Number to be formatted
	 * @return String with the formatted number, [num]
	 */
	public static String formatNumber(ChatColor color, int num) {
		String output = ChatColor.DARK_GRAY + "[" + color + Integer.toString(num) + ChatColor.DARK_GRAY + "]";
		return output;
	}
	
	
}
