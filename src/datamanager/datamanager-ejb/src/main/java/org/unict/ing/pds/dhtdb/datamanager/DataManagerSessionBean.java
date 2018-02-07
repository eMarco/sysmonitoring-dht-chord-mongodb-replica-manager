/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Stateless
public class DataManagerSessionBean implements DataManagerSessionBeanLocal {
    private static final int TETA_SPLIT = 5;
    
    @EJB
    private LightSessionBeanLocal lightSessionBean;

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;

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
            //lightPut(elem);
        });
        // Wrong topic in request
    }

    @Override
    public String get(String scanner, String topic, String tsStart, String tsEnd) {
        // TODO calculate the dht node, based on f([tsStart, tsEnd], scanner)
        // TODO send the query to the proper nodes
        return "TODO";
    }

    private Label lowestCommonAncestor(Range range) {
        System.err.println("LOWEST COMMON ANCESTOR");
        Label lower = this.lightLabelLookup(range.getLower());
        System.err.println(lower.toString());
        Label upper = this.lightLabelLookup(range.getUpper());
        System.err.println(upper.toString());
        return Label.lowestCommonAncestor(lower, upper);
    }

    // Algorithm 1 modified (needed for lowestCommonAncestor)
    private Label lightLabelLookup(long timestamp) {
        int lower = 2;
        int upper = lightSessionBean.getTreeHeight() + 1;
        int mid;
        Label u = Label.prefix(upper, timestamp);
        
        while (lower < upper){
            mid = (lower + upper) / 2;
            System.err.println("LightLookup: MID: " + mid + " \t TIMESTAMP: " + timestamp);
            Label x = Label.prefix(mid, timestamp);
            List<GenericValue> t = dataManagerChordSessionBean.lookup(x.toKey());
            Bucket bucket = null;
            if (t.size() > 0) {
                System.err.println("THE LABEL " + x.toString() + " HAS BEEN FOUND, GETTING...");
                bucket = (Bucket)t.get(0);
                System.err.println("BUCKET TAKEN: " + bucket.getLabel().toString() + "; RANGE: " + bucket.getRange().getUpper());
                checkTreeHeight(bucket.getLeafLabel()); // TODO TEST
            }
            if (bucket == null) 
                upper = x.toDHTKey().getLength();
            else if (bucket.getRange().contains(timestamp)) {
                return x; 
            } else {
                System.out.println("Called nextNamingFunction... Iterating");
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
    private List<GenericValue> lightLookupAndGetBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLookup(timestamp).toKey());
    }
 
    // Lookup the entire bucket leaf and return the list of referenced datas that could contain a subSet with the timestamp 
    // given (select stats where `timestamp` "contains" $timestamp)
    public List<GenericValue> lightLookupAndGetDataBucket(long timestamp) {
        return dataManagerChordSessionBean.lookup(lightLookup(timestamp).toDataKey());
    }   
 
    public List<GenericValue> lightLookupAndGetDataBucket(Label bucketLabel) {
        return dataManagerChordSessionBean.lookup(bucketLabel.toDataKey());
    }   
   
    // Exact match (Get the data) (select stats where `timestamp` is exactly $timestamp
    public List<GenericValue> lightLookupAndGetValue(long timestamp) {
        List<GenericValue> l = lightLookupAndGetDataBucket(timestamp);
        /*List<GenericStat> stats = new LinkedList<>();
        l.forEach((e) -> stats.add((GenericStat)e));*/
        List<GenericStat> filter = new LinkedList<>();
        filter.add(new GenericStat(timestamp));
        l.retainAll(filter);
        return l;

    }
 
    // Put a new GenericStat in the Database
    public void lightPut(GenericStat stat) {
        long timestamp = stat.getTimestamp();
        Label dhtKey   = lightLookup(timestamp);
        if (dhtKey == null) { // The database is empty
            // Creates a new bucket
            Bucket theFirst = new Bucket(Range.REPRESENTABLE_RANGE, new Label("#0"), 0);
            System.err.println("#0");
            System.err.println(new Label("#0").toKey());
            System.err.println(new Label("#0").toDHTKey().toKey());
            System.err.println(theFirst.getKey());
            dataManagerChordSessionBean.write(theFirst.getKey(), theFirst);
            lightPut(stat);
            return;
        }
        Bucket bucket  = (Bucket)dataManagerChordSessionBean.lookup(dhtKey.toKey()).get(0);
        if (bucket.getRecordsCounter() >= TETA_SPLIT) {
            dhtKey = this.splitAndPut(bucket, timestamp, stat);
            //return;
        } else {
            bucket.incrementRecordsCounter();
            dataManagerChordSessionBean.update(bucket.getKey(), bucket);
        }
        
        stat.setKey(dhtKey.toDataKey());
        dataManagerChordSessionBean.write(stat.getKey(), stat);
    }
    private void checkTreeHeight(Label label) {
        int currentHeight = lightSessionBean.getTreeHeight();
        int max = Math.max(label.getLength(), currentHeight);
        if (currentHeight != max)
           lightSessionBean.setTreeHeight(max);
    }
    private Label splitAndPut(Bucket localBucket, long timestamp, GenericStat elem) {
        System.err.println("SPLITTING");
        Label localLabel = localBucket.getLeafLabel();
        Range localRange = localBucket.getRange();
        int   currentRecords = localBucket.getRecordsCounter();
        List<GenericValue> currentDatas = lightLookupAndGetDataBucket(localLabel);
        long mid = localRange.createSplit(false).getUpper();
        
        Bucket newLocalBucket;
        Bucket newRemoteBucket;

        // Just two pointers (TODO improve me)
        Bucket leftPointer;
        Bucket rightPointer;
        List<GenericValue> leftDatas   = new LinkedList<>();
        List<GenericValue> rightDatas  = new LinkedList<>();
       
        List<GenericValue> records = dataManagerChordSessionBean.lookup(localLabel.toDataKey());
        Bucket remoteBucket = new Bucket();
        if (localLabel.isRight()) {
            newRemoteBucket = leftPointer  = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newLocalBucket  = rightPointer = new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        } else { // isLeft
            newLocalBucket  = leftPointer = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newRemoteBucket = rightPointer= new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        }
        
        System.err.println("Local Bucket");
        System.err.println(localBucket);
        System.err.println(newLocalBucket);
        System.err.println("Remote bucket");
        System.err.println(newRemoteBucket);
        checkTreeHeight(leftPointer.getLeafLabel());
        currentDatas.forEach((GenericValue e) -> {
            if (e instanceof GenericStat)
                if (((GenericStat) e).getTimestamp() >= mid) {
                    e.setKey(rightPointer.getLeafLabel().toDataKey());
                    rightDatas.add(e);
                } else {
                    e.setKey(leftPointer.getLeafLabel().toDataKey());
                    leftDatas.add(e);
                }
        });
        
        leftPointer.setRecordsCounter(leftDatas.size());
        rightPointer.setRecordsCounter(rightDatas.size());
        
        System.err.println("Left Datas");
        System.err.println(leftDatas);
        System.err.println("Right Datas");
        System.err.println(rightDatas);
        // update localBucket
        dataManagerChordSessionBean.update(newLocalBucket.getKey(), newLocalBucket);
        
        // put bucket to remoteBucket
        dataManagerChordSessionBean.write(newRemoteBucket.getKey(), newRemoteBucket);

        // TODO
        // put datas (One of the children should not need to put the entire bucket but we need a way to 
        // delete only one half of the datas associated with a given key derived from the localBucket label
        dataManagerChordSessionBean.write(leftPointer.getLeafLabel().toDataKey(), leftDatas);
        dataManagerChordSessionBean.write(rightPointer.getLeafLabel().toDataKey(), rightDatas);
        
        //return the label where the put has to send the new stat
        if (timestamp > mid) {
            return rightPointer.getLeafLabel();
        } 
        return leftPointer.getLeafLabel(); 
        //lightPut(elem);
        //return null;
    }

    // Used when the range query is spawned across different bucket leaves
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
        Label lca = lowestCommonAncestor(range);
        System.err.println("FIRST LOOKUP: LCA " + lca.getLabel());
        Bucket bucket = (Bucket)dataManagerChordSessionBean.lookup(lca.toKey()).get(0);
        Set<Bucket> returnedSet = new HashSet<>();
        System.err.println("FIRST LOOKUP RETURNED: " + bucket);
        
        if (bucket == null) { // the range is too small, just a lookup is going to return the only one bucket that references the datas
            System.err.println("BUCKET NULL");
            returnedSet.add((Bucket)lightLookupAndGetBucket(range.getLower()).get(0));
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
    public List<GenericValue> getRangeQueryDatas(Range range) {
        Set<Bucket> references = rangeQuery(range);
        List<GenericValue> returnDatas = new LinkedList<>();
        references.forEach((leaf) -> {
            returnDatas.addAll(dataManagerChordSessionBean.lookup(leaf.getLeafLabel().toDataKey()));
        });
        return returnDatas;
    } 

    @Override public String test(String content) {
        
        System.err.println("INIT: ");
        System.err.println(Range.REPRESENTABLE_RANGE.getUpper());
        /*lightPut(new CPUStat((float)0.5, 1517998300, "1", new Key("")));
        lightPut(new CPUStat((float)0.5, 1517998305, "1", new Key("")));
        lightPut(new CPUStat((float)0.5, 1517998310, "1", new Key("")));*/
        lightPut(new CPUStat((float)0.5, 1517908320, "1", new Key("")));
        lightPut(new CPUStat((float)0.5, 1618998330, "1", new Key("")));
        lightPut(new CPUStat((float)0.5, 1517908340, "1", new Key("")));
        //lightPut(new CPUStat((float)0.5, 1517998350, "1", new Key("")));
        System.err.println("DONE THE PUT");
        List<GenericValue> list = lightLookupAndGetDataBucket(1517998300);
        System.err.println("DONE THE LOOKUP");
        /*Set<Bucket> buckets = rangeQuery(new Range(1517998266, false, 1518998266, false));
        List<GenericValue> list2 = new LinkedList<GenericValue>();
        buckets.forEach(b -> { 
            list2.addAll(lightLookupAndGetDataBucket(b.getLeafLabel()));
        });
        */
        return JsonHelper.writeList(list);
        //return "";
    }
    @Override 
    public String test2(String content) {
        Set<Bucket> buckets = rangeQuery(new Range(1517998266, false, 1518998266, false));
        List<GenericValue> list = new LinkedList<>();
        buckets.forEach(b -> { 
            list.addAll(lightLookupAndGetDataBucket(b.getLeafLabel()));
        });
        
        return JsonHelper.writeList(list);
    }
    
/* 
    @Override
    public String test(String content) {
        try {

            content =  "{ \"MemTotal\":\"12126164\", \"MemFree\":\"230924\"," +
                    " \"MemAvailable\":\"838236\", \"timestamp\": 1517777828, "
                    + "\"className\": \"org.unict.ing.pds.dhtdb.utils.model.RAMStat\" }";
            content = "{\"seconds\": \"17\", \"minutes\":\"7\", \"hours\":\"6\", \"days\": \"0\", "
                    + "\"timestamp\": 1517778670, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.UptimeStat\"}";

            content = "{\"usage\":0.5,\"timestamp\":4,\"scannerId\":\"asd\",\"key\":{\"key\":\"1699d6b5508374cf2becc8778548b263271da293\"}}";

            content = "[{\"disk\":\"sda\", \"WritekBps\":\"313.87\", \"ReadkBps\":\"694.29\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"sdb\", \"WritekBps\":\"13.16\", \"ReadkBps\":\"44.46\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-0\", \"WritekBps\":\"305.58\", \"ReadkBps\":\"637.26\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-1\", \"WritekBps\":\"0.23\", \"ReadkBps\":\"0.00\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }, {\"disk\":\"dm-2\", \"WritekBps\":\"7.92\", \"ReadkBps\":\"57.04\", \"timestamp\": 1517780002, \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" }][{\"disk\":\"sda\", \"WritekBps\":\"315.71\", \"ReadkBps\":\"692.67\", \"timestamp\": 1517779859}, {\"disk\":\"sdb\", \"WritekBps\":\"13.24\", \"ReadkBps\":\"43.04\", \"timestamp\": 1517779859}, {\"disk\":\"dm-0\", \"WritekBps\":\"307.38\", \"ReadkBps\":\"635.28\", \"timestamp\": 1517779859}, {\"disk\":\"dm-1\", \"WritekBps\":\"0.23\", \"ReadkBps\":\"0.00\", \"timestamp\": 1517779859}, {\"disk\":\"dm-2\", \"WritekBps\":\"7.96\", \"ReadkBps\":\"57.39\", \"timestamp\": 1517779859}]";
            ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            List<GenericValue> fromJson = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));

            String toJson = mapper.writeValueAsString(fromJson);
            List<GenericValue> fromJson2 = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, GenericValue.class));

            return "Ciao mbare " + content + mapper.writeValueAsString(fromJson) + mapper.writeValueAsString(fromJson2) + toJson;
        } catch (IOException ex) {
            Logger.getLogger(DataManagerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            return "FUCK " + ex.getMessage();
        }
    }
*/
}
