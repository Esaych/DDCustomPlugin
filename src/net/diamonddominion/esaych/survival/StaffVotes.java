package net.diamonddominion.esaych.survival;

import java.util.ArrayList;
import java.util.List;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class StaffVotes {
	private CustomPlugin plugin;
	Inventory voteInv;
	ItemStack no;
	ItemStack yes;
	Chest chest;
	ArrayList<String> votingPlayers = new ArrayList<String>();

	public StaffVotes(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		noSlots.add(0);
		noSlots.add(9);
		noSlots.add(18);
		noSlots.add(1);
		noSlots.add(10);
		noSlots.add(19);
		noSlots.add(2);
		noSlots.add(11);
		noSlots.add(20);
		noSlots.add(3);
		noSlots.add(12);
		noSlots.add(21);
		yesSlots.add(8);
		yesSlots.add(17);
		yesSlots.add(26);
		yesSlots.add(7);
		yesSlots.add(16);
		yesSlots.add(25);
		yesSlots.add(5);
		yesSlots.add(15);
		yesSlots.add(24);
		yesSlots.add(4);
		yesSlots.add(14);
		yesSlots.add(23);
		log("Enabled");
	}
	
	public Inventory getChest() {
		chest = (Chest) Bukkit.getWorld("Survival").getBlockAt(59, 71, 171).getState();
		return chest.getBlockInventory();
	}
	
	public Inventory getInv() {
		Sign sign = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 71, 171).getState();
		Sign sign2 = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 71, 170).getState();
		
		String title = sign.getLine(1) + " " + sign.getLine(2) + " " + sign.getLine(3) + " " + sign2.getLine(1) + " " + sign2.getLine(2) + " " + sign2.getLine(3);
		String invtitle = title;
		if (title.length() > 32)
			invtitle = title.substring(0, 32);
		
		no = new ItemStack(Material.WOOL);
		no.setDurability((short) 14);
		ItemMeta im = no.getItemMeta();
		im.setDisplayName("Click to vote " + ChatColor.RED + "NO");
		List<String> list = new ArrayList<String>();
		list.add(title);
		im.setLore(list);
		no.setItemMeta(im);
		
		yes = new ItemStack(Material.WOOL);
		yes.setDurability((short) 5);
		im = yes.getItemMeta();
		im.setDisplayName("Click to vote " + ChatColor.GREEN + "YES");
		im.setLore(list);
		yes.setItemMeta(im);
		
		voteInv = Bukkit.getServer().createInventory(null, 9, invtitle);
		voteInv.setItem(0, no);
		voteInv.setItem(1, no);
		voteInv.setItem(2, no);
		voteInv.setItem(3, no);
		ItemStack ns = new ItemStack(Material.NETHER_STAR);
		im = ns.getItemMeta();
		im.setDisplayName("Pick One");
		ns.setItemMeta(im);
		voteInv.setItem(4, ns);
		voteInv.setItem(5, yes);
		voteInv.setItem(6, yes);
		voteInv.setItem(7, yes);
		voteInv.setItem(8, yes);
		return voteInv;
	}
	
	public void voteNo(Player player) {
		removePlayer(player);
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short)3);
		
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
		
		ItemMeta im = head.getItemMeta();
		im.setDisplayName(ChatColor.DARK_RED + player.getName());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Voting NO");
		im.setLore(lore);
		head.setItemMeta(im);
		
		getChest().setItem(getNextNoSlot(), head);
		player.openInventory(getChest());
	}

	public void voteYes(Player player) {
		removePlayer(player);
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short)3);
		
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
		
		ItemMeta im = head.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + player.getName());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Voting YES");
		im.setLore(lore);
		head.setItemMeta(im);
		
		getChest().setItem(getNextYesSlot(), head);
		player.openInventory(getChest());
	}

	public void removePlayer(Player player) {
		for (ItemStack item : getChest().getContents()) {
			if (item != null && item.getType().equals(Material.SKULL_ITEM)) {
				SkullMeta skull = (SkullMeta) item.getItemMeta();
				if (skull.getOwner().equals(player.getName()))
					getChest().remove(item);
			}
		}
	}
	
	ArrayList<Integer> noSlots = new ArrayList<Integer>();
	ArrayList<Integer> yesSlots = new ArrayList<Integer>();
	
	public int getNextNoSlot() {
		for (int i : noSlots) {
			if (getChest().getItem(i) == null)
				return i;
		}
		return 0;
	}
	
	public int getNextYesSlot() {
		for (int i : yesSlots) {
			if (getChest().getItem(i) == null)
				return i;
		}
		return 0;
	}
	
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission("customplugin.staffvote")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to vote");
			return true;
		}
		Player player = (Player) sender;
		if (args.length > 0) {
			if (!sender.hasPermission("customplugin.staffvote.manage")) {
				sender.sendMessage(ChatColor.RED + "Only headadmin and up can manage staffvotes.");
				return true;
			}
			String arg = args[0];
//			if (arg.equals("setquestion")) {
//				String question = "";
//				for (String str: args) {
//					if (!str.equals("setquestion")) {
//						question += str + " ";
//					}
//				}
//				question = question.substring(0, question.length() - 1);
//				Sign sign = (Sign) Bukkit.getWorld("Survival").getBlockAt(64, 71, 133).getState();
//				if (question.length() > 16) {
//					String line1 = question.substring(question.substring(0, 16).lastIndexOf(" "));
//					question = question.substring(line1.length(), question.length());
//					if (question.length() > 16) {
//						String line2 = question.substring(question.substring(0, 16).lastIndexOf(" "));
//						question = question.substring(line2.length(), question.length());
//						if (question.length() > 16) {
//							String line3 = question.substring(question.substring(0, 16).lastIndexOf(" "));
//							question = question.substring(line3.length(), question.length());
//						} else {
//							sign.setLine(1, line1);
//							sign.setLine(2, line2);
//							sign.setLine(3, question);
//						}
//					} else {
//						sign.setLine(1, line1);
//						sign.setLine(2, question);
//						sign.setLine(3, "");
//					}
//				} else {
//					sign.setLine(1, question);
//					sign.setLine(2, "");
//					sign.setLine(3, "");
//				}
//				sign.update();
//				sender.sendMessage(ChatColor.GREEN + "Question changed to: " + ChatColor.BLUE + sign.getLine(1) + " " + sign.getLine(2) + " " + sign.getLine(3));
//				return true;
//			} else 
			if (arg.equalsIgnoreCase("clear")) {
				getChest().clear();
				sender.sendMessage(ChatColor.GREEN + "Vote chest cleared.");
				return true;
			} else if (arg.equalsIgnoreCase("open")) {
				player.openInventory(getChest());
			} else {
				sender.sendMessage(ChatColor.RED + "Error: Args can be open, or clear");
				return true;
			}
		}
		votingPlayers.add(player.getName());
		player.openInventory(getInv());
		return true;
	}
	
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity hm = event.getWhoClicked();
		ItemStack ci = event.getCurrentItem();
		if (votingPlayers.contains(hm.getName())) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getDurability() == (short) 14)
					voteNo((Player) hm);
				if (event.getCurrentItem().getDurability() == (short) 5)
					voteYes((Player) hm);
				
				votingPlayers.remove(event.getWhoClicked().getName());
			}
		}
		if (ci != null && ci.getType().equals(Material.SKULL_ITEM)) {
			ItemMeta skull = event.getCurrentItem().getItemMeta();
			List<String> lore = skull.getLore();
			if (lore != null && lore.size() > 0) {
				if (lore.get(0).startsWith("Voting ")) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void onInventoryClose(InventoryCloseEvent event) {
		if (votingPlayers.contains(event.getPlayer().getName())) {
			votingPlayers.remove(event.getPlayer().getName());
		}
	}
	
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("customplugin.staffvote")) {
			for (ItemStack item : getChest().getContents()) {
				if (item != null && item.getType().equals(Material.SKULL_ITEM)) {
					SkullMeta skull = (SkullMeta) item.getItemMeta();
					if (skull.getOwner().equals(event.getPlayer().getName()))
						return;
				}
			}
			event.getPlayer().sendMessage(ChatColor.BLUE + "STAFFVOTE: " + ChatColor.RED + "You have yet to vote!");
			Sign sign = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 71, 171).getState();
			Sign sign2 = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 71, 170).getState();
			
			String title = sign.getLine(1) + " " + sign.getLine(2) + " " + sign.getLine(3) + " " + sign2.getLine(1) + " " + sign2.getLine(2) + " " + sign2.getLine(3);
			event.getPlayer().sendMessage(ChatColor.GREEN + title);
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<StaffVotes> " + info);
	}
}
