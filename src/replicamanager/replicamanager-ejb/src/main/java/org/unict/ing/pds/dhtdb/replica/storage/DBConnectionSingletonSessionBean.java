/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import javax.ejb.Singleton;

/**
 *
 * @author aleskandro
 */
@Singleton
public class DBConnectionSingletonSessionBean implements DBConnectionSingletonSessionBeanLocal {

    private MongoClient     mongo;
    private MongoCredential credential;
    private MongoDatabase   database;
    
    @Override
    public MongoDatabase getDatabase () {
        if (database == null) {
            // Creating a Mongo client 
            mongo = new MongoClient("localhost", 27017); 
            // Creating Credentials (not needed?)
            // User, Db, Password
            //credential = MongoCredential.createCredential("", "", "".toCharArray()); 
            System.out.println("Connected to the database successfully");  
            // Accessing the database 
            database = mongo.getDatabase("myDb"); 
        }
        return database;
    }
}
