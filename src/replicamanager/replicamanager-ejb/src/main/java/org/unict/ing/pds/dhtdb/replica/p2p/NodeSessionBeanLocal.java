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
package org.unict.ing.pds.dhtdb.replica.p2p;

import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.chord.ChordNode;
import org.unict.ing.pds.dhtdb.utils.dht.DHTNode;
import java.util.List;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

import org.unict.ing.pds.dhtdb.utils.common.NodeReference;

/**
 *
 */
@Local
public interface NodeSessionBeanLocal extends DHTNode, ChordNode {

    public Boolean write(Key k, GenericValue elem);
    public List<GenericValue> lookup(Key k);

    public NodeReference getNodeReference();

    public List<GenericValue> remove(Key key);

    public Boolean update(Key key, List<GenericValue> elems);
}
