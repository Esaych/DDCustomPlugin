package net.diamonddominion.esaych.survival;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.ps.PS;


@SuppressWarnings("deprecation")
public class LegitWarzone {
	
	private CustomPlugin plugin;

	private Map<Player, Integer> cooldowns = new HashMap<Player, Integer>();
	
	public LegitWarzone(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	Map<String, Boolean> legit = new HashMap();
	
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		log("Enabled");
	}
	
//	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoves(PlayerMoveEvent event) {	
			Player player = event.getPlayer();

			if (locationInWarzone(player.getLocation())) {
				if (!player.hasPermission("legitwarzone.bypass"))
					makeLegit(player);
				if (isNotProtected(player)) {
					selfDistruct(player);
				} else {
					if (player.getName().equals("Esaych")) {
						ItemStack i = new ItemStack(Material.DIAMOND_SWORD);
						i.addEnchantment(Enchantment.FIRE_ASPECT, 1);
						i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
						ItemStack stick = new ItemStack(Material.STICK);
						stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 20);
						if (!player.getInventory().contains(Material.DIAMOND_SWORD))
							player.getInventory().addItem(i);
						if (!player.getInventory().contains(Material.STICK))
							player.getInventory().addItem(stick);
					}
					if (selfDestructing.contains(player)) {
						selfDestructing.remove(player);
						msg(player, "Your self destruct has been canceled");
					}
				}
			} else {
				if (selfDestructing.contains(player)) {
					selfDestructing.remove(player);
					msg(player, "Your self destruct has been canceled");
				}
			}
	}
	
//	@EventHandler
//	public void somebodyMentionsWither(PlayerChatEvent event) {
//		if (event.getMessage().toLowerCase().contains("wither")) {
//			checkWither();
//		}
//	}
	
//	@EventHandler
//	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//		if (event.getDamager() instanceof WitherSkull || event.getEntity() instanceof WitherSkull) {
//			if (checkWither())
//				event.setCancelled(true);
//		}
//		if (event.getDamager() instanceof Player) {
//			if ((Player) event.getDamager())
//		}
//	}
	
//	@EventHandler
	public void onSplashPotionThrown(PotionSplashEvent event) {
		ThrownPotion potion = event.getPotion();
		PotionEffect pShort = new PotionEffect(PotionEffectType.INVISIBILITY, 2701, 0, true);
		PotionEffect pLong = new PotionEffect(PotionEffectType.INVISIBILITY, 7201, 0, true);
		LivingEntity entity = potion.getShooter();
		if ((entity instanceof Player)) {
			if (locationInWarzone(entity.getLocation())) {
				if (potion.getEffects().contains(pShort)) {
					event.setCancelled(true);
					giveBackPotion((Player) entity, (short) 32702);
				} else if (potion.getEffects().contains(pLong)) {
					event.setCancelled(true);
					giveBackPotion((Player) entity, (short) 32766);
				}
			}
		}
	}
	
//	@EventHandler
	public void onPlayerDies(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (!player.isOp() && locationInWarzone(player.getLocation())) {
			cooldowns.put(player, (int) (System.currentTimeMillis() / 1000) + 30);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					cooldowns.remove(player);
				}
			}, 1000);
		} 
		if (player.getName().equals("Esaych")) {
			event.getDrops().clear();
			player.getWorld().createExplosion(player.getLocation(), 0);
			event.setDeathMessage("The owner was ANNIHILATED by " + player.getKiller());
			event.setDroppedExp(100);
		}
	}

//	@EventHandler
	public void onPlayerSendsCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event.getMessage().startsWith("/back")) {
			if (cooldowns.containsKey(player)) {
				if (cooldowns.get(player) > (int) (System.currentTimeMillis() / 1000)) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You can't go back for another " + (cooldowns.get(player) - (int) (System.currentTimeMillis() / 1000)) + " seconds!");
				} else {
					cooldowns.remove(player);
				}
			}
		}
		if ((event.getMessage().startsWith("/island") || event.getMessage().startsWith("/skyblock")) && locationInWarzone(player.getLocation())) {
			player.sendMessage(ChatColor.RED + "That command is disabled in here!");
			event.setCancelled(true);
		}
	}
	
