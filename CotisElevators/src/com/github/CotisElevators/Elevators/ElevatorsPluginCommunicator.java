
package com.github.CotisElevators.Elevators;

import org.bukkit.entity.Player;

// Referenced classes of package com.gmail.creathir.Elevators:
//            Elevators

public class ElevatorsPluginCommunicator
{
	public enum PermissionLevel {BUILDER, ADVANCED, OPERATOR};	    

    public ElevatorsPluginCommunicator(Elevators plugin)
    {
        PermissionsEnabled = false;
        PermissionSetup(plugin);
    }

    private void PermissionSetup(Elevators plugin)
    {
        try
        {
        	
        }
        catch(Exception e)
        {
            e.printStackTrace();
            plugin.OpWarning("Elevators - error while linking to Permissions!");
        }
    }

    public boolean Permission(Player player, PermissionLevel level)
    {
        if(PermissionsEnabled)
            switch(level)
            {
            case BUILDER: // '\001'
                if(player.hasPermission("elevators.builder"))
                    return true;
                // fall through

            case ADVANCED: // '\002'
                if(player.hasPermission("elevators.advancedbuilder"))
                    return true;
                // fall through

            case OPERATOR: // '\003'
                if(player.hasPermission("elevators.operator"))
                    return true;
                break;
            }
        else
        if(level == PermissionLevel.OPERATOR)
        {
            if(player.isOp())
                return true;
        } else
        {
            return true;
        }
        return false;
    }


    //private PermissionHandler Permissions;
    public boolean PermissionsEnabled;
}