package net.diamonddominion.esaych;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.diamonddominion.esaych.creative.CreativeEntityRemover;
import net.diamonddominion.esaych.creative.PlotMeOverride;
import net.diamonddominion.esaych.destruction.TnTPunch;
import net.diamonddominion.esaych.events.EWBlockFenceWalk;
import net.diamonddominion.esaych.events.EWButtons;
import net.diamonddominion.esaych.global.BeastSprint;
import net.diamonddominion.esaych.global.BungeeGlobalCommand;
import net.diamonddominion.esaych.global.Chat;
import net.diamonddominion.esaych.global.ChatPrefix;
import net.diamonddominion.esaych.global.ColorArmor;
import net.diamonddominion.esaych.global.CommandModifier;
import net.diamonddominion.esaych.global.CrashPlayer;
import net.diamonddominion.esaych.global.CustomRecipies;
import net.diamonddominion.esaych.global.FullMute;
import net.diamonddominion.esaych.global.HeartBeat;
import net.diamonddominion.esaych.global.Homes;
import net.diamonddominion.esaych.global.HubPortals;
import net.diamonddominion.esaych.global.LaunchPad;
import net.diamonddominion.esaych.global.Nameless;
import net.diamonddominion.esaych.global.RespectedAutoPromote;
import net.diamonddominion.esaych.global.ServerRestart;
import net.diamonddominion.esaych.global.SignEdit;
import net.diamonddominion.esaych.global.SpawnerSwitch;
import net.diamonddominion.esaych.global.Trampoline;
import net.diamonddominion.esaych.global.Voodoo;
import net.diamonddominion.esaych.global.VoteRewards;
import net.diamonddominion.esaych.skyblock.SkyBlockPortalReward;
import net.diamonddominion.esaych.skyblock.SkyBlockTP;
import net.diamonddominion.esaych.survival.DemiGodPackage;
import net.diamonddominion.esaych.survival.DirectionBook;
import net.diamonddominion.esaych.survival.DonorShop;
import net.diamonddominion.esaych.survival.EndDragonRespawn;
import net.diamonddominion.esaych.survival.EnderEggCollection;
import net.diamonddominion.esaych.survival.ExtraMiningLoot;
import net.diamonddominion.esaych.survival.FairFly;
import net.diamonddominion.esaych.survival.FairGod;
import net.diamonddominion.esaych.survival.ModAppWall;
import net.diamonddominion.esaych.survival.ModListWall;
import net.diamonddominion.esaych.survival.SpawnMusic;
import net.diamonddominion.esaych.survival.StaffVotes;
import net.diamonddominion.esaych.survival.TopPvPers;
import net.diamonddominion.esaych.survival.VoteReceiver;
import net.diamonddominion.esaych.survival.WarpWild;
import net.diamonddominion.esaych.survival.WelcomeBook;
import net.diamonddominion.esaych.survival.WelcomeSign;
import net.diamonddominion.esaych.unused.AutoConfiscate;
import net.diamonddominion.esaych.unused.GameTime;
import net.diamonddominion.esaych.unused.IpRecorder;
import net.diamonddominion.esaych.unused.RestartWarning;
import net.diamonddominion.esaych.unused.UnderCover;
import net.diamonddominion.esaych.unused.VirtualChest;
import net.diamonddominion.esaych.util.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;



/**
 * Class, that loads all custom plugins, 
 * send commands and events to all custom plugins, 
 * and disables them and clears all tasks.
 */

public class CustomPlugin extends JavaPlugin implements Listener, PluginMessageListener {

	//	private CustomPlugin plugin;
	//	
	//	public ___(CustomPlugin plugin) {
	//		this.plugin = plugin;
	//	}
	//	
	//	public void enable() {
	//		log("Enabled");
	//	}
	//	
	//	public void log(String info) {
	//		plugin.getLogger().info("<___> " + info);
	//	}
	
