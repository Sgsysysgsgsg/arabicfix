package com.arabicfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ArabicChatFixPlugin extends JavaPlugin implements Listener {

    private FloodgateApi floodgateApi;
    private Map<Character, Character> arabicMap;
    private boolean useProtocolLib = false;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            floodgateApi = FloodgateApi.getInstance();
            getLogger().info("Floodgate detected!");
        } else {
            getLogger().warning("Floodgate not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initArabicMap();
        getServer().getPluginManager().registerEvents(this, this);
        
        // Setup ProtocolLib if available
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            setupProtocolLib();
            useProtocolLib = true;
            getLogger().info("ProtocolLib detected! Full message interception enabled.");
        } else {
            getLogger().info("ProtocolLib not found. Install it for Discord message support.");
        }
        
        getLogger().info("ArabicChatFix enabled!");
    }

    private void setupProtocolLib() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SYSTEM_CHAT, PacketType.Play.Server.CHAT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player player = event.getPlayer();
                    
                    if (!isBedrock(player)) {
                        return; // Java player, send original
                    }
                    
                    try {
                        // Get the chat component
                        WrappedChatComponent component = packet.getChatComponents().read(0);
                        if (component == null) return;
                        
                        String json = component.getJson();
                        String text = component.getJson();
                        
                        // Extract plain text from JSON
                        String plainText = extractTextFromJson(json);
                        
                        if (!containsArabic(plainText)) {
                            return; // No Arabic
                        }
                        
                        // Convert Arabic for Bedrock
                        String converted = convertArabic(plainText);
                        
                        // Replace the text in JSON
                        String newJson = json.replace(plainText, converted);
                        
                        // Set the modified component
                        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(newJson));
                        
                    } catch (Exception e) {
                        // Fallback: try simple text replacement
                        try {
                            WrappedChatComponent component = packet.getChatComponents().read(0);
                            String json = component.getJson();
                            
                            if (containsArabic(json)) {
                                String converted = convertAllArabicInJson(json);
                                packet.getChatComponents().write(0, WrappedChatComponent.fromJson(converted));
                            }
                        } catch (Exception ex) {
                            // Silent fail
                        }
                    }
                }
            }
        );
    }
    
    private String extractTextFromJson(String json) {
        // Simple extraction - look for "text":"..."
        try {
            int start = json.indexOf("\"text\":\"");
            if (start == -1) return json;
            start += 8;
            int end = json.indexOf("\"", start);
            if (end == -1) return json;
            return json.substring(start, end);
        } catch (Exception e) {
            return json;
        }
    }
    
    private String convertAllArabicInJson(String json) {
        StringBuilder result = new StringBuilder();
        StringBuilder arabicBuffer = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
                if (arabicBuffer.length() > 0) {
                    result.append(convertArabic(arabicBuffer.toString()));
                    arabicBuffer.setLength(0);
                }
                result.append(c);
            } else if (inQuotes && containsArabic(String.valueOf(c))) {
                arabicBuffer.append(c);
            } else {
                if (arabicBuffer.length() > 0) {
                    result.append(convertArabic(arabicBuffer.toString()));
                    arabicBuffer.setLength(0);
                }
                result.append(c);
            }
        }
        
        if (arabicBuffer.length() > 0) {
            result.append(convertArabic(arabicBuffer.toString()));
        }
        
        return result.toString();
    }

    private void initArabicMap() {
        arabicMap = new HashMap<>();
        arabicMap.put('\u0627', '\uFE8D'); // ا
        arabicMap.put('\u0628', '\uFE8F'); // ب
        arabicMap.put('\u062A', '\uFE95'); // ت
        arabicMap.put('\u062B', '\uFE99'); // ث
        arabicMap.put('\u062C', '\uFE9D'); // ج
        arabicMap.put('\u062D', '\uFEA1'); // ح
        arabicMap.put('\u062E', '\uFEA5'); // خ
        arabicMap.put('\u062F', '\uFEA9'); // د
        arabicMap.put('\u0630', '\uFEAB'); // ذ
        arabicMap.put('\u0631', '\uFEAD'); // ر
        arabicMap.put('\u0632', '\uFEAF'); // ز
        arabicMap.put('\u0633', '\uFEB1'); // س
        arabicMap.put('\u0634', '\uFEB5'); // ش
        arabicMap.put('\u0635', '\uFEB9'); // ص
        arabicMap.put('\u0636', '\uFEBD'); // ض
        arabicMap.put('\u0637', '\uFEC1'); // ط
        arabicMap.put('\u0638', '\uFEC5'); // ظ
        arabicMap.put('\u0639', '\uFEC9'); // ع
        arabicMap.put('\u063A', '\uFECD'); // غ
        arabicMap.put('\u0641', '\uFED1'); // ف
        arabicMap.put('\u0642', '\uFED5'); // ق
        arabicMap.put('\u0643', '\uFED9'); // ك
        arabicMap.put('\u0644', '\uFEDD'); // ل
        arabicMap.put('\u0645', '\uFEE1'); // م
        arabicMap.put('\u0646', '\uFEE5'); // ن
        arabicMap.put('\u0647', '\uFEE9'); // ه
        arabicMap.put('\u0648', '\uFEED'); // و
        arabicMap.put('\u064A', '\uFEF1'); // ي
        arabicMap.put('\u0649', '\uFEEF'); // ى
        arabicMap.put('\u0629', '\uFE93'); // ة
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!containsArabic(event.getMessage())) return;
        
        String original = event.getMessage();
        String converted = convertArabic(original);
        
        Set<Player> recipients = new HashSet<>(event.getRecipients());
        event.getRecipients().clear();
        
        for (Player p : recipients) {
            String msg = String.format(event.getFormat(), 
                event.getPlayer().getDisplayName(),
                isBedrock(p) ? converted : original);
            p.sendMessage(msg);
        }
        
        Bukkit.getConsoleSender().sendMessage(
            String.format(event.getFormat(), event.getPlayer().getDisplayName(), original));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        if (!cmd.startsWith("/say ") && !cmd.startsWith("/minecraft:say ")) return;
        
        String msg = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
        if (!containsArabic(msg)) return;
        
        event.setCancelled(true);
        String converted = convertArabic(msg);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("[Server] " + (isBedrock(p) ? converted : msg));
        }
        Bukkit.getConsoleSender().sendMessage("[Server] " + msg);
    }

    private boolean isBedrock(Player p) {
        return floodgateApi.isFloodgatePlayer(p.getUniqueId());
    }

    private boolean containsArabic(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x0600 && c <= 0x06FF) return true;
        }
        return false;
    }

    private String convertArabic(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = words.length - 1; i >= 0; i--) {
            if (containsArabic(words[i])) {
                result.append(shapeAndReverse(words[i]));
            } else {
                result.append(words[i]);
            }
            if (i > 0) result.append(" ");
        }
        
        return result.toString();
    }

    private String shapeAndReverse(String word) {
        StringBuilder shaped = new StringBuilder();
        char[] chars = word.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            
            if (c == '\u0644' && i + 1 < chars.length && chars[i + 1] == '\u0627') {
                boolean before = i > 0 && canConnect(chars[i-1]);
                shaped.append(before ? '\uFEFC' : '\uFEFB');
                i++;
                continue;
            }
            
            if (arabicMap.containsKey(c)) {
                boolean before = i > 0 && canConnect(chars[i-1]);
                boolean after = i + 1 < chars.length && canConnect(c) && arabicMap.containsKey(chars[i+1]);
                shaped.append(getShapedChar(c, before, after));
            } else {
                shaped.append(c);
            }
        }
        
        return shaped.reverse().toString();
    }

    private boolean canConnect(char c) {
        return !(c == '\u0627' || c == '\u062F' || c == '\u0630' || 
                 c == '\u0631' || c == '\u0632' || c == '\u0648' || c == '\u0649');
    }

    private char getShapedChar(char c, boolean before, boolean after) {
        switch (c) {
            case '\u0628': return before && after ? '\uFE92' : before ? '\uFE90' : after ? '\uFE91' : '\uFE8F';
            case '\u062A': return before && after ? '\uFE98' : before ? '\uFE96' : after ? '\uFE97' : '\uFE95';
            case '\u062B': return before && after ? '\uFE9C' : before ? '\uFE9A' : after ? '\uFE9B' : '\uFE99';
            case '\u062C': return before && after ? '\uFEA0' : before ? '\uFE9E' : after ? '\uFE9F' : '\uFE9D';
            case '\u062D': return before && after ? '\uFEA4' : before ? '\uFEA2' : after ? '\uFEA3' : '\uFEA1';
            case '\u062E': return before && after ? '\uFEA8' : before ? '\uFEA6' : after ? '\uFEA7' : '\uFEA5';
            case '\u0633': return before && after ? '\uFEB4' : before ? '\uFEB2' : after ? '\uFEB3' : '\uFEB1';
            case '\u0634': return before && after ? '\uFEB8' : before ? '\uFEB6' : after ? '\uFEB7' : '\uFEB5';
            case '\u0635': return before && after ? '\uFEBC' : before ? '\uFEBA' : after ? '\uFEBB' : '\uFEB9';
            case '\u0636': return before && after ? '\uFEC0' : before ? '\uFEBE' : after ? '\uFEBF' : '\uFEBD';
            case '\u0637': return before && after ? '\uFEC4' : before ? '\uFEC2' : after ? '\uFEC3' : '\uFEC1';
            case '\u0638': return before && after ? '\uFEC8' : before ? '\uFEC6' : after ? '\uFEC7' : '\uFEC5';
            case '\u0639': return before && after ? '\uFECC' : before ? '\uFECA' : after ? '\uFECB' : '\uFEC9';
            case '\u063A': return before && after ? '\uFED0' : before ? '\uFECE' : after ? '\uFECF' : '\uFECD';
            case '\u0641': return before && after ? '\uFED4' : before ? '\uFED2' : after ? '\uFED3' : '\uFED1';
            case '\u0642': return before && after ? '\uFED8' : before ? '\uFED6' : after ? '\uFED7' : '\uFED5';
            case '\u0643': return before && after ? '\uFEDC' : before ? '\uFEDA' : after ? '\uFEDB' : '\uFED9';
            case '\u0644': return before && after ? '\uFEE0' : before ? '\uFEDE' : after ? '\uFEDF' : '\uFEDD';
            case '\u0645': return before && after ? '\uFEE4' : before ? '\uFEE2' : after ? '\uFEE3' : '\uFEE1';
            case '\u0646': return before && after ? '\uFEE8' : before ? '\uFEE6' : after ? '\uFEE7' : '\uFEE5';
            case '\u0647': return before && after ? '\uFEEC' : before ? '\uFEEA' : after ? '\uFEEB' : '\uFEE9';
            case '\u064A': return before && after ? '\uFEF4' : before ? '\uFEF2' : after ? '\uFEF3' : '\uFEF1';
            case '\u0629': return before ? '\uFE94' : '\uFE93';
            default: return arabicMap.getOrDefault(c, c);
        }
    }
}
