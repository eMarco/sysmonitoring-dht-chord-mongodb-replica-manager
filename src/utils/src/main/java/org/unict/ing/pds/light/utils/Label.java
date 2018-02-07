/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class Label {
    private final BitSet label;
    private int length = -1;

    /**
     * Returns the corresponding label given the "binary" label.
     * @param label
     */
    public Label(String label) {
        if (label.charAt(0) != '#') throw new IllegalArgumentException("Input label malformed: no # found at the beginning of the input.");

        int labelLength = label.length() - 1;
        BitSet labelBits = new BitSet(labelLength);

        for (int i = 1; i < label.length(); i++) {
            switch (label.charAt(i)) {
                case '0':
                    // TODO: unnecessary clear (new BitSets have no bit set)
                    labelBits.clear(i-1);
                    break;

                case '1':
                    labelBits.set(i-1);
                    break;
                default:
                    throw new IllegalArgumentException("Input label malformed: found char != (0 | 1) ");
            }
        }

        this.label = labelBits;
        this.length = labelLength;
    }

    private Label(String label,
            int length) {
        this(BitSet.valueOf(label.getBytes()), length);
    }

    private Label(BitSet bits, int length) {
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
     * @param length
     * @param value
     * @return
     */
    public static Label prefix(int length, long value) {
        // '#' is not a bit!
        length -= 1;

        if (length < 0) throw new IllegalArgumentException("Prefix cannot be null!");

        BitSet labelBits = new BitSet(length);

        long lower, upper, mid;

        lower = Range.REPRESENTABLE_RANGE.getLower();
        upper = Range.REPRESENTABLE_RANGE.getUpper();

        // Root contains [lower, upper]
        labelBits.clear(0);

        for (int i = 1; i < length; i++) {
            mid = lower + (upper - lower) / 2;

            System.out.println("MID: " + mid);

            if (value < mid) {
                labelBits.clear(i);

                upper = mid;
            }
            else {
                labelBits.set(i);

                lower = mid;
            }
        }

        return new Label(labelBits, length);
    }

    public int getLength() {
        return this.length + 1;
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
     * @param treeLength
     * @param prefixLength
     * @return
     */
    public Label nextNamingFunction(int treeLength, int prefixLength) {
        return Label.nextNamingFunction(this, prefixLength, treeLength);
    }

    public Range interval() {
        return Label.interval(this);
    }

    /**
     * Returns true if label ends by 1 bit, which means this is the child node on the right
     * @return boolean
     */
    @JsonIgnore
    public boolean isRight() {
        return (this.label.get(this.length-1) == true);
    }

    /**
     * Returns true if label ends by 0 bit, which means this is the child node on the left
     * @return boolean
     */
    @JsonIgnore
    public boolean isLeft(){
        return (this.label.get(this.length-1) == false);
    }

    public Label leftChild() {
        BitSet labelBits = this.getBitSet();
        int newLength = this.length + 1;

        BitSet newLabelBits = new BitSet(newLength);
        newLabelBits.xor(labelBits);

        // this + "0"
        newLabelBits.clear(newLength-1);

        return new Label(newLabelBits, newLength);
    }

    public Label rightChild(){
        BitSet labelBits = this.getBitSet();
        int newLength = this.length + 1;

        BitSet newLabelBits = new BitSet(newLength);
        newLabelBits.xor(labelBits);

        // this + "1"
        newLabelBits.set(newLength-1);

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

    private static Label namingFunction(BitSet bits, int dimentions, int len) {
        if (len <= dimentions) {
            return new Label("#");
        }
        else if (bits.get(len -1 - dimentions) == bits.get(len-1)) {
            // Unset last bit
            bits.clear(len);
            return namingFunction(bits, dimentions, len - 1);
        } else {
            return new Label(bits.get(0, len-1), len-1);
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
     * @param treeLength
     * @return
     */
    public static Label nextNamingFunction(Label label, int prefixLength, int treeLength) {
        BitSet labelBits = label.getBitSet();

        int firstDifferentBit;

        // '#' is not a bit
        prefixLength -= 1;
        if (prefixLength < 0) throw new IllegalArgumentException("Prefix cannot be null!");

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
        for (int i = 2; i < labels.length; i++) {
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

        // get firstDifferentBit
        int prefixLen = xor.nextSetBit(0);
        if (prefixLen < 0) {
            // labels are identical
            return label1.length()-1;
        }

        // Return the prefix of one of the two parameter labels
        // Length of the prefix: Min(label1.len, label2.len, last_common_bit.pos)
        return Integer.min(Integer.min(label1.length(), label2.length()), (prefixLen > 0) ? prefixLen-1 : 0);
    }

    /**
     * Among all branch nodes (i.e., the second children of the ancestors) in the local tree, there exist one or more whose
     * regions overlap the query range. This function returns all those nodes.
     * @param label
     * @param region
     * @return
     */
    public static Set<Label> branchNodesBetweenLabels(Label label, Label region) {
        int lcaLength = lowestCommonAncestor(label, region).length;

        if (lcaLength == label.length) return null;

        BitSet labelBits = label.getBitSet();

        Set<Label> buffer = new HashSet<>();
        Set<Label> ret = new HashSet<>();

        buffer.add(label.leftChild());
        buffer.add(label.rightChild());

        BitSet lBits;
        int i;
        for (Label l : buffer) {
            lBits = l.getBitSet();
            i = l.length - 1;

            if (lBits.get(i) != labelBits.get(i))  {
                ret.add(l);
            }

            if (i < label.length) {
                buffer.add(l.leftChild());
                buffer.add(l.rightChild());
            }

            buffer.remove(l);
        }

        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Label other = (Label) obj;

        if (!this.label.get(0, this.length).equals(other.label.get(0, other.length))) {
            return false;
        }

        return true;
    }
}
