package com.gmail.val59000mc.kit.upgrade;

import com.gmail.val59000mc.players.UhcPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DbKitUpgrades {

    private static final String URL = "jdbc:mysql://%s:%s/%s";

    private final String ip;
    private final int port;

    private final String db;

    private @Nullable ExecutorService executor;

    public DbKitUpgrades(String ip, int port, String db) {
        this.ip = ip;
        this.port = port;
        this.db = db;
    }

    private boolean connect(@NotNull Consumer<Connection> consumer) {
        if (executor == null) return false;

        try (Connection connection = DriverManager.getConnection(String.format(URL, ip, port, db))) {
            consumer.accept(connection);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean load(@NotNull UhcPlayer player) {
        if (executor == null) return false;

        return connect(connection -> {
//            connection.
        });
    }

    public boolean save(@NotNull UhcPlayer player) {
        if (executor == null) return false;

        return connect(connection -> {

        });
    }

    public void start() {
        if (executor == null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
    }

    public void stop() {
        if (executor == null) return;

        try {
            this.executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