//	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent event) {
//	    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
	        if (locationInWarzone(event.getPlayer().getLocation())) {
	    		ItemStack compass = new ItemStack(Material.COMPASS);
	    		Player player = event.getPlayer();
	    		if ((player.hasPermission("worldedit.navigation.jumpto") || player.hasPermission("worldedit.navigation.*")) && player.getInventory().getItemInHand().equals(compass)) {
	    			int open = player.getInventory().firstEmpty();
	    			if (open != -1) {
	    				player.getInventory().setItem(open, compass);
	    				player.setItemInHand(new ItemStack(Material.AIR));
	    			} else {
	    				player.getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getPlayer().getItemInHand());
	    	            player.setItemInHand(new ItemStack(Material.AIR));
	    			}
	    			player.updateInventory();
	    			player.sendMessage(ChatColor.RED + "No teleporting in the WarZone!");
		            event.setCancelled(true);
	    		}
	        }
//	    }
	}
	
//	@EventHandler
//	public void onPlayerTeleport(PlayerTeleportEvent event) {
//		Player player = event.getPlayer();
//		if (locationInWarzone(player.getLocation())) {
//			player.sendMessage(ChatColor.RED + "NO TPING in warzone!");
//			event.setCancelled(true);
//		}
//	}
	
	public boolean isNotProtected(Player player) {
		if (player.getInventory().getHelmet() == null
				&& player.getInventory().getChestplate() == null
				&& player.getInventory().getLeggings() == null
				&& player.getInventory().getBoots() == null)
			return true;
		return false;
	}
	
	public ArrayList<Player> selfDestructing = new ArrayList<Player>();
