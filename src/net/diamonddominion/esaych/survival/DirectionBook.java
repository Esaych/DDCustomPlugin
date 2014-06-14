package net.diamonddominion.esaych.survival;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;


public class DirectionBook {
	private CustomPlugin plugin;
	
	public DirectionBook(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	ItemStack sBook = new ItemStack(Material.WRITTEN_BOOK);
	ItemStack shovel = new ItemStack(Material.GOLD_SPADE);
	ItemStack stick = new ItemStack(Material.STICK);
	World griefFree;
	Location loc;
	Location adjLoc;
	Entity droppedBook = null;
	Entity droppedStick = null;
	Entity droppedShovel = null;
	ArrayList<Player> recieved = new ArrayList<Player>();
	
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override  
			public void run() {
				spawnBook(true);
			}
		}, 20 * 6, 20 * 10);
		writeBooks();
		log("Enabled");
	}
	
	public void writeBooks() {
		BookMeta bm = (BookMeta)book.getItemMeta();
		bm.setAuthor("DD Staff");
		bm.setTitle("How to claim land (Creative)");
		ArrayList<String> pages = new ArrayList<String>();
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&1&lStep 1:\n&0Find an open area to build, or type '/plot auto' to find an open plot\n\n" + 
				"&1&lStep 2:\n&0Simply step in the square, type '/plot claim' and its yours."
			));
//		pages.add(ChatColor.translateAlternateColorCodes('&', 
//				"&1&lStep 3:\n&0Right click one corner of the location you want to build at. This will be a corner of the rectangular area you are claiming for yourself. A diamond block should show up, making it seen that you have selected that spot."
//			));
//		pages.add(ChatColor.translateAlternateColorCodes('&', 
//				"&1&lStep 4:\n&0Quickly (without removing the shovel from your hand) go to the other corner of the area you want to build in and right click the ground there. \nA rectangle of Gold and Glowstone should appear, this is your claim."
//			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/plot claim'\n&0Claims your the plot you are in.\n\n" +
				"&8'/plot auto'\n&0Claims next available plot.\n\n" +
				"&8'/plot home[:#]'\n&0Get back to a plot.\n\n"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/plot clear'\n&0RESETS the claim\n\n" +
				"&8'/plot add <player>'\n&0Lets another player build. &oSlightly glitchy\n\n" +
				"&8'/plot remove <player>'\n&0Disallows another player to build.\n\n"
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/plot dispose'\n&0RESETS and DELETES the claim\n\n" +
				"&8'/plot deny <player>'\n&0Denies player to enter your plot\n\n" +
				"&8'/plot undeny <player>'\n&0Allows player to enter your plot\n\n"
				));
		bm.setPages(pages);
		book.setItemMeta(bm);
		
		bm = (BookMeta)sBook.getItemMeta();
		bm.setAuthor("DD Staff");
		bm.setTitle("How to use Towny");
		pages = new ArrayList<String>();
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&11. &0Travel to a location that is not inhabited yet.\n" +
				"&12. &0Right click with the stick to see any claims.\n" +
				"&13. &0Resize a claim by clicking the corners with the gold shovel.\n\n" +
				"&14. &8You can claim land manually with the steps on the next page:"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&1&lStep 1:\n&0Determine your house size so you can get around to the corners of your property.\n\n" + 
				"&1&lStep 2:\n&0Take out the Golden Shovel, this is your claiming tool."
			));
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&1&lStep 3:\n&0Right click one corner of the property you want to claim. This will be a corner of the rectangular area you are claiming for yourself. A diamond + should show up, making it seen that you have selected that spot."
			));
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&1&lStep 4:\n&0Quickly (without removing the shovel from your hand) go to the other corner of the property and right click the ground there. \nA rectangle of Gold and Glowstone should appear, this is your claim."
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/abandonclaim'\n&0Deletes the claim you're standing in\n\n" +
				"&8'/trust <player>'\n&0Let other people build in your place\n\n" +
				"&8'/untrust <player>'\n&0Unallow trusted people from building in your claim\n\n"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/accesstrust <player>'\n&0Lets a player use your buttons, levers, and beds\n\n" +
				"&8'/containertrust <player>'\n&0Lets a player use your buttons, levers, beds, crafting gear, containers, and animals.\n\n"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lClaiming Commands:\n" + 
				"&8'/buyclaimblocks <#>'\n&0Converts server money to more claim blocks\n\n" +
				"&8'/sellclaimblocks <#>'\n&0Converts claim blocks to server money\n\n" +
				"&1If you want a visual demonstration, ask how to claim, &oa video link will appear."
				));
		bm.setPages(pages);
		sBook.setItemMeta(bm);
	}
	
