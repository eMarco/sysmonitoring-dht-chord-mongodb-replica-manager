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
import org.unict.ing.pds.dhtdb.datamanager.LightSessionBeanLocal;
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

    
    // Algorithm 1 modified (needed for lowestCommonAncestor)
    @Override
    public Label lightLabelLookup(long timestamp) {
        int lower = 2;
        int upper = lightSessionBean.getTreeHeight() + 1;
        int mid;
        Label u = Label.prefix(upper, timestamp);
        System.err.println("NU: " + u + "UPPER: " + upper);
        
        while (lower < upper){
            mid = (lower + upper) / 2;
            System.err.println("LightLookup: MID: " + mid + " \t TIMESTAMP: " + timestamp);
            Label x = Label.prefix(mid, timestamp);
            System.err.println("TRYING PREFIX: " + x);
            List<GenericValue> t = dataManagerChordSessionBean.lookup(x.toKey());
            Bucket bucket = null;
            if (t.size() > 0) {
                System.err.println("THE LABEL " + x.toString() + " HAS BEEN FOUND, GETTING...");
                bucket = (Bucket)t.get(0);
                System.err.println("BUCKET TAKEN: " + bucket);
                lightSessionBean.checkTreeHeight(bucket.getLeafLabel()); // TODO TEST
            }
            if (bucket == null)  {
                System.err.println("LOOKUP FAILED");
                upper = x.toDHTKey().getLength();
            }
            else if (bucket.getRange().contains(timestamp)) {
                System.err.println("THE BUCKET COVER DELTA: returning " + x);
                return x; 
            } else {
                System.out.println("Called nextNamingFunction... Iterating (" + u + " ; " + x.getLength() + "; " + lightSessionBean.getTreeHeight());
                lower = u.nextNamingFunction(x.getLength(), lightSessionBean.getTreeHeight()).getLength();
            }
        }
        return null;
    }

    // Algorithm 1 with Naming Function
    private Label lightLookup(long timestamp) {
        try {
            return lightLabelLookup(timestamp).toDHTKey();
        } catch (NullPointerException e) {
            System.err.println("Label not found");
        }
        return null;
    }
   
    // Lookup an entire bucket leaf (select buckets where `timestamp` is $timestamp
    @Override
    public List<GenericValue> lightLookupAndGetBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLookup(timestamp).toKey());
    }
 
    // Lookup the entire bucket leaf and return the list of referenced datas that could contain a subSet with the timestamp 
    // given (select stats where `timestamp` "contains" $timestamp)
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLookup(timestamp).toDataKey());
    }   
 
    @Override
    public List<GenericValue> lightLookupAndGetDataBucket(Label bucketLabel) {
        return dataManagerChordSessionBean.lookup(bucketLabel.toDataKey());
    }   

    @Override
    public Label lowestCommonAncestor(Range range) {
        System.err.println("LOWEST COMMON ANCESTOR");
        Label lower = this.lightLabelLookup(range.getLower());
        System.err.println(lower.toString());
        Label upper = this.lightLabelLookup(range.getUpper());
        System.err.println(upper.toString());
        return Label.lowestCommonAncestor(lower, upper);
    }
    
    // Exact match (Get the data) (select stats where `timestamp` is exactly $timestamp
    
    private List<GenericValue> lightLookupAndGetValue(long timestamp) {
        List<GenericValue> l = lightLookupAndGetDataBucket(timestamp);
        /*List<GenericStat> stats = new LinkedList<>();
        l.forEach((e) -> stats.add((GenericStat)e));*/
        List<GenericStat> filter = new LinkedList<>();
        filter.add(new GenericStat(timestamp));
        l.retainAll(filter);
        return l;

    }
    
}
