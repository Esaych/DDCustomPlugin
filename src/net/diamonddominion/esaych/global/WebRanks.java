package net.diamonddominion.esaych.global;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class WebRanks {

	private CustomPlugin plugin;

	public WebRanks(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	File file;
	FileConfiguration ymlfile;

	@SuppressWarnings("deprecation")
	public void enable() {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateFile();
			}
		}, 20*5, 20*60*2);
		log("Enabled");
	}
	
	public void updateFile() {
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manload");
		try {
			file = new File("plugins/GroupManager/worlds/survival/users.yml");
			ymlfile = YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			log("Failed to load ranks!");
		}
		ArrayList<String> stats = updateWith();
		String data = "";
		for (String line : stats) {
			try {
				data = data + line.substring(1, line.length()) + "\r\n";
			} catch (Exception e) {
				data = data + "\r\n";
			}
		}
		data = data.substring(0, data.length() - 2);
		try {
			writeFile("ranks.txt", data);
		} catch (Exception e) {
			log("Failed to write file");
		}
	}

	public ArrayList<String> updateWith() {
		String admins = "";
		String mods = "";
		String demigods = "";
		String bosses = "";
		String premiums = "";
		String sponsors = "";
		String vipps = "";
		String vips = "";
		try {
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String currentLine = reader.nextLine();
				if (currentLine.startsWith("  ")
						&& !currentLine.startsWith("   ")) {
					String name = currentLine.replaceAll(" ", "")
							.replaceAll(":", "").replaceAll("'", "");
					if (!name.contains("?"))
						try {
							String rank = ymlfile.getString("users." + name
									+ ".group");
							if (rank.contains("Admin") && !rank.contains("Re"))
								admins = admins + ";" + name;
							if (rank.contains("Mod"))
								mods = mods + ";" + name;
							if (rank.contains("DemiGod"))
								demigods = demigods + ";" + name;
							if (rank.contains("Boss"))
								bosses = bosses + ";" + name;
							if (rank.contains("Premium"))
								premiums = premiums + ";" + name;
							if (rank.contains("Sponsor"))
								sponsors = sponsors + ";" + name;
							if (rank.contains("VIP+")) {
								vipps = vipps + ";" + name;
							} else if (rank.contains("VIP"))
								vips = vips + ";" + name;
						} catch (Exception e) {
							log("Can not find " + name);
						}
				}
			}
		} catch (FileNotFoundException e) {
			log("Extracting rankings failure. :(");
		}
		ArrayList<String> stats = new ArrayList<String>();
		stats.add(admins);
		stats.add(mods);
		stats.add(bosses);
		stats.add(premiums);
		stats.add(sponsors);
		stats.add(vipps);
		stats.add(vips);
		stats.add(demigods);

		return stats;
	}

	public void log(String info) {
		plugin.getLogger().info("<WebRanks> " + info);
	}

	public static void writeFile(String filename, String text)
			throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(text.getBytes("UTF-8"));
		} catch (IOException e) {
			close(fos);
			throw e;
		}
	}

	public static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException ignored) {
		}
	}
}
