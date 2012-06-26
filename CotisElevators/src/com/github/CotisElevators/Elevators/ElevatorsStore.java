
package com.github.CotisElevators.Elevators;

import com.gmail.creathir.util.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators, ElevatorsStoreFormat121, ElevatorsMoveTask, ElevatorSubRoutines, 
//            ElevatorsPluginCommunicator

public class ElevatorsStore
{
    public static class CommandList
    {

        public String[] Substitute(String commands[])
        {
            String cmd = (new StringBuilder(" ")).append(ElevatorSubRoutines.GetList(commands, " ")).append(" ").toString();
            for(int i = 0; i < cc.size(); i++)
                cmd = cmd.replaceAll((new StringBuilder(" ")).append((String)cc.get(i)).append(" ").toString(), (new StringBuilder(" ")).append((String)oc.get(i)).append(" ").toString());

            return cmd.trim().split(" ");
        }

        private ArrayList oc;
        private ArrayList cc;

        public CommandList(Configuration config)
        {
            oc = new ArrayList();
            cc = new ArrayList();
            String list = ConfigHelper.GetString(config, "CustomCommands");
            String list2[] = list.split(",");
            String as[];
            int j = (as = list2).length;
            for(int i = 0; i < j; i++)
            {
                String cset = as[i];
                String list3[] = cset.trim().split(":");
                if(list3.length == 2)
                {
                    oc.add(list3[0].trim());
                    cc.add(list3[1].trim());
                }
            }

        }
    }

    public static class ForbiddenBlocks
    {

        public boolean Forbidden(int typeID)
        {
            return flist.indexOf(Integer.valueOf(typeID)) >= 0;
        }

        ArrayList flist;

        public ForbiddenBlocks(Configuration config)
        {
            String forbidden[] = ConfigHelper.GetString(config, "ForbiddenBlocks").split(",");
            flist = new ArrayList();
            String as[];
            int j = (as = forbidden).length;
            for(int i = 0; i < j; i++)
            {
                String item = as[i];
                try
                {
                    int value = Integer.valueOf(item).intValue();
                    flist.add(Integer.valueOf(value));
                }
                catch(Exception exception) { }
            }

        }
    }


    public ElevatorsStore(Elevators instance)
    {
        needsUpdate = false;
        dropped = 0;
        converted = 0;
        plugin = instance;
        plugin.log(Level.INFO, "reading configuration");
        ReadConfig();
        plugin.log(Level.INFO, "reading storage");
        ReadStore();
    }

