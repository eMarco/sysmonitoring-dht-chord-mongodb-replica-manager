/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import org.unict.ing.pds.dhtdb.utils.dht.Key;
import java.util.BitSet;
import java.util.Set;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class Label {
    private final BitSet label;
    private int length = -1;

    public Label(String label, int length) {
        this(BitSet.valueOf(label.getBytes()), length);
    }

    public Label(BitSet bits, int length) {
        // TODO remove clone?
        this.label = (BitSet) bits.clone();
        this.length = length;
    }

    public String getLabel() {
        return this.toString();
    }

    /**
     * Given the value, returns the prefix label with the specified length.
     * The label is obtained by iteratively dividing the subranges of the narrowest representable range
     * in two and appending the 0 bit if the value is lower than mid, 1 otherwise.
     * @param lenght
     * @param value
     * @return
     */
    public static Label prefix(int lenght, long value) {
        BitSet labelBits = new BitSet(lenght);

        long lower, upper, mid;

        lower = Range.REPRESENTABLE_RANGE.getLower();
        upper = Range.REPRESENTABLE_RANGE.getUpper();

        for (int i = 0; i < lenght; i++) {
            mid = (upper - lower) / 2;

            if (value < mid) {
                labelBits.clear(i);

                upper = mid;
            }
            else {
                labelBits.set(i);

                lower = mid;
            }
        }

        return new Label(labelBits, lenght);
    }

    public int getLength() {
        return this.length;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("#");

        BitSet labelBits = this.getBitSet();

        for (int i = 0; i < this.length; i++) {
            ret.append((labelBits.get(i) == true) ? '1' : '0');
        }

        return ret.toString();
    }

    public Label toDHTKey() {
        // TODO add #??
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

    /**
     *
     * @param treeLenght
     * @param prefixLenght
     * @return
     */
    public Label nextNamingFunction(int treeLenght, int prefixLenght) {
        return Label.nextNamingFunction(this, prefixLenght, treeLenght);
    }

    public Range interval() {
        return Label.interval(this);
    }

    /**
     * Returns true if label ends by 0 bit, which means this is the child node on the right
     * @return boolean
     */
    public boolean isRight() {
        return (this.label.get(this.length) == false);
    }

    /**
     * Returns true if label ends by 1 bit, which means this is the child node on the left
     * @return boolean
     */
    public boolean isLeft(){
        return (this.label.get(this.length) == true);
    }

    public Label leftChild() {
        BitSet labelBits = this.getBitSet();
        int newLength = this.length + 1;

        BitSet newLabelBits = new BitSet(newLength);
        newLabelBits.xor(labelBits);

        // this + "0"
        newLabelBits.clear(newLength);

        return new Label(newLabelBits, newLength);
    }

    public Label rightChild(){
        BitSet labelBits = this.getBitSet();
        int newLength = this.length + 1;

        BitSet newLabelBits = new BitSet(newLength);
        newLabelBits.xor(labelBits);

        // this + "1"
        newLabelBits.set(newLength);

        return new Label(newLabelBits, newLength);
    }

    private BitSet getBitSet() {
        return (BitSet) label.clone();
    }

    /**
     * Dual of prefix. Given a label, this function returns the range it represents.
     * @param label
     * @return
     */
    public static Range interval(Label label) {
        BitSet labelBits = label.getBitSet();

        long lower, upper, mid;
        Boolean lowerIncluded, upperIncluded;

        lower = Range.REPRESENTABLE_RANGE.getLower();
        upper = Range.REPRESENTABLE_RANGE.getUpper();
        lowerIncluded = upperIncluded = true;

        for (int i = 0; i < labelBits.length(); i++) {
            mid = (upper - lower) / 2;

            // label[i] = 0
            if (labelBits.get(i) == false) {
                upper = mid;
                upperIncluded = false;
            }
            // label[i] = 1
            else {
                lower = mid;
//                lowerIncluded = true;
            }
        }

        return new Range(lower, lowerIncluded, upper, upperIncluded);
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
        BitSet bits = label.getBitSet();

        return namingFunction(bits, dimentions, bits.length());
    }

    public static Label namingFunction(BitSet bits, int dimentions, int len) {
        if (bits.get(len - dimentions) == bits.get(len)) {
            // Unset last bit
            bits.clear(len);

            return namingFunction(bits, dimentions, len - 1);
        } else {
            return new Label(bits, len);
        }
    }

    /**
     * Γ(μ) is the set of possible prefixes of μ
     * Γ(μ, D) set of possibile prefixes with maximum length D
     *
     * Notation: μ = label, x = prefix
     *
     * Locates the first bit in the suffix of μ (with respect to x) that differs from x’s ending bit; the value
     * nextNamingFunction(μ, x) is then the prefix of μ, which ends up with this located bit.
     *
     * fnn(x, μ) =
     *              p00∗1 ∈ Γ(μ) if x = p0,
     *              p11∗0 ∈ Γ(μ) if x = p1.
     *
     * Intuitively, fnn locates the first bit in the suffix of μ (with respect to x) that differs from x’s ending bit;
     * the value nextNamingFunction(μ, x) is then the prefix of μ, which ends up with this located bit.
     * @param label
     * @param prefixLength
     * @param treeLenght
     * @return
     */
    public static Label nextNamingFunction(Label label, int prefixLength, int treeLenght) {
        BitSet labelBits = label.getBitSet();

        int firstDifferentBit;

        // If prefix's last bit is 0 ==> p00∗1 (look for the first 1 bit)
        if (labelBits.get(prefixLength) == false) {
            firstDifferentBit = labelBits.nextSetBit(prefixLength);
        }
        // If prefix's last bit is 1 ==> p11∗0 (look for the first 0 bit)
        else {
            firstDifferentBit = labelBits.nextClearBit(prefixLength);
        }

        if (firstDifferentBit == -1) return null;

        return new Label(labelBits.get(0, firstDifferentBit), firstDifferentBit);
    }

    /**
     *
     * @param labels
     * @return
     */
    public static Label lowestCommonAncestor(Label... labels) {
        if (labels.length < 2) return labels[0];

        int prefixLength = lowestCommonAncestor(labels[0].getBitSet(), labels[1].getBitSet());

        // TODO optimize this loop!
        for (int i = 3; i < labels.length; i++) {
            prefixLength = lowestCommonAncestor(labels[0].getBitSet(), labels[i].getBitSet());
        }

        return new Label(labels[0].getBitSet().get(0, prefixLength), prefixLength);
    }

    /**
     *
     * @param label1
     * @param label2
     * @return
     */
    public static int lowestCommonAncestor(BitSet label1, BitSet label2) {
        BitSet xor = (BitSet) label1.clone();

        // Label1 XOR label2
        // xor[i] = 1 <==> label1[i] != label2[i]
        xor.xor(label2);

        // Return the prefix of one of the two parameter labels
        // Length of the prefix: Min(label1.len, label2.len, last_common_bit.pos)
        return Integer.min(Integer.min(label1.length(), label2.length()), xor.nextSetBit(0)-1);
    }



    public static Set<Label> branchNodesBetweenLabels(Label label1, Label label2) {
       return null;
    }
}
