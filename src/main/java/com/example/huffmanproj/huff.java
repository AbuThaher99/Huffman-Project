package com.example.huffmanproj;




import java.util.HashMap;
import java.util.Map;

public class huff implements Comparable<huff> {
    private byte data;
    private int frequency;
    private huff left;
    private huff right;

    public huff() {

    }

    public huff( int frequency, huff left, huff right) {

        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public huff(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }
    public huff(byte data, int frequency, huff left, huff right){
        this.data = data;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public huff(byte data) {
        this.data = data;
    }


    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }
    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public huff getLeft() {
        return left;
    }

    public void setLeft(huff left) {
        this.left = left;
    }

    public huff getRight() {
        return right;
    }

    public void setRight(huff right) {
        this.right = right;
    }
    //to String method

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, "", this);
        return sb.toString();
    }
    public boolean isLeaf() {
        return left == null && right == null;
    }

    private void toStringHelper(StringBuilder sb, String indent, huff node) {
        if (node == null) {
            sb.append(indent + "null\n");
            return;
        }
        sb.append(indent + node.getData() + " (" + node.frequency + ")\n");
        toStringHelper(sb, indent + "  ", node.left);
        toStringHelper(sb, indent + "  ", node.right);
    }

    @Override
    public int compareTo(huff o) {
        return this.frequency - o.frequency;

    }



    public void buildEncodingTable(HashMap<Byte, String> encodingTable, String s) {
        if (this.left != null) {
            this.left.buildEncodingTable(encodingTable, s + "0");
        }
        if (this.right != null) {
            this.right.buildEncodingTable(encodingTable, s + "1");
        }
        if (this.isLeaf()) {
            encodingTable.put(this.data, s);
        }
    }


}
