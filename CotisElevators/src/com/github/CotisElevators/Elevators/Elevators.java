
package com.github.CotisElevators.Elevators;

import com.github.CotisElevators.util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

// Referenced classes of package com.gmail.creathir.Elevators:
//            ElevatorsStore, NetworkManagerEx, ElevatorsWorldListener, ElevatorsBlockListener, 
//            ElevatorsPlayerListener, ElevatorsPluginCommunicator, ElevatorsWaitControl, ElevatorsStoreFormat121, 
//            ElevatorSubRoutines, ElevatorsMoveTask

public class Elevators extends JavaPlugin
{

    public Elevators()
    {
        flog = null;
    }

    public void onDisable()
    {
        store.WriteStore(null);
        net.ShutdownSession();
        log(Level.INFO, "Elevators plugin disabled!");
        if(debugging)
            flog.Close();
    }

    public void onEnable()
    {
        server = getServer();
        log = Logger.getLogger("Minecraft");
        PluginDescriptionFile pdfFile = getDescription();
        if(debugging)
            EnableDebugging();
        MoveTasks = new ArrayList();
        Valids = new ArrayList();
        store = new ElevatorsStore(this);
        if(ConfigHelper.GetBoolean(store.config, "Debugging-Log") && !debugging)
            EnableDebugging();
        worldListener = new ElevatorsWorldListener(this);
        blockListener = new ElevatorsBlockListener(this);
        playerListener = new ElevatorsPlayerListener(this);
        comm = new ElevatorsPluginCommunicator(this);
        wctrl = new ElevatorsWaitControl(this);
        net = new NetworkManagerEx(this);
        PluginManager pm = server.getPluginManager();       
        pm.registerEvent(org.bukkit.event.block.BlockRedstoneEvent, blockListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.block.BlockBreakEvent, blockListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.block.BlockPlaceEvent, blockListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.player.PlayerChatEvent, playerListener, org.bukkit.event.EventPriority.LOW, this);
        pm.registerEvent(org.bukkit.event.player.PlayerInteractEvent, playerListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.world.WorldLoadEvent, worldListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.player.PlayerJoinEvent, playerListener, org.bukkit.event.EventPriority.NORMAL, this);
        pm.registerEvent(org.bukkit.event.player.PlayerQuitEvent, playerListener, org.bukkit.event.EventPriority.NORMAL, this);
        String message = "";
        if(comm.PermissionsEnabled)
            message = (new StringBuilder(String.valueOf(message))).append(" and linked to Permissions").toString();
        String message2 = store.ElevMessage(store.data.Database.size(), server.getWorlds());
        OpInfo((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" version ").append(pdfFile.getVersion()).append(debugging ? " debugging build" : "").append(" is enabled").append(message).append("! ").append(message2).toString());
    }

