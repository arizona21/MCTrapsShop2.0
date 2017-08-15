package eu.mctraps.shop.ChatInput;

import eu.mctraps.shop.MCTrapsShop;
import eu.mctraps.shop.Vouchers.Voucher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static eu.mctraps.shop.strtotime.strtotime;

public class VoucherAddParser extends ChatInputStuff {
    private int stage = 0;
    private Voucher v;

    @Override
    public int getStage() {
        return stage;
    }

    @Override
    public void setStage(int a) {
        stage = a;
    }

    @Override
    public void dialog(ChatInput map, String username, String message, AsyncPlayerChatEvent event, MCTrapsShop plugin) {
        event.setCancelled(true);

        if(!(message.toLowerCase().contains("cancel"))) {
            if (stage == 0) {
                v = new Voucher(plugin);
                if (!(message.toLowerCase().contains("gen"))) {
                    if (message.matches("[A-Za-z0-9]{10}")) {
                        v.setCode(message);
                        stage++;
                        Bukkit.getPlayer(username).sendMessage("§9Ile razy kod moze zostac uzyty? §6(cyfry)");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§4Blad: §cvoucher moze zawierac duze i male litery alfabetu lacinskiego i cyfry i musi miec dlugosc 10. Wpisz §6\"gen\" §caby wygenerowac");
                    }
                } else {
                    v.genCode();
                    Bukkit.getPlayer(username).sendMessage("§2Pomyslnie wygenerowano kod §6(" + v.getCode() + ")");
                    stage++;
                    Bukkit.getPlayer(username).sendMessage("§9Ile razy kod moze zostac uzyty? §6(cyfry)");
                }
                return;
            } else if (stage == 1) {
                try {
                    int uses = Integer.parseInt(message);
                    v.setUses(uses);
                    stage++;
                    Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? (wpisz id)");
                    ResultSet result = plugin.statement.executeQuery("SELECT * FROM " + plugin.oTable);
                    while(result.next()) {
                        Bukkit.getPlayer(username).sendMessage("  §7#" + result.getInt("id") + " §6" + result.getString("name"));
                    }
                } catch (NumberFormatException e) {
                    Bukkit.getPlayer(username).sendMessage("§4Blad: §cmozesz uzywac tylko cyfr!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas laczenia z baza danych");
                    Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? §6(wpisz id)");
                    stage++;
                }
                return;
            } else if(stage == 2) {
                try {
                    int offer = Integer.parseInt(message);
                    v.setOffer(offer);
                    stage++;
                    Bukkit.getPlayer(username).sendMessage("§9Czy kod ma byc ograniczony czasowo? §6(tak/nie)");
                } catch (NumberFormatException e) {
                    Bukkit.getPlayer(username).sendMessage("§4Blad: §cmozesz uzywac tylko cyfr!");
                }
                return;
            } else if(stage == 3) {
                if(message.toLowerCase().contains("tak")) {
                    v.setTimed(1);
                    stage++;
                    Bukkit.getPlayer(username).sendMessage("§9Ile czasu ma dzialac kod? §6(np. 10 minutes, 2 hours, 5 days)");
                } else if(message.toLowerCase().contains("nie")) {
                    v.setTimed(0);
                    Bukkit.getPlayer(username).sendMessage("§9Pomyslnie stworzono voucher. Wysylanie do bazy danych...");
                    boolean sent = v.create();
                    if(sent) {
                        Bukkit.getPlayer(username).sendMessage("§2Pomyslnie wyslano voucher do bazy danych :)");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                    }
                    stage = 9000;
                    map.removePlayer(username);
                } else {
                    Bukkit.getPlayer(username).sendMessage("§4Blad: §cpowinienes odpisac §6\"tak\" §calbo §6\"nie\"");
                }
                return;
            } else if(stage == 4) {
                if(strtotime(message) != null) {
                    Date end = strtotime(message);
                    long time = end.getTime();
                    Timestamp endtime = new Timestamp(time);
                    v.setEndtime(endtime);

                    Bukkit.getPlayer(username).sendMessage("§9Pomyslnie stworzono voucher. Wysylanie do bazy danych...");

                    boolean sent = v.create();
                    if(sent) {
                        Bukkit.getPlayer(username).sendMessage("§9Pomyslnie wyslano voucher do bazy danych :)");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                    }
                    stage = 9000;
                    map.removePlayer(username);
                } else {
                    Bukkit.getPlayer(username).sendMessage("§4Blad: §czly format daty");
                }
            }
        } else {
            Bukkit.getPlayer(username).sendMessage("§7Wychodzenie z kreatora voucherow");
            map.removePlayer(username);
        }
    }

    @Override
    public void dialog(ChatInput map, Player p, String message, AsyncPlayerChatEvent event, MCTrapsShop plugin) {
        dialog(map, p.getName(), message, event, plugin);
    }

    @Override
    public void cleanup() {

    }
}
