module com.example.huffmanproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.huffmanproj to javafx.fxml;
    exports com.example.huffmanproj;
}