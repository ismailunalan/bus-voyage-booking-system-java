import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Premium voyage.
 */
public class Premium extends Voyage {
    private ArrayList<String> numberOfRows;
    private float refundCut;
    private float premiumFee;

    /**
     * Constructs a Premium voyage with the given parameters.
     *
     * @param voyageID   The ID of the voyage.
     * @param fromWhere  The departure location of the voyage.
     * @param toWhere    The destination of the voyage.
     * @param seatFee    The fee for each seat on the voyage.
     * @param row        The number of rows in the premium voyage.
     * @param refundCut  The percentage of refund cut for the voyage.
     * @param premiumFee The additional fee for premium seats.
     */
    public Premium(int voyageID, String fromWhere, String toWhere, float seatFee, int row, float refundCut, float premiumFee) {
        super(voyageID, fromWhere, toWhere, seatFee, row);
        this.refundCut = refundCut;
        this.premiumFee = premiumFee;
        this.numberOfRows = new ArrayList<>(Collections.nCopies(row * 3, "*"));
    }

    @Override
    public float getPremiumFee() {
        return premiumFee;
    }

    /**
     * Calculates and returns the seat fee for the given seat number.
     * If the seat is a premium seat, calculates the fee including the premium fee.
     *
     * @param seatNumber The seat number for which to calculate the fee.
     * @return The calculated seat fee.
     */
    @Override
    public float getSeatFee(int seatNumber) {
        if (seatNumber % 3 == 1) {
            return (super.getSeatFee(seatNumber) * ((getPremiumFee() + 100)) / 100);
        } else {
            return super.getSeatFee(seatNumber);
        }
    }

    /**
     * Calculates and returns the refund cut for the given seat fee.
     *
     * @param seatFee The seat fee for which to calculate the refund cut.
     * @return The calculated refund cut.
     */
    @Override
    public float getRefundCut(float seatFee) {
        return ((100 - refundCut) * seatFee) / 100;
    }

    @Override
    public List<String> getNumberOfSeats() {
        return numberOfRows;
    }
}
