package net.diamonddominion.esaych.global;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;


@SuppressWarnings("deprecation")
public class SpawnerSwitch {
	
	private CustomPlugin plugin;
	private ArrayList<CreatureType> mobs = new ArrayList<CreatureType>();
	LivingEntity e;
	
	public SpawnerSwitch(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		mobs.add(CreatureType.PIG);
		mobs.add(CreatureType.SHEEP);
		mobs.add(CreatureType.COW);
		mobs.add(CreatureType.CHICKEN);
		mobs.add(CreatureType.ZOMBIE);
		mobs.add(CreatureType.SKELETON);
		mobs.add(CreatureType.SPIDER);
		mobs.add(CreatureType.CAVE_SPIDER);
		mobs.add(CreatureType.PIG_ZOMBIE);
		log("Enabled");
	}
	
	private int getInt(CreatureType c) {
		for (int i = 0; i < mobs.size(); i++) {
			if (mobs.get(i) == c)
				return i;
		}
		return 0;
	}
	
	private void playSound(CreatureType c, Block b) {
		e = b.getWorld().spawnCreature(b.getLocation().add(.5, 1, .5), c);
		e.damage(200);
	}
	
	private boolean isAllowedToBeBreaked(Block spawner) {
		Block lowerBlock = spawner.getRelative(BlockFace.DOWN);
		if (lowerBlock.getType() == Material.GLOWSTONE && lowerBlock.getData() == (byte)1)
			return false;
		return true;
	}
	
	public void msg(Player player, String msg) {
		player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.RED + "Spawner" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN + msg);
	}
	
//	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() == e) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
		
	}
	
//	@EventHandler
	public void onPlayerPunch(PlayerInteractEvent event) {
		final Block spawner = event.getClickedBlock();
		if (spawner != null && spawner.getType() == Material.MOB_SPAWNER) {
			Player player = event.getPlayer();
			String world = player.getWorld().getName();
			if (!world.equals("GameWorld") && isAllowedToBeBreaked(spawner)) {
				CreatureSpawner cage = ((CreatureSpawner) spawner.getState());
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (!player.getInventory().getItemInHand().getType().isBlock() || player.getItemInHand().getType() == Material.AIR) {
						int i = getInt(cage.getCreatureType()) + 1;
						try {
							cage.setCreatureType(mobs.get(i));
							playSound(mobs.get(i), spawner);
						} catch (Exception e) {
							cage.setCreatureType(mobs.get(0));
							playSound(mobs.get(0), spawner);
						}
						//					Location loc = spawner.getLocation();
						//					if (i == 1) {
						//						spawner.getWorld().playSound(loc, Sound.SHEEP_IDLE , 3F, 3F);
						//					} else if (i == 2) {
						//						spawner.getWorld().playSound(loc, Sound.COW_IDLE, 3F, 3F);
						//					}
						msg(player, "Changed to a " + ChatColor.AQUA + cage.getCreatureTypeName() + ". " + ChatColor.GREEN + "Relog to see it.");
					} else {
						msg(player, "You can only change spawners when you have nothing in your hand.");
					}
				} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					player.sendMessage(ChatColor.RED + "=========== " + ChatColor.DARK_RED + "Mob Spawner List" + ChatColor.RED + " ===========");
					for (CreatureType c : mobs) {
						String msg = ChatColor.DARK_GREEN + String.valueOf(getInt(c) + 1) + ". " + ChatColor.GREEN + c.getName();
						if (c.getName().equals("Cow"))
							msg += ChatColor.RED + "                 Current Spawner Type:";
						if (c.getName().equals("Chicken"))
							msg += " " + ChatColor.BOLD + "         " + cage.getCreatureTypeName();
						if (c.getName().equals("CaveSpider"))
							msg += ChatColor.RED + "  Use like you would a noteblock";
						player.sendMessage(msg);
					}
				}
			} else {
				msg(player, "You can not change this spawner.");
			}
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<SpawnerSwitch> " + info);
	}
}
