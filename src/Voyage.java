import java.util.List;

/**
 * Voyage class represents a voyage with its properties and methods for managing voyage-related operations.
 */
public class Voyage {
    private int voyageID;
    private String fromWhere;
    private String toWhere;
    private float seatFee;
    private float revenue;
    private int row;

    public Voyage() {
    }

    /**
     * Constructor for Voyage class with specified parameters.
     *
     * @param voyageID   The ID of the voyage.
     * @param fromWhere  The starting point of the voyage.
     * @param toWhere    The destination of the voyage.
     * @param seatFee    The fee for each seat on the voyage.
     * @param row        The number of rows of the voyage.
     */
    public Voyage(int voyageID, String fromWhere, String toWhere,float seatFee, int row) {
        this.voyageID = voyageID;
        this.fromWhere = fromWhere;
        this.toWhere = toWhere;
        this.seatFee = seatFee;
        this.row = row;
    }

    /**
     * Reads and processes input lines to execute commands related to voyages.
     *
     * @param boxOffice   The BoxOffice object managing voyage operations.
     * @param voyageList  The list of voyages to operate on.
     * @param lines       The array of input lines containing commands.
     */
    public void inputReader(BoxOffice boxOffice, List<Voyage> voyageList, String[] lines){
        //The program reads inputs line by line.
        int i = 0;
        boolean lastLine = false;
        for (String line : lines) {
            i++;
            if (i == lines.length)
                lastLine = true;
            FileOutput.writeToFile("COMMAND: " + line,true,true);
            String[] infos = line.split("\t");
            String lineType = infos[0];
            try {
                //It determines the methods to be executed based on the types of commands.
                switch (lineType) {
                    case "INIT_VOYAGE":
                        //If any errors exist, it catches them; otherwise, it performs the initialization voyage operation.
                        try {
                            int voyageID = Integer.parseInt(infos[2]);
                            if (voyageID < 0){
                                FileOutput.writeToFile("ERROR: " + voyageID + " is not a positive integer, ID of a voyage must be a positive integer!",true,true);
                                break;
                            }
                            try {
                                //If the voyage ID already exists, it throws an error.
                                for (Voyage voyage : voyageList){
                                    if (voyage.getVoyageID() == voyageID){
                                        FileOutput.writeToFile("ERROR: There is already a voyage with ID of " + voyageID + "!",true,true);
                                        throw new RuntimeException();
                                    }
                                }
                            }catch (RuntimeException e){
                                break;
                            }
                            String fromWhere = infos[3];
                            String toWhere = infos[4];
                            int row = Integer.parseInt(infos[5]);
                            if (row < 0){
                                FileOutput.writeToFile("ERROR: " + row + " is not a positive integer, number of seat rows of a voyage must be a positive integer!",true,true);
                                break;
                            }
                            float seatFee = Float.parseFloat(infos[6]);
                            if (seatFee < 0){
                                FileOutput.writeToFile("ERROR: " + BookingSystem.formatNumber(seatFee) + " is not a positive number, price must be a positive number!",true,true);
                                break;
                            }
                            float premiumFee = 0, refundCut = 0;
                            //If they exist, it reads the premium fee and refund cut values.
                            if (infos.length > 8) {
                                premiumFee = Float.parseFloat(infos[8]);
                                if (premiumFee < 0){
                                    FileOutput.writeToFile("ERROR: " + BookingSystem.formatNumber(premiumFee) + " is not a non-negative integer, premium fee must be a non-negative integer!",true,true);
                                    break;
                                }
                                refundCut = Float.parseFloat(infos[7]);
                                if (refundCut < 0 || refundCut > 100){
                                    FileOutput.writeToFile("ERROR: " + BookingSystem.formatNumber(refundCut) + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!",true,true);
                                    break;
                                }
                            } else if (infos.length > 7) {
                                refundCut = Float.parseFloat(infos[7]);
                                if (refundCut < 0 || refundCut > 100){
                                    FileOutput.writeToFile("ERROR: " + BookingSystem.formatNumber(refundCut) + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!",true,true);
                                    break;
                                }                            }
                            boxOffice.initVoyage(voyageID, fromWhere, toWhere, seatFee, row, refundCut, premiumFee, infos, voyageList);
                            break;
                        }catch (ArrayIndexOutOfBoundsException e){
                            FileOutput.writeToFile("ERROR: Erroneous usage of \"INIT_VOYAGEf\" command!",true,true);
                            break;
                        }
                    case "Z_REPORT":
                        //If any errors exist, it catches them; otherwise, it performs the Z_REPORT operation.
                        try {
                            try {
                                if (infos.length > 1)
                                    throw new ArrayIndexOutOfBoundsException();
                                FileOutput.writeToFile("Z Report:\n----------------",true,true);
                                boxOffice.zReport(voyageList, lastLine);
                                break;
                            }catch (ArrayIndexOutOfBoundsException e){
                                FileOutput.writeToFile("ERROR: Erroneous usage of \"Z_REPORT\" command!",true,true);
                                break;
                            }
                        }catch (NullPointerException e){
                            break;
                        }
                    case "SELL_TICKET":
                        //If any errors exist, it catches them; otherwise, it performs the SELL_TICKET operation.
                        try {
                            if (infos.length != 3) {
                                throw new ArrayIndexOutOfBoundsException();
                            }
                            String seats = infos[2];
                            boolean found = false;
                            voyageID = Integer.parseInt(infos[1]);
                            for (Voyage voyage : voyageList){
                                if (voyage.getVoyageID() == voyageID){
                                    found = true;
                                }
                            }
                            if (!found){
                                FileOutput.writeToFile("ERROR: There is no voyage with ID of " + voyageID + "!",true,true);
                                break;
                            }
                            boxOffice.sellTicket(voyageList, voyageID, seats);
                            break;
                        }catch (ArrayIndexOutOfBoundsException e) {
                            FileOutput.writeToFile("ERROR: Erroneous usage of \"SELL_TICKET\" command!", true, true);
                            break;
                        }
                    case "CANCEL_VOYAGE":
                        //If any errors exist, it catches them; otherwise, it performs the CANCEL_VOYAGE operation.
                        try {
                            boolean found = false;
                            voyageID = Integer.parseInt(infos[1]);
                            if (infos.length > 2)
                                throw new ArrayIndexOutOfBoundsException();
                            if (voyageID < 0){
                                FileOutput.writeToFile("ERROR: " + voyageID  +" is not a positive integer, ID of a voyage must be a positive integer!",true,true);
                                break;
                            }

                            for (Voyage voyage : voyageList){
                                if (voyage.getVoyageID() == voyageID){
                                    found = true;
                                }
                            }
                            if (!found){
                                FileOutput.writeToFile("ERROR: There is no voyage with ID of " + voyageID + "!",true,true);
                                break;
                            }
                            boxOffice.cancelVoyage(voyageList, voyageID);
                            break;
                        }catch (ArrayIndexOutOfBoundsException e){
                            FileOutput.writeToFile("ERROR: Erroneous usage of \"CANCEL_VOYAGE\" command!", true, true);
                            break;
                        }
                    case "PRINT_VOYAGE":
                        //If any errors exist, it catches them; otherwise, it performs the PRINT_VOYAGE operation.
                        boolean found = false;
                        try {
                            voyageID = Integer.parseInt(infos[1]);
                            if (voyageID < 0){
                                FileOutput.writeToFile("ERROR: " + voyageID + " is not a positive integer, ID of a voyage must be a positive integer!",true,true);
                                break;
                            }
                            for (Voyage voyage : voyageList) {
                                if (voyage.getVoyageID() == voyageID) {
                                    found = true;
                                    boxOffice.voyagePrinter(voyageID, voyage);
                                }
                            }
                            if (!found){
                                FileOutput.writeToFile("ERROR: There is no voyage with ID of " + voyageID + "!",true,true);
                            }
                            break;
                        }catch (ArrayIndexOutOfBoundsException e){
                            FileOutput.writeToFile("ERROR: Erroneous usage of \"PRINT_VOYAGE\" command!", true, true);
                            break;
                        }
                    case "REFUND_TICKET":
                        //If any errors exist, it catches them; otherwise, it performs the REFUND_TICKET operation.
                        try {
                            voyageID = Integer.parseInt(infos[1]);
                            String seats = infos[2];
                            boxOffice.refundTicket(voyageList, voyageID, seats);
                            break;
                        }catch (ArrayIndexOutOfBoundsException e) {
                                FileOutput.writeToFile("ERROR: Erroneous usage of \"REFUND_TICKET\" command!", true, true);
                                break;
                            }
                    default:
                        //If none of the commands are suitable, it throws an error.
                        throw new NullPointerException() ;
                }
            }catch (NullPointerException e){
                FileOutput.writeToFile("ERROR: There is no command namely " + lineType + "!",true,true);
            }
        }
    }
    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue += revenue;
    }

    public float getSeatFee(int seatNumber) {
        return seatFee;
    }

    public int getVoyageID() {
        return voyageID;
    }
    public List<String> getNumberOfSeats() {
        return null;
    }
    public float getPremiumFee() {
        return 0;
    }
    public float getRefundCut(float seatFee) {
        return 0;
    }

    public String getFromWhere() {
        return fromWhere;
    }

    public String getToWhere() {
        return toWhere;
    }

}
