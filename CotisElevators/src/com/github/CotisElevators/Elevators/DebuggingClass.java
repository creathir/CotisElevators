
package com.github.CotisElevators.Elevators;

import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators

public class DebuggingClass
    implements Runnable
{

    public void run()
    {
        if(last)
        {
            if(lastres)
                ((Block)blocks.get(0)).setTypeId(tmp);
            blocks.remove(0);
            types.remove(0);
        }
        if(blocks.size() > 0)
        {
            last = true;
            tmp = ((Block)blocks.get(0)).getTypeId();
            if(((Integer)types.get(0)).intValue() == 0)
            {
                ((Block)blocks.get(0)).setTypeId(41);
                lastres = true;
            } else
            {
                ((Block)blocks.get(0)).setTypeId(57);
                lastres = false;
            }
            schedule.scheduleSyncDelayedTask(plugin, this, 5L);
        } else
        {
            for(int i = 0; i < resets.size(); i++)
                ((Block)resets.get(i)).setTypeId(((Integer)resettypeIDs.get(i)).intValue());

        }
    }

    public DebuggingClass()
    {
        last = false;
        lastres = false;
        blocks = new ArrayList();
        resets = new ArrayList();
        resettypeIDs = new ArrayList();
        types = new ArrayList();
    }

    public void AddNormal(Block block)
    {
        blocks.add(block);
        types.add(Integer.valueOf(0));
    }

    public void AddPermanent(Block block)
    {
        blocks.add(block);
        types.add(Integer.valueOf(1));
        resets.add(block);
        resettypeIDs.add(Integer.valueOf(block.getTypeId()));
    }

    public ArrayList blocks;
    public ArrayList resets;
    public ArrayList resettypeIDs;
    public ArrayList types;
    private int tmp;
    private boolean last;
    private boolean lastres;
    public BukkitScheduler schedule;
    public Elevators plugin;
}