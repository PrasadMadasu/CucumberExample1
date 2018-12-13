package dto;
/**
 * 
 * @author m.prasad
 *
 */
public class TimeOffDetailsDTO {
	
	public String timeOffName;
	public String carryForward;
	public String fullOrHalf;
	public Float maxLimit;
	
	public String getTimeOffName() {
		return timeOffName;
	}
	public void setTimeOffName(String timeOffName) {
		this.timeOffName = timeOffName;
	}
	public String getCarryForward() {
		return carryForward;
	}
	public void setCarryForward(String carryForward) {
		this.carryForward = carryForward;
	}
	public String getFullOrHalf() {
		return fullOrHalf;
	}
	public void setFullOrHalf(String fullOrHalf) {
		this.fullOrHalf = fullOrHalf;
	}
	public Float getMaxLimit() {
		return maxLimit;
	}
	public void setMaxLimit(Float maxLimit) {
		this.maxLimit = maxLimit;
	}
	
	@Override
	public String toString() {
		return "TimeOffDetailsDTO [timeOffName=" + timeOffName + ", carryForward=" + carryForward + ", fullOrHalf="
				+ fullOrHalf + ", maxLimit=" + maxLimit + "]";
	}
	
}
