
package com.github.CotisElevators.Elevators;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators, ElevatorsStore

public class ElevatorsWorldListener implements Listener
{

    public ElevatorsWorldListener(Elevators instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event)
    {
        plugin.store.ReadWorldStore(event.getWorld(), true);
    }

    private Elevators plugin;
}