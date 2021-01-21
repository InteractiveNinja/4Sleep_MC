package eu.imninja.sleep;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class Main extends JavaPlugin {


    public static String tag = "[4Sleep]";
    FileConfiguration config = this.getConfig();
    @Override
    public void onEnable() {
        System.out.println( tag +" is enabled");
        this.getServer().getPluginManager().registerEvents(new Listener(this,config),this);
        this.getCommand("4sleep").setExecutor(new Command());
        config.options().copyDefaults(true);
        saveConfig();

    }

    @Override
    public void onDisable() {
        System.out.println( tag + " is disabled");
    }
}