	//	public DrunkenWizard drunkenWizard = new DrunkenWizard(this);
	//	public JailSigns jailSigns = new JailSigns(this);
//	public LegitWarzone legitWarzone;
	//	public PeacefulGriefFree peacefulGriefFree = new PeacefulGriefFree(this);
	//	public TabNameFix tabNameFix = new TabNameFix(this);
	public AutoConfiscate autoConfiscate;
    public BeastSprint beastSprint;
    public Chat chat;
    public ChatPrefix chatPrefix;
    public ColorArmor colorArmor;
    public CommandModifier commandModifier;
    public CrashPlayer crashPlayer;
    public CreativeEntityRemover creativeEntityRemover;
    public CustomRecipies customRecipies;
    public DemiGodPackage demiGod;
    public DirectionBook directionBook;
    public DonorShop donorShop;
    public EndDragonRespawn endDragonRespawn;
    public EnderEggCollection enderEggCollection;
    public EWBlockFenceWalk ewBlockFenceWalk;
    public EWButtons ewButtons;
    public ExtraMiningLoot extraMiningLoot;
    public FairGod fairGod;
//    public ForumActivation forumActivation;
    public FullMute fullMute;
    public GameTime gameTime;
    public HeartBeat heartBeat;
    public HubPortals hubPortals;
    public IpRecorder ipRecorder;
    public LaunchPad launchPad;
    public ModAppWall modAppWall;
    public ModListWall modListWall;
    public Nameless nameless;
    public PlotMeOverride plotMeOverride;
    public RestartWarning restartWarning;
    public SignEdit signEdit;
    public SkyBlockPortalReward sbPortalReward;
    public SkyBlockTP skyBlockTP;
    public SpawnerSwitch spawnerSwitch;
    public SpawnMusic spawnMusic;
    public TnTPunch tntpunch;
    public TopPvPers topPvPers;
    public Trampoline trampoline;
    public UnderCover underCover;
    public VirtualChest virtualChest;
    public Voodoo voodoo;
    public VoteRewards voteRewards;
    public VoteReceiver voteReceiver;
    public WarpWild warpWild;
//    public WebRanks webRanks;
    public WelcomeBook welcomeBook;
    public WelcomeSign welcomeSign;
//    public WorldBlocks worldBlocks;
    public BungeeGlobalCommand bungeeGBCMD;
//    public BungeeProxy bungeeProxy = new BungeeProxy();
//    public ProxyServer proxy = bungeeProxy.getProxy();
    public StaffVotes staffVotes;
    public RespectedAutoPromote respectedAutoPromote;
    public Homes homes;
    public FairFly fly;
    public ServerRestart serverRestart;


    
    /**
     * Plugin onEnable() event, enables all custom plugins that are on the current server
     */
	public void onEnable() {
		log("Diamond Dominion Custom Plugin ENABLING!");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		String server = detectedServer();
		log("Detected Server: " + server);
		
		DataManager dm = new DataManager();
		dm.reloaddata();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex set default group Player");
		log("Loading Custom Items:");
		/*
		 * Start Loading all Custom Plugin Classes
		 */
		if (shouldEnable("EndDragonRespawn")) {
			log(" - EndDragonRespawn:");
			try {
				endDragonRespawn = new EndDragonRespawn(this);
				endDragonRespawn.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		}

		if (shouldEnable("HeartBeat"))
			try {
				heartBeat = new HeartBeat(this);
				heartBeat.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("FairGod"))
			try {
				fairGod = new FairGod(this);
				fairGod.enable();
			} catch (Exception e) {
				pluginFailed();
			}

//		if (shouldEnable("LegitWarzone"))
//			try {
//				legitWarzone = new LegitWarzone(this);
//				legitWarzone.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

		if (shouldEnable("TopPvPers"))
			try {
				topPvPers = new TopPvPers(this);
				topPvPers.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("VoteRewards"))
			try {
				voteRewards = new VoteRewards(this);
				voteRewards.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("VoteReceiver"))
			try {
				voteReceiver = new VoteReceiver(this);
				voteReceiver.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("DemiGodPackage"))
			try {
				demiGod = new DemiGodPackage(this);
				demiGod.enable();
			} catch (Exception e) {
				pluginFailed();
			}

//		if (shouldEnable("DirectionBook"))
//			try {
//				directionBook = new DirectionBook(this);
//				directionBook.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

//		if (shouldEnable("GameTime"))
//			try {
//				gameTime = new GameTime(this);
//				gameTime.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

		if (shouldEnable("CustomRecipies"))
			try {
				customRecipies = new CustomRecipies(this);
				customRecipies.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("SpawnerSwitch"))
			try {
				spawnerSwitch = new SpawnerSwitch(this);
				spawnerSwitch.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		//		if (shouldEnable("JailSigns"))
		//		jailSigns.enable();

		//		if (shouldEnable("AutoConfiscate"))
		//		autoConfiscate.enable();

		if (shouldEnable("ModAppWall"))
			try {
				modAppWall = new ModAppWall(this);
				modAppWall.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("ModListWall"))
			try {
				modListWall = new ModListWall(this);
				modListWall.enable();
			} catch (Exception e) {
				pluginFailed();
			}

//		if (shouldEnable("WebRanks"))
//			try {
//				webRanks = new WebRanks(this);
//				webRanks.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

		if (shouldEnable("Trampoline"))
			try {
				trampoline = new Trampoline(this);
				trampoline.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("PlotMeOverride"))
			try {
				plotMeOverride = new PlotMeOverride(this);
				plotMeOverride.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("Chat"))
			try {
				chat = new Chat(this);
				chat.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("ChatPrefix"))
			try {
				chatPrefix = new ChatPrefix(this);
				chatPrefix.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("FullMute"))
			try {
				fullMute = new FullMute(this);
				fullMute.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("LaunchPad"))
			try {
				launchPad = new LaunchPad(this);
				launchPad.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("ColorArmor"))
			try {
				colorArmor = new ColorArmor(this);
				colorArmor.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		//		if (shouldEnable("DrunkenWizard"))
		//		drunkenWizard.enable();

		if (shouldEnable("CreativeEntityRemover"))
			try {
				creativeEntityRemover = new CreativeEntityRemover(this);
				creativeEntityRemover.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("WelcomeBook"))
			try {
				welcomeBook = new WelcomeBook(this);
				welcomeBook.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("IpRecorder"))
			try {
				ipRecorder = new IpRecorder(this);
				ipRecorder.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("WelcomeSign"))
			try {
				welcomeSign = new WelcomeSign(this);
				welcomeSign.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		//		if (shouldEnable("PeacefulGriefFree"))
		//		peacefulGriefFree.enable();

//		if (shouldEnable("ForumActivation"))
//			try {
//				forumActivation = new ForumActivation(this);
//				forumActivation.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

		if (shouldEnable("VirtualChest"))
			try {
				virtualChest = new VirtualChest(this);
				virtualChest.enable();
			} catch (Exception e) {
				pluginFailed();
			}

//		if (shouldEnable("WorldBlocks"))
//			try {
//				worldBlocks = new WorldBlocks(this);
//				worldBlocks.enable();
//			} catch (Exception e) {
//				pluginFailed();
//			}

		if (shouldEnable("DonorShop"))
			try {
				donorShop = new DonorShop(this);
				donorShop.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("SpawnMusic"))
			try {
				spawnMusic = new SpawnMusic(this);
				spawnMusic.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("CrashPlayer"))
			try {
				crashPlayer = new CrashPlayer(this);
				crashPlayer.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("BeastSprint"))
			try {
				beastSprint = new BeastSprint(this);
				beastSprint.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("Nameless"))
			try {
				nameless = new Nameless(this);
				nameless.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("TnTPunch"))
			try {
				tntpunch = new TnTPunch(this);
				tntpunch.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("RestartWarning"))
			try {
				restartWarning = new RestartWarning(this);
				restartWarning.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("UnderCover"))
			try {
				underCover = new UnderCover(this);
				underCover.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("SignEdit"))
			try {
				signEdit = new SignEdit(this);
				signEdit.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("WarpWild"))
			try {
				warpWild = new WarpWild(this);
				warpWild.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("EWButtons"))
			try {
				ewButtons = new EWButtons(this);
				ewButtons.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("SkyBlockTP"))
			try {
				skyBlockTP = new SkyBlockTP(this);
				skyBlockTP.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("EnderEggCollection"))
			try {
				enderEggCollection = new EnderEggCollection(this);
				enderEggCollection.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("Voodoo"))
			try {
				voodoo = new Voodoo(this);
				voodoo.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("ExtraMiningLoot"))
			try {
				extraMiningLoot = new ExtraMiningLoot(this);
				extraMiningLoot.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("EWBlockFenceWalk"))
			try {
				ewBlockFenceWalk = new EWBlockFenceWalk(this);
				ewBlockFenceWalk.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("CommandModifier"))
			try {
				commandModifier = new CommandModifier(this);
				commandModifier.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("SkyBlockPortalReward"))
			try {
				sbPortalReward = new SkyBlockPortalReward(this);
				sbPortalReward.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("HubPortals"))
			try {
				hubPortals = new HubPortals(this);
				hubPortals.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("BungeeGlobalCommand"))
			try {
				bungeeGBCMD = new BungeeGlobalCommand(this);
				bungeeGBCMD.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("StaffVotes"))
			try {
				staffVotes = new StaffVotes(this);
				staffVotes.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		if (shouldEnable("RespectedAutoPromote"))
			try {
				respectedAutoPromote = new RespectedAutoPromote(this);
				respectedAutoPromote.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("Homes"))
			try {
				homes = new Homes(this);
				homes.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("FairFly"))
			try {
				fly = new FairFly(this);
				fly.enable();
			} catch (Exception e) {
				pluginFailed();
			}
		
		if (shouldEnable("ServerRestart"))
			try {
				serverRestart = new ServerRestart(this);
				serverRestart.enable();
			} catch (Exception e) {
				pluginFailed();
			}

		/*
		 * Complete loading all custom plugin classes
		 */
		log("Fully Enabled!");

	}
	
	/**
	 * Plugin onDisable() event, disables all plugins that needs disabling, and cancels all tasks
	 */
	public void onDisable() {
		heartBeat.disable();
//		worldBlocks.disable();
		
		if (donorShop != null)
		donorShop.disable();
		if (demiGod != null)
		demiGod.disable();
		
		for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
			if (task.getOwner() == this) {
				task.cancel();
			}
		}
		DataManager dm = new DataManager();
		dm.cpsavedata();
		getLogger().info("Diamond Dominion Custom Plugin disabled");
	}
	
	/**
	 * This method checks if the plugin should be enabling on this server
	 */
	public boolean shouldEnable(String pluginName) {
		String server = detectedServer();
//		if (server.equals("test server") || server.contains("multicraft")) {
//			log(" - " + pluginName);
//			return true;
//		}
		List<String> classes = new ArrayList<String>();
		try {
			classes = getClasses();
		} catch (IOException e) {
			log("Couldn't find custom plugin file");
			return true;
//			e.printStackTrace();
		}
		if (classes.contains("net.diamonddominion.esaych." + server + "." + pluginName)
				|| classes.contains("net.diamonddominion.esaych.global." + pluginName)) {
			log(" - " + pluginName);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets all classes in the CustomPlugin jar file and returns a list
	 */
	List<String> zipClasses = new ArrayList<String>();
	private List<String> getClasses() throws IOException {
		if (zipClasses.size() != 0) {
			return zipClasses;
		}
		List<String> classNames = new ArrayList<String>();
		ZipInputStream zip = null;

		try {
			zip = new ZipInputStream(new FileInputStream("plugins/DDCustomPlugin.jar"));
		} catch (FileNotFoundException e) {
			throw new IOException();
		}
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry())
			if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
				// This ZipEntry represents a class. Now, what class does it represent?
				StringBuilder className = new StringBuilder();
				for (String part : entry.getName().split("/")) {
					if (className.length() != 0)
						className.append(".");
					className.append(part);
					if (part.endsWith(".class"))
						className.setLength(className.length() - ".class".length());
				}
				if (className.toString().indexOf("$") == -1)
					classNames.add(className.toString());
			}
		zipClasses = classNames;
		return classNames;
	}
	
	/**
	 * Returns the detected server that is on the dedicated environment
	 */
	public String detectedServer() {
		String path = getDataFolder().getAbsolutePath();
		String server = path.substring(6, path.length()-23);
		return server.toLowerCase();
	}

	/**
	 * Logs for the messages above
	 * Possibly will be removed after the plugin scheme
	 */
	public void log(String log) {
		getLogger().info(log);
	}

	/**
	 * This is the message written when a plugin fails to load
	 */
	public void pluginFailed() {
		Bukkit.getLogger().warning("Plugin failed to load");
	}

	/**
	 * Plugin onCommand() event, associates all plugins that have commands
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (commandLabel.equalsIgnoreCase("god") && fairGod != null)
			return fairGod.godCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("creative") && demiGod != null)
			return demiGod.creativeCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("survival") && demiGod != null)
			return demiGod.survivalCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("aconf") && autoConfiscate != null)
			return autoConfiscate.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("delprefix") && chatPrefix != null)
			return chatPrefix.delPrefix(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("fullprefix") && chatPrefix != null)
			return chatPrefix.fullPrefix(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("prefix") && chatPrefix != null)
			return chatPrefix.onCommand(sender, cmd, commandLabel, args);
		if ((commandLabel.equalsIgnoreCase("msg")
				|| commandLabel.equalsIgnoreCase("m")
				|| commandLabel.equalsIgnoreCase("message")
				|| commandLabel.equalsIgnoreCase("tell")
				|| commandLabel.equalsIgnoreCase("whisper")
				|| commandLabel.equalsIgnoreCase("t")) && chat != null)
			return chat.msgCommand(sender, cmd, commandLabel, args);
		if ((commandLabel.equalsIgnoreCase("r")
				|| commandLabel.equalsIgnoreCase("reply")) && chat != null)
			return chat.rCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("channel")
				|| commandLabel.equalsIgnoreCase("ch")
				|| commandLabel.equalsIgnoreCase("ac")
				|| commandLabel.equalsIgnoreCase("sc") && chat != null)
			return chat.chCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("date") && chat != null)
			return chat.dateCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("dump") && chat != null)
			return chat.dumpCommand(sender, cmd, commandLabel, args);
		if ((commandLabel.equalsIgnoreCase("mute")
				|| commandLabel.equalsIgnoreCase("me")) && chat != null)
			return fullMute.onCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("ignore") || commandLabel.equalsIgnoreCase("unignore"))
			return chat.ignoreCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("vote"))
			return voteRewards.onCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("toggleprofile") && chat != null)
			return chat.badProfileCommand(sender, cmd, commandLabel, args);
		// if (commandLabel.equalsIgnoreCase("peaceful"))
		// return peacefulGriefFree.onCommand(sender, cmd, commandLabel, args);
		// if (commandLabel.equalsIgnoreCase("virtualchest") ||
		// commandLabel.equalsIgnoreCase("vchest") ||
		// commandLabel.equalsIgnoreCase("vc"))
		// return virtualChest.onVCCommand(sender, args);
//		if (commandLabel.equalsIgnoreCase("world")
//				|| commandLabel.equalsIgnoreCase("w"))
//			return worldBlocks.onWorldCommand(sender);
		if (commandLabel.equalsIgnoreCase("spy") && chat != null)
			return chat.spyCommand(sender);
		if (commandLabel.equals("plotmeoverride") && plotMeOverride != null)
			if (!(sender instanceof Player)) {
				PlotMeOverride.command(args[0]);
				return true;
			}
		if (commandLabel.equalsIgnoreCase("crash"))
			return crashPlayer.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("sprint"))
			return beastSprint.onCommand(sender);
		if (commandLabel.equalsIgnoreCase("nameless"))
			return nameless.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("tntpunch") && tntpunch != null)
			return tntpunch.onCommand(sender);
		if (commandLabel.equalsIgnoreCase("undercover"))
			return underCover.onCommand(sender);
		if (commandLabel.equalsIgnoreCase("edit"))
			return signEdit.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("gcmd"))
			return bungeeGBCMD.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("staffvote") || commandLabel.equalsIgnoreCase("svote") && staffVotes != null)
			return staffVotes.onCommand(sender, args);
		if (commandLabel.equalsIgnoreCase("home") || commandLabel.equalsIgnoreCase("homes") || commandLabel.equalsIgnoreCase("sethome") || commandLabel.equalsIgnoreCase("delhome"))
			return homes.onCommand(sender, commandLabel, args);
		if (commandLabel.equalsIgnoreCase("servertime") || commandLabel.equalsIgnoreCase("stime"))
			return respectedAutoPromote.onCommand(sender);
		if (commandLabel.equalsIgnoreCase("fly"))
			if (fly != null)
				return fly.onCommand(sender, cmd, commandLabel, args);
		if (commandLabel.equals("ddstop"))
			return serverRestart.onRestartCommand(sender);
		if (commandLabel.equals("ddwarn"))
			return serverRestart.onWarnCommand(sender);
		return false;
	}

	/*
	Part of the owner login process
	*/
	private ItemStack colorize(ItemStack i, int r, int g, int b)
	{
		if (i != null) {
			String t = i.getType().name().split("_")[0];
			if (t.equals("LEATHER"))
			{
				LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
				lam.setColor(Color.fromRGB(r, g, b));
				i.setItemMeta(lam);
			}
//			i.addUnsafeEnchantment(Enchantment.THORNS, 5000);
//			i.addUnsafeEnchantment(Enchantment.DURABILITY, 5000);
//			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5000);
//			i.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5000);
//			i.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 5000);
//			i.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 5000);
			
			i.addUnsafeEnchantment(Enchantment.THORNS, 3);
			i.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 10);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 5);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 10);
			i.setDurability((short)0);
		}
		return i;
	}
	
	
	
	/*
	 * This if for custom parts that need it:
	 */
	public boolean isGod(Player player) {
		if (fairGod != null)
			return fairGod.isGod(player);
		else
			return false;
	}
	
	/*
	 * This is for custom parts that need it:
	 */
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
	public static String serverName; // Example: using the GetServer subchannel
	
	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equalsIgnoreCase("BungeeCord")) {
			return;
		}
		bungeeGBCMD.onBungeeMessageReceived(channel, player, message);
		chat.onBungeeMessageReceived(channel, player, message);
		homes.onBungeeMessageReceived(channel, player, message);
    }

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		/*
		This exploded players when an admin killed them:
		
		if (event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player)
			if (event.getEntity().getKiller().hasPermission("customplugin.megakill")) {
				Location loc = event.getEntity().getLocation();
				event.getEntity().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), (float) 3, false, false);
			}
		*/
		spawnerSwitch.onEntityDeath(event);
		if (topPvPers != null)
			topPvPers.onPlayerDeathEvent(event);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
