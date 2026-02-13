
package com.arabicfix;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

public class ArabicChatFixPlugin extends JavaPlugin implements Listener {

    private FloodgateApi floodgateApi;

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
            getLogger().severe("Floodgate not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        floodgateApi = FloodgateApi.getInstance();
        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("ArabicChatFix Pro (Paper 1.21) Enabled!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        if (!(event.message() instanceof TextComponent textComponent)) return;

        String message = textComponent.content();

        event.viewers().forEach(viewer -> {
            if (viewer instanceof org.bukkit.entity.Player player) {

                if (floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
                    String fixed = processArabic(message);
                    player.sendMessage(Component.text(fixed));
                } else {
                    player.sendMessage(Component.text(message));
                }
            }
        });

        event.setCancelled(true);
    }

    private String processArabic(String input) {
        try {
            ArabicShaping shaping = new ArabicShaping(
                    ArabicShaping.LETTERS_SHAPE |
                    ArabicShaping.TEXT_DIRECTION_LOGICAL |
                    ArabicShaping.LENGTH_GROW_SHRINK
            );

            String shaped = shaping.shape(input);

            Bidi bidi = new Bidi(shaped, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
            bidi.setReorderingMode(Bidi.REORDER_DEFAULT);

            return bidi.writeReordered(Bidi.DO_MIRRORING);

        } catch (ArabicShapingException e) {
            return input;
        }
    }
}
