package dto;

public class CarryOverBalanceDTO {
	
	private String empCode;
	private String empFullName;
	private String timeOffName;
	private Float balanceBeforeReset;
	private String carryForward;
	private String carryForwardRule;
	private Float maxCarryOverLimit;
	private Float balanceAfterReset;
	private String hoursOrDays;
	private String resetOn;
	
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getEmpFullName() {
		return empFullName;
	}
	public void setEmpFullName(String empFullName) {
		this.empFullName = empFullName;
	}
	public String getTimeOffName() {
		return timeOffName;
	}
	public void setTimeOffName(String timeOffName) {
		this.timeOffName = timeOffName;
	}
	public Float getBalanceBeforeReset() {
		return balanceBeforeReset;
	}
	public void setBalanceBeforeReset(Float balanceBeforeReset) {
		this.balanceBeforeReset = balanceBeforeReset;
	}
	public String getCarryForward() {
		return carryForward;
	}
	public void setCarryForward(String carryForward) {
		this.carryForward = carryForward;
	}
	public String getCarryForwardRule() {
		return carryForwardRule;
	}
	public void setCarryForwardRule(String carryForwardRule) {
		this.carryForwardRule = carryForwardRule;
	}
	public Float getMaxCarryOverLimit() {
		return maxCarryOverLimit;
	}
	public void setMaxCarryOverLimit(Float maxCarryOverLimit) {
		this.maxCarryOverLimit = maxCarryOverLimit;
	}
	public Float getBalanceAfterReset() {
		return balanceAfterReset;
	}
	public void setBalanceAfterReset(Float balanceAfterReset) {
		this.balanceAfterReset = balanceAfterReset;
	}
	public String getHoursOrDays() {
		return hoursOrDays;
	}
	public void setHoursOrDays(String hoursOrDays) {
		this.hoursOrDays = hoursOrDays;
	}
	public String getResetOn() {
		return resetOn;
	}
	public void setResetOn(String resetOn) {
		this.resetOn = resetOn;
	}
	
	@Override
	public String toString() {
		return "CarryOverBalanceDTO [empCode=" + empCode + ", empFullName=" + empFullName + ", timeOffName="
				+ timeOffName + ", balanceBeforeReset=" + balanceBeforeReset + ", carryForward=" + carryForward
				+ ", carryForwardRule=" + carryForwardRule + ", maxCarryOverLimit=" + maxCarryOverLimit
				+ ", balanceAfterReset=" + balanceAfterReset + ", hoursOrDays=" + hoursOrDays + ", resetOn=" + resetOn
				+ "]";
	}
	
}
