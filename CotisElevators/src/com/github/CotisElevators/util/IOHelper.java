
package com.github.CotisElevators.util;

import java.io.*;
import java.util.*;

// Referenced classes of package com.gmail.creathir.util:
//            IOSection

public class IOHelper
{

    public IOHelper()
    {
    }

    public static IOSection NewMaster()
    {
        Properties properties = new Properties();
        IOSection Master = new IOSection(properties, "Master");
        return Master;
    }

    private static String PropertyIdentifier(IOSection Section, String ParamName)
    {
        return (new StringBuilder("[")).append(Section.SectionIdentifier).append("-").append(ParamName).append("]").toString();
    }

    private static String SectionIdentifier(IOSection Section, int ID)
    {
        return (new StringBuilder(String.valueOf(Section.SectionIdentifier))).append("|S").append(ID).toString();
    }

    public static IOSection OpenMaster(String Filename)
    {
        IOSection Master = NewMaster();
        try
        {
            FileInputStream fs = new FileInputStream(Filename);
            Master.source.load(fs);
            fs.close();
        }
        catch(FileNotFoundException fnfe)
        {
            return null;
        }
        catch(IOException ioe)
        {
            return null;
        }
        return Master;
    }

    public static int GetSubSectionCount(IOSection Section)
    {
        String SecCountStr = Section.source.getProperty(PropertyIdentifier(Section, "SubSections"), "0");
        int SecCount;
        try
        {
            SecCount = Integer.parseInt(SecCountStr);
        }
        catch(Exception ie)
        {
            SecCount = 0;
        }
        return SecCount;
    }

    public static ArrayList GetSubSections(IOSection Section)
    {
        int SecCount = GetSubSectionCount(Section);
        ArrayList Result = new ArrayList();
        for(int i = 0; i < SecCount; i++)
            Result.add(new IOSection(Section.source, SectionIdentifier(Section, i)));

        return Result;
    }

    public static String GetSectionName(IOSection Section)
    {
        return Section.source.getProperty(PropertyIdentifier(Section, "SectionName"), "");
    }

    public static IOSection GetSubSection(IOSection Section, String SubSectionName)
    {
        for(Iterator iterator = GetSubSections(Section).iterator(); iterator.hasNext();)
        {
            IOSection SubSec = (IOSection)iterator.next();
            String CurSubSecName = GetSectionName(SubSec);
            if(CurSubSecName.equals(SubSectionName))
                return SubSec;
        }

        return null;
    }

    public static String ReadString(IOSection Section, String PropertyName, String DefaultValue)
    {
        String Result = Section.source.getProperty(PropertyIdentifier(Section, PropertyName), DefaultValue);
        if(Result.equals("[null]"))
            Result = null;
        return Result;
    }

    public static String ReadString(IOSection Section, String PropertyName)
    {
        return ReadString(Section, PropertyName, "");
    }

    public static int ReadInteger(IOSection Section, String PropertyName, int DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
        int Result;
        try
        {
            Result = Integer.parseInt(ResultStr);
        }
        catch(Exception ie)
        {
            Result = DefaultValue;
        }
        return Result;
    }

    public static int ReadInteger(IOSection Section, String PropertyName)
    {
        return ReadInteger(Section, PropertyName, 0);
    }

    public static byte ReadByte(IOSection Section, String PropertyName, byte DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
        byte Result;
        try
        {
            Result = Byte.parseByte(ResultStr);
        }
        catch(Exception be)
        {
            Result = DefaultValue;
        }
        return Result;
    }

    public static byte ReadByte(IOSection Section, String PropertyName)
    {
        return ReadByte(Section, PropertyName, (byte)0);
    }

    public static UUID ReadUUID(IOSection Section, String PropertyName, UUID DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
    	UUID Result;
        try
        {
            Result = UUID.fromString(ResultStr);
        }
        catch(Exception ie)
        {
            Result = DefaultValue;
        }
    	return Result;
    }
    
