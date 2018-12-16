package seleniumgluecode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import constants.StringConstants;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import dto.CarryOverBalanceDTO;
import dto.TimeOffDetailsDTO;
import read.write.excel.files.ReadExcel;
import read.write.excel.files.WriteExcel;
import util.CompareTwoExcelFiles;
import util.Java8Utils;
/**
 * 
 * @author m.prasad
 *
 */
public class StepDefinition {
	
	public static final Logger LOGGER = Logger.getLogger(StepDefinition.class.getName());
	
	public static WebDriver driver;
	public static List<String> employees = new ArrayList<String>();
	public static Table<String,String,Float> timeOffBalancesReportData = HashBasedTable.create();
	public static Table<String,String,Float> carryOverBalancesReportData = HashBasedTable.create();
	public static Table<String,String,String> timeOffBalancesNotMatchedDetails = HashBasedTable.create();
	public static List<TimeOffDetailsDTO> timeOffDetailsDTOs = new ArrayList<TimeOffDetailsDTO>();
	public static List<CarryOverBalanceDTO> carryOverBalanceDTOs = new ArrayList<CarryOverBalanceDTO>();
	public static Table<String,String,String> carryOverBalancesNotMatchedDetails = HashBasedTable.create();
	public static String companyName;
	
	@Given("^go to SutiHR application login page$")
	public void go_to_SutiHR_application_login_page() throws Throwable {
		
		LOGGER.info("== Entered into go_to_SutiHR_application_login_page() method ==");
		
		System.setProperty(StringConstants.CHROME_PROPERTY, StringConstants.CHROME_DRIVER);
		driver = new ChromeDriver();
		
		//System.setProperty(StringConstants.FIREFOX_PROPERTY, StringConstants.FIREFOX_DRIVER);
		//driver = new FirefoxDriver();
		
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(StringConstants.APPLICATION_URL);
	}

	@Given("^login as HR admin username and password$")
	public void login_as_HR_admin_username_and_password() throws Throwable {
		LOGGER.info("== Entered into login_as_HR_admin_username_and_password() ==");
		driver.findElement(By.id("username")).sendKeys(StringConstants.USERNAME);
	    driver.findElement(By.id("password")).sendKeys(StringConstants.PASSWORD);
	    driver.findElement(By.id("loginForm_0")).click();
	}

	@Then("^read Time Off Balances report data$")
	public void read_Time_Off_Balances_report_data() throws Throwable {
		
		LOGGER.info("== Entered into read_Time_Off_Balances_report_data() ==");
		
		timeOffBalancesReportData = ReadExcel.getTimeOffBalanceReportData(StringConstants.TIME_OFF_BALANCES_REPORT);
		employees = new ArrayList<String>(timeOffBalancesReportData.rowKeySet());
	}
	
	@Then("^read Carry Over Balances report data$")
	public void read_Carry_Over_Balances_report_data() throws Throwable {
		
		LOGGER.info("== Entered into read_Carry_Over_Balances_report_data() ==");
		
		ReadExcel.getCarryOverBalances2018ReportData(carryOverBalancesReportData,
													 carryOverBalanceDTOs,
													 StringConstants.CARRY_OVER_BALANCES_2018_REPORT);
	}
	
