package net.diamonddominion.esaych.survival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class DemiGodPackage implements Listener {
	private CustomPlugin plugin;
	private BlockLog bl = new BlockLog(plugin);
	private ArrayList<Material> disallowedBlocks = new ArrayList<Material>();
	private ArrayList<Material> unclickableBlocks = new ArrayList<Material>();
	private ArrayList<Material> unbreakableBlocks = new ArrayList<Material>();
	private ArrayList<String> disallowedCommands = new ArrayList<String>();
	
//	private Map<TNTPrimed, String> godTNT = new HashMap<TNTPrimed, String>();
//	private Map<Block, String> explodedBlocks = new HashMap<Block, String>();
//	private Map<Block, String> explodedBlockData = new HashMap<Block, String>();
	
	public DemiGodPackage(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		populateDisallowedBlocks(); // Blocks you can't take out of inv
		populateUnclickableBlocks();// Blocks you can't open/right click
		populateUnbreakableBocks(); // Blocks you can't break
		populateDisallowedCommands();//Commands you can't type
//		startTntWatch();
		log("Enabled");
	}
	
	public void disable() {
		bl.saveLog();
//		if (explodedBlocks.size() > 0) {
//			Set<Block> set = explodedBlocks.keySet();
//			for (Block b : set) { //Loop through all logged exploded blocks
//				b.setTypeId(Integer.parseInt(explodedBlockData.get(b).split(";")[0]));
//				b.setData(Byte.parseByte(explodedBlockData.get(b).split(";")[1]));
//				if (b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
//					Sign sign = (Sign) b.getState();
//					sign.setLine(0, explodedBlockData.get(b).split(";")[2]);
//					sign.setLine(1, explodedBlockData.get(b).split(";")[3]);
//					sign.setLine(2, explodedBlockData.get(b).split(";")[4]);
//					sign.setLine(3, explodedBlockData.get(b).split(";")[5]);
//				}
//			}
//			explodedBlockData.clear();
//			explodedBlocks.clear();
//		}
	}

	public boolean creativeCommand(CommandSender sender, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save"))
				return onReloadCommand(sender, args);
		}
		if (!(sender instanceof Player)) {
			msg(sender, "You may NOT get creative mode. :P");
			return true;
		}
		Player p = (Player) sender;
		if (p.getWorld().getName().equals("Build") || p.getWorld().getName().equals("Destruction")) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				msg(p, "You are already in creative mode");
				return true;
			}
			p.setGameMode(GameMode.CREATIVE);
			return true;
		}
		
		
		if (!p.hasPermission("customplugin.creative")) {
			msg(p, "You must purchase the DemiGod class for this command.");
			return true;
		}
		if (p.getGameMode() == GameMode.CREATIVE) {
			msg(p, "You are already in Creative Mode! Try /survival");
			return true;
		}
		int a = 0;
		for (ItemStack i : p.getInventory()) {
			if (i != null)
				a++;
		}
		boolean helm = (p.getInventory().getHelmet() != null);
		boolean ches = (p.getInventory().getChestplate() != null);
		boolean legg = (p.getInventory().getLeggings() != null);
		boolean boot = (p.getInventory().getBoots() != null);
		if (a > 0 || helm || ches || legg || boot) {
			msg(p, "You must clear your inventory completely to switch to creative mode.");
			return true;
		}
		p.setGameMode(GameMode.CREATIVE);

		return true;
	}

	public boolean survivalCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			msg(sender, "Trying to troll?? NOT WORKING.");
			return true;
		}
		Player p = (Player) sender;

		if (p.getWorld().getName().equals("Build") || p.getWorld().getName().equals("Destruction")) {
			if (p.getGameMode().equals(GameMode.SURVIVAL)) {
				msg(p, "You are already in survival mode");
				return true;
			}
			p.setGameMode(GameMode.SURVIVAL);
			return true;
		}
		
		if (!p.hasPermission("customplugin.creative")) {
			msg(p, "You must purchase the DemiGod class for this command.");
			return true;
		}
		if (p.getGameMode() == GameMode.SURVIVAL) {
			msg(p, "You are already in Survival Mode! Try /creative");
			return true;
		}
		clearInv(p);

		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void clearInv(Player p) { 
		p.getInventory().clear();
		p.getInventory().setHelmet(new ItemStack(0));
		p.getInventory().setChestplate(new ItemStack(0));
		p.getInventory().setLeggings(new ItemStack(0));
		p.getInventory().setBoots(new ItemStack(0));
		p.setGameMode(GameMode.SURVIVAL);
	}
	
	public boolean onReloadCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (sender.isOp()) {
				bl.saveLog();
				msg(sender, "Creative BlockLog saved.");
			} else {
				msg(sender, "You do not have permission.");
			}
		} else {
			msg(sender, "Creative BlockLog saved.");
		}
		return true;
	}
	
	private boolean res(Player player) {
//		return player.getGameMode() == GameMode.CREATIVE && !player.isOp();
		return player.getGameMode() == GameMode.CREATIVE && (player.hasPermission("customplugin.creative.restrict") || (plugin.voteRewards.rewardCache.containsKey(player.getName()) && plugin.voteRewards.rewardCache.get(player.getName()).containsKey(3)));
//		return player.getGameMode() == GameMode.CREATIVE && player.hasPermission("customplugin.creative") && !player.isOp();
	}
	
	//================================================================================================================
	
	private void populateDisallowedBlocks() {
		disallowedBlocks.add(Material.BEDROCK);
		disallowedBlocks.add(Material.MONSTER_EGG);
		disallowedBlocks.add(Material.MONSTER_EGGS);
		disallowedBlocks.add(Material.ENCHANTED_BOOK);
		disallowedBlocks.add(Material.COMMAND);
		disallowedBlocks.add(Material.ENDER_PORTAL_FRAME);
		disallowedBlocks.add(Material.EYE_OF_ENDER);
		disallowedBlocks.add(Material.POTION);
		disallowedBlocks.add(Material.ITEM_FRAME);
		disallowedBlocks.add(Material.MINECART);
		disallowedBlocks.add(Material.SADDLE);
		disallowedBlocks.add(Material.BOAT);
		disallowedBlocks.add(Material.STORAGE_MINECART);
		disallowedBlocks.add(Material.POWERED_MINECART);
		disallowedBlocks.add(Material.FISHING_ROD);
		disallowedBlocks.add(Material.EXPLOSIVE_MINECART);
		disallowedBlocks.add(Material.HOPPER_MINECART);
		disallowedBlocks.add(Material.EXP_BOTTLE);
		disallowedBlocks.add(Material.LAVA_BUCKET);
		disallowedBlocks.add(Material.EGG);
		disallowedBlocks.add(Material.TNT);
	}
	
	private void populateUnclickableBlocks() { 
		unclickableBlocks.add(Material.CHEST);
		unclickableBlocks.add(Material.HOPPER);
		unclickableBlocks.add(Material.DISPENSER);
		unclickableBlocks.add(Material.DROPPER);
		unclickableBlocks.add(Material.FURNACE);
		unclickableBlocks.add(Material.BREWING_STAND);
		unclickableBlocks.add(Material.TRAPPED_CHEST);
		unclickableBlocks.add(Material.ENDER_CHEST);
		unclickableBlocks.add(Material.TNT);
	}
	
	private void populateUnbreakableBocks() {
		unbreakableBlocks.add(Material.BEDROCK);
	}
	
	private void populateDisallowedCommands() {
		disallowedCommands.add("/enderchest");
		disallowedCommands.add("/echest");
		disallowedCommands.add("/vc");
		disallowedCommands.add("/vchest");
		disallowedCommands.add("/virtualchest");
		disallowedCommands.add("/kit");
		disallowedCommands.add("/ekit");
	}
	
	//===============================================================================================================

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (res(event.getPlayer())) {
			msg(event.getPlayer(), "You may not drop items in creative mode!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (res(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (res(player)) {
				event.getDrops().clear();
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (res(event.getPlayer())) {
			bl.write(event.getBlock(), event.getPlayer());
		}
	}
	
	@EventHandler 
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		WorldGuardPlugin wg = plugin.getWorldGuard();
		boolean canBuild;
		if (wg == null) {
			canBuild = true;
		} else {
			canBuild = wg.canBuild(event.getPlayer(), event.getBlock());
		}
		if (bl.isLogged(event.getBlock()) && canBuild) {
			if (!res(event.getPlayer())) {
				msg(event.getPlayer(), "A creative mode player placed this block.");
			}
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
			bl.remove(event.getBlock());
		}
	}
	
	@EventHandler
	public void onBlockPistonEvent(BlockPistonExtendEvent event) {
		for (Block b : event.getBlocks()) { 
			if (bl.isLogged(b)) {
				event.setCancelled(true);
//				animateBlock(b);
			}
		}
	}
	
	@EventHandler
	public void onBlockPistonContract(BlockPistonRetractEvent event) {
		if (event.getBlock().getType().equals(Material.PISTON_STICKY_BASE)) {
			if (!event.getBlock().getType().equals(Material.AIR))
				if (bl.isLogged(event.getBlock())) {
					event.setCancelled(true);
				}
		}
	}
	
