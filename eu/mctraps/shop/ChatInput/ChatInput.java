package eu.mctraps.shop.ChatInput;

import eu.mctraps.shop.MCTrapsShop;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatInput {
    private HashMap<String, Object> chatMap = new HashMap<>();
    public HashMap<String, Object> getChatMap() {return chatMap;}

    public void addToMap(Player p, Object o) {
        addToMap(p.getName(), (ChatInputStuff) o);
    }

    public void addToMap(String p, ChatInputStuff o) {
        if(chatMap.containsKey(p)) {
            ((ChatInputStuff)chatMap.get(p)).cleanup();
            chatMap.remove(p);
        } else {
            chatMap.put(p, o);
        }
    }

    public void dialog(String name, String message, AsyncPlayerChatEvent event, MCTrapsShop plugin) {
        if(chatMap.containsKey(name)) {
            if(chatMap.get(name) != null) {
                ((ChatInputStuff)chatMap.get(name)).dialog(this, name, message, event, plugin);
            }
        }
    }

    public boolean playerInChat(String name) {
        return chatMap.containsKey(name);
    }

    public void removePlayer(String name) {
        if(chatMap.containsKey(name)) {
            ((ChatInputStuff)chatMap.get(name)).cleanup();
            chatMap.remove(name);
        }
    }
}
