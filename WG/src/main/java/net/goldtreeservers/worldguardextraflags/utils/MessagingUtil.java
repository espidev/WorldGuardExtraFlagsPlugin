package net.goldtreeservers.worldguardextraflags.utils;

import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessagingUtil {
    public static void sendStringToAction(Player p, String message) {
        message = LegacyComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.INSTANCE.deserialize(message, '&'));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/title " + p.getName() + " actionbar " + message);
    }
}
