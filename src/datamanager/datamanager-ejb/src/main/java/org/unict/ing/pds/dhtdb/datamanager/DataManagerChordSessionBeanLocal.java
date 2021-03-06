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
package org.unict.ing.pds.dhtdb.datamanager;

import java.util.List;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 *
 */
@Local
public interface DataManagerChordSessionBeanLocal {

    public Boolean write(Key key, GenericValue elem);

    public List<GenericValue> lookup(Key key);

    public Boolean write(Key key, List<GenericValue> elems);

    public Boolean update(Key key, List<GenericValue> elems);

    public Boolean update(Key key, GenericValue elem);
    
}
