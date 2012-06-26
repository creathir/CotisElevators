package com.github.CotisElevators.util;

import java.util.ArrayList;

public class Configuration
{
    public class Setting
    {

        public String name;
        public String value;
        public ArrayList description;
        public int index;
        final Configuration this$0;

        public Setting(int index)
        {
            super();
            this$0 = Configuration.this;
            description = new ArrayList();
            Settings.add(this);
            this.index = index;
        }
    }


    public Configuration()
    {
        Settings = new ArrayList();
    }

    public String header;
    public ArrayList Settings;
    public String file;
}