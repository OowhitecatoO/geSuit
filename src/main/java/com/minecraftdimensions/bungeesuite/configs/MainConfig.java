package com.minecraftdimensions.bungeesuite.configs;

import com.minecraftdimensions.bungeesuite.configlibrary.Config;

import java.io.File;

public class MainConfig {
    public static String configpath = File.separator + "plugins" + File.separator + "BungeeSuite" + File.separator + "config.yml";
    public static Config config = new Config( configpath );

    /* MySQL Information */
    public static String host = config.getString( "Database.Host", "localhost" );
    public static String database = config.getString( "Database.Database", "minecraft" );
    public static String port = config.getString( "Database.Port", "3306" );
    public static String username = config.getString( "Database.Username", "username" );
    public static String password = config.getString( "Database.Password", "password" );
    public static int threads = config.getInt( "Database.Threads", 5 );
    public static boolean motd = config.getBoolean( "MOTD.Enabled", true );
    public static boolean newPlayerBroadcast = config.getBoolean( "NewPlayerBroadcast", true );
    public static int playerDisconnectDelay = config.getInt( "PlayerDisconnectDelay", 10 );
    public static boolean UserSocketPort = config.getBoolean( "UseSocketPort", true );
    public static int BackupSocketPort = config.getInt( "BackupSocketPort", 14455 );
}
