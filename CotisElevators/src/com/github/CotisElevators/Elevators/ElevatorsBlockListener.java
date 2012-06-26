
package com.github.CotisElevators.Elevators;

import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators, ElevatorsWaitControl, ElevatorsStore, ElevatorSubRoutines, 
//            ElevatorsMoveTask, ElevatorsValidationClass, ElevatorsStoreFormat121

public class ElevatorsBlockListener implements Listener
{

    public ElevatorsBlockListener(Elevators instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled())
        {
            return;
        } else
        {
            plugin.wctrl.onBlockPlace(event);
            return;
        }
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = plugin.store.FindNearbyElevatorAcc(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ(), event.getBlock().getWorld());
        if(Elev == null)
            return;
        int BlockType = ElevatorSubRoutines.CheckSpecialBlock(plugin.GetMoveTask(Elev), event.getBlock().getLocation());
        if(BlockType > -1)
            if(BlockType == 4)
                event.setNewCurrent(event.getOldCurrent());
            else
            if(event.getNewCurrent() > 0 && event.getOldCurrent() == 0)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = ElevatorSubRoutines.GetSpecialBlock(plugin.GetMoveTask(Elev), event.getBlock().getLocation());
                if(BlockType == 0)
                {
                    plugin.log(Level.INFO, (new StringBuilder("Elevator movement initiated via CALL_BLOCK ")).append(SpecialBlock.toString()).toString());
                    plugin.MoveElevatorCon(Elev, SpecialBlock, event.getBlock().getLocation());
                } else
                if(BlockType == 5)
                {
                    plugin.log(Level.INFO, (new StringBuilder("Elevator movement initiated via DIRECT_BLOCK ")).append(SpecialBlock.toString()).toString());
                    ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = ElevatorSubRoutines.GetSpecialBlock(Elev, SpecialBlock.targetID);
                    plugin.MoveElevator(Elev, CallBlock, event.getBlock().getLocation(), "", "");
                } else
                {
                    plugin.GetMoveTask(Elev).AddPress(SpecialBlock);
                    event.setNewCurrent(0);
                }
            }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())
            return;
        Block block = event.getBlock();
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = plugin.store.FindNearbyElevatorAcc(block.getX(), block.getY(), block.getZ(), block.getWorld());
        if(Elev != null && ElevatorSubRoutines.IsMovingPart(plugin.GetMoveTask(Elev), block))
        {
            event.setCancelled(true);
            return;
        } else
        {
            plugin.Valids.add(new ElevatorsValidationClass(event.getPlayer(), event.getBlock(), 1, plugin));
            return;
        }
    }

    public static Elevators plugin;
}