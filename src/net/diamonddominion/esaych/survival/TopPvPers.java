package net.diamonddominion.esaych.survival;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.codisimus.plugins.pvpreward.PvPReward;


public class TopPvPers {
	
	private CustomPlugin plugin;
	
	public TopPvPers(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	private ArrayList<String> records = readFile();
	private boolean processing = false;
	
	public void enable() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		log ("Day: " + day);
		if (day == 2 && highestKill(300)) {
			log("Clearing all KDR's shortly");
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pvp reset kdr all");
				}
			}, 200);
		}
//		updateSigns();
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
	}
	
	public boolean highestKill(int max) {
		for (String line : records) {
			String[] splitLine = line.split(";");
			if (Integer.parseInt(splitLine[1]) > max) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> readFile() {
		ArrayList<String> lines = new ArrayList<String>();
		try{
			Scanner reader = new Scanner(new File("plugins/PvPReward/pvpreward.records"));
			while(reader.hasNextLine()){;
			String currentLine = reader.nextLine();
			lines.add(currentLine);
			}
		}catch(FileNotFoundException e){
			log("Could not load PvPReward Records!");
		}
		return lines;
	}
	
	public void setIndividualStats(String player, Sign sign) {
		records = readFile();
		sign.setLine(0, Bukkit.getPlayer(player).getPlayerListName());
		double kdr = 0;
		String[] splitLine = {player,"0","0","0"};
		for (String line : records) {
			splitLine = line.split(";");
			if (splitLine[0].equals(player)) {
				if (Integer.parseInt(splitLine[2]) != 0) {
					kdr = (double)Integer.parseInt(splitLine[1]) / Integer.parseInt(splitLine[2]);
				} else {
					kdr = Integer.parseInt(splitLine[1]);
				}
				break;
			}
		}
		sign.setLine(1, ChatColor.DARK_RED + "KDR: " + ChatColor.DARK_BLUE + kdr + "          ");
		sign.setLine(2, ChatColor.DARK_RED + "Kills: " + ChatColor.DARK_BLUE + splitLine[1] + "         ");
		sign.setLine(3, ChatColor.DARK_RED + "Deaths: " + ChatColor.DARK_BLUE + splitLine[2] + "         ");
		sign.update();
	}
	
	@SuppressWarnings("null")
	public void updateSigns() {
		records = readFile();
		processing = true;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Double> data = new HashMap();
		ValueComparator bvc =  new ValueComparator(data);
		TreeMap<String,Double> tops = new TreeMap<String,Double>(bvc);
		for (String line : records) {
			String[] splitLine = line.split(";");
			double kdr;
			if (Integer.parseInt(splitLine[2]) != 0) {
				kdr = (double)Integer.parseInt(splitLine[1]) / Integer.parseInt(splitLine[2]);
			} else {
				kdr = Integer.parseInt(splitLine[1]);
			}
			data.put(splitLine[0], kdr);
		}
		tops.putAll(data);
		
	    @SuppressWarnings("rawtypes")
		Iterator it = tops.entrySet().iterator();
	    
	    @SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<String> write = new ArrayList(0);
	    
	    int playerNum = 1;
	    
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
	        String nameLine = playerNum + ". " + pairs.getKey();
	        if (nameLine.length() > 14)
	        	nameLine = nameLine.substring(0, 14);
	        nameLine += ":";
	        if (write == null) {
	        	write.set(0, nameLine);
	        } else {
	        	write.add(nameLine);
	        }
	        write.add(ChatColor.DARK_RED + "KDR: " + pairs.getValue());
	        playerNum++;
	        it.remove();
	    }

	    Sign sign;
	    try {
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 321).getBlock().getState();
	    	sign.setLine(0, write.get(0));
	    	sign.setLine(1, write.get(1));
	    	sign.setLine(2, write.get(2));
	    	sign.setLine(3, write.get(3));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 322).getBlock().getState();
	    	sign.setLine(0, write.get(4));
	    	sign.setLine(1, write.get(5));
	    	sign.setLine(2, write.get(6));
	    	sign.setLine(3, write.get(7));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 323).getBlock().getState();
	    	sign.setLine(0, write.get(8));
	    	sign.setLine(1, write.get(9));
	    	sign.setLine(2, write.get(10));
	    	sign.setLine(3, write.get(11));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 324).getBlock().getState();
	    	sign.setLine(0, write.get(12));
	    	sign.setLine(1, write.get(13));
	    	sign.setLine(2, write.get(14));
	    	sign.setLine(3, write.get(15));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 325).getBlock().getState();
	    	sign.setLine(0, write.get(16));
	    	sign.setLine(1, write.get(17));
	    	sign.setLine(2, write.get(18));
	    	sign.setLine(3, write.get(19));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 326).getBlock().getState();
	    	sign.setLine(0, write.get(20));
	    	sign.setLine(1, write.get(21));
	    	sign.setLine(2, write.get(22));
	    	sign.setLine(3, write.get(23));
	    	sign.update();
	    } catch (Exception e) {
	    	log("Failed to write signs on left side!");
	    }
	    try {
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 326).getBlock().getState();
	    	sign.setLine(0, write.get(0));
	    	sign.setLine(1, write.get(1));
	    	sign.setLine(2, write.get(2));
	    	sign.setLine(3, write.get(3));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 325).getBlock().getState();
	    	sign.setLine(0, write.get(4));
	    	sign.setLine(1, write.get(5));
	    	sign.setLine(2, write.get(6));
	    	sign.setLine(3, write.get(7));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 324).getBlock().getState();
	    	sign.setLine(0, write.get(8));
	    	sign.setLine(1, write.get(9));
	    	sign.setLine(2, write.get(10));
	    	sign.setLine(3, write.get(11));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 323).getBlock().getState();
	    	sign.setLine(0, write.get(12));
	    	sign.setLine(1, write.get(13));
	    	sign.setLine(2, write.get(14));
	    	sign.setLine(3, write.get(15));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 322).getBlock().getState();
	    	sign.setLine(0, write.get(16));
	    	sign.setLine(1, write.get(17));
	    	sign.setLine(2, write.get(18));
	    	sign.setLine(3, write.get(19));
	    	sign.update();
	    	sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 321).getBlock().getState();
	    	sign.setLine(0, write.get(20));
	    	sign.setLine(1, write.get(21));
	    	sign.setLine(2, write.get(22));
	    	sign.setLine(3, write.get(23));
	    	sign.update();
	    } catch (Exception e) {
	    	log("Failed to write signs on right side!");
	    }
	    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	public void run() {
	    		processing = false;
	    	}
	    }, 100L);
	}
	
	public boolean alreadyHasASign(Player player) {
		ArrayList<String> topLines = new ArrayList<String>();
		
		try {
		Sign sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 321).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 322).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 323).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 324).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 325).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 272, 65, 326).getBlock().getState();
		topLines.add(sign.getLine(0));
		
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 326).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 325).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 324).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 323).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 322).getBlock().getState();
		topLines.add(sign.getLine(0));
		sign = (Sign) new Location(Bukkit.getWorld("Survival"), 246, 65, 321).getBlock().getState();
		topLines.add(sign.getLine(0));
		} catch (Exception e) {}
		
		boolean already = topLines.contains(player.getPlayerListName());
		if (already) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "LeaderBoard" + ChatColor.GOLD + "]" + ChatColor.RED + " You have your own sign!");
		}
		return already;
	}
	
