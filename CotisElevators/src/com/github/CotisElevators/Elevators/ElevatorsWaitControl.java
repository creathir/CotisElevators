
package com.github.CotisElevators.Elevators;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators, ElevatorsStore, ElevatorsStoreFormat121

public class ElevatorsWaitControl
{
    private class WaitControl
    {

        public String playername;
        public int BlockType;
        public String Parameter[];
        final ElevatorsWaitControl this$0;

        private WaitControl()
        {
            super();
            this$0 = ElevatorsWaitControl.this;
        }

        WaitControl(WaitControl waitcontrol)
        {
            this();
        }
    }


    public ElevatorsWaitControl(Elevators instance)
    {
        plugin = instance;
        WaitControls = new ArrayList();
    }

    public void QueueWaitControl(Player player, int BlockType, String Parameter[])
    {
        WaitControl Result = QueueWaitControl(player, BlockType);
        Result.Parameter = Parameter;
    }

    public WaitControl QueueWaitControl(Player player, int BlockType)
    {
        WaitControl existing = FindWaitControl(player);
        if(existing != null)
            WaitControls.remove(existing);
        WaitControl Result = new WaitControl(null);
        Result.playername = player.getName();
        Result.BlockType = BlockType;
        WaitControls.add(Result);
        return Result;
    }

    private WaitControl FindWaitControl(Player player)
    {
        for(Iterator iterator = WaitControls.iterator(); iterator.hasNext();)
        {
            WaitControl ctrlsearch = (WaitControl)iterator.next();
            if(ctrlsearch.playername.equalsIgnoreCase(player.getName()))
                return ctrlsearch;
        }

        return null;
    }

    public boolean RemoveGlassWaitControl(Player player)
    {
        WaitControl existing = FindWaitControl(player);
        if(existing == null)
            return false;
        if(existing.BlockType == 6)
        {
            WaitControls.remove(existing);
            return true;
        } else
        {
            return false;
        }
    }

    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        WaitControl existing = FindWaitControl(player);
        if(existing == null)
            return;
        Block newBlock = event.getBlock();
        if(newBlock.getTypeId() == 20 && existing.BlockType == 6)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = plugin.store.FindNearbyElevatorMod(newBlock.getX(), newBlock.getY(), newBlock.getZ(), newBlock.getWorld(), player, "Glass door placement failed!");
            if(Elev != null)
                plugin.CreateGlassDoor(Elev, newBlock, existing.Parameter, player);
        }
    }

    public void onBlockRightClick(PlayerInteractEvent event)
    {
        WaitControl existing = FindWaitControl(event.getPlayer());
        if(existing != null)
        {
            if(existing.BlockType == 6)
                return;
            if(existing.BlockType == 0)
                plugin.CreateCallBlock(event.getPlayer(), event.getClickedBlock().getLocation(), existing.Parameter);
            if(existing.BlockType == 1)
                plugin.CreateUpBlock(event.getPlayer(), event.getClickedBlock().getLocation());
            if(existing.BlockType == 2)
                plugin.CreateDownBlock(event.getPlayer(), event.getClickedBlock().getLocation());
            if(existing.BlockType == 4)
                plugin.CreateRedstoneOut(event.getPlayer(), event.getClickedBlock(), existing.Parameter);
            if(existing.BlockType == 5)
                plugin.CreateDirectBlock(event.getPlayer(), event.getClickedBlock().getLocation(), existing.Parameter);
            WaitControls.remove(existing);
        }
    }

    private ArrayList WaitControls;
    private Elevators plugin;
}