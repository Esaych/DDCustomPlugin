package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SignEdit {
	private CustomPlugin plugin;

	public SignEdit(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, String args[]) {
		Player p = (Player) sender;
		if (!p.hasPermission("customplugin.signedit")) {
			p.sendMessage(ChatColor.RED + "You can't edit signs... Sorry!");
			return true;
		}
		Block b = p.getTargetBlock(null, 5);
		if (b != null && (b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST))) {
			if (args.length >= 1) {
				try {
					int lineNum = Integer.parseInt(args[0]);
					Sign s = (Sign) b.getState();
					if (args.length == 1) {
						s.setLine(lineNum - 1, "");
						s.update();
						return true;
					}
					String a = "";
					for (int i = 1; i < args.length; i++) {
						a += args[i] + " ";
					}
					a = a.substring(0, a.length()-1);
					if (a.length() > 16) {
						p.sendMessage(ChatColor.RED + "A sign line can be up to 16 characters long");
					}
					s.setLine(lineNum - 1, ChatColor.translateAlternateColorCodes('&', a));
					s.update();
				} catch (Exception e) {
					p.sendMessage(ChatColor.RED + "Type /edit <lineNum> <line>");
				}
				return true;
			} else {
				p.sendMessage(ChatColor.RED + "Type /edit <lineNum> <line>");
				return true;
			}
		} else {
			p.sendMessage(ChatColor.RED + "You must look at a sign to edit");
			return true;
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<SignEdit> " + info);
	}
}
