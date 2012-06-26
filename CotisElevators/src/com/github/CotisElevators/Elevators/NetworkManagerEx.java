
package com.github.CotisElevators.Elevators;

import com.github.CotisElevators.util.IOHelper;
import com.github.CotisElevators.util.IOSection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NetServerHandler;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

// Referenced classes of package com.github.CotisElevators.Elevators:
//            ElevatorsStore, Elevators, ElevatorSubRoutines, ElevatorsMoveTask, 
//            Packet240Elevators, ElevatorsStoreFormat121

public class NetworkManagerEx
{
    private class ListenerTimeout
        implements Runnable
    {

        public void run()
        {
            MuteClient(player);
        }

        private Player player;
        final NetworkManagerEx this$0;

        public ListenerTimeout(Player player)
        {
            super();
            this$0 = NetworkManagerEx.this;
            this.player = player;
        }
    }


    public NetworkManagerEx(Elevators instance)
    {
        plugin = instance;
        disabled = false;
        enabled = new ArrayList();
        listener = new ArrayList();
        ReadLastSession();
    }

    private boolean ReadLastSession()
    {
        IOSection master = IOHelper.OpenMaster(file);
        if(master == null)
            return false;
        long time = IOHelper.ReadLong(master, "time");
        if(time > System.currentTimeMillis() + 15000L)
            return false;
        ArrayList eplayers = IOHelper.ReadStringList(master, "physicsclientmodenabled", null);
        if(eplayers == null)
            return false;
        for(Iterator iterator = eplayers.iterator(); iterator.hasNext();)
        {
            String eplayer = (String)iterator.next();
            Player player = plugin.server.getPlayer(eplayer);
            if(player != null)
                enabled.add(player);
        }

        return true;
    }

    public void ShutdownSession()
    {
        IOSection master = IOHelper.NewMaster();
        IOHelper.WriteLong(master, "time", System.currentTimeMillis());
        ArrayList eplayers = new ArrayList();
        Player player;
        for(Iterator iterator = enabled.iterator(); iterator.hasNext(); eplayers.add(player.getName()))
            player = (Player)iterator.next();

        IOHelper.WriteStringList(master, "physicsclientmodenabled", eplayers);
        eplayers.clear();
        eplayers = null;
        IOHelper.SaveMaster(master, file, "Player session data");
    }

    public void ListenClient(Player player)
    {
        if(!listener.contains(player))
        {
            listener.add(player);
            ListenerTimeout timeout = new ListenerTimeout(player);
            plugin.server.getScheduler().scheduleSyncDelayedTask(plugin, timeout, 200L);
        }
    }

    public void MuteClient(Player player)
    {
        if(listener.contains(player))
            listener.remove(player);
    }

    public void ActivateClient(Player player, String args[])
    {
        if(!listener.contains(player))
            return;
        if(args.length < 3)
            return;
        if(!args[1].equals("PhysicsClientModActivation"))
            return;
        int ver = 0;
        try
        {
            ver = Integer.parseInt(args[2]);
        }
        catch(Exception exception) { }
        if(ver < 1401)
        {
            ElevatorSubRoutines.WarnPlayer(player, "Your Physics Client Mod is outdated. Visit the Elevators post at bukkit.org to get a newer one.");
            return;
        }
        if(!enabled.contains(player))
            enabled.add(player);
        plugin.log(Level.INFO, (new StringBuilder("Player \"")).append(player.getName()).append("\" is using Elevators Physics Client Mod v").append(ver).toString());
    }

    public void DeactivateClient(Player player)
    {
        if(enabled.contains(player))
            enabled.remove(player);
    }

    public void SendClientInit(ElevatorsMoveTask MoveTask)
    {
        if(((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)MoveTask.Floors.get(0)).BlockY - 2 - MoveTask.curY == 0)
            return;
        Packet240Elevators packet = InitPacket();
        if(packet == null)
        {
            return;
        } else
        {
            packet.Init(((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)MoveTask.Floors.get(0)).BlockY - 2 - MoveTask.curY, MoveTask.curY, MoveTask.BuildBlocks, MoveTask.entities, MoveTask.selfElev);
            SendClientPacket(packet);
            return;
        }
    }

    public void SendClientStop(ElevatorsMoveTask MoveTask)
    {
        Packet240Elevators packet = InitPacket();
        if(packet == null)
        {
            return;
        } else
        {
            packet.Stop(MoveTask.selfElev, MoveTask.curY);
            SendClientPacket(packet);
            return;
        }
    }

    public void SendClientRemoveBlock(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block)
    {
        Packet240Elevators packet = InitPacket();
        if(packet == null)
        {
            return;
        } else
        {
            packet.RemoveBlock(MoveTask.selfElev, block);
            SendClientPacket(packet);
            return;
        }
    }

    public void SendClientAddEntity(ElevatorsMoveTask MoveTask, Entity entity)
    {
        Packet240Elevators packet = InitPacket();
        if(packet == null)
        {
            return;
        } else
        {
            packet.AddEntity(MoveTask.selfElev, entity.getEntityId());
            SendClientPacket(packet);
            return;
        }
    }

    private Packet240Elevators InitPacket()
    {
        try
        {
            return new Packet240Elevators();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        disabled = true;
        plugin.OpWarning("Elevators - NetworkManager - Instance error. Client Mod support is being disabled. Plugin and server network versions are incompatible!");
        return null;
    }

    private void SendClientPacket(Packet240Elevators packet)
    {
        if(disabled)
            return;
        try
        {
            Player aplayer[];
            int j = (aplayer = plugin.server.getOnlinePlayers()).length;
            for(int i = 0; i < j; i++)
            {
                Player player = aplayer[i];
                if(enabled.contains(player))
                {
                    plugin.log(Level.INFO, (new StringBuilder("Elevators NetworkManager: Packet240-")).append(packet.pType).append(" sent to player \"").append(player.getName()).append("\"").toString());
                    
                    ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            disabled = true;
            plugin.OpWarning("Elevators - NetworkManager - Instance error. Client Mod support is being disabled. Plugin and server network versions are incompatible!");
        }
    }

    private static final int clientver = 1401;
    private static final String file;
    private Elevators plugin;
    private ArrayList enabled;
    private ArrayList listener;
    private boolean disabled;

    static 
    {
        file = (new StringBuilder(String.valueOf(ElevatorsStore.dir12))).append(ElevatorsStore.fs).append("NetworkSession.properties").toString();
    }
}