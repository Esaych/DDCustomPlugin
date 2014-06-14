package net.diamonddominion.esaych.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;


public class WorldBlocks {
	private CustomPlugin plugin;
	private Map<String, ItemStack[]> invs = new HashMap<String, ItemStack[]>();
	private ArrayList<Player> selecting = new ArrayList<Player>();
	
	public WorldBlocks(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void disable() {
		if (selecting.size() > 0) {
			for (Player p : selecting) {
				try {
					restoreInvent(p);
				} catch (Exception e) {
					log(p + " didn't get their inv back");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean onWorldCommand(CommandSender sender) {
		Player p = (Player) sender;
		if (selecting.contains(p)) {
			restoreInvent(p);
		}
		PlayerInventory inv = p.getInventory();
		saveInvent(p);
		
		
		ItemStack i = new ItemStack(Material.IRON_DOOR);
		ItemMeta im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.YELLOW + "Exit");
		List<String> l = new ArrayList<String>();
		l.add("Exit this Menu");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.FEATHER);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.DARK_RED + "Survival");
		l = new ArrayList<String>();
		l.add("Factions Survival World");
		l.add("Griefing and Raiding ALLOWED");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.DIAMOND);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.AQUA + "Build");
		l = new ArrayList<String>();
		l.add("Plots Creative World");
		l.add("Claim and build what you want");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.YELLOW_FLOWER);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.GOLD + "TownsWorld");
		l = new ArrayList<String>();
		l.add("Towny Peaceful Survival world");
		l.add("Griefing and Raiding DENIED");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.DIAMOND_SWORD);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.AQUA + "EventWorld");
		l = new ArrayList<String>();
		l.add("Minigames, and fun world");
		l.add("MobArena, Parkour, PvP, HorseRaces...");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.SAPLING);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.DARK_GREEN + "SkyBlock");
		l = new ArrayList<String>();
		l.add("Skyblock world");
		l.add("Floating island survival");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.BOW);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.DARK_BLUE + "SurvivalGames");
		l = new ArrayList<String>();
		l.add("Survival Games world");
		l.add("Survive and fight to the death in the games");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.SULPHUR);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.DARK_RED + "Destruction");
		l = new ArrayList<String>();
		l.add("TNT and Destruction Creative world");
		l.add("Destroy everyone elses stuff, for FUN!");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		i = new ItemStack(Material.REDSTONE);
		im = i.getItemMeta(); 
		im.setDisplayName(ChatColor.RED + "Redstone");
		l = new ArrayList<String>();
		l.add("Redstone Creative Development");
		l.add("Meant for creative machines alone");
		im.setLore(l);
		i.setItemMeta(im);
		inv.addItem(i);
		
		p.updateInventory();
		
		p.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "WorldBlocks" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "Select a world and punch to warp there");
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private void saveInvent(Player p) {
		Inventory inv = p.getInventory();
		ItemStack[] itemarray = new ItemStack[inv.getSize()];
		int iter = 0;
		for(ItemStack item : inv.getContents()){
			itemarray[iter] = item;
			iter++;
		}
		invs.put(p.getName(), itemarray);
		selecting.add(p);
		p.getInventory().clear();
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	private void restoreInvent(Player p) {
		if(invs.containsKey(p.getName())){
			p.getInventory().clear();
			ItemStack[] oldinv = invs.get(p.getName());
			for(ItemStack i : oldinv){
				if(i==null){
					continue;
				}
				p.getInventory().addItem(i);
			}
			p.updateInventory();
			invs.remove(p);
			selecting.remove(p);
		}
		
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (selecting.contains(p)) {
			Material i = p.getItemInHand().getType();
			restoreInvent(p);
			if (i.equals(Material.FEATHER)) {
				Bukkit.dispatchCommand(p, "spawn");
			} else if (i.equals(Material.DIAMOND)) {
				Bukkit.dispatchCommand(p, "warp Build");
			} else if (i.equals(Material.YELLOW_FLOWER)) {
				Bukkit.dispatchCommand(p, "warp TownWorld");
			} else if (i.equals(Material.DIAMOND_SWORD)) {
				Bukkit.dispatchCommand(p, "warp EventWorld");
			} else if (i.equals(Material.SAPLING)) {
				Bukkit.dispatchCommand(p, "warp SkyBlock");
			} else if (i.equals(Material.BOW)) {
				Bukkit.dispatchCommand(p, "warp SurvivalGames");
			} else if (i.equals(Material.SULPHUR)) {
				Bukkit.dispatchCommand(p, "warp Destruction");
			} else if (i.equals(Material.REDSTONE)) {
				Bukkit.dispatchCommand(p, "warp Redstone");
			} 
			event.setCancelled(true);
		}
	}
	
	public void onPlayerMove(PlayerMoveEvent event) {
		if (selecting.contains(event.getPlayer())) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if (from.getX() != to.getX() && from.getZ() != to.getZ()) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "WorldBlocks" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "Don't move when you select worlds");
				restoreInvent(event.getPlayer());
			}
		}
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (selecting.contains(event.getPlayer())) {
			restoreInvent(event.getPlayer());
		}
	}
	
	public void onPlayerTakeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (selecting.contains((Player) event.getEntity())) {
				restoreInvent((Player) event.getEntity());
			}
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (selecting.contains(event.getPlayer())) {
			restoreInvent(event.getPlayer());
		}
	}
	
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (selecting.contains(event.getPlayer())) {
			restoreInvent(event.getPlayer());
			event.getItemDrop().remove();
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<WorldBlocks> " + info);
	}
}
