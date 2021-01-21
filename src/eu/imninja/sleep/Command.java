package eu.imninja.sleep;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        Player p = (Player) commandSender;

        p.sendMessage(ChatColor.AQUA + Main.tag);
        p.sendMessage(ChatColor.GRAY + "Made by InteractiveNinja");
        p.sendMessage(ChatColor.GRAY + "https://imninja.eu/");
        p.playSound(p.getLocation(),Sound.BLOCK_ANVIL_LAND,1f,1f);
        return true;
    }
}
