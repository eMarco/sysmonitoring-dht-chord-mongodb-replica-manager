/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class LabelTest {

    public LabelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//    /**
//     * Test of getLabel method, of class Label.
//     */
//    @org.junit.Test
//    public void testGetLabel() {
//        System.out.println("getLabel");
//        Label instance = null;
//        String expResult = "";
//        String result = instance.getLabel();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of prefix method, of class Label.
     */
    @org.junit.Test
    public void testPrefix() {
        System.out.println("prefix");

        // 1
        int length = 4;
        long value = 1517998300L;
        Label expResult = new Label("#0100");

        Label result = Label.prefix(length, value);
        assertEquals(expResult, result);

        // 2
        length = 3;
        value = Integer.MAX_VALUE;
        expResult = new Label("#01");

        result = Label.prefix(length, value);
        assertEquals(expResult, result);

        // 3
        length = 5;
        value = Integer.MAX_VALUE;
        expResult = new Label("#0111");

        result = Label.prefix(length, value);
        assertEquals(expResult, result);

        // 4
        length = 5;
        value = 0;
        expResult = new Label("#0000");

        result = Label.prefix(length, value);
        assertEquals(expResult, result);

        // 5
        length = 5;
        value = 268435455;
        expResult = new Label("#0001");

        result = Label.prefix(length, value);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of getLength method, of class Label.
//     */
//    @org.junit.Test
//    public void testGetLength() {
//        System.out.println("getLength");
//        Label instance = null;
//        int expResult = 0;
//        int result = instance.getLength();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of toString method, of class Label.
     */
    @org.junit.Test
    public void testToString() {
        System.out.println("toString");
        Label instance = new Label("#0111");
        String expResult = "#0111";

        String result = instance.toString();
        System.out.println("ToString: R: " + result + " E: " + expResult);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of toDHTKey method, of class Label.
//     */
//    @org.junit.Test
//    public void testToDHTKey() {
//        System.out.println("toDHTKey");
//        Label instance = null;
//        Label expResult = null;
//        Label result = instance.toDHTKey();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of toKey method, of class Label.
     */
    @org.junit.Test
    public void testToKey() {
        System.out.println("toKey");
        Label instance = new Label("#0");
        Key expResult = new Key("#", true);
        System.out.println("KEY HASH " + expResult.getKey());
        Key result = instance.toKey();
        assertEquals(expResult, result);
    }

//    /**
//     * Test of toDataKey method, of class Label.
//     */
//    @org.junit.Test
//    public void testToDataKey() {
//        System.out.println("toDataKey");
//        Label instance = null;
//        Key expResult = null;
//        Key result = instance.toDataKey();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of namingFunction method, of class Label.
     */
    @org.junit.Test
    public void testNamingFunction_0args() {
        System.out.println("namingFunction");

        // 1
        Label instance = new Label("#0");
        Label expResult = new Label("#");

        Label result = instance.namingFunction();

        assertEquals(expResult, result);

        // 2
        instance = new Label("#000");
        result = instance.namingFunction();

        assertEquals(expResult, result);

        // 3
        instance = new Label("#111");
        result = instance.namingFunction();

        assertEquals(expResult, result);

        // 4
        instance = new Label("#0111");
        expResult = new Label("#0");
        result = instance.namingFunction();

        assertEquals(expResult, result);

        // 5
        System.out.println("\n\n\n5 TRY");
        instance = new Label("#010");
        expResult = new Label("#01");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");

        assertEquals(expResult, result);

        // 6
        instance = new Label("#101");
        expResult = new Label("#10");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");

        assertEquals(expResult, result);


        // 7
        instance = new Label("#011");
        expResult = new Label("#0");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");

        // 8
        instance = new Label("#101");
        expResult = new Label("#10");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");
        assertEquals(expResult, result);

        // 9
        instance = new Label("#101011");
        expResult = new Label("#1010");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");
        assertEquals(expResult, result);

        // 10
        instance = new Label("#101010");
        expResult = new Label("#10101");
        result = instance.namingFunction();

        System.out.println("---> " + instance.getLabel() + " => naming => GOT " + result + "(" + expResult.getLabel() + " expected)");
        assertEquals(expResult, result);
    }

    /**
     * Test of nextNamingFunction method, of class Label.
     */
    @org.junit.Test
    public void testNextNamingFunction_int_int() {
        System.out.println("nextNamingFunction");

        // 1
        int treeLength = 6;
        int prefixLength = 4;
        Label instance = new Label("#0011100");
        Label expResult = new Label("#0011100");

        Label result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);

        // 2
        treeLength = 14;
        prefixLength = 4;
        instance = new Label("#01110011001100");
        expResult = new Label("#01110");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);

        // 3
        treeLength = 3;
        prefixLength = 3;
        instance = new Label("#01");
        expResult = new Label("#010");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);

        // 3
        treeLength = 3;
        prefixLength = 7;
        instance = new Label("#0100011011001001010111");
        expResult = new Label("#01000110");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);

        // 4
        treeLength = 2;
        prefixLength = 2;
        instance = new Label("#01");
        expResult = new Label("#01");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        System.out.println(result.getLength());
        assertEquals(expResult, result);

        // 5
        treeLength = 2;
        prefixLength = 2;
        instance = new Label("#10");
        expResult = new Label("#10");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);

        // 6
        treeLength = 2;
        prefixLength = 4;
        instance = new Label("#10111011101101111101");
        expResult = new Label("#101110");

        result = instance.nextNamingFunction(prefixLength, treeLength);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of interval method, of class Label.
