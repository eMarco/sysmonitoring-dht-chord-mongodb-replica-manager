/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author aleskandro
 */
@Stateless
public class LookupSessionBean implements LookupSessionBeanLocal {

    @EJB
    private LightSessionBeanLocal lightSessionBean;

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;

    @Override
    public Bucket lookupBucket(Label l) {
        List<GenericValue> bucket = dataManagerChordSessionBean.lookup(l.toKey());
        if (bucket.size() > 0) {
            return (Bucket)bucket.get(0);
        }
        return null;
    }
    // Algorithm 1
    @Override
    public Bucket lightLabelLookup(long timestamp) {
        int lower = 2;
        int upper = lightSessionBean.getTreeHeight() + 1;
        int mid;
        Label u = Label.prefix(upper, timestamp);
        System.out.println("NU: " + u);
        
        while (lower < upper){
            mid = (lower + upper) / 2;
            Label x = Label.prefix(mid, timestamp);
            System.out.println("MID: " + mid + " \t TIMESTAMP: " + timestamp + " \t PREFIX: " + x);
            Bucket bucket = lookupBucket(x);    
            if (bucket == null)  {
                System.err.println("LOOKUP FAILED");
                upper = x.toDHTKey().getLength();
            } else {
                lightSessionBean.checkTreeHeight(bucket.getLeafLabel());  
                if (bucket.getRange().contains(timestamp)) {
                    System.err.println("THE BUCKET COVER DELTA: returning " + x);
                    return bucket; 
                } else {
                    System.err.println("INCREMENT LOWER: nextNamingFunction...");
                    lower = u.nextNamingFunction(x.getLength(), lightSessionBean.getTreeHeight()).getLength();
                }
            }
        }
        return null;
    }

    // Lookup the entire bucket leaf and return the list of referenced datas that could contain a subSet with the timestamp 
    // given (select stats where `timestamp` "contains" $timestamp)
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLabelLookup(timestamp)
                .getLeafLabel().toDataKey());
    }   
 
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(Label bucketLabel) {
        return dataManagerChordSessionBean.lookup(bucketLabel.toDataKey());
    }   

    @Override
    public Label lowestCommonAncestor(Range range) {
        Bucket lower = this.lightLabelLookup(range.getLower());
        if (lower == null) {
            return null;
        }
        Bucket upper = this.lightLabelLookup(range.getUpper());
        if (upper == null)
            return lower.getLeafLabel();
        return Label.lowestCommonAncestor(lower.getLeafLabel(), upper.getLeafLabel());
    }
    
    // Exact match (Get the data) (select stats where `timestamp` is exactly $timestamp
    private List<GenericValue> lightLookupAndGetValue(long timestamp) {
        List<GenericValue> l = lightLookupAndGetDataBucket(timestamp);
        List<GenericStat> filter = new LinkedList<>();
        filter.add(new GenericStat(timestamp));
        l.retainAll(filter);
        return l;
    }
}
