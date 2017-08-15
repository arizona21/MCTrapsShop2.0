package eu.mctraps.shop.Vouchers;

import eu.mctraps.shop.MCTrapsShop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class Voucher {
    private final MCTrapsShop plugin;

    private int id;
    private String code;
    private int uses;
    private int offer;
    private int timed;
    private Date endtime;

    public Voucher(MCTrapsShop plugin) {
        this.plugin = plugin;
    }

    public Voucher(MCTrapsShop plugin, int id, String code, int uses, int offer, int timed, Date endtime) {
        this.plugin = plugin;
        this.id = id;
        this.code = code;
        this.uses = uses;
        this.offer = offer;
        this.timed = timed;
        this.endtime = endtime;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setUses(int uses) {
        this.uses = uses;
    }
    public void setOffer(int offer) {
        this.offer = offer;
    }
    public void setTimed(int timed) {
        this.timed = timed;
    }
    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public int getId() {
        return id;
    }
    public String getCode() {
        return code;
    }
    public int getUses() {
        return uses;
    }
    public int getOffer() {
        return offer;
    }
    public int getTimed() {
        return timed;
    }
    public Date getEndtime() {
        return endtime;
    }

    public void genCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        code = sb.toString();
    }

    public boolean create() {
        if("x" + code + uses + offer + timed + endtime != "x") {
            if(timed == 1) {
                try {
                    plugin.statement.executeUpdate("INSERT INTO " + plugin.vTable + " (code, uses, offer, timed, endtime) VALUES ('" + code + "', '" + uses + "', '" + offer + "', '" + timed + "', '" + endtime + "')");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            } else {
                try {
                    plugin.statement.executeUpdate("INSERT INTO " + plugin.vTable + " (code, uses, offer) VALUES ('" + code + "', '" + uses + "', '" + offer + "')");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean push() {
        if ("x" + code + uses + offer + timed + endtime != "x") {
            if(timed == 1) {
                try {
                    plugin.statement.executeUpdate("UPDATE " + plugin.vTable + " SET code='" + code + "', uses='" + uses + "', offer='" + offer + "', timed='" + timed + "', endtime='" + endtime + "')");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            } else {
                try {
                    plugin.statement.executeUpdate("UPDATE " + plugin.vTable + " SET code='" + code + "', uses='" + uses + "', offer='" + offer + "', timed='" + timed + "')");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
