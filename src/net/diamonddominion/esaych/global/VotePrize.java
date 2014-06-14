package net.diamonddominion.esaych.global;

import java.util.HashMap;
import java.util.Map;

public class VotePrize {

	public Map<String, String> prizes = new HashMap<String, String>();
	public static int prizeAmount = 8;
	public int prizeId;
	
	public VotePrize(int id) {
		prizeId = id;
	}
	
//	prizes.put("fly",      "Fly for %X minutes        ;1;5"); // DisplayName;Cost;Extra Var
//	prizes.put("god",      "Godmode for %X minutes    ;1;5");
//	prizes.put("creative", "Be Creative for %X minutes;3;5");
//	prizes.put("disguise", "Disguise for %X minutes   ;1;5");
//	prizes.put("vanish",   "Vanish up to %X minutes   ;1;5");
//	prizes.put("healing",  "Instant health healing    ;1;0");
//	prizes.put("food",     "Instant hunger replenish  ;1;0");
//	prizes.put("xp",       "Get a %X xp boost        ;1;60");
	
	public static boolean isValid(int id) {
		return id <= prizeAmount && id > 0;
	}
	
	public static String getName(int id) {
		switch (id) {
		case 1: return "Fly";
		case 2: return "God";
		case 3: return "Creative";
		case 4: return "Disguise";
		case 5: return "Vanish";
		case 6: return "Healing";
		case 7: return "Food";
		case 8: return "XP";
		}
		return null;
	}
	
	public static String getDescription(int id) {
		if (id != 6 && id != 7)
			return getTechnicalDescription(id).replaceAll("%X", getExtraVar(id) + "");
		return getTechnicalDescription(id).replaceAll("%X", getExtraVar(id)/2 + "");
	}
	
	public static int getCost(int id) {
		switch (id) {
		case 1: return 1;
		case 2: return 1;
		case 3: return 3;
		case 4: return 1;
		case 5: return 1;
		case 6: return 1;
		case 7: return 1;
		case 8: return 1;
		}
		return 0;
	}
	
	public static int getExtraVar(int id) {
		switch (id) {
		case 1: return 5;
		case 2: return 5;
		case 3: return 5;
		case 4: return 5;
		case 5: return 5;
		case 6: return 10;
		case 7: return 10;
		case 8: return 60;
		}
		return 0;
	}
	
	public static String getTechnicalDescription(int id) {
		switch (id) {
		case 1: return "Fly for %X minutes";
		case 2: return "Godmode for %X minutes";
		case 3: return "Be Creative for %X minutes";
		case 4: return "Disguise for %X minutes";
		case 5: return "Vanish up to %X minutes";
		case 6: return "Heal %X hearts";
		case 7: return "Feed %X hunger levels";
		case 8: return "Get a %X xp boost";
		}
		return null;
	}
}
