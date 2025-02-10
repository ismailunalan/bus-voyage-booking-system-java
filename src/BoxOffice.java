import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * BoxOffice class manages ticket sales, refunds, initialization of voyages, cancellation of voyages,
 * and Z_Reports.
 */
public class BoxOffice {
    public BoxOffice() {
    }

    /**
     * Sells tickets for the specified voyage and seats.
     *
     * @param voyageList The list of voyages.
     * @param voyageID   The ID of the voyage.
     * @param seats      The seat numbers to sell, separated by underscores.
     */
    public void sellTicket(List<Voyage> voyageList, int voyageID, String seats) {
        String[] seatNumbers = seats.split("_");
        Voyage voyage1 = null;
        float sellAmount = 0;
        //It tries to find the voyage in the voyageList and if it finds it, it makes the sale.
        for (Voyage voyage : voyageList) {
            if (voyage.getVoyageID() == voyageID) {
                voyage1 = voyage;
                //Checks seat availability
                if (isSeatAvailable(seatNumbers, voyage)) {
                    //Sells seats individually
                    for(String seat : seatNumbers) {
                        voyage.getNumberOfSeats().set(Integer.parseInt(seat) - 1, "X");
                        float price = voyage.getSeatFee(Integer.parseInt(seat));
                        voyage.setRevenue(price);
                        sellAmount += price;
                    }
                }else {
                    return;
                }
            }
        }
        String content = String.format("Seat %s of the Voyage %d from %s to %s was successfully sold for %.2f TL.",seats.replace("_","-"),voyageID,voyage1.getFromWhere(),voyage1.getToWhere(),sellAmount);
        FileOutput.writeToFile(content,true,true);
    }

