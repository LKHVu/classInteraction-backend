/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vgu.vgu;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final String configFile = "./config.properties";
    private static Config config = null;
    private String dbServer;
    private String dbPort;
    private String dbName;
    private String dbUserName;
    private String dbPassword;
    private String logFile;

    public String getDbServer() {
        return this.dbServer;
    }

    public String getDbPort() {
        return this.dbPort;
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getDbUserName() {
        return this.dbUserName;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public String getLogFile() {
        return this.logFile;
    }

    private Config() {
        FileInputStream file = null;
        try {
            Properties prop = new Properties();
            file = new FileInputStream("./config.properties");
            Config cf = null;
            prop.load(file);
            this.dbServer = prop.getProperty("dbServer");
            this.dbPort = prop.getProperty("dbPort");
            this.dbName = prop.getProperty("dbName");
            this.dbUserName = prop.getProperty("dbUserName");
            this.dbPassword = prop.getProperty("dbPassword");
            this.logFile = prop.getProperty("logFile");
            return;
        } catch (IOException e) {
            System.out.println("Failed to read config file.");
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }
}
