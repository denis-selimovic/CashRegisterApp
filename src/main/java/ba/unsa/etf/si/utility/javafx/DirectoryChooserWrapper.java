package ba.unsa.etf.si.utility.javafx;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class DirectoryChooserWrapper {

    private DirectoryChooserWrapper() {}

    private static DirectoryChooser createDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(Paths.get(".").toFile());
        return directoryChooser;
    }

    public static String loadPath(String path, String title) {
        File directory = createDirectoryChooser(title).showDialog(new Stage());
        if(directory != null) path = directory.getAbsolutePath();
        return path;
    }
}
