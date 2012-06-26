
package com.github.CotisElevators.Elevators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class ElevatorsStoreFormat121
    implements Serializable
{
    public static class ElevatorsStoreFormatBlock121
        implements Serializable
    {

        public String toString()
        {
            return (new StringBuilder("[ElevatorsStoreFormatBlock121 ID:")).append(selfID).append(" X:").append(BlockX).append(" Y:").append(BlockY).append(" Z:").append(BlockZ).append(" elevator block type:").append(BlockType).append(" is relative:").append(Relative).append(" data:").append(BlockData).append(" targetID:").append(targetID).append("]").toString();
        }

        private static final long serialVersionUID = 0x566e3a72f0548b45L;
        public int selfID;
        public int BlockX;
        public int BlockY;
        public int BlockZ;
        public int BlockType;
        public String Parameter;
        public int targetID;
        public boolean Relative;
        public byte BlockData;
        public String password;
        public ArrayList users;

        public ElevatorsStoreFormatBlock121(int X, int Y, int Z, int Type, byte Data, String Param, int sID, 
                int tID, boolean rel)
        {
            selfID = sID;
            BlockX = X;
            BlockY = Y;
            BlockZ = Z;
            BlockType = Type;
            Parameter = Param;
            targetID = tID;
            Relative = rel;
            BlockData = Data;
            password = "";
            users = null;
        }
    }

    public class ElevatorsStoreFormatElevator121
        implements Serializable
    {

        private static final long serialVersionUID = 0xd073a814e381999L;
        public int selfID;
        public int elType;
        public byte elData;
        public int elX1;
        public int elX2;
        public int elY;
        public int elZ1;
        public int elZ2;
        public UUID elWorldOld;
        public String elWorld;
        public ArrayList SpecialBlocks;
        public ArrayList BuildBlocks;
        public ArrayList GlassDoors;
        public int nextblockID;
        public int nextbuildID;
        public int nextglassID;
        public int StoreVersion;
        public String owner;
        public String password;
        public ArrayList users;
        public boolean locked;
        final ElevatorsStoreFormat121 this$0;

        public ElevatorsStoreFormatElevator121(int X1, int X2, int Y, int Z1, int Z2, int type, 
                byte Data, String world, int ID, int Version, String owner)
        {
            super();
            this$0 = ElevatorsStoreFormat121.this;
            selfID = ID;
            elType = type;
            elData = Data;
            elX1 = X1;
            elX2 = X2;
            elY = Y;
            elZ1 = Z1;
            elZ2 = Z2;
            elWorld = world;
            elWorldOld = UUID.fromString("00000000-0000-0000-0000-000000000000");
            SpecialBlocks = new ArrayList();
            BuildBlocks = null;
            GlassDoors = new ArrayList();
            nextblockID = 0;
            nextbuildID = 0;
            nextglassID = 0;
            StoreVersion = Version;
            this.owner = owner;
            password = "";
            users = null;
            locked = false;
        }
    }


    public ElevatorsStoreFormat121()
    {
        Database = new ArrayList();
        nextelID = 0;
    }

    private static final long serialVersionUID = 0x26520d24077108afL;
    public ArrayList Database;
    public int nextelID;
    public int version;
}