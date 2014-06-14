package net.diamonddominion.esaych.survival;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;


public class WelcomeSign {
	private CustomPlugin plugin;

	public WelcomeSign(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	private int scroll = 0;
	public Sign sign;

	public void enable() {
		sign = (Sign) Bukkit.getWorld("Survival").getBlockAt(64, 72, 200).getState();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				sign.setLine(2, ChatColor.AQUA + ("Diamond Dominion     Diamond Dominion").substring(scroll, scroll + 15));
				sign.update();
				scroll++;
				if (scroll > 20)
					scroll = 0;
			}
		}, 20*5, 5);
		log("Enabled");
	}

	public void log(String info) {
		plugin.getLogger().info("<WelcomeSign> " + info);
	}
}
