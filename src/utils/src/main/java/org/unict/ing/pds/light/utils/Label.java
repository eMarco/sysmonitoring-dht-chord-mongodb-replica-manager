/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import java.util.Collection;
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

    public static Label namingFunction(Label label) {
        return Label.namingFunction(label, 1);
    }

    public Label nextNamingFunction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Label namingFunction(Label label, int dimentions) {
        byte[] bytes = label.getBytes();

//        int len = bytes.length;

//        int bits = len * BYTE_LEN;

        BitSet bits = BitSet.valueOf(bytes);
        return namingFunction(bits, dimentions, bits.length());
    }

    public static Label namingFunction(BitSet bits, int dimentions, int len) {
        if (bits.get(len - dimentions) == bits.get(len)) {
            // Unset last bit
            bits.clear(len);
//            bits.set(len, false);

            return namingFunction(bits, dimentions, len - 1);
        } else {
            return new Label(new String(bits.toByteArray()));
        }
    }

//    public static Label namingFunction(byte[] bytes, int dimentions, int len) {
//        if (getBit(bytes, (len-dimentions)) == getBit(bytes, len)) {
//
//            // Remove byte?
//            int byteN = len % BYTE_LEN;
//            // bit len % BYTE_LEN
//            bytes[byteN] = 0;
//
//            return namingFunction(bytes, dimentions, len-1);
//        }
//        else return new Label(new String(bytes));
//    }

    public static Label lowestCommonAncestor(Label... labels) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Range interval() {
        return interval(Range.REPRESENTABLE_RANGE);
    }


//
//    private static final short BYTE_LEN = 8;

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

    private byte[] getBytes() {
        return this.label.getBytes();
    }

//    private static byte getBit(byte[] fromBytes, int position) {
//        int byteN = position % BYTE_LEN;
//
//        return getBit(fromBytes[byteN], (short) (position % BYTE_LEN));
//    }
//
//    private static byte getBit(byte fromByte, short position) {
//        // Todo throw new exception?
//        if (position < 0 || position > 7) return (byte) 0;
//
//        return (byte) ((fromByte >> position) & 1);
//    }
//
//    private static byte setBit(byte fromByte, short position, byte value) {
//        // Todo throw new exception?
//        if (position < 0 || position > 7) return (byte) 0;
//
//        return (byte) ((fromByte >> position) & 1);
//    }
}
