/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import static java.nio.file.Files.*;
import java.nio.file.Path;
import static java.nio.file.Paths.get;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author 1633028
 */
public class PropertiesAccessor {

    private static Path txtFile;

    private PropertiesAccessor() {

    }

    /**
     * Reads the properties from a property file
     *
     * @param path the path to the file
     * @param keys the key we are looking for in the file
     * @param fileName the name of the file
     * @return string array with all the values read from file
     * @throws IOException
     */
    public static String[] readProperties(String path, String[] keys, String fileName) throws IOException {
        Properties props = new Properties();
        txtFile = get(path, fileName);
        if (Files.exists(txtFile)) {
            try (InputStream propFileStream = newInputStream(txtFile)) {
                props.load(propFileStream);
            }
            String[] returnString = new String[props.size()];
            for (int i = 0; i < keys.length; i++) {
                returnString[i] = props.getProperty(keys[i]);
            }
            return returnString;
        } else {
            throw new IOException("Property file missing containing configuration");
        }
    }

    /**
     * sets all the properties in the property file in the file
     *
     * @param path the path of the file
     * @param items the items we use to set the property file
     * @param fileName the name of the file
     * @throws IOException
     */
    public static void setProperties(String path, String[] items, String fileName) throws IOException {
        if (items == null || items.length == 0) {
            throw new IllegalArgumentException("Invalid items list in SetProperty method");
        }
        Properties props = new Properties();
        txtFile = get(path, fileName);
        if (!Files.exists(txtFile)) {
            try (OutputStream propFileStream = newOutputStream(txtFile, StandardOpenOption.CREATE)) {
                props.setProperty("name", items[0]);
                props.setProperty("email", items[1]);
                props.setProperty("pass", items[2]);
                props.setProperty("imap", items[3]);
                props.setProperty("smtp", items[4]);
                props.setProperty("imapPort", items[5]);
                props.setProperty("smtpPort", items[6]);
                props.setProperty("sqlURL", items[7]);
                props.setProperty("dbName", items[8]);
                props.setProperty("sqlPort", items[9]);
                props.setProperty("dbUser", items[10]);
                props.setProperty("dbPass", items[11]);
                props.store(propFileStream, fileName);
            }
        }
    }

    /**
     * Reads the language bundles
     *
     * @param currentLocale the current local language
     * @param msg the resources bundle we assign the currentLocale to
     * @param keys the key
     * @return the string array with all the words of the language
     */
    public static String[] readLanguage(Locale currentLocale, ResourceBundle msg, String[] keys) {
        msg = ResourceBundle.getBundle("MessagesBundle", currentLocale);
        String[] returnArray = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            returnArray[i] = msg.getString(keys[i]).replace("\\:", ":").replace("\\=", "=");
        }
        return returnArray;
    }

    /**
     * creates a property file
     *
     * @param path the path to the file
     * @param fileName name of the file
     * @return the property after it has been loaded
     * @throws IOException
     */
    public static Properties instantiateProperty(String path, String fileName) throws IOException {
        Properties p = new Properties();
        txtFile = get(path, fileName);
        if (Files.exists(txtFile)) {
            try (InputStream propFileStream = newInputStream(txtFile)) {
                p.load(propFileStream);
            }
        }
        return p;
    }
}