	@Then("^read the employees Time Off balances$")
	public void read_the_employees_Time_Off_balances() throws Throwable {
		
		LOGGER.info("== Entered into read_the_employees_Time_Off_balances() ==");
		
		try {
			
			companyName = driver.findElement(By.id("hiddenCompanyName")).getAttribute("value");
			companyName = (companyName != null && !companyName.trim().equals("")) ? companyName.replaceAll("\\ ", "_") : companyName;
			LOGGER.info("@@@ Company Name: "+companyName);
			
		} catch (Exception e) {
			LOGGER.error("== Exception raised at the time of getting company name: "+e,e);
		}
		
	    if (employees != null && employees.size() > 0) {
	    	
	    	Set<String> uniqueValues = new HashSet<>();
	    	uniqueValues.addAll(employees);
	    	employees.clear();
	    	employees.addAll(uniqueValues);
	    	
	    	int count = 0;
	    	
	    	for (String empFullName: employees) {
	    		
	    		count++;
	    		
	    		LOGGER.info("@@@ Employee code with name is: "+empFullName);
	    		
	    		try {
	    			
		    		String[] strArray = empFullName.split("\\@");
		    		String empCode = strArray[0];
		    		String fullName = strArray[1];
		    		
		    		Thread.sleep(4000);
		    		if (count == 1) {
		    			driver.get(StringConstants.APPLICATION_PERSONNEL_PAGE);
		    			Thread.sleep(5000);
			    		driver.findElement(By.xpath("//*[@id='paEmpSearch']/div[4]/div[3]/div/div/div/div[2]/a")).click();
		    		} else {
		    			driver.findElement(By.id("srchEmpCode")).clear();
		    		}
		    		
		    		driver.findElement(By.id("srchEmpCode")).sendKeys(empCode.trim());
		    		driver.findElement(By.xpath("//*[@id='paEmpSearch']/div[5]/div/input[2]")).click();
		    		Thread.sleep(3000);
		    		driver.findElement(By.id("searchId")).sendKeys(fullName.trim());
		    		Thread.sleep(5000);
		    		try {
		    			driver.findElement(By.xpath("//img[contains(@title,'View/Edit')]")).click();
					} catch (Exception e) {
						continue;
					}
		    		Thread.sleep(3000);
		    		driver.findElement(By.id("timeOffTabId")).click();
		    		
		    		WebElement tableElement = driver.findElement(By.xpath(".//*[@id='balances']/div[3]/table"));
		    		
		    		// create empty table object and iterate through all rows of the found table element
		    		ArrayList<HashMap<String, WebElement>> userTable = new ArrayList<HashMap<String, WebElement>>();
		    		List<WebElement> rowElements = tableElement.findElements(By.xpath(".//tr"));

		    		// get column names of table from table headers
		    		ArrayList<String> columnNames = new ArrayList<String>();
		    		List<WebElement> headerElements = rowElements.get(0).findElements(By.xpath(".//th"));
		    		
		    		for (WebElement headerElement: headerElements) {
		    		  columnNames.add(headerElement.getText());
		    		}

		    		// iterate through all rows and add their content to table array
		    		for (WebElement rowElement: rowElements) {
		    		  HashMap<String, WebElement> row = new HashMap<String, WebElement>();
		    		  
		    		  // add table cells to current row
		    		  int columnIndex = 0;
		    		  List<WebElement> cellElements = rowElement.findElements(By.xpath(".//td"));
		    		  for (WebElement cellElement: cellElements) {
		    		    row.put(columnNames.get(columnIndex), cellElement);
		    		    columnIndex++;
		    		  }
		    		  
		    		  userTable.add(row);
		    		}
		    		
		    		int size = 0;
		    		if (userTable != null) {
		    			size = userTable.size();
		    		}
		    		if (size > 1) {
		    			for (int i = 1; i < size; i++) {
		    				
		    				String type = "";
		    				String projectedBalance = "";
		    				
		    				for (String columnName: columnNames) {
		    					
		    					if ((columnName != null) && 
		    						!(columnName.trim().equalsIgnoreCase("Type") || 
		    						  columnName.trim().equalsIgnoreCase("Projected Balance"))) {
		    						continue;
		    					}
		    					
		    					WebElement data = userTable.get(i).get(columnName);
		    					
		    					if (data != null && 
		    						!data.getText().trim().equalsIgnoreCase("No records to display")) {
			    					if (columnName.trim().equalsIgnoreCase("Type")) {
			    						type = data.getText();
			    					} else if (columnName.trim().equalsIgnoreCase("Projected Balance")) {
			    						projectedBalance = data.getText();
			    					}
		    					}
		    					
		    				}
		    				
		    				if ((type != null && !type.trim().equals("")) && 
		    					(projectedBalance != null && !projectedBalance.trim().equals(""))) {
		    					
		    					String[] typeArray = type.split("\\[");
		    					String timeOffName = "";
		    					
		    					if (typeArray != null && typeArray.length > 0) {
		    						timeOffName = typeArray[0].trim();
		    					}
		    					
		    					Float balance = Float.parseFloat(projectedBalance.trim());
		    					
		    					Float timeOffBalValue = null;
		    					Float carryOverBalValue = null;
		    					
		    					LOGGER.info("@@@ Employee balance: "+balance);
		    					LOGGER.info("@@@ Time Off Name: "+timeOffName);
		    					LOGGER.info("@@@ Emp Full Name: "+empFullName);
		    					LOGGER.info("@@@ timeOffBalancesReportData.get(empFullName, timeOffName): "+timeOffBalancesReportData.get(empFullName, timeOffName));
		    					LOGGER.info("@@@ carryOverBalancesReportData.get(empFullName, timeOffName): "+carryOverBalancesReportData.get(empFullName, timeOffName));
		    					
		    					if (timeOffBalancesReportData != null && timeOffBalancesReportData.get(empFullName, timeOffName) != null) {
		    						timeOffBalValue = timeOffBalancesReportData.get(empFullName, timeOffName);
		    					}
		    					
		    					if (carryOverBalancesReportData != null && carryOverBalancesReportData.get(empFullName, timeOffName) != null) {
		    						carryOverBalValue = carryOverBalancesReportData.get(empFullName, timeOffName);
		    					}
		    					
		    					LOGGER.info("@@@ timeOffBalValue: "+timeOffBalValue);
		    					LOGGER.info("@@@ carryOverBalValue: "+carryOverBalValue);
		    					
		    					if (timeOffBalValue != null && balance != null) {
		    						if (!balance.equals(timeOffBalValue)) {
		    							String msg = "In Time Off Balances Report balance value is : "+timeOffBalValue+" and then employee balance value is : "+balance;
		    							timeOffBalancesNotMatchedDetails.put(empFullName, timeOffName, msg);
		    						}
		    					} else if (timeOffBalValue == null) {
		    						String msg = "Time Off Balance Report not contains any balance and employee balance is : "+balance;
	    							timeOffBalancesNotMatchedDetails.put(empFullName, timeOffName, msg);
		    					}
		    					
		    					if (carryOverBalValue != null && balance != null) {
		    						if (!balance.equals(carryOverBalValue)) {
		    							String msg = "In Carry Over Balances 2018 Report balance value is : "+carryOverBalValue+" and then employee balance value is : "+balance;
		    							timeOffBalancesNotMatchedDetails.put(empFullName, timeOffName, msg);
		    						}
		    					} else if (carryOverBalValue == null) {
		    						String msg = "Carry Over Balance 2018 Report not contains any balance and employee balance is : "+balance;
	    							timeOffBalancesNotMatchedDetails.put(empFullName, timeOffName, msg);
		    					}
		    					
		    				}
		    				
		    			}
		    		}
		    		
		    		LOGGER.info("@@@ @@@ Employee Code: "+empCode+" Employee Name: "+fullName+" Validation Completed @@@");
		    		
				} catch (Exception e) {
					LOGGER.error("== Exception raised at the time of validation the "+empFullName+" is: "+e,e);
				}
	    		
	    		Thread.sleep(1000);
	    		driver.navigate().back();
	    		driver.navigate().back();
	    	}
	    }
	}