    /**
     * Prints the details of a voyage, including seat arrangement and revenue.
     *
     * @param voyageID The ID of the voyage.
     * @param voyage   The voyage object.
     */
    public void voyagePrinter(int voyageID, Voyage voyage) {
        FileOutput.writeToFile("Voyage " + voyageID + "\n" + voyage.getFromWhere() + "-" + voyage.getToWhere(), true, true);
        List<String> numberOfRows = voyage.getNumberOfSeats();
        int seatsPerRow = 0;
        int corridor = 0;
        switch (voyage.getClass().getSimpleName()){
            case "Standard":
                seatsPerRow = 4;
                corridor = 1;
                break;
            case "Premium":
                seatsPerRow = 3;
                break;
            case "Minibus":
                seatsPerRow = 2;
                break;
            default:
        }
        //It prints the output according to the seating arrangement.
        for (int i = 0; i < numberOfRows.size(); i += seatsPerRow) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < seatsPerRow && i + j < numberOfRows.size(); j++) {
                rowBuilder.append(numberOfRows.get(i + j));
                if (j < seatsPerRow - 1 && i + j + 1 < numberOfRows.size()) {
                    rowBuilder.append(j == corridor ? seatsPerRow != 2 ? " | " : " " : " ");
                }
            }
            FileOutput.writeToFile(rowBuilder.toString(),true,true);
        }
        FileOutput.writeToFile("Revenue: " + String.format("%.2f",voyage.getRevenue()),true,true);
    }

    /**
     * Refunds tickets for the specified voyage and seats.
     *
     * @param voyageList The list of voyages.
     * @param voyageID   The ID of the voyage.
     * @param seats      The seat numbers to refund, separated by underscores.
     */
    public void refundTicket(List<Voyage> voyageList, int voyageID, String seats) {
        String[] seatNumbers = seats.split("_");
        Voyage a = null ;
        boolean found = false;
        float refundValue = 0;
        for (Voyage voyage : voyageList) {
            String voyageType = voyage.getClass().getSimpleName();
            if (voyage.getVoyageID() == voyageID) {
                found = true;
                a = voyage;
                if(voyageType == "Minibus"){
                    FileOutput.writeToFile("ERROR: Minibus tickets are not refundable!",true,true);
                    break;
                }else {
                    //It checks whether the seat is sold or not.
                    if (isSeatSold(seatNumbers, voyage)) {
                        for (String seat : seatNumbers) {
                            voyage.getNumberOfSeats().set(Integer.parseInt(seat) - 1, "*");
                            refundValue += voyage.getRefundCut(voyage.getSeatFee(Integer.parseInt(seat)));
                            voyage.setRevenue(-voyage.getRefundCut(voyage.getSeatFee(Integer.parseInt(seat))));
                        }
                    }else {
                        break;
                    }
                    String content = String.format("Seat %s of the Voyage %d from %s to %s was successfully refunded for %.2f TL.",seats.replace("_","-"), voyageID, a.getFromWhere(),a.getToWhere(),refundValue);
                    FileOutput.writeToFile(content,true,true);
                }
            }
        }
        if (!found){
            FileOutput.writeToFile("ERROR: There is no voyage with ID of " + voyageID + "!",true,true);
        }
    }

    /**
     * Initializes a new voyage with the specified parameters.
     *
     * @param voyageID    The ID of the voyage.
     * @param fromWhere   The starting point of the voyage.
     * @param toWhere     The destination of the voyage.
     * @param seatFee     The fee for each seat on the voyage.
     * @param row         The number of rows in the seating arrangement for the voyage.
     * @param refundCut   The percentage of refund cut.
     * @param premiumFee  The fee for premium seats (if applicable).
     * @param infos       Additional information related to the voyage.
     * @param voyageList  The list of voyages.
     */
    public void initVoyage(int voyageID,String fromWhere,String toWhere,float seatFee, int row, float refundCut, float premiumFee, String[] infos, List<Voyage> voyageList){
        String voyageType = infos[1];
        Voyage voyage ;
        String content;
        try {
            //It initializes the voyage of the specified type.
            switch (voyageType) {
                case "Standard":
                    // Initialize a standard voyage
                    voyage = new Standard(voyageID, fromWhere, toWhere, seatFee, row, refundCut);
                    content = String.format("Voyage %s was initialized as a standard (2+2) voyage from %s to %s with %.2f TL priced %d regular seats. Note that refunds will be %.0f%% less than the paid amount.",voyageID,fromWhere,toWhere,seatFee,row*4,refundCut);
                    FileOutput.writeToFile(content,true,true);
                    break;
                case "Premium":
                    // Initialize a premium voyage
                    voyage = new Premium(voyageID, fromWhere, toWhere, seatFee, row, refundCut, premiumFee);
                    content = String.format("Voyage %s was initialized as a premium (1+2) voyage from %s to %s with %.2f TL priced %d regular seats and %.2f TL priced %d premium seats. Note that refunds will be %.0f%% less than the paid amount.",voyageID,fromWhere,toWhere,seatFee,row*2,voyage.getSeatFee(1),row,refundCut);
                    FileOutput.writeToFile(content,true,true);
                    break;
                case "Minibus":
                    // Initialize a minibus voyage
                    voyage = new Minibus(voyageID, fromWhere, toWhere, seatFee, row);
                    content = String.format("Voyage %s was initialized as a minibus (2) voyage from %s to %s with %.2f TL priced %d regular seats. Note that minibus tickets are not refundable.",voyageID,fromWhere,toWhere,seatFee,row*2);
                    FileOutput.writeToFile(content,true,true);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            voyageList.add(voyage);
        }catch (IllegalArgumentException e){
            FileOutput.writeToFile("ERROR: Erroneous usage of \"INIT_VOYAGE\" command!",true,true);
        }
    }

    /**
     * Cancels the voyage with the specified ID.
     *
     * @param voyageList The list of voyages.
     * @param voyageID   The ID of the voyage to cancel.
     */
    public void cancelVoyage(List<Voyage> voyageList, int voyageID){
        try {
            int a = 0;
            boolean found = false;
            //Finds which voyage will be canceled
            for (Voyage voyage : voyageList) {
                if (voyage.getVoyageID() == voyageID) {
                    found = true;
                    a = voyageList.indexOf(voyage);
                    for(int i = 0 ; i < voyage.getNumberOfSeats().size() ; i++) {
                        if (voyage.getNumberOfSeats().get(i) == "X") {
                            voyage.setRevenue(-voyage.getSeatFee(i + 1));
                        }
                    }
                    FileOutput.writeToFile("Voyage " + voyageID + " was successfully cancelled!\nVoyage details can be found below:",true,true);
                    voyagePrinter(voyageID,voyage);
                }
            }
            //Deletes voyage if found
            if (found)
                voyageList.remove(a);
        }catch (IndexOutOfBoundsException e){
            FileOutput.writeToFile("ERROR: Erroneous usage of \"CANCEL_VOYAGE\" command!",true,true);
        }
    }

    /**
     * Checks if the specified seats are available for refund.
     *
     * @param seatNumbers The seat numbers to check.
     * @param voyage      The voyage object.
     * @return True if seats are sold, false otherwise.
     */
    public boolean isSeatSold(String[] seatNumbers, Voyage voyage){
        boolean available = false;
        //It checks whether the seat is sold or not and prints any errors.
        for(String seat : seatNumbers){
            if (Integer.parseInt(seat) < 0){
                FileOutput.writeToFile("ERROR: " + seat + " is not a positive integer, seat number must be a positive integer!",true,true);
                available = false;
                break;
            }
            if (Integer.parseInt(seat) <= voyage.getNumberOfSeats().size()){
                if(voyage.getNumberOfSeats().get(Integer.parseInt(seat) - 1) == "X"){
                    available = true;
                }else {
                    FileOutput.writeToFile("ERROR: One or more seats are already empty!",true,true);
                    available = false;
                    break;
                }
            }else{
                FileOutput.writeToFile("ERROR: There is no such a seat!",true,true);
                available = false;
                break;
            }
        }
        return available;
    }

    /**
     * Generates the Z report containing details of all voyages and their revenues.
     *
     * @param voyageList The list of voyages.
     */
    public void zReport(List<Voyage> voyageList,boolean lastLine){
        //Checks if voyageList is empty, prints if not empty
        if(!voyageList.isEmpty()) {
            Collections.sort(voyageList, new Comparator<Voyage>() {
                @Override
                public int compare(Voyage v1, Voyage v2) {
                    return Integer.compare(v1.getVoyageID(), v2.getVoyageID());
                }
            });
            //Prints each voyage one by one
            for (Voyage voyage : voyageList) {
                voyagePrinter(voyage.getVoyageID(), voyage);
                if (lastLine && voyage.equals(voyageList.get(voyageList.size() - 1)))
                    FileOutput.writeToFile("----------------",true,false);
                else
                    FileOutput.writeToFile("----------------",true,true);
            }
        }else {
            if (lastLine)
                FileOutput.writeToFile("No Voyages Available!\n----------------",true,false);
            else
                FileOutput.writeToFile("No Voyages Available!\n----------------",true,true);
        }
    }

    /**
     * Checks if the specified seats are available for sale.
     *
     * @param seatNumbers The seat numbers to check.
     * @param voyage      The voyage object.
     * @return True if seats are available, false otherwise.
     */
    public boolean isSeatAvailable(String[] seatNumbers, Voyage voyage){
        boolean available = false;
        //It checks whether the seat is available or not and prints an error if there is an error.
        for(String seat : seatNumbers){
            if (Integer.parseInt(seat) < 0){
                FileOutput.writeToFile("ERROR: " + seat + " is not a positive integer, seat number must be a positive integer!",true,true);
                break;
            }
            if (Integer.parseInt(seat) <= voyage.getNumberOfSeats().size()){
                if(voyage.getNumberOfSeats().get(Integer.parseInt(seat) - 1) == "*"){
                    available = true;
                }else {
                    FileOutput.writeToFile("ERROR: One or more seats already sold!",true,true);
                    available = false;
                    break;
                }
            }else{
                FileOutput.writeToFile("ERROR: There is no such a seat!",true,true);
                available = false;
                break;
            }
        }
        return available;
    }
}