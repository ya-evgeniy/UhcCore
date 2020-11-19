package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.kit.db.DbKitUpgrades;
import com.gmail.val59000mc.kit.table.KitTableRegistry;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class KitsManager {

    private static final Random RANDOM = new Random();

    private final KitTableRegistry tableRegistry = new KitTableRegistry();

    private final Map<String, Kit> kitById = new HashMap<>();
    private final Map<KitGroup, List<Kit>> kitsByGroup = new HashMap<>();
    private final Map<String, KitGroup> groupById = new HashMap<>();
    private final Map<String, KitTableSet> setById = new HashMap<>();

    private final @NotNull GameManager gameManager;
    private final @NotNull DbKitUpgrades dbKitUpgrades;

    public KitsManager(@NotNull GameManager gameManager) {
        this.gameManager = gameManager;
        this.dbKitUpgrades = new DbKitUpgrades(gameManager);

        this.groupById.put("default", new KitGroup("default", 1));
    }

    public void registerKit(@NotNull Kit kit) {
        Kit oldKit = this.kitById.put(kit.getId(), kit);
        if (oldKit != null) {
            KitGroup group = oldKit.getGroup();
            List<Kit> kits = this.kitsByGroup.get(group);
            if (kits != null) {
                kits.remove(oldKit);
            }
        }
        List<Kit> kits = this.kitsByGroup.computeIfAbsent(kit.getGroup(), group -> new ArrayList<>());
        kits.add(kit);
    }

    public void registerGroup(@NotNull KitGroup group) {
        KitGroup oldGroup = this.groupById.put(group.getId(), group);
        if (oldGroup != null) {
            List<Kit> kits = this.kitsByGroup.remove(oldGroup);
            if (kits != null) this.kitsByGroup.put(group, kits);
        }
    }

    public void registerSet(@NotNull KitTableSet set) {
        this.setById.put(set.getId(), set);
    }

    public @NotNull KitGroup getDefaultGroup() {
        return Objects.requireNonNull(this.groupById.get("default"));
    }

    public boolean hasKits() {
        return !this.kitById.isEmpty();
    }

    public void giveKit(@Nullable Kit kit, @NotNull Player player) {
        if (kit == null) return;

        kit.give(player, this, RANDOM);
    }

    public @Nullable Kit getKit(@NotNull String kitId) {
        return this.kitById.get(kitId);
    }

    public @Nullable Kit getRandomKit(@NotNull UhcPlayer player) {
        try {
            Player bukkitPlayer = player.getPlayer();
            List<Kit> availableKits = this.kitById.values().stream()
                    .filter(kit -> bukkitPlayer.hasPermission("uhccore.kit." + kit.getId()))
                    .collect(Collectors.toList());

            if (availableKits.isEmpty()) return null;
            int index = RANDOM.nextInt(availableKits.size());
            return availableKits.get(index);
        } catch (UhcPlayerNotOnlineException ignore) {
            return null;
        }
    }

    public @Nullable KitTableSet getSet(@NotNull String setId) {
        return this.setById.get(setId);
    }

    public @NotNull List<Kit> getKits(@NotNull KitGroup group) {
        List<Kit> kits = this.kitsByGroup.get(group);
        if (kits == null) return Collections.emptyList();
        return new ArrayList<>(kits);
    }

    public @Nullable KitGroup getGroup(@Nullable String id) {
        if (id == null) return null;
        return groupById.get(id);
    }

    public @NotNull List<KitGroup> getGroups() {
        return new ArrayList<>(this.groupById.values());
    }

    public @NotNull List<Kit> getKits() {
        return new ArrayList<>(this.kitById.values());
    }

    public int countKits() {
        return this.kitById.size();
    }

    public KitTableRegistry getTableRegistry() {
        return tableRegistry;
    }

    public @NotNull GameManager getGameManager() {
        return gameManager;
    }

    public DbKitUpgrades getDbKitUpgrades() {
        return dbKitUpgrades;
    }

}
