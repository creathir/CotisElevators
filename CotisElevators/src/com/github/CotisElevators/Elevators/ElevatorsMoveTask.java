
package com.github.CotisElevators.Elevators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

// Referenced classes of package com.github.CotisElevators.Elevators:
//            ElevatorSubRoutines, Elevators, NetworkManagerEx, ElevatorsStoreFormat121

public class ElevatorsMoveTask
    implements Runnable
{
    public static class AdjacentRail
    {

        public Block rail;
        public int type;

        public AdjacentRail(Block block)
        {
            rail = block;
            type = block.getTypeId();
        }
    }

    public static class BuildBlockInventory
    {

        public void CopyBack(ElevatorsMoveTask MoveTask)
        {
            BlockState state = ElevatorSubRoutines.GetBlock(MoveTask, block).getState();
            if(state instanceof Chest)
                ((Chest)state).getInventory().setContents(ElevatorSubRoutines.InventoryCopy(items));
            if(state instanceof Dispenser)
                ((Dispenser)state).getInventory().setContents(ElevatorSubRoutines.InventoryCopy(items));
        }

        protected ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block;
        protected ItemStack items[];

        public BuildBlockInventory(ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block, Inventory inventory)
        {
            this.block = block;
            items = ElevatorSubRoutines.InventoryCopy(inventory.getContents());
        }
    }

    public static class FurnaceStorage extends BuildBlockInventory
    {

        public void CopyBack(ElevatorsMoveTask MoveTask)
        {
            BlockState state = ElevatorSubRoutines.GetBlock(MoveTask, block).getState();
            if(state instanceof Furnace)
            {
                ((Furnace)state).getInventory().setContents(ElevatorSubRoutines.InventoryCopy(items));
                ((Furnace)state).setBurnTime(BurnTime);
                ((Furnace)state).setCookTime(CookTime);
            }
        }

        private short BurnTime;
        private short CookTime;

        FurnaceStorage(ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block, Furnace furnace)
        {
            super(block, furnace.getInventory());
            BurnTime = furnace.getBurnTime();
            CookTime = furnace.getCookTime();
        }
    }

    public class GlassDoorSide
        implements Runnable
    {

        public void run()
        {
            for(Iterator iterator = glassblocks.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SFglass = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                boolean change = false;
                int value = 0;
                switch(direction)
                {
                case 0: // '\0'
                    value = SFglass.BlockX;
                    break;

                case 1: // '\001'
                    value = SFglass.BlockY;
                    break;

                case 2: // '\002'
                    value = SFglass.BlockZ;
                    break;
                }
                if(direction2 == 6 || direction2 == 9)
                {
                    if((double)value == Math.floor((FirstVal + (LastVal - FirstVal) / 2) - CurVal) || (double)value == Math.ceil(FirstVal + (LastVal - FirstVal) / 2 + CurVal))
                        change = true;
                } else
                if(value == FirstVal + CurVal)
                    change = true;
                if(change)
                {
                    Block glass = ElevatorSubRoutines.GetBlock(MoveTask, SFglass);
                    glass.setTypeId(newID);
                    if(SFglass.Relative && MoveTask.BuildBlocks != null)
                    {
                        for(Iterator iterator1 = MoveTask.BuildBlocks.iterator(); iterator1.hasNext();)
                        {
                            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator1.next();
                            if(BuildBlock.BlockX == SFglass.BlockX && BuildBlock.BlockY == SFglass.BlockY && BuildBlock.BlockZ == SFglass.BlockZ)
                            {
                                MoveTask.BuildBlocks.remove(BuildBlock);
                                break;
                            }
                        }

                        if(newID == 20)
                            MoveTask.BuildBlocks.add(SFglass);
                    }
                }
            }

            if(direction2 == 6 || direction2 == 9)
            {
                if(Increase == 1)
                {
                    if(CurVal >= (LastVal - FirstVal) / 2)
                        stopglassrun(true);
                } else
                if(CurVal == 0)
                    stopglassrun(true);
            } else
            if(CurVal == LastVal - FirstVal)
                stopglassrun(true);
            CurVal += Increase;
        }

        public void stopglassrun(boolean Remove)
        {
            scheduler.cancelTask(TimerID);
            glassblocks.clear();
            if(Remove)
                GlassDoors.remove(this);
        }

        public void startglassrun()
        {
            FirstVal = -1;
            LastVal = -1;
            if(direction2 >= 4 && direction2 <= 6)
                direction = 1;
            if(direction == 3)
                direction = 0;
            Iterator iterator = glassblocks.iterator();
            while(iterator.hasNext()) 
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SFglass = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
label0:
                for(Iterator iterator1 = GlassDoors.iterator(); iterator1.hasNext();)
                {
                    GlassDoorSide existing = (GlassDoorSide)iterator1.next();
                    for(Iterator iterator2 = existing.glassblocks.iterator(); iterator2.hasNext();)
                    {
                        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 existingblock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator2.next();
                        if(existingblock.equals(SFglass))
                        {
                            existing.glassblocks.remove(existingblock);
                            break label0;
                        }
                    }

                }

                int tmpCurVal;
                switch(direction)
                {
                default:
                    continue;

                case 0: // '\0'
                    tmpCurVal = SFglass.BlockX;
                    break;

                case 1: // '\001'
                    tmpCurVal = SFglass.BlockY;
                    break;

                case 2: // '\002'
                    tmpCurVal = SFglass.BlockZ;
                    break;
                }
                if(FirstVal == -1 && LastVal == -1)
                {
                    FirstVal = tmpCurVal;
                    LastVal = tmpCurVal;
                } else
                {
                    FirstVal = Math.min(FirstVal, tmpCurVal);
                    LastVal = Math.max(LastVal, tmpCurVal);
                }
            }
            LastVal++;
            float Xmid = selfElev.elX2 + (selfElev.elX1 - selfElev.elX2) / 2;
            float Zmid = selfElev.elZ2 + (selfElev.elZ1 - selfElev.elZ2) / 2;
            if(direction == 0 && (float)refZ > Zmid && direction2 == 7 || direction == 0 && (float)refZ < Zmid && direction2 == 8 || direction == 2 && (float)refX > Xmid && direction2 == 8 || direction == 2 && (float)refX < Xmid && direction2 == 7 || direction == 1 && direction2 == 5)
            {
                int tmpVal = LastVal;
                LastVal = FirstVal;
                FirstVal = tmpVal;
            }
            if(newID > 0)
            {
                int tmpVal = LastVal;
                LastVal = FirstVal;
                FirstVal = tmpVal;
            }
            if(LastVal > FirstVal)
                Increase = 1;
            else
                Increase = -1;
            int Period = 15 / (Math.abs(LastVal - FirstVal) + 1);
            if(direction2 == 6 || direction2 == 9)
                Period *= 2;
            CurVal = 0;
            if((direction2 == 6 || direction2 == 9) && Increase == -1)
            {
                int tmpVal = LastVal;
                LastVal = FirstVal;
                FirstVal = tmpVal;
                CurVal = (int)Math.ceil((LastVal - FirstVal) / 2);
            }
            TimerID = scheduler.scheduleSyncRepeatingTask(plugin, this, 0L, Period);
            GlassDoors.add(this);
        }

        public static final int XDIR = 0;
        public static final int YDIR = 1;
        public static final int ZDIR = 2;
        public static final int UNDEF = 3;
        public int direction;
        public ArrayList glassblocks;
        public int refX;
        public int refZ;
        public boolean refrel;
        public byte direction2;
        private int FirstVal;
        private int LastVal;
        private int Increase;
        private int CurVal;
        private int TimerID;
        private int newID;
        private ElevatorsMoveTask MoveTask;
        final ElevatorsMoveTask this$0;

        public GlassDoorSide(int newtypeID, ElevatorsMoveTask parent)
        {
            super();
            this$0 = ElevatorsMoveTask.this;
            newID = newtypeID;
            direction = 3;
            glassblocks = new ArrayList();
            MoveTask = parent;
        }
    }

    public class MoveTaskCounter
        implements Runnable
    {

        public void stopPressrun()
        {
            if(PressThread >= 0)
                scheduler.cancelTask(PressThread);
            PressThread = -2;
        }

        public void run()
        {
            PressThread = -2;
            int BlockType = -1;
            Location loc = null;
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock;
            try
            {
                SpecialBlock = ElevatorSubRoutines.GetSpecialBlock(selfElev, BlockID);
                loc = ElevatorSubRoutines.GetBlock(parent, SpecialBlock).getLocation();
                BlockType = SpecialBlock.BlockType;
            }
            catch(Exception e)
            {
                return;
            }
            if(BlockType == 1)
            {
                plugin.log(Level.INFO, (new StringBuilder("Elevator movement initiated via UP_BLOCK ")).append(SpecialBlock.toString()).toString());
                plugin.MoveElevatorUp(selfElev, loc, counter);
            }
            if(BlockType == 2)
            {
                plugin.log(Level.INFO, (new StringBuilder("Elevator movement initiated via DOWN_BLOCK ")).append(SpecialBlock.toString()).toString());
                plugin.MoveElevatorDown(selfElev, loc, counter);
            }
            counter = 0;
        }

        public int counter;
        public int BlockID;
        public int PressThread;
        private ElevatorsMoveTask parent;
        final ElevatorsMoveTask this$0;

        public MoveTaskCounter(int ID, ElevatorsMoveTask MoveTask)
        {
            super();
            this$0 = ElevatorsMoveTask.this;
            counter = 0;
            BlockID = ID;
            PressThread = -2;
            parent = MoveTask;
        }
    }

    public enum StopCode {INTEGRITY_STOP_BB_OVERFLOW,INTEGRITY_STOP_BB_NOTDEPENDENT};
  

    public void run()
    {
        if((double)System.currentTimeMillis() < (double)lastTick + delay)
            return;
        lastTick += (int)delay;
        if(Floors.size() == 0)
        {
            stoprun();
            return;
        }
        if(delay == 600D)
            ElevatorSubRoutines.EntityScan(this);
        if(delay == (double)waitdelay)
        {
            delay = 600D;
            ElevatorSubRoutines.EntityScan(this);
            plugin.net.SendClientInit(this);
            return;
        }
        if(Math.abs(curY - (((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)Floors.get(0)).BlockY - 2)) <= 2)
        {
            delay = Math.min(600D, delay * 2D);
            if(curFloor == null)
            {
                curFloor = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)Floors.get(0);
                setFloorActivations(true);
            }
        } else
        {
            delay = Math.max(150D, delay / 2D);
            if(curFloor != null)
            {
                setFloorActivations(false);
                curFloor = null;
            }
        }
        int Status = plugin.DoMoveElevator(this);
        if(Status == 2 || Status == 0)
        {
            Floors.remove(0);
            callloc.remove(0);
            entities.clear();
            plugin.net.SendClientStop(this);
        }
        if(Status == 2 && Floors.size() >= 1)
            delay = waitdelay;
        if(Status == 0 || Status == 2 && Floors.size() < 1)
        {
            delay = 600D;
            stoprun();
        }
    }

    public void setFloorActivations(boolean State)
    {
        plugin.ToggleRedstoneOutputs(this, curFloor, State);
        plugin.ToggleGlassDoor(this, curFloor, State);
    }

    public void startrun()
    {
        if(Floors.size() < 1)
            return;
        if(curFloor == Floors.get(0))
            setFloorActivations(true);
        if(ThreadRunning < 0)
        {
            lastTick = System.currentTimeMillis();
            ElevatorSubRoutines.EntityScan(this);
            ThreadRunning = scheduler.scheduleSyncRepeatingTask(plugin, this, 2L, 2L);
            plugin.net.SendClientInit(this);
        }
    }

    private void stoprun()
    {
        if(ThreadRunning >= 0)
            scheduler.cancelTask(ThreadRunning);
        if(BuildBlocks != null)
            BuildBlocks = null;
        ThreadRunning = -2;
    }

    public void clear()
    {
        stoprun();
        MoveTaskCounter PressedTime;
        for(Iterator iterator = PressedTimes.iterator(); iterator.hasNext(); PressedTime.stopPressrun())
            PressedTime = (MoveTaskCounter)iterator.next();

        PressedTimes.clear();
        Floors.clear();
        callloc.clear();
        selfElev = null;
        worldaddr = null;
        curFloor = null;
        plugin = null;
        AdjacentRails.clear();
        Inventories.clear();
        GlassDoorSide glass;
        for(Iterator iterator1 = GlassDoors.iterator(); iterator1.hasNext(); glass.stopglassrun(false))
            glass = (GlassDoorSide)iterator1.next();

        GlassDoors.clear();
        entities.clear();
        ignoredStopcodes.clear();
    }

    public World world()
    {
        if(worldaddr == null)
        {
            worldaddr = plugin.server.getWorld(selfElev.elWorld);
            if(worldaddr == null)
                return null;
            curY = ElevatorSubRoutines.FindElevatorY(this);
            for(Iterator iterator = selfElev.SpecialBlocks.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                if(SpecialBlock.BlockType == 0 && curY == SpecialBlock.BlockY - 2)
                    curFloor = SpecialBlock;
            }

        }
        return worldaddr;
    }

    public ElevatorsMoveTask(Elevators instance, ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, float waitdelay, int pressdelay)
    {
        delay = 400D;
        plugin = instance;
        worldaddr = null;
        ThreadRunning = -2;
        scheduler = plugin.getServer().getScheduler();
        selfElev = Elev;
        Floors = new ArrayList();
        callloc = new ArrayList();
        PressedTimes = new ArrayList();
        AdjacentRails = new ArrayList();
        Inventories = new ArrayList();
        GlassDoors = new ArrayList();
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock;
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext(); PressedTimes.add(new MoveTaskCounter(SpecialBlock.selfID, this)))
            SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();

        curFloor = null;
        this.waitdelay = waitdelay * 1000F;
        this.pressdelay = (pressdelay * 20) / 1000;
        entities = new ArrayList();
        ignoredStopcodes = new ArrayList();
    }

    public void AddPress(ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block)
    {
        for(Iterator iterator = PressedTimes.iterator(); iterator.hasNext();)
        {
            MoveTaskCounter PressedTime = (MoveTaskCounter)iterator.next();
            if(PressedTime.BlockID == Block.selfID)
            {
                PressedTime.counter++;
                if(PressedTime.PressThread >= 0)
                    scheduler.cancelTask(PressedTime.PressThread);
                PressedTime.PressThread = scheduler.scheduleSyncDelayedTask(plugin, PressedTime, pressdelay);
            }
        }

    }

    public int curY;
    private float waitdelay;
    private double delay;
    private final double maxdelay = 600D;
    private final double mindelay = 150D;
    private int pressdelay;
    public Elevators plugin;
    public ArrayList Floors;
    public ArrayList callloc;
    public ArrayList PressedTimes;
    public ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 curFloor;
    public ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 selfElev;
    public int ThreadRunning;
    private long lastTick;
    private BukkitScheduler scheduler;
    private World worldaddr;
    public ArrayList AdjacentRails;
    public ArrayList Inventories;
    public ArrayList GlassDoors;
    public ArrayList entities;
    public ArrayList BuildBlocks;
    public StopCode stopcode;
    public ArrayList ignoredStopcodes;

}