    public static UUID ReadUUID(IOSection Section, String PropertyName)
    {
        return ReadUUID(Section, PropertyName, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }
    
    public static long ReadLong(IOSection Section, String PropertyName, long DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
        long Result;
        try
        {
            Result = Long.parseLong(ResultStr);
        }
        catch(Exception ie)
        {
            Result = DefaultValue;
        }
        return Result;
    }

    public static long ReadLong(IOSection Section, String PropertyName)
    {
        return ReadLong(Section, PropertyName, 0L);
    }

    public static boolean ReadBoolean(IOSection Section, String PropertyName, boolean DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
        boolean Result;
        try
        {
            Result = Boolean.parseBoolean(ResultStr);
        }
        catch(Exception ie)
        {
            Result = DefaultValue;
        }
        return Result;
    }

    public static boolean ReadBoolean(IOSection Section, String PropertyName)
    {
        return ReadBoolean(Section, PropertyName, false);
    }

    public static double ReadDouble(IOSection Section, String PropertyName, double DefaultValue)
    {
        String ResultStr = ReadString(Section, PropertyName);
        if(ResultStr.equals(""))
            return DefaultValue;
        double Result;
        try
        {
            Result = Double.parseDouble(ResultStr);
        }
        catch(Exception ie)
        {
            Result = DefaultValue;
        }
        return Result;
    }

    public static double ReadDouble(IOSection Section, String PropertyName)
    {
        return ReadDouble(Section, PropertyName, 0.0D);
    }

    public static ArrayList ReadStringList(IOSection Section, String PropertyName, ArrayList DefaultValues)
    {
        IOSection subsection = GetSubSection(Section, PropertyName);
        if(subsection == null)
            return DefaultValues;
        int type = ReadInteger(subsection, "stringlistinit", 0);
        if(type == 0)
            return DefaultValues;
        if(type == 1)
            return null;
        int size = ReadInteger(subsection, "stringlistsize", 0);
        ArrayList Result = new ArrayList();
        for(int i = 0; i < size; i++)
        {
            String ResultStr = ReadString(subsection, (new Integer(i)).toString());
            Result.add(ResultStr);
        }

        return Result;
    }

    private static int ChangeSubSectionCount(IOSection Section, int IncValue)
    {
        int SecCount = GetSubSectionCount(Section);
        SecCount += IncValue;
        Section.source.setProperty(PropertyIdentifier(Section, "SubSections"), (new StringBuilder()).append(SecCount).toString());
        return SecCount - 1;
    }

    private static void WriteSectionName(IOSection Section, String SectionName)
    {
        Section.source.setProperty(PropertyIdentifier(Section, "SectionName"), SectionName);
    }

    public static IOSection WriteSection(IOSection Section, String SectionName)
    {
        int ID = ChangeSubSectionCount(Section, 1);
        IOSection Result = new IOSection(Section.source, SectionIdentifier(Section, ID));
        WriteSectionName(Result, SectionName);
        return Result;
    }

    public static void WriteString(IOSection Section, String PropertyName, String Value)
    {
        if(Value == null)
            Value = "[null]";
        Section.source.setProperty(PropertyIdentifier(Section, PropertyName), Value);
    }

    public static void WriteInteger(IOSection Section, String PropertyName, int Value)
    {
        WriteString(Section, PropertyName, Integer.toString(Value));
    }

    public static void WriteByte(IOSection Section, String PropertyName, byte Value)
    {
        WriteString(Section, PropertyName, Byte.toString(Value));
    }

    public static void WriteLong(IOSection Section, String PropertyName, long Value)
    {
        WriteString(Section, PropertyName, Long.toString(Value));
    }

    public static void WriteBoolean(IOSection Section, String PropertyName, boolean Value)
    {
        WriteString(Section, PropertyName, Boolean.toString(Value));
    }

    public static void WriteDouble(IOSection Section, String PropertyName, double Value)
    {
        WriteString(Section, PropertyName, Double.toString(Value));
    }

    public static void WriteStringList(IOSection Section, String PropertyName, ArrayList Values)
    {
        IOSection subsection = WriteSection(Section, PropertyName);
        if(Values == null)
        {
            WriteInteger(subsection, "stringlistinit", 1);
            return;
        }
        WriteInteger(subsection, "stringlistinit", 2);
        WriteInteger(subsection, "stringlistsize", Values.size());
        for(int i = 0; i < Values.size(); i++)
            WriteString(subsection, (new Integer(i)).toString(), (String)Values.get(i));

    }

    public static boolean SaveMaster(IOSection Master, String Filename, String MasterName)
    {
        try
        {
            File Output1 = new File(Filename);
            File Backup = null;
            if(Output1.exists())
            {
                Backup = new File((new StringBuilder(String.valueOf(Output1.getAbsolutePath()))).append(".bak").toString());
                Output1.renameTo(Backup);
            }
            File Output2 = new File(Filename);
            Output2.getParentFile().mkdirs();
            Output2.createNewFile();
            FileOutputStream fos = new FileOutputStream(Output2);
            Master.source.store(fos, MasterName);
            fos.flush();
            fos.close();
            if(Backup != null)
                Backup.delete();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}