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
 * This Bean is responsible for the Lookup operations over LIGHT
 */
@Stateless
public class LookupSessionBean implements LookupSessionBeanLocal {

    @EJB
    private LightSessionBeanLocal lightSessionBean;

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;
    
    /**
     * Get a Bucket in the Chord network and return that one if it exists
     * @param l |
     * @return | 
     */
    @Override
    public Bucket lookupBucket(Label l) {
        List<GenericValue> bucket = dataManagerChordSessionBean.lookup(l.toKey());
        if (bucket.size() > 0) {
            return (Bucket)bucket.get(0);
        }
        return null;
    }
    
    /**
     * An implementation of the Algorithm 1 provided in LIGHT paper
     * It takes a timestamp and return the associated Bucket, if it exists,
     * or null
     * @param timestamp |
     * @return | 
     */
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
    /**
     * Useful method to get all the datas referenced by the Bucket that has a Range
     * containing the timestamp given
     * 
     * @param timestamp |
     * @return | the datas referenced by the Bucket associated with the timestamp given
     */
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLabelLookup(timestamp)
                .getLeafLabel().toDataKey());
    }   
 
    /**
     * Useful method to get all the datas referenced by the Bucket with
     * label bucketLabel
     * @param bucketLabel |
     * @return | the List of GenericValue referenced by the bucket
     */
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(Label bucketLabel) {
        return dataManagerChordSessionBean.lookup(bucketLabel.toDataKey());
    }   

    /**
     * Get the lowest common ancestor between the Label (s) associated with
     * the upperBound and the lowerBound of the Range given
     * @param range |
     * @return | a Label corresponding to the lowest common ancestor
     */
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
    /*private List<GenericValue> lightLookupAndGetValue(long timestamp) {
        List<GenericValue> l = lightLookupAndGetDataBucket(timestamp);
        List<GenericStat> filter = new LinkedList<>();
        filter.add(new GenericStat(timestamp));
        l.retainAll(filter);
        return l;
    }*/
}
