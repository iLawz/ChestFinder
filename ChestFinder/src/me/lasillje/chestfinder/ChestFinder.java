package me.lasillje.chestfinder;

import java.util.logging.Level;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import me.lasillje.chestfinder.commands.CommandFindChest;

public class ChestFinder extends JavaPlugin {

	public static ChestFinder PLUGIN;
	
	@Override
	public void onEnable() {
		
		PLUGIN = this;
		
		getConfig().addDefault("maxradius", 150);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		registerCommand("findchest", new CommandFindChest());
		
	}
	
	public void registerCommand(String name, CommandExecutor executor) {
		PluginCommand command = getCommand(name);
		if(command != null) {
			command.setExecutor(executor);
			if(executor instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter) executor);
			}
		} else {
			getLogger().log(Level.SEVERE, "Couldn't register command: /" + name);
		}
	}	
}
