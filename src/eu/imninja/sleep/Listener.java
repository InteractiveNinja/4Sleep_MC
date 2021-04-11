package eu.imninja.sleep;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Listener implements org.bukkit.event.Listener {
    private Plugin p;
    private World w;
    private ArrayList<Player> playersSleeping = new ArrayList<Player>();
    private FileConfiguration config;


    private boolean playSoundAfterSleep = true;
    private boolean dayCounter = true;
    private boolean showTitle = true;
    private boolean showDayCounter = true;
    private String titleBig = "Good Morning";
    private String titleSmall = "Day {time}";
    private String textCantSleepAtNight = "You only can sleep at Night time";
    private String textWantsToSleep = "{playername} wants to sleep, {missing} zzZZzzzzZ";
    private String textDoesntSleepMore = "{playername} woke up, {missing}";

    private int minimalSleep = -1;


    public Listener(Plugin p, FileConfiguration config) {
        this.p = p;
        this.w = p.getServer().getWorld("world");
        this.config = config;
        setValues();
    }

    private void setValues() {
        try {
            playSoundAfterSleep = (boolean) config.get("soundAfterSleep");
            showTitle = (boolean) config.get("showTitle");
            showDayCounter = (boolean) config.get("showDayCounter");
            minimalSleep = (int) config.get("minimalSleep");
            titleBig = (String) config.get("titleTextBig");
            titleSmall = (String) config.get("titleTextSmall");
            textCantSleepAtNight = (String) config.get("textCantSleepAtNight");
            textWantsToSleep = (String) config.get("textWantsToSleep");
            textDoesntSleepMore = (String) config.get("textDoesntSleepMore");


        } catch (ClassCastException e) {
            System.out.println(Main.tag + " Casting Error, please see the Config, defaults will be used");
            playSoundAfterSleep = true;
            showTitle = true;
            showDayCounter = true;
            minimalSleep = -1;
            titleBig = "Good Morning";
            titleSmall = "Day {time}";
            textCantSleepAtNight = "You only can sleep at Night time";
            textWantsToSleep = "{playername} wants to sleep, {missing} zzZZzzzzZ";
            textDoesntSleepMore = "{playername} woke up, {missing}";
        }

    }

    @EventHandler
    private void onPlayerSleep(PlayerBedEnterEvent e) {
        String playerName = e.getPlayer().getDisplayName();

        if (isNight() || isRaining()) {
            playersSleeping.add(e.getPlayer());
            String message = textWantsToSleep.replace("{playername}", ChatColor.AQUA + playerName + ChatColor.GOLD).replace("{missing}", showMissing());
            p.getServer().broadcastMessage(message);
            if (isEnough()) wakeUp();

        } else {
            String message = ChatColor.AQUA + Main.tag + ChatColor.GOLD + textCantSleepAtNight;
            e.getPlayer().sendMessage(message);
            e.setCancelled(true);
        }

    }

    @EventHandler
    private void onPlayerStopSleep(PlayerBedLeaveEvent e) {
        String playerName = e.getPlayer().getDisplayName();
        if(playersSleeping.contains(e.getPlayer())) {
            playersSleeping.remove(e.getPlayer());
                String message = textDoesntSleepMore.replace("{playername}", ChatColor.AQUA + playerName + ChatColor.GRAY).replace("{missing}", showMissing());
                p.getServer().broadcastMessage(message);
        }

    }

    @EventHandler
    private void onPlayerQuits(PlayerQuitEvent e) {
        playersSleeping.remove(e.getPlayer());
        if ((isNight() || isRaining()) && isEnough()) {
                wakeUp();
        }
    }


    private boolean isNight() {
        long time = w.getTime();
        long beforeNight = 12541;
        long afterNight = 23458;
        return (time >= beforeNight && time <= afterNight);
    }

    private boolean isRaining() {
        return w.hasStorm();
    }

    private boolean isEnough() {
        int needToSleep = playersThatNeedToSleep();
        return (needToSleep <= getPlayersSleeping());
    }

    private int playersThatNeedToSleep() {
        return (minimalSleep == -1) ? getPlayersOnline() / 2 : minimalSleep;
    }

    private int getPlayersOnline() {
        return p.getServer().getOnlinePlayers().size();
    }

    private int getPlayersSleeping() {
        return playersSleeping.size();
    }

    private String showMissing() {
        int online = (minimalSleep == -1) ? playersThatNeedToSleep() : minimalSleep;
        int sleeping = getPlayersSleeping();

        return sleeping + "/" + online;
    }

    private void resetPlayerOnlineStats() {
        p.getServer().getOnlinePlayers().forEach(player -> {
            player.setStatistic(Statistic.TIME_SINCE_REST,0);
            System.out.println();
        });
    }

    private void wakeUp() {

        resetPlayerOnlineStats();

        playersSleeping.clear();

        w.setTime(0L);
        w.setThundering(false);
        w.setStorm(false);
        p.getServer().getOnlinePlayers().forEach(this::sendTitle);
    }

    private void sendTitle(Player p) {
        String smallTitle = (showDayCounter) ? ChatColor.GOLD + titleSmall.replace("{time}", "" + (w.getFullTime() / 24000)) : "";
        String bigTitle = ChatColor.GOLD + titleBig;
        if (showTitle){
            if(showDayCounter){
                p.sendTitle(bigTitle, smallTitle);
            } else {
                p.sendTitle(bigTitle,"");

            }

        }
        if (playSoundAfterSleep) p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

}
