package net.diamonddominion.esaych.unused;

import net.diamonddominion.esaych.CustomPlugin;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.EntityTracker;
import net.minecraft.server.v1_6_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class UnderCover {
	private CustomPlugin plugin;

	public UnderCover(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender) {
		if (sender instanceof Player) {
			if (((Player) sender).getName().equals("Esaych")) {
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "     &c-&r &5Esaych"));
				setPlayerName((EntityPlayer) ((CraftPlayer) sender).getHandle(), "Hycase");
			}
		}
		return true;
	}

	private void setPlayerName(EntityPlayer player, String newname) {
		WorldServer world = (WorldServer) player.world;
		EntityTracker tracker = world.tracker;
		tracker.untrackEntity(player);
//		player.name = newname;
		player.displayName = ChatColor.GRAY + newname;
		Bukkit.dispatchCommand(player.getBukkitEntity(), "fullprefix &7Hycase");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload mcmmo");
		player.getBukkitEntity().setPlayerListName(ChatColor.GRAY + newname);
		tracker.track(player);
	}
	
	public void log(String info) {
		plugin.getLogger().info("<UnderCover> " + info);
	}
}
