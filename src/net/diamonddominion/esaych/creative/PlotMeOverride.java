package net.diamonddominion.esaych.creative;

import java.util.List;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;


public class PlotMeOverride implements Listener {
	private CustomPlugin plugin;

	int wallLength = 22;

	public PlotMeOverride(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
	}

	private void attemptConnection(Player player) {
		// Define whether he can make a link of 4 plots
		String name = player.getName();
		int x;
		int z;
		Plot in = PlotManager.getPlotById(player);
		try {
			x = PlotManager.getIdX(PlotManager.getPlotId(player.getLocation()));
			z = PlotManager.getIdZ(PlotManager.getPlotId(player.getLocation()));
		} catch (Exception e) {
			return;
		}
		boolean isIn;
		if (in == null) {
			isIn = false;
		} else {
			if (in.owner.equals(name)) {
				isIn = true;
			} else {
				return;
			}
		}
		boolean doneAnything = false;
		if (!isIn) {
			regenWalls(x, z);
			return;
		}
		Plot a = getPlot((x - 1) + ";" + (z + 1));
		Plot b = getPlot(x + ";" + (z + 1));
		Plot c = getPlot((x + 1) + ";" + (z + 1));
		Plot d = getPlot((x + 1) + ";" + z);
		Plot e = getPlot((x + 1) + ";" + (z - 1));
		Plot f = getPlot(x + ";" + (z - 1));
		Plot g = getPlot((x - 1) + ";" + (z - 1));
		Plot h = getPlot((x - 1) + ";" + z);
		if (a != null && h != null && b != null)
			if (a.owner.equals(name) && h.owner.equals(name)
					&& b.owner.equals(name)) {
				openWalls(1, in, player.getName());
				doneAnything = true;
			} else {
				removeMode(in, 1);
			}
		if (c != null && d != null && b != null)
			if (c.owner.equals(name) && d.owner.equals(name)
					&& b.owner.equals(name)) {
				openWalls(2, in, player.getName());
				doneAnything = true;
			} else {
				removeMode(in, 2);
			}
		if (e != null && f != null && d != null)
			if (e.owner.equals(name) && f.owner.equals(name)
					&& d.owner.equals(name)) {
				openWalls(3, in, player.getName());
				doneAnything = true;
			} else {
				removeMode(in, 3);
			}
		if (g != null && h != null && f != null)
			if (g.owner.equals(name) && h.owner.equals(name)
					&& f.owner.equals(name)) {
				openWalls(4, in, player.getName());
				doneAnything = true;
			} else {
				removeMode(in, 4);
			}
		if (doneAnything)
			return;
		// If so, run another method, else default here
		if (b != null)
			if (b.owner.equals(name)) {
				openWalls(5, in, player.getName());
			} else {
				removeMode(in, 5);
			}
		if (d != null)
			if (d.owner.equals(name)) {
				openWalls(6, in, player.getName());
			} else {
				removeMode(in, 6);
			}
		if (f != null)
			if (f.owner.equals(name)) {
				openWalls(7, in, player.getName());
			} else {
				removeMode(in, 7);
			}
		if (h != null)
			if (h.owner.equals(name)) {
				openWalls(8, in, player.getName());
			} else {
				removeMode(in, 8);
			}
	}

	private void openWalls(int mode, Plot plot, String player) {
		// log("Mode Detected: " + mode);
		World w = Bukkit.getWorld("Build");
		if (mode == 1 && !hasMode(plot, 1)) {
			Location loc = new Location(w, PlotManager.bottomX(plot.id, w), 64,
					PlotManager.topZ(plot.id, w));
			loc.add(-3, 0, 3);
			buildWithCenter(loc, player);
		}
		if (mode == 2 && !hasMode(plot, 2)) {
			Location loc = new Location(w, PlotManager.topX(plot.id, w), 64,
					PlotManager.topZ(plot.id, w));
			loc.add(3, 0, 3);
			buildWithCenter(loc, player);
		}
		if (mode == 3 && !hasMode(plot, 3)) {
			Location loc = new Location(w, PlotManager.topX(plot.id, w), 64,
					PlotManager.bottomZ(plot.id, w));
			loc.add(3, 0, -3);
			buildWithCenter(loc, player);
		}
		if (mode == 4 && !hasMode(plot, 4)) {
			Location loc = new Location(w, PlotManager.bottomX(plot.id, w), 64,
					PlotManager.bottomZ(plot.id, w));
			loc.add(-3, 0, -3);
			buildWithCenter(loc, player);
		}
		
		if (mode == 5 && !hasMode(plot, 5)) {
			Location loc = new Location(w, midNum(
					PlotManager.bottomX(plot.id, w),
					PlotManager.topX(plot.id, w)), 64, PlotManager.topZ(
					plot.id, w));
			loc.add(0, 0, 3);
			buildWithCenterHoriz(loc);
		}
		if (mode == 6 && !hasMode(plot, 6)) {
			Location loc = new Location(w, PlotManager.topX(plot.id, w), 64,
					midNum(PlotManager.bottomZ(plot.id, w),
							PlotManager.topZ(plot.id, w)));
			loc.add(3, 0, 0);
			buildWithCenterVert(loc);
		}
		if (mode == 7 && !hasMode(plot, 7)) {
			Location loc = new Location(w, midNum(
					PlotManager.bottomX(plot.id, w),
					PlotManager.topX(plot.id, w)), 64, PlotManager.bottomZ(
					plot.id, w));
			loc.add(0, 0, -3);
			buildWithCenterHoriz(loc);
		}
		if (mode == 8 && !hasMode(plot, 8)) {
			Location loc = new Location(w, PlotManager.bottomX(plot.id, w), 64,
					midNum(PlotManager.bottomZ(plot.id, w),
							PlotManager.topZ(plot.id, w)));
			loc.add(-3, 0, 0);
			buildWithCenterVert(loc);
		}
		addMode(plot, mode);
	}
	
