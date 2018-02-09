/* 
 * Copyright (C) 2018 aleskandro - eMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 */
public class MongoDBStorage implements Storage {

    private final DB db;
    private final MongoCollection collection;

    public MongoDBStorage(DB db) {
        // Using a single connection to provide better (query-oriented) scalability
        this.db = db;
        Jongo jongo = new Jongo(db);
        this.collection = jongo.getCollection("lightMonitor");
    }
    /**
     * Insert
     * @param elem  |
     */
    @Override
    public void insert(GenericValue elem) {
        System.err.println(JsonHelper.write(elem));
        collection.insert(elem);
    }
    
    /**
     * 
     * @param elems  |
     */
    @Override
    public void insertMany(List<GenericValue> elems) {
        collection.insert(elems.toArray());
    }

    /**
     * 
     * @param key  |
     */
    @Override
    public void remove(Key key) {
        String query = "{ key: { key: '"+ key + "' } }";
        removeBy(query);
    }

    /**
     * 
     * @param key |
     * @return | 
     */
    @Override
    public List<GenericValue> find(Key key) {
        String query = "{ key: { key: '"+ key + "' } }";
        return findBy(query);
    }

    /**
     * 
     * @param key |
     * @return | 
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
     * @param elem |
     * @param key  |
     */
    @Override
    public void update(GenericValue elem, Key key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param query  |
     */
    private void removeBy(String query) {
        collection.remove(query);
    }

    /**
     * 
     * @param query |
     * @return | 
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
