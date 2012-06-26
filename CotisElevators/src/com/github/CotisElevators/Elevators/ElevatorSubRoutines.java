
package com.github.CotisElevators.Elevators;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

// Referenced classes of package com.gmail.creathir.Elevators:
//            ElevatorsMoveTask, ElevatorsStoreFormat121

public class ElevatorSubRoutines
{
    public static class BlockDependenceClass
    {

        public void AddItem(int X, int Y, int Z, int dependent)
        {
            list.add(new BlockDependenceItem(X, Y, Z, dependent));
        }

        public int IsDependent(int X, int Y, int Z)
        {
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                BlockDependenceItem item = (BlockDependenceItem)iterator.next();
                if(item.X == X && item.Y == Y && item.Z == Z)
                    return item.dependent;
            }

            return -1;
        }

        public void ChangeDependence(int X, int Y, int Z, int newdep)
        {
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                BlockDependenceItem item = (BlockDependenceItem)iterator.next();
                if(item.X == X && item.Y == Y && item.Z == Z)
                    item.dependent = newdep;
            }

        }

        protected void finalize()
            throws Throwable
        {
            list.clear();
            list = null;
            super.finalize();
        }

        private static final long serialVersionUID = 0x2e47a80db53940eL;
        private ArrayList list;

        public BlockDependenceClass()
        {
            list = new ArrayList();
        }
    }

    private static class BlockDependenceItem
    {

        public int X;
        public int Y;
        public int Z;
        public int dependent;

        public BlockDependenceItem(int X, int Y, int Z, int dependent)
        {
            this.X = X;
            this.Y = Y;
            this.Z = Z;
            this.dependent = dependent;
        }
    }


    public ElevatorSubRoutines()
    {
    }

    public static int[] ResolvePlayerLocation(Location loc)
    {
        int values[] = new int[3];
        values[0] = (int)loc.getX();
        if(loc.getX() < 0.0D)
            values[0]--;
        values[1] = (int)loc.getY();
        values[2] = (int)loc.getZ();
        if(loc.getZ() < 0.0D)
            values[2]--;
        return values;
    }

    public static boolean IsInElevator(ElevatorsMoveTask MoveTask, double curY, int resloc[])
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int add = 0;
        if(MoveTask.BuildBlocks != null)
        {
            for(Iterator iterator = MoveTask.BuildBlocks.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                add = Math.max(add, block.BlockY);
            }

        }
        return resloc[0] >= Elev.elX2 && resloc[0] <= Elev.elX1 && resloc[2] >= Elev.elZ2 && resloc[2] <= Elev.elZ1 && (double)resloc[1] >= curY - 1.0D && (double)resloc[1] <= curY + 3D + (double)add;
    }

    private static int CheckElevatorPosition(ElevatorsMoveTask MoveTask, int Y)
    {
        int Matches = 0;
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        for(int X = Elev.elX2; X <= Elev.elX1; X++)
        {
            for(int Z = Elev.elZ2; Z <= Elev.elZ1; Z++)
                if(CheckGroundType(Elev, MoveTask.world().getBlockTypeIdAt(X, Y, Z)))
                    Matches++;

        }

        if(Elev.BuildBlocks != null)
        {
            for(Iterator iterator = Elev.BuildBlocks.iterator(); iterator.hasNext();)
            {
                ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
                if(MoveTask.world().getBlockTypeIdAt(Elev.elX2 + BuildBlock.BlockX, Y + BuildBlock.BlockY, Elev.elZ2 + BuildBlock.BlockZ) == BuildBlock.BlockType)
                    Matches++;
            }

        }
        return Matches;
    }

    public static int FindElevatorY(ElevatorsMoveTask MoveTask)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        boolean curvalid = true;
