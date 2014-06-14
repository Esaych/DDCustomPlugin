package net.diamonddominion.esaych.unused;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;


public class AutoConfiscate implements Listener {
	private CustomPlugin plugin;
	private FileConfiguration chestData = null;
	private File chestDataFile = null;
	
	public AutoConfiscate(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		reloadChestData();
		try {
			if (getChestData().getString("first_chest.location.world").equals("")) {
				log("Reloading Chest Data Defaults");
				getChestData().set("first_chest.location.world", "Survival");
				getChestData().set("first_chest.location.x", 196);
				getChestData().set("first_chest.location.y", 64);
				getChestData().set("first_chest.location.z", 177);
				getChestData().set("players_confiscated", 0);
				getChestData().set("chest_range", 0);
			}
		} catch (Exception e) {
			log("Reloading Chest Data Defaults");
			getChestData().set("first_chest.location.world", "Survival");
			getChestData().set("first_chest.location.x", 196);
			getChestData().set("first_chest.location.y", 64);
			getChestData().set("first_chest.location.z", 177);
			getChestData().set("players_confiscated", 0);
			getChestData().set("chest_range", 0);
		}
//		if (node.equals("")) {
//			log("Reloading Chest Data Defaults");
//			getChestData().set("first_chest.location.world", "Survival");
//			getChestData().set("first_chest.location.x", 196);
//			getChestData().set("first_chest.location.y", 64);
//			getChestData().set("first_chest.location.z", 200);
//			getChestData().set("players_confiscated", 0);
//			getChestData().set("chest_range", 0);
//		}
		saveChestData();
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 0) {
			msg(sender, "Invalid Arguments!");
			return true;
		}
		if (args[0].equalsIgnoreCase("tpchest")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (((Player) sender).hasPermission("autoconfiscate.admin")) {
					if (args.length >= 2) {
						if (playerExists(args[1])) {
							World world = Bukkit.getWorld(getChestData().getString("first_chest_location.world"));
							int x = getChestData().getInt(args[1] + ".chest_location.x");
							String isY = getChestData().getString(args[1] + ".chest_location.y");
							if (isY == null) {
								getChestData().set(args[1] + ".chest_location.y", getChestData().getInt("first_chest_location.y"));
								saveChestData();
							}
							int y = getChestData().getInt(args[1] + ".chest_location.y");
							int z = getChestData().getInt(args[1] + ".chest_location.z");
							Location chestLoc = new Location(world, x + 0.5, y, z + 1.5);
							player.teleport(chestLoc);
							msg(sender, "Teleported to " + args[1] + "'s chest");
						} else {
							msg(sender, "Player not found");
						}
					} else {
						msg(sender, "Correct use: /ac tpchest <player>");
					}
				} else {
					msg(sender, "You have no permission to do that!");
				}
			} else {
				msg(sender, "You can't tp to a chest!");
			}
		} else if (args[0].equalsIgnoreCase("regen")) {
			if (sender instanceof Player) {
				if (((Player) sender).hasPermission("autoconfiscate.admin")) {
					regen();
					msg(sender, "Area Regenerated.");
				}
			} else {
				regen();
				msg(sender, "Area Regenerated.");
			}
		} else {
			msg(sender, "Invalid Command");
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void regen() {
		try {
			Scanner scanner = new Scanner(chestDataFile);
			ArrayList<String> lines = new ArrayList<String>();
			while (scanner.hasNext()) {
				String string = scanner.nextLine();
				if (!string.startsWith(" ")) {
					if (!(string.startsWith("enabled: ") || string.equals("first_chest_location:") || string.startsWith("players_confiscated:") || string.startsWith("chest_range:")))
						lines.add(string.substring(0, string.length()-1));
				}
			}
			for (String name : lines) {
				String isY = getChestData().getString(name + ".chest_location.y");
				if (isY == null) {
					getChestData().set(name + ".chest_location.y", getChestData().getInt("first_chest.location.y"));
				}
				int x = getChestData().getInt(name + ".chest_location.x");
				int y = getChestData().getInt(name + ".chest_location.y");
				int z = getChestData().getInt("first_chest.location.z");
				y++;
				
				blockSet(x, y, z-1, 4);
				blockSet(x+1, y, z-1, 4);
				
				blockSet(x, y, z, 68);
				
				Sign sign = (Sign) new Location(Bukkit.getWorld(getChestData().getString("first_chest.location.world")), x, y, z).getBlock().getState();
				sign.setRawData((byte)3);
				sign.update();
				
				sign.setLine(0, ChatColor.WHITE + "-=-=-=-=-=-=-");
				sign.setLine(1, ChatColor.DARK_RED + "CONFISCATED:");
				sign.setLine(2, name);
				sign.setLine(3, ChatColor.WHITE + "-=-=-=-=-=-=-");
				sign.update();

				blockSet(x+1, y, z, 50);
//				log(name + ", " + x + ";" + y + ";" + z);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void msg(CommandSender sender, String msg) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "AutoConfiscate" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + msg);
		} else {
			log(msg);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void scanInventory(Player player, Inventory inv) {
		reloadChestData();
		if (!(player.getGameMode() == GameMode.CREATIVE)
				&& !player.getWorld().getName().equals("GameWorld")
				&& !player.hasPermission("autoconfiscate.bypass")) {
			
			scanPlayerArmor(player);
			
			int diamonds = 0;
			int emeralds = 0;
			int dragonEggs = 0;
			int superGoldenApples = 0;

			for (int i = 0; i < inv.getSize(); i++) {
				ItemStack is = inv.getItem(i);
				if (is != null) {
//					ItemStack isOld = is;
//					is = replaceItem(is);
//					if (isOld != is) {
//						inv.remove(isOld);
//						inv.addItem(is);
//					}
					
					if (confiscatable(player, is)) {
						inv.remove(is);
//						if (plugin.getConfig().getBoolean("confiscate.supply_with_unenchanted_item") && !itemType(is).equals("other") && is.getEnchantments().size() != 0) {
//							ItemStack noEnchants = new ItemStack(is.getType(), is.getAmount(), is.getDurability());
//							inv.addItem(noEnchants);
//						}
//						if (plugin.getConfig().getBoolean("chest_room.create")) {
							moveToChest(player, is);
							saveChestData();
//						}
						addToList(player, is);
					}
					if (is.getTypeId() == 57)
						diamonds += is.getAmount();
					if (is.getTypeId() == 133)
						emeralds += is.getAmount();
					if (is.getTypeId() == 122)
						dragonEggs += is.getAmount();
					if (is.getTypeId() == 322 && is.getDurability() == (short) 1)
						superGoldenApples += is.getAmount();
				}
			}

			int maxDiamonds = 320;
			int maxEmeralds = 320;
			int maxDragonEggs = 5;
			int maxSuperGoldenApples = 192;
			
			if (diamonds > maxDiamonds) {
				inv.remove(Material.DIAMOND_BLOCK);
				ItemStack newItems = new ItemStack(Material.DIAMOND_BLOCK, maxDiamonds);
				ItemStack moveItems = new ItemStack(Material.DIAMOND_BLOCK, diamonds-maxDiamonds);
				inv.addItem(newItems);
				moveToChest(player, moveItems);
				saveChestData();
			}
			if (emeralds > maxEmeralds) {
				inv.remove(Material.EMERALD_BLOCK);
				ItemStack newItems = new ItemStack(Material.EMERALD_BLOCK, maxEmeralds);
				ItemStack moveItems = new ItemStack(Material.EMERALD_BLOCK, emeralds-maxEmeralds);
				inv.addItem(newItems);
				moveToChest(player, moveItems);
				saveChestData();
			}
			if (dragonEggs > maxDragonEggs) {
				inv.remove(Material.DRAGON_EGG);
				ItemStack newItems = new ItemStack(Material.DRAGON_EGG, maxDragonEggs);
				ItemStack moveItems = new ItemStack(Material.DRAGON_EGG, dragonEggs-maxDragonEggs);
				inv.addItem(newItems);
				moveToChest(player, moveItems);
				saveChestData();
			}
			if (superGoldenApples > maxSuperGoldenApples) {
				inv.remove(Material.GOLDEN_APPLE);
				ItemStack newItems = new ItemStack(Material.GOLDEN_APPLE, maxSuperGoldenApples);
				newItems.setDurability((short)1);
				ItemStack moveItems = new ItemStack(Material.GOLDEN_APPLE, superGoldenApples-maxSuperGoldenApples);
				moveItems.setDurability((short)1);
				inv.addItem(newItems);
				moveToChest(player, moveItems);
				saveChestData();
			}
		}
	}
	
	public void scanPlayerArmor(Player player) {
		PlayerInventory playerInv = player.getInventory();
		ItemStack delete = new ItemStack (Material.AIR);
		ItemStack is = playerInv.getHelmet();
		if (is != null) {
			if (confiscatable(player, is)) {
				playerInv.setHelmet(delete);
				moveToChest(player, is);
				saveChestData();
			}
		}
		is = playerInv.getChestplate();
		if (is != null) {
			if (confiscatable(player, is)) {
				playerInv.setChestplate(delete);
				moveToChest(player, is);
				saveChestData();
			}
		}
		is = playerInv.getLeggings();
		if (is != null) {
			if (confiscatable(player, is)) {
				playerInv.setLeggings(delete);
				moveToChest(player, is);
				saveChestData();
			}
		}
		is = playerInv.getBoots();
		if (is != null) {
			if (confiscatable(player, is)) {
				playerInv.setBoots(delete);
				moveToChest(player, is);
				saveChestData();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public boolean confiscatable(Player player, ItemStack item) {
		
		File dataFile = new File("ConfiscationItems.yml");
		FileConfiguration list = YamlConfiguration.loadConfiguration(dataFile);
		
		ItemMeta im = item.getItemMeta();
		
		if (im != null) {
			if (im.getDisplayName() != null)
				if (im.getDisplayName().equals("WalkInPvP") || im.getDisplayName().equals("WalkInTeamsPvP") || im.getDisplayName().equals("Red Team WalkInTeamsPvP") || im.getDisplayName().equals("Blue Team WalkInTeamsPvP") || im.getDisplayName().equals("WalkInHockey"))
					return true;
			if (im.getLore() != null)
				if (im.getLore().get(0).equals(ChatColor.translateAlternateColorCodes('&', "&5&lmcMMO Ability Tool")))
					return false;
		
		}
		
		if (item.getTypeId() == 383) {
			if (player.getWorld().getName().equals("SkyBlock"))
				return false;
			if (item.getDurability() == (short) 100)
				return false;
		}
		
		if ((list.getList("confiscate.items").contains(item.getType().name()) 
				|| list.getList("confiscate.items").contains(item.getTypeId())) 
				&& !player.hasPermission("autoconfiscate.itemid." + item.getTypeId() + ".bypass")) {
			return true;
		}
		if (item.getMaxStackSize() < item.getAmount() && !item.getType().name().equals("POTION"))
			return true;
		if (!itemType(item).equals("other")) {
			if (checkEnchantments(player, item)) {
				return true;
			}
		} else {
			Map<Enchantment, Integer> enchantments = item.getEnchantments();
			if (enchantments.size() != 0)
				return true;
		}

		return false;
	}

	private static final Map<Integer, Integer> allEnchants = new HashMap<Integer, Integer>();
	static {
		allEnchants.put(0, 4);
		allEnchants.put(1, 4); // Boots
		allEnchants.put(2, 4);
		allEnchants.put(3, 4);
		allEnchants.put(4, 4);
		allEnchants.put(5, 3); // Helmet
		allEnchants.put(6, 1); // Helmet
		allEnchants.put(7, 3);
		allEnchants.put(34, 3);

		allEnchants.put(16, 5); // Axe
		allEnchants.put(17, 5); // Axe
		allEnchants.put(18, 5); // Axe
		allEnchants.put(19, 2);
		allEnchants.put(20, 2);
		allEnchants.put(21, 3);
		allEnchants.put(34, 3);

		allEnchants.put(32, 5);
		allEnchants.put(33, 1);
		allEnchants.put(34, 3);
		allEnchants.put(35, 3);

		allEnchants.put(34, 3);
		allEnchants.put(48, 5);
		allEnchants.put(49, 2);
		allEnchants.put(50, 1);
		allEnchants.put(51, 1);
	}

	public boolean checkEnchantments(Player player, ItemStack item){
		int maxEnchantAmount = -1;
		if (itemType(item).equals("armor"))
			maxEnchantAmount = 4;

		if (itemType(item).equals("weapon"))
			maxEnchantAmount = 4;

		if (itemType(item).equals("tool"))
			maxEnchantAmount = 3;
		
		if (getEnchantmentPermission(player, false) != -1) {
			maxEnchantAmount = getEnchantmentPermission(player, false);
		}

		if (item.getEnchantments().size() > maxEnchantAmount && maxEnchantAmount != -1 && !player.hasPermission("autoconfiscate.enchantmentnum.bypass")) {
			return true;
		}

		int maxEnchantNum = 0;
		Map<Enchantment, Integer> enchantments = item.getEnchantments();

		for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
			@SuppressWarnings("deprecation")
			int enchantmentNum = enchantment.getKey().getId();
			int enchantmentValue = enchantment.getValue();
			if (allEnchants.get(enchantmentNum) == enchantmentValue)
				maxEnchantNum++;
			if (allEnchants.get(enchantmentNum) < enchantmentValue)
				return true;
//			if (plugin.getConfig().getList("confiscate.disabled_enchantments") != null)
//				if (plugin.getConfig().getList("confiscate.disabled_enchantments").contains(enchantment.getKey().getName()))
//					return true;
		}

		int maxFullEnchant = -1;
		if (itemType(item).equals("armor"))
			maxFullEnchant = 3;

		if (itemType(item).equals("weapon"))
			maxFullEnchant = 3;

		if (itemType(item).equals("tool"))
			maxFullEnchant = -1;
		
		if (getEnchantmentPermission(player, true) != -1) {
			maxFullEnchant = getEnchantmentPermission(player, true);
		}
		
		if (maxEnchantNum > maxFullEnchant && maxFullEnchant != -1 && !player.hasPermission("autoconfiscate.enchantmentnum.bypass")) {
			return true;
		}
		
		if (hasIllegalEnchantments(item))
			return true;
		
		return false;
	}
	
	List<Integer> allArmor = Arrays.asList(0, 1, 3, 4, 7, 34);
	List<Integer> bootsExtras = Arrays.asList(2);
	List<Integer> helmetExtras = Arrays.asList(5, 6);
	List<Integer> sword = Arrays.asList(16, 17, 18, 19, 20, 21, 34);
	List<Integer> axe = Arrays.asList(16, 17, 18, 32, 33, 34, 35);
	List<Integer> bow = Arrays.asList(34, 48, 49, 50, 51);
	List<Integer> pickShovel = Arrays.asList(32, 33, 34, 35);
	List<Integer> shears = Arrays.asList(32, 33, 34);
	
	@SuppressWarnings("deprecation")
	public boolean hasIllegalEnchantments(ItemStack item) {
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		String name = item.getType().name();
		if (name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				int id = enchantment.getKey().getId();
				if (!allArmor.contains(id))
					if (!(name.contains("BOOTS") && bootsExtras.contains(id)))
						if (!(name.contains("HELMET") && helmetExtras.contains(id)))
							return true;
			}
		if (name.contains("SWORD"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (!sword.contains(enchantment.getKey().getId()))
					return true;
//				if (!(item.getType() == Material.GOLD_SWORD) 
//						&& enchantment.getKey().getId() == 16 && enchantment.getValue() == 5)
//					return true; //Sharpness V is not obtainable EXCEPT on gold swords
			}
		if (name.equals("BOW"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (!bow.contains(enchantment.getKey().getId()))
					return true;
			}
		if (name.contains("AXE") && !name.contains("PICK"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (!axe.contains(enchantment.getKey().getId()))
					return true;
//				if ((item.getType() == Material.STONE_AXE || item.getType() == Material.DIAMOND_AXE) 
//						&& enchantment.getKey().getId() == 32 && enchantment.getValue() == 5)
//					return true; //Efficiency V is not obtainable on stone or diamond tools
			}
		if (name.contains("PICK") || name.contains("SPADE"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (!pickShovel.contains(enchantment.getKey().getId()))
					return true;
//				if ((item.getType() == Material.STONE_SPADE || item.getType() == Material.DIAMOND_SPADE
//						|| item.getType() == Material.STONE_PICKAXE || item.getType() == Material.DIAMOND_PICKAXE) 
//						&& enchantment.getKey().getId() == 32 && enchantment.getValue() == 5)
//					return true; //Efficiency V is not obtainable on stone or diamond tools
			}
		if (name.equals("SHEARS"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (!shears.contains(enchantment.getKey().getId()))
					return true;
			}
		if (name.equals("FISHING_ROD") || name.contains("HOE") || name.equals("FLINT_AND_STEEL") || name.equals("CARROT_STICK"))
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
				if (enchantment.getKey().getId() != 33)
					return true;
			}
		return false;
	}

	public String itemType(ItemStack item){
		@SuppressWarnings("deprecation")
		int id = item.getTypeId();
		if (id == 267 ||
				id == 268 ||
				id == 272 ||
				id == 276 ||
				id == 261)
			return "weapon";
		if (id >= 298 && id <= 317)
			return "armor";
		if ((id >= 256 && id <= 258) || (id >= 269 && id <= 286) || (id >= 290 && id <= 2294))
			return "tool";
		return "other";
	}
	
	public int getEnchantmentPermission(Player player, boolean max) {
		if (max) {
			for (int i = 0; i < 25; i++) {
				if (player.hasPermission("autoconfiscate.maxfullenchantments." + i))
					return i;
			}
			return -1;
		} else {
			for (int i = 0; i < 25; i++) {
				if (player.hasPermission("autoconfiscate.maxenchantments." + i))
					return i;
			}
			return -1;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void moveToChest(Player player, ItemStack items){
		World world = Bukkit.getWorld(getChestData().getString("first_chest.location.world"));
		int x;
		int y;
		int z;
//		int z = getChestData().getInt("first_chest.location.z");
		
		Chest chest = null;
		
		if (playerExists(player.getName())) {
			x = getChestData().getInt(player.getName() + ".chest_location.x");
			String isY = getChestData().getString(player.getName() + ".chest_location.y");
			if (isY == null) {
				getChestData().set(player.getName() + ".chest_location.y", getChestData().getInt("first_chest.location.y"));
			}
			y = getChestData().getInt(player.getName() + ".chest_location.y");
			z = getChestData().getInt(player.getName() + ".chest_location.z");
		} else {
			int yLayerNum = yLayerNum();
			int newY = getChestData().getInt("first_chest.location.y") + (yLayerNum * 4);
			int newZ = getChestData().getInt("first_chest.location.z");
			
			int maxXDistance = 120;
//			if (maxXDistance <= 3)
//				maxXDistance = 3;
			
			int newX = getChestData().getInt("first_chest.location.x") + (getChestData().getInt("players_confiscated") - (yLayerNum * (maxXDistance / 3))) * 3;
			
			getChestData().set("players_confiscated", getChestData().getInt("players_confiscated") + 1);
			
			generateChest(newX, newY, newZ, true);
			plugin.getLogger().info("New chest created at: " + newX + ", " + newY + ", " + newZ);
			
			Location signLoc = new Location(world, newX, newY+1, newZ);
			
			blockSet(newX, newY+1, newZ, 68);
			Sign sign = (Sign) signLoc.getBlock().getState();
			sign.setRawData((byte)3);
			sign.update();
			
			sign.setLine(0, ChatColor.WHITE + "-=-=-=-=-=-=-");
			sign.setLine(1, ChatColor.DARK_RED + "CONFISCATED:");
			sign.setLine(2, player.getName());
			sign.setLine(3, ChatColor.WHITE + "-=-=-=-=-=-=-");
			sign.update();
			
			getChestData().set(player.getName() + ".chest_location.x", newX);
			getChestData().set(player.getName() + ".chest_location.y", newY); 
			getChestData().set(player.getName() + ".chest_location.z", newZ); 
			getChestData().set(player.getName() + ".chest_location.num", 1); 
			
			if (getChestData().getInt("chest_range") < 1) 
				getChestData().set("chest_range", 1);
			saveChestData();
			
			x = newX;
			y = newY;
			z = newZ;
		}
		
		Location chestLoc = new Location(world, x, y, z);
		try{
			chest = (Chest) chestLoc.getBlock().getState();
		} catch (Exception e) {
			plugin.getLogger().info("Couldn't find chest at: " + x + ", " + y + ", " + z + ". Regenerating...");
			blockSet(x, y, z, 54);
			blockSet(x+1, y, z, 54);
			chest = (Chest) chestLoc.getBlock().getState();
		}
		
		Inventory chestInv = chest.getInventory();
		if (chestInv.firstEmpty() == -1) {
			plugin.getLogger().info("Chest full, creating new chest");
			int origz = getChestData().getInt(player.getName() + ".chest_location.z");
			int chestNum = getChestData().getInt(player.getName() + ".chest_location.num");
			
			chestNum++;
			
			getChestData().set(player.getName() + ".chest_location.z", origz + 2);
			getChestData().set(player.getName() + ".chest_location.num", chestNum);
			
			if (getChestData().getInt("chest_range") < chestNum) 
				getChestData().set("chest_range", chestNum);
			saveChestData();
			
			x = getChestData().getInt(player.getName() + ".chest_location.x");
			z = getChestData().getInt(player.getName() + ".chest_location.z");
			generateChest(x, y, z, false);
			
			chestLoc = new Location(world, x, y, z);
			chest = (Chest) chestLoc.getBlock().getState();
			chestInv = chest.getInventory();
		}
		
		chestInv.addItem(items);
	}
	
	public void addToList(Player player, ItemStack items) {
		ArrayList<String> itemList = (ArrayList<String>) getChestData().getStringList(player.getName() + ".confiscated_items");
		String itemName = getItemName(items);
			try {
				itemList.add(itemName);
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to add: '" + itemName + "' to the chestdata.yml file");
			}
		getChestData().set(player.getName() + ".confiscated_items", itemList);
		saveChestData();
	}
	
	public void generateChest(int x, int y, int z, boolean starter){
//		int y = getChestData().getInt("first_chest.location.y");
		
		int backMat = 4;
		if (starter) {
			for (int x1 = x-1; x1 <= x+2; x1++)
				for (int y1 = y; y1 <= y+2; y1++)
					for (int z1 = z; z1 <= z+1; z1++)
						blockSet(x1, y1, z1, 0);
			
			for (int y1 = y; y1 <= y+2; y1++){
				for (int x1 = x-1; x1 <= x+2; x1++) {
					blockSet(x1, y1, z-1, backMat);
				}
			}
			blockSet(x, y+1, z, 0);
			blockSet(x+1, y+1, z, 50);
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
		
		for (int z1 = z-1; z1 <= z+1; z1++) {
			for (int x1 = x-1; x1 <= x+2; x1++) {
				blockSet(x1, y-1, z1, backMat);
			}
		}
		saveChestData();
		
		
	}
	
	@SuppressWarnings("deprecation")
	public void blockSet(int x, int y, int z, int id){
		Block block = Bukkit.getWorld(getChestData().getString("first_chest.location.world")).getBlockAt(x, y, z);
//		if (!block.getChunk().isLoaded())
//			block.getChunk().load();
		block.setTypeId(id);
	}
	
	public boolean playerExists(String player) {
		String node = getChestData().getString(player + ".chest_location.x");
		return (node != null);
	}
	
	
	
	public void reloadChestData() {
	    if (chestDataFile == null) {
	    chestDataFile = new File("ConfiscationLog.yml");
	    }
	    chestData = YamlConfiguration.loadConfiguration(chestDataFile);
	 
	    InputStream defConfigStream = plugin.getResource("ConfiscationLog.yml");
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
	
	public boolean isChestLogged(Chest chest) {
		reloadChestData();
		Location chestLoc = chest.getLocation();
		
		if (!chestLoc.getWorld().getName().equals(getChestData().getString("first_chest.location.world")))
			return false;
		
		int y = chestLoc.getBlockY();
		int minY = getChestData().getInt("first_chest.location.y");
		int maxY = minY + (yLayerNum() * 4);
		
		int x = chestLoc.getBlockX();
		int minX = getChestData().getInt("first_chest.location.x");
		int maxX = getChestData().getInt("players_confiscated") * 3 + minX - 2;
		
		int z = chestLoc.getBlockZ();
		int minZ = getChestData().getInt("first_chest.location.z");
		int maxZ = getChestData().getInt("chest_range") * 2 + minZ - 2;
		
//		plugin.getLogger().info("Found chest_range: " + getChestData().getInt("chest_range"));
//		plugin.getLogger().info("Found players_confiscated: " + getChestData().getInt("players_confiscated"));
//		plugin.getLogger().info("Found layer number: " + yLayerNum());
//		
//		plugin.getLogger().info("minX = " + minX + " x = " + x + " maxX = " + maxX);
//		plugin.getLogger().info("minZ = " + minZ + " z = " + z + " maxZ = " + maxZ);
//		plugin.getLogger().info("minY = " + minY + " y = " + y + " maxY = " + maxY);
		
		if (y < minY)
			return false;
		if (y > maxY)
			return false;
		
		if (z < minZ)
			return false;
		if (z > maxZ)
			return false;
		
		if (x < minX)
			return false;
		if (x > maxX)
			return false;
		
//		plugin.getLogger().info("Chest Logged");
		
		return true;
	}
	
	public String getItemName(ItemStack items) {
		String strEnchantments = "";
		Map<Enchantment, Integer> enchantments = items.getEnchantments();
		for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet())
			strEnchantments += enchantment.getKey().getName() + " " + enchantment.getValue() + "; ";
		String name = items.getAmount() + " " + items.getType().name();
		if (items.getDurability() != 0)
			name += ":"+ items.getDurability();
		if (strEnchantments != "")
			name += "; " + strEnchantments;
		return name;
	}
	
	public int yLayerNum() {
//		if (120 >= 3) {
		return (int) (getChestData().getInt("players_confiscated") / (120 / 3));
//		} else {
//			if (plugin.getConfig().getInt("chest_room.max_x_distance") == 0) {
//				return 0;
//			} else {
//				return getChestData().getInt("players_confiscated") - 1;
//			}
//		}
	}
	
	@EventHandler
	public void scanPlayerInventory(InventoryCloseEvent event) {
		scanInventory((Player) event.getPlayer(), event.getPlayer().getInventory());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		scanInventory((Player) event.getPlayer(), event.getPlayer().getInventory());
	}

	@EventHandler
	public void scanChestContents(PlayerInteractEvent event) {
		Block b = event.getClickedBlock();
		if (b != null) {
			BlockState bs = b.getState();

			if (bs instanceof Chest) {
				Chest c = (Chest) bs;
				if (!isChestLogged(c)) {
					scanInventory(event.getPlayer(), c.getBlockInventory());

					BlockState bsNorth = b.getRelative(BlockFace.NORTH).getState();
					BlockState bsEast = b.getRelative(BlockFace.EAST).getState();
					BlockState bsSouth = b.getRelative(BlockFace.SOUTH).getState();
					BlockState bsWest = b.getRelative(BlockFace.WEST).getState();
					
					if (bsNorth instanceof Chest) {
						c = (Chest) bsNorth;
						scanInventory(event.getPlayer(), c.getBlockInventory());
					} else if (bsEast instanceof Chest) {
						c = (Chest) bsEast;
						scanInventory(event.getPlayer(), c.getBlockInventory());
					} else if (bsSouth instanceof Chest) {
						c = (Chest) bsSouth;
						scanInventory(event.getPlayer(), c.getBlockInventory());
					} else if (bsWest instanceof Chest) {
						c = (Chest) bsWest;
						scanInventory(event.getPlayer(), c.getBlockInventory());
					}
				}
			}
			if (bs instanceof BrewingStand) {
				BrewingStand bstand = (BrewingStand) bs;
				scanInventory(event.getPlayer(), bstand.getInventory());
			}
			if (bs instanceof Dispenser) {
				Dispenser d = (Dispenser) bs;
				scanInventory(event.getPlayer(), d.getInventory());
			}
			if (bs instanceof Furnace) {
				Furnace f = (Furnace) bs;
				scanInventory(event.getPlayer(), f.getInventory());
			}
			if (b.getType() == Material.ENDER_CHEST) {
				scanInventory(event.getPlayer(), event.getPlayer()
						.getEnderChest());
			}
		}
	}
	
	@EventHandler
	public void scanMinecart(PlayerInteractEntityEvent event) {
		Entity mc = event.getRightClicked();
		if (mc instanceof StorageMinecart){
			scanInventory(event.getPlayer(), ((StorageMinecart) mc).getInventory());
		}
	}
	
	
	public void log(String info) {
		plugin.getLogger().info("<AutoConfiscate> " + info);
	}
}
