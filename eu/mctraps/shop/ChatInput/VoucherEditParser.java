package eu.mctraps.shop.ChatInput;

import eu.mctraps.shop.MCTrapsShop;
import eu.mctraps.shop.Vouchers.Voucher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static eu.mctraps.shop.strtotime.strtotime;

public class VoucherEditParser extends ChatInputStuff {
    private int stage = 0;
    private Voucher v;

    public VoucherEditParser(ResultSet result, MCTrapsShop plugin) throws SQLException {
        int id = 0;
        int uses = 0;
        int offer = 0;
        int timed = 0;
        String code = "";
        Date endtime = new Date();
        while(result.next()) {
            id = result.getInt("id");
            code = result.getString("code");
            uses = result.getInt("uses");
            offer = result.getInt("offer");
            timed = result.getInt("timed");
            endtime = result.getTimestamp("endtime");
        }
        v = new Voucher(plugin, id, code, uses, offer, timed, endtime);
    }

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
                if (!(message.toLowerCase().equalsIgnoreCase("L"))) {
                    if (message.matches("[A-Za-z0-9]{10}")) {
                        v.setCode(message);
                        stage++;
                        String uses = Integer.toString(v.getUses());
                        Bukkit.getPlayer(username).sendMessage("§9Ile razy kod moze zostac uzyty? §6(cyfry, wpisz L aby zostawic) §b[" + uses + "]");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§4Blad: §cvoucher moze zawierac duze i male litery alfabetu lacinskiego i cyfry i musi miec dlugosc 10");
                    }
                } else {
                    stage++;
                    String uses = Integer.toString(v.getUses());
                    Bukkit.getPlayer(username).sendMessage("§9Ile razy kod moze zostac uzyty? §6(cyfry, wpisz L aby zostawic) §b[" + uses + "]");
                }
                return;
            } else if (stage == 1) {
                if(!(message.toLowerCase().equalsIgnoreCase("L"))) {
                    try {
                        int uses = Integer.parseInt(message);
                        v.setUses(uses);
                        stage++;
                        String offer = Integer.toString(v.getOffer());
                        Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? §6(wpisz id lub L aby zostawic) §b[" + offer + "]");
                        ResultSet result = plugin.statement.executeQuery("SELECT * FROM " + plugin.oTable);
                        while (result.next()) {
                            Bukkit.getPlayer(username).sendMessage("  §7#" + result.getInt("id") + " §6" + result.getString("name"));
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getPlayer(username).sendMessage("§4Blad: §cmozesz uzywac tylko cyfr!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas laczenia z baza danych");
                        String offer = Integer.toString(v.getOffer());
                        Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? §6(wpisz id lub L aby zostawic) §b[" + offer + "]");
                        stage++;
                    }
                } else {
                    try {
                        stage++;
                        ResultSet result = plugin.statement.executeQuery("SELECT * FROM " + plugin.oTable);
                        String offer = Integer.toString(v.getOffer());
                        Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? §6(wpisz id lub L aby zostawic) §b[" + offer + "]");
                        while (result.next()) {
                            Bukkit.getPlayer(username).sendMessage("  §7#" + result.getInt("id") + " §6" + result.getString("name"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas laczenia z baza danych ");
                        String offer = Integer.toString(v.getOffer());
                        Bukkit.getPlayer(username).sendMessage("§9Do ktorej oferty przypisac voucher? §6(wpisz id lub L aby zostawic) §b[" + offer + "]");
                        stage++;
                    }
                }
                return;
            } else if(stage == 2) {
                if(!(message.toLowerCase().equalsIgnoreCase("L"))) {
                    try {
                        int offer = Integer.parseInt(message);
                        v.setOffer(offer);
                        stage++;
                        String timed = (v.getTimed() == 1) ? "tak" : "nie";
                        Bukkit.getPlayer(username).sendMessage("§9Czy kod ma byc ograniczony czasowo? §6(tak/nie lub L aby zostawic) §b[" + timed + "]");
                    } catch (NumberFormatException e) {
                        Bukkit.getPlayer(username).sendMessage("§4Blad: §cmozesz uzywac tylko cyfr!");
                    }
                } else {
                    stage++;
                    String timed = (v.getTimed() == 1) ? "tak" : "nie";
                    Bukkit.getPlayer(username).sendMessage("§9Czy kod ma byc ograniczony czasowo? §6(tak/nie lub L aby zostawic) §b[" + timed + "]");
                }
                return;
            } else if(stage == 3) {
                if(!(message.toLowerCase().equalsIgnoreCase("L"))) {
                    if (message.toLowerCase().contains("tak")) {
                        v.setTimed(1);
                        stage++;
                        Bukkit.getPlayer(username).sendMessage("§9Ile czasu ma dzialac kod (od teraz)? §6(np. 10 minutes, 2 hours, 5 days) §b[Lub wpisz L aby zostawic do " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(v.getEndtime()) + "]");
                    } else if (message.toLowerCase().contains("nie")) {
                        v.setTimed(0);
                        Bukkit.getPlayer(username).sendMessage("§9Pomyslnie zedytowano voucher. Wysylanie do bazy danych...");
                        boolean sent = v.push();
                        if (sent) {
                            Bukkit.getPlayer(username).sendMessage("§2Pomyslnie wyslano voucher do bazy danych :)");
                        } else {
                            Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                        }
                        stage = 9000;
                        map.removePlayer(username);
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§4Blad: §cpowinienes odpisac §6\"tak\" §calbo §6\"nie\"");
                    }
                } else {
                    if(v.getTimed() == 1) {
                        stage++;
                        Bukkit.getPlayer(username).sendMessage("§9Ile czasu ma dzialac kod (od teraz)? §6(np. 10 minutes, 2 hours, 5 days) §b[Lub wpisz L aby zostawic do " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(v.getEndtime()) + "]");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§9Pomyslnie zedytowano voucher. Wysylanie do bazy danych...");
                        boolean sent = v.push();
                        if (sent) {
                            Bukkit.getPlayer(username).sendMessage("§2Pomyslnie wyslano voucher do bazy danych :)");
                        } else {
                            Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                        }
                        stage = 9000;
                        map.removePlayer(username);
                    }
                }
                return;
            } else if(stage == 4) {
                if(!(message.toLowerCase().equalsIgnoreCase("L"))) {
                    if (strtotime(message) != null) {
                        Date end = strtotime(message);
                        long time = end.getTime();
                        Timestamp endtime = new Timestamp(time);
                        v.setEndtime(endtime);

                        Bukkit.getPlayer(username).sendMessage("§9Pomyslnie stworzono voucher. Wysylanie do bazy danych...");
                        boolean sent = v.push();
                        if (sent) {
                            Bukkit.getPlayer(username).sendMessage("§9Pomyslnie wyslano voucher do bazy danych :)");
                        } else {
                            Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                        }
                        stage = 9000;
                        map.removePlayer(username);
                    }
                } else {
                    Bukkit.getPlayer(username).sendMessage("§9Pomyslnie zedytowano voucher. Wysylanie do bazy danych...");
                    boolean sent = v.push();
                    if (sent) {
                        Bukkit.getPlayer(username).sendMessage("§9Pomyslnie wyslano voucher do bazy danych :)");
                    } else {
                        Bukkit.getPlayer(username).sendMessage("§cWystapil blad podczas wysylania vouchera do bazy danych :(");
                    }
                }
            }
        } else {
            Bukkit.getPlayer(username).sendMessage("§7Wychodzenie z edytora voucherow");
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
