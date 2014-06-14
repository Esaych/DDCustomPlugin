package net.diamonddominion.esaych.unused;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class VirtualChest {
	private CustomPlugin plugin;
	
	private FileConfiguration chestData = null;
	private File chestDataFile = null;
	
	private int chestX = 0;
	private int chestY = 254;
	private int chestZ = 0;
	private String chestWorld = "Build";

	public VirtualChest(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		reloadChestData();
		saveChestData();
		log("Enabled");
	}
	
	public boolean onVCCommand(CommandSender sender, String[] args) {
		boolean isAdmin = sender.hasPermission("customplugin.virtualchest.admin");
		if ((args.length == 0) || (args.length > 0 && args[0].equalsIgnoreCase("help"))) {
			msg(sender, "&6======== &aVirtual Chest Help &6========");
			msg(sender, "'&b/vc info&f'/'&b/vc status&f' - See info on your Virtual Chests");
			msg(sender, "'&b/vc open [number]&f' - Open Virtual Chest");
			msg(sender, "'&b/vc new&f' - Add a new chest");
			msg(sender, "Virtual Chest opens a chest which you can store your extra inventory contents and store items until after your deaths and server restarts for later use.");
			msg(sender, "Removing all items from your last chest will delete it. Other chests will remain empty.");
//			msg(sender, "This way chest numbers stay organized.");
			if (isAdmin) {
				msg(sender, "&6======== &c&lAdmin Commands &6========");
				msg(sender, "'&b/vc info [player]&f' - See other player's chest info");
				msg(sender, "'&b/vc open [number] [player]&f' - Open another player's chests");
			}
			msg(sender, "&6=================================");
			return true;
		}
		if (!(sender instanceof Player)) {
			msg(sender, "Consoles don't have anything to store.");
			return true;
		}
		Player player = (Player)sender;
		if (!player.hasPermission("customplugin.virtualchest")) {
			msg(player, "You may not use Virtual Chests here.");
			return true;
		}
		if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("status")) {
			if (isAdmin && args.length > 1) {
				Player search = Bukkit.getPlayer(args[1]);
				if (search != null) {
					showInfo(player, search.getName());
				} else if (playerExists(args[1])) {
					showInfo(player, args[1]);
				} else {
					msg(player, args[1] + " does not exist/have a chest");
				}
			} else {
				showInfo(player, player.getName());
			}
			return true;
		} else if (args[0].equalsIgnoreCase("open")) {
			int chestNum = 0;
			try {
				chestNum = Integer.parseInt(args[1]);
			} catch (Exception e) {
				msg(player, "Please specify a chest number");
				return true;
			}
			if (isAdmin && args.length > 2) {
				Player search = Bukkit.getPlayer(args[2]);
				if (search != null) {
					String searchChest = "";
					int chestNum2 = 0;
					while (searchChest != null) {
						chestNum2++;
						searchChest = getChestData().getString(search.getName() + "." + chestNum2 + ".x");
					}
					Inventory inv = getChestInv(search.getName(), chestNum);
					if (chestNum < chestNum2 && chestNum > 0) {
						player.openInventory(inv);
					} else {
						msg(player, "That chest does not exist");
					}
				} else if (playerExists(args[2])) {
					String searchChest = "";
					int chestNum2 = 0;
					while (searchChest != null) {
						chestNum2++;
						searchChest = getChestData().getString(args[2] + "." + chestNum2 + ".x");
					}
					Inventory inv = getChestInv(args[2], chestNum);
					if (chestNum < chestNum2 && chestNum > 0) {
						player.openInventory(inv);
					} else {
						msg(player, "That chest does not exist");
					}
				} else {
					msg(player, args[2] + " does not exist/have a chest");
				}
			} else {
				String searchChest = "";
				int chestNum2 = 0;
				while (searchChest != null) {
					chestNum2++;
					searchChest = getChestData().getString(player.getName() + "." + chestNum2 + ".x");
				}
				Inventory inv = getChestInv(player.getName(), chestNum);
				if (chestNum < chestNum2 && chestNum > 0) {
					player.openInventory(inv); 
				} else {
					msg(player, "That chest does not exist");
				}
			}
			return true;
		} else if (args[0].equalsIgnoreCase("new")) {
			
			String search = "";
			int chestNum = 0;
			while (search != null) {
				chestNum++;
				search = getChestData().getString(player.getName() + "." + chestNum + ".x");
			}
			chestNum--;
			for(int i = 1; i <= chestNum; i++) {
				Inventory inv = getChestInv(player.getName(), chestNum);
				if (inv == null) {
					int x = getChestData().getInt(player.getName() + "." + chestNum + "x");
					int y = chestY;
					int z = getChestData().getInt(player.getName() + "." + chestNum + "z");
					plugin.getLogger().info("Couldn't find chest at: " + x + ", " + y + ", " + z + ". Regenerating...");
					blockSet(x, y, z, 54);
					blockSet(x+1, y, z, 54);
					inv = getChestInv(player.getName(), i);
				}
				int contSize = 0;
				for (ItemStack item : inv.getContents()) {
					if (item != null) {
						contSize++;
					}
				}
				if (contSize < 40) {
					msg(player, "All chests must have atleast 40 slots full before creating a new chest!");
					return true;
				}
			}
			
			
			
			search = "";
			chestNum = 0;
			while (search != null) {
				chestNum++;
				search = getChestData().getString(player.getName() + "." + chestNum + ".x");
			}
			chestNum--;
			if (chestNum == 0) {
				getChestInv(player.getName(), 1);
				msg(player, "New chest &b1&f created.");
				return true;
			}
			int origz = chestZ - (2 * chestNum) - 2;
			
			chestNum++;

			getChestData().set(player.getName() + "." + chestNum + ".x", getChestData().getInt(player.getName() + "." + (chestNum-1) + ".x"));
			getChestData().set(player.getName() + "." + chestNum + ".z", origz + 2);
			saveChestData();
			
			int x = getChestData().getInt(player.getName() + "." + chestNum + ".x");
			int y = chestY;
			int z = getChestData().getInt(player.getName() + "." + chestNum + ".z");
			generateChest(x, y, z, false);
			
//			Location chestLoc = new Location(Bukkit.getWorld(chestWorld), x, y, z);
//			Chest chest = (Chest) chestLoc.getBlock().getState();
//			chestInv = chest.getInventory();
			msg(player, "New chest &b" + chestNum + "&f created.");
			return true;
		} else {
			msg(player, "Type &b/vc help");
			return true;
		}
	}
	
	public void showInfo(Player player, String lookup) {
		msg(player, "Showing information for: &b" + lookup);
		String search = "";
		int chestNum = 0;
		while (search != null) {
			chestNum++;
			search = getChestData().getString(lookup + "." + chestNum + ".x");
		}
		chestNum--;
		msg(player, "Amount of chests: " + chestNum);
		for(int i = 1; i <= chestNum; i++) {
			Inventory inv = getChestInv(lookup, i);
			if (inv == null) {
				int x = getChestData().getInt(player.getName() + "." + i + "x");
				int y = chestY;
				int z = getChestData().getInt(player.getName() + "." + i + "z");
				plugin.getLogger().info("Couldn't find chest at: " + x + ", " + y + ", " + z + ". Regenerating...");
				blockSet(x, y, z, 54);
				blockSet(x+1, y, z, 54);
				inv = getChestInv(lookup, i);
			}
			int invSize = inv.getSize();
			int contSize = 0;
			for (ItemStack item : inv.getContents()) {
				if (item != null) {
					contSize++;
				}
			}
			if (invSize == contSize) {
				msg(player, "Chest " + i + ": &cFULL");
			} else if (contSize == 0){
				msg(player, "Chest " + i + ": &aEMPTY");
			} else {
				msg(player, "Chest " + i + ": " + contSize + "/" + invSize + " slots used");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public Inventory getChestInv(String playerName, int chestNum){
		World world = Bukkit.getWorld(chestWorld);
		int x;
		int y;
		int z;
//		int z = getChestData().getInt("first_chest.location.z");
		
		Chest chest = null;
		
		if (playerExists(playerName)) {
//			String search = "";
//			int chestNum = 0;
//			while (search != null) {
//				chestNum++;
//				search = getChestData().getString(player.getName() + "." + chestNum + ".x");
//			}
			x = getChestData().getInt(playerName + "." + chestNum + ".x");
			y = chestY;
			z = getChestData().getInt(playerName + "." + chestNum + ".z");
		} else {
			int newZ = chestZ;
			int newX = chestX + (getChestData().getInt("playerNum") * 3);
			int newY = chestY;
			
			if (getChestData().getString("playerNum") == null) {
				getChestData().set("playerNum", 1);
			} else {
				getChestData().set("playerNum", getChestData().getInt("playerNum") + 1);
			}
			
			generateChest(newX, newY, newZ, true);
			plugin.getLogger().info("New chest created at: " + newX + ", " + newY + ", " + newZ);
			
			Location signLoc = new Location(world, newX, newY, newZ+1);
			
			blockSet(newX, newY, newZ+1, 68);
			Sign sign = (Sign) signLoc.getBlock().getState();
			sign.setRawData((byte)3);
			sign.update();
			
			sign.setLine(0, ChatColor.WHITE + "-=-=-=-=-=-=-");
			sign.setLine(1, ChatColor.GREEN + "VirtualChest:");
			sign.setLine(2, playerName);
			sign.setLine(3, ChatColor.WHITE + "-=-=-=-=-=-=-");
			sign.update();
			
			getChestData().set(playerName + ".1.x", newX);
			getChestData().set(playerName + ".1.z", newZ); 
			
			saveChestData();
			
			x = newX;
			y = newY;
			z = newZ;
		}
		
		Location chestLoc = new Location(world, x, y, z);
		try {
			chest = (Chest) chestLoc.getBlock().getState();
		} catch (Exception e) {
//			plugin.getLogger().info("Couldn't find chest at: " + x + ", " + y + ", " + z + ". Regenerating...");
//			blockSet(x, y, z, 54);
//			blockSet(x+1, y, z, 54);
//			chest = (Chest) chestLoc.getBlock().getState();
			return null;
		}
		return chest.getInventory();
	}
	
	public void generateChest(int x, int y, int z, boolean starter){
//		int y = getChestData().getInt("first_chest.location.y");
		
//		int backMat = 4;
		if (starter) {
//			for (int x1 = x-1; x1 <= x+2; x1++)
//				for (int y1 = y; y1 <= y+2; y1++)
//					for (int z1 = z; z1 <= z+1; z1++)
//						blockSet(x1, y1, z1, 0);
//			
//			for (int y1 = y; y1 <= y+2; y1++){
//				for (int x1 = x-1; x1 <= x+2; x1++) {
//					blockSet(x1, y1, z-1, backMat);
//				}
//			}
			blockSet(x, y+1, z, 0);
			blockSet(x, y+2, z, 0);
			blockSet(x+1, y+2, z, 0);
			
		} else {
			for (int x1 = x-1; x1 <= x+2; x1++)
				for (int y1 = y; y1 <= y+2; y1++)
					for (int z1 = z-1; z1 <= z+1; z1++)
						blockSet(x1, y1, z1, 0);
		}
		
		blockSet(x, y, z, 54);
		blockSet(x+1, y, z, 54);
		
//		for (int z1 = z-1; z1 <= z+1; z1++) {
//			for (int x1 = x-1; x1 <= x+2; x1++) {
//				blockSet(x1, y-1, z1, backMat);
//			}
//		}
		saveChestData();
		
		
	}
	
	@SuppressWarnings("deprecation")
	public void blockSet(int x, int y, int z, int id){
		Block block = Bukkit.getWorld(chestWorld).getBlockAt(x, y, z);
//		if (!block.getChunk().isLoaded())
//			block.getChunk().load();
		block.setTypeId(id);
	}
	
	public boolean playerExists(String player) {
		String node = getChestData().getString(player + ".1.x");
		return (node != null);
	}
	
	public void reloadChestData() {
	    if (chestDataFile == null) {
	    chestDataFile = new File("VirtualChests.yml");
	    }
	    chestData = YamlConfiguration.loadConfiguration(chestDataFile);
	 
	    InputStream defConfigStream = plugin.getResource("VirtualChests.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        chestData.setDefaults(defConfig);
	    }
	}
	public FileConfiguration getChestData() {
	    if (chestData == null) {
	        reloadChestData();
	    }
	    return chestData;
	}
	public void saveChestData() {
	    if (chestData == null || chestDataFile == null) {
//	    	reloadChestData();
	    	return;
	    }
	    try {
	        getChestData().save(chestDataFile);
	    } catch (IOException ex) {
	        plugin.getLogger().severe("Could not save config to " + chestDataFile);
	    }
	}
	
	
	public void msg(CommandSender sender, String msg) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "VirtualChest" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			log(msg);
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<VirtualChest> " + info);
	}
}	
