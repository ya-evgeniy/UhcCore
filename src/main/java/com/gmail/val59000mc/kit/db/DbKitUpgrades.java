package com.gmail.val59000mc.kit.db;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.KitsConfiguration;
import com.gmail.val59000mc.events.kit.PlayerKitUpgradeLoaded;
import com.gmail.val59000mc.events.kit.PlayerKitUpgradeUploaded;
import com.gmail.val59000mc.events.kit.PlayerKitUpgradesLoaded;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.upgrade.KitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DbKitUpgrades {

    private static final String DEFAULT_URL = "jdbc:mysql://%s:%s/%s";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, player_unique_id VARCHAR(36) NOT NULL, upgrade_id VARCHAR(255) NOT NULL,upgrade_level INT NOT NULL, upgrade_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String INSERT_PLAYER_UPGRADE = "INSERT INTO `%s` (`player_unique_id`, `upgrade_id`, `upgrade_level`) VALUES ('%s', '%s', %s);";
    private static final String SELECT_PLAYER_UPGRADE = "SELECT A.`upgrade_id`, A.`upgrade_level` FROM `%s` A RIGHT JOIN (SELECT `upgrade_id`, MAX(`id`) AS lastest_id FROM `%s` WHERE (`player_unique_id`='%s') GROUP BY `upgrade_id`) B ON A.`upgrade_id`=B.`upgrade_id` WHERE (`player_unique_id`='%s' AND `id`=`lastest_id`);";

    private final @NotNull GameManager manager;

    private @Nullable Connection connection;
    private @Nullable ExecutorService executor;

    public DbKitUpgrades(@NotNull GameManager manager) {
        this.manager = manager;
    }

    private @Nullable Connection getConnection() {
        if (connection != null) return this.connection;

        KitsConfiguration configuration = manager.getKitsConfiguration();
        if (!configuration.isEnabled()) {
            UhcCore.getPlugin().getLogger().info("Kits is disabled");
            return null;
        }
        if (!configuration.isDatabaseEnabled()) {
            UhcCore.getPlugin().getLogger().info("Kits database is disabled");
            return null;
        }

        try {
            String sql = String.format(
                    configuration.getUrl(),
                    configuration.getIp(),
                    configuration.getPort(),
                    configuration.getDb()
            );
            this.connection = DriverManager.getConnection(
                    sql,
                    configuration.getUsername(),
                    configuration.getPassword()
            );
        } catch (SQLException e) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: failed connect to database", e);
        }

        return connection;
    }

    public boolean execute(@NotNull DbKitUpgrades.StatementConsumer consumer) {
        if (executor == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: executor are null");
            return false;
        }

        Connection connection = getConnection();
        if (connection == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: connection are null");
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            executor.submit(() -> {
                try {
                    consumer.accept(statement);
                } catch (SQLException e) {
                    UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: statement execution", e);
                }

                try {
                    statement.close();
                } catch (SQLException e) {
                    UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Failed to close statement", e);
                }
            });

            return true;
        } catch (SQLException e) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: failed to create statement", e);
        }

        return false;
    }

    public boolean load(@NotNull UhcPlayer player) {
        if (executor == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: executor are null");
            return false;
        }

        execute(statement -> {
            KitsConfiguration configuration = manager.getKitsConfiguration();
            ResultSet result = statement.executeQuery(String.format(SELECT_PLAYER_UPGRADE, configuration.getTable(), configuration.getTable(), player.getUuid(), player.getUuid()));
            Map<String, Integer> levelByUpgrade = new HashMap<>();

            while (result.next()) {
                String upgradeId = result.getString(1);
                int upgradeLevel = result.getInt(2);

                levelByUpgrade.put(upgradeId, upgradeLevel);
            }

            Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> {
                for (Map.Entry<String, Integer> entry : levelByUpgrade.entrySet()) {
                    String upgradeId = entry.getKey();
                    int upgradeLevel = entry.getValue();

                    player.getKitUpgrades().setLevel(upgradeId, upgradeLevel);
                    Bukkit.getPluginManager().callEvent(new PlayerKitUpgradeLoaded(player, upgradeId, upgradeLevel));
                }
                player.getKitUpgrades().setLoaded(true);
                Bukkit.getPluginManager().callEvent(new PlayerKitUpgradesLoaded(player));
            });
        });
        return true;
    }

    public boolean save(@NotNull UhcPlayer player, @NotNull Kit kit, int level) {
        if (executor == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit: executor are null");
            return false;
        }

        KitUpgrades upgrades = kit.getUpgrades();
        if (upgrades == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "Kit has not upgrades");
            return false;
        }

        execute(statement -> {
            KitsConfiguration configuration = manager.getKitsConfiguration();
            statement.execute(String.format(INSERT_PLAYER_UPGRADE, configuration.getTable(), player.getUuid(), upgrades.getId(), level));

            Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> {
                Bukkit.getPluginManager().callEvent(new PlayerKitUpgradeUploaded(player, upgrades.getId(), level));
            });
        });
        return true;
    }

    public void start() {
        if (executor == null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
        execute(statement -> {
            UhcCore.getPlugin().getLogger().info("Creating kit upgrades table");
            KitsConfiguration configuration = manager.getKitsConfiguration();
            statement.execute(String.format(CREATE_TABLE, configuration.getTable()));
        });
    }

    public void stop() {
        if (executor != null) {
            try {
                this.executor.shutdown();
                this.executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    public interface StatementConsumer {

        void accept(@NotNull Statement statement) throws SQLException;

    }

}
