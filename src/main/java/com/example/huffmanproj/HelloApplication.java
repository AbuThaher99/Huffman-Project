package com.example.huffmanproj;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {
    byte[] treeBytes;
    int index = 0;
    File f = null;

    byte[] fileBytes = null;
    short treeSizeLastIndex = 0;
    int arrPosition = 0;
    String path = "";

    File f1 = null;
    String path1 = "";
    static String header="";
    static String statics="";
    TextArea textArea = new TextArea();

    static ObservableList<Encoding> EncodingItems = FXCollections.observableArrayList();


    public static void writeSize(String inputFileName, String outputFileName) throws IOException {
        File file = new File(inputFileName);
        long originalSize = file.length();
        File file2 = new File(outputFileName);
        long compressedSize = file2.length();
        double ratio = (double) compressedSize / originalSize*100;
        statics = "The size of the original file is " + originalSize + " bytes.\n";
        statics += "The size of the compressed file is " + compressedSize + " bytes.\n";
        statics += "The compression ratio is %" + ratio + ".\n";

    }



    public void compression(Stage stage ,String getname ,String exe ,String parnt) throws IOException {


        String fileName = getname;
        String extention = exe;
        byte extentionLength = (byte) extention.length();
        File file = new File(path);
        long[] freq = new long[256];
        byte[] fileBytes = null;
        try {
            // read all bytes in the file
            fileBytes = Files.readAllBytes(file.toPath());
            // count the chars
            for (int i = 0; i < fileBytes.length; i++) {
                if (fileBytes[i] < 0) {
                    short tempNum = (short) (fileBytes[i] + 256);
                    freq[tempNum]++;
                } else {
                    freq[fileBytes[i]]++;
                }

            }
        } catch (IOException e1) {
           e1.printStackTrace();
        }

        if (fileBytes != null) {
            PriorityQueue heap = new PriorityQueue();
            HuffmanNode[] huffmanTable = new HuffmanNode[256];

            // make Huffman table and
            // add the nodes to the heap
            short leafs = 0;
            for (short i = 0; i < 256; i++) {
                huffmanTable[i] = new HuffmanNode((char) i, freq[i]);
                if (freq[i] != 0) {
                    heap.Enqueue(huffmanTable[i]);
                    leafs++;
                }
            }

            int n = leafs; // Number of Huffman tree nodes
            // build Huffman tree
            for (short i = 1; i < leafs; i++) {
                HuffmanNode node = new HuffmanNode();
                HuffmanNode left = heap.Dequeue();
                HuffmanNode right = heap.Dequeue();
                node.setRight(right);
                node.setLeft(left);
                node.setFreq(left.getFreq() + right.getFreq());
                heap.Enqueue(node);
                n++;
            }
            // Assign code to each char
            HuffmanTree huffmanTree = new HuffmanTree(heap.Dequeue());
            heap = null;
            huffmanTree.buildCode();

            // Show table
           HuffmanTable hs = new HuffmanTable();
            HashMap<Byte, Integer> frequencyTable = hs.countFrequency1(path);
            huff roots = hs.buildHuffmanTree(frequencyTable);
            Map<Byte, String> encodingTable = hs.createEncodingTable(roots);
            for (HashMap.Entry<Byte, String> entry : encodingTable.entrySet()) {
                for(HashMap.Entry<Byte, Integer> entry1 : frequencyTable.entrySet()) {
                    if(entry.getKey()==entry1.getKey()) {
                        EncodingItems.add(new Encoding(entry.getKey(), entry.getValue(), entry1.getValue(), entry.getValue().length(),(char) entry.getKey().byteValue()));
                    }
                }

            }


            int fileSize = fileBytes.length; // original file size in bytes

            // get the byte array that represent the tree
            short treeBytesSize = (short) ((leafs * 2) + (n - leafs));
            treeBytes = new byte[treeBytesSize];
            index = 0;
            treeBytes(huffmanTree.getRoot());

            // Create the buffer
            ByteBuffer buffer = ByteBuffer.allocate(104857600);

            // Display the header and printint it to the file;
            // Sign

            textArea.appendText("ABU\n");
            buffer.put((byte) 'A');
            buffer.put((byte) 'B');
            buffer.put((byte) 'U');

            // Extention and length
            textArea.appendText(extentionLength + " " + extention + "\n");
            buffer.put(extentionLength);
            for (int i = 0; i < extentionLength; i++) {
                buffer.put((byte) extention.charAt(i));
            }
            textArea.appendText(fileBytes.length + "\n");
            buffer.putInt(fileSize);

            // The tree and it's size
            buffer.putShort(treeBytesSize);
            textArea.appendText(treeBytesSize + "\n");
            String tree = "";
            int test = 0;
            for (int i = 0; i < treeBytesSize; i++) {
                if (treeBytes[i] == 1) {
                    i++;
                    if (treeBytes[i] < 0) {
                        tree = tree + "1" + (char) (treeBytes[i] + 256) + "\n";
                    } else {
                        tree = tree + "1" + (char) treeBytes[i] + "\n";
                    }
                } else {
                    tree = tree + "0 ";
                }
                test = i;
            }
            textArea.appendText(tree);
            buffer.put(treeBytes);



            // creat the outfile and channel to print

            String outFilePath = parnt;

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFilePath);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

            if (fos != null) {
                FileChannel fc = fos.getChannel();

                // Print the header and clear the buffer
                buffer.flip();
                try {
                    fc.write(buffer);
                    buffer.clear();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                String buffCode = "";
                MyConverter convert = new MyConverter();
                for (int i = 0; i < fileBytes.length; i++) {
                    // Get the byte with Huffman Representation
                    String charCode = null;
                    if (fileBytes[i] < 0) {
                        charCode = huffmanTable[fileBytes[i] + 256].huffCode;
                    } else {
                        charCode = huffmanTable[fileBytes[i]].huffCode;
                    }

                    for (int j = 0; j < charCode.length(); j++) {

                        if (buffCode.length() == 8) {
                            if (buffer.position() != buffer.limit()) {
                                buffer.put(convert.binaryToByte(buffCode));
                                buffCode = "";
                            } else {
                                buffer.flip();
                                try {
                                    fc.write(buffer);
                                    buffer.clear();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                buffer.put(convert.binaryToByte(buffCode));
                                buffCode = "";
                            }

                        }
                        buffCode = buffCode + charCode.charAt(j);
                    }
                }

                if (buffCode.length() != 0) {
                    if (buffCode.length() != 8) {
                        int bits = 8 - buffCode.length();
                        for (int i = 0; i < bits; i++) {
                            buffCode = buffCode + "0";
                        }
                    }

                    if (buffer.position() != buffer.limit()) {
                        buffer.put(convert.binaryToByte(buffCode));
                        buffCode = "";
                    } else {
                        try {
                            buffer.flip();
                            fc.write(buffer);
                            buffer.clear();
                            buffer.put(convert.binaryToByte(buffCode));
                            buffer.flip();
                            fc.write(buffer);
                            buffer.clear();
                            buffCode = "";
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                if (buffer.remaining() != 0) {
                    buffer.flip();
                    try {
                        fc.write(buffer);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    buffer.clear();
                }
                writeSize(path,outFilePath);

            }
        }

    }

    private void treeBytes(HuffmanNode root) {
        if (root == null) {
            return;
        }

        if (root.getLeft() == null && root.getRight() == null) {
            treeBytes[index] = 1;
            index++;
            treeBytes[index] = (byte) root.getCh();
            index++;
        } else {
            treeBytes[index] = 0;
            index++;
            treeBytes(root.getLeft());
            treeBytes(root.getRight());
        }

    }




    public void decomprission(Stage stage) throws IOException {
        String sign = "";
        try{


            // read all bytes in the file
            fileBytes = Files.readAllBytes(f1.toPath());
            // Check the signature

            // read the signature
            for (int i = 0; i < 3; i++) {
                if (fileBytes[i] < 0) {
                    char c = (char) (fileBytes[i] + 256);
                    sign = sign + c;
                } else {
                    char c = (char) fileBytes[i];
                    sign = sign + c;
                }
            }

        } catch (Exception e1) {
          e1.printStackTrace();
        }

        if (sign.equals("ABU")) {
            int position = 3;

            // read the extension length
            byte extensionLength;
            position++;
            if (fileBytes[3] < 0) {
                extensionLength = (byte) (fileBytes[3] + 256);
            } else {
                extensionLength = fileBytes[3];
            }

            // The actual size index = 4 + extentionLength
            int actualSizeIndex = position + extensionLength;

            // read the extension
            String extension = "";
            for (int i = position; i < actualSizeIndex; i++) {
                if (fileBytes[i] < 0) {
                    char c = (char) (fileBytes[i] + 256);
                    extension = extension + c;
                } else {
                    char c = (char) fileBytes[i];
                    extension = extension + c;
                }
            }
            position = actualSizeIndex;

            // read the actual size
            String binarySize = "";
            for (int i = actualSizeIndex; i < actualSizeIndex + 4; i++) {
                binarySize = binarySize + MyConverter.byteToBinary(fileBytes[i]);
            }
            position += 4;
            // getting the actual size
            int actualSize = Integer.parseInt(binarySize, 2);

            binarySize = "";
            for (int i = actualSizeIndex + 4; i < actualSizeIndex + 6; i++) {
                binarySize = binarySize + MyConverter.byteToBinary(fileBytes[i]);
            }
            short treeSize = Short.parseShort(binarySize, 2);
            position += 2;
            // Where the Tree begins

            // Rebuildthe tree from the header
            treeSizeLastIndex = (short) (position + treeSize);
            arrPosition = position - 1;
            HuffmanNode root = readTree();
            HuffmanTree tree = new HuffmanTree(root);
            tree.buildCode();

            position = position + treeSize; // Point position to data
//            // Display the table
//            String[] codes = tree.getCodes();
//            tree.setCodes(null);
//            DefaultTableModel model = (DefaultTableModel) MainWindow.table_1.getModel();
//            model.setRowCount(0);
//            for (int i = 0; i < 256; i++) {
//                model.addRow(new Object[] { (char) i, codes[i] });
//
//            }

            // Create the buffer
            ByteBuffer buffer = ByteBuffer.allocate(104857600);

            // creat the outfile and channel to print
            int index = path1.lastIndexOf(".");
        String outputFileName = path1.substring(0, index);

            String outFilePath = outputFileName + "." + extension;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFilePath);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

            if (fos != null) {
                FileChannel fc = fos.getChannel();

                HuffmanNode tempNode = tree.getRoot();
                String tempHuffCode = "";
                String temp = "";
                for (; position < fileBytes.length;position++) {
                    tempHuffCode = MyConverter.byteToBinary(fileBytes[position]);
                    for (int j = 0, i=0; i< actualSize && j < tempHuffCode.length(); j++) {

                        char c = tempHuffCode.charAt(j);
                        temp = temp + c;
                        byte bt;

                        if (c == '1') {
                            tempNode = tempNode.getRight();
                        } else {
                            tempNode = tempNode.getLeft();
                        }

                        if (tempNode.getLeft() == null && tempNode.getRight() == null) {
                            if (buffer.position() == buffer.limit()) {
                                buffer.flip();
                                try {
                                    fc.write(buffer);
                                    buffer.clear();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            buffer.put((byte) tempNode.getCh());
                            i++;
                            temp = "";
                            tempNode = tree.getRoot();
                        }

                    }
                }

                if(buffer.remaining() != 0) {
                    buffer.flip();
                    try {
                        fc.write(buffer);
                        buffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Wrong Entry, Signature don't match", "Signature Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public HuffmanNode readTree() {
        arrPosition++;
        HuffmanNode root = null;
        if (arrPosition >= treeSizeLastIndex - 1) {
            return null;
        }
        if (fileBytes[arrPosition] == 1) {
            if (fileBytes[arrPosition] < 0) {
                root = new HuffmanNode((char) (fileBytes[arrPosition + 1] + 256));
            } else {
                root = new HuffmanNode((char) fileBytes[arrPosition + 1]);
            }
            arrPosition++;
        } else {
            root = new HuffmanNode();
            root.left = readTree(); // 3 5
            root.right = readTree(); // 4 5
        }

        return root;
    }





    @Override

    public void start(Stage stage) throws IOException {







        Image mh8 = new Image("main.jpg");
        ImageView mah8 = new ImageView(mh8);
        mah8.setFitHeight(1050);
        mah8.setFitWidth(1920);

       Pane root = new Pane();



        TableView<Encoding> EncodingTable = new TableView<>();

        TableColumn<Encoding,Byte> data = new TableColumn<>("Ascii");
        data.setPrefWidth(100);
        data.setResizable(false);
        data.setCellValueFactory(new PropertyValueFactory<Encoding, Byte>("data"));

        TableColumn<Encoding, String> Code = new TableColumn<>("code");
        Code.setPrefWidth(100);
        Code.setResizable(false);
        Code.setCellValueFactory(new PropertyValueFactory<Encoding, String>("code"));

        TableColumn<Encoding, Integer> fre = new TableColumn<>("Frequency");
        fre.setPrefWidth(100);
        fre.setResizable(false);
        fre.setCellValueFactory(new PropertyValueFactory<Encoding, Integer>("frequency"));

        TableColumn<Encoding, Integer> length = new TableColumn<>("Length");
        length.setPrefWidth(100);
        length.setResizable(false);
        length.setCellValueFactory(new PropertyValueFactory<Encoding, Integer>("length"));

        TableColumn<Encoding, Character> data1 = new TableColumn<>("Data");
        data1.setPrefWidth(100);
        data1.setResizable(false);
        data1.setCellValueFactory(new PropertyValueFactory<Encoding, Character>("character"));


        EncodingTable.setPrefSize       (500, 235);
        EncodingTable.getColumns().addAll(data,Code,fre,length,data1);
        EncodingTable.setItems(EncodingItems);
        EncodingTable.setLayoutX(14);
        EncodingTable.setLayoutY(175);




          textArea = new TextArea();
        textArea.setPrefWidth(246);
        textArea.setPrefHeight(248);
        textArea.setLayoutX(543);
        textArea.setLayoutY(21);

        TextArea textArea2 = new TextArea();
        textArea2.setPrefWidth(246);
        textArea2.setPrefHeight(248);
        textArea2.setLayoutX(543);
        textArea2.setLayoutY(311);
        Button combut = new Button("Compress");
        combut.setPrefHeight(50);
        combut.setPrefWidth(85);
        combut.setLayoutX(131);
        combut.setLayoutY(476);
        combut.setTextFill(Color.BLACK);
        combut.setFont(Font.font("Oranienbaum", 12));
        combut.setContentDisplay(ContentDisplay.TOP);

        combut.setOnAction(w->{
            try {
                FileChooser chooser = new FileChooser();
                f = chooser.showOpenDialog(stage);
                path = f.getAbsolutePath();
                String getname =f.getName();
                int pos = getname.lastIndexOf(".");
                String exe = "";
                if(pos > 0){
                    exe = getname.substring(pos+1);
                }


                if(exe.equals("huf")){
                    Alert alert = new Alert(Alert.AlertType.NONE, "The File is Already Encoded ", ButtonType.OK);
                    if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    }
                    return;
                }

                EncodingItems.clear();
                EncodingTable.refresh();
                textArea2.clear();
                textArea.clear();
                String getname1 =f.getName();
                int pos1 = getname1.lastIndexOf(".");
                if(pos1 > 0){
                    getname1 = getname1.substring(0,pos1);
                }
                String parnt = f.getParent();
                parnt = parnt+"\\"+getname1+".huf";



                long startTime = System.currentTimeMillis();
                compression(stage,getname,exe,parnt);
              //  textArea.setText(header);
                textArea2.setText(statics);
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                long mints = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);


                Alert alert = new Alert(Alert.AlertType.NONE, "The compressing Process is Done", ButtonType.OK);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }
                Alert alert1 = new Alert(Alert.AlertType.NONE, "The Time while Processing(Mints) :  "+mints +"\n"+"In Millis : "+ elapsedTime, ButtonType.OK);
                if (alert1.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }



            } catch (NullPointerException d){
                Alert alert = new Alert(Alert.AlertType.NONE, "You Must Chose a file. ", ButtonType.OK);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }
                return;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });

        Button decombut = new Button("Decompress");
        decombut.setPrefHeight(50);
        decombut.setPrefWidth(85);
        decombut.setTextFill(Color.BLACK);
        decombut.setFont(Font.font("Oranienbaum", 12));
        decombut.setContentDisplay(ContentDisplay.TOP);
        decombut.setLayoutX(348);
        decombut.setLayoutY(476);
        decombut.setOnAction(e->{
            FileChooser chooser = new FileChooser();
            f1=chooser.showOpenDialog(stage);
            path1 = f1.getAbsolutePath();
            String getname =f1.getName();
            int pos = getname.lastIndexOf(".");
            String exe = "";
            if(pos > 0){
                exe = getname.substring(pos+1);
            }

            System.out.println(exe);
            if(!exe.equals("huf")){
                Alert alert = new Alert(Alert.AlertType.NONE, "The File does not have the Extension huf ", ButtonType.OK);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                }

                return;
            }

            long startTime = System.currentTimeMillis();
            try {
                decomprission(stage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            long mints = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);


            Alert alert = new Alert(Alert.AlertType.NONE, "The decompressing Process is Done", ButtonType.OK);
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            }
            Alert alert1 = new Alert(Alert.AlertType.NONE, "The Time while Processing(Mints) :  "+mints +"\n"+"In Millis : "+ elapsedTime, ButtonType.OK);
            if (alert1.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            }


        });

     root.getChildren().addAll(mah8,combut,decombut,textArea,textArea2,EncodingTable);

        Scene scene = new Scene(root, 800, 573);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}