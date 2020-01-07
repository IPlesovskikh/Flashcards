package flashcards;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logging {
    private ArrayList<String> log = new ArrayList<>();

    public void saveData(String data){
        log.add(data);
    }
    public void exportData(String path) throws IOException {
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        for (String line : log) {
            writer.write(line + "\n");
        }
        writer.close();
    }

}
