import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Minibus voyage.
 */
public class Minibus extends Voyage {
    private ArrayList<String> numberOfRows;

    /**
     * Constructs a Minibus voyage with the given parameters.
     *
     * @param voyageID  The ID of the voyage.
     * @param fromWhere The departure location of the voyage.
     * @param toWhere   The destination of the voyage.
     * @param seatFee   The fee for each seat on the voyage.
     * @param row       The number of rows in the minibus.
     */
    public Minibus(int voyageID, String fromWhere, String toWhere, float seatFee, int row) {
        super(voyageID, fromWhere, toWhere, seatFee, row);
        this.numberOfRows = new ArrayList<>(Collections.nCopies(row * 2, "*"));
    }

    @Override
    public List<String> getNumberOfSeats() {
        return numberOfRows;
    }
}
