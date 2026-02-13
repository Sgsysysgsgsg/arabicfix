package com.arabicfix;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.Map;

public class ArabicChatFixPlugin extends JavaPlugin implements Listener {

    private FloodgateApi floodgateApi;
    private Map<Character, Character> arabicMap;

    @Override
    public void onEnable() {
        // Check if Floodgate is available
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            floodgateApi = FloodgateApi.getInstance();
            getLogger().info("Floodgate detected! Arabic chat fix enabled.");
        } else {
            getLogger().warning("Floodgate not found! Plugin will not work without Floodgate.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Arabic character mapping for visual display
        initArabicMap();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            getLogger().info("DiscordSRV detected! Compatibility enabled.");
        }
        
        getLogger().info("ArabicChatFix has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ArabicChatFix has been disabled!");
    }

    private void initArabicMap() {
        arabicMap = new HashMap<>();
        
        // We'll use isolated forms as fallback
        // The actual shaping will be done in the convertArabicForBedrock method
        arabicMap.put('\u0627', '\uFE8D'); // ا - Alef
        arabicMap.put('\u0628', '\uFE8F'); // ب - Beh
        arabicMap.put('\u062A', '\uFE95'); // ت - Teh
        arabicMap.put('\u062B', '\uFE99'); // ث - Theh
        arabicMap.put('\u062C', '\uFE9D'); // ج - Jeem
        arabicMap.put('\u062D', '\uFEA1'); // ح - Hah
        arabicMap.put('\u062E', '\uFEA5'); // خ - Khah
        arabicMap.put('\u062F', '\uFEA9'); // د - Dal
        arabicMap.put('\u0630', '\uFEAB'); // ذ - Thal
        arabicMap.put('\u0631', '\uFEAD'); // ر - Reh
        arabicMap.put('\u0632', '\uFEAF'); // ز - Zain
        arabicMap.put('\u0633', '\uFEB1'); // س - Seen
        arabicMap.put('\u0634', '\uFEB5'); // ش - Sheen
        arabicMap.put('\u0635', '\uFEB9'); // ص - Sad
        arabicMap.put('\u0636', '\uFEBD'); // ض - Dad
        arabicMap.put('\u0637', '\uFEC1'); // ط - Tah
        arabicMap.put('\u0638', '\uFEC5'); // ظ - Zah
        arabicMap.put('\u0639', '\uFEC9'); // ع - Ain
        arabicMap.put('\u063A', '\uFECD'); // غ - Ghain
        arabicMap.put('\u0641', '\uFED1'); // ف - Feh
        arabicMap.put('\u0642', '\uFED5'); // ق - Qaf
        arabicMap.put('\u0643', '\uFED9'); // ك - Kaf
        arabicMap.put('\u0644', '\uFEDD'); // ل - Lam
        arabicMap.put('\u0645', '\uFEE1'); // م - Meem
        arabicMap.put('\u0646', '\uFEE5'); // ن - Noon
        arabicMap.put('\u0647', '\uFEE9'); // ه - Heh
        arabicMap.put('\u0648', '\uFEED'); // و - Waw
        arabicMap.put('\u064A', '\uFEF1'); // ي - Yeh
        arabicMap.put('\u0649', '\uFEEF'); // ى - Alef Maksura
        arabicMap.put('\u0629', '\uFE93'); // ة - Teh Marbuta
    }
    