    private void ReadConfig()
    {
        config = ConfigHelper.LoadConfig(config13);
        ConfigHelper.UpdateSetting(config, "WaitTime", "5", 2, 0, new String[] {
            "Time in seconds, how long the elevator stays at a single floor.", "Use values like '5' or '3' or '4.5' (without inverted commas)"
        });
        ConfigHelper.UpdateSetting(config, "ButtonPressTime", "900", 1, 1, new String[] {
            "Time in milliseconds, how long the plugin waits for pressing a up- or down-button several times.", "The counter will restart after every button press.", "Use values like 900 or 600 or 1200."
        });
        ConfigHelper.UpdateSetting(config, "ReachMessage", "Elevator reached %F", 0, 13, new String[] {
            "Message when the elevator reached a floor.", "Use %F as floorname variable."
        });
        ConfigHelper.UpdateSetting(config, "ReachNoNameMessage", "Elevator reached new floor.", 0, 14, new String[] {
            "Message when the elevator reached a floor without a specific name."
        });
        ConfigHelper.UpdateSetting(config, "ReachWaitMessage", "(holding for %T seconds)", 0, 15, new String[] {
            "Message appended to the ReachMessage, when the elevator waits before moving to the next floor.", "Use %T as time variable. Value is in seconds."
        });
        ConfigHelper.UpdateSetting(config, "DoorBlockedMessage", "Something blocks the door!", 0, 16, new String[] {
            "Message when the glass doors cannot close because an entity is between."
        });
        ConfigHelper.UpdateSetting(config, "NoHighFloorMessage", "There is not such a high floor.", 0, 17, new String[] {
            "Message when the the elevator is called to a non-existing upper floor."
        });
        ConfigHelper.UpdateSetting(config, "NoLowFloorMessage", "There is not such a low floor.", 0, 18, new String[] {
            "Message when the the elevator is called to a non-existing lower floor."
        });
        ConfigHelper.UpdateSetting(config, "ElevatorNotFoundMessage", "Can't find the Elevator! Maybe it is destroyed.", 0, 19, new String[] {
            "Message when the the elevator ground blocks are undetectable."
        });
        ConfigHelper.UpdateSetting(config, "ElevatorDamagedMessage", "The elevator is broken! You have to repair it before usage.", 0, 20, new String[] {
            "Message when some of the elevator ground blocks were found, but the basic shape is not complete."
        });
        ConfigHelper.UpdateSetting(config, "StuckAboveMessage", "Oh no, the elevator is stuck above! You have to clear the shaft and restart manually.", 0, 21, new String[] {
            "Message when the elevator got stuck above."
        });
        ConfigHelper.UpdateSetting(config, "StuckBelowMessage", "Oh no, the elevator is stuck below! You have to clear the shaft and restart manually.", 0, 22, new String[] {
            "Message when the elevator got stuck below."
        });
        ConfigHelper.UpdateSetting(config, "UserNotAllowedMessage", "You are not allowed to use the elevator. Contact %O.", 0, 23, new String[] {
            "Message when a user tries to use the elevator who is not allowed to do.", "Use %O as variable for the name of the elevator's owner."
        });
        ConfigHelper.UpdateSetting(config, "UserNotAllowedMessageNoOwner", "You are not allowed to use the elevator.", 0, 24, new String[] {
            "Message when a user tries to use the elevator who is not allowed to do and the elevator has no owner."
        });
        ConfigHelper.UpdateSetting(config, "ElevatorPasswordRequiredMessage", "Elevator's password required. Just write the password in the chat.", 0, 25, new String[] {
            "Message when a password is required to use the elevator."
        });
        ConfigHelper.UpdateSetting(config, "FloorPasswordRequiredMessage", "Password for floor %F required. Just write the password in the chat.", 0, 26, new String[] {
            "Message when a password is required to get to a floor.", "Use %F as floorname variable."
        });
        ConfigHelper.UpdateSetting(config, "WrongPasswordMessage", "Wrong password. Action will be aborted.", 0, 27, new String[] {
            "Message when an entered password is wrong."
        });
        ConfigHelper.UpdateSetting(config, "owner-restriction", "true", 3, 2, new String[] {
            "If this option is set to true, only the elevator's owner can modify it."
        });
        ConfigHelper.UpdateSetting(config, "ignore-passwords", "false", 3, 3, new String[] {
            "If this option is set to true, password protection is disabled."
        });
        ConfigHelper.UpdateSetting(config, "ignore-users", "false", 3, 4, new String[] {
            "If this option is set to true, user whitelist restriction is disabled."
        });
        ConfigHelper.UpdateSetting(config, "MaximumGroundBlocks", "25", 1, 5, new String[] {
            "Maximum amount of ground blocks an elevator can have. Limits the size.", "BE CAREFUL, large values can cause performance problems."
        });
        ConfigHelper.UpdateSetting(config, "AuthenticationRange", "8", 2, 6, new String[] {
            "All players within this range are checked for authentication when requesting a move.", "Every player will be asked for password, if required.", "If one of the players has an authentication, the elevator will move, independant from the status of the other players within the range."
        });
        ConfigHelper.UpdateSetting(config, "Debugging-Log", "false", 3, 7, new String[] {
            "If this option is set to true, elevators debugging messages will be shown in console and saved to file plugins\\Elevators\\DebuggingLog.log.", "Debugging Messages provide information about function steps and storage coordinates."
        });
        ConfigHelper.UpdateSetting(config, "ForbiddenBlocks", "0,7,12,13", 0, 8, new String[] {
            "Set block IDs that cannot be used as elevator ground blocks.", "Use syntax \"ID1,ID2,IDn\""
        });
        ConfigHelper.UpdateSetting(config, "ForbidExisting", "true", 3, 8, new String[] {
            "If this option is set to true, existing elevators that are using a forbidden block type will stop working."
        });
        ConfigHelper.UpdateSetting(config, "CustomCommands", "glassdoor:gd,glassremove:gr", 0, 9, new String[] {
            "Set custom command names for the elevator commands.", "Use syntax \"elevatorcommand1:customcommandname1,elevatorcommand2:customcommandname2\""
        });
        ConfigHelper.UpdateSetting(config, "ForbiddenBlockMessage", "The block type %I is not allowed as an elevator's ground block.", 0, 10, new String[] {
            "Message when a block was placed as ground block, that is restricted by the ForbiddenBlocks list.", "Use %I as variable for the block's type ID."
        });
        ConfigHelper.UpdateSetting(config, "BlockDestroyedMessage", "To elevator linked block destroyed.", 0, 11, new String[] {
            "Message when a block (that was linked to an elevator) had been destroyed by the player."
        });
        ConfigHelper.UpdateSetting(config, "BlocksDestroyedMessage", "%C to elevator linked blocks destroyed.", 0, 12, new String[] {
            "Message when several blocks (that were linked to an elevator) had been destroyed by the player.", "Use %C as variable for the block amount."
        });
        if(!ConfigHelper.WriteConfig(config, "ELEVATORS PLUGIN CONFIGURATION"))
            plugin.OpWarning("Elevators - Store - Configuration update failed!");
        commands = new CommandList(config);
        forbidden = new ForbiddenBlocks(config);
    }