//	@EventHandler
//	public void onPlayerOpenChest(PlayerInteractEvent event) {
//		Block chest = event.getClickedBlock();
//		if (chest != null && chest.getType().equals(Material.CHEST) && chest.getLocation().equals(loc)) {
//			((Chest) chest.getState()).getBlockInventory().clear();
//			((Chest) chest.getState()).getBlockInventory().addItem(book);
//		}
//	}
	
//	@EventHandler
//    public void StepOnPressurePlate(PlayerInteractEvent event){
//		if (event.getAction().equals(Action.PHYSICAL)){
//			Block plate = event.getClickedBlock();
//			if (plate.getTypeId() == 72 && plate.getLocation().equals(loc)){
//				PlayerInventory playerInv = event.getPlayer().getInventory();
//				playerInv.addItem(book);
//				if (!playerInv.contains(Material.GOLD_SPADE))
//					playerInv.addItem(shovel);
//				if (!playerInv.contains(Material.STICK))
//					playerInv.addItem(stick);
//			}
//		}
//	}
	
	public void spawnBook(final boolean check) {
		if (loc == null || griefFree == null) {
			griefFree = Bukkit.getWorld("TownWorld");
			loc = new Location(griefFree, 163, 77, 303);
			adjLoc = loc.clone().add(.5,.5,.5);
		} else {
			try {
				if (loc.getChunk() != null) {
					if (!loc.getChunk().isLoaded())
						loc.getChunk().load();
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override 
						public void run() {
							if (check && checkExisting()) {
								return;
							}
							try {
								loc.getBlock().setType(Material.WEB);
								griefFree.createExplosion(adjLoc.getX(), adjLoc.getY(), adjLoc.getZ(), (float) 3, false, false);
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									@Override 
									public void run() {
										try {
											droppedBook = griefFree.dropItem(adjLoc, sBook);
											droppedShovel = griefFree.dropItem(adjLoc, shovel);
											droppedStick = griefFree.dropItem(adjLoc, stick);
										} catch (Exception e) {
											log("(2) Spawning the items failed...");
										}
									}
								}, 1);
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									@Override 
									public void run() {
										try {
											loc.getBlock().setType(Material.AIR);
										} catch (Exception e) {
											log("(3) Spawning the items failed...");
										}
									}
								}, 2);
							} catch (Exception e) {
								log("(1) Spawning the items failed...");
							}
						}
					}, 20 * 5);
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public boolean checkExisting() {
		try {
			for (Entity thing : loc.getChunk().getEntities()) {
				if (thing instanceof Item) {
					if (thing.getLocation().distance(loc) < 2 && ((Item) thing).getItemStack().getType() == Material.WRITTEN_BOOK) {
//						log("Found items. No need to respawn");
						return true;
					}
				}
			}
		} catch (Exception e) {
			log("Couldn't find the items before spawning!");
		}
		return false;
	}
	
//	@EventHandler
	public void onPlayerPickupBook(PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		if (player.getWorld().equals(griefFree)) {
			Item drop = event.getItem();
			if (drop.equals(droppedBook) || drop.equals(droppedShovel) || drop.equals(droppedStick)) {
//				PlayerInventory playerInv = player.getInventory();
				if (!recieved.contains(player)) {
					if (drop.equals(droppedBook)) {
						spawnBook(false);
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override 
							public void run() {
								recieved.add(player);
							}
						}, 2);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
//	@EventHandler
	public void onDropsDespawn(ItemDespawnEvent event) {
		Entity item = event.getEntity();
		if (item.equals(droppedBook) || item.equals(droppedShovel) || item.equals(droppedStick)) {
			event.setCancelled(true);
			log("Renewing item");
		}
	}
	
//	@EventHandler
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(sBook)) {
			event.setCancelled(true);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					if (event.getPlayer().getInventory().getItemInHand().equals(sBook))
					event.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR));
				}
			}, 1);
		}
	}
	
//	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (player.getWorld().getName().equals("Build")) {
			Inventory playerInv = player.getInventory();
			if (!playerInv.contains(book))
				playerInv.addItem(book);
		}
		if (player.getWorld() == griefFree) {
			spawnBook(true);
//			player.getWorld().spawnEntity(player.getLocation().add(0, 5, 0), EntityType.FIREWORK);
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<DirectionBook> " + info);
	}
}
