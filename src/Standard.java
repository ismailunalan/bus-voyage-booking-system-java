import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Standard voyage.
 */
public class Standard extends Voyage {
    private float refundCut;
    private ArrayList<String> numberOfRows;

    /**
     * Constructs a Standard voyage with the given parameters.
     *
     * @param voyageID   The ID of the voyage.
     * @param fromWhere  The departure location of the voyage.
     * @param toWhere    The destination of the voyage.
     * @param seatFee    The fee for each seat on the voyage.
     * @param row        The number of rows in the standard voyage.
     * @param refundCut  The percentage of refund cut for the voyage.
     */
    public Standard(int voyageID, String fromWhere, String toWhere, float seatFee, int row, float refundCut) {
        super(voyageID, fromWhere, toWhere, seatFee, row);
        this.refundCut = refundCut;
        this.numberOfRows = new ArrayList<>(Collections.nCopies(row * 4, "*"));
    }

    @Override
    public List<String> getNumberOfSeats() {
        return numberOfRows;
    }

    @Override
    public void setRevenue(float revenue) {
        super.setRevenue(revenue);
    }

    /**
     * Calculates and returns the refund cut for the given seat fee.
     *
     * @param seatFee The seat fee for which to calculate the refund cut.
     * @return The calculated refund cut.
     */
    public float getRefundCut(float seatFee) {
        return ((100 - refundCut) * seatFee) / 100;
    }
}