//     */
//    @org.junit.Test
//    public void testInterval_0args() {
//        System.out.println("interval");
//        Label instance = null;
//        Range expResult = null;
//        Range result = instance.interval();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of isRight method, of class Label.
     */
    @org.junit.Test
    public void testIsRight() {
        System.out.println("isRight");
        Label instance = new Label("#0100");
        boolean expResult = false;

        boolean result = instance.isRight();
        assertEquals(expResult, result);

        instance = new Label("#0101");
        expResult = true;

        result = instance.isRight();
        assertEquals(expResult, result);
    }

    /**
     * Test of isLeft method, of class Label.
     */
    @org.junit.Test
    public void testIsLeft() {
        System.out.println("isLeft");
       Label instance = new Label("#0100");
        boolean expResult = true;

        boolean result = instance.isLeft();
        assertEquals(expResult, result);

        instance = new Label("#0101");
        expResult = false;

        result = instance.isLeft();
        assertEquals(expResult, result);
    }

    
    private void split(Bucket localBucket) {
        System.err.println("SPLITTING");
        Label localLabel = localBucket.getLeafLabel();
        Range localRange = localBucket.getRange();
        int   currentRecords = localBucket.getRecordsCounter();
        
        long mid = localRange.createSplit(false).getUpper();
        
        Bucket newLocalBucket;
        Bucket newRemoteBucket;

        // Just two pointers (TODO improve me)
        Bucket leftPointer;
        Bucket rightPointer;
       
        if (localLabel.isRight()) {
            newRemoteBucket = leftPointer  = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newLocalBucket  = rightPointer = new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        } else { // isLeft
            newLocalBucket  = leftPointer = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newRemoteBucket = rightPointer  = new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        }
        
        System.err.println("Local Bucket");
        System.err.println(JsonHelper.write(localBucket));
        System.err.println(JsonHelper.write(newLocalBucket));
        System.err.println("Remote bucket");
        System.err.println(JsonHelper.write(newRemoteBucket));
    }
    /**
     * Test of leftChild method, of class Label.
     */
    @org.junit.Test
    public void testLeftChild() {
        System.out.println("leftChild");
        Label instance = new Label("#010");
        Label expResult = new Label("#0100");

        Label result = instance.leftChild();

        assertEquals(result.getLength(), 5);
        assertEquals(expResult, result);
        
        
        instance = new Label("#01");
        expResult = new Label("#010");

        result = instance.leftChild();
        Label result2 = instance.rightChild();
        System.out.println(instance);
        System.out.println(expResult);
        System.out.println(instance.toKey());
        System.out.println(expResult.toKey());
        
        
        Bucket localBucket = new Bucket(new Range(0, false, 1000, false), new Label("#0"), 10);
        split(localBucket);
        localBucket = new Bucket(new Range(0, false, 1000, false), new Label("#01"), 10);
        split(localBucket);
        localBucket = new Bucket(new Range(0, false, 1000, false), new Label("#010"), 10);
        split(localBucket);
        assertEquals(result.toKey(), expResult.toKey());
        assertEquals(expResult, result);
    }

    /**
     * Test of rightChild method, of class Label.
     */
    @org.junit.Test
    public void testRightChild() {
        System.out.println("rightChild");
        Label instance = new Label("#010");
        Label expResult = new Label("#0101");

        Label result = instance.rightChild();
        System.out.println(result);

        assertEquals(result.getLength(), 5);
        assertEquals(expResult, result);
    }

    /**
     * Test of interval method, of class Label.
     */
    @org.junit.Test
    public void testInterval_Label() {
        System.out.println("interval");
        Label label = new Label("#");
        Range expResult = Range.REPRESENTABLE_RANGE;
        Range result = Label.interval(label);

        System.out.println("Result: " + result.toString());
        System.out.println("ExpResult: " + expResult.toString());

        assertEquals(expResult, result);
    }