    private void EnableDebugging()
    {
        debugging = true;
        try
        {
            flog = new FileLogger("Elevators", (new StringBuilder(String.valueOf(ElevatorsStore.dir12))).append(System.getProperty("file.separator")).append("DebuggingLog.log").toString(), log);
        }
        catch(Exception e)
        {
            flog = null;
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[])
    {
        String CommandName = command.getName();
        if(!CommandName.equalsIgnoreCase("elevator") && !CommandName.equalsIgnoreCase("elevators") && !CommandName.equalsIgnoreCase("elev"))
            break MISSING_BLOCK_LABEL_307;
        if(args.length <= 0)
            break MISSING_BLOCK_LABEL_289;
        log(Level.INFO, (new StringBuilder("command received: \"")).append(command.getName()).append(" ").append(ElevatorSubRoutines.GetList(args, " ")).append("\" by ").append((sender instanceof Player) ? ((Player)sender).getName() : "CONSOLE").toString());
        if(!(sender instanceof Player))
            break MISSING_BLOCK_LABEL_232;
        if(!args[0].equals("NetworkPacket"))
            break MISSING_BLOCK_LABEL_156;
        net.ActivateClient((Player)sender, args);
        return true;
        try
        {
            args = store.commands.Substitute(args);
            log(Level.INFO, (new StringBuilder("command translated to: \"")).append(command.getName()).append(" ").append(ElevatorSubRoutines.GetList(args, " ")).append("\"").toString());
            return AnalyzeCommands(args, (Player)sender);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        break MISSING_BLOCK_LABEL_298;
        if(!args[0].equalsIgnoreCase("info"))
            break MISSING_BLOCK_LABEL_278;
        if(store.InfoFile())
            sender.sendMessage("Elevator's debugging info file created. \\plugins\\elevators\\DebuggingInfo.txt");
        else
            sender.sendMessage("Error while creating debugging info file!");
        return true;
        sender.sendMessage("This command can be only accessed by players.");
        return false;
        return false;
        sender.sendMessage("WARNING: Elevators - command analyzation exception.");
        return false;
    }

    private boolean AnalyzeCommands(String commands[], Player player)
    {
        if(commands[0].equalsIgnoreCase("?") || commands[0].equalsIgnoreCase("help"))
            return false;
        if(commands[0].equalsIgnoreCase("permission") || commands[0].equalsIgnoreCase("permissions"))
        {
            String permission = "unauthorized";
            if(comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.OPERATOR))
                permission = "operator";
            else
            if(comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.ADVANCED))
                permission = "advanced builder";
            else
            if(comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.BUILDER))
                permission = "basic builder";
            ElevatorSubRoutines.InfoPlayer(player, (new StringBuilder("Your Permission level is: ")).append(permission).toString());
            return false;
        }
        if(!comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.BUILDER))
        {
            ElevatorSubRoutines.WarnPlayer(player, "You don't have the permission to acces this command level (basic) or your entered command is wrong. Type /elev help or /elev permission.");
            return true;
        }
        if(commands[0].equalsIgnoreCase("create"))
            return CreateElevator(player, commands);
        if(commands[0].equalsIgnoreCase("remove") || commands[0].equalsIgnoreCase("destroy"))
            return RemoveElevator(player);
        if(commands[0].equalsIgnoreCase("call") || commands[0].equalsIgnoreCase("floor"))
            return PrepCallBlock(player, commands);
        if(commands[0].equalsIgnoreCase("up"))
            return PrepUpBlock(player);
        if(commands[0].equalsIgnoreCase("down"))
            return PrepDownBlock(player);
        if(commands[0].equalsIgnoreCase("redstoneout"))
            return PrepRedstoneOut(player, commands);
        if(commands[0].equalsIgnoreCase("go") || commands[0].equalsIgnoreCase("direct"))
            return PrepDirectBlock(player, commands);
        if(commands[0].equalsIgnoreCase("glassdoor"))
            return PrepGlassDoor(player, commands);
        if(commands[0].equalsIgnoreCase("finish"))
            return FinishActions(player);
        if(commands[0].equalsIgnoreCase("glassremove"))
            return RemoveGlassDoors(player, commands);
        if(commands[0].equalsIgnoreCase("give"))
            return GiveOwnerStatus(player, commands);
        if(commands[0].equalsIgnoreCase("lock"))
            return LockCabin(player);
        if(!comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.ADVANCED))
        {
            ElevatorSubRoutines.WarnPlayer(player, "You don't have the permission to acces this command level (advanced) or your entered command is wrong. Type /elev help or /elev permission.");
            return true;
        }
        if(commands[0].equalsIgnoreCase("pw") || commands[0].equalsIgnoreCase("password"))
            return SetPassword(player, commands);
        if(commands[0].equalsIgnoreCase("user"))
            return SetUsers(player, commands);
        else
            return false;
    }

    public boolean log(Level level, String message)
    {
        if(debugging)
        {
            flog.log(level, message);
            return true;
        } else
        {
            return false;
        }
    }

    public void OpWarning(String message)
    {
        if(!log(Level.WARNING, message))
            log.warning(message);
        for(int i = 0; i < server.getOnlinePlayers().length; i++)
            if(server.getOnlinePlayers()[i].isOp())
                server.getOnlinePlayers()[i].sendMessage((new StringBuilder()).append(ChatColor.RED).append("WARNING: ").append(message).toString());

    }

    public void OpInfo(String message)
    {
        if(!log(Level.INFO, message))
            log.info(message);
        for(int i = 0; i < server.getOnlinePlayers().length; i++)
            if(server.getOnlinePlayers()[i].isOp())
                server.getOnlinePlayers()[i].sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("INFO: ").append(message).toString());

    }

    private boolean CreateElevator(Player player, String commands[])
    {
        Location location = player.getLocation();
        World world = player.getWorld();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        int typeID = world.getBlockTypeIdAt(newloc[0], newloc[1], newloc[2]);
        byte typeData = world.getBlockAt(newloc[0], newloc[1], newloc[2]).getData();
        if(typeID != 44)
        {
            newloc[1]--;
            typeID = world.getBlockTypeIdAt(newloc[0], newloc[1], newloc[2]);
        }
        if(typeID == 0)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Elevator creation failed! You are standing..  ..in the AIR?!").toString());
            return true;
        }
        int elX1 = newloc[0];
        int elX2 = newloc[0];
        int elZ1 = newloc[2];
        int elZ2 = newloc[2];
        for(; world.getBlockTypeIdAt(elX1 + 1, newloc[1], newloc[2]) == typeID && world.getBlockTypeIdAt(elX1 + 1, newloc[1] + 1, newloc[2]) == 0; elX1++);
        for(; world.getBlockTypeIdAt(elX2 - 1, newloc[1], newloc[2]) == typeID && world.getBlockTypeIdAt(elX2 - 1, newloc[1] + 1, newloc[2]) == 0; elX2--);
        for(; world.getBlockTypeIdAt(newloc[0], newloc[1], elZ1 + 1) == typeID && world.getBlockTypeIdAt(newloc[0], newloc[1] + 1, elZ1 + 1) == 0; elZ1++);
        for(; world.getBlockTypeIdAt(newloc[0], newloc[1], elZ2 - 1) == typeID && world.getBlockTypeIdAt(newloc[0], newloc[1] + 1, elZ2 - 1) == 0; elZ2--);
        int elW = (elX1 - elX2) + 1;
        int elH = (elZ1 - elZ2) + 1;
        if(elW * elH > ConfigHelper.GetInteger(store.config, "MaximumGroundBlocks"))
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Elevator creation denied! ").append(elW).append("x").append(elH).append(", forget it!").toString());
            return true;
        }
        for(int iX = elX2; iX <= elX1; iX++)
        {
            for(int iZ = elZ2; iZ <= elZ1; iZ++)
            {
                int chkID = world.getBlockTypeIdAt(iX, newloc[1], iZ);
                if(chkID != typeID)
                {
                    player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Elevator creation failed! You HAVE TO use ONE type of block for the WHOLE elevator ground!").toString());
                    return true;
                }
                if(store.forbidden.Forbidden(chkID))
                {
                    ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(store.config, "ForbiddenBlockMessage").replaceAll("%I", (new StringBuilder()).append(chkID).toString()));
                    return true;
                }
                if(store.FindNearbyElevatorAcc(iX, newloc[1], iZ, world) != null)
                {
                    player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Elevator creation denied! Found another nearby elevator.").toString());
                    return true;
                }
            }

        }

        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.AddElevator(elX1, elX2, newloc[1], elZ1, elZ2, typeID, typeData, player.getWorld().getName(), player.getName());
        boolean us = SetUsers(Elev, null, commands, player);
        boolean pw = SetPassword(Elev, null, commands, player);
        ElevatorSubRoutines.CongratPlayer(player, (new StringBuilder()).append(elW).append("x").append(elH).append(" Elevator creation").append(ElevatorSubRoutines.ProtectionMessage(pw, us)).append("succesfull!").toString());
        return true;
    }

    private boolean PrepCallBlock(Player player, String commands[])
    {
        wctrl.QueueWaitControl(player, 0, commands);
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Call-Block creation outstanding...").toString());
        return true;
    }

    private boolean PrepUpBlock(Player player)
    {
        wctrl.QueueWaitControl(player, 1);
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Up-Block creation outstanding...").toString());
        return true;
    }

    private boolean PrepDownBlock(Player player)
    {
        wctrl.QueueWaitControl(player, 2);
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Down-Block creation outstanding...").toString());
        return true;
    }

    private boolean PrepRedstoneOut(Player player, String commands[])
    {
        wctrl.QueueWaitControl(player, 4, commands);
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Redstone  output creation outstanding...").toString());
        return true;
    }

    private boolean PrepDirectBlock(Player player, String commands[])
    {
        wctrl.QueueWaitControl(player, 5, commands);
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Direct-Block creation outstanding...").toString());
        return true;
    }

    private boolean PrepGlassDoor(Player player, String commands[])
    {
        wctrl.QueueWaitControl(player, 6, commands);
        if(ElevatorSubRoutines.ExtractDirection(commands) == 1)
        {
            ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder("You specified a wrong direction. Valid identifiers are: ")).append(ElevatorSubRoutines.GetDirectionList()).toString());
            return true;
        } else
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append("Place your glass blocks now! Type '/elev finish' afterwards to complete the placement.").toString());
            return true;
        }
    }

    private boolean FinishActions(Player player)
    {
        if(wctrl.RemoveGlassWaitControl(player))
            ElevatorSubRoutines.CongratPlayer(player, "Glass door placement finished!");
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "ignore");
        if(Elev == null)
            return true;
        ElevatorsMoveTask MoveTask = GetMoveTask(Elev);
        if(MoveTask.stopcode != null)
        {
            MoveTask.ignoredStopcodes.add(MoveTask.stopcode);
            ElevatorSubRoutines.InfoPlayer(player, "Warning ignored. Your problem, if something bad happens now ;D");
        }
        return true;
    }

    private boolean LockCabin(Player player)
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Toggle lock status failed!");
        if(Elev == null)
            return true;
        ElevatorsMoveTask MoveTask = GetMoveTask(Elev);
        int Y = ElevatorSubRoutines.FindElevatorY(MoveTask);
        if(Y < 0)
        {
            ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(store.config, "ElevatorNotFoundMessage"));
            return true;
        }
        Elev.locked = !Elev.locked;
        if(Elev.locked)
        {
            Elev.BuildBlocks = ElevatorSubRoutines.ScanBuildBlocks(GetMoveTask(Elev).world(), Elev.elX1, Elev.elX2, Y, Elev.elZ1, Elev.elZ2);
            ElevatorSubRoutines.CongratPlayer(player, "Elevator cabin is now LOCKED!");
        } else
        {
            ElevatorSubRoutines.CongratPlayer(player, "Elevator cabin is NOT locked again!");
        }
        store.WriteStore(Elev);
        return true;
    }

    public void CreateCallBlock(Player player, Location loc, String commands[])
    {
        int cbX = loc.getBlockX();
        int cbY = loc.getBlockY();
        int cbZ = loc.getBlockZ();
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(cbX, cbY, cbZ, player.getWorld(), player, "Call-Block creation failed!");
        if(Elev == null)
            return;
        if(ElevatorSubRoutines.CheckSpecialBlock(GetMoveTask(Elev), loc) > -1)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Call-Block creation failed! Already another linked block on this position.").toString());
            return;
        }
        String FloorName = ElevatorSubRoutines.ExtractFloorname(commands);
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = store.AddSpecialBlock(Elev, cbX, cbY, cbZ, 0, FloorName);
        boolean us = SetUsers(Elev, CallBlock, commands, player);
        boolean pw = SetPassword(Elev, CallBlock, commands, player);
        String message;
        if(FloorName == "")
            message = (new StringBuilder("Call-Block and floor")).append(ElevatorSubRoutines.ProtectionMessage(pw, us)).append("succesfully created!").toString();
        else
            message = (new StringBuilder("Call-Block and floor \"")).append(FloorName).append("\"").append(ElevatorSubRoutines.ProtectionMessage(pw, us)).append("succesfully created!").toString();
        ElevatorSubRoutines.CongratPlayer(player, message);
    }

    public void CreateUpBlock(Player player, Location loc)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), player.getWorld(), player, "Up-Block creation failed!");
        if(Elev == null)
            return;
        if(ElevatorSubRoutines.CheckSpecialBlock(GetMoveTask(Elev), loc) > -1)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Up-Block creation failed! Already another linked block on this position.").toString());
            return;
        } else
        {
            CreateDirectionBlock(Elev, loc, 1, -1);
            player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Up-Block succesfully created!").toString());
            return;
        }
    }

    public void CreateDownBlock(Player player, Location loc)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), player.getWorld(), player, "Down-Block creation failed!");
        if(Elev == null)
            return;
        if(ElevatorSubRoutines.CheckSpecialBlock(GetMoveTask(Elev), loc) > -1)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Down-Block creation failed! Already another linked block on this position.").toString());
            return;
        } else
        {
            CreateDirectionBlock(Elev, loc, 2, -1);
            player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Down-Block succesfully created!").toString());
            return;
        }
    }

    public void CreateDirectBlock(Player player, Location loc, String commands[])
    {
        int cbX = loc.getBlockX();
        int cbY = loc.getBlockY();
        int cbZ = loc.getBlockZ();
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(cbX, cbY, cbZ, player.getWorld(), player, "Direct-Block creation failed!");
        if(Elev == null)
            return;
        if(ElevatorSubRoutines.CheckSpecialBlock(GetMoveTask(Elev), loc) > -1)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Direct-Block creation failed! Already another linked block on this position.").toString());
            return;
        }
        String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock;
        if(Floorname.equalsIgnoreCase(""))
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 NextUp = ElevatorSubRoutines.GetNextFloorUp(Elev, cbY, 1, -1);
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 NextDown = ElevatorSubRoutines.GetNextFloorDown(Elev, cbY, 1, -1);
            if(NextUp == null && NextDown == null)
            {
                player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Direct-Block creation failed! No extendable CallBlock found.").toString());
                return;
            }
            if(NextDown == null)
                CallBlock = NextUp;
            else
            if(NextUp == null)
                CallBlock = NextDown;
            else
            if(Math.abs(NextDown.BlockY - cbY) < Math.abs(NextUp.BlockY - cbY))
                CallBlock = NextDown;
            else
                CallBlock = NextUp;
        } else
        {
            CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, "Direct-Block creation failed! The specified floor wasn't found.");
                return;
            }
        }
        CreateDirectionBlock(Elev, loc, 5, CallBlock.selfID);
        ElevatorSubRoutines.CongratPlayer(player, (new StringBuilder("Direct-Block to floor \"")).append(Floorname).append("\" succesfully created!").toString());
    }

    private void CreateDirectionBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, Location loc, int Type, int tID)
    {
        int relpos[] = new int[3];
        if(!ElevatorSubRoutines.EstablishRelativePosition(GetMoveTask(Elev), loc, relpos))
            store.AddSpecialBlock(Elev, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Type, "", tID, false);
        else
            store.AddSpecialBlock(Elev, relpos[0], relpos[1], relpos[2], Type, "", tID, true);
    }

    public void CreateRedstoneOut(Player player, Block block, String commands[])
    {
        if(block.getTypeId() != 69)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Redstone output creation failed! The block has to be a lever.").toString());
            return;
        }
        Location loc = block.getLocation();
        int cbX = loc.getBlockX();
        int cbY = loc.getBlockY();
        int cbZ = loc.getBlockZ();
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(cbX, cbY, cbZ, player.getWorld(), player, "Direct-Block creation failed!");
        if(Elev == null)
            return;
        if(ElevatorSubRoutines.CheckSpecialBlock(GetMoveTask(Elev), loc) > -1)
        {
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Redstone-Output creation failed! Already another linked block on this position.").toString());
            return;
        }
        String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock;
        if(Floorname.equalsIgnoreCase(""))
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 NextUp = ElevatorSubRoutines.GetNextFloorUp(Elev, cbY, 1, -1);
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 NextDown = ElevatorSubRoutines.GetNextFloorDown(Elev, cbY, 1, -1);
            if(NextUp == null && NextDown == null)
            {
                player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Redstone-Output creation failed! No extendable CallBlock found.").toString());
                return;
            }
            if(NextDown == null)
                CallBlock = NextUp;
            else
            if(NextUp == null)
                CallBlock = NextDown;
            else
            if(Math.abs(NextDown.BlockY - cbY) < Math.abs(NextUp.BlockY - cbY))
                CallBlock = NextDown;
            else
                CallBlock = NextUp;
        } else
        {
            CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, "Redstone-Output creation failed! The specified floor wasn't found.");
                return;
            }
        }
        store.AddSpecialBlock(Elev, cbX, cbY, cbZ, 4, CallBlock.selfID);
        GetMoveTask(Elev).setFloorActivations(true);
        player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Redstone-Output to floor \"").append(CallBlock.Parameter).append("\"  succesfully created!").toString());
    }

    public void MoveElevatorUp(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, Location loc, int Times)
    {
        int curY = ElevatorSubRoutines.FindElevatorY(GetMoveTask(Elev));
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = ElevatorSubRoutines.GetNextFloorUp(Elev, curY + 2, Times, 0);
        if(CallBlock == null)
        {
            BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "NoHighFloorMessage")).toString(), Elev, -10, loc);
            return;
        } else
        {
            MoveElevator(Elev, CallBlock, loc, "", "");
            return;
        }
    }

    public void MoveElevatorDown(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, Location loc, int Times)
    {
        int curY = ElevatorSubRoutines.FindElevatorY(GetMoveTask(Elev));
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = ElevatorSubRoutines.GetNextFloorDown(Elev, curY + 2, Times, 0);
        if(CallBlock == null)
        {
            BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "NoLowFloorMessage")).toString(), Elev, -10, loc);
            return;
        } else
        {
            MoveElevator(Elev, CallBlock, loc, "", "");
            return;
        }
    }

    public void MoveElevator(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, Location callloc, String elevpw, String floorpw)
    {
        float Distance = ConfigHelper.GetFloat(store.config, "AuthenticationRange");
        ArrayList players = new ArrayList();
        Player aplayer[];
        int j = (aplayer = getServer().getOnlinePlayers()).length;
        for(int i = 0; i < j; i++)
        {
            Player player = aplayer[i];
            if(player.getWorld().getName().equals(Elev.elWorld))
            {
                Location loc = player.getLocation();
                int resloc[] = ElevatorSubRoutines.ResolvePlayerLocation(loc);
                if(Math.abs((double)resloc[0] - callloc.getX()) + Math.abs((double)resloc[1] - callloc.getY()) + Math.abs((double)resloc[2] - callloc.getZ()) < (double)Distance)
                    players.add(player);
            }
        }

        boolean validplayer = false;
        boolean validop = false;
        for(Iterator iterator = players.iterator(); iterator.hasNext();)
        {
            Player player = (Player)iterator.next();
            if(ElevatorSubRoutines.IsAllowed(Elev, CallBlock, player))
                validplayer = true;
            if(comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.OPERATOR))
                validop = true;
        }

        if(!validplayer && !ConfigHelper.GetBoolean(store.config, "ignore-users") && !validop && (Elev.users != null || CallBlock.users != null))
        {
            String message = "";
            if(!Elev.owner.equals(""))
                message = ConfigHelper.GetString(store.config, "UserNotAllowedMessageNoOwner");
            else
                message = ConfigHelper.GetString(store.config, "UserNotAllowedMessage").replaceAll("%O", Elev.owner);
            Player player;
            for(Iterator iterator2 = players.iterator(); iterator2.hasNext(); ElevatorSubRoutines.WarnPlayer(player, message))
                player = (Player)iterator2.next();

            return;
        }
        if((!Elev.password.equals(elevpw) || !CallBlock.password.equals(floorpw)) && !ConfigHelper.GetBoolean(store.config, "ignore-passwords") && !validop)
        {
            Player player;
            for(Iterator iterator1 = players.iterator(); iterator1.hasNext(); new ElevatorsPlayerListener.PasswordControl(playerListener, Elev, CallBlock, callloc, elevpw, player))
                player = (Player)iterator1.next();

            return;
        } else
        {
            MoveElevatorCon(Elev, CallBlock, callloc);
            return;
        }
    }

    public void MoveElevatorCon(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, Location callloc)
    {
        log(Level.INFO, (new StringBuilder("starting movement to ")).append(CallBlock.toString()).append(", calling source location ").append(ElevatorSubRoutines.LocToString(callloc)).toString());
        ElevatorsMoveTask MoveTask = GetMoveTask(Elev);
        for(Iterator iterator = MoveTask.Floors.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 ExBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(ExBlock.selfID == CallBlock.selfID)
                return;
        }

        int curY = ElevatorSubRoutines.FindElevatorY(MoveTask);
        if(curY < 0)
        {
            BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "ElevatorNotFoundMessage")).toString(), Elev, -10, callloc);
            return;
        }
        log(Level.INFO, (new StringBuilder("Elevator found on ")).append(curY).toString());
        if(MoveTask.curFloor != null && !ElevatorSubRoutines.CheckGlassDoorClear(MoveTask, MoveTask.curFloor, callloc))
        {
            BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "DoorBlockedMessage")).toString(), Elev, MoveTask.curY, callloc);
            return;
        }
        if(MoveTask.ThreadRunning < 0)
        {
            if(!CheckElevatorIntegrity(MoveTask, callloc))
                return;
            log(Level.INFO, "Integrity check succesfull.");
        }
        log(Level.INFO, "Starting movement...");
        MoveTask.callloc.add(callloc);
        MoveTask.Floors.add(CallBlock);
        MoveTask.startrun();
    }

    public int DoMoveElevator(ElevatorsMoveTask MoveTask)
    {
        int prevY = MoveTask.curY;
        if(((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)MoveTask.Floors.get(0)).BlockY - 2 < MoveTask.curY)
        {
            if(!BlockCheck(MoveTask, false))
            {
                BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "StuckBelowMessage")).toString(), MoveTask.selfElev, MoveTask.curY, (Location)MoveTask.callloc.get(0));
                return 2;
            }
            BlockClear(MoveTask);
            MoveTask.curY--;
            BlockWrite(MoveTask);
        } else
        if(((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)MoveTask.Floors.get(0)).BlockY - 2 > MoveTask.curY)
        {
            if(!BlockCheck(MoveTask, true))
            {
                BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "StuckAboveMessage")).toString(), MoveTask.selfElev, MoveTask.curY, (Location)MoveTask.callloc.get(0));
                return 2;
            }
            BlockClear(MoveTask);
            MoveTask.curY++;
            BlockWrite(MoveTask);
        } else
        {
            String message;
            if(MoveTask.curFloor.Parameter.trim().equals(""))
                message = ConfigHelper.GetString(store.config, "ReachNoNameMessage");
            else
                message = ConfigHelper.GetString(store.config, "ReachMessage").replaceAll("%F", MoveTask.curFloor.Parameter);
            if(MoveTask.Floors.size() == 1)
            {
                MessagePlayersInElevator((new StringBuilder()).append(ChatColor.GREEN).append(message).toString(), MoveTask, MoveTask.curY);
                return 0;
            } else
            {
                MessagePlayersInElevator((new StringBuilder()).append(ChatColor.GREEN).append(message).append(" ").append(ConfigHelper.GetString(store.config, "ReachWaitMessage").replaceAll("%T", ConfigHelper.GetString(store.config, "WaitTime"))).toString(), MoveTask, MoveTask.curY);
                return 2;
            }
        }
        EntityWrite(MoveTask, MoveTask.curY - prevY);
        return 1;
    }

    private void EntityWrite(ElevatorsMoveTask MoveTask, int LastChange)
    {
        Entity entity;
        Location loc;
        for(Iterator iterator = MoveTask.entities.iterator(); iterator.hasNext(); entity.teleport(loc))
        {
            entity = (Entity)iterator.next();
            loc = entity.getLocation();
            loc.setY(loc.getY() + (double)LastChange);
            loc.setX(Math.max(Math.min((double)(MoveTask.selfElev.elX1 + 1) - 0.29999999999999999D, loc.getX()), (double)MoveTask.selfElev.elX2 + 0.29999999999999999D));
            loc.setZ(Math.max(Math.min((double)(MoveTask.selfElev.elZ1 + 1) - 0.29999999999999999D, loc.getZ()), (double)MoveTask.selfElev.elZ2 + 0.29999999999999999D));
        }

    }

    private void MessagePlayersInElevator(String message, ElevatorsMoveTask MoveTask, int Y)
    {
        double curY = (double)Y - 0.5D;
        Player aplayer[];
        int j = (aplayer = getServer().getOnlinePlayers()).length;
        for(int i = 0; i < j; i++)
        {
            Player player = aplayer[i];
            if(player.getWorld().getName().equals(MoveTask.selfElev.elWorld))
            {
                Location loc = player.getLocation();
                if(ElevatorSubRoutines.IsInElevator(MoveTask, curY, ElevatorSubRoutines.ResolvePlayerLocation(loc)))
                    player.sendMessage(message);
            }
        }

    }

    private Player GetNextPlayer(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, Location callloc)
    {
        Player Result = null;
        double Distance = 50D;
        Player aplayer[];
        int j = (aplayer = getServer().getOnlinePlayers()).length;
        for(int i = 0; i < j; i++)
        {
            Player player = aplayer[i];
            if(player.getWorld().getName().equals(Elev.elWorld))
            {
                Location loc = player.getLocation();
                int resloc[] = ElevatorSubRoutines.ResolvePlayerLocation(loc);
                if(Math.abs((double)resloc[0] - callloc.getX()) + Math.abs((double)resloc[1] - callloc.getY()) + Math.abs((double)resloc[2] - callloc.getZ()) < Distance)
                {
                    Distance = Math.abs((double)resloc[0] - callloc.getX()) + Math.abs((double)resloc[1] - callloc.getY()) + Math.abs((double)resloc[2] - callloc.getZ());
                    Result = player;
                }
            }
        }

        return Result;
    }

    private void BroadcastMessage(String message, ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int Y, Location callloc)
    {
        double curY = (double)Y - 0.5D;
        Player aplayer[];
        int j = (aplayer = getServer().getOnlinePlayers()).length;
        for(int i = 0; i < j; i++)
        {
            Player player = aplayer[i];
            if(player.getWorld().getName().equals(Elev.elWorld))
            {
                Location loc = player.getLocation();
                int resloc[] = ElevatorSubRoutines.ResolvePlayerLocation(loc);
                if(ElevatorSubRoutines.IsInElevator(GetMoveTask(Elev), curY, resloc) || (double)resloc[0] > callloc.getX() - 5D && (double)resloc[0] < callloc.getX() + 5D && (double)resloc[2] > callloc.getZ() - 5D && (double)resloc[2] < callloc.getZ() + 5D && (double)resloc[1] > callloc.getY() - 3D && (double)resloc[1] < callloc.getY() + 2D)
                    player.sendMessage(message);
            }
        }

    }

    private boolean BlockCheck(ElevatorsMoveTask MoveTask, boolean Above)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        World world = MoveTask.world();
        for(int iX = 0; iX <= Elev.elX1 - Elev.elX2; iX++)
        {
            for(int iZ = 0; iZ <= Elev.elZ1 - Elev.elZ2; iZ++)
                if(Above)
                {
                    int maxY = 1;
                    for(Iterator iterator = MoveTask.BuildBlocks.iterator(); iterator.hasNext();)
                    {
                        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                        if(BuildBlock.BlockX == iX && BuildBlock.BlockZ == iZ)
                            maxY = Math.max(maxY, BuildBlock.BlockY + 1);
                    }

                    for(int iA = 0; iA <= (Elev.locked ? 0 : 1); iA++)
                    {
                        int typeID = world.getBlockTypeIdAt(Elev.elX2 + iX, MoveTask.curY + maxY + iA, Elev.elZ2 + iZ);
                        if(typeID != 0 && (typeID < 8 || typeID > 11))
                            return false;
                    }

                } else
                {
                    int typeID = world.getBlockTypeIdAt(Elev.elX2 + iX, MoveTask.curY - 1, Elev.elZ2 + iZ);
                    if(typeID != 0 && (typeID < 8 || typeID > 11))
                        return false;
                }

        }

        return true;
    }

    private void BlockClear(ElevatorsMoveTask MoveTask)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int Y = MoveTask.curY;
        World world = MoveTask.world();
        ElevatorSubRoutines.AdjacentRailUpdate(MoveTask, true);
        for(Iterator iterator = MoveTask.BuildBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(BuildBlock.BlockType == 54)
                ((Chest)ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).getState()).getInventory().clear();
            if(BuildBlock.BlockType == 23)
                ((Dispenser)ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).getState()).getInventory().clear();
        }

        for(int i = MoveTask.BuildBlocks.size() - 1; i >= 0; i--)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)MoveTask.BuildBlocks.get(i);
            if(ElevatorSubRoutines.BlockPriority(0, BuildBlock.BlockType))
                ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).setTypeId(0);
        }

        for(Iterator iterator1 = MoveTask.BuildBlocks.iterator(); iterator1.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
            if(!ElevatorSubRoutines.BlockPriority(0, BuildBlock.BlockType))
                ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).setTypeId(0);
        }

        for(int iX = Elev.elX2; iX <= Elev.elX1; iX++)
        {
            for(int iZ = Elev.elZ2; iZ <= Elev.elZ1; iZ++)
                world.getBlockAt(iX, Y, iZ).setTypeId(0);

        }

    }

    private void BlockWrite(ElevatorsMoveTask MoveTask)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int Y = MoveTask.curY;
        World world = MoveTask.world();
        for(int iX = Elev.elX2; iX <= Elev.elX1; iX++)
        {
            for(int iZ = Elev.elZ2; iZ <= Elev.elZ1; iZ++)
            {
                world.getBlockAt(iX, Y, iZ).setTypeId(Elev.elType);
                world.getBlockAt(iX, Y, iZ).setData(Elev.elData);
            }

        }

        for(Iterator iterator = MoveTask.BuildBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(ElevatorSubRoutines.BlockPriority(1, BuildBlock.BlockType))
            {
                ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).setTypeId(BuildBlock.BlockType);
                ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).setData(BuildBlock.BlockData);
            }
        }

        RemoveList removes = new RemoveList(MoveTask.BuildBlocks);
        for(Iterator iterator1 = MoveTask.BuildBlocks.iterator(); iterator1.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
            if(!ElevatorSubRoutines.BlockPriority(1, BuildBlock.BlockType))
            {
                Block block = ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock);
                if(!ElevatorSubRoutines.BlockPlaceable(BuildBlock.BlockType, BuildBlock.BlockData, block))
                {
                    removes.add(BuildBlock);
                    net.SendClientRemoveBlock(MoveTask, BuildBlock);
                    Item item = ElevatorSubRoutines.DropBlock(block.getLocation(), BuildBlock.BlockType, BuildBlock.BlockData);
                    if(item != null)
                    {
                        MoveTask.entities.add(item);
                        net.SendClientAddEntity(MoveTask, item);
                    }
                } else
                {
                    block.setTypeId(BuildBlock.BlockType);
                    if(BuildBlock.BlockType != 55)
                        block.setData(BuildBlock.BlockData);
                    if(BuildBlock.BlockType == 68 || BuildBlock.BlockType == 63)
                    {
                        BlockState state = block.getState();
                        if(state instanceof Sign)
                        {
                            Sign sign = (Sign)state;
                            String Lines[] = BuildBlock.Parameter.split(";d9f~");
                            int LineCnt = 0;
                            String as[];
                            int j = (as = Lines).length;
                            for(int i = 0; i < j; i++)
                            {
                                String Line = as[i];
                                sign.setLine(LineCnt, Line);
                                LineCnt++;
                            }

                        }
                    }
                }
            }
        }

        removes.Execute();
        ElevatorsMoveTask.BuildBlockInventory invent;
        for(Iterator iterator2 = MoveTask.Inventories.iterator(); iterator2.hasNext(); invent.CopyBack(MoveTask))
            invent = (ElevatorsMoveTask.BuildBlockInventory)iterator2.next();

        ElevatorSubRoutines.AdjacentRailUpdate(MoveTask, false);
    }

    private boolean CheckElevatorIntegrity(ElevatorsMoveTask MoveTask, Location callloc)
    {
        World world = MoveTask.world();
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int Y = MoveTask.curY;
        for(int X = Elev.elX2; X < Elev.elX1; X++)
        {
            for(int Z = Elev.elZ2; Z < Elev.elZ1; Z++)
            {
                int ID = world.getBlockTypeIdAt(X, Y, Z);
                if(!ElevatorSubRoutines.CheckGroundType(Elev, ID))
                {
                    log(Level.SEVERE, (new StringBuilder("Elevator damaged by X:")).append(X).append(" Y:").append(Y).append(" Z:").append(Z).append(". Required typeID: ").append(Elev.elType).append(". Scanned typeID: ").append(world.getBlockTypeIdAt(X, Y, Z)).toString());
                    BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "ElevatorDamagedMessage")).toString(), Elev, Y, callloc);
                    return false;
                }
                if(ConfigHelper.GetBoolean(store.config, "ForbidExisting") && store.forbidden.Forbidden(ID))
                {
                    BroadcastMessage((new StringBuilder()).append(ChatColor.RED).append(ConfigHelper.GetString(store.config, "ForbiddenBlockMessage").replaceAll("%I", (new StringBuilder()).append(ID).toString())).toString(), Elev, Y, callloc);
                    return false;
                }
                Elev.elData = world.getBlockAt(X, Y, Z).getData();
            }

        }

        if(Elev.locked)
        {
            RemoveList BuildRemoves = new RemoveList(Elev.BuildBlocks);
            Iterator iterator = Elev.BuildBlocks.iterator();
            while(iterator.hasNext()) 
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                Block block = ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock);
                if(block.getTypeId() != BuildBlock.BlockType)
                {
                    if(block.getTypeId() == 0)
                    {
                        log(Level.WARNING, (new StringBuilder("(locked) block remove planned on ")).append(BuildBlock.toString()).toString());
                        BuildRemoves.add(BuildBlock);
                        continue;
                    }
                    BuildBlock.BlockType = block.getTypeId();
                }
                BuildBlock.BlockData = ElevatorSubRoutines.StorageData(block);
            }
            BuildRemoves.Execute();
            MoveTask.BuildBlocks = Elev.BuildBlocks;
        } else
        {
            MoveTask.BuildBlocks = ElevatorSubRoutines.ScanBuildBlocks(world, Elev.elX1, Elev.elX2, Y, Elev.elZ1, Elev.elZ2);
            Elev.BuildBlocks = MoveTask.BuildBlocks;
        }
        ArrayList SpecialRemoves = new ArrayList();
        for(Iterator iterator1 = Elev.SpecialBlocks.iterator(); iterator1.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
            Block block = ElevatorSubRoutines.GetBlock(MoveTask, SpecialBlock);
            if(block.getTypeId() == 0)
            {
                log(Level.WARNING, (new StringBuilder("specialblock remove planned on ")).append(SpecialBlock.toString()).toString());
                SpecialRemoves.add(SpecialBlock);
            }
        }

        Player player = GetNextPlayer(Elev, callloc);
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 RemBlock;
        for(Iterator iterator2 = SpecialRemoves.iterator(); iterator2.hasNext(); store.RemoveLinkedBlock(Elev, RemBlock, player))
            RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator2.next();

        SpecialRemoves.clear();
        SpecialRemoves = null;
        ArrayList GlassRemoves = new ArrayList();
        Iterator iterator3 = Elev.GlassDoors.iterator();
