package eu.mctraps.shop.ChatInput;

import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.mctraps.shop.MCTrapsShop;

public class ActionbarDisplayer implements Listener {
    private PacketPlayOutChat packet;
    
    public ActionbarDisplayer(String message){
        this.packet = packet;
    }
    
    public void Send(Player player){
    	((CraftPlayer) player).getHandle ().playerConnection.sendPacket(packet);
    }
    
    public void onJoin(PlayerJoinEvent e){
    	Player player = e.getPlayer();
    	ActionbarDisplayer ab = new ActionbarDisplayer("Wiadomosc testowa ");
    	ab.Send(player);
    }
    
}