//    /**
//     * Test of namingFunction method, of class Label.
//     */
//    @org.junit.Test
//    public void testNamingFunction_Label() {
//        System.out.println("namingFunction");
//        Label label = null;
//        Label expResult = null;
//        Label result = Label.namingFunction(label);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of namingFunction method, of class Label.
//     */
//    @org.junit.Test
//    public void testNamingFunction_Label_int() {
//        System.out.println("namingFunction");
//        Label label = null;
//        int dimentions = 0;
//        Label expResult = null;
//        Label result = Label.namingFunction(label, dimentions);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of nextNamingFunction method, of class Label.
//     */
//    @org.junit.Test
//    public void testNextNamingFunction_3args() {
//        System.out.println("nextNamingFunction");
//        Label label = null;
//        int prefixLength = 0;
//        int treeLength = 0;
//        Label expResult = null;
//        Label result = Label.nextNamingFunction(label, prefixLength, treeLength);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of lowestCommonAncestor method, of class Label.
     */
    @org.junit.Test
    public void testLowestCommonAncestor_LabelArr() {
        System.out.println("lowestCommonAncestor");

        //1
        Label[] labels = new Label[] { new Label("#010"), new Label("#01"), new Label("#01011"), new Label("#01001") };
        Label expResult = new Label("#01");

        Label result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);

        //2
        labels = new Label[] { new Label("#1010"), new Label("#01"), new Label("#01011"), new Label("#01001") };
        expResult = new Label("#");

        result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);

        //3
        labels = new Label[] { new Label("#101010"), new Label("#01"), new Label("#01011"), new Label("#01001") };
        expResult = new Label("#0");

        result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);

        //4
        labels = new Label[] { new Label("#0"), new Label("#01"), new Label("#01001") };
        expResult = new Label("#0");

        result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);

        //5
        labels = new Label[] { new Label("#1"), new Label("#01") };
        expResult = new Label("#");

        result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);

        //6
        labels = new Label[] { new Label("#0"), new Label("#0") };
        expResult = new Label("#0");

        result = Label.lowestCommonAncestor(labels);
        assertEquals(expResult, result);
    }

    /**
     * Test of lowestCommonAncestor method, of class Label.
     */
//    @org.junit.Test
//    public void testLowestCommonAncestor_BitSet_BitSet() {
//        System.out.println("lowestCommonAncestor");
//        BitSet label1 = null;
//        BitSet label2 = null;
//        int expResult = 0;
//        int result = Label.lowestCommonAncestor(label1, label2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of branchNodesBetweenLabels method, of class Label.
     */
    @org.junit.Test
    public void testBranchNodesBetweenLabels() {
        /*System.out.println("branchNodesBetweenLabels");

        Label label     = new Label("#01011");
        Label region    = new Label("#0101");

        Set<Label> expResult = new HashSet<>();
        expResult.add(new Label("#01010"));

        Set<Label> result = Label.branchNodesBetweenLabels(label, region);
        assertEquals(expResult, result);

        label     = new Label("#010101");
        region    = new Label("#0101");

        expResult = new HashSet<>();
        expResult.add(new Label("#01011"));
        expResult.add(new Label("#010100"));

        result = Label.branchNodesBetweenLabels(label, region);
        assertEquals(expResult, result);*/
    }

}