label0:
        while(iterator3.hasNext()) 
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator3.next();
            if(GlassBlock.targetID == -1 || MoveTask.curFloor != null && GlassBlock.targetID == MoveTask.curFloor.selfID)
                continue;
            Block block = ElevatorSubRoutines.GetBlock(MoveTask, GlassBlock);
            for(Iterator iterator7 = MoveTask.GlassDoors.iterator(); iterator7.hasNext();)
            {
                ElevatorsMoveTask.GlassDoorSide glassdoor = (ElevatorsMoveTask.GlassDoorSide)iterator7.next();
                for(Iterator iterator8 = glassdoor.glassblocks.iterator(); iterator8.hasNext();)
                {
                    ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block2 = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator8.next();
                    if(GlassBlock.equals(block2))
                        continue label0;
                }

            }

            if(block.getTypeId() != 20)
                GlassRemoves.add(GlassBlock);
        }
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 RemBlock;
        for(Iterator iterator4 = GlassRemoves.iterator(); iterator4.hasNext(); store.RemoveGlassDoor(Elev, RemBlock))
            RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator4.next();

        GlassRemoves.clear();
        GlassRemoves = null;
        for(Iterator iterator5 = MoveTask.BuildBlocks.iterator(); iterator5.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator5.next();
            if(BuildBlock.BlockType == 68 || BuildBlock.BlockType == 63)
            {
                Block sign = ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock);
                if(sign.getState() instanceof Sign)
                {
                    String SignText = "";
                    String as[];
                    int j = (as = ((Sign)sign.getState()).getLines()).length;
                    for(int i = 0; i < j; i++)
                    {
                        String Line = as[i];
                        if(SignText != "")
                            SignText = (new StringBuilder(String.valueOf(SignText))).append(";d9f~").toString();
                        SignText = (new StringBuilder(String.valueOf(SignText))).append(Line).toString();
                    }

                    BuildBlock.Parameter = SignText;
                }
            }
        }

        MoveTask.Inventories.clear();
        for(Iterator iterator6 = MoveTask.BuildBlocks.iterator(); iterator6.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator6.next();
            try
            {
                if(BuildBlock.BlockType == 61 || BuildBlock.BlockType == 62)
                {
                    Furnace furnace = (Furnace)ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).getState();
                    MoveTask.Inventories.add(new ElevatorsMoveTask.FurnaceStorage(BuildBlock, furnace));
                }
                if(BuildBlock.BlockType == 54)
                {
                    Inventory invent = ((Chest)ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).getState()).getInventory();
                    MoveTask.Inventories.add(new ElevatorsMoveTask.BuildBlockInventory(BuildBlock, invent));
                }
                if(BuildBlock.BlockType == 23)
                {
                    Inventory invent = ((Dispenser)ElevatorSubRoutines.GetBlock(MoveTask, BuildBlock).getState()).getInventory();
                    MoveTask.Inventories.add(new ElevatorsMoveTask.BuildBlockInventory(BuildBlock, invent));
                }
            }
            catch(Exception exception) { }
        }

        store.WriteStore(Elev);
        return true;
    }

    private boolean RemoveElevator(Player player)
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Elevator remove failed!");
        if(Elev == null)
        {
            return true;
        } else
        {
            store.RemoveElevator(Elev);
            player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Elevator succesfully removed! All nearby blocks unlinked.").toString());
            return true;
        }
    }

    public ElevatorsMoveTask GetMoveTask(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        for(Iterator iterator = MoveTasks.iterator(); iterator.hasNext();)
        {
            ElevatorsMoveTask MoveTask = (ElevatorsMoveTask)iterator.next();
            if(MoveTask.selfElev == Elev)
                return MoveTask;
        }

        return null;
    }

    public void ToggleRedstoneOutputs(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, boolean State)
    {
        for(Iterator iterator = MoveTask.selfElev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(Block.targetID == CallBlock.selfID && Block.BlockType == 4)
            {
                Block lever = ElevatorSubRoutines.GetBlock(MoveTask, Block);
                byte data = lever.getData();
                if(State)
                    data |= 8;
                else
                    data &= 7;
                lever.setData(data);
            }
        }

    }

    private void onBlockDestroy2(Player player, Block block)
    {
        if(block.getTypeId() != 0)
            return;
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorAcc(block.getX(), block.getY(), block.getZ(), block.getWorld());
        if(Elev != null)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 DestBlock = ElevatorSubRoutines.GetSpecialBlock(GetMoveTask(Elev), block.getLocation());
            if(DestBlock != null)
                store.RemoveLinkedBlock(Elev, DestBlock, player);
        }
    }

    public void onBlockDestroy(Player player, Block block)
    {
        int X = block.getX();
        int Y = block.getY();
        int Z = block.getZ();
        World world = block.getWorld();
        onBlockDestroy2(player, world.getBlockAt(X, Y, Z));
        onBlockDestroy2(player, world.getBlockAt(X + 1, Y, Z));
        onBlockDestroy2(player, world.getBlockAt(X - 1, Y, Z));
        onBlockDestroy2(player, world.getBlockAt(X, Y + 1, Z));
        onBlockDestroy2(player, world.getBlockAt(X, Y, Z + 1));
        onBlockDestroy2(player, world.getBlockAt(X, Y, Z - 1));
    }

    public void CreateGlassDoor(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, Block glass, String commands[], Player player)
    {
        int relOut[] = new int[3];
        byte dir = ElevatorSubRoutines.ExtractDirection(commands);
        if(ElevatorSubRoutines.EstablishRelativePosition(GetMoveTask(Elev), glass.getLocation(), relOut))
        {
            store.AddGlassDoor(Elev, relOut[0], relOut[1], relOut[2], -1, true, dir);
        } else
        {
            String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock;
            if(Floorname.equalsIgnoreCase(""))
                CallBlock = ElevatorSubRoutines.GetNextFloorDown(Elev, glass.getY() + 2, 1, -1);
            else
                CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder("Floor \"")).append(Floorname).append("\" was not found.").toString());
                return;
            }
            store.AddGlassDoor(Elev, glass.getX(), glass.getY(), glass.getZ(), CallBlock.selfID, false, dir);
        }
    }

    public void ToggleGlassDoor(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, boolean State)
    {
        int newID = 20;
        if(State)
            newID = 0;
        ArrayList glassdoors = new ArrayList();
        for(Iterator iterator = MoveTask.selfElev.GlassDoors.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(GlassBlock.targetID == CallBlock.selfID || GlassBlock.targetID == -1)
            {
                boolean added = false;
                Iterator iterator2 = glassdoors.iterator();
                while(iterator2.hasNext()) 
                {
                    ElevatorsMoveTask.GlassDoorSide existing = (ElevatorsMoveTask.GlassDoorSide)iterator2.next();
                    if(existing.refrel != GlassBlock.Relative || existing.direction2 != GlassBlock.BlockData)
                        continue;
                    if(existing.refX == GlassBlock.BlockX && existing.refZ == GlassBlock.BlockZ)
                    {
                        added = true;
                        existing.glassblocks.add(GlassBlock);
                        break;
                    }
                    if(existing.refX == GlassBlock.BlockX && (existing.direction == 2 || existing.direction == 3))
                    {
                        added = true;
                        existing.direction = 2;
                        existing.glassblocks.add(GlassBlock);
                        break;
                    }
                    if(existing.refZ != GlassBlock.BlockZ || existing.direction != 0 && existing.direction != 3)
                        continue;
                    added = true;
                    existing.direction = 0;
                    existing.glassblocks.add(GlassBlock);
                    break;
                }
                if(!added)
                {
                    ElevatorsMoveTask.GlassDoorSide tmp = new ElevatorsMoveTask.GlassDoorSide(MoveTask, newID, MoveTask);
                    tmp.glassblocks.add(GlassBlock);
                    tmp.refX = GlassBlock.BlockX;
                    tmp.refZ = GlassBlock.BlockZ;
                    tmp.refrel = GlassBlock.Relative;
                    tmp.direction2 = GlassBlock.BlockData;
                    glassdoors.add(tmp);
                }
            }
        }

        ElevatorsMoveTask.GlassDoorSide existing;
        for(Iterator iterator1 = glassdoors.iterator(); iterator1.hasNext(); existing.startglassrun())
            existing = (ElevatorsMoveTask.GlassDoorSide)iterator1.next();

    }

    private boolean SetUsers(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, String commands[], Player player)
    {
        if(!comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.ADVANCED))
        {
            ElevatorSubRoutines.WarnPlayer(player, "You don't have the permission level (advanced) to use this optional parameter.");
            return false;
        }
        ArrayList users = ElevatorSubRoutines.ExtractUsers(commands);
        if(CallBlock == null)
            Elev.users = users;
        else
            CallBlock.users = users;
        return users != null;
    }

    private boolean SetPassword(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, String commands[], Player player)
    {
        if(!comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.ADVANCED))
        {
            ElevatorSubRoutines.WarnPlayer(player, "You don't have the permission level (advanced) to use this optional parameter.");
            return false;
        }
        String pw = ElevatorSubRoutines.ExtractPassword(commands);
        if(CallBlock == null)
            Elev.password = pw;
        else
            CallBlock.password = pw;
        return !pw.equals("");
    }

    private boolean SetUsers(Player player, String commands[])
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Changing user whitelist failed!");
        if(Elev == null)
            return true;
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = null;
        String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
        if(!Floorname.equals(""))
        {
            CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder("Changing user whitelist failed! Specified floor \"")).append(Floorname).append("\" wasn't found.").toString());
                return true;
            }
        }
        boolean changed = SetUsers(Elev, CallBlock, commands, player);
        store.WriteStore(Elev);
        String message;
        if(Floorname.equals(""))
            message = "Elevator's user whitelist changed!";
        else
            message = (new StringBuilder("Whitelist of floor \"")).append(Floorname).append("\" changed!").toString();
        if(changed)
            message = (new StringBuilder(String.valueOf(message))).append(" New users: ").append(ElevatorSubRoutines.UserList(commands)).toString();
        else
            message = (new StringBuilder(String.valueOf(message))).append("Protection removed, all users have acces again.").toString();
        ElevatorSubRoutines.CongratPlayer(player, message);
        return true;
    }

    private boolean SetPassword(Player player, String commands[])
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Changing password failed!");
        if(Elev == null)
            return true;
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = null;
        String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
        if(!Floorname.equals(""))
        {
            CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder("Changing password failed! Specified floor \"")).append(Floorname).append("\" wasn't found.").toString());
                return true;
            }
        }
        boolean pw = SetPassword(Elev, CallBlock, commands, player);
        store.WriteStore(Elev);
        String message;
        if(Floorname.equals(""))
            message = "Elevator's password ";
        else
            message = (new StringBuilder("Password of floor \"")).append(Floorname).append("\" ").toString();
        if(pw)
            message = (new StringBuilder(String.valueOf(message))).append("was set to \"").append(ElevatorSubRoutines.ExtractPassword(commands)).append("\"!").toString();
        else
            message = (new StringBuilder(String.valueOf(message))).append("had been reset!").toString();
        ElevatorSubRoutines.CongratPlayer(player, message);
        return true;
    }

    private boolean GiveOwnerStatus(Player player, String commands[])
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Changing owner failed!");
        if(Elev == null)
            return true;
        String message;
        if(commands.length < 2)
        {
            Elev.owner = "";
            message = "Owner removed! All users can modify the elevator.";
        } else
        {
            Player owner = server.getPlayer(commands[1]);
            if(owner == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, "Changing owner failed! Specified player wasn't found on the server.");
                return true;
            }
            Elev.owner = owner.getName();
            message = (new StringBuilder("New owner is \"")).append(owner.getDisplayName()).append("\" now!").toString();
        }
        store.WriteStore(Elev);
        ElevatorSubRoutines.CongratPlayer(player, message);
        return true;
    }

    private boolean RemoveGlassDoors(Player player, String commands[])
    {
        Location location = player.getLocation();
        int newloc[] = ElevatorSubRoutines.ResolvePlayerLocation(location);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = store.FindNearbyElevatorMod(newloc[0], newloc[1], newloc[2], player.getWorld(), player, "Removing glass doors failed!");
        if(Elev == null)
            return true;
        String Floorname = ElevatorSubRoutines.ExtractFloorname(commands);
        int tID;
        if(Floorname.equals(""))
        {
            tID = -1;
        } else
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = ElevatorSubRoutines.GetFloor(Elev, Floorname);
            if(CallBlock == null)
            {
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder("Removing glass doors failed! Specified floor \"")).append(Floorname).append("\" wasn't found.").toString());
                return true;
            }
            tID = CallBlock.selfID;
        }
        RemoveList GlassRemoves = new RemoveList(Elev.GlassDoors);
        for(Iterator iterator = Elev.GlassDoors.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(GlassBlock.targetID == tID)
                GlassRemoves.add(GlassBlock);
        }

        GlassRemoves.Execute();
        GlassRemoves = null;
        store.WriteStore(Elev);
        if(tID > -1)
            ElevatorSubRoutines.CongratPlayer(player, (new StringBuilder("Glass doors from floor \"")).append(Floorname).append("\" removed!").toString());
        else
            ElevatorSubRoutines.CongratPlayer(player, "Glass doors from elevator cabin removed!");
        return true;
    }

    private static boolean debugging = false;
    public ElevatorsBlockListener blockListener;
    private ElevatorsPlayerListener playerListener;
    private ElevatorsWorldListener worldListener;
    public ElevatorsPluginCommunicator comm;
    public ElevatorsWaitControl wctrl;
    public ElevatorsStore store;
    public NetworkManagerEx net;
    private Logger log;
    private FileLogger flog;
    public Server server;
    public ArrayList MoveTasks;
    public ArrayList Valids;

}