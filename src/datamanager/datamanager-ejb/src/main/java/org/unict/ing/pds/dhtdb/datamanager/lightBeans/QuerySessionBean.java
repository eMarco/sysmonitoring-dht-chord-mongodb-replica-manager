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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 * This is responsible for the Query on the distributed database over LIGHT indexing
 */
@Stateless
public class QuerySessionBean implements QuerySessionBeanLocal {

    @EJB
    private LightSessionBeanLocal lightSessionBean;

    @EJB
    private LookupSessionBeanLocal lookupSessionBean;
    /**
     * Recursive function that search the neighbours Bucket leaves that match with the Range of the query running
     * 
     * @param initialRange the initial range of the query |
     * @param range the range after intersection with a neighbour's one, the methods exits when this range is empty |
     * @param region the first the lowest common ancestor, after the branchNode of a neighbour |
     * @param subRangesSet a reference to the Set to be returned, filled across the recursions |
     * @param maxLength a safety parameter to avoid infinite recursion: the methods will not use a label with length greater than maxLength (the tree height) |
     * @return | a Set of Bucket that could store the associated datas
     */
    private Set<Bucket> recursiveForward(Range initialRange, Range range, Label region, Set<Bucket> subRangesSet, int maxLength) {
        Bucket bucket = lookupSessionBean.lookupBucket(region);
        if (bucket == null) {
            return null;
        }
        if (!initialRange.intersect(bucket.getRange()).isEmpty())
            subRangesSet.add(bucket);
        Set<Label> branchNodes = Label.branchNodesBetweenLabels(bucket.getLeafLabel(), region);
        System.err.println("PRINTING BRANCH NODES");
        System.err.println(branchNodes);
        for (Label branchNode : branchNodes) {
            Range intersection = range.intersect(branchNode.interval());
            if (!intersection.isEmpty() && branchNode.getLength() < maxLength) {
                System.err.println("STILL RECURSIVE FORWARD: " + intersection + branchNode);
                recursiveForward(initialRange, intersection, branchNode, subRangesSet, maxLength);
            } 
        }
        return subRangesSet;
    }

    /**
     * Creates the Set of Bucket that could store the Records associated with the Range given (the query)
     * @param range |
     * @param maxLength |
     * @return | 
     */
    private Set<Bucket> rangeQuery(Range range, int maxLength) {
        System.err.println("RANGE IS " + range.toString());
        Label lca = lookupSessionBean.lowestCommonAncestor(range);
        Bucket bucket = lookupSessionBean.lookupBucket(lca);
        Set<Bucket> returnedSet = new HashSet<>();
        
        if (bucket == null) { // the range is too small, just a lookup is going to return the only one bucket that references the datas
            System.err.println("BUCKET NULL: The range is too small, using a lookup");
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
        return recursiveForward(range, range, lca, returnedSet, maxLength); // the range has datas in more than one bucket calling recurse forward
    }

    /**
     * This method can be called outside the Bean to make the query and get the Datas associated
     * @param range |
     * @return | the result datas of the query given
     */
    @Override
    public List<GenericValue> getRangeQueryDatas(Range range) {
        int maxLength = lightSessionBean.getTreeHeight();
        Set<Bucket> references = rangeQuery(range, maxLength);
        List<GenericValue> returnDatas = new LinkedList<>();
        System.err.println("GETTING DATAS FROM:");
        System.err.println(references);
        references.forEach((leaf) -> {
            List<GenericValue> t = lookupSessionBean.lightLookupAndGetDataBucket(leaf.getLeafLabel());
            returnDatas.addAll(t);
            System.err.println("GOT " + t.size() + " RECORDS FROM " + leaf);
        });
        
        return returnDatas;
    } 

}
