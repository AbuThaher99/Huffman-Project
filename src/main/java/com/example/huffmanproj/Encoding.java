package com.example.huffmanproj;



public class Encoding {
    private char character;
    private byte data;
    private String code;
    private long frequency;
    private int length;

    public Encoding(byte data,String code, long frequency, int length, char character) {
        this.data = data;
        this.code = code;
        this.frequency = frequency;
        this.length = length;
        this.character= character;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public String toString() {
        return ""+" "+code+" "+frequency+" "+length+"\n";
    }

}
