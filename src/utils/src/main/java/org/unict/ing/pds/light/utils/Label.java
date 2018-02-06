/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import org.unict.ing.pds.dhtdb.utils.dht.Key;
import java.util.BitSet;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class Label {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    public Label(byte[] label) {
        this.label = new String(label);
    }

    public String getLabel() {
        return "#" + this.label;
    }

    public Label(long timestamp) {
        // TODO

        this.label = String.valueOf(timestamp);
    }

    public Label prefix(int mid) {
        return null;
    }

    public int getLength() {
        return 0;
    }    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getLabel();
    }

    public Label toDHTKey() {
        return Label.namingFunction(this, 1);
    }

    public Key toKey() {
        return new Key(toDHTKey().getLabel(), true);
    }

    public Key toDataKey() {
        return new Key(toDHTKey().getLabel() + "DATA", true);
    }

    public Label namingFunction() {
        return Label.namingFunction(this, 1);
    }

//    public Label nextNamingFunction() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    /**
     *
     * @param treeLenght
     * @return
     */
    public Label nextNamingFunction(int treeLenght) {
        return Label.nextNamingFunction(this, treeLenght);
    }

    /**
     *
     * @param label
     * @return
     */
    public static Label namingFunction(Label label) {
        return Label.namingFunction(label, 1);
    }

    /**
     *
     * @param label
     * @param dimentions
     * @return
     */
    public static Label namingFunction(Label label, int dimentions) {
        byte[] bytes = label.getBytes();

        BitSet bits = BitSet.valueOf(bytes);
        return namingFunction(bits, dimentions, bits.length());
    }

    public static Label namingFunction(BitSet bits, int dimentions, int len) {
        if (bits.get(len - dimentions) == bits.get(len)) {
            // Unset last bit
            bits.clear(len);

            return namingFunction(bits, dimentions, len - 1);
        } else {
            return new Label(bits.toByteArray());
        }
    }

    public static Label nextNamingFunction(Label label, int treeLenght) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param labels
     * @return
     */
    public static Label lowestCommonAncestor(Label... labels) {
        if (labels.length < 2) return labels[0];

        BitSet prefix = lowestCommonAncestor(labels[0].getBitSet(), labels[1].getBitSet());

        // TODO optimize this loop!
        for (int i = 3; i < labels.length; i++) {
            prefix = lowestCommonAncestor(prefix, labels[i].getBitSet());
        }

        return new Label(prefix.toByteArray());
    }

    /**
     *
     * @param label1
     * @param label2
     * @return
     */
    public static BitSet lowestCommonAncestor(BitSet label1, BitSet label2) {
        BitSet xor = (BitSet) label1.clone();

        // Label1 XOR label2
        // xor[i] = 1 <==> label1[i] != label2[i]
        xor.xor(label2);

        // Return the prefix of one of the two parameter labels
        // Length of the prefix: Min(label1.len, label2.len, last_common_bit.pos)
        return label1.get(0, Integer.min(Integer.min(label1.length(), label2.length()), xor.nextSetBit(0)-1));
    }

    public static Range interval() {
        return interval(Range.REPRESENTABLE_RANGE);
    }

    // TODO : rename maximum_range!!
    public static Range interval(Range maximum_range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isRight(){
        return true;
    }
    public boolean isLeft(){
        return false;
    }

    public Label childToLeft() {
        return new Label(this.label + "0");
    }

    public Label childToRight(){
        return new Label(this.label + "1");
    }

    private byte[] getBytes() {
        return this.label.getBytes();
    }

    private BitSet getBitSet() {
        return BitSet.valueOf(this.label.getBytes());
    }
}
