package com.gmail.val59000mc.lobby.pvp;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.LobbyPvpConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import com.gmail.val59000mc.utils.equipment.EquipmentUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbLobbyPvp {

    private static final String DEFAULT_URL = "jdbc:mysql://%s:%s/%s";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (player_unique_id VARCHAR(36) NOT NULL, slot_id VARCHAR(32) NOT NULL, slot VARCHAR(32) NOT NULL, primary key (player_unique_id, slot_id));";
    private static final String INSERT_PLAYER_SLOT = "INSERT INTO `%s` (`player_unique_id`, `slot_id`, `slot`) VALUES ('%s', '%s', '%s') ON DUPLICATE KEY UPDATE `slot`='%s';";
    private static final String SELECT_PLAYER_SLOTS = "SELECT * from `%s` where `player_unique_id`='%s';";

    private final @NotNull GameManager manager;

    private @Nullable Connection connection;
    private @Nullable ExecutorService executor;

    public DbLobbyPvp(@NotNull GameManager manager) {
        this.manager = manager;
    }

    private @Nullable Connection getConnection() {
        if (connection != null) return this.connection;

        final LobbyPvpConfiguration configuration = manager.getLobbyPvpConfiguration();
        if (!configuration.isEnabled()) {
            UhcCore.getPlugin().getLogger().info("LPZ is disabled. Skip db initialization...");
            return null;
        }

        if (!configuration.isDatabaseEnabled()) {
            UhcCore.getPlugin().getLogger().info("LPZ database is disabled. Skip db initialization...");
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
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Failed connect to database", e);
        }

        return connection;
    }

    public boolean execute(@NotNull StatementConsumer consumer) {
        if (executor == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Executor are null");
            return false;
        }

        Connection connection = getConnection();
        if (connection == null) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Connection are null");
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            executor.submit(() -> {
                try {
                    consumer.accept(statement);
                } catch (SQLException e) {
                    UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Failed to execute statement", e);
                }

                try {
                    statement.close();
                } catch (SQLException e) {
                    UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Failed to close statement", e);
                }
            });

            return true;
        } catch (SQLException e) {
            UhcCore.getPlugin().getLogger().log(Level.SEVERE, "LPZ: Failed to create statement", e);
        }

        return false;
    }

    public boolean load(@NotNull UhcPlayer player) {
        final LobbyPvpConfiguration configuration = manager.getLobbyPvpConfiguration();

        if (!configuration.isEnabled() || !configuration.isDatabaseEnabled()) {
            return true;
        }

        final Logger logger = UhcCore.getPlugin().getLogger();
        logger.log(Level.INFO, "LPZ: Trying to load " + player.getName() + " profile...");

        if (executor == null) {
            logger.log(Level.SEVERE, "LPZ: Executor are null");
            return false;
        }

        execute(statement -> {
            final ResultSet result = statement.executeQuery(String.format(SELECT_PLAYER_SLOTS, configuration.getTable(), player.getUuid()));

            while (result.next()) {
                final String slotId = result.getString(2);
                final String slot = result.getString(3);

                final EquipmentSlot equipmentSlot = EquipmentUtils.from(slot);

                player.getLobbyPvpInventory().getPlayerEquipment().put(slotId, equipmentSlot);
            }
            player.getLobbyPvpInventory().setLoaded(true);
            logger.log(Level.INFO, "LPZ: Profile " + player.getName() + " success loaded.");
        });
        return true;
    }

    public boolean save(@NotNull UhcPlayer player) {
        final Logger logger = UhcCore.getPlugin().getLogger();
        if (executor == null) {
            logger.log(Level.SEVERE, "LPZ: Executor are null");
            return false;
        }

        logger.log(Level.INFO, "LPZ: Trying to save " + player.getName() + " profile...");
        final PlayerLobbyPvpInventory inventory = player.getLobbyPvpInventory();

        execute(statement -> {
            final LobbyPvpConfiguration configuration = manager.getLobbyPvpConfiguration();

            for (Map.Entry<String, EquipmentSlot> entry : inventory.getPlayerEquipment().entrySet()) {
                final String key = entry.getKey();
                final EquipmentSlot value = entry.getValue();

                final String slot = value.buildId();
                statement.execute(String.format(INSERT_PLAYER_SLOT, configuration.getTable(), player.getUuid(), key, slot, slot));
            }

            logger.log(Level.INFO, "LPZ: Profile " + player.getName() + " success saved.");
        });
        return true;
    }

    public void start() {
        if (executor == null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
        execute(statement -> {
            UhcCore.getPlugin().getLogger().info("LPZ: creating table...");
            final LobbyPvpConfiguration configuration = manager.getLobbyPvpConfiguration();
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