//	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block clicked = event.getClickedBlock();
		if (clicked != null && clicked.getState() instanceof Sign) {
			Location loc = clicked.getLocation();
			if (loc.getBlockX() == 246 || loc.getBlockX() == 272) {
				if (loc.getBlockY() == 66) {
					if (loc.getBlockZ() == 322 || loc.getBlockZ() == 325) {
						if (!processing) {
							updateSigns();
							log(event.getPlayer().getName() + " reset sign data");
						}
					}
				} else if (loc.getBlockY() == 65) {
					if (loc.getBlockZ() >= 321 && loc.getBlockZ() <= 326) {
						if(!alreadyHasASign(event.getPlayer()))
							setIndividualStats(event.getPlayer().getName(), (Sign) clicked.getState());
					}
				}
			} 
		}
	}
	
//	@EventHandler
	public void onPlayerDeathEvent(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Player killer = player.getKiller();
			String world = player.getWorld().getName();
			if (killer != null && killer instanceof Player && !(world.equals("Build") || world.equals("Destruction"))) {
				if (!PvPReward.hasPermisson(killer, player) || world.equals("GameWorld")) {
					PvPReward.getRecord(player.getName()).incrementDeaths();
					PvPReward.getRecord(killer.getName()).incrementKills();
				}
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<TopPvPers> " + info);
	}
}


class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}