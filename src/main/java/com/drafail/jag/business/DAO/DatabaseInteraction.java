/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drafail.jag.business.DAO;

import com.drafail.jag.data.DatabaseBean;

/**
 *
 * @author 1633028
 */
public abstract class DatabaseInteraction {

    private final DatabaseBean data;

    /**
     * Constructor used to create an item which will instantiate a connection
     * @param connection string to connect to the database
     * @param id id of the user
     * @param pass password of the user
     * @throws IllegalArgumentException 
     */
    public DatabaseInteraction(String connection, String id, String pass) throws IllegalArgumentException {
        data = new DatabaseBean(connection, id, pass);

    }

    /**
     * Method to return the item which creates the connection
     * @return data
     */
    public DatabaseBean returnData() {
        return data;
    }
}
