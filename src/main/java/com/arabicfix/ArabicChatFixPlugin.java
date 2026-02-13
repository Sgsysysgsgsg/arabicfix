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
        
        getLogger().info("ArabicChatFix has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ArabicChatFix has been disabled!");
    }

    private void initArabicMap() {
        arabicMap = new HashMap<>();
        
        // Map Arabic characters to their visual forms (isolated forms)
        // This ensures they display correctly when reversed for Bedrock
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
        
        // Tashkeel (diacritics)
        arabicMap.put('\u064B', '\u064B'); // Fathatan
        arabicMap.put('\u064C', '\u064C'); // Dammatan
        arabicMap.put('\u064D', '\u064D'); // Kasratan
        arabicMap.put('\u064E', '\u064E'); // Fatha
        arabicMap.put('\u064F', '\u064F'); // Damma
        arabicMap.put('\u0650', '\u0650'); // Kasra
        arabicMap.put('\u0651', '\u0651'); // Shadda
        arabicMap.put('\u0652', '\u0652'); // Sukun
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        
        // Check if message contains Arabic characters
        if (!containsArabic(message)) {
            return; // No Arabic, no conversion needed
        }

        // Cancel the original event
        event.setCancelled(true);

        Player sender = event.getPlayer();
        String senderName = sender.getDisplayName();
        
        // Send to each recipient with appropriate formatting
        for (Player recipient : event.getRecipients()) {
            String displayMessage;
            
            if (floodgateApi.isFloodgatePlayer(recipient.getUniqueId())) {
                // Bedrock player - send converted Arabic
                displayMessage = String.format(event.getFormat(), senderName, convertArabicForBedrock(message));
            } else {
                // Java player - send original message
                displayMessage = String.format(event.getFormat(), senderName, message);
            }
            
            recipient.sendMessage(displayMessage);
        }
        
        // Send to console with original message
        Bukkit.getConsoleSender().sendMessage(String.format(event.getFormat(), senderName, message));
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
        StringBuilder result = new StringBuilder();
        StringBuilder arabicBuffer = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            if (isArabicCharacter(c)) {
                // Add to Arabic buffer
                arabicBuffer.append(c);
            } else {
                // Non-Arabic character found
                if (arabicBuffer.length() > 0) {
                    // Process the Arabic buffer
                    result.append(reverseAndMapArabic(arabicBuffer.toString()));
                    arabicBuffer.setLength(0);
                }
                result.append(c);
            }
        }
        
        // Process any remaining Arabic text
        if (arabicBuffer.length() > 0) {
            result.append(reverseAndMapArabic(arabicBuffer.toString()));
        }
        
        return result.toString();
    }

    private boolean isArabicCharacter(char c) {
        return (c >= 0x0600 && c <= 0x06FF) || (c >= 0xFE70 && c <= 0xFEFF);
    }

    private String reverseAndMapArabic(String arabic) {
        StringBuilder mapped = new StringBuilder();
        
        // Map characters to their visual forms
        for (int i = 0; i < arabic.length(); i++) {
            char c = arabic.charAt(i);
            
            // Check for Lam-Alef ligature (لا)
            if (c == '\u0644' && i + 1 < arabic.length() && arabic.charAt(i + 1) == '\u0627') {
                // Use the combined Lam-Alef character
                mapped.append('\uFEFB');
                i++; // Skip the next character (Alef) as we've already processed it
            } else {
                // Normal character mapping
                Character visualForm = arabicMap.get(c);
                if (visualForm != null) {
                    mapped.append(visualForm);
                } else {
                    mapped.append(c);
                }
            }
        }
        
        // Reverse the string for RTL display in LTR-only Bedrock
        return mapped.reverse().toString();
    }
}
