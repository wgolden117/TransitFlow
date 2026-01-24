package transitflow.prediction;

/**
 * Represents risk metrics derived from prediction results.
 *
 * This class interprets predictions but does not influence
 * simulation or prediction behavior.
 */
public class RiskAssessment {

    private final double delayRiskScore;

    public RiskAssessment(double delayRiskScore) {
        this.delayRiskScore = delayRiskScore;
    }

    public double getDelayRiskScore() {
        return delayRiskScore;
    }
}
