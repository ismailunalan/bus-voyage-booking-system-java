import java.util.*;

/**
 * The main class responsible for executing the program.
 */
public class BookingSystem {

    /**
     * The main method of the program.
     *
     * @param args Command line arguments. The first argument should be the input file and the second argument should be the output file.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("ERROR: This program works exactly with two command line arguments," +
                    " the first one is the path to the input file whereas the second one is the path" +
                    " to the output file. Sample usage can be as follows:" +
                    " \"java8 BookingSystem input.txt output.txt\". Program is going to terminat");
            System.exit(1);
        }
        BoxOffice boxOffice = new BoxOffice();
        List<Voyage> voyageList = new ArrayList<>();
        Voyage voyage = new Voyage();
        String[] lines = FileInput.readFile(args[0], true, true);
        FileOutput.main(args);
        FileOutput.writeToFile("",false,false);
        if (lines.length == 0){
            FileOutput.writeToFile("Z Report:\n----------------",true,true);
            boxOffice.zReport(voyageList,true);
            System.exit(1);
        }
        //Start the program.
        voyage.inputReader(boxOffice, voyageList, lines);
        //Sorts voyages in voyageList by VoyageID
        Collections.sort(voyageList, new Comparator<Voyage>() {
            @Override
            public int compare(Voyage v1, Voyage v2) {
                return Integer.compare(v1.getVoyageID(), v2.getVoyageID());
            }
        });
        if (!(lines[lines.length - 1].equals("Z_REPORT"))){
            FileOutput.writeToFile("Z Report:\n----------------",true,true);
            boxOffice.zReport(voyageList,true);
        }
    }

    /**
     * Formats the given number.
     *
     * @param number The number to be formatted.
     * @return The formatted number.
     */
    public static String formatNumber(double number) {
        String value;
        if (number == (int) number) {
            value = String.valueOf((int) number);
        } else {
            value = String.format("%.2f",number);
        }
        return value;
    }
}