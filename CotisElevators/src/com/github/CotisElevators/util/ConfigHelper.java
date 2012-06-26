
package com.github.CotisElevators.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.gmail.creathir.util:
//            Configuration

public class ConfigHelper
{

    public ConfigHelper()
    {
    }

    public static Configuration LoadConfig(String filename)
    {
        Configuration Result = new Configuration();
        Configuration.Setting lastset = null;
        Result.file = filename;
        try
        {
            BufferedReader input = new BufferedReader(new FileReader(filename));
            String line;
            while((line = input.readLine()) != null) 
                if(line.length() >= 2)
                {
                    String rawline = line.substring(1).trim();
                    String starter = line.substring(0, 1);
                    if(starter.equals("!"))
                        Result.header = rawline;
                    else
                    if(starter.equals("*"))
                    {
                        lastset = new Configuration.Setting(Result.Settings.size());
                        lastset.name = rawline.split("=", 2)[0].trim();
                        lastset.value = rawline.split("=", 2)[1].trim();
                    } else
                    if(starter.equals("#") && lastset != null)
                        lastset.description.add(rawline);
                }
            input.close();
        }
        catch(IOException ioexception) { }
        return Result;
    }

    public static transient void UpdateSetting(Configuration config, String name, String value, int type, int index, String comments[])
    {
        Configuration.Setting setting = GetSetting(config, name);
        boolean reset = false;
        if(setting == null)
        {
            setting = new Configuration.Setting(config, index);
            reset = true;
        } else
        {
            setting.index = index;
            setting.description.clear();
            try
            {
                switch(type)
                {
                case 1: // '\001'
                    GetInteger(config, name);
                    break;

                case 2: // '\002'
                    GetFloat(config, name);
                    break;

                case 3: // '\003'
                    GetBoolean(config, name);
                    break;
                }
            }
            catch(Exception e)
            {
                reset = true;
            }
        }
        setting.name = name;
        String as[];
        int j = (as = comments).length;
        for(int i = 0; i < j; i++)
        {
            String comment = as[i];
            setting.description.add(comment);
        }

        if(reset)
            setting.value = value;
    }

    private static Configuration.Setting GetSetting(Configuration config, String name)
    {
        for(Iterator iterator = config.Settings.iterator(); iterator.hasNext();)
        {
            Configuration.Setting setting = (Configuration.Setting)iterator.next();
            if(setting.name.equalsIgnoreCase(name))
                return setting;
        }

        return null;
    }

    public static String GetString(Configuration config, String settingname)
    {
        return GetSetting(config, settingname).value;
    }

    public static int GetInteger(Configuration config, String settingname)
    {
        return Integer.parseInt(GetString(config, settingname));
    }

    public static float GetFloat(Configuration config, String settingname)
    {
        return Float.parseFloat(GetString(config, settingname));
    }

    public static boolean GetBoolean(Configuration config, String settingname)
    {
        return Boolean.parseBoolean(GetString(config, settingname));
    }

    public static boolean WriteConfig(Configuration config, String header)
    {
        ArrayList order = new ArrayList();
        int lastindex = 0;
        int currentindex = -1;
        int newindex = -1;
        config.header = header;
        try
        {
            File fOutput = new File(config.file);
            File fdir = fOutput.getParentFile();
            if(!fdir.exists())
                fdir.mkdir();
            if(!fOutput.exists())
                fOutput.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(fOutput));
            output.write((new StringBuilder("! ")).append(header).toString());
            output.newLine();
            output.newLine();
            output.newLine();
            for(int i = 0; i < config.Settings.size(); i++)
            {
                for(int i2 = 0; i2 < config.Settings.size(); i2++)
                    if(((Configuration.Setting)config.Settings.get(i2)).index >= lastindex && (((Configuration.Setting)config.Settings.get(i2)).index < currentindex || newindex == -1) && order.indexOf(Integer.valueOf(i2)) == -1)
                    {
                        currentindex = ((Configuration.Setting)config.Settings.get(i2)).index;
                        newindex = i2;
                    }

                if(newindex != -1)
                {
                    lastindex = currentindex;
                    order.add(Integer.valueOf(newindex));
                    currentindex = -1;
                    newindex = -1;
                }
            }

            for(Iterator iterator = order.iterator(); iterator.hasNext(); output.newLine())
            {
                Integer i3 = (Integer)iterator.next();
                Configuration.Setting setting = (Configuration.Setting)config.Settings.get(i3.intValue());
                output.write((new StringBuilder("* ")).append(setting.name).append(" = ").append(setting.value).toString());
                output.newLine();
                output.newLine();
                for(Iterator iterator1 = setting.description.iterator(); iterator1.hasNext(); output.newLine())
                {
                    String comment = (String)iterator1.next();
                    output.write((new StringBuilder("# ")).append(comment).toString());
                }

                output.newLine();
            }

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

    public static final int CSTRING = 0;
    public static final int CINTEGER = 1;
    public static final int CFLOAT = 2;
    public static final int CBOOLEAN = 3;
}