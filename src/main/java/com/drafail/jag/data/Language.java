/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.data;

import java.util.Locale;

/**
 * Class used to hold the global variables for the language
 * @author Daniel
 */
public class Language {
    private Language(){}
    
    private static Locale currentLocale = new Locale("en", "CA");

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale currentLocale) {
        Language.currentLocale = currentLocale;
    }
}
