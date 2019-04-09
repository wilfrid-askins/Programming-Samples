import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Demonstrates reading a csv file using Java 8's Stream API.
 *
 * @author Wilfrid Askins
 */
public class FileReader {

    /**
     * The program's main method.
     * @param args unused
     */
    public static void main(String[] args) {

        // The file to read from
        var file = new File("test.csv");
        // Store the entries here
        var entries = new HashSet<>();

        // Read from file
        read(entries, file, Entry::valueOf);
        // Print all entries
        entries.forEach(System.out::println);
    }

    /**
     * Reads a file line by line, converting each line to an object, then puts the objects into a list.
     *
     * This method is being listed here because it's extremely useful and an example of many different Java concepts.
     *
     * @param <T> the generic type of the objects to be created
     * @param set the set to be loaded into
     * @param file the file to be loaded from
     * @param readable the function to convert a line to an instance of T
     */
    private static <T> void read(Set<T> set, File file, Function<String,T> readable){

        try{

            Files.lines(file.toPath()) // read by line
                    .map(readable::apply) // convert to instances of T
                    .forEach(set::add); // add to the set

        }catch (IOException e) { // If reading the file failed
            e.printStackTrace(); // Print an exception
        }
    }

    /**
     * An example class which holds a username and password.
     * This isn't important and is just to show the read method in action.
     *
     * @author Wilfrid Askins
     */
    static class Entry {

        /** The username and password */
        private String user, pass;

        public Entry(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "user='" + user + '\'' +
                    ", pass='" + pass + '\'' +
                    '}';
        }

        public static Entry valueOf(String text) {
            var parts = text.split(",");
            return new Entry(parts[0], parts[1]);
        }
    }
}
