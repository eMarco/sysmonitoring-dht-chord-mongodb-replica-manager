/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author aleskandro
 */
@Stateless
public class QuerySessionBean implements QuerySessionBeanLocal {

    @EJB
    private LookupSessionBeanLocal lookupSessionBean;

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;

    private Set<Bucket> recursiveForward(Range range, Label region, Set<Bucket> subRangesSet) {
        System.err.println("Starting recursive forwarding");
        List<GenericValue> t = dataManagerChordSessionBean.lookup(region.toKey());
        Bucket bucket = null;
        if (t.size() > 0)
            bucket = (Bucket)t.get(0);
        if (bucket == null) {
            return null;
        }
        subRangesSet.add(bucket);
        Set<Label> branchNodes = Label.branchNodesBetweenLabels(bucket.getLeafLabel(), region);
        System.err.println("PRINTING BRANCH NODES");
        System.err.println(branchNodes);
        for (Label branchNode : branchNodes) {
            Range intersection = range.intersect(branchNode.interval());
            System.err.println("ENDING/RECURSION OF RECURSIVE WITH intersection: " + intersection + branchNode + subRangesSet);
            if (!intersection.isEmpty()) {
                recursiveForward(intersection, branchNode, subRangesSet);
            } 
        }
        return subRangesSet;
    }
   
    // Make the query and return the set of the Bucket leaves that reference the datas queried (select buckets where timestamp is between ($range.lower, $range.upper)
    private Set<Bucket> rangeQuery(Range range) {
        System.err.println("RANGE IS " + range.toString());
        Label lca = lookupSessionBean.lowestCommonAncestor(range);
        System.err.println("FIRST LOOKUP: LCA " + lca.getLabel());
        Bucket bucket = (Bucket)dataManagerChordSessionBean.lookup(lca.toKey()).get(0);
        Set<Bucket> returnedSet = new HashSet<>();
        System.err.println("FIRST LOOKUP RETURNED: " + bucket);
        
        if (bucket == null) { // the range is too small, just a lookup is going to return the only one bucket that references the datas
            System.err.println("BUCKET NULL");
            returnedSet.add((Bucket)lookupSessionBean.lightLabelLookup(range.getLower()));
            return returnedSet;
        } 
        
        System.err.println("Range Query");
        System.err.println(range);
        System.err.println("Bucket range");
        System.err.println(bucket.getRange());
        System.err.println("Intersection");
        Range inters = bucket.getRange().intersect(range);
        System.err.println(inters);
        
        if (bucket.getRange().contains(range)) { // the range is totally contained in the bucket found, the algorithm has ended
            returnedSet.add(bucket);
            return returnedSet;
        } 

        System.err.println("RECURSIVE FORWARD");
        return recursiveForward(range, lca, returnedSet); // the range has datas in more than one bucket calling recurse forward
    }

    // Make the query, get the bucket leaves and get the related datas (select stats where timestamp is between ($range.lower, $range.upper)
    @Override
    public List<GenericValue> getRangeQueryDatas(Range range) {
        Set<Bucket> references = rangeQuery(range);
        List<GenericValue> returnDatas = new LinkedList<>();
        references.forEach((leaf) -> {
            returnDatas.addAll(dataManagerChordSessionBean.lookup(leaf.getLeafLabel().toDataKey()));
        });
        return returnDatas;
    } 

}
