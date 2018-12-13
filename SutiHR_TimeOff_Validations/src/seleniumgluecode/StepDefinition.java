package seleniumgluecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import constants.StringConstants;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import read.write.excel.files.ReadExcel;
import read.write.excel.files.WriteExcel;
/**
 * 
 * @author m.prasad
 *
 */
public class StepDefinition {
	
	public static WebDriver driver;
	public static List<String> employees = new ArrayList<String>();
	public static Table<String,String,Float> timeOffBalancesReportData = HashBasedTable.create();
	public static Table<String,String,Float> carryOverBalancesReportData = HashBasedTable.create();
	public static Table<String,String,String> timeOffBalancesNotMatchedDetails = HashBasedTable.create();
	
	@Given("^go to SutiHR application login page$")
	public void go_to_SutiHR_application_login_page() throws Throwable {
		
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
		driver.findElement(By.id("username")).sendKeys(StringConstants.USERNAME);
	    driver.findElement(By.id("password")).sendKeys(StringConstants.PASSWORD);
	    driver.findElement(By.id("loginForm_0")).click();
	}

	@Then("^read Time Off Balances report data$")
	public void read_Time_Off_Balances_report_data() throws Throwable {
		timeOffBalancesReportData = ReadExcel.getTimeOffBalanceReportData(StringConstants.TIME_OFF_BALANCES_REPORT);
		employees = new ArrayList<String>(timeOffBalancesReportData.rowKeySet());
	}
	
	@Then("^read Carry Over Balances report data$")
	public void read_Carry_Over_Balances_report_data() throws Throwable {
		carryOverBalancesReportData = ReadExcel.getCarryOverBalances2018ReportData(StringConstants.CARRY_OVER_BALANCES_2018_REPORT);
	}
	
	@Then("^read the employees Time Off balances$")
	public void read_the_employees_Time_Off_balances() throws Throwable {
		
	    if (employees != null && employees.size() > 0) {
	    	
	    	Set<String> uniqueValues = new HashSet<>();
	    	uniqueValues.addAll(employees);
	    	employees.clear();
	    	employees.addAll(uniqueValues);
	    	
	    	for (String empFullName: employees) {
	    		
	    		System.out.println("Employee code with name is: "+empFullName);
	    		try {
	    			
		    		String[] strArray = empFullName.split("\\@");
		    		String empCode = strArray[0];
		    		String fullName = strArray[1];
		    		
		    		Thread.sleep(5000);
		    		driver.get(StringConstants.APPLICATION_PERSONNEL_PAGE);
		    		Thread.sleep(5000);
		    		driver.findElement(By.xpath("//*[@id='paEmpSearch']/div[4]/div[3]/div/div/div/div[2]/a")).click();
		    		driver.findElement(By.id("srchEmpCode")).sendKeys(empCode.trim());
		    		driver.findElement(By.xpath("//*[@id='paEmpSearch']/div[5]/div/input[2]")).click();
		    		Thread.sleep(3000);
		    		driver.findElement(By.id("searchId")).sendKeys(fullName.trim());
		    		Thread.sleep(5000);
		    		driver.findElement(By.xpath("//img[contains(@title,'View/Edit')]")).click();
		    		Thread.sleep(5000);
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
		    					
		    					if (timeOffBalancesReportData != null && timeOffBalancesReportData.get(empFullName, timeOffName) != null) {
		    						timeOffBalValue = timeOffBalancesReportData.get(empFullName, timeOffName);
		    					}
		    					
		    					if (carryOverBalancesReportData != null && carryOverBalancesReportData.get(empFullName, timeOffName) != null) {
		    						carryOverBalValue = carryOverBalancesReportData.get(empFullName, timeOffName);
		    					}
		    					
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
		    		
		    		System.out.println("@@@ Employee Code: "+empCode+" Employee Name: "+fullName+" Validation Completed @@@");
		    		
				} catch (Exception e) {
					System.out.println("== Exception raised at the time of validation the "+empFullName+" is: ");
					e.printStackTrace();
				}
	    		
	    	}
	    }
	}

	@Then("^store all details into xlsx file$")
	public void store_all_details_into_xlsx_file() throws Throwable {
		WriteExcel.WriteExcelData(StringConstants.TIMEOFF_BALANCES_NOT_MATCHED, timeOffBalancesNotMatchedDetails);
	}
	
	@Then("^read Time Off details$")
	public void read_Time_Off_details() throws Throwable {
		
		driver.get(StringConstants.APPLICATION_TIMEOFF_DETAILS_URL);
		
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
		  
		  int size = 0;
		  if (userTable != null) {
  			size = userTable.size();
  		  }
  		
		  for (int i = 1; i < size; i++) {
				
				String type = "";
				String projectedBalance = "";
				
				for (String columnName: columnNames) {
					
					System.out.println("Time Off Details Column Name: "+columnName);
					
					WebElement data = userTable.get(i).get(columnName);
					
					System.out.println("Time Off Details Value: "+data.getText());
					
				}
				
			}
		}
	}

	@Then("^sign out the SutiHR application$")
	public void sign_out_the_SutiHR_application() throws Throwable {
		Thread.sleep(5000);
		driver.findElement(By.id("headerProImgId")).click();
		driver.get(StringConstants.APPLICATION_SIGN_OUT_URL);
		Thread.sleep(5000);
		driver.quit();
	}
}
