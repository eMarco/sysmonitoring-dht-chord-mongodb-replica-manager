/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.storage;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.replica.p2p.Storage;

/**
 * A singleton that is responsible for a single connection to the local mongo database
 * @author aleskandro
 */
@Singleton
public class DBConnectionSingletonSessionBean implements DBConnectionSingletonSessionBeanLocal {

    private MongoClient     mongo;
    private MongoCredential credential;
    private DB   database;

    private Storage storage;
    
    @Override
    public DB getDatabase () {
        if (database == null) {
            // Creating a Mongo client
            mongo = new MongoClient("localhost", 27017);
            // Creating Credentials (not needed?)
            // User, Db, Password
            //credential = MongoCredential.createCredential("", "", "".toCharArray());
            System.out.println("Connected to the database successfully");
            // Accessing the database
            database = mongo.getDB("myDb");
            storage = new MongoDBStorage(database);
                    
        }
        return database;
    }
    
    @Override
    public Storage getStorage() {
        getDatabase();
        return storage;
    }
}