    private void ReadStore()
    {
        CreateStore();
        try
        {
            boolean readable = false;
            if(ReadOldStore())
            {
                readable = true;
                needsUpdate = true;
                plugin.OpInfo((new StringBuilder("Elevators - conversion of storage v")).append(data.version).append(" to v").append(10402).append(" outstanding...").toString());
            }
            if(ReadStoreHeader())
            {
                readable = true;
                World world;
                for(Iterator iterator = plugin.server.getWorlds().iterator(); iterator.hasNext(); ReadWorldStore(world, false))
                    world = (World)iterator.next();

            }
            if(!readable)
                plugin.OpWarning("Elevators - no storage file found.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            plugin.OpWarning("Elevators - reading storage failed!");
        }
    }

    private void UpdateDatabase()
    {
        for(Iterator iterator = data.Database.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();
            if(plugin.GetMoveTask(Elev) == null)
            {
                ElevatorsMoveTask MoveTask = new ElevatorsMoveTask(plugin, Elev, ConfigHelper.GetFloat(config, "WaitTime"), ConfigHelper.GetInteger(config, "ButtonPressTime"));
                MoveTask.curY = Elev.elY;
                plugin.MoveTasks.add(MoveTask);
            }
        }

        CleanupStore();
    }

    private boolean ReadStoreHeader()
    {
        IOSection Master = IOHelper.OpenMaster(file142);
        if(Master != null)
        {
            data.nextelID = IOHelper.ReadInteger(Master, "nextelID", 0);
            return true;
        } else
        {
            return false;
        }
    }

    public void ReadWorldStore(World world, boolean print)
    {
        String path = (new StringBuilder(String.valueOf(world.getName()))).append(fs).append("elevators").toString();
        File dir = new File(path);
        int count = 0;
        if(dir.listFiles() != null)
        {
            File afile[];
            int j = (afile = dir.listFiles()).length;
            for(int i = 0; i < j; i++)
            {
                File subfile = afile[i];
                if(!subfile.isDirectory() && subfile.getName().lastIndexOf(".properties") != -1)
                {
                    IOSection ElevMaster = IOHelper.OpenMaster(subfile.getPath());
                    if(ElevMaster != null && ReadElevator(ElevMaster, subfile.getPath()) != null)
                        count++;
                }
            }

            UpdateDatabase();
        }
        World worlds[] = {
            world
        };
        List<World> worlds2 = Arrays.asList(worlds);
        if(print)
            plugin.OpInfo(ElevMessage(count, worlds2));
        if(needsUpdate)
        {
            for(Iterator iterator = data.Database.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();
                if(Elev.elWorld.equals("") && Elev.elWorldOld == world.getUID())
                {
                    Elev.elWorld = world.getName();
                    WriteStore(Elev);
                    converted++;
                }
            }

            boolean isUpdated = true;
            for(Iterator iterator1 = data.Database.iterator(); iterator1.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev2 = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator1.next();
                if(Elev2.elWorld.equals(""))
                    isUpdated = false;
            }

            if(isUpdated)
            {
                needsUpdate = false;
                (new File(file121)).delete();
                String message1 = "1 elevator dropped, ";
                if(dropped != 1)
                    message1 = (new StringBuilder(String.valueOf(dropped))).append(" elevators dropped, ").toString();
                String message2 = "1 elevator saved.";
                if(converted != 1)
                    message2 = (new StringBuilder(String.valueOf(converted))).append(" elevators saved.").toString();
                plugin.OpInfo((new StringBuilder("Elevators - conversion completed! ")).append(message1).append(message2).toString());
            }
        }
    }

    private boolean ReadOldStore()
    {
        IOSection Master = IOHelper.OpenMaster(file121);
        if(Master != null)
        {
            data.nextelID = IOHelper.ReadInteger(Master, "nextelID", 0);
            data.version = IOHelper.ReadInteger(Master, "StoreVersion");
            for(Iterator iterator = IOHelper.GetSubSections(Master).iterator(); iterator.hasNext();)
            {
                IOSection inputElev = (IOSection)iterator.next();
                ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = ReadElevator(inputElev, file121);
                if(Elev != null)
                {
                    Elev.elWorld = "";
                    Elev.elWorldOld = IOHelper.ReadUUID(inputElev, "elWorld");
                }
            }

            return true;
        } else
        {
            return false;
        }
    }

    private ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 ReadElevator(IOSection inputElev, String filename)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev;
        Iterator iterator;
        Elev = new ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121(data, IOHelper.ReadInteger(inputElev, "elX1", 0), IOHelper.ReadInteger(inputElev, "elX2", 0), IOHelper.ReadInteger(inputElev, "elY", 0), IOHelper.ReadInteger(inputElev, "elZ1", 0), IOHelper.ReadInteger(inputElev, "elZ2", 0), IOHelper.ReadInteger(inputElev, "elType", 0), IOHelper.ReadByte(inputElev, "elData", (byte)0), IOHelper.ReadString(inputElev, "elWorld", ""), IOHelper.ReadInteger(inputElev, "selfID", 0), IOHelper.ReadInteger(inputElev, "StoreVersion", 10402), IOHelper.ReadString(inputElev, "owner", ""));
        iterator = data.Database.iterator();
          goto _L1
_L4:
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 existing = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();
        if(existing.selfID != Elev.selfID) goto _L1; else goto _L2
_L2:
        if(existing.elWorld.equals(""))
        {
            ElevatorsMoveTask MoveTask = plugin.GetMoveTask(existing);
            if(MoveTask != null)
            {
                MoveTask.clear();
                plugin.MoveTasks.remove(MoveTask);
            }
            data.Database.remove(existing);
            dropped++;
            break; /* Loop/switch isn't completed */
        }
        Elev = null;
        return null;
_L1:
        if(iterator.hasNext()) goto _L4; else goto _L3
_L3:
        Elev.password = IOHelper.ReadString(inputElev, "password", "");
        Elev.users = IOHelper.ReadStringList(inputElev, "users", null);
        Elev.nextblockID = IOHelper.ReadInteger(inputElev, "nextblockID", Elev.nextblockID);
        Elev.nextbuildID = IOHelper.ReadInteger(inputElev, "nextbuildID", Elev.nextbuildID);
        Elev.nextglassID = IOHelper.ReadInteger(inputElev, "nextglassID", Elev.nextglassID);
        Elev.locked = IOHelper.ReadBoolean(inputElev, "locked", false);
        data.Database.add(Elev);
        IOSection inputBuildBlocks = IOHelper.GetSubSection(inputElev, "BuildBlocks");
        if(inputBuildBlocks != null)
        {
            Elev.BuildBlocks = new ArrayList();
            ReadElevatorBlocks(inputBuildBlocks, Elev.BuildBlocks);
        }
        IOSection inputSpecialBlocks = IOHelper.GetSubSection(inputElev, "SpecialBlocks");
        ReadElevatorBlocks(inputSpecialBlocks, Elev.SpecialBlocks);
        IOSection inputGlassDoors = IOHelper.GetSubSection(inputElev, "GlassDoors");
        if(inputGlassDoors != null)
            ReadElevatorBlocks(inputGlassDoors, Elev.GlassDoors);
        return Elev;
        Exception e;
        e;
        e.printStackTrace();
        plugin.OpWarning((new StringBuilder("Elevators - Store - Section missing! (file\"")).append(filename).append("\")").toString());
        return null;
    }

