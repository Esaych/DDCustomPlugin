package net.diamonddominion.esaych.global;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import net.diamonddominion.esaych.CustomPlugin;


public class HeartBeat {
	
	private CustomPlugin plugin;
	
	public HeartBeat(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public double tps = 20;
	boolean ping = true;

	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						long timestamp = System.currentTimeMillis() / 1000;
						ping = ping();
						try {
							writeFile("heartbeat.txt", timestamp + "\nDISABLED\n" + plugin.getServer().getOnlinePlayers().length + "\n" + ping);
						} catch (IOException e) {
							log("HeartBeat was not able to update time stamp!");
							log("Might receieve email notifications that server is down!");
						}
					}
				}, 500, 1200);
//		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
//
//					long sec;
//					long second = 0;
//					int ticks;
//
//					@Override
//					public void run() {
//						sec = (System.currentTimeMillis() / 1000);
//
//						if (second == sec) {
//							ticks++;
//						} else {
//							second = sec;
//							tps = ((tps + ticks) / 2);
//							ticks = 0;
//						}
//					}
//				}, 100, 1);

		log("Enabled");

	}
	
	public void disable() {
		long timestamp = (System.currentTimeMillis() / 1000) + 60;
		try {
			writeFile("heartbeat.txt", timestamp + "\n" + tps + "\n" + plugin.getServer().getOnlinePlayers().length + "\n" + ping);
		} catch (IOException e) {
			log("HeartBeat was not able to update time stamp!");
			log("Might receieve email notifications that server is down!");
		}
	}
	
//	@EventHandler(priority = EventPriority.HIGHEST)
//    public void onServerListPing(ServerListPingEvent event){
//        if(!ping)
//        	event.setMotd(ChatColor.AQUA + "DD" + ChatColor.RED + " IS DOWN, working on a fix.");
//    }

	public static void writeFile(String filename, String text) throws IOException {
	    FileOutputStream fos = null;
	    try {
	      fos = new FileOutputStream(filename);
	      fos.write(text.getBytes("UTF-8"));
	    } catch (IOException e) {
	      close(fos);
	      throw e;
	    }
	  }

	public static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException ignored) {
		}
	}
	
	public static boolean ping()
	{
		try {
			//make a URL to a known source
			URL url = new URL("http://www.google.com");

			//open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

			//trying to retrieve data from the source. If there
			//is no connection, this line will fail
			urlConnect.getContent();

		} catch (UnknownHostException e) {
			System.out.println("[HeartBeat] Connection Failure");
			return false;
		}
		catch (IOException e) {
			System.out.println("[HeartBeat] Connection Failure");
			return false;
		}
		return true;
	}
	
	public void log(String info) {
		plugin.getLogger().info("<HeartBeat> " + info);
	}
}
