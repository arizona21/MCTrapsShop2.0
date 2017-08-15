package eu.mctraps.shop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    private final MCTrapsShop plugin;

    public PlayerChatListener(MCTrapsShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(plugin.ci.playerInChat(event.getPlayer().getName())) {
            plugin.ci.dialog(event.getPlayer().getName(), event.getMessage(), event, plugin);
        }
    }
}