    private void ReadElevatorBlocks(IOSection Section, ArrayList BlockStore)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block;
        for(Iterator iterator = IOHelper.GetSubSections(Section).iterator(); iterator.hasNext(); BlockStore.add(block))
        {
            IOSection inputBlock = (IOSection)iterator.next();
            block = new ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121(IOHelper.ReadInteger(inputBlock, "BlockX", 0), IOHelper.ReadInteger(inputBlock, "BlockY", 0), IOHelper.ReadInteger(inputBlock, "BlockZ", 0), IOHelper.ReadInteger(inputBlock, "BlockType", 0), IOHelper.ReadByte(inputBlock, "BlockData", (byte)0), IOHelper.ReadString(inputBlock, "Parameter", ""), IOHelper.ReadInteger(inputBlock, "selfID", 0), IOHelper.ReadInteger(inputBlock, "targetID", 0), IOHelper.ReadBoolean(inputBlock, "Relative", true));
            block.password = IOHelper.ReadString(inputBlock, "password", "");
            block.users = IOHelper.ReadStringList(inputBlock, "users", null);
        }

    }

    private void CreateStore()
    {
        data = new ElevatorsStoreFormat121();
    }

    private String ElevFile(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        return (new StringBuilder(String.valueOf(Elev.elWorld))).append(fs).append("elevators").append(fs).append(Elev.elX1).append("_").append(Elev.elX2).append("_").append(Elev.elZ1).append("_").append(Elev.elZ2).append(".properties").toString();
    }

    public String ElevMessage(int count, List worlds)
    {
        if(worlds.size() < 1)
            return "";
        String message = "";
        if(worlds.size() == 1)
        {
            message = (new StringBuilder("in world \"")).append(((World)worlds.get(0)).getName()).append("\"").toString();
        } else
        {
            message = "in worlds ";
            String worldnames[] = new String[worlds.size()];
            for(int i = 0; i < worlds.size(); i++)
                worldnames[i] = (new StringBuilder("\"")).append(((World)worlds.get(i)).getName()).append("\"").toString();

            message = (new StringBuilder(String.valueOf(message))).append(ElevatorSubRoutines.GetList(worldnames, ", ")).toString();
        }
        String message2 = "";
        if(count == 1)
            message2 = (new StringBuilder("1 elevator ")).append(message).append(" loaded.").toString();
        else
            message2 = (new StringBuilder(String.valueOf(count))).append(" elevators ").append(message).append(" loaded.").toString();
        return message2;
    }

    public void WriteStore(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        IOSection Master = IOHelper.NewMaster();
        IOHelper.WriteInteger(Master, "nextelID", data.nextelID);
        IOHelper.WriteInteger(Master, "StoreVersion", 10402);
        if(!IOHelper.SaveMaster(Master, file142, "ElevatorsStore Header"))
            plugin.OpWarning("Elevators - Store - saving header data failed!");
        if(Elev == null)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev2;
            for(Iterator iterator = data.Database.iterator(); iterator.hasNext(); WriteWorldStore(Elev2))
                Elev2 = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();

        } else
        {
            WriteWorldStore(Elev);
        }
    }

    public void DeleteStore(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        (new File(ElevFile(Elev))).delete();
    }

    private void WriteWorldStore(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        if(Elev.elWorld.equals(""))
            return;
        IOSection Master = IOHelper.NewMaster();
        WriteElevator(Master, Elev);
        if(!IOHelper.SaveMaster(Master, ElevFile(Elev), "ElevatorsStore Elevator"))
            plugin.OpWarning("Elevators - Store - saving elevator data failed!");
    }

    private void WriteElevator(IOSection outElev, ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        IOHelper.WriteInteger(outElev, "selfID", Elev.selfID);
        IOHelper.WriteInteger(outElev, "elType", Elev.elType);
        IOHelper.WriteByte(outElev, "elData", Elev.elData);
        IOHelper.WriteInteger(outElev, "elX1", Elev.elX1);
        IOHelper.WriteInteger(outElev, "elX2", Elev.elX2);
        IOHelper.WriteInteger(outElev, "elY", Elev.elY);
        IOHelper.WriteInteger(outElev, "elZ1", Elev.elZ1);
        IOHelper.WriteInteger(outElev, "elZ2", Elev.elZ2);
        IOHelper.WriteString(outElev, "elWorld", Elev.elWorld);
        IOHelper.WriteInteger(outElev, "nextblockID", Elev.nextblockID);
        IOHelper.WriteInteger(outElev, "nextbuildID", Elev.nextbuildID);
        IOHelper.WriteInteger(outElev, "nextglassID", Elev.nextglassID);
        IOHelper.WriteInteger(outElev, "StoreVersion", Elev.StoreVersion);
        IOHelper.WriteString(outElev, "password", Elev.password);
        IOHelper.WriteString(outElev, "owner", Elev.owner);
        IOHelper.WriteStringList(outElev, "users", Elev.users);
        IOHelper.WriteBoolean(outElev, "locked", Elev.locked);
        IOSection outSpecialBlocks = IOHelper.WriteSection(outElev, "SpecialBlocks");
        WriteElevatorBlocks(outSpecialBlocks, Elev.SpecialBlocks, "SpecialBlock");
        if(Elev.BuildBlocks != null)
        {
            IOSection outBuildBlocks = IOHelper.WriteSection(outElev, "BuildBlocks");
            WriteElevatorBlocks(outBuildBlocks, Elev.BuildBlocks, "BuildBlock");
        }
        IOSection outGlassDoors = IOHelper.WriteSection(outElev, "GlassDoors");
        WriteElevatorBlocks(outGlassDoors, Elev.GlassDoors, "GlassDoor");
    }

    private void WriteElevatorBlocks(IOSection Section, ArrayList Blocks, String BlockName)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block;
        IOSection outBlock;
        for(Iterator iterator = Blocks.iterator(); iterator.hasNext(); IOHelper.WriteStringList(outBlock, "users", Block.users))
        {
            Block = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            outBlock = IOHelper.WriteSection(Section, BlockName);
            IOHelper.WriteInteger(outBlock, "selfID", Block.selfID);
            IOHelper.WriteInteger(outBlock, "BlockX", Block.BlockX);
            IOHelper.WriteInteger(outBlock, "BlockY", Block.BlockY);
            IOHelper.WriteInteger(outBlock, "BlockZ", Block.BlockZ);
            IOHelper.WriteInteger(outBlock, "BlockType", Block.BlockType);
            IOHelper.WriteString(outBlock, "Parameter", Block.Parameter);
            IOHelper.WriteInteger(outBlock, "targetID", Block.targetID);
            IOHelper.WriteBoolean(outBlock, "Relative", Block.Relative);
            IOHelper.WriteByte(outBlock, "BlockData", Block.BlockData);
            IOHelper.WriteString(outBlock, "password", Block.password);
        }

    }

    private void CleanupStore()
    {
        for(Iterator iterator = data.Database.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();
            ArrayList SpecialRemoves = new ArrayList();
            for(Iterator iterator1 = Elev.SpecialBlocks.iterator(); iterator1.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
                if(SpecialBlock.targetID >= 0 && ElevatorSubRoutines.GetSpecialBlock(Elev, SpecialBlock.targetID) == null)
                    SpecialRemoves.add(SpecialBlock);
            }

            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 RemBlock;
            for(Iterator iterator2 = SpecialRemoves.iterator(); iterator2.hasNext(); RemoveLinkedBlock(Elev, RemBlock, null))
                RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator2.next();

            SpecialRemoves.clear();
            SpecialRemoves = null;
            ArrayList GlassRemoves = new ArrayList();
            for(Iterator iterator3 = Elev.GlassDoors.iterator(); iterator3.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator3.next();
                if(GlassBlock.targetID != -1 && ElevatorSubRoutines.GetSpecialBlock(Elev, GlassBlock.targetID) == null)
                    GlassRemoves.add(GlassBlock);
            }

            for(Iterator iterator4 = GlassRemoves.iterator(); iterator4.hasNext(); RemoveGlassDoor(Elev, RemBlock))
                RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator4.next();

            GlassRemoves.clear();
            GlassRemoves = null;
        }

    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 AddElevator(int X1, int X2, int Y, int Z1, int Z2, int type, byte Data, 
            String world, String owner)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = new ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121(X1, X2, Y, Z1, Z2, type, Data, world, data.nextelID, 10402, owner);       
        data.Database.add(Elev);
        ElevatorsMoveTask MoveTask = new ElevatorsMoveTask(plugin, Elev, ConfigHelper.GetFloat(config, "WaitTime"), ConfigHelper.GetInteger(config, "ButtonPressTime"));
        MoveTask.curY = Y;
        plugin.MoveTasks.add(MoveTask);
        data.nextelID++;
        WriteStore(Elev);
        return Elev;
    }

    public void RemoveElevator(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        Elev.SpecialBlocks.clear();
        if(Elev.BuildBlocks != null)
            Elev.BuildBlocks.clear();
        if(Elev.GlassDoors != null)
            Elev.GlassDoors.clear();
        ElevatorsMoveTask MoveTask = plugin.GetMoveTask(Elev);
        MoveTask.clear();
        plugin.MoveTasks.remove(MoveTask);
        DeleteStore(Elev);
        data.Database.remove(Elev);
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 FindNearbyElevatorAcc(int X, int Y, int Z, World world)
    {
        return FindNearbyElevatorAcc(X, Y, Z, world, Tolerance);
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 FindNearbyElevatorAcc(int X, int Y, int Z, World world, int tol)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Result = null;
        int distance = -1;
        for(Iterator iterator = data.Database.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = (ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121)iterator.next();
            if(Elev.elWorld.equals(world.getName()) && X >= Elev.elX2 - tol && X <= Elev.elX1 + tol && Z >= Elev.elZ2 - tol && Z <= Elev.elZ1 + tol)
            {
                int curdist = Math.min(Math.abs(Elev.elX1 - X), Math.abs(Elev.elX2 - X)) + Math.min(Math.abs(Elev.elZ1 - Z), Math.abs(Elev.elZ2 - Z));
                if(curdist < distance || distance == -1)
                {
                    Result = Elev;
                    distance = curdist;
                }
            }
        }

        if(Result != null && Result.StoreVersion < 10402)
            VersionUpdater(plugin.GetMoveTask(Result));
        return Result;
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 FindNearbyElevatorMod(int X, int Y, int Z, World world, Player player, String message)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Result = FindNearbyElevatorAcc(X, Y, Z, world, Tolerance);
        if(Result == null)
        {
            if(!message.equalsIgnoreCase("ignore"))
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder(String.valueOf(message))).append(" No nearby elevator found.").toString());
            return null;
        }
        if(ConfigHelper.GetBoolean(config, "owner-restriction") && !player.getName().equals(Result.owner) && !Result.owner.equals("") && !plugin.comm.Permission(player, ElevatorsPluginCommunicator.PermissionLevel.OPERATOR))
        {
            if(!message.equalsIgnoreCase("ignore"))
                ElevatorSubRoutines.WarnPlayer(player, (new StringBuilder(String.valueOf(message))).append(" Only the owner can modify the elevator.").toString());
            return null;
        } else
        {
            return Result;
        }
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 AddSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int X, int Y, int Z, int Type, boolean rel)
    {
        return AddSpecialBlock(Elev, X, Y, Z, Type, "", -1, rel);
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 AddSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int X, int Y, int Z, int Type, int tID)
    {
        return AddSpecialBlock(Elev, X, Y, Z, Type, "", tID, false);
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 AddSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int X, int Y, int Z, int Type, String FloorName)
    {
        return AddSpecialBlock(Elev, X, Y, Z, Type, FloorName, -1, false);
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 AddSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int X, int Y, int Z, int Type, String Parameter, int tID, 
            boolean rel)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Result = new ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121(X, Y, Z, Type, (byte)0, Parameter, Elev.nextblockID, tID, rel);
        Elev.SpecialBlocks.add(Result);
        plugin.GetMoveTask(Elev).PressedTimes.add(new ElevatorsMoveTask.MoveTaskCounter(plugin.GetMoveTask(Elev), Elev.nextblockID, plugin.GetMoveTask(Elev)));
        Elev.nextblockID++;
        WriteStore(Elev);
        if(Type == 0 && plugin.GetMoveTask(Elev).curY == Y - 2)
            plugin.GetMoveTask(Elev).curFloor = Result;
        return Result;
    }

    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 AddGlassDoor(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int X, int Y, int Z, int tID, boolean relative, byte dir)
    {
        for(Iterator iterator = Elev.GlassDoors.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 glass = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(glass.BlockX == X && glass.BlockY == Y && glass.BlockZ == Z)
            {
                glass.BlockData = dir;
                glass.targetID = tID;
                return glass;
            }
        }

        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Result = new ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121(X, Y, Z, 20, dir, "", Elev.nextglassID, tID, relative);
        Elev.GlassDoors.add(Result);
        Elev.nextglassID++;
        WriteStore(Elev);
        return Result;
    }

    public void RemoveGlassDoor(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock)
    {
        Elev.GlassDoors.remove(GlassBlock);
    }

    private void RemoveSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block, ArrayList Removes)
    {
        Removes.add(Block);
        ElevatorsMoveTask MoveTask = plugin.GetMoveTask(Elev);
        for(Iterator iterator = MoveTask.PressedTimes.iterator(); iterator.hasNext();)
        {
            ElevatorsMoveTask.MoveTaskCounter PrTime = (ElevatorsMoveTask.MoveTaskCounter)iterator.next();
            if(PrTime.BlockID == Block.selfID)
            {
                PrTime.stopPressrun();
                MoveTask.PressedTimes.remove(PrTime);
                break;
            }
        }

        for(Iterator iterator1 = Elev.SpecialBlocks.iterator(); iterator1.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
            if(SpecialBlock.targetID == Block.selfID)
                RemoveSpecialBlock(Elev, SpecialBlock, Removes);
        }

    }

    public void RemoveLinkedBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block, Player player)
    {
        if(Block.BlockType == 0)
        {
            if(plugin.GetMoveTask(Elev).curFloor == Block)
                plugin.GetMoveTask(Elev).curFloor = null;
            ArrayList GlassRemoves = new ArrayList();
            for(Iterator iterator = Elev.GlassDoors.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                if(GlassBlock.targetID == Block.selfID)
                    GlassRemoves.add(GlassBlock);
            }

            for(Iterator iterator1 = GlassRemoves.iterator(); iterator1.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
                if(Elev.GlassDoors.indexOf(RemBlock) != -1)
                    Elev.GlassDoors.remove(RemBlock);
            }

            GlassRemoves.clear();
        }
        ArrayList Removes = new ArrayList();
        RemoveSpecialBlock(Elev, Block, Removes);
        int BlocksRemoved = Removes.size();
        for(Iterator iterator2 = Removes.iterator(); iterator2.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 RemBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator2.next();
            if(Elev.SpecialBlocks.indexOf(RemBlock) != -1)
                Elev.SpecialBlocks.remove(RemBlock);
        }

        WriteStore(Elev);
        if(player == null)
            return;
        if(BlocksRemoved == 1)
            ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(config, "BlockDestroyedMessage"));
        else
            ElevatorSubRoutines.WarnPlayer(player, ConfigHelper.GetString(config, "BlocksDestroyedMessage").replaceAll("%C", (new StringBuilder()).append(BlocksRemoved).toString()));
    }

    public void VersionUpdater(ElevatorsMoveTask MoveTask)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int lastVersion = 0;
        if(Elev.StoreVersion < 10202)
        {
            int curY = ElevatorSubRoutines.FindElevatorY(MoveTask);
            Elev.elData = MoveTask.world().getBlockAt(Elev.elX2, curY, Elev.elZ2).getData();
        } else
        {
            lastVersion = 10202;
        }
        if(Elev.StoreVersion < 10300)
            Elev.StoreVersion = 10300;
        else
            lastVersion = 10300;
        if(Elev.StoreVersion < 10301)
            Elev.StoreVersion = 10301;
        else
            lastVersion = 10301;
        if(Elev.StoreVersion < 10400)
            Elev.StoreVersion = 10400;
        else
            lastVersion = 10400;
        if(Elev.StoreVersion < 10401)
        {
            for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                if(SpecialBlock.BlockType == 3)
                    SpecialBlock.BlockType = 5;
            }

            Elev.StoreVersion = 10401;
        } else
        {
            lastVersion = 10401;
        }
        if(Elev.StoreVersion < 10402)
        {
            if(lastVersion < 10401)
                Elev.locked = true;
            Elev.StoreVersion = 10402;
        } else
        {
            lastVersion = 10402;
        }
    }

    public boolean InfoFile()
    {
        WriteStore(null);
        try
        {
            File fOutput = new File(infofile);
            File fdir = fOutput.getParentFile();
            if(!fdir.exists())
                fdir.mkdir();
            if(!fOutput.exists())
                fOutput.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(fOutput));
            output.write("ELEVATORS DEBUGGING INFORMATION FILE");
            output.newLine();
            output.write("===============================================");
            output.newLine();
            output.newLine();
            output.write((new StringBuilder("CraftBukkit version: ")).append(plugin.server.getVersion()).toString());
            output.newLine();
            output.write((new StringBuilder("Plugin version: ")).append(plugin.getDescription().getVersion()).toString());
            output.newLine();
            output.write("Plugin storage version: 10402");
            output.newLine();
            output.newLine();
            output.write("-----------------------------------------------");
            output.newLine();
            output.newLine();
            BufferedReader input = new BufferedReader(new FileReader(file121));
            String line;
            while((line = input.readLine()) != null) 
            {
                output.write(line);
                output.newLine();
            }
            input.close();
            output.flush();
            output.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ElevatorsStoreFormat121 data;
    private static int Tolerance = 3;
    public static final int CALL_BLOCK = 0;
    public static final int UP_BLOCK = 1;
    public static final int DOWN_BLOCK = 2;
    public static final int CALL_EXTEND_BLOCK_OLD = 3;
    public static final int REDSTONE_OUT = 4;
    public static final int DIRECT_BLOCK = 5;
    public static final int GLASS_DOOR = 6;
    private Elevators plugin;
    public static final String fs = System.getProperty("file.separator");
    public static final String dir12 = (new StringBuilder("plugins")).append(fs).append("Elevators").toString();
    private static final String file121 = (new StringBuilder(String.valueOf(dir12))).append(fs).append("ElevatorsStore121.properties").toString();
    private static final String file142 = (new StringBuilder(String.valueOf(dir12))).append(fs).append("StorageHeader.properties").toString();
    private static final String config13 = (new StringBuilder(String.valueOf(dir12))).append(fs).append("Configuration.txt").toString();
    private static final String infofile = (new StringBuilder(String.valueOf(dir12))).append(fs).append("DebuggingInfo.txt").toString();
    public static final int version = 10402;
    public Configuration config;
    public CommandList commands;
    public ForbiddenBlocks forbidden;
    private boolean needsUpdate;
    private int dropped;
    private int converted;

}