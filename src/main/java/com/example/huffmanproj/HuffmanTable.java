package com.example.huffmanproj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.PriorityQueue;
public class HuffmanTable {

    public HuffmanTable(){}
    public static HashMap<Byte, Integer> countFrequency1(String fileName) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(fileName));

        // Count the frequencies of the characters in a single pass
        HashMap<Byte, Integer> frequencyTable = new HashMap<>();
        for (byte b : data) {
            frequencyTable.put(b, frequencyTable.getOrDefault(b, 0) + 1);
        }

        return frequencyTable;
    }

    public static huff buildHuffmanTree(HashMap<Byte, Integer> frequencyTable) {
        // Create a priority queue to hold the Huffman nodes
        PriorityQueue<huff> pq = new PriorityQueue<huff>();

        // Add a Huffman node for each character in the frequency table
        for (HashMap.Entry<Byte, Integer> entry : frequencyTable.entrySet()) {
            pq.add(new huff(entry.getKey(), entry.getValue()));
        }

        // Continuously remove the two lowest frequency nodes and combine them into a new node
        // until there is only one node left in the priority queue
        while (pq.size() > 1) {
            huff left = pq.poll();
            huff right = pq.poll();
            int sum = left.getFrequency() + right.getFrequency();
            pq.add(new huff((byte)0, sum, left, right));
        }

        // The remaining node is the root of the Huffman Tree
        return pq.poll();
    }


    public static HashMap<Byte, String> createEncodingTable(huff root) {
        HashMap<Byte, String> encodingTable = new HashMap<>();
        //       root.buildEncodingTable(encodingTable, "");
        root.buildEncodingTable(encodingTable, "");
        return encodingTable;
    }



}
