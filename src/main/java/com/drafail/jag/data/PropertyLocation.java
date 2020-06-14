/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;


/**
 *
 * @author 1633028
 */
public class PropertyLocation {
    private PropertyLocation(){}
    
    private static String path = "./";
    private static String fileName = "configuration.properties";

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        PropertyLocation.path = path;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        PropertyLocation.fileName = fileName;
    }
}