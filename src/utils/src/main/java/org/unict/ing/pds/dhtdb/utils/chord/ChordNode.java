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
package org.unict.ing.pds.dhtdb.utils.chord;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 */
public interface ChordNode {
    public void bootstrap(NodeReference nodeRef);
    public NodeReference notify(NodeReference nodeRef);

    public Boolean put(List<GenericValue> elem);
    public List<GenericValue> getLessThanAndRemove(Key key);

    public NodeReference findSuccessor(Key key);
    //public NodeReference findPredecessor(Key key);
    public NodeReference getPredecessorNodeRef();

    public List<GenericValue> delete(Key key);
}
