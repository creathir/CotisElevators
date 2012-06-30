
package com.github.CotisElevators.Elevators;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;
import org.bukkit.entity.Entity;

// Referenced classes of package com.github.CotisElevators.Elevators:
//            ElevatorsStoreFormat121

public class Packet240Elevators extends Packet
{

    public Packet240Elevators()
    {
        try
        {
        	
        	
        	Class packetClass;
			try {
				packetClass = Class.forName("net.minecraft.server.Packet");                
	            Field b = packetClass.getDeclaredField("b");
	            b.setAccessible(true);
	            Object fo = b.get(this);
	            //Map fv = (Map)fo;
	            //fv.put(getClass(), Integer.valueOf(240));
	            //b.set(this, fv);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e2)
        {
            e2.printStackTrace();
        }
    }

    public void Init(int distance, int curY, ArrayList bsource, ArrayList esource, ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev)
    {
        this.distance = distance;
        pType = 1;
        pSize = 36 + bsource.size() * 3 * 4;
        X1 = Elev.elX1;
        X2 = Elev.elX2;
        Y = curY;
        Z1 = Elev.elZ1;
        Z2 = Elev.elZ2;
        blocks = new int[bsource.size()][3];
        for(int i = 0; i < bsource.size(); i++)
        {
            blocks[i][0] = ((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)bsource.get(i)).BlockX;
            blocks[i][1] = ((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)bsource.get(i)).BlockY;
            blocks[i][2] = ((ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121)bsource.get(i)).BlockZ;
        }

        entities = new int[esource.size()];
        for(int i = 0; i < esource.size(); i++)
            entities[i] = ((Entity)esource.get(i)).getEntityId();

    }

    public void Stop(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int curY)
    {
        pType = 2;
        pSize = 28;
        X1 = Elev.elX1;
        X2 = Elev.elX2;
        Z1 = Elev.elZ1;
        Z2 = Elev.elZ2;
        Y = curY;
    }

    public void RemoveBlock(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, ElevatorsStoreFormat121.ElevatorsStoreFormatBlock121 block)
    {
        pType = 3;
        pSize = 36;
        X1 = Elev.elX1;
        X2 = Elev.elX2;
        Z1 = Elev.elZ1;
        Z2 = Elev.elZ2;
        blocks = new int[1][3];
        blocks[0][0] = block.BlockX;
        blocks[0][1] = block.BlockY;
        blocks[0][2] = block.BlockZ;
    }

    public void AddEntity(ElevatorsStoreFormat121.ElevatorsStoreFormatElevator121 Elev, int ID)
    {
        pType = 4;
        pSize = 28;
        X1 = Elev.elX1;
        X2 = Elev.elX2;
        Z1 = Elev.elZ1;
        Z2 = Elev.elZ2;
        entities = new int[1];
        entities[0] = ID;
    }

    public int a()
    {
        return pSize;
    }

    public void a(DataInputStream datainputstream)
    {
    }

    public void a(DataOutputStream arg0)
    {
        try
        {
            arg0.writeInt(pSize);
            arg0.writeInt(pType);
            switch(pType)
            {
            default:
                break;

            case 1: // '\001'
                arg0.writeInt(distance);
                arg0.writeInt(X1);
                arg0.writeInt(X2);
                arg0.writeInt(Y);
                arg0.writeInt(Z1);
                arg0.writeInt(Z2);
                arg0.writeInt(blocks.length);
                for(int i = 0; i < blocks.length; i++)
                {
                    for(int j = 0; j < 3; j++)
                        arg0.writeInt(blocks[i][j]);

                }

                arg0.writeInt(entities.length);
                for(int i = 0; i < entities.length; i++)
                    arg0.writeInt(entities[i]);

                break;

            case 2: // '\002'
                arg0.writeInt(X1);
                arg0.writeInt(X2);
                arg0.writeInt(Y);
                arg0.writeInt(Z1);
                arg0.writeInt(Z2);
                break;

            case 3: // '\003'
                arg0.writeInt(X1);
                arg0.writeInt(X2);
                arg0.writeInt(Z1);
                arg0.writeInt(Z2);
                arg0.writeInt(blocks[0][0]);
                arg0.writeInt(blocks[0][1]);
                arg0.writeInt(blocks[0][2]);
                break;

            case 4: // '\004'
                arg0.writeInt(X1);
                arg0.writeInt(X2);
                arg0.writeInt(Z1);
                arg0.writeInt(Z2);
                arg0.writeInt(entities[0]);
                break;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handle(NetHandler nethandler)
    {
    }

    public static final int TYPE_INIT = 1;
    public static final int TYPE_STOP = 2;
    public static final int TYPE_REMBLOCK = 3;
    public static final int TYPE_ADDENTITY = 4;
    private int pSize;
    public int pType;
    public int blocks[][];
    public int entities[];
    public int distance;
    public int X1;
    public int X2;
    public int Z1;
    public int Z2;
    public int Y;
	
}