	@SuppressWarnings("deprecation")
	private boolean hasMode(Plot plot, int mode) {
		Block base = PlotManager.getBottom(Bukkit.getWorld("Build"), plot).getBlock();
		if (base.getLocation().clone().add(mode, 0, 0).getBlock().getData() == 1)
			return true;
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private void addMode(Plot plot, int mode) {
		Block base = PlotManager.getBottom(Bukkit.getWorld("Build"), plot).getBlock();
		base.getLocation().clone().add(mode, 0, 0).getBlock().setData((byte) 1);
	}
	
	@SuppressWarnings("deprecation")
	private void removeMode(Plot plot, int mode) {
		Block base = PlotManager.getBottom(Bukkit.getWorld("Build"), plot).getBlock();
		base.getLocation().clone().add(mode, 0, 0).getBlock().setData((byte) 0);
	}

	private int midNum(int a, int b) {
		return (a + b) / 2;
	}

	private void buildWithCenter(Location loc, String player) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		for (int i = 0; i < wallLength + 2; i++) {
			place(2, x - 1, y, z);
			place(2, x, y, z);
			place(2, x + 1, y, z);
			place(2, x + 2, y, z);
			place(2, x - 2, y, z);
			place(0, x + 2, y + 1, z);
			place(0, x - 2, y + 1, z);
			z++;
		}
		Plot plot = PlotManager.getPlotById(new Location(Bukkit
				.getWorld("Build"), x + 10, y, z + 10));
		Plot plot2 = PlotManager.getPlotById(new Location(Bukkit
				.getWorld("Build"), x - 10, y, z + 10));
		if (!(plot != null && plot.owner.equals(player))
				|| !(plot2 != null && plot2.owner.equals(player))) {
			place(44, 7, x - 2, y + 1, z - 1);
			place(44, 7, x - 1, y + 1, z - 1);
			place(44, 7, x, y + 1, z - 1);
			place(44, 7, x + 1, y + 1, z - 1);
			place(44, 7, x + 2, y + 1, z - 1);
		}
		x = loc.getBlockX();
		z = loc.getBlockZ();
		for (int i = 0; i < wallLength + 2; i++) {
			place(2, x - 1, y, z);
			place(2, x, y, z);
			place(2, x + 1, y, z);
			place(2, x + 2, y, z);
			place(2, x - 2, y, z);
			place(0, x + 2, y + 1, z);
			place(0, x - 2, y + 1, z);
			z--;
		}
		plot = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x + 10, y, z - 10));
		plot2 = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x - 10, y, z - 10));
		if (!(plot != null && plot.owner.equals(player))
				|| !(plot2 != null && plot2.owner.equals(player))) {
			place(44, 7, x - 2, y + 1, z + 1);
			place(44, 7, x - 1, y + 1, z + 1);
			place(44, 7, x, y + 1, z + 1);
			place(44, 7, x + 1, y + 1, z + 1);
			place(44, 7, x + 2, y + 1, z + 1);
		}
		x = loc.getBlockX();
		z = loc.getBlockZ();
		for (int i = 0; i < wallLength + 2; i++) {
			place(2, x, y, z - 1);
			place(2, x, y, z);
			place(2, x, y, z + 1);
			place(2, x, y, z + 2);
			place(2, x, y, z - 2);
			place(0, x, y + 1, z + 2);
			place(0, x, y + 1, z - 2);
			x++;
		}
		plot = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x + 10, y, z + 10));
		plot2 = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x + 10, y, z - 10));
		if (!(plot != null && plot.owner.equals(player))
				|| !(plot2 != null && plot2.owner.equals(player))) {
			place(44, 7, x - 1, y + 1, z - 2);
			place(44, 7, x - 1, y + 1, z - 1);
			place(44, 7, x - 1, y + 1, z);
			place(44, 7, x - 1, y + 1, z + 1);
			place(44, 7, x - 1, y + 1, z + 2);
		}
		x = loc.getBlockX();
		z = loc.getBlockZ();
		for (int i = 0; i < wallLength + 2; i++) {
			place(2, x, y, z - 1);
			place(2, x, y, z);
			place(2, x, y, z + 1);
			place(2, x, y, z + 2);
			place(2, x, y, z - 2);
			place(0, x, y + 1, z + 2);
			place(0, x, y + 1, z - 2);
			x--;
		}
		plot = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x - 10, y, z + 10));
		plot2 = PlotManager.getPlotById(new Location(Bukkit.getWorld("Build"),
				x - 10, y, z - 10));
		if (!(plot != null && plot.owner.equals(player))
				|| !(plot2 != null && plot2.owner.equals(player))) {
			place(44, 7, x + 1, y + 1, z - 2);
			place(44, 7, x + 1, y + 1, z - 1);
			place(44, 7, x + 1, y + 1, z);
			place(44, 7, x + 1, y + 1, z + 1);
			place(44, 7, x + 1, y + 1, z + 2);
		}
		x = loc.getBlockX();
		z = loc.getBlockZ();

	}

	private void buildWithCenterHoriz(Location loc) {
		int x = loc.getBlockX();
		int x2 = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		for (int i = 0; i < wallLength / 2; i++) {
			place(0, x, y + 1, z - 2);
			place(2, x, y, z - 2);
			place(2, x, y, z - 1);
			place(2, x, y, z);
			place(2, x, y, z + 1);
			place(2, x, y, z + 2);
			place(0, x, y + 1, z + 2);

			place(0, x2, y + 1, z - 2);
			place(2, x2, y, z - 2);
			place(2, x2, y, z - 1);
			place(2, x2, y, z);
			place(2, x2, y, z + 1);
			place(2, x2, y, z + 2);
			place(0, x2, y + 1, z + 2);
			x++;
			x2--;
		}
		if (x > 0) {
			x++;
			x2++;
		}
		place(44, 7, x - 1, y + 1, z - 2);
		place(44, 7, x - 1, y + 1, z - 1);
		place(44, 7, x - 1, y + 1, z);
		place(44, 7, x - 1, y + 1, z + 1);
		place(44, 7, x - 1, y + 1, z + 2);

		place(44, 7, x2, y + 1, z - 2);
		place(44, 7, x2, y + 1, z - 1);
		place(44, 7, x2, y + 1, z);
		place(44, 7, x2, y + 1, z + 1);
		place(44, 7, x2, y + 1, z + 2);
	}

	private void buildWithCenterVert(Location loc) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		int z2 = loc.getBlockZ();
		for (int i = 0; i < wallLength / 2; i++) {
			place(0, x - 2, y + 1, z);
			place(2, x - 2, y, z);
			place(2, x - 1, y, z);
			place(2, x, y, z);
			place(2, x + 1, y, z);
			place(2, x + 2, y, z);
			place(0, x + 2, y + 1, z);

			place(0, x - 2, y + 1, z2);
			place(2, x - 2, y, z2);
			place(2, x - 1, y, z2);
			place(2, x, y, z2);
			place(2, x + 1, y, z2);
			place(2, x + 2, y, z2);
			place(0, x + 2, y + 1, z2);
			z++;
			z2--;
		}
		if (z > 0) {
			z++;
			z2++;
		}
		place(44, 7, x - 2, y + 1, z - 1);
		place(44, 7, x - 1, y + 1, z - 1);
		place(44, 7, x, y + 1, z - 1);
		place(44, 7, x + 1, y + 1, z - 1);
		place(44, 7, x + 2, y + 1, z - 1);

		place(44, 7, x - 2, y + 1, z2);
		place(44, 7, x - 1, y + 1, z2);
		place(44, 7, x, y + 1, z2);
		place(44, 7, x + 1, y + 1, z2);
		place(44, 7, x + 2, y + 1, z2);
	}

	@SuppressWarnings("deprecation")
	private static void setBlock(int id, int x, int y, int z) {
		try {
			Bukkit.getWorld("Build").getBlockAt(x, y, z).setTypeId(id);
		} catch (Exception e) {}
	}

	@SuppressWarnings("deprecation")
	private static void place(int id, int data, int x, int y, int z) {
		Block b = Bukkit.getWorld("Build").getBlockAt(x, y, z);
		int type = b.getTypeId();
		if (type == 0 || type == 1 || type == 56 || (type == 44 && b.getData() == (byte) 7) || type == 2 || type == 3)
			setBlock(id, x, y, z);
		try {
			Bukkit.getWorld("Build").getBlockAt(x, y, z).setData((byte) data);
		} catch (Exception e) {}
	}

	@SuppressWarnings("deprecation")
	private static void place(int id, int x, int y, int z) {
		Block b = Bukkit.getWorld("Build").getBlockAt(x, y, z);
		int type = b.getTypeId();
		if (type != id)
			if (type == 0 || type == 1 || type == 56 || (type == 44 && b.getData() == (byte) 7) || type == 2 || type == 3)
				setBlock(id, x, y, z);
	}
	
	@SuppressWarnings("deprecation")
	private static void dplace(int id, int data, int x, int y, int z) {
		setBlock(id, x, y, z);
		try {
			Bukkit.getWorld("Build").getBlockAt(x, y, z).setData((byte) data);
		} catch (Exception e) {}
	}

	@SuppressWarnings("deprecation")
	private static void dplace(int id, int x, int y, int z) {
		int type = Bukkit.getWorld("Build").getBlockAt(x, y, z).getTypeId();
		if (type != id) {
			setBlock(id, x, y, z);
		}
	}
	
	private static void regenWalls(int plotx, int plotz) {
		Location bottomPlot = PlotManager.getPlotBottomLoc(
				Bukkit.getWorld("Build"), plotx + ";" + plotz);
		Location topPlot = PlotManager.getPlotTopLoc(Bukkit.getWorld("Build"),
				plotx + ";" + plotz);
		int minx = bottomPlot.getBlockX() - 1;
		int minz = bottomPlot.getBlockZ() - 1;
		int maxx = topPlot.getBlockX() + 1;
		int maxz = topPlot.getBlockZ() + 1;

		// Layers around the plot

		for (int i = minx; i < maxx; i++) {
			dplace(1, i, 64, minz);
			dplace(1, i, 64, maxz);
			fullResetStepPillar(i, minz);
			fullResetStepPillar(i, maxz);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(1, minx, 64, i);
			dplace(1, maxx, 64, i);
			fullResetStepPillar(minx, i);
			fullResetStepPillar(minx, i);
		}
		minx--;
		minz--;
		maxx++;
		maxz++;
		for (int i = minx; i < maxx; i++) {
			dplace(1, i, 64, minz);
			dplace(1, i, 64, maxz);
			fullResetPillar(i, minz);
			fullResetPillar(i, maxz);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(56, minx, 64, i);
			dplace(56, maxx, 64, i);
			fullResetPillar(minx, i);
			fullResetPillar(maxx, i);
		}
		minx--;
		minz--;
		maxx++;
		maxz++;
		for (int i = minx; i < maxx; i++) {
			dplace(1, i, 64, minz);
			dplace(1, i, 64, maxz);
			fullResetPillar(i, minz);
			fullResetPillar(i, maxz);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(1, minx, 64, i);
			dplace(1, maxx, 64, i);
			fullResetPillar(minx, i);
			fullResetPillar(maxx, i);
		}
		minx--;
		minz--;
		maxx++;
		maxz++;
		for (int i = minx; i < maxx; i++) {
			dplace(1, i, 64, minz);
			dplace(1, i, 64, maxz);
			fullResetPillar(i, minz);
			fullResetPillar(i, maxz);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(56, minx, 64, i);
			dplace(56, maxx, 64, i);
			fullResetPillar(minx, i);
			fullResetPillar(maxx, i);
		}
		minx--;
		minz--;
		maxx++;
		maxz++;
		for (int i = minx; i < maxx; i++) {
			dplace(1, i, 64, minz);
			dplace(1, i, 64, maxz);
			fullResetStepPillar(i, minz);
			fullResetStepPillar(i, maxz);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(1, minx, 64, i);
			dplace(1, maxx, 64, i);
			fullResetStepPillar(minx, i);
			fullResetStepPillar(maxx, i);
		}
		// the stair blocks
		minx = bottomPlot.getBlockX() - 1;
		minz = bottomPlot.getBlockZ() - 1;
		maxx = topPlot.getBlockX() + 1;
		maxz = topPlot.getBlockZ() + 1;
		for (int i = minx; i < maxx; i++) {
			dplace(44, 7, i, 65, minz);
			dplace(44, 7, i, 65, maxz);
			dplace(44, 7, i, 65, minz - 4);
			dplace(44, 7, i, 65, maxz + 4);
		}
		for (int i = minz; i < maxz; i++) {
			dplace(44, 7, minx, 65, i);
			dplace(44, 7, maxx, 65, i);
			dplace(44, 7, minx - 4, 65, i);
			dplace(44, 7, maxx + 4, 65, i);
		}
		minx = bottomPlot.getBlockX() - 1;
		minz = bottomPlot.getBlockZ() - 1;
		maxx = topPlot.getBlockX() + 1;
		maxz = topPlot.getBlockZ() + 1;

		// Possible blocks in the pathway cleared encase its not there.

		dplace(0, minx - 1, 65, minz);
		dplace(0, minx - 2, 65, minz);
		dplace(0, minx - 3, 65, minz);
		dplace(0, minx, 65, minz - 1);
		dplace(0, minx, 65, minz - 2);
		dplace(0, minx, 65, minz - 3);
		dplace(44, 7, minx, 65, minz);

		dplace(0, maxx + 1, 65, maxz);
		dplace(0, maxx + 2, 65, maxz);
		dplace(0, maxx + 3, 65, maxz);
		dplace(0, maxx, 65, maxz + 1);
		dplace(0, maxx, 65, maxz + 2);
		dplace(0, maxx, 65, maxz + 3);
		dplace(44, 7, maxx, 65, maxz);

		dplace(0, minx - 1, 65, maxz);
		dplace(0, minx - 2, 65, maxz);
		dplace(0, minx - 3, 65, maxz);
		dplace(0, minx, 65, maxz + 1);
		dplace(0, minx, 65, maxz + 2);
		dplace(0, minx, 65, maxz + 3);
		dplace(44, 7, minx, 65, maxz);

		dplace(0, maxx + 1, 65, minz);
		dplace(0, maxx + 2, 65, minz);
		dplace(0, maxx + 3, 65, minz);
		dplace(0, maxx, 65, minz - 1);
		dplace(0, maxx, 65, minz - 2);
		dplace(0, maxx, 65, minz - 3);
		dplace(44, 7, maxx, 65, minz);

		minx = bottomPlot.getBlockX() - 3;
		minz = bottomPlot.getBlockZ() - 3;
		maxx = topPlot.getBlockX() + 3;
		maxz = topPlot.getBlockZ() + 3;

		setFloorStamp(minx, minz);
		setFloorStamp(minx, maxz);
		setFloorStamp(maxx, minz);
		setFloorStamp(maxx, maxz);

		minx = bottomPlot.getBlockX();
		minz = bottomPlot.getBlockZ();
		maxx = topPlot.getBlockX();
		maxz = topPlot.getBlockZ();

		// Check for open plots to close the walls for.

		Plot a = getPlot((plotx + 1) + ";" + (plotz + 1));
		Plot b = getPlot(plotx + ";" + (plotz + 1));
		Plot c = getPlot((plotx - 1) + ";" + (plotz + 1));
		Plot d = getPlot((plotx - 1) + ";" + plotz);
		Plot e = getPlot((plotx - 1) + ";" + (plotz - 1));
		Plot f = getPlot(plotx + ";" + (plotz - 1));
		Plot g = getPlot((plotx + 1) + ";" + (plotz - 1));
		Plot h = getPlot((plotx + 1) + ";" + plotz);
		if (a != null && b != null)
			if (a.owner.equals(b.owner)) {
				dplace(44, 7, maxx + 1, 65, maxz + 5);
				dplace(44, 7, maxx + 2, 65, maxz + 5);
				dplace(44, 7, maxx + 3, 65, maxz + 5);
				dplace(44, 7, maxx + 4, 65, maxz + 5);
				dplace(44, 7, maxx + 5, 65, maxz + 5);
			}
		if (b != null && c != null)
			if (b.owner.equals(c.owner)) {
				dplace(44, 7, minx - 2, 65, maxz + 5);
				dplace(44, 7, minx - 3, 65, maxz + 5);
				dplace(44, 7, minx - 4, 65, maxz + 5);
				dplace(44, 7, minx - 5, 65, maxz + 5);
			}
		if (c != null && d != null)
			if (c.owner.equals(d.owner)) {
				dplace(44, 7, minx - 5, 65, maxz + 1);
				dplace(44, 7, minx - 5, 65, maxz + 2);
				dplace(44, 7, minx - 5, 65, maxz + 3);
				dplace(44, 7, minx - 5, 65, maxz + 4);
				dplace(44, 7, minx - 5, 65, maxz + 5);
			}
		if (d != null && e != null)
			if (d.owner.equals(e.owner)) {
				dplace(44, 7, minx - 5, 65, minz - 2);
				dplace(44, 7, minx - 5, 65, minz - 3);
				dplace(44, 7, minx - 5, 65, minz - 4);
				dplace(44, 7, minx - 5, 65, minz - 5);
			}
		if (e != null && f != null)
			if (e.owner.equals(f.owner)) {
				dplace(44, 7, minx - 2, 65, minz - 5);
				dplace(44, 7, minx - 3, 65, minz - 5);
				dplace(44, 7, minx - 4, 65, minz - 5);
				dplace(44, 7, minx - 5, 65, minz - 5);
			}
		if (f != null && g != null)
			if (f.owner.equals(g.owner)) {
				dplace(44, 7, maxx + 1, 65, minz - 5);
				dplace(44, 7, maxx + 2, 65, minz - 5);
				dplace(44, 7, maxx + 3, 65, minz - 5);
				dplace(44, 7, maxx + 4, 65, minz - 5);
				dplace(44, 7, maxx + 5, 65, minz - 5);
			}
		if (g != null && h != null)
			if (g.owner.equals(h.owner)) {
				dplace(44, 7, maxx + 5, 65, minz - 2);
				dplace(44, 7, maxx + 5, 65, minz - 3);
				dplace(44, 7, maxx + 5, 65, minz - 4);
				dplace(44, 7, maxx + 5, 65, minz - 5);
			}
		if (h != null && a != null)
			if (h.owner.equals(a.owner)) {
				dplace(44, 7, maxx + 5, 65, maxz + 1);
				dplace(44, 7, maxx + 5, 65, maxz + 2);
				dplace(44, 7, maxx + 5, 65, maxz + 3);
				dplace(44, 7, maxx + 5, 65, maxz + 4);
				dplace(44, 7, maxx + 5, 65, maxz + 5);
			}
	}

	private static void setFloorStamp(int x, int z) {
		for (int tx = x - 2; tx <= x + 2; tx++) {
			for (int tz = z - 2; tz <= z + 2; tz++) {
				dplace(1, tx, 64, tz);
			}
		}
		dplace(56, x, 64, z);
		dplace(56, x + 1, 64, z);
		dplace(56, x - 1, 64, z);
		dplace(56, x, 64, z + 1);
		dplace(56, x, 64, z - 1);
	}

	private static void fullResetPillar(int x, int z) {
		for (int i = 0; i <= 63; i++)
			dplace(3, x, i, z);
		for (int i = 65; i <= 256; i++)
			dplace(0, x, i, z);
	}

	private static void fullResetStepPillar(int x, int z) {
		for (int i = 0; i <= 63; i++)
			dplace(3, x, i, z);
		for (int i = 66; i <= 256; i++)
			dplace(0, x, i, z);
	}

	private boolean inLegalArea(Location loc, Player player) {
		String id = PlotManager.getPlotId(loc);
		String xCheck1 = PlotManager.getPlotId(loc.clone().add(5, 0, 0));
		String xCheck2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 0));
		String yCheck1 = PlotManager.getPlotId(loc.clone().add(0, 0, 5));
		String yCheck2 = PlotManager.getPlotId(loc.clone().add(0, 0, -5));
		// log(xCheck1 + " : " + xCheck2 + " : " + yCheck1 + " : " + yCheck2);
		if (getPlot(id) != null && getPlot(id).isAllowed(player.getName()))
			return true;
		if (player.isOp())
			return true;
		String name = player.getName();
		if (!xCheck1.equals("") && !xCheck2.equals(""))
			if (getPlot(xCheck1) != null && getPlot(xCheck2) != null)
				if (getPlot(xCheck1).isAllowed(name)
						&& getPlot(xCheck2).isAllowed(name))
					return true;
		if (!yCheck1.equals("") && !yCheck2.equals(""))
			if (getPlot(yCheck1) != null && getPlot(yCheck2) != null)
				if (getPlot(yCheck1).isAllowed(name)
						&& getPlot(yCheck2).isAllowed(name))
					return true;
		if (checkCorners(loc, player))
			return true;

		return false;
	}

	private boolean checkCorners(Location loc, Player player) {
		String cor1 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
		String cor2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 5));
		String cor3 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
		String cor4 = PlotManager.getPlotId(loc.clone().add(-5, 0, -5));
		// log(cor1 + " : " + cor2 + " : " + cor3 + " : " + cor4);
		String name = player.getName();
		if (!cor1.equals("") && !cor2.equals("") && !cor3.equals("")
				&& !cor4.equals(""))
			if (getPlot(cor1) != null && getPlot(cor2) != null
					&& getPlot(cor3) != null && getPlot(cor4) != null)
				if (getPlot(cor1).isAllowed(name)
						&& getPlot(cor2).isAllowed(name)
						&& getPlot(cor3).isAllowed(name)
						&& getPlot(cor4).isAllowed(name))
					return true;
		return false;
	}

	private static Plot getPlot(String id) {
		return PlotManager.getPlotById(Bukkit.getWorld("Build"), id);
	}

	public static void command(String id) {
		regenWalls(PlotManager.getIdX(id), PlotManager.getIdZ(id));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (e.getPlayer() != null
				&& e.getPlayer().getWorld().getName().equals("Build")) {
			if (e.isCancelled()
					&& inLegalArea(e.getBlock().getLocation(), e.getPlayer()))
				e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBreak(BlockBreakEvent e) {
		if (e.getPlayer() != null
				&& e.getPlayer().getWorld().getName().equals("Build") && !e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE) && !e.getPlayer().getItemInHand().getType().equals(Material.COMPASS)) {
			if (e.isCancelled()
					&& inLegalArea(e.getBlock().getLocation(), e.getPlayer()))
				e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getClickedBlock() != null
				&& e.getPlayer().getWorld().getName().equals("Build")
				&& !e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE) && !e.getPlayer().getItemInHand().getType().equals(Material.COMPASS))
			if (e.isCancelled()
					&& inLegalArea(e.getClickedBlock().getLocation(),
							e.getPlayer()))
				e.setCancelled(false);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCommandSent(final PlayerCommandPreprocessEvent e) {
		
		if (e.getMessage().toLowerCase().startsWith("/plot")) {
			if (e.getPlayer().getWorld().getName().equals("Build")) {
				if (!e.getMessage().toLowerCase().startsWith("/plotme"))
					e.setMessage("/plotme" + e.getMessage().substring(5, e.getMessage().length()));
				
				final boolean tp;
				if (e.getMessage().toLowerCase().startsWith("/plotme auto")) {
					tp = true;
				} else {
					tp = false;
				}
				final Location playerLoc = e.getPlayer().getLocation();
				plugin.getServer().getScheduler()
						.scheduleAsyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								if (tp && e.getPlayer().getLocation() != playerLoc)
									e.getPlayer().teleport(
											e.getPlayer().getLocation().clone()
													.add(0, 0, 5));
								attemptConnection(e.getPlayer());
							}
						}, 5);
				if (e.getMessage().toLowerCase().startsWith("/plotme update")) {
					e.getPlayer().sendMessage(
							ChatColor.BLUE + "[PlotMe] " + ChatColor.WHITE
									+ "Plot updated relative to plots around it.");
					e.setCancelled(true);
				}
				if (e.getMessage().toLowerCase().startsWith("/plotme clear")) {
					if (e.getPlayer().hasPermission("plotme.admin")) {
						e.getPlayer().sendMessage(
								ChatColor.BLUE + "[PlotMe] " + ChatColor.WHITE
										+ "Plot resetting...");
						Bukkit.getServer().dispatchCommand(e.getPlayer(), "plotme claim");
						Bukkit.getServer().dispatchCommand(e.getPlayer(), "plotme clear");
						Bukkit.getServer().dispatchCommand(e.getPlayer(), "plotme dispose");
						e.setCancelled(true);
					}
				}
				if (e.getMessage().toLowerCase().startsWith("/plotme dispose")) {
					Bukkit.getServer().dispatchCommand(e.getPlayer(), "plotme clear");
				}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void BlockFromTo(BlockFromToEvent e) {
		if (e.isCancelled()) {
			Location loc = e.getToBlock().getLocation();
			if (loc.getWorld().getName().equals("Build")) {
//				String id = PlotManager.getPlotId(loc);
//				if (getPlot(id) != null)
//					return;
				String x1 = PlotManager.getPlotId(loc.clone().add(5, 0, 0));
				String x2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 0));
				String y1 = PlotManager.getPlotId(loc.clone().add(0, 0, 5));
				String y2 = PlotManager.getPlotId(loc.clone().add(0, 0, -5));
				boolean allowed = false;
				if (getPlot(x1) != null && getPlot(x2) != null)
					allowed = getPlot(x1).getOwner().equals(getPlot(x2).getOwner());
				if (!allowed && getPlot(y1) != null && getPlot(y2) != null)
					allowed = getPlot(y1).getOwner().equals(getPlot(y2).getOwner());
				if (allowed) {
					e.setCancelled(false);
					return;
				}
				String cor1 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 5));
				String cor3 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor4 = PlotManager.getPlotId(loc.clone().add(-5, 0, -5));
				if (getPlot(cor1) != null && getPlot(cor2) != null && getPlot(cor3) != null && getPlot(cor4) != null)
					if (getPlot(cor1).getOwner().equals(getPlot(cor2).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor3).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor4).getOwner())) {
						e.setCancelled(false);
					}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		if (e.isCancelled()) {
			Location loc = e.getBlockClicked().getLocation();
			if (loc.getWorld().getName().equals("Build")) {
//				String id = PlotManager.getPlotId(loc);
//				if (getPlot(id) != null)
//					return;
				String x1 = PlotManager.getPlotId(loc.clone().add(5, 0, 0));
				String x2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 0));
				String y1 = PlotManager.getPlotId(loc.clone().add(0, 0, 5));
				String y2 = PlotManager.getPlotId(loc.clone().add(0, 0, -5));
				boolean allowed = false;
				if (getPlot(x1) != null && getPlot(x2) != null)
					allowed = getPlot(x1).getOwner().equals(getPlot(x2).getOwner()) && getPlot(x1).isAllowed(e.getPlayer().getName());
				if (!allowed && getPlot(y1) != null && getPlot(y2) != null)
					allowed = getPlot(y1).getOwner().equals(getPlot(y2).getOwner()) && getPlot(y1).isAllowed(e.getPlayer().getName());
				if (allowed) {
					e.setCancelled(false);
					return;
				}
				String cor1 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 5));
				String cor3 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor4 = PlotManager.getPlotId(loc.clone().add(-5, 0, -5));
				if (getPlot(cor1) != null && getPlot(cor2) != null && getPlot(cor3) != null && getPlot(cor4) != null)
					if (getPlot(cor1).getOwner().equals(getPlot(cor2).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor3).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor4).getOwner())) {
						e.setCancelled(false);
					}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void onPistonExtendEvent(BlockPistonExtendEvent e) {
		if (e.isCancelled()) {
			List<Block> blocks = e.getBlocks();
			if (blocks.size() > 0 && blocks.get(0).getLocation().getWorld().getName().equals("Build"))
				for (int blocknum = 0; blocknum < blocks.size(); blocknum++) {
					Location loc = blocks.get(blocknum).getLocation();
					if (loc.getWorld().getName().equals("Build")) {
						String x1 = PlotManager.getPlotId(loc.clone().add(5, 0, 0));
						String x2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 0));
						String y1 = PlotManager.getPlotId(loc.clone().add(0, 0, 5));
						String y2 = PlotManager.getPlotId(loc.clone().add(0, 0, -5));
						boolean allowed = false;
						if (getPlot(x1) != null && getPlot(x2) != null)
							allowed = getPlot(x1).getOwner().equals(getPlot(x2).getOwner());
						if (!allowed && getPlot(y1) != null && getPlot(y2) != null)
							allowed = getPlot(y1).getOwner().equals(getPlot(y2).getOwner());
						if (allowed) {
							if (blocknum+1 != blocks.size()) {
								continue;
							} else {
								e.setCancelled(false);
								return;
							}
						} else {
							String cor1 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
							String cor2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 5));
							String cor3 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
							String cor4 = PlotManager.getPlotId(loc.clone().add(-5, 0, -5));
							if (getPlot(cor1) != null && getPlot(cor2) != null && getPlot(cor3) != null && getPlot(cor4) != null)
								if (getPlot(cor1).getOwner().equals(getPlot(cor2).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor3).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor4).getOwner())) {
									if (blocknum != blocks.size()) {
										continue;
									} else {
										e.setCancelled(false);
										return;
									}
								}
						}
					}
				}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST)
	public void onPistonRetractEvent(BlockPistonRetractEvent e) {
		if (e.isCancelled()) {
			Location loc = e.getBlock().getLocation();
			if (loc.getWorld().getName().equals("Build")) {
				String x1 = PlotManager.getPlotId(loc.clone().add(5, 0, 0));
				String x2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 0));
				String y1 = PlotManager.getPlotId(loc.clone().add(0, 0, 5));
				String y2 = PlotManager.getPlotId(loc.clone().add(0, 0, -5));
				boolean allowed = false;
				if (getPlot(x1) != null && getPlot(x2) != null)
					allowed = getPlot(x1).getOwner().equals(getPlot(x2).getOwner());
				if (!allowed && getPlot(y1) != null && getPlot(y2) != null)
					allowed = getPlot(y1).getOwner().equals(getPlot(y2).getOwner());
				if (allowed) {
					e.setCancelled(false);
					return;
				}
				String cor1 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor2 = PlotManager.getPlotId(loc.clone().add(-5, 0, 5));
				String cor3 = PlotManager.getPlotId(loc.clone().add(5, 0, 5));
				String cor4 = PlotManager.getPlotId(loc.clone().add(-5, 0, -5));
				if (getPlot(cor1) != null && getPlot(cor2) != null && getPlot(cor3) != null && getPlot(cor4) != null)
					if (getPlot(cor1).getOwner().equals(getPlot(cor2).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor3).getOwner()) && getPlot(cor1).getOwner().equals(getPlot(cor4).getOwner())) {
						e.setCancelled(false);
					}
			}
		}
	}


	private void log(String info) {
		plugin.getLogger().info("<PlotMeOverride> " + info);
	}
}
