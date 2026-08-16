module com.example.targetdatefundappjava {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.targetdatefundappjava to javafx.fxml;
    exports com.example.targetdatefundappjava;
}