//		ipRecorder.onPlayerJoinEvent(event);
//		forumActivation.onPlayerJoinEvent(event);
		respectedAutoPromote.onPlayerJoinEvent(event);
		homes.onJoinEvent(event);
		hubPortals.onJoinEvent(event);
		/*
		This allowed the owner when joining the game to get full epic armor
		*/
		if (event.getPlayer().getName().equals("Esaych")) {
			PlayerInventory inv = event.getPlayer().getInventory();
			colorize(inv.getHelmet(), 47, 0, 111);
			colorize(inv.getChestplate(), 47, 0, 111);
			colorize(inv.getLeggings(), 47, 0, 111);
			colorize(inv.getBoots(), 47, 0, 111);
		}
		if (staffVotes != null) {
			staffVotes.onPlayerJoinEvent(event);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {

		trampoline.onPlayerInteract(event);
//		worldBlocks.onPlayerInteract(event);
		voodoo.onPlayerInteract(event);
		launchPad.onPlayerInteractEvent(event);
//		legitWarzone.onClick(event);
		spawnerSwitch.onPlayerPunch(event);
		
		if (ewButtons != null)
			ewButtons.onPlayerInteract(event);
		if (skyBlockTP != null)
			skyBlockTP.onPlayerInteract(event);
		if (enderEggCollection != null)
			enderEggCollection.onPlayerInteract(event);
		if (topPvPers != null)
			topPvPers.onPlayerInteractEvent(event);
		if (donorShop != null)
			donorShop.onPlayerPunchBlock(event);
		if (tntpunch != null)
			tntpunch.onPlayerInteract(event);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		trampoline.onEntityDamage(event);
		launchPad.onPlayerDamage(event);
//		worldBlocks.onPlayerTakeDamage(event);

		if (fairGod != null)
			fairGod.onEntityDamage(event);
		if (fly != null)
			fly.onDamage(event);
		hubPortals.onPlayerFallEvent(event);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if (extraMiningLoot != null)
			extraMiningLoot.onPlayerBreakBlock(e);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (extraMiningLoot != null)
			extraMiningLoot.onPlayerPlaceBlock(e);
	}


	/*
	Double Jumping code:
	
	public ArrayList<Player> doubleJumping = new ArrayList<Player>();
	*/
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		/*
		Double Jumping code:
		
		if (!player.getAllowFlight() && ((player.hasPermission("customplugin.doublejump") || Board.getFactionAt(loc).isSafeZone())) && (player.getWorld().getName().equals("Survival") || player.getWorld().getName().equals("GriefFree"))) {
			if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && player.getLocation().getY() - player.getLocation().getBlockY() < .1) {
				event.getPlayer().setAllowFlight(true);
				doubleJumping.add(event.getPlayer());
			}
		}
		*/
		beastSprint.onPlayerMoveEvent(event);
		hubPortals.onPlayerMoveEvent(event);
//		worldBlocks.onPlayerMove(event);
		
		if (ewBlockFenceWalk != null)
			ewBlockFenceWalk.onPlayerMove(event);
//		if (legitWarzone != null)
//			legitWarzone.onPlayerMoves(event);
		if (donorShop != null)
			donorShop.onPlayerMoveEvent(event);
	}

	/*
	Double Jump code:
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (doubleJumping.contains(player) && !player.getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setVelocity(player.getLocation().getDirection().setY(1));
			doubleJumping.remove(player);
		}
	}
	*/
	
	/*
	Peaceful GriefFree Code:
	*/
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
//		peacefulGriefFree.onPlayerCloseInventoryEvent(event);
		if (staffVotes != null)
			staffVotes.onInventoryClose(event);
	}
	

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
//		worldBlocks.onPlayerQuit(event);
		if (donorShop != null)
			donorShop.onPlayerQuitEvent(event);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (directionBook != null)
			directionBook.onPlayerPickupBook(event);
	}

	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		if (directionBook != null)
			directionBook.onDropsDespawn(event);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