    private char getShapedChar(char c, boolean connects_before, boolean connects_after) {
        // Arabic character shaping based on position
        switch (c) {
            case '\u0628': // ب - Beh
                if (connects_before && connects_after) return '\uFE92'; // medial
                if (connects_before) return '\uFE90'; // final
                if (connects_after) return '\uFE91'; // initial
                return '\uFE8F'; // isolated
            case '\u062A': // ت - Teh
                if (connects_before && connects_after) return '\uFE98'; // medial
                if (connects_before) return '\uFE96'; // final
                if (connects_after) return '\uFE97'; // initial
                return '\uFE95'; // isolated
            case '\u062B': // ث - Theh
                if (connects_before && connects_after) return '\uFE9C'; // medial
                if (connects_before) return '\uFE9A'; // final
                if (connects_after) return '\uFE9B'; // initial
                return '\uFE99'; // isolated
            case '\u062C': // ج - Jeem
                if (connects_before && connects_after) return '\uFEA0'; // medial
                if (connects_before) return '\uFE9E'; // final
                if (connects_after) return '\uFE9F'; // initial
                return '\uFE9D'; // isolated
            case '\u062D': // ح - Hah
                if (connects_before && connects_after) return '\uFEA4'; // medial
                if (connects_before) return '\uFEA2'; // final
                if (connects_after) return '\uFEA3'; // initial
                return '\uFEA1'; // isolated
            case '\u062E': // خ - Khah
                if (connects_before && connects_after) return '\uFEA8'; // medial
                if (connects_before) return '\uFEA6'; // final
                if (connects_after) return '\uFEA7'; // initial
                return '\uFEA5'; // isolated
            case '\u0633': // س - Seen
                if (connects_before && connects_after) return '\uFEB4'; // medial
                if (connects_before) return '\uFEB2'; // final
                if (connects_after) return '\uFEB3'; // initial
                return '\uFEB1'; // isolated
            case '\u0634': // ش - Sheen
                if (connects_before && connects_after) return '\uFEB8'; // medial
                if (connects_before) return '\uFEB6'; // final
                if (connects_after) return '\uFEB7'; // initial
                return '\uFEB5'; // isolated
            case '\u0635': // ص - Sad
                if (connects_before && connects_after) return '\uFEBC'; // medial
                if (connects_before) return '\uFEBA'; // final
                if (connects_after) return '\uFEBB'; // initial
                return '\uFEB9'; // isolated
            case '\u0636': // ض - Dad
                if (connects_before && connects_after) return '\uFEC0'; // medial
                if (connects_before) return '\uFEBE'; // final
                if (connects_after) return '\uFEBF'; // initial
                return '\uFEBD'; // isolated
            case '\u0637': // ط - Tah
                if (connects_before && connects_after) return '\uFEC4'; // medial
                if (connects_before) return '\uFEC2'; // final
                if (connects_after) return '\uFEC3'; // initial
                return '\uFEC1'; // isolated
            case '\u0638': // ظ - Zah
                if (connects_before && connects_after) return '\uFEC8'; // medial
                if (connects_before) return '\uFEC6'; // final
                if (connects_after) return '\uFEC7'; // initial
                return '\uFEC5'; // isolated
            case '\u0639': // ع - Ain
                if (connects_before && connects_after) return '\uFECC'; // medial
                if (connects_before) return '\uFECA'; // final
                if (connects_after) return '\uFECB'; // initial
                return '\uFEC9'; // isolated
            case '\u063A': // غ - Ghain
                if (connects_before && connects_after) return '\uFED0'; // medial
                if (connects_before) return '\uFECE'; // final
                if (connects_after) return '\uFECF'; // initial
                return '\uFECD'; // isolated
            case '\u0641': // ف - Feh
                if (connects_before && connects_after) return '\uFED4'; // medial
                if (connects_before) return '\uFED2'; // final
                if (connects_after) return '\uFED3'; // initial
                return '\uFED1'; // isolated
            case '\u0642': // ق - Qaf
                if (connects_before && connects_after) return '\uFED8'; // medial
                if (connects_before) return '\uFED6'; // final
                if (connects_after) return '\uFED7'; // initial
                return '\uFED5'; // isolated
            case '\u0643': // ك - Kaf
                if (connects_before && connects_after) return '\uFEDC'; // medial
                if (connects_before) return '\uFEDA'; // final
                if (connects_after) return '\uFEDB'; // initial
                return '\uFED9'; // isolated
            case '\u0644': // ل - Lam
                if (connects_before && connects_after) return '\uFEE0'; // medial
                if (connects_before) return '\uFEDE'; // final
                if (connects_after) return '\uFEDF'; // initial
                return '\uFEDD'; // isolated
            case '\u0645': // م - Meem
                if (connects_before && connects_after) return '\uFEE4'; // medial
                if (connects_before) return '\uFEE2'; // final
                if (connects_after) return '\uFEE3'; // initial
                return '\uFEE1'; // isolated
            case '\u0646': // ن - Noon
                if (connects_before && connects_after) return '\uFEE8'; // medial
                if (connects_before) return '\uFEE6'; // final
                if (connects_after) return '\uFEE7'; // initial
                return '\uFEE5'; // isolated
            case '\u0647': // ه - Heh
                if (connects_before && connects_after) return '\uFEEC'; // medial
                if (connects_before) return '\uFEEA'; // final
                if (connects_after) return '\uFEEB'; // initial
                return '\uFEE9'; // isolated
            case '\u064A': // ي - Yeh
                if (connects_before && connects_after) return '\uFEF4'; // medial
                if (connects_before) return '\uFEF2'; // final
                if (connects_after) return '\uFEF3'; // initial
                return '\uFEF1'; // isolated
            case '\u0629': // ة - Teh Marbuta
                if (connects_before) return '\uFE94'; // final
                return '\uFE93'; // isolated
            // Non-connecting characters (always isolated)
            case '\u0627': return '\uFE8D'; // ا - Alef
            case '\u062F': return '\uFEA9'; // د - Dal
            case '\u0630': return '\uFEAB'; // ذ - Thal
            case '\u0631': return '\uFEAD'; // ر - Reh
            case '\u0632': return '\uFEAF'; // ز - Zain
            case '\u0648': return '\uFEED'; // و - Waw
            case '\u0649': return '\uFEEF'; // ى - Alef Maksura
            default:
                return c; // Return as-is for unknown characters
        }
    }
    
