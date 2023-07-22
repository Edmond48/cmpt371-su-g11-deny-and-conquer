module com.vng_eleven.deny_and_conquer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.vng_eleven.deny_and_conquer to javafx.fxml;
    exports com.vng_eleven.deny_and_conquer;
}