//		worldBlocks.onPlayerDropItem(event);
		if (directionBook != null)
			directionBook.onPlayerDropItem(event);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		/*
		Extra unused Code for removing Survival Games Queue removal and removing vanish from eventworld and stuff:
		
		String world = event.getPlayer().getWorld().getName();
		if (!world.equals("GameWorld") && !world.equals("Survival") && !world.equals("GriefFree") && !world.equals("End") && !world.equals("Nether")) {
			ArrayList<Game> games = GameManager.getInstance().getGames();
			for (Game game : games) {
				if (game != null && game.isInQueue(event.getPlayer())) {
					game.removeFromQueue(event.getPlayer());
					event.getPlayer().sendMessage(ChatColor.RED  + "You have been removed from the Survival Games queue.");
				}
			}
		}
		if (!world.equals("GameWorld")) {
			if (DisguiseCraft.getAPI().isDisguised(event.getPlayer())) {
				Bukkit.dispatchCommand(event.getPlayer(), "ud");
			}
			try {
				if (VanishNoPacket.isVanished(event.getPlayer().getName())) {
					Bukkit.dispatchCommand(event.getPlayer(), "v");
				}
			} catch (VanishNotLoadedException e) {}
		}
		
		event.getPlayer().setFlying(false);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sudo " + event.getPlayer().getName() + " sg leave");
		
		*/
		if (directionBook != null)
			directionBook.onPlayerChangeWorld(event);
		if (endDragonRespawn != null)
			endDragonRespawn.onPlayerGoesToEnd(event);
	}

	@EventHandler
	public void onEntityCreatesPortal(EntityCreatePortalEvent event)
	{
		if (endDragonRespawn != null)
			endDragonRespawn.onDragonCreatesPortal(event);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (fairGod != null)
			fairGod.onEntityDamageByEntity(event);
		if (fly != null)
			fly.onHit(event);
//		if (legitWarzone != null)
//			legitWarzone.onEntityDamageByEntity(event);
		//DISABLED BECAUSE WITHER CODE IS DISABLED
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		if (fairGod != null)
			fairGod.onShoot(event);
		if (fly != null)
			fly.onShoot(event);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		commandModifier.onCommandPreprocess(event);
		
		fullMute.onCommandPreProcess(event);
//		if (legitWarzone != null)
//			legitWarzone.onPlayerSendsCommand(event);
		if (welcomeBook != null)
			welcomeBook.onCommandPreprocess(event);
		if (warpWild != null)
			warpWild.onCommandPreprocess(event);
	}

	/*
	LegitWarzone code:
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		legitWarzone.somebodyMentionsWither(event);
	}
	 */
	
