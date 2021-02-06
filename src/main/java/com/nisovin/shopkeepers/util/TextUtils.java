package com.nisovin.shopkeepers.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.api.util.ChunkCoords;
import com.nisovin.shopkeepers.spigot.text.SpigotText;
import com.nisovin.shopkeepers.text.Text;

/**
 * Text and messaging utilities.
 * <p>
 * In contrast to {@link StringUtils}, this contains utilities that are more minecraft and messaging specific.
 */
public class TextUtils {

	private TextUtils() {
	}

	/*
	 * PLAIN TEXT
	 */

	// FORMATTING AND CONVERSION

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));
	public static final DecimalFormat DECIMAL_FORMAT_PRECISE = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));
	static {
		DECIMAL_FORMAT.setGroupingUsed(false);
		DECIMAL_FORMAT_PRECISE.setGroupingUsed(false);
	}

	public static String getLocationString(Location location) {
		return getLocationString(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

	public static String getLocationString(Block block) {
		return getLocationString(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
	}

	// More performant variant if coordinates are integers:
	public static String getLocationString(String worldName, int x, int y, int z) {
		return worldName + "," + x + "," + y + "," + z;
	}

	public static String getLocationString(String worldName, double x, double y, double z) {
		return worldName + "," + DECIMAL_FORMAT.format(x) + "," + DECIMAL_FORMAT.format(y) + "," + DECIMAL_FORMAT.format(z);
	}

	public static String getChunkString(ChunkCoords chunk) {
		return getChunkString(chunk.getWorldName(), chunk.getChunkX(), chunk.getChunkZ());
	}

	public static String getChunkString(Chunk chunk) {
		return getChunkString(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}

	public static String getChunkString(String worldName, int cx, int cz) {
		return worldName + "," + cx + "," + cz;
	}

	public static String getPlayerString(Player player) {
		return getPlayerString(player.getName(), player.getUniqueId());
	}

	public static String getPlayerString(String playerName, UUID playerUUID) {
		// Either of them might be null.
		if (playerName != null) {
			return playerName + (playerUUID == null ? "" : " (" + playerUUID.toString() + ")");
		} else if (playerUUID != null) {
			return playerUUID.toString();
		} else {
			return "[unknown]";
		}
	}

	public static String getPlayerNameOrUUID(String playerName, UUID playerUUID) {
		// Either of them might be null.
		// Prefer name, else use uuid.
		if (playerName != null) {
			return playerName;
		} else if (playerUUID != null) {
			return playerUUID.toString();
		} else {
			return "[unknown]";
		}
	}

	public static final char COLOR_CHAR_ALTERNATIVE = '&';
	private static final Pattern STRIP_COLOR_ALTERNATIVE_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR_ALTERNATIVE) + "[0-9A-FK-OR]");

	// Reverse of ChatColor#translateAlternateColorCodes:
	public static String translateColorCodesToAlternative(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = altColorChar;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static String stripColor(String colored) {
		if (colored == null || colored.isEmpty()) return colored;
		String uncolored = ChatColor.stripColor(colored);
		uncolored = STRIP_COLOR_ALTERNATIVE_PATTERN.matcher(uncolored).replaceAll("");
		return uncolored;
	}

	public static String decolorize(String colored) {
		if (colored == null || colored.isEmpty()) return colored;
		return translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, colored);
	}

	public static List<String> decolorize(List<String> colored) {
		if (colored == null) return null;
		List<String> decolored = new ArrayList<>(colored.size());
		for (String string : colored) {
			decolored.add(translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, string));
		}
		return decolored;
	}

	// Decolorizes String entries, otherwise adopts them as they are:
	public static List<Object> decolorizeUnknown(List<?> colored) {
		if (colored == null) return null;
		List<Object> decolored = new ArrayList<>(colored.size());
		for (Object entry : colored) {
			Object decolorizedEntry = entry;
			if (entry instanceof String) {
				decolorizedEntry = translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, (String) entry);
			}
			decolored.add(decolorizedEntry);
		}
		return decolored;
	}

	public static String colorize(String message) {
		if (message == null || message.isEmpty()) return message;
		return ChatColor.translateAlternateColorCodes(COLOR_CHAR_ALTERNATIVE, message);
	}

	public static List<String> colorize(List<String> messages) {
		if (messages == null) return messages;
		List<String> colored = new ArrayList<>(messages.size());
		for (String message : messages) {
			colored.add(colorize(message));
		}
		return colored;
	}

	// Colorizes String entries, otherwise adopts them as they are:
	public static List<Object> colorizeUnknown(List<?> uncolored) {
		if (uncolored == null) return null;
		List<Object> colored = new ArrayList<>(uncolored.size());
		for (Object entry : uncolored) {
			Object colorizedEntry = entry;
			if (entry instanceof String) {
				colorizedEntry = colorize((String) entry);
			}
			colored.add(colorizedEntry);
		}
		return colored;
	}

	// SENDING

	public static void sendMessage(CommandSender recipient, String message) {
		Validate.notNull(recipient, "Recipient is null!");
		Validate.notNull(message, "Message is null!");
		// Skip sending if message is empty: Allows disabling of messages.
		if (message.isEmpty()) return;

		// Send (potentially multi-line) message:
		for (String line : StringUtils.splitLines(message)) {
			recipient.sendMessage(line);
		}
	}

	public static void sendMessage(CommandSender recipient, String message, Map<String, Object> arguments) {
		// Replace message arguments and then send:
		sendMessage(recipient, StringUtils.replaceArguments(message, arguments));
	}

	public static void sendMessage(CommandSender recipient, String message, Object... argumentPairs) {
		// Replace message arguments and then send:
		sendMessage(recipient, StringUtils.replaceArguments(message, argumentPairs));
	}

	/*
	 * TEXT COMPONENTS
	 */

	public static Text getPlayerText(Player player) {
		assert player != null;
		String playerName = player.getName();
		String playerUUIDString = player.getUniqueId().toString();
		return Text.hoverEvent(Text.of(playerUUIDString))
				.childInsertion(playerUUIDString)
				.childText(playerName)
				.buildRoot();
	}

	public static Text getPlayerText(String playerName, UUID playerUUID) {
		// Either of them might be null.
		// Prefer name, else use uuid.
		if (playerName != null) {
			if (playerUUID != null) {
				String playerUUIDString = playerUUID.toString();
				return Text.hoverEvent(Text.of(playerUUIDString))
						.childInsertion(playerUUIDString)
						.childText(playerName)
						.buildRoot();
			} else {
				return Text.of(playerName);
			}
		} else if (playerUUID != null) {
			return Text.of(playerUUID.toString());
		} else {
			return Text.of("[unknown]");
		}
	}

	// SENDING

	public static void sendMessage(CommandSender recipient, Text message) {
		SpigotText.sendMessage(recipient, message);
	}

	public static void sendMessage(CommandSender recipient, Text message, Map<String, Object> arguments) {
		Validate.notNull(recipient, "Recipient is null!");
		Validate.notNull(message, "Message is null!");
		// Assign arguments and then send:
		message.setPlaceholderArguments(arguments);
		sendMessage(recipient, message);
	}

	public static void sendMessage(CommandSender recipient, Text message, Object... argumentPairs) {
		Validate.notNull(recipient, "Recipient is null!");
		Validate.notNull(message, "Message is null!");
		// Assign arguments and then send:
		message.setPlaceholderArguments(argumentPairs);
		sendMessage(recipient, message);
	}
}
