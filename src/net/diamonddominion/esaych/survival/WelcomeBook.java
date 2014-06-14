package net.diamonddominion.esaych.survival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;


public class WelcomeBook {
	private CustomPlugin plugin;

	public WelcomeBook(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	
	public void enable() {
		writeBook();
		log("Enabled");
	}
	
	public void writeBook() {
		BookMeta bm = (BookMeta)book.getItemMeta();
		bm.setAuthor("DD Staff");
		bm.setTitle("Diamond Dominion Server Guide");
		ArrayList<String> pages = new ArrayList<String>();
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"Welcome to the\n&1&oDiamond Dominion!\n\n" + 
				"&0Our rules can be seen by typing &2/rules\n" +
				"&0All issues should be brought up to &cAdmins&0, &aModerators&0, or &6VIMods\n\n" +
				"&0You can always return to spawn with &2/spawn"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lOur chat format:\n\n" +
				"&bFaction\n&3<World&a(Channel)&3>\n&e<&6Donor Prefix&e>\n&cRankColor&0+&8Name:\n&0Message\n\n" +
				"&0Message players:\n&2/msg [player] [msg]\n" + 
				"&0Reply to messages:\n&2/r [msg]"
			));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lUseful warps:\n\n" + 
				"&2/warp &1shop\n" +
				"&2/warp &1info\n" + 
				"&2/warp &1rules\n" +
				"&2/warp &1modapps\n" +
				"&2/warp &1portals\n" +
				"&2/warp &1contest\n" +
				"&2/warp &3<World>\n\n" +
				"&0Replace &3<World> &0with the name/abbreviation of the server worlds in next pages."
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&3<SV> &4&lSurvival&r&0:\n" + 
				"Our Grief World. Claim and protect land using Factions, type /f help for faction help. Raiding, and tnt usage all allowed.\n\n" +
				"&3<N> &4&lNether&r&0\n" +
				"&3<E> &6&lEnd&r&0\n" +
				"Typical Nether and End linked to Survival."
				));
		pages.add(ChatColor.translateAlternateColorCodes('&', 
				"&3<GF> &1&lTownyWorld&r&0:\n" +
				"Griefing and Raiding is NOT allowed. PvP disabled, peaceful living. Use /towny for help.\n\n" +
				"&3<SB> &2&lSkyBlock&e&0:\n" +
				"SkyBlock world! Survive on floating islands. Build up your island rank."
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&3<B> &6&lBuild&r&0:\n" + 
				"Creative plot world. Claiming help provided. Griefing not allowed. Specialty extensions for all designers.\n\n" +
				"Build world extensions:\n" + 
				"&3<RS> &4&lRedstone&r&0\n" +
				"&3<PX> &1&lPixelArt&r&0\n" +
				"&3<RS> &2&lFreeBuild&r&0\n\n" +
				"&3<DS> &4&l&oDestruction&4&0"
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&3<EW> &1&lEventWorld&r&0:\n" +
				"Many games including MobArena, WalkInPvP, Hockey, Spleef, CTF, Trampoline, PaintBall, Parkour.\n\n" +
				"&3<SG> &4&lSurvivalGames&r&0:\n" +
				"Join games and fight to the death with others in the famous Survival Games, with great maps."
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				"&1&lUseful tips:&r&0\n" +
				" Playing for 24 hours will give you &bRespected\n" +
				"&0 Voting 60 times a month with &2/vote&0 can buy you a donor rank\n" + 
				" Spawn and creative worlds provide beast sprint. &2/sprint&0 toggles\n" +
				" Smelting &4rotten flesh &0gives you leather\n"
				));
		pages.add(ChatColor.translateAlternateColorCodes('&',
				" Mob spawners change mob like noteblocks change note" +
				" Plots in creative world link when claimed together\n" +
				" We have a secret potion shop\n" +
				" Build launch pads like the ones at warzone anywhere.\n" + 
				" &4R&6a&2i&1n&5b&4o&6w &0armor is only for $20+ donors\n"
				));
		bm.setPages(pages);
		book.setItemMeta(bm);
	}
	
	public void onKitStarterGiven(Player player) {
		File File = new File("plugins/Essentials/userdata/" + player.getName().toLowerCase() + ".yml");
		FileConfiguration YMLFile = YamlConfiguration.loadConfiguration(File);
		if (YMLFile.getString("starterbook") == null) {
			player.getInventory().addItem(book);
			YMLFile.set("starterbook", true);
			try {
				YMLFile.save(File);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if ((event.getMessage().toLowerCase().startsWith("/kit starter"))) {
			onKitStarterGiven(event.getPlayer());
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<WelcomeBook> " + info);
	}
}