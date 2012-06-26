
package com.github.CotisElevators.util;

import java.io.IOException;
import java.util.logging.*;

// Referenced classes of package com.gmail.creathir.util:
//            CompactFormatter

public class FileLogger extends Logger
{

    public FileLogger(String name, String file, Logger parent)
        throws SecurityException, IOException
    {
        super(name, null);
        setParent(parent);
        fh = new FileHandler(file, true);
        addHandler(fh);
        fh.setLevel(Level.ALL);
        setLevel(Level.ALL);
        fh.setFormatter(new CompactFormatter());
    }

    protected void finalize()
        throws Throwable
    {
        Close();
        super.finalize();
    }

    public void Close()
    {
        fh.flush();
        fh.close();
    }

    private FileHandler fh;
}