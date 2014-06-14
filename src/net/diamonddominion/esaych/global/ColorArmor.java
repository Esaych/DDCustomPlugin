package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class ColorArmor {
	private CustomPlugin plugin;
	
	public ColorArmor(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	private int red = 255, green = 0, blue = 0;
	public int add = 20;
	private int gaining = 1; //1=RED, 2=YELLOW, 2=GREEN, 3=AQUA, 4=BLUE, 5=PURPLE
	private Color color = Color.fromRGB(red, green, blue);

	@SuppressWarnings("deprecation")
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				changeColors();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("customplugin.colorarmor")) {
						apply(player);
					}
				}
			}
		}, 5, 5);
	}
	
	private void changeColors() {
		if (gaining == 1) {
			if (red == 255 && green == 0 && blue == 0) {
				gaining = 2;
				return;
			}
			red += add;
			green -= add;
			blue -= add;
		}
		if (gaining == 2) {
			if (red == 255 && green == 255 && blue == 0) {
				gaining = 3;
				return;
			}
			red += add;
			green += add;
			blue -= add;
		}
		if (gaining == 3) {
			if (red == 0 && green == 255 && blue == 0) {
				gaining = 4;
				return;
			}
			red -= add;
			green += add;
			blue -= add;
		}
		if (gaining == 4) {
			if (red == 0 && green == 255 && blue == 255) {
				gaining = 5;
				return;
			}
			red -= add;
			green += add;
			blue += add;
		}
		if (gaining == 5) {
			if (red == 0 && green == 0 && blue == 255) {
				gaining = 6;
				return;
			}
			red -= add;
			green -= add;
			blue += add;
		}
		if (gaining == 6) {
			if (red == 255 && green == 0 && blue == 255) {
				gaining = 1;
				return;
			}
			red += add;
			green -= add;
			blue += add;
		}
		if (red > 255) {
			red = 255;
		}
		if (red < 0) {
			red = 0;
		}
		if (green > 255) {
			green = 255;
		}
		if (green < 0) {
			green = 0;
		}
		if (blue > 255) {
			blue = 255;
		}
		if (blue < 0) {
			blue = 0;
		}
		color = Color.fromRGB(red, green, blue);
	}
	
	private ItemStack setColor(ItemStack is) {
		LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
		im.setColor(color);
		is.setItemMeta(im);
		return is;
	}
	
	private void apply(Player player) {
		if (player == null)
			return;
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		if (helmet == null || !helmet.getType().equals(Material.LEATHER_HELMET))
			return;
		if (chestplate == null || !chestplate.getType().equals(Material.LEATHER_CHESTPLATE))
			return;
		if (leggings == null || !leggings.getType().equals(Material.LEATHER_LEGGINGS))
			return;
		if (boots == null || !boots.getType().equals(Material.LEATHER_BOOTS))
			return;
		player.getInventory().setHelmet(setColor(helmet));
		player.getInventory().setChestplate(setColor(chestplate));
		player.getInventory().setLeggings(setColor(leggings));
		player.getInventory().setBoots(setColor(boots));
	}

	private void log(String info) {
		plugin.getLogger().info("<ColorArmor> " + info);
	}
}