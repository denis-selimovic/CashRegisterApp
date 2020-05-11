package ba.unsa.etf.si.utility.javafx;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.persistance.repository.CashRegisterRepository;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class DirectoryChooserWrapper {

    private static final CashRegisterRepository repository = new CashRegisterRepository();

    private DirectoryChooserWrapper() {}

    private static DirectoryChooser createDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(Paths.get(".").toFile());
        return directoryChooser;
    }

    public static void loadReceiptPath(String title) {
        File directory = createDirectoryChooser(title).showDialog(new Stage());
        if(directory != null) {
            App.cashRegister.setReceiptPath(directory.getAbsolutePath());
            repository.update(App.cashRegister);
        }
    }

    public static void loadReportPath(String title) {
        File directory = createDirectoryChooser(title).showDialog(new Stage());
        if(directory != null) {
            App.cashRegister.setReportPath(directory.getAbsolutePath());
            repository.update(App.cashRegister);
        }
    }
}
