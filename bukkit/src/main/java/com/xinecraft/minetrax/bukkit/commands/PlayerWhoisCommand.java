package com.xinecraft.minetrax.bukkit.commands;

import com.xinecraft.minetrax.bukkit.MinetraxBukkit;
import com.xinecraft.minetrax.common.utils.LoggingUtil;
import com.xinecraft.minetrax.common.utils.WhoisUtil;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PlayerWhoisCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        // Do nothing if the feature is disabled.
        if (!MinetraxBukkit.getPlugin().getIsWhoisOnCommandEnabled()) {
            return false;
        }

        String username = null;
        if (strings.length > 0) {
            username = strings[0];
        }
        boolean shouldBroadcast = false;
        Player senderPlayer = null;
        if ((commandSender instanceof Player)) {
            senderPlayer = (Player) commandSender;
        } else {
            shouldBroadcast = true;
        }

        if (username == null) {
            if (senderPlayer != null) {
                username = senderPlayer.getName();
            } else {
                MinetraxBukkit.getPlugin().getLogger().info("Username is required! Eg: ww notch");
                return false;
            }
        }

        // Check if Player is online
        Player player = Bukkit.getPlayerExact(username);
        String uuid = null;
        String ipAddress = null;
        if (player != null && player.isOnline()) {
            uuid = player.getUniqueId().toString();
            ipAddress = Objects.requireNonNull(player.getAddress()).getHostString();
        }

        this.handleWhois(uuid, username, ipAddress, shouldBroadcast, senderPlayer);
        return true;
    }

    private void handleWhois(String uuid, String username, String ipAddress, Boolean shouldBroadcast, Player senderPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(MinetraxBukkit.getPlugin(), () -> {
            try {
                Boolean isFromJoinEvent = false;
                Boolean isRanByAdminPlayer = senderPlayer != null && senderPlayer.hasPermission("minetrax.admin");
                List<String> sayList = WhoisUtil.forPlayerSync(
                        username,
                        uuid,
                        ipAddress,
                        shouldBroadcast,
                        isFromJoinEvent,
                        isRanByAdminPlayer,
                        MinetraxBukkit.getPlugin().getWhoisNoMatchFoundMessage(),
                        MinetraxBukkit.getPlugin().getWhoisPlayerOnFirstJoinMessage(),
                        MinetraxBukkit.getPlugin().getWhoisPlayerOnJoinMessage(),
                        MinetraxBukkit.getPlugin().getWhoisPlayerOnCommandMessage(),
                        MinetraxBukkit.getPlugin().getWhoisPlayerOnAdminCommandMessage(),
                        MinetraxBukkit.getPlugin().getWhoisMultiplePlayersTitleMessage(),
                        MinetraxBukkit.getPlugin().getWhoisMultiplePlayersListMessage()
                );
                if (sayList != null) {
                    for (String line : sayList) {
                        Tell(senderPlayer, line);
                    }
                }
            } catch (Exception e) {
                LoggingUtil.warntrace(e);
            }
        });
    }

    private void Tell(Player player, String message) {
        if (player == null) {
            MinetraxBukkit.getPlugin().adventure().players().sendMessage(MineDown.parse(message));
        } else {
            MinetraxBukkit.getPlugin().adventure().player(player).sendMessage(MineDown.parse(message));
        }
    }
}
