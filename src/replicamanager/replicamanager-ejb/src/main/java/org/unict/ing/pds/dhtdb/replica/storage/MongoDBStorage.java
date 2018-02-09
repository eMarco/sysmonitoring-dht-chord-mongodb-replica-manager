/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.storage;

import com.mongodb.DB;
import java.util.LinkedList;
import java.util.List;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.unict.ing.pds.dhtdb.replica.p2p.Storage;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * An implementation of the Storage interface for MongoDB
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class MongoDBStorage implements Storage {

    private final DB db;
    private final MongoCollection collection;

    public MongoDBStorage(DB db) {
        // Using a single connection to provide better (query-oriented) scalability
        this.db = db;
        Jongo jongo = new Jongo(db);
        this.collection = jongo.getCollection("lightMonitor45");
    }
    /**
     * Insert
     * @param elem 
     */
    @Override
    public void insert(GenericValue elem) {
        System.err.println(JsonHelper.write(elem));
        collection.insert(elem);
    }
    
    /**
     * 
     * @param elems 
     */
    @Override
    public void insertMany(List<GenericValue> elems) {
        collection.insert(elems.toArray());
    }

    /**
     * 
     * @param key 
     */
    @Override
    public void remove(Key key) {
        String query = "{ key: { key: '"+ key + "' } }";
        removeBy(query);
    }

    /**
     * 
     * @param key
     * @return 
     */
    @Override
    public List<GenericValue> find(Key key) {
        String query = "{ key: { key: '"+ key + "' } }";
        return findBy(query);
    }

    /**
     * 
     * @param key
     * @return 
     */
    @Override
    public List<GenericValue> lessThanAndRemove(Key key) {
        String query = "{ key: { key: { $lte: '" + key + "' } } }";
        List<GenericValue> ret = findBy(query);
        removeBy(query);
        return ret;
    }

    /**
     * 
     * @param elem
     * @param key 
     */
    @Override
    public void update(GenericValue elem, Key key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param query 
     */
    private void removeBy(String query) {
        collection.remove(query);
    }

    /**
     * 
     * @param query
     * @return 
     */
    private List<GenericValue> findBy(String query) {
        MongoCursor<GenericValue> iterDoc;
        List<GenericValue> ret = new LinkedList();

        if (query == null) {
            // Put HERE a default query (TODO)
            iterDoc = collection.find().as(GenericValue.class);
        } else {
            iterDoc = collection.find(query).as(GenericValue.class);
        }

        iterDoc.forEach(v -> ret.add(v));
        return ret;
    }
}
