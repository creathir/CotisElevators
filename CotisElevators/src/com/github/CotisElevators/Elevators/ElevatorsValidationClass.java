
package com.github.CotisElevators.Elevators;

import java.util.ArrayList;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators

public class ElevatorsValidationClass
    implements Runnable
{

    public void run()
    {
        if(tf == 1)
            plugin.onBlockDestroy(pl, bl);
        plugin.Valids.remove(this);
    }

    ElevatorsValidationClass(Player player, Block block, int TargetFunction, Elevators instance)
    {
        plugin = instance;
        pl = player;
        bl = block;
        tf = TargetFunction;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 2L);
    }

    public static final int BLOCK_DESTROY = 1;
    private Elevators plugin;
    private Player pl;
    private Block bl;
    private int tf;
}