import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileOutput {
    private static String defaultPath = "o11.txt"; // Default path initially empty

    public static void main(String[] args) {
        if (args.length >= 2) {
            defaultPath = args[1];
        }
    }
    public static void writeToFile(Object content, boolean append, boolean newLine) {
        /**
         * This method writes given content to file at given path.
         *
         * @param path    Path for the file content is going to be written.
         * @param content Content that is going to be written to file.
         * @param append  Append status, true if wanted to append to file if it exists, false if wanted to create file from zero.
         * @param newLine True if wanted to append a new line after content, false if vice versa.
         */
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(defaultPath, append));
            ps.print(content + (newLine ? "\n" : ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { //Flushes all the content and closes the stream if it has been successfully created.
                ps.flush();
                ps.close();
            }
        }
    }
}