label0:
        for(int X = Elev.elX2; X <= Elev.elX1; X++)
        {
            for(int Z = Elev.elZ2; Z <= Elev.elZ1; Z++)
            {
                if(CheckGroundType(Elev, MoveTask.world().getBlockTypeIdAt(X, MoveTask.curY, Z)))
                    continue;
                curvalid = false;
                break label0;
            }

        }

        if(curvalid)
            return MoveTask.curY;
        int Matches = 0;
        int Result = -2;
        int diff = 128;
        for(int iY = 0; iY < 128; iY++)
        {
            int curmat = CheckElevatorPosition(MoveTask, iY);
            if(curmat > Matches || curmat == Matches && Math.abs(iY - MoveTask.curY) < diff)
            {
                Matches = curmat;
                Result = iY;
                diff = Math.abs(iY - MoveTask.curY);
            }
        }

        if(Result != -2)
            MoveTask.curY = Result;
        return Result;
    }

    public static boolean EstablishRelativePosition(ElevatorsMoveTask MoveTask, Location loc, int relOut[])
    {
        int Y = FindElevatorY(MoveTask);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        if(Y < 0)
            return false;
        if(!CheckDependence(new BlockDependenceClass(), loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Elev.elX1, Elev.elX2, Y, Elev.elZ1, Elev.elZ2))
        {
            return false;
        } else
        {
            relOut[0] = loc.getBlockX() - Elev.elX2;
            relOut[1] = loc.getBlockY() - Y;
            relOut[2] = loc.getBlockZ() - Elev.elZ2;
            return true;
        }
    }

    public static boolean RelativePosition(ElevatorsMoveTask MoveTask, Location loc, int relOut[])
    {
        int ElevY = FindElevatorY(MoveTask);
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        if(ElevY < 0)
        {
            return false;
        } else
        {
            relOut[0] = loc.getBlockX() - Elev.elX2;
            relOut[1] = loc.getBlockY() - ElevY;
            relOut[2] = loc.getBlockZ() - Elev.elZ2;
            return true;
        }
    }

    public static void RelativePosition(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block, int relOut[])
    {
        if(!block.Relative)
        {
            int ElevY = MoveTask.curY;
            ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
            relOut[0] = block.BlockX - Elev.elX2;
            relOut[1] = block.BlockY - ElevY;
            relOut[2] = block.BlockZ - Elev.elZ2;
        } else
        {
            relOut[0] = block.BlockX;
            relOut[1] = block.BlockY;
            relOut[2] = block.BlockZ;
        }
    }

    public static Block GetBlock(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 StoreBlock)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        if(StoreBlock.Relative)
            return MoveTask.world().getBlockAt(Elev.elX2 + StoreBlock.BlockX, MoveTask.curY + StoreBlock.BlockY, Elev.elZ2 + StoreBlock.BlockZ);
        else
            return MoveTask.world().getBlockAt(StoreBlock.BlockX, StoreBlock.BlockY, StoreBlock.BlockZ);
    }

    public static ItemStack[] InventoryCopy(ItemStack source[])
    {
        ItemStack dest[] = new ItemStack[source.length];
        System.arraycopy(source, 0, dest, 0, source.length);
        return dest;
    }

    public static int[] GetElevatorYBounds(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        int Result[] = {
            128, 0
        };
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(SpecialBlock.BlockType == 0)
            {
                Result[0] = Math.min(SpecialBlock.BlockY, Result[0]);
                Result[1] = Math.max(SpecialBlock.BlockY, Result[1]);
            }
        }

        return Result;
    }

    public static ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GetSpecialBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int BlockID)
    {
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(SpecialBlock.selfID == BlockID)
                return SpecialBlock;
        }

        return null;
    }

    public static ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GetSpecialBlock(ElevatorsMoveTask MoveTask, Location loc)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev = MoveTask.selfElev;
        int RelPos[] = new int[3];
        RelativePosition(MoveTask, loc, RelPos);
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Block = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(!Block.Relative)
            {
                if(loc.getBlockX() == Block.BlockX && Block.BlockY == loc.getBlockY() && loc.getBlockZ() == Block.BlockZ)
                    return Block;
            } else
            if(RelPos != null && RelPos[0] == Block.BlockX && Block.BlockY == RelPos[1] && RelPos[2] == Block.BlockZ)
                return Block;
        }

        return null;
    }

    public static int CheckSpecialBlock(ElevatorsMoveTask MoveTask, Location loc)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = GetSpecialBlock(MoveTask, loc);
        if(SpecialBlock != null)
            return SpecialBlock.BlockType;
        else
            return -1;
    }

    public static ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GetNextFloorUp(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int Y, int Times, int tol)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = null;
        int curY = 128;
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(SpecialBlock.BlockType == 0 && SpecialBlock.BlockY > Y + tol && SpecialBlock.BlockY < curY)
            {
                curY = SpecialBlock.BlockY;
                CallBlock = SpecialBlock;
            }
        }

        if(Times > 1)
            CallBlock = GetNextFloorUp(Elev, curY, Times - 1, tol);
        return CallBlock;
    }

    public static ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GetNextFloorDown(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int Y, int Times, int tol)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock = null;
        int curY = 0;
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(SpecialBlock.BlockType == 0 && SpecialBlock.BlockY < Y - tol && SpecialBlock.BlockY > curY)
            {
                curY = SpecialBlock.BlockY;
                CallBlock = SpecialBlock;
            }
        }

        if(Times > 1)
            CallBlock = GetNextFloorDown(Elev, curY, Times - 1, tol);
        return CallBlock;
    }

    public static ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GetFloor(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, String Floorname)
    {
        for(Iterator iterator = Elev.SpecialBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 SpecialBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(SpecialBlock.BlockType == 0 && SpecialBlock.Parameter.trim().equalsIgnoreCase(Floorname.trim()))
                return SpecialBlock;
        }

        return null;
    }

    public static boolean CheckGroundType(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int givenType)
    {
        if(Elev.elType == givenType)
            return true;
        if(Elev.elType == 44 && givenType == 43)
            return true;
        if(Elev.elType == 2 && givenType == 3)
            return true;
        return Elev.elType == 3 && givenType == 2;
    }

    public static boolean BlockPriority(int TypeMulti, int BlockType)
    {
        boolean Result;
        if(BlockType >= 63 && BlockType <= 78 || BlockType == 50 || BlockType == 55 || BlockType == 59 || BlockType == 51 || BlockType >= 37 && BlockType <= 40 || BlockType == 83 || BlockType == 81 || BlockType == 27 || BlockType == 28)
            Result = true;
        else
            Result = false;
        if(TypeMulti > 0)
            Result = !Result;
        return Result;
    }

    public static boolean FluidBlock(Block block)
    {
        return block.getTypeId() == 0 || block.getTypeId() >= 8 && block.getTypeId() <= 11;
    }

    public static boolean PersistentBlock(Block block)
    {
        return BlockPriority(1, block.getTypeId()) && !FluidBlock(block);
    }

    public static boolean InsideBlock(Location location, Block block)
    {
        return location.getX() >= (double)block.getX() && location.getX() <= (double)(block.getX() + 1) && location.getY() >= (double)block.getY() && location.getY() <= (double)(block.getY() + 1) && location.getZ() >= (double)block.getZ() && location.getZ() <= (double)(block.getZ() + 1);
    }

    public static void EntityScan(ElevatorsMoveTask MoveTask)
    {
        double curY = MoveTask.curY;
        if(MoveTask.selfElev.elType == 44)
            curY -= 0.5D;
        for(Iterator iterator = MoveTask.world().getEntities().iterator(); iterator.hasNext();)
        {
            Entity entity = (Entity)iterator.next();
            Location loc = entity.getLocation();
            int resloc[] = ResolvePlayerLocation(loc);
            if(IsInElevator(MoveTask, curY, resloc) && MoveTask.entities.indexOf(entity) < 0)
                MoveTask.entities.add(entity);
        }

    }

    private static int FindKeyword(String commands[], String keyword)
    {
        for(int i = 1; i < commands.length; i++)
            if(commands[i].equalsIgnoreCase(keyword))
                return i;

        return -1;
    }

    private static boolean IsKeyword(String word)
    {
        return word.equalsIgnoreCase("password") || word.equalsIgnoreCase("users") || word.equalsIgnoreCase("direction");
    }

    public static byte ExtractDirection(String commands[])
    {
        int index = FindKeyword(commands, "direction");
        if(index < 0 || commands.length <= index + 1)
            return 0;
        int index2 = Arrays.asList(directionStrings).indexOf(commands[index + 1].trim());
        if(index2 < 0)
            return 1;
        else
            return directionIDs[index2];
    }

    public static String GetDirectionList()
    {
        return GetList(directionStrings, ", ");
    }

    public static String GetList(String items[], String delimiter)
    {
        if(items.length == 0)
            return "";
        List list = Arrays.asList(items);
        String Result = (String)list.get(0);
        for(int i = 1; i < list.size(); i++)
            Result = (new StringBuilder(String.valueOf(Result))).append(delimiter).append((String)list.get(i)).toString();

        return Result;
    }

    public static String ExtractFloorname(String commands[])
    {
        String FloorName = "";
        for(int i = 1; i < commands.length; i++)
        {
            if(IsKeyword(commands[i]))
                break;
            FloorName = (new StringBuilder(String.valueOf(FloorName))).append(commands[i]).append(" ").toString();
        }

        return FloorName.trim();
    }

    public static String ExtractPassword(String commands[])
    {
        int index = FindKeyword(commands, "password");
        if(index < 0 || commands.length <= index + 1)
            return "";
        else
            return commands[index + 1].trim();
    }

    public static ArrayList ExtractUsers(String commands[])
    {
        int index = FindKeyword(commands, "users");
        if(index < 0)
            return null;
        ArrayList Result = new ArrayList();
        for(int i = index + 1; i < commands.length; i++)
        {
            if(IsKeyword(commands[i]))
                break;
            Result.add(commands[i].trim());
        }

        return Result;
    }

    public static String UserList(String commands[])
    {
        ArrayList users = ExtractUsers(commands);
        String Result = "";
        for(Iterator iterator = users.iterator(); iterator.hasNext();)
        {
            String user = (String)iterator.next();
            if(Result.equals(""))
                Result = user;
            else
                Result = (new StringBuilder(String.valueOf(Result))).append(", ").append(user).toString();
        }

        return Result;
    }

    public static void WarnPlayer(Player player, String message)
    {
        player.sendMessage((new StringBuilder()).append(ChatColor.RED).append(message).toString());
    }

    public static void CongratPlayer(Player player, String message)
    {
        player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append(message).toString());
    }

    public static void InfoPlayer(Player player, String message)
    {
        player.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append(message).toString());
    }

    public static byte StorageData(Block block)
    {
        if(block.getTypeId() == 77 || block.getTypeId() == 69)
            return (byte)(block.getData() & 7);
        else
            return block.getData();
    }

    public static String ProtectionMessage(boolean password, boolean users)
    {
        String msgadditions = "";
        if(password && users)
        {
            msgadditions = " with password and user restrictions ";
        } else
        {
            if(password)
                msgadditions = " with password protection ";
            if(users)
                msgadditions = " with user restrictions ";
        }
        return msgadditions;
    }

    public static boolean IsAllowed(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, Player player)
    {
        boolean elevall = false;
        boolean elevrest = false;
        if(Elev.users == null)
        {
            elevall = true;
        } else
        {
            for(Iterator iterator = Elev.users.iterator(); iterator.hasNext();)
            {
                String user = (String)iterator.next();
                if(user.equalsIgnoreCase(player.getDisplayName()) || user.equalsIgnoreCase("*"))
                    elevall = true;
                if(user.equalsIgnoreCase((new StringBuilder("-")).append(player.getDisplayName()).toString()))
                    elevrest = true;
            }

        }
        if(!elevall || elevrest)
            return false;
        boolean floorall = false;
        boolean floorrest = false;
        if(CallBlock.users == null)
        {
            floorall = true;
        } else
        {
            for(Iterator iterator1 = CallBlock.users.iterator(); iterator1.hasNext();)
            {
                String user = (String)iterator1.next();
                if(user.equalsIgnoreCase(player.getDisplayName()) || user.equalsIgnoreCase("*"))
                    floorall = true;
                if(user.equalsIgnoreCase((new StringBuilder("-")).append(player.getDisplayName()).toString()))
                    floorrest = true;
            }

        }
        return floorall && !floorrest;
    }

    public static String LocToString(Location loc)
    {
        return (new StringBuilder("[Location X:")).append(loc.getX()).append(" Y:").append(loc.getY()).append(" Z:").append(loc.getZ()).append(" world:").append(loc.getWorld().getName()).append("]").toString();
    }

    public static boolean CheckDependence(BlockDependenceClass list, World world, int X, int Y, int Z, int X1, int X2, int nY, 
            int Z1, int Z2)
    {
        if(X < X2 || X > X1 || Z < Z2 || Z > Z1 || Y > 128)
            return false;
        int prevdependence = list.IsDependent(X, Y, Z);
        if(prevdependence > -1)
            return prevdependence == 2;
        int Result = 0;
        list.AddItem(X, Y, Z, 0);
        Block block = world.getBlockAt(X, Y, Z);
        if(!FluidBlock(block) || block.getTypeId() == 9 || block.getTypeId() == 11)
            if(Y == nY + 1)
            {
                Result = 2;
            } else
            {
                list.ChangeDependence(X, Y, Z, 1);
                if(CheckDependence(list, world, X, Y - 1, Z, X1, X2, nY, Z1, Z2))
                    Result = 2;
                else
                if(CheckDependence(list, world, X - 1, Y, Z, X1, X2, nY, Z1, Z2))
                    Result = 2;
                else
                if(CheckDependence(list, world, X + 1, Y, Z, X1, X2, nY, Z1, Z2))
                    Result = 2;
                else
                if(CheckDependence(list, world, X, Y, Z - 1, X1, X2, nY, Z1, Z2))
                    Result = 2;
                else
                if(CheckDependence(list, world, X, Y, Z + 1, X1, X2, nY, Z1, Z2))
                    Result = 2;
                else
                if(CheckDependence(list, world, X, Y + 1, Z, X1, X2, nY, Z1, Z2))
                    Result = 2;
            }
        list.ChangeDependence(X, Y, Z, Result);
        return Result == 2;
    }

    private static void AddBuildBlock(ArrayList list, Block block, int X2, int nY, int Z2)
    {
        ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 Result = new ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121(block.getX() - X2, block.getY() - nY, block.getZ() - Z2, block.getTypeId(), block.getData(), null, 0, 0, true);
        list.add(Result);
    }

    public static ArrayList ScanBuildBlocks(World world, int X1, int X2, int nY, int Z1, int Z2)
    {
        ArrayList Result = new ArrayList();
        BlockDependenceClass blocklist = new BlockDependenceClass();
        for(int iY = nY + 1; iY < 128; iY++)
        {
            boolean isActive = false;
            for(int iX = X2; iX <= X1; iX++)
            {
                for(int iZ = Z2; iZ <= Z1; iZ++)
                    if(CheckDependence(blocklist, world, iX, iY, iZ, X1, X2, nY, Z1, Z2))
                    {
                        isActive = true;
                        AddBuildBlock(Result, world.getBlockAt(iX, iY, iZ), X2, nY, Z2);
                    }

            }

            if(!isActive)
                break;
        }

        return Result;
    }

    public static boolean CheckGlassDoorClear(ElevatorsMoveTask MoveTask, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 CallBlock, Location callloc)
    {
        for(Iterator iterator = MoveTask.selfElev.GlassDoors.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 GlassBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(GlassBlock.targetID == CallBlock.selfID || GlassBlock.targetID == -1)
            {
                Block block = GetBlock(MoveTask, GlassBlock);
                int type = block.getTypeId();
                if(type != 0 && type != 20)
                    return false;
                for(Iterator iterator1 = MoveTask.world().getEntities().iterator(); iterator1.hasNext();)
                {
                    Entity entity = (Entity)iterator1.next();
                    if(InsideBlock(entity.getLocation(), block))
                        return false;
                }

            }
        }

        return true;
    }

    public static Item DropBlock(Location loc, int typeID, byte data)
    {
        int loot = 0;
        switch(typeID)
        {
        case 50: // '2'
            loot = 50;
            break;

        case 75: // 'K'
            loot = 76;
            break;

        case 76: // 'L'
            loot = 76;
            break;

        case 65: // 'A'
            loot = 65;
            break;

        case 68: // 'D'
            loot = 323;
            break;

        case 69: // 'E'
            loot = 69;
            break;

        case 77: // 'M'
            loot = 77;
            break;

        case 59: // ';'
            if(data == 7)
                loot = 296;
            break;
        }
        if(loot != 0)
            return loc.getWorld().dropItemNaturally(loc, new ItemStack(loot, 1));
        else
            return null;
    }

    public static boolean BlockPlaceable(int typeID, byte data, Block block)
    {
        int modX = 0;
        int modZ = 0;
        int X = block.getX();
        int Y = block.getY();
        int Z = block.getZ();
        if(typeID == 59 && block.getWorld().getBlockTypeIdAt(X, Y + 1, Z) != 0)
            return false;
        switch(typeID)
        {
        case 50: // '2'
        case 75: // 'K'
        case 76: // 'L'
            switch(data)
            {
            case 1: // '\001'
                modX = -1;
                break;

            case 2: // '\002'
                modX = 1;
                break;

            case 3: // '\003'
                modZ = -1;
                break;

            case 4: // '\004'
                modZ = 1;
                break;
            }
            break;

        case 65: // 'A'
        case 68: // 'D'
            switch(data)
            {
            case 2: // '\002'
                modZ = 1;
                break;

            case 3: // '\003'
                modZ = -1;
                break;

            case 4: // '\004'
                modX = 1;
                break;

            case 5: // '\005'
                modX = -1;
                break;
            }
            break;

        case 69: // 'E'
        case 77: // 'M'
            switch(data & 7)
            {
            case 1: // '\001'
                modX = -1;
                break;

            case 2: // '\002'
                modX = 1;
                break;

            case 3: // '\003'
                modZ = -1;
                break;

            case 4: // '\004'
                modZ = 1;
                break;
            }
            break;
        }
        if(modX == 0 && modZ == 0)
            return true;
        return PersistentBlock(block.getWorld().getBlockAt(X + modX, Y, Z + modZ));
    }

    public static boolean IsMovingPart(ElevatorsMoveTask MoveTask, Block block)
    {
        if(MoveTask.ThreadRunning < 0)
            return false;
        if(block.getX() > MoveTask.selfElev.elX1 || block.getX() < MoveTask.selfElev.elX2 || block.getZ() > MoveTask.selfElev.elZ1 || block.getZ() < MoveTask.selfElev.elZ2)
            return false;
        int relpos[] = new int[3];
        if(!RelativePosition(MoveTask, block.getLocation(), relpos))
            return false;
        if(relpos[1] == 0)
            return true;
        for(Iterator iterator = MoveTask.BuildBlocks.iterator(); iterator.hasNext();)
        {
            ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 BuildBlock = (ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)iterator.next();
            if(BuildBlock.BlockX == relpos[0] && BuildBlock.BlockY == relpos[1] && BuildBlock.BlockZ == relpos[2])
                return true;
        }

        return false;
    }

    private static void AdjacentRailScan(ElevatorsMoveTask MoveTask)
    {
        for(int iX = MoveTask.selfElev.elX2 - 1; iX <= MoveTask.selfElev.elX1 + 1; iX++)
        {
            for(int iZ = MoveTask.selfElev.elZ2 - 1; iZ <= MoveTask.selfElev.elZ1 + 1; iZ++)
                if(iX < MoveTask.selfElev.elX2 || iX > MoveTask.selfElev.elX1 || iZ < MoveTask.selfElev.elZ2 || iZ > MoveTask.selfElev.elZ1)
                {
                    Block block = MoveTask.world().getBlockAt(iX, MoveTask.curY, iZ);
                    if(block.getTypeId() == 66 || block.getTypeId() == 27 || block.getTypeId() == 28)
                    {
                        MoveTask.AdjacentRails.add(new ElevatorsMoveTask.AdjacentRail(block));
                        block.setTypeId(0);
                    }
                }

        }

    }

    private static void AdjacentRailSet(ElevatorsMoveTask MoveTask)
    {
        ElevatorsMoveTask.AdjacentRail rail;
        for(Iterator iterator = MoveTask.AdjacentRails.iterator(); iterator.hasNext(); rail.rail.setTypeId(rail.type))
            rail = (ElevatorsMoveTask.AdjacentRail)iterator.next();

        MoveTask.AdjacentRails.clear();
    }

    public static void AdjacentRailUpdate(ElevatorsMoveTask MoveTask, boolean get)
    {
        if(get)
        {
            MoveTask.AdjacentRails.clear();
            AdjacentRailScan(MoveTask);
        } else
        {
            AdjacentRailSet(MoveTask);
            AdjacentRailScan(MoveTask);
            AdjacentRailSet(MoveTask);
        }
    }

    private static final String directionStrings[] = {
        "up", "down", "right", "left", "splitH", "splitV"
    };
    private static final byte directionIDs[] = {
        4, 5, 8, 7, 6, 9
    };

}