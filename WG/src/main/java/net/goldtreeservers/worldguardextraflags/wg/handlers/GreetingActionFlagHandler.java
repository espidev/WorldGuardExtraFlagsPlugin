package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.google.common.collect.Sets;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.utils.MessagingUtil;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

public class GreetingActionFlagHandler extends HandlerWrapper {

    public static final Factory FACTORY(Plugin plugin)
    {
        return new Factory(plugin);
    }

    public static class Factory extends HandlerWrapper.Factory<GreetingActionFlagHandler>
    {
        public Factory(Plugin plugin)
        {
            super(plugin);
        }

        @Override
        public GreetingActionFlagHandler create(Session session)
        {
            return new GlideFlagHandler(this.getPlugin(), session);
        }
    }

    private Set<String> lastActionStack = Collections.emptySet();

    protected GreetingActionFlagHandler(Plugin plugin, Session session)
    {
        super(plugin, session);
    }

    private Set<String> getMessages(LocalPlayer p, ApplicableRegionSet set, Flag<String> flag)
    {
        return Sets.newLinkedHashSet(set.queryAllValues(p, flag));
    }

    @Override
    public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
    {
        lastActionStack = sendAndCollect(WorldGuardPlugin.inst().wrapPlayer(player), player, toSet, Flags.GREET_ACTION, lastActionStack, MessagingUtil::sendStringToAction);
        return true;
    }

    private Set<String> sendAndCollect(LocalPlayer player, Player p, ApplicableRegionSet toSet, Flag<String> flag, Set<String> stack, BiConsumer<Player, String> msgFunc)
    {
        Collection<String> messages = getMessages(player, toSet, flag);

        for (String message : messages)
        {
            if (!stack.contains(message))
            {
                msgFunc.accept(p, message);
                break;
            }
        }

        stack = Sets.newHashSet(messages);

        if (!stack.isEmpty())
        {
            // Due to flag priorities, we have to collect the lower
            // priority flag values separately
            for (ProtectedRegion region : toSet)
            {
                String message = region.getFlag(flag);
                if (message != null)
                {
                    stack.add(message);
                }
            }
        }

        return stack;
    }
}
