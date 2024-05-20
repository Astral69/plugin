package com.xinecraft.minetrax.bukkit.tasks;

import com.xinecraft.minetrax.bukkit.MinetraxBukkit;
import com.xinecraft.minetrax.common.data.PlayerData;
import de.themoep.minedown.adventure.MineDown;
import org.bukkit.entity.Player;

public class AccountLinkReminderTask implements Runnable {
    @Override
    public void run() {
        // Get list of all online players and loop thru them
        for (Player player : MinetraxBukkit.getPlugin().getServer().getOnlinePlayers()) {
            PlayerData playerData = MinetraxBukkit.getPlugin().playersDataMap.get(player.getUniqueId().toString());
            if (playerData != null && playerData.is_verified) continue;

            for (String line : MinetraxBukkit.getPlugin().getRemindPlayerToLinkMessage()) {
                MinetraxBukkit.getPlugin().adventure().player(player).sendMessage(MineDown.parse(line));
            }
        }
    }
}