//	private ArrayList<Player> selfDestructingCache = new ArrayList<Player>();
	
	public void selfDistruct(final Player player) {
//		if (selfDestructing.contains(player) || selfDestructingCache.contains(player) || player.isDead())
//			return;
//		msg(player, "YOU ARE NOT WEARING ANY ARMOR");
//		msg(player, "You will self destruct in 30 seconds.");
//		selfDestructing.add(player);
//		selfDestructingCache.add(player);
//		
//		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//			@Override 
//			public void run() {
//				if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//					msg(player, "25 seconds before self destruct.");
//					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//						@Override 
//						public void run() {
//							if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//								msg(player, "20 seconds before self destruct.");
//								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//									@Override 
//									public void run() {
//										if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//											msg(player, "15 seconds before self destruct.");
//											plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//												@Override 
//												public void run() {
//													if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//														msg(player, "10 seconds before self destruct.");
//														plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//															@Override 
//															public void run() {
//																if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//																	msg(player, "5 seconds before self destruct.");
//																	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//																		@Override 
//																		public void run() {
//																			if (player.isOnline() && locationInWarzone(player.getLocation()) && selfDestructing.contains(player)) {
//																				selfDestruct(player);
//																				selfDestructingCache.remove(player);
//																			}
//																		}
//																	}, 20*5);
//																} else {
//																	selfDestructingCache.remove(player);
//																}
//															}
//														}, 20*5);
//													} else {
//														selfDestructingCache.remove(player);
//													}
//												}
//											}, 20*5);
//										} else {
//											selfDestructingCache.remove(player);
//										}
//									}
//								}, 20*5);
//							} else {
//								selfDestructingCache.remove(player);
//							}
//						}
//					}, 20*5);
//				} else {
//					selfDestructingCache.remove(player);
//				}
//			}
//		}, 20*5);
	}
	
	public void selfDestruct(Player player) {
//		Block block = player.getLocation().getBlock();
//		block.setType(Material.CHEST);
//		final Chest chest = (Chest) block.getState();
//		for (ItemStack is : player.getInventory()) {
//			if (is != null)
//				chest.getInventory().addItem(is);
//		}
		
//		final Inventory inv = player.getInventory();
//		final World world = player.getWorld();
//		final Location loc = player.getLocation();
		
		Random r = new Random();
		double x, z;
		for (ItemStack is : player.getInventory()) {
			if (is != null) {
				Entity i = player.getWorld().dropItemNaturally(player.getLocation(), is);

				x = r.nextDouble()/2 - .25;
				z = r.nextDouble()/2 - .25;

				i.setVelocity(new Vector(x, 1, z));
			}
		}
		Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
		FireworkMeta fwm = fw.getFireworkMeta();
		           
		FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(Type.BALL).build();
		       
		fwm.addEffects(effect);
		fwm.setPower(0);       
		fw.setFireworkMeta(fwm);
		
//		((CraftWorld)player.getWorld()).getHandle().broadcastEntityEffect(
//                ((CraftFirework)fw).getHandle(),
//                (byte)17);
		
		player.getInventory().clear();
		player.setHealth(0);
		selfDestructing.remove(player);
//		log(inv.toString());
		
//		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//			@Override 
//			public void run() {
//				log(inv.toString());
//				Random r = new Random();
//				double x, z;
//				for (ItemStack is : chest.getInventory()) {
//					if (is != null) {
//						Entity i = chest.getWorld().dropItemNaturally(chest.getLocation(), is);
//
//						x = r.nextDouble()/2 - .25;
//						z = r.nextDouble()/2 - .25;
//
//						log("x: " + x + " z: " + z);
//						i.setVelocity(new Vector(x, 5, z));
//					}
//				}
//			}
//		}, 20 * 3);
//		Bukkit.getWorld("Survival").createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), (float)5, false, false);
	}
	
	public boolean locationInWarzone(Location loc) {
		Faction faction = BoardColls.get().getFactionAt(PS.valueOf(loc));
		if (faction.getName().equals("WarZone"))
			return true;
		return false;
	}
	
	public void makeLegit(Player player) {
		try {
			if (plugin.isGod(player) || player.getAllowFlight()) {
//				DisguiseCraft.getAPI().isDisguised(player)
//			if (VanishNoPacket.isVanished(player.getName()) || plugin.isGod(player) || player.getAllowFlight()) {
//				log("Vanished: " + VanishNoPacket.isVanished(player.getName()));
//				log("God: " + plugin.isGod(player));
//				log("Flying: " + player.getAllowFlight());
				msg(player, "Now entering the warzone:");
			}
		} catch (Exception e) {
		}
//		try {
//			if (VanishNoPacket.isVanished(player.getName())) {
//				Bukkit.dispatchCommand(player, "v");
//			}
//		} catch (VanishNotLoadedException e) {}
		if (plugin.isGod(player)) {
			Bukkit.dispatchCommand(player, "god");
		}
		if (player.getAllowFlight()) {
			Bukkit.dispatchCommand(player, "fly");
		}
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.INVISIBILITY)) {
					
					ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE, 1);
					if (player.getInventory().getItemInHand().equals(bottle)) {
						player.getInventory().remove(bottle);
					} else if (player.getInventory().contains(bottle)) {
						player.getInventory().remove(bottle);
					}
					
					if (effect.getDuration() <= 3600) {
						giveBackPotion(player, (short) 16318);
					} else {
						giveBackPotion(player, (short) 16382);
					}
					break;
				}
			}
			player.sendMessage(ChatColor.GOLD + "You have been made visible.");
		}
//		if (DisguiseCraft.getAPI().isDisguised(player)) {
//			Bukkit.dispatchCommand(player, "ud");
//		}
	}
	
	public void giveBackPotion(Player player, short dv) {
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		ItemStack potionItem = new ItemStack(Material.POTION, 1);
		potionItem.setDurability(dv);
		player.getInventory().addItem(potionItem);
	}
	
	public void log(String info) {
		plugin.getLogger().info("<LegitWarzone> " + info);
	}
	
	public void msg(Player player, String msg) {
		player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "LegitWarZone" + ChatColor.GOLD + "] " + ChatColor.GREEN + msg);
	}
}
