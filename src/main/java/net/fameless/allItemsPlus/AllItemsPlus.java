package net.fameless.allItemsPlus;

import net.fameless.allItemsPlus.game.*;
import net.fameless.allItemsPlus.game.command.*;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.language.LanguageCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllItemsPlus extends JavaPlugin {

    private static AllItemsPlus instance;
    private Timer timer = null;
    private GameManager gameManager;
    private BossbarManager bossbarManager;

    public static AllItemsPlus get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("data.json", false);
        saveResource("lang_de.json", false);
        saveResource("lang_en.json", false);

        Lang.loadLanguage(this);

        gameManager = new GameManager(this);
        LanguageCommand languageCommand = new LanguageCommand(this);
        timer = new Timer(this, false, getConfig().getInt("timer.time", 0));
        SettingsCommand settingsCommand = new SettingsCommand();
        ExcludeCommand excludeCommand = new ExcludeCommand();
        MissingObjectivesCommand missingObjectivesCommand = new MissingObjectivesCommand(this);
        FinishedObjectivesCommand finishedObjectivesCommand = new FinishedObjectivesCommand(this);
        LeaderboardCommand leaderboardCommand = new LeaderboardCommand();
        BossbarCommand bossbarCommand = new BossbarCommand(this);

        GameListener gameListener = new GameListener(this);
        bossbarManager = new BossbarManager(this);
        new ScoreboardManager(this);

        getServer().getPluginManager().registerEvents(languageCommand, this);
        getServer().getPluginManager().registerEvents(settingsCommand, this);
        getServer().getPluginManager().registerEvents(missingObjectivesCommand, this);
        getServer().getPluginManager().registerEvents(finishedObjectivesCommand, this);
        getServer().getPluginManager().registerEvents(leaderboardCommand, this);
        getServer().getPluginManager().registerEvents(gameListener, this);

        getCommand("language").setExecutor(languageCommand);
        getCommand("timer").setExecutor(timer);
        getCommand("settings").setExecutor(settingsCommand);
        getCommand("exclude").setExecutor(excludeCommand);
        getCommand("missingobjectives").setExecutor(missingObjectivesCommand);
        getCommand("finishedobjectives").setExecutor(finishedObjectivesCommand);
        getCommand("leaderboard").setExecutor(leaderboardCommand);
        getCommand("togglebossbar").setExecutor(bossbarCommand);

        getCommand("timer").setTabCompleter(timer);
    }

    @Override
    public void onDisable() {
        gameManager.saveData();
        getConfig().set("timer.time", timer.getTime());
        saveConfig();
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public BossbarManager getBossbarManager() {
        return bossbarManager;
    }

    public Timer getTimer() {
        return timer;
    }
}
