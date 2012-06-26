
package com.github.CotisElevators.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.*;

public class CompactFormatter extends Formatter
{

    public CompactFormatter()
    {
    }

    public String format(LogRecord record)
    {
        date.setTime(record.getMillis());
        StringBuffer sb = new StringBuffer();
        sb.append(dateformater.format(date));
        sb.append(" ");
        sb.append((new StringBuilder("[")).append(record.getLevel().getName()).append("] ").toString());
        sb.append(record.getMessage());
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    private final Date date = new Date();
    private final DateFormat dateformater = DateFormat.getDateTimeInstance(3, 2);
}