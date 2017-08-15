package eu.mctraps.shop;

import eu.mctraps.shop.ChatInput.ActionbarDisplayer;
import eu.mctraps.shop.ChatInput.ChatInput;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MCTrapsShop extends JavaPlugin {
    FileConfiguration config;

    public String vTable;
    public String oTable;
    public String hTable;

    Connection connection;
    public Statement statement;
    private String host, database, username, password;
    private int port;

    ChatInput ci = new ChatInput();

    @Override
    public void onEnable() {
        getLogger().info("MCTrapsShop has been enabled!");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ActionbarDisplayer(null), this);

        saveDefaultConfig();
        getDataFolder().mkdir();
        config = getConfig();

        host = config.getString("database.host");
        port = config.getInt("database.port");
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");
        vTable = config.getString("tables.vouchers");
        oTable = config.getString("tables.offers");
        hTable = config.getString("tables.history");

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                } catch(SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(this);

        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);

        getCommand("smsshop").setExecutor(new MCTrapsShopCommandExecutor(this));
        getCommand("voucher").setExecutor(new MCTrapsShopCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("MCTrapsShop has been disabled");
    }

    void openConnection() throws SQLException, ClassNotFoundException {
        if(connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if(connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

            getLogger().info("Successfully connected to database. Hurrey!");
        }
    }

    public static String colorify(String s) {
        if(s != null) {
            return ChatColor.translateAlternateColorCodes('&', s);
        }

        return null;
    }
}
