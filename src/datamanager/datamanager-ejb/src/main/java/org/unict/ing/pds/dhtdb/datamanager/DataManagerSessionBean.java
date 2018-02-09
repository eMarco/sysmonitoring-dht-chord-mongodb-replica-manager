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

import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.lightBeans.PutSessionBeanLocal;
import org.unict.ing.pds.dhtdb.datamanager.lightBeans.QuerySessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Range;

/**
 * The bean responsible for the requests from the FrontEnd REST APIs
 */
@Stateless
public class DataManagerSessionBean implements DataManagerSessionBeanLocal {

    @EJB
    private QuerySessionBeanLocal querySessionBean;

    @EJB
    private PutSessionBeanLocal putSessionBean;

    @Override
    public void put(String scanner, String topic, String content) {
        // Convert the request in the proper model object
        List<GenericValue> fromJson2;
        fromJson2 = JsonHelper.readList(content);
        List<GenericStat> fromJson = new LinkedList<>();
        fromJson2.forEach(elem -> {
            fromJson.add((GenericStat)elem);
        });
        fromJson.forEach(elem -> {
            elem.setScannerId(scanner);
            putSessionBean.lightPut(elem);
        });
        // Wrong topic in request
    }

    @Override
    public String get(String scanner, String topic, String tsStart, String tsEnd) {
        long upperTs = System.currentTimeMillis() / 1000l;
        long lowerTs = upperTs - 24 * 3600;
        try {
            if (tsStart != null) {
                lowerTs = Long.valueOf(tsStart);
                if (tsEnd != null) {
                    upperTs = Long.valueOf(tsEnd);
                }
            }
            if (upperTs < lowerTs || (upperTs - lowerTs) > 7*86400 ||
                    !(new Range(lowerTs, true, upperTs, true)
                            .isContainedIn(Range.REPRESENTABLE_RANGE))
                    ) {
                return this.get(scanner, topic, null, null);
            }
        } catch (NumberFormatException e) {
            return this.get(scanner, topic, null, null);
        }
        List<GenericValue> ret = new LinkedList<>();
        querySessionBean.getRangeQueryDatas(new Range(lowerTs, true, upperTs, true)).forEach((e) -> {
            boolean match = true;
            GenericStat stat = (GenericStat)e;
            if (scanner != null && !stat.getScannerId().equals(scanner))
                match = false;
            
           if (topic != null && !stat.getTopic().equalsIgnoreCase(topic)) 
                match = false;
            
            if (match)
                ret.add(e);
        });
        return(JsonHelper.writeList(ret));
    }

}
