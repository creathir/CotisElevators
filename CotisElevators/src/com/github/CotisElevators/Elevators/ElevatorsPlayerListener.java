
package com.github.CotisElevators.Elevators;

import com.github.CotisElevators.util.ConfigHelper;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators, NetworkManagerEx, ElevatorsWaitControl, ElevatorsStore, 
//            ElevatorSubRoutines, ElevatorsStoreFormat121

public class ElevatorsPlayerListener implements Listener
{
    public class PasswordControl
    {

        public void Activate(String password)
        {
            if(elevpw.equals("") && !Elev.password.equals(""))
            {
                if(password.equals(Elev.password))
                    ElevatorsPlayerListener.plugin.MoveElevator(Elev, CallBlock, callloc, password, "");
                else
                    ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(ElevatorsPlayerListener.plugin.store.config, "WrongPasswordMessage"));
            } else
            if(password.equals(CallBlock.password))
                ElevatorsPlayerListener.plugin.MoveElevator(Elev, CallBlock, callloc, elevpw, password);
            else
                ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(ElevatorsPlayerListener.plugin.store.config, "WrongPasswordMessage"));
            pwctrls.remove(this);
            Elev = null;
            CallBlock = null;
            callloc = null;
            elevpw = null;
            player = null;
        }

        public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev;
        public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock;
        public Location callloc;
        public String elevpw;
        public Player player;
        final ElevatorsPlayerListener this$0;

        public PasswordControl(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, Location callloc, String elevpw, Player player)
        {
            super();
            this$0 = ElevatorsPlayerListener.this;
            this.Elev = Elev;
            this.CallBlock = CallBlock;
            this.callloc = callloc;
            this.elevpw = elevpw;
            this.player = player;
            if(elevpw.equals("") && !Elev.password.equals(""))
                ElevatorSubRoutines.InfoPlayer(player, ConfigHelper.GetString(ElevatorsPlayerListener.plugin.store.config, "ElevatorPasswordRequiredMessage"));
            else
                ElevatorSubRoutines.InfoPlayer(player, ConfigHelper.GetString(ElevatorsPlayerListener.plugin.store.config, "FloorPasswordRequiredMessage").replaceAll("%F", CallBlock.Parameter));
            pwctrls.add(this);
        }
    }


    public ElevatorsPlayerListener(Elevators instance)
    {
        plugin = instance;
        pwctrls = new ArrayList();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.net.ListenClient(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.net.DeactivateClient(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
            plugin.wctrl.onBlockRightClick(event);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event)
    {
        if(event.isCancelled())
            return;
        for(Iterator iterator = pwctrls.iterator(); iterator.hasNext();)
        {
            PasswordControl handler = (PasswordControl)iterator.next();
            if(handler.player == event.getPlayer())
            {
                event.setCancelled(true);
                handler.Activate(event.getMessage());
                break;
            }
        }

    }

    public static Elevators plugin;
    public ArrayList pwctrls;
}