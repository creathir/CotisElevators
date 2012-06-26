
package com.github.CotisElevators.util;

import java.util.ArrayList;
import java.util.Iterator;

public class RemoveList extends ArrayList
{

    public RemoveList(ArrayList targetArray)
    {
        this.targetArray = targetArray;
    }

    public void Execute()
    {
        for(Iterator iterator = iterator(); iterator.hasNext();)
        {
            Object item = (Object)iterator.next();
            if(targetArray.indexOf(item) >= 0)
                targetArray.remove(item);
        }

        clear();
        targetArray = null;
    }

    private static final long serialVersionUID = 0xc1f7ad5a088f35a5L;
    private ArrayList targetArray;
}