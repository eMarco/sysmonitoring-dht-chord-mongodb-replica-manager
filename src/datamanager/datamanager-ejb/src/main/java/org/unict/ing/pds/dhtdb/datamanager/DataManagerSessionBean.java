/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.lightBeans.LookupSessionBeanLocal;
import org.unict.ing.pds.dhtdb.datamanager.lightBeans.PutSessionBeanLocal;
import org.unict.ing.pds.dhtdb.datamanager.lightBeans.QuerySessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Stateless
public class DataManagerSessionBean implements DataManagerSessionBeanLocal {

    @EJB
    private LookupSessionBeanLocal lookupSessionBean;

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
        // TODO calculate the dht node, based on f([tsStart, tsEnd], scanner)
        // TODO send the query to the proper nodes
        long upperTs = System.currentTimeMillis() / 1000l;
        long lowerTs = upperTs - 24 * 3600;
        try {
            if (tsStart != null) {
                lowerTs = Long.valueOf(tsStart);
                if (tsEnd != null) {
                    upperTs = Long.valueOf(tsEnd);
                }
            }
            if (upperTs < lowerTs) {
                return this.get(scanner, topic, null, null);
            }
        } catch (NumberFormatException e) {
            return this.get(scanner, topic, null, null);
        }
        List<GenericValue> ret = new LinkedList<>();
        querySessionBean.getRangeQueryDatas(new Range(lowerTs, true, upperTs, true)).forEach((e) -> {
            boolean match = true;
            GenericStat stat = (GenericStat)e;
            System.err.println(stat.getScannerId());
            System.err.println(scanner);
            if (scanner != null && !stat.getScannerId().equals(scanner)) {
                match = false;
            }
            /*
            if (topic != null && stat.getTopic().equalsIgnoreCase(topic)) {
                match = false;
            }*/
            if (match)
                ret.add(e);
        });
        return(JsonHelper.writeList(ret));
    }


    @Override public String test(String content) {
        CPUStat x = new CPUStat((float)0.5, System.currentTimeMillis() / 1000l, "1", new Key(""));
        System.out.println("Making a put for:");
        System.out.println(JsonHelper.write(x));
        putSessionBean.lightPut(x);
        System.err.println("DONE THE PUT");
        List<GenericValue> list = lookupSessionBean.lightLookupAndGetDataBucket(System.currentTimeMillis() / 1000l);
        System.err.println("DONE THE LOOKUP");
        return JsonHelper.writeList(list);
        //return "";
    }
    
    @Override 
    public String test2(String content) {
        //Set<Bucket> buckets = ;
        List<GenericValue> list = querySessionBean.getRangeQueryDatas(new Range(1517998266, false, 1518998266, false));
       
        return JsonHelper.writeList(list);
        //return "";
    }

}