	@Then("^store all details into xlsx file$")
	public void store_all_details_into_xlsx_file() throws Throwable {
		WriteExcel.WriteExcelData(StringConstants.TIMEOFF_BALANCES_NOT_MATCHED,
								  companyName, "_Time_Off_Balances_Not_Matched.xlxs",
								  timeOffBalancesNotMatchedDetails);
	}
	
	@Then("^read Time Off details$")
	public void read_Time_Off_details() throws Throwable {
		
		boolean isEnablePagination = true;
		
		try {
			
			driver.get(StringConstants.APPLICATION_TIMEOFF_DETAILS_URL);
			
			try {
				driver.findElement(By.xpath("//*[@id='searchForm']/div/div/div/div[5]/div[3]/div/div/div[2]/a[1]"));
			} catch (Exception e) {
				isEnablePagination = false;
			}
			
			LOGGER.info("@@@ Time Off Details isEnablePagination: "+isEnablePagination);
			
			if (isEnablePagination) {

		    	int count = 0;
		    	while (count < 4) {
		    		count++;
		    		try {
		    			
		    			driver.findElement(By.xpath("//*[@id='searchForm']/div/div/div/div[5]/div[3]/div/div/div[2]/a["+count+"]")).click();

		    			WebElement tableElement = driver.findElement(By.xpath(".//*[@id='border']"));
		    			
		    			// create empty table object and iterate through all rows of the found table element
		    			ArrayList<HashMap<String, WebElement>> userTable = new ArrayList<HashMap<String, WebElement>>();
		    			List<WebElement> rowElements = tableElement.findElements(By.xpath(".//tr"));

		    			// get column names of table from table headers
		    			ArrayList<String> columnNames = new ArrayList<String>();
		    			List<WebElement> headerElements = rowElements.get(0).findElements(By.xpath(".//th"));
		    			
		    			for (WebElement headerElement: headerElements) {
		    			  columnNames.add(headerElement.getText());
		    			}
		    			
		    			int table_size = rowElements.size();
		    			
		    			for (int i = 2; i <= table_size; i++) {
		    				
		    				try {
		    		    		driver.findElement(By.xpath("//*[@id='border']/tbody/tr["+i+"]/td[7]/img[1]")).click();
		    				} catch (Exception e) {
		    					continue;
		    				}

		    	    		String leaveName = driver.findElement(By.id("leaveName")).getAttribute("value");
		    	    		
		    	    		List<WebElement> radioButtons = driver.findElements(By.name("accruedLapsed"));
		    	    		String radioButtonValue = "No";
		    	    		String fullORHalf = "";
		    	    		String maxLimit = "";
		    	    		
		    	    		for (int j = 0; j < radioButtons.size(); j++) {
		    	    			
		    	    			String checked = radioButtons.get(j).getAttribute("checked");
		    	    			String value = radioButtons.get(j).getAttribute("value");
		    	    			
		    	    			if ((value != null && value.trim().equalsIgnoreCase("Accrued")) && 
		    	    				(checked != null && checked.trim().equalsIgnoreCase("true"))) {
		    	    				radioButtonValue = "Yes";
		    	    				fullORHalf = driver.findElement(By.id("selectAccrued")).getAttribute("value");
		    	    				maxLimit = driver.findElement(By.id("limit")).getAttribute("value");
		    	    			}
		    	    			
		    	    		}
		    	    		
		    	    		leaveName = (leaveName != null) ? leaveName.trim() : leaveName;
		    	    		radioButtonValue = (radioButtonValue != null) ? radioButtonValue.trim() : radioButtonValue;
		    	    		fullORHalf = (fullORHalf != null) ? fullORHalf.trim() : fullORHalf;
		    	    		Float limit = (maxLimit != null && !maxLimit.trim().equals("")) ? Float.parseFloat(maxLimit.trim()) : 0f;
		    	    		
		    	    		TimeOffDetailsDTO timeOffDetailsDTO = new TimeOffDetailsDTO();
		    	    		timeOffDetailsDTO.setTimeOffName(leaveName);
		    	    		timeOffDetailsDTO.setCarryForward(radioButtonValue);
		    	    		timeOffDetailsDTO.setFullOrHalf(fullORHalf);
		    	    		timeOffDetailsDTO.setMaxLimit(limit);
		    	    		
		    	    		timeOffDetailsDTOs.add(timeOffDetailsDTO);
		    	    		
		    	    		LOGGER.info("@@@ Leave Name: "+leaveName+" Carry Forward: "+radioButtonValue+" Full / Half: "+fullORHalf+" Max Limit: "+maxLimit);
		    	    		
		    	    		Thread.sleep(2000);
		    	    		driver.navigate().back();
		    			}
		    			
					} catch (Exception e) {
						try {
							try {
								driver.findElement(By.xpath("//*[@id='paginationNextAction']/a[1]")).click();
							} catch (Exception e2) {
								break;
							}
							driver.findElement(By.xpath("//*[@id='searchForm']/div/div/div/div[5]/div[3]/div/div/div[2]/a["+count+"]")).click();
						} catch (Exception e2) {
							count = 0;
							continue;
						}
					}
		    		
		    	}
		    	
			} else {

				WebElement tableElement = driver.findElement(By.xpath(".//*[@id='border']"));
				
				// create empty table object and iterate through all rows of the found table element
				ArrayList<HashMap<String, WebElement>> userTable = new ArrayList<HashMap<String, WebElement>>();
				List<WebElement> rowElements = tableElement.findElements(By.xpath(".//tr"));

				// get column names of table from table headers
				ArrayList<String> columnNames = new ArrayList<String>();
				List<WebElement> headerElements = rowElements.get(0).findElements(By.xpath(".//th"));
				
				for (WebElement headerElement: headerElements) {
				  columnNames.add(headerElement.getText());
				}
				
				int table_size = rowElements.size() - 1;
				
				for (int i = 2; i <= table_size; i++) {
					
					try {
			    		driver.findElement(By.xpath("//*[@id='border']/tbody/tr["+i+"]/td[7]/img[1]")).click();
					} catch (Exception e) {
						continue;
					}

		    		String leaveName = driver.findElement(By.id("leaveName")).getAttribute("value");
		    		
		    		List<WebElement> radioButtons = driver.findElements(By.name("accruedLapsed"));
		    		String radioButtonValue = "No";
		    		String fullORHalf = "";
		    		String maxLimit = "";
		    		
		    		for (int j = 0; j < radioButtons.size(); j++) {
		    			
		    			String checked = radioButtons.get(j).getAttribute("checked");
		    			String value = radioButtons.get(j).getAttribute("value");
		    			
		    			if ((value != null && value.trim().equalsIgnoreCase("Accrued")) && 
		    				(checked != null && checked.trim().equalsIgnoreCase("true"))) {
		    				radioButtonValue = "Yes";
		    				fullORHalf = driver.findElement(By.id("selectAccrued")).getAttribute("value");
		    				maxLimit = driver.findElement(By.id("limit")).getAttribute("value");
		    			}
		    			
		    		}
		    		
		    		leaveName = (leaveName != null) ? leaveName.trim() : leaveName;
		    		radioButtonValue = (radioButtonValue != null) ? radioButtonValue.trim() : radioButtonValue;
		    		fullORHalf = (fullORHalf != null) ? fullORHalf.trim() : fullORHalf;
		    		Float limit = (maxLimit != null && !maxLimit.trim().equals("")) ? Float.parseFloat(maxLimit.trim()) : 0f;
		    		
		    		TimeOffDetailsDTO timeOffDetailsDTO = new TimeOffDetailsDTO();
		    		timeOffDetailsDTO.setTimeOffName(leaveName);
		    		timeOffDetailsDTO.setCarryForward(radioButtonValue);
		    		timeOffDetailsDTO.setFullOrHalf(fullORHalf);
		    		timeOffDetailsDTO.setMaxLimit(limit);
		    		
		    		timeOffDetailsDTOs.add(timeOffDetailsDTO);
		    		
		    		LOGGER.info("@@@ Leave Name: "+leaveName+" Carry Forward: "+radioButtonValue+" Full / Half: "+fullORHalf+" Max Limit: "+maxLimit);
		    		
		    		Thread.sleep(2000);
		    		driver.navigate().back();
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("@@@ == Exception raised in read_Time_Off_details() is: "+e,e);
		}
	}
	
	@Then("^validate the carry over balances before reset and after reset values with Time Off details setup$")
	public void validate_the_carry_over_balances_before_reset_and_after_reset_values_with_Time_Off_details_setup() throws Throwable {
		
		LOGGER.info("== Entered into validate_the_carry_over_balances_before_reset_and_after_reset_values_with_Time_Off_details_setup() method ===");
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		if ((timeOffDetailsDTOs != null && timeOffDetailsDTOs.size() > 0) &&
			(carryOverBalanceDTOs != null && carryOverBalanceDTOs.size() > 0) && 
			(carryOverBalancesReportData != null && carryOverBalancesReportData.size() > 0)) {
			
			List<String> uniqueEmpDetails = new ArrayList<String>(carryOverBalancesReportData.rowKeySet());
			
			if (uniqueEmpDetails != null && uniqueEmpDetails.size() > 0) {
				
				Set<String> uniqueValues = new HashSet<>();
		    	uniqueValues.addAll(uniqueEmpDetails);
		    	uniqueEmpDetails.clear();
		    	uniqueEmpDetails.addAll(uniqueValues);
		    	
		    	for (String empCodeWithName: uniqueEmpDetails) {
		    		
		    		String[] strArray = empCodeWithName.split("\\@");
		    		String empCode = strArray[0];
		    		String empFullName = strArray[1];
		    		
		    		for (TimeOffDetailsDTO dto: timeOffDetailsDTOs) {
		    			
		    			String timeOffName = dto.getTimeOffName();
		    			String carryForward = dto.getCarryForward();
		    			String fullOrHalf = dto.getFullOrHalf();
		    			Float maxLimit = dto.getMaxLimit();
		    			
		    			Predicate<CarryOverBalanceDTO> predicate = Java8Utils.matchedObjects(empCode, empFullName, timeOffName);
		    			List<CarryOverBalanceDTO> matchedObject = null;
		    			
		    			if (predicate != null) {
		    				matchedObject = Java8Utils.filterCarryOverBalanceDTOs(carryOverBalanceDTOs, predicate);
		    				
		    				if (matchedObject != null && matchedObject.size() > 0) {
		    					
		    					String matcedObjCarryForward = matchedObject.get(0).getCarryForward();
		    					String matchedObjectFullOrHalf = matchedObject.get(0).getCarryForwardRule();
		    					Float balanceBeforeReset = matchedObject.get(0).getBalanceBeforeReset();
		    					Float balanceAfterReset = matchedObject.get(0).getBalanceAfterReset();
		    					Float maxCarryOverLimit = matchedObject.get(0).getMaxCarryOverLimit();
		    					
		    					if ("Yes".equalsIgnoreCase(carryForward)) {
		    						if (carryForward.equalsIgnoreCase(matcedObjCarryForward)) {
		    							
		    							if ("Full".equalsIgnoreCase(fullOrHalf)) {
		    								if (fullOrHalf.equalsIgnoreCase(matchedObjectFullOrHalf)) {
		    									
		    									if (maxCarryOverLimit != null && maxLimit != null) {
		    										if (df.format(maxCarryOverLimit).equals(df.format(maxLimit))) {

				    									if (balanceBeforeReset != null && balanceAfterReset != null) {
				    										if (balanceBeforeReset < maxCarryOverLimit) {
				    											if (!df.format(balanceBeforeReset).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report Balance After and Before Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										} else if (balanceBeforeReset > maxCarryOverLimit) {
				    											if (!df.format(maxCarryOverLimit).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report Max Carry Over Limit and Balance After Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										} else if (balanceBeforeReset.equals(maxCarryOverLimit)) {
				    											if (!df.format(maxCarryOverLimit).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report Max Carry Over Limit and Balance After Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										}
				    									} else {
				    										String msg = "Please check the Carry Over Balance Report Balance After and Before Reset Values";
				    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    									}
				    									
		    										} else {
		    											String msg = "In Carry Over Balance Report (Max Carry Over Limit) value is not matched with "+timeOffName+" setup";
		    											carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    										}
		    									} else {
		    										String msg = "Please check the Time Off setup and Carry Over Balance Report Max Limit Values";
		    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    									}
		    									
		    									
		    								} else {
		    									String msg = "In Carry Over Balance Report (Carry Forward Rule) value not matched with "+timeOffName+" setup";
		    									carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    								}
		    							} else if ("Half".equalsIgnoreCase(fullOrHalf)) {
		    								if (fullOrHalf.equalsIgnoreCase(matchedObjectFullOrHalf)) {
		    									
		    									Float halfBeforeResetValue = null;
		    									if (balanceBeforeReset != null) {
		    										halfBeforeResetValue = balanceBeforeReset / 2;
		    									}
		    									
		    									if (maxCarryOverLimit != null && maxLimit != null) {
		    										if (df.format(maxCarryOverLimit).equals(df.format(maxLimit))) {

				    									if (balanceBeforeReset != null && balanceAfterReset != null) {
				    										if (halfBeforeResetValue < maxCarryOverLimit) {
				    											if (!df.format(halfBeforeResetValue).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report having MAX LIMIT (Half) Balance After and Before Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										} else if (halfBeforeResetValue > maxCarryOverLimit) {
				    											if (!df.format(maxCarryOverLimit).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report having MAX LIMIT (Half) Max Carry Over Limit and Balance After Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										} else if (halfBeforeResetValue.equals(maxCarryOverLimit)) {
				    											if (!df.format(maxCarryOverLimit).equals(df.format(balanceAfterReset))) {
				    												String msg = "In Carry Over Balance Report having MAX LIMIT (Half) Max Carry Over Limit and Balance After Reset Values not matched";
						    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    											}
				    										}
				    									} else {
				    										String msg = "Please check the Carry Over Balance Report Balance After and Before Reset Values";
				    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
				    									}
				    									
		    										} else {
		    											String msg = "In Carry Over Balance Report (Max Carry Over Limit) value is not matched with "+timeOffName+" setup";
		    											carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    										}
		    									} else {
		    										String msg = "Please check the Time Off setup and Carry Over Balance Report Max Limit Values";
		    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    									}
		    									
		    									
		    								} else {
		    									String msg = "In Carry Over Balance Report (Carry Forward Rule) value not matched with "+timeOffName+" setup";
		    									carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    								}
		    							}
		    							
		    						} else {
		    							String msg = "In Carry Over Balance Report (Carry Forward) value is not matced to "+timeOffName+" setup";
		    							carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    						}
		    					} else if ("No".equals(carryForward)) {
		    						if (carryForward.equalsIgnoreCase(matcedObjCarryForward)) {
		    							if (balanceAfterReset != null) {
		    								if (!balanceAfterReset.equals(0f)) {
		    									String msg = "In Carry Over Balance Report (Balance After reset value) is not becomes zero even after applying Carry Forward is (No) also";
	    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    								}
		    							} else {
    										String msg = "Please check the Carry Over Balance Report Balance After Value";
    										carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
    									}
		    						} else {
		    							String msg = "In Carry Over Balance Report (Carry Forward) value is not matced to "+timeOffName+" setup";
		    							carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    						}
		    					}
		    				} else {
		    					String msg = timeOffName+" has no record in Carry Over Balance Report for this employee";
		    					carryOverBalancesNotMatchedDetails.put(empCode, empFullName, msg);
		    				}
		    			}
		    		}
		    	}
		    	
			}
			
		}
		
	}
	
	@Then("^store all details into Carry Forward Not Matched xlsx file$")
	public void store_all_details_into_Carry_Forward_Not_Matched_xlsx_file() throws Throwable {
		
		LOGGER.info("== Entered into store_all_details_into_Carry_Forward_Not_Matched_xlsx_file() method ==");
		
		WriteExcel.WriteExcelData(StringConstants.TIMEOFF_BALANCES_NOT_MATCHED, companyName, 
								  "_Carry_Over_Balances_Not_Matched.xlsx",  
								  carryOverBalancesNotMatchedDetails);
	}
	
	@Then("^compare two files Time Off Balance Report and Carry Over Balance Report$")
	public void compare_two_files_Time_Off_Balance_Report_and_Carry_Over_Balance_Report() throws Throwable {
		
		LOGGER.info("== Entered into compare_two_files_Time_Off_Balance_Report_and_Carry_Over_Balance_Report() method ==");
		
		CompareTwoExcelFiles.CompareTwoFile(timeOffBalancesReportData, carryOverBalancesReportData, 
											StringConstants.TIMEOFF_BALANCES_NOT_MATCHED, companyName);
		
	}
	
	@Then("^sign out the SutiHR application$")
	public void sign_out_the_SutiHR_application() throws Throwable {
		
		LOGGER.info("== Entered into sign_out_the_SutiHR_application() method ==");
		
		Thread.sleep(3000);
		driver.findElement(By.id("headerProImgId")).click();
		driver.get(StringConstants.APPLICATION_SIGN_OUT_URL);
		Thread.sleep(5000);
		driver.quit();
	}
}
