/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.storage;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.bson.Document;
import org.unict.ing.pds.dhtdb.replica.p2p.Storage;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class MongoDBStorage implements Storage {

    DBConnectionSingletonSessionBeanLocal dbSessionBean = lookupDBConnectionSingletonSessionBeanLocal();
    
    private final MongoDatabase db;
    private final MongoCollection<Document> collection;
    
    public MongoDBStorage(MongoDatabase db) {
        // Using a single connection to provide better (query-oriented) scalability
        this.db = dbSessionBean.getDatabase();
        this.collection = db.getCollection("myMonitor");
    }
    
    @Override
    public void insert(GenericStat elem) {
        
        Document document = new Document("stat", elem).append("topic", elem.getClass().getSimpleName());
        collection.insertOne(document);
    }

    @Override
    public void update(GenericStat elem, String primaryKey) {
        collection.updateOne(Filters.eq("_id", primaryKey), Updates.set("stat", elem));
    }

    @Override
    public void remove(String primaryKey) {
        collection.deleteOne(Filters.eq("_id", primaryKey));
    }

    @Override
    public List<GenericStat> find(String primaryKey) {
            FindIterable<Document> iterDoc;
            if (primaryKey == null) {
                iterDoc = collection.find();
            } else {
                iterDoc = collection.find(Filters.eq("_id", primaryKey));
            }
            List<GenericStat> ret = new ArrayList();
            
            iterDoc.forEach((Block<Document>)(Document t) -> {
                try {
                    Class<? extends GenericStat> topicClass = Class.forName("org.unict.ing.pds.dhtdb.utils.model." + t.get("topic", String.class)).asSubclass(GenericStat.class);
                    ret.add(t.get("elem", topicClass));
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MongoDBStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            return ret;
    }
 
    @Override
    public List<String> getTopics() {
        List<String> ret = new ArrayList();
        db.listCollectionNames().forEach((Block<String>)(String t) -> {
            ret.add(t);
        });
        return ret;
    }

    private DBConnectionSingletonSessionBeanLocal lookupDBConnectionSingletonSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DBConnectionSingletonSessionBeanLocal) c.lookup("java:global/replicamanager-ear-1.0-SNAPSHOT/replicamanager-ejb-1.0-SNAPSHOT/DBConnectionSingletonSessionBean!org.unict.ing.pds.dhtdb.replica.storage.DBConnectionSingletonSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
