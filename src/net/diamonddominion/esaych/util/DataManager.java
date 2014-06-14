package net.diamonddominion.esaych.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataManager {
	private FileConfiguration data = null;
	private File dataFile = null;
	
	public void reloaddata() {
	    if (dataFile == null) {
	    	dataFile = new File("/home/Bungee/customplugin/data.yml");
	    }
	    data = YamlConfiguration.loadConfiguration(dataFile);
	}
	
	public FileConfiguration cpgetdata() {
		reloaddata();
	    return data;
	}
	
	public void cpsavedata() {
	    try {
	        cpgetdata().save(dataFile);
	    } catch (IOException ex) {
	        System.out.println("Could not save config to " + dataFile);
	        System.out.println(ex.toString());
	    }
	}
	
	public void set(String plugin, String data, Object ob) {
		cpgetdata().set(plugin + data, ob);
		cpsavedata();
	}
	
	public Object get(String plugin, String data) {
		return cpgetdata().get(plugin + data);
	}

}
