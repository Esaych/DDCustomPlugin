package net.diamonddominion.esaych.unused;

import java.io.File;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;


public class JailSigns {

	private CustomPlugin plugin;

	public JailSigns(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, new Integer[] {
						Integer.valueOf(130) 
				})
				{
					public void onPacketSending(PacketEvent event) {
						if (event.getPacketID() == 130)
							try {
								event.setPacket(modify(event.getPacket(), event.getPlayer().getName()));
							} catch (FieldAccessException e) {
								plugin.getLogger().log(Level.SEVERE, "Couldn't access field.", e);
							}
					}
				});
		log("Enabled");
	}

	private PacketContainer modify(PacketContainer psign, String playerName) {
		Packet82UpdateSign incoming = new Packet82UpdateSign(psign);
		Packet82UpdateSign outgoing = new Packet82UpdateSign();
		String[] lines = incoming.getLines();
		if (lines[0].equals("[JailSign]")) {
			if (inJail(playerName) != -1) {
				lines[0] = ChatColor.WHITE + "Suspect:";
				lines[1] = ChatColor.DARK_BLUE + playerName;
				if (inJail(playerName)/1000/60/60 != 0) {
//					lines[2] = ChatColor.WHITE + "Hours Left:";
					lines[2] = ChatColor.DARK_RED + String.valueOf((int)inJail(playerName)/1000/60/60).substring(0,15) + "H";
				} else if (inJail(playerName)/1000/60 != 0) {
//					lines[2] = ChatColor.WHITE + "Minutes Left:";
					lines[2] = ChatColor.DARK_RED + String.valueOf((int)inJail(playerName)/1000/60).substring(0,15) + "M";
				} else {
//					lines[2] = ChatColor.WHITE + "Seconds Left:";
					lines[2] = ChatColor.DARK_RED + String.valueOf((int)inJail(playerName)/1000).substring(0,15) + "S";
				}
			} else {
				lines[2] = ChatColor.AQUA + "You are FREE";
			}
		}
		String[] newLines = { lines[0], lines[1], lines[2], lines[3] };
		outgoing.setX(incoming.getX());
		outgoing.setY(incoming.getY());
		outgoing.setZ(incoming.getZ());
		outgoing.setLines(newLines);
		return outgoing.getHandle();
	}

	public int inJail(String name) {
		try {
			int num = getPlayerData(name).getInt("timestamps.jail");
			if (num != 0)
				return (int) (num - System.currentTimeMillis());
		} catch (Exception e) {}
		return -1;
	}

	public FileConfiguration getPlayerData(String player) {
		return YamlConfiguration.loadConfiguration(new File("plugins/Essentials/userdata/" + player.toLowerCase() + ".yml"));
	}

	public void log(String info) {
		plugin.getLogger().info("<JailSigns> " + info);
	}
}

class Packet82UpdateSign extends AbstractPacket
{
	public static final int ID = 130;

	public Packet82UpdateSign()
	{
		super(new PacketContainer(130), 130);
		this.handle.getModifier().writeDefaults();
	}

	public Packet82UpdateSign(PacketContainer packet) {
		super(packet, 130);
	}

	public int getX()
	{
		return ((Integer)this.handle.getIntegers().read(0)).intValue();
	}

	public void setX(int value)
	{
		this.handle.getIntegers().write(0, Integer.valueOf(value));
	}

	public short getY()
	{
		return ((Integer)this.handle.getIntegers().read(1)).shortValue();
	}

	public void setY(short value)
	{
		this.handle.getIntegers().write(1, Integer.valueOf(value));
	}

	public int getZ()
	{
		return ((Integer)this.handle.getIntegers().read(2)).intValue();
	}

	public void setZ(int value)
	{
		this.handle.getIntegers().write(2, Integer.valueOf(value));
	}

	public String[] getLines()
	{
		return (String[])this.handle.getStringArrays().read(0);
	}

	public void setLines(@Nonnull String[] lines)
	{
		if (lines == null)
			throw new IllegalArgumentException("Array cannot be NULL.");
		if (lines.length != 4)
			throw new IllegalArgumentException("The lines array must be four elements long.");
		this.handle.getStringArrays().write(0, lines);
	}
}

abstract class AbstractPacket
{
	protected PacketContainer handle;

	protected AbstractPacket(PacketContainer handle, int packetID)
	{
		if (handle == null)
			throw new IllegalArgumentException("Packet handle cannot be NULL.");
		if (handle.getID() != packetID) {
			throw new IllegalArgumentException(handle.getHandle() + " is not a packet " + 
					Packets.getDeclaredName(packetID) + "(" + packetID + ")");
		}
		this.handle = handle;
	}

	public PacketContainer getHandle()
	{
		return this.handle;
	}
}