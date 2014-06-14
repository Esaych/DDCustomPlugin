package net.diamonddominion.esaych.global;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeGlobalCommand {
	private static CustomPlugin plugin;

	public BungeeGlobalCommand(CustomPlugin plugin) {
		BungeeGlobalCommand.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	//2.55
	public void onBungeeMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subChannel = in.readUTF();
			if (subChannel.equals("DDCustomPlugin_GBCommand")) {
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);

				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String command = msgin.readUTF();
				log(command);
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendCommand(String cmd) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("DDCustomPlugin_GBCommand");

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF(cmd);

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// OR, if you don't need to send it to a specific player
		Player p = Bukkit.getOnlinePlayers()[0];
		 
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}
	
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission("customplugin.globalcommand"))
			return true;
		String cmd = "";
		for (int i = 0; i < args.length; i++) {
			cmd += args[i] + " ";
		}
		cmd = cmd.substring(0, cmd.length()-1);
		sendCommand(cmd);
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
		sender.sendMessage(ChatColor.BLUE + "Complete: " + ChatColor.DARK_GRAY + "/" + cmd);
		return true;
	}

	public void log(String info) {
		plugin.getLogger().info("<BungeeGlobalCommand> " + info);
	}
}