//	private void animateBlock(final Block b) {
//		final Material type = b.getType();
//		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//			@Override 
//			public void run() {
//				b.setType(Material.REDSTONE_BLOCK);
//			}
//		}, 5);
//		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//			@Override 
//			public void run() {
//				b.setType(type);
//			}
//		}, 10);
//	}
	
//	@EventHandler
//	public void onBlocksFall(BlockPhysicsEvent event) {
////		if ((event.getBlock().getType().equals(Material.SAND) || event.getBlock().getType().equals(Material.GRAVEL)) && bl.isLogged(event.getBlock())) {
////			event.getBlock().setType(Material.AIR);
////			event.setCancelled(true);
//			bl.remove(event.getBlock());
////		}
//	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClick(InventoryClickEvent event) {
		if (res((Player) event.getWhoClicked())) {
//			log("Cursor: " + event.getCursor().getType().name());
//			log("CurrentItem: " + event.getCurrentItem().getType().name());
			if (event.getCurrentItem() != null) {
				if (disallowedBlocks.contains(event.getCurrentItem().getType())) {
					event.setCurrentItem(new ItemStack(Material.AIR));
					event.setCancelled(true);
				}
			}
			if (event.getCursor() != null) {
				if (disallowedBlocks.contains(event.getCursor().getType())) {
					event.setCancelled(true);
				}
			}
		}
		if (event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getType().equals(Material.MONSTER_EGG)) {
					event.setCurrentItem(new ItemStack(Material.AIR));
					event.setCancelled(true);
				}
			}
			if (event.getCursor() != null) {
				if (event.getCursor().getType().equals(Material.MONSTER_EGG)) {
					event.setCursor(new ItemStack(Material.AIR));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (res(event.getPlayer())) {
			Block cB = event.getClickedBlock();
			if (cB != null) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (unclickableBlocks.contains(cB.getType())) {
						event.setCancelled(true);
						msg(event.getPlayer(), "You may not use this block.");
					}
					if (cB.getType().equals(Material.WALL_SIGN) || cB.getType().equals(Material.SIGN_POST)) {
						final Sign sign = (Sign) cB.getState();
						if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Buy]")) {
							sign.setLine(0, "");
							sign.update();
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override 
								public void run() {
									sign.setLine(0, ChatColor.DARK_BLUE + "[Buy]");
									sign.update();
								}
							}, 1);
						}
						if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Sell]")) {
							sign.setLine(0, "");
							sign.update();
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override 
								public void run() {
									sign.setLine(0, ChatColor.DARK_BLUE + "[Sell]");
									sign.update();
								}
							}, 1);
						}
					}
				} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (unbreakableBlocks.contains(cB.getType())) {
						event.setCancelled(true);
						msg(event.getPlayer(), "You may not break this block.");
					}
				}
			}
		}

		if (event.isCancelled())
			return;
