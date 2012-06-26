
package com.github.CotisElevators.util;

import java.util.Properties;

public class IOSection
{

    public IOSection(Properties properties, String Identifier)
    {
        source = properties;
        SectionIdentifier = Identifier;
    }

    public Properties source;
    public String SectionIdentifier;
}