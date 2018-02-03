/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.storage;

import com.mongodb.DB;
import com.mongodb.client.model.Filters;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.unict.ing.pds.dhtdb.replica.p2p.Storage;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class MongoDBStorage implements Storage {

    DBConnectionSingletonSessionBeanLocal dbSessionBean = lookupDBConnectionSingletonSessionBeanLocal();
    
    private final DB db;
    private final MongoCollection collection;
    
    public MongoDBStorage() {
        // Using a single connection to provide better (query-oriented) scalability
        this.db = dbSessionBean.getDatabase();
        Jongo jongo = new Jongo(db);
        this.collection = jongo.getCollection("myMonitor");
    }
    
    @Override
    public void insert(GenericValue elem) {
        collection.insert(elem);
    }
    
    @Override
    public void insertMany(List<GenericValue> elems) {
        collection.insert(elems.toArray());
    }
    
    @Override
    public void remove(String key) {
        String query = "{ key: '"+ key + "' } }";
        removeBy(query);
    }
    
    private void removeBy(String query) {
        collection.remove(query);
    }
    
    private List<GenericValue> findBy(String query) {
        MongoCursor<GenericValue> iterDoc;
        List<GenericValue> ret = new LinkedList();
        if (query == null) {
            // Put HERE a default query
            iterDoc = collection.find().as(GenericValue.class);
        } else {
            iterDoc = collection.find(query).as(GenericValue.class);
        }

        iterDoc.forEach((GenericValue v) -> {            
            //try {
                ret.add(v);
                //ret.add(Class.forName("org.unict.ing.pds.dhtdb.utils.model." + v.getType()).asSubclass(GenericValue.class).cast(v));
            /*} catch (ClassNotFoundException ex) {
                Logger.getLogger(MongoDBStorage.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        });
        return ret;
    }
    @Override
    public List<GenericValue> find(String key) {
        // TODO Validation
        String query = "{ key: '"+ key + "' } }";
        return findBy(query);
    }
    

    
    @Override
    public List<GenericValue> lessThanAndRemove(String primaryKey) {
        String query = "{ key: { $lte: '"+ primaryKey + "' } }";
        List<GenericValue> ret = findBy(query);
        remove(query);
        
        return ret;
    }

    @Override
    public void update(GenericValue elem, String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