    private boolean isArabicLetter(char c) {
        return (c >= 0x0621 && c <= 0x064A) || c == 0x0629;
    }
    
    private boolean canConnectAfter(char c) {
        // These characters don't connect to the letter after them
        return !(c == '\u0627' || c == '\u062F' || c == '\u0630' || 
                 c == '\u0631' || c == '\u0632' || c == '\u0648' || 
                 c == '\u0649');
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player sender = event.getPlayer();
        
        // Check if the SENDER is a Bedrock player typing Arabic
        if (!floodgateApi.isFloodgatePlayer(sender.getUniqueId()) || !containsArabic(message)) {
            return; // Not a Bedrock player or no Arabic, skip
        }
        
        // Bedrock player is typing Arabic - we need to convert it
        // But we can't change the event message now (DiscordSRV already processed it)
        // So we cancel and send manually to in-game players only
        
        event.setCancelled(true);
        
        String convertedMessage = convertArabicForBedrock(message);
        String format = event.getFormat();
        String senderName = sender.getDisplayName();
        
        // Send converted message to ALL in-game players (Bedrock AND Java)
        for (Player player : Bukkit.getOnlinePlayers()) {
            String formatted = String.format(format, senderName, convertedMessage);
            player.sendMessage(formatted);
        }
        
        // Send to console
        Bukkit.getConsoleSender().sendMessage(String.format(format, senderName, convertedMessage));
        
        // DiscordSRV already saw the original message before we cancelled it
        // So Discord will get the ORIGINAL Arabic text
    }

    private boolean containsArabic(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x0600 && c <= 0x06FF) { // Arabic Unicode block
                return true;
            }
        }
        return false;
    }

    private String convertArabicForBedrock(String text) {
        // Split text by spaces to handle each word separately
        String[] parts = text.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            if (containsArabic(part)) {
                // This is an Arabic word - apply shaping and reverse
                result.append(reverseAndMapArabic(part));
            } else {
                // Non-Arabic word (English, numbers, etc.) - keep as-is
                result.append(part);
            }
            
            // Add space between words (except after last word)
            if (i < parts.length - 1) {
                result.append(" ");
            }
        }
        
        // Now reverse the entire sentence to get RTL word order
        String[] words = result.toString().split(" ");
        StringBuilder finalResult = new StringBuilder();
        
        for (int i = words.length - 1; i >= 0; i--) {
            finalResult.append(words[i]);
            if (i > 0) {
                finalResult.append(" ");
            }
        }
        
        return finalResult.toString();
    }

    private boolean isArabicCharacter(char c) {
        return (c >= 0x0600 && c <= 0x06FF) || (c >= 0xFE70 && c <= 0xFEFF);
    }

    private String reverseAndMapArabic(String arabic) {
        StringBuilder shaped = new StringBuilder();
        char[] chars = arabic.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            
            if (!isArabicLetter(current)) {
                // Keep diacritics and other marks as-is
                shaped.append(current);
                continue;
            }
            
            // Check for Lam-Alef ligature
            if (current == '\u0644' && i + 1 < chars.length && chars[i + 1] == '\u0627') {
                boolean connects_before = i > 0 && isArabicLetter(chars[i - 1]) && canConnectAfter(chars[i - 1]);
                
                if (connects_before) {
                    shaped.append('\uFEFC'); // لا final form
                } else {
                    shaped.append('\uFEFB'); // لا isolated form
                }
                i++; // Skip the Alef
                continue;
            }
            
            // Determine connection context
            boolean connects_before = i > 0 && isArabicLetter(chars[i - 1]) && canConnectAfter(chars[i - 1]);
            boolean connects_after = i + 1 < chars.length && isArabicLetter(chars[i + 1]) && canConnectAfter(current);
            
            // Get properly shaped character
            char shapedChar = getShapedChar(current, connects_before, connects_after);
            shaped.append(shapedChar);
        }
        
        // Reverse for LTR display
        return shaped.reverse().toString();
    }
}