//		if (!event.getClickedBlock().getType().equals(Material.TNT))
//			return;
//		if (bl.isLogged(event.getClickedBlock())) {
//        	final Block tnt = event.getClickedBlock();
//        	final Player player = event.getPlayer();
//        	if (tnt.getWorld().getName().equals("Survival") || tnt.getWorld().getName().equals("PeaceWorld"))
//        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//				@Override 
//				public void run() {
//					if (tnt.getType() != Material.AIR)
//						return;
//					if (player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.FLINT_AND_STEEL)) {
//						log("LIT!");
//						bl.remove(tnt);
//						for (Entity possibleTNT : tnt.getWorld().getEntities()) {
//							if (possibleTNT instanceof TNTPrimed) {
//								TNTPrimed tntEnt = (TNTPrimed) possibleTNT;
//								log(tntEnt.getSource().toString());
//								if (tntEnt.getSource().equals(player)) {
//									godTNT.put(tntEnt, player.getName());
//									log(player.getName() + " just lit tnt at " + tntEnt.getLocation().toString());
//								}
//							}
//						}
//					}
//				}
//			}, 1);
//        }
	}
	
	@EventHandler
	public void onPlayerSendCommand(PlayerCommandPreprocessEvent event) {
		if (res(event.getPlayer())) {
			String command = event.getMessage().split(" ")[0];
			if (disallowedCommands.contains(command)) {
				event.setCancelled(true);
				msg(event.getPlayer(), "You may not type that command.");
			}
		}
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		if (res(event.getPlayer())) {
			clearInv(event.getPlayer());
		}
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onPlayerQuitGame(PlayerQuitEvent event) {
		if (res(event.getPlayer())) {
			clearInv(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerPunch(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && res((Player) event.getDamager())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerTouchEntity(PlayerInteractEntityEvent event) {
		if (res(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
//	@EventHandler
//	public void onTNTPrimeEvent (ExplosionPrimeEvent event) {
//		log("PRIMED");
//	}
	
//	@EventHandler
//	public void onRedstoneEvent (BlockRedstoneEvent event) {
//		final Block tnt = event.getBlock();
//		if (tnt.getType().equals(Material.TNT)) {
//			if (bl.isLogged(tnt)) {
//				log("TNT CURRENT: " + event.getNewCurrent());
//				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//					@Override 
//					public void run() {
//						if (tnt.getTypeId() != 0)
//							return;
//						log("LIT!");
//						bl.remove(tnt);
//						for (Entity possibleTNT : tnt.getWorld().getEntities()) {
//							if (possibleTNT instanceof TNTPrimed) {
//								TNTPrimed tntEnt = (TNTPrimed) possibleTNT;
//								log(tntEnt.getSource().toString());
//								godTNT.put(tntEnt, "REDSTONE");
//								log("REDSTONE just lit tnt at " + tntEnt.getLocation().toString());
//							}
//						}
//					}
//				}, 1);
//			}
//		}
//	}

//	@SuppressWarnings("deprecation")
//	@EventHandler
//	public void onExplodeEvent(EntityExplodeEvent event) {
//		log("EXPLODE");
//		
//		if (event.isCancelled())
//			return;
//		for (Block block : event.blockList()) {
//			if (bl.isLogged(block)) {
//				block.setType(Material.AIR);
//				bl.remove(block);
//			}
//			if (block.getType().equals(Material.TNT)) {
//				block.setType(Material.AIR);
//			}
//		}
//		if (event.getEntity() instanceof TNTPrimed) {
//			if (godTNT.keySet().contains(event.getEntity())) {
//				//			for (Block b : event.blockList()) {
//				//				if (b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
//				//					explodedBlocks.put(b, godTNT.get(event.getEntity()));
//				//					Sign sign = (Sign) b.getState();
//				//					String signData = ";" + sign.getLine(0) + ";" + sign.getLine(1) + ";" + sign.getLine(2) + ";" + sign.getLine(3);
//				//					explodedBlockData.put(b, b.getTypeId() + ";" + b.getData() + signData);
//				//					b.setTypeId(0);
//				//				}
//				//			}
//				for (Block b : event.blockList()) {
//					//				if (!(b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST))) {
//					explodedBlocks.put(b, godTNT.get(event.getEntity()));
//					explodedBlockData.put(b, b.getTypeId() + ";" + b.getData());
//					b.setType(Material.AIR);
//					//				}
//				}
//				godTNT.remove(event.getEntity());
//			}
//		}
//	}
	
//	private void startTntWatch() {
//		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
//			@SuppressWarnings("deprecation")
//			@Override 
//			public void run() {
//				if (explodedBlocks.size() > 0) {
//					ArrayList<String> alreadyGened = new ArrayList<String>(); //This allows for several people to regen at the same time
//					ArrayList<Block> toRemove = new ArrayList<Block>();
//					Set<Block> set = explodedBlocks.keySet();
//					for (Block b : set) { //Loop through all logged exploded blocks
//						String playerName = explodedBlocks.get(b);
//						if (!alreadyGened.contains(playerName)) { //If the player hasn't already been covered
//							b = getLowestBlockPerPlayer(playerName);
////							int typeID = Integer.parseInt(explodedBlockData.get(b).split(";")[0]);
////							if (typeID == 63 || typeID == 68) {
////								if (typeID == 68) {
////									for (Block wallBlock : getPlayerBlocksExploded(playerName)) {
////										if (wallBlock.getLocation().distance(b.getLocation()) == 1) {
////											b = wallBlock;
////											typeID = Integer.parseInt(explodedBlockData.get(b).split(";")[0]);
////											if (typeID == 68) {
////												continue;
////											}
////										}
////									}
////								}
////								if (typeID == 68 || typeID == 68) {
////									Sign sign = (Sign) b.getState();
////									sign.setLine(0, explodedBlockData.get(b).split(";")[2]);
////									sign.setLine(1, explodedBlockData.get(b).split(";")[3]);
////									sign.setLine(2, explodedBlockData.get(b).split(";")[4]);
////									sign.setLine(3, explodedBlockData.get(b).split(";")[5]);
////									sign.update();
////								}
////							}
//							b.setTypeId(Integer.parseInt(explodedBlockData.get(b).split(";")[0]));
//							b.setData(Byte.parseByte(explodedBlockData.get(b).split(";")[1]));
//							alreadyGened.add(playerName);
//							explodedBlockData.remove(b);
//							toRemove.add(b);
//						}
//					}
//					for (Block b : toRemove) {
//						explodedBlocks.remove(b);
//					}
//				}
//			}
//		}, 20 * 10, 1);
//	}
	
//	private ArrayList<Block> getPlayerBlocksExploded(String p) {
//		ArrayList<Block> data = new ArrayList<Block>();
//		for (Block b : explodedBlocks.keySet()) {
//			if (explodedBlocks.get(b).equals(p)) {
//				data.add(b);
//			}
//		}
//		return data;
//	}
	
//	private Block getLowestBlockPerPlayer (String p) {
//		int lowest = 300;
//		Block block = null;
//		for (Block b : getPlayerBlocksExploded(p)) {
//			if (b.getLocation().getBlockY() < lowest) {
//				lowest = b.getLocation().getBlockY();
//				block = b;
//			}
//		}
//		return block;
//	}
	
	private void msg(CommandSender sender, String msg) {
		if (sender instanceof Player) {
			((Player) sender).sendMessage(ChatColor.DARK_RED + "["
					+ ChatColor.GOLD + "CREATIVE" + ChatColor.DARK_RED + "] "
					+ ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			log(msg);
		}
	}

	private void log(String info) {
		plugin.getLogger().info("<CreativeMode> " + info);
	}
}


class BlockLog {

	private File blockLogFile;
	private FileConfiguration blockLog;
	private Plugin plugin;
	
	public BlockLog(Plugin plugin)
	{
		this.plugin = plugin;
		blockLogFile = new File("CreativeBlockLog.yml");
		blockLog = YamlConfiguration.loadConfiguration(blockLogFile);
	}
	
	public List<String> getList() {
		@SuppressWarnings("unchecked")
		List<String> blocks = (List<String>) blockLog.getList("blocks");
		if (blocks == null) {
			blocks = new ArrayList<String>();
		}
		return blocks;
	}
	
	public void write(Block block, Player player) {
		List<String> blocks = getList();
		blocks.add(getData(block, player));
		blockLog.set("blocks", blocks);
	}
	
	public void remove(Block block) {
		List<String> blocks = getList();
		List<String> newBlocks = new ArrayList<String>();
		for (String log : blocks) {
			if (!log.contains(getTitle(block)))
				newBlocks.add(log);
		}
		blockLog.set("blocks", newBlocks);
	}
	
	public boolean isLogged(Block block) {
		for (String s : getList()) {
			if (s.contains(getTitle(block))) {
				return true;
			}
		}
		return false;
	}
	
	public void saveLog() {
		if (blockLog == null || blockLogFile == null) {
			plugin.getLogger().severe("Could not save config to " + blockLogFile);
			return;
		}
		try {
			blockLog.save(blockLogFile);
		} catch (IOException ex) {
			plugin.getLogger().severe("Could not save config to " + blockLogFile);
		}
	}
	
	private String getTitle(Block b) {
		return b.getWorld().getName() + ";" + b.getX() + ";" + b.getY() + ";" + b.getZ();
	}
	
	private String getData(Block b, Player p) {
		return getTitle(b) + ";" + b.getType().name() + ";" + p.getName();
	}
}