//	@EventHandler
//	public void onPotionSplash(PotionSplashEvent event) {
//		if (legitWarzone != null)
//			legitWarzone.onSplashPotionThrown(event);
//	}

//	@EventHandler
//	public void onPlayerDeath(PlayerDeathEvent event) {
//		if (legitWarzone != null)
//			legitWarzone.onPlayerDies(event);
//	}
//	

	@EventHandler
	public void EntityPortalEnter(EntityPortalEnterEvent event) {
		if (sbPortalReward != null)
			sbPortalReward.EntityPortalEnter(event);
	}

	@EventHandler
	public void PlayerTeleportEvent(final PlayerTeleportEvent event) {
//		worldBlocks.onPlayerTeleport(event);
		
		if (donorShop != null)
			donorShop.onPlayerTeleportEvent(event);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (donorShop != null)
			donorShop.onTntExplodeEvent(event);
	}

	
	@EventHandler
	public void PlayerToggleSneak(PlayerToggleSneakEvent event) {
		if (donorShop != null)
			donorShop.onPlayerSneaks(event);
//		drunkenWizard.onPlayerSneaks(event);
	}
	
	@EventHandler
	public void onPlayerInventoryClick(InventoryClickEvent event) {
		if (staffVotes != null)
			staffVotes.onInventoryClick(event);
	}
	
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (event.getPlayer().getName().equals("Esaych")) {
			PlayerInventory eInv = event.getPlayer().getInventory();
			if (eInv.getHelmet() != null)
				return;
			if (eInv.getChestplate() != null)
				return;
			if (eInv.getLeggings() != null)
				return;
			if (eInv.getBoots() != null)
				return;
			eInv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
			eInv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			eInv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			eInv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
			PlayerInventory inv = event.getPlayer().getInventory();
			colorize(inv.getHelmet(), 47, 0, 111);
			colorize(inv.getChestplate(), 47, 0, 111);
			colorize(inv.getLeggings(), 47, 0, 111);
			colorize(inv.getBoots(), 47, 0, 111);
		}
	}

}
