package net.diamonddominion.esaych.unused;

import java.io.File;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;


public class IpRecorder {
	private CustomPlugin plugin;
	
	private static File File;
	private static FileConfiguration YMLFile;

	public IpRecorder(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		loadIps();
		log("Enabled");
	}
	
	public void loadIps() {
		try {
			File = new File("ips.yml");
			YMLFile = YamlConfiguration.loadConfiguration(File);
		} catch (Exception e) {
			log("Ips failed to load :/");
		}
	}
	
	public void saveIps() {
		try {
			YMLFile.save(File);
		} catch (Exception e) {
			log("Ips failed to save :/");
		}
	}
		
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		YMLFile.set(event.getPlayer().getName(), event.getPlayer().getAddress().getAddress().getHostAddress());
		saveIps();
	}

	public void log(String info) {
		plugin.getLogger().info("<IpRecorder> " + info);
	}
}
