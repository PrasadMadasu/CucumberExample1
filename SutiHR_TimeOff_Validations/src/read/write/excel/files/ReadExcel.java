package read.write.excel.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
/**
 * 
 * @author m.prasad
 *
 */
public class ReadExcel {
	
	/**
	 * Method getEmployees
	 * @param fileName
	 * @return List object
	 */
	public static List<String> getEmployees(String fileName) {
		
		System.out.println("== Entered into getEmployees() of ReadExcel clas ==");
		Workbook workbook = null;
		Sheet sheet = null;
		DataFormatter dataFormatter = null;
		List<String> employees = null;
		
		try {
			employees = new ArrayList<String>();
			workbook = WorkbookFactory.create(new File(fileName));
			sheet = workbook.getSheetAt(0);
			dataFormatter = new DataFormatter();
			for (Row row: sheet) {
				List<String> name = new ArrayList<String>();
	            for(Cell cell: row) {
	                String cellValue = dataFormatter.formatCellValue(cell);
	                name.add(cellValue);
	            }
	            String firstName = name.get(0);
	            String lastName = name.get(1);
	            if (firstName != null && firstName.trim().equalsIgnoreCase("First Name")) {
	            	continue;
	            }
	            employees.add(firstName+" "+lastName);
	        }
			workbook.close();
			System.out.println("Employee Details: "+employees);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employees;
	}
	
	/**
	 * Method getCarryOverBalances2018ReportData
	 * @param fileName
	 * @return Table Object
	 */
	public static Table<String,String,Float> getCarryOverBalances2018ReportData(String fileName) {
		
		System.out.println("== Entered into getCarryOverBalances2018ReportData() of ReadExcel clas ==");
		
		Workbook workbook = null;
		Sheet sheet = null;
		DataFormatter dataFormatter = null;
		Table<String,String,Float> carryOverBalances2018ReportData = null;
		
		try {
			
			carryOverBalances2018ReportData = HashBasedTable.create();
			workbook = WorkbookFactory.create(new File(fileName));
			sheet = workbook.getSheetAt(0);
			dataFormatter = new DataFormatter();
			
			int count = 0;
			
			for (Row row: sheet) {
				count++;
				
				if (count<2) {
					continue;
				}
				
				try {

					List<String> name = new ArrayList<String>();
		            for(Cell cell: row) {
		                String cellValue = dataFormatter.formatCellValue(cell);
		                cellValue = (cellValue == null) ? "" : cellValue;
		                name.add(cellValue);
		            }
		            
		            String empCode = name.get(0);
		            empCode = (empCode != null) ? empCode.trim() : empCode;
		            		
		            String empFullName = name.get(1);
		            empFullName = (empFullName != null) ? empFullName.trim() : empFullName;
		            
		            String timeOffName = name.get(2);
		            timeOffName = (timeOffName != null) ? timeOffName.trim() : timeOffName;
		            
		            String projectedBalance = name.get(3);
		            projectedBalance = (projectedBalance != null) ? projectedBalance.trim() : projectedBalance;
		            Float balance = (projectedBalance != null && !projectedBalance.trim().equals("")) ? Float.parseFloat(projectedBalance.trim()) : 0f ;
		            
		            carryOverBalances2018ReportData.put(empCode+"@"+empFullName, timeOffName, balance);
		            
				} catch (Exception e) {
					System.out.println("== Exception raised in reading rows code is: \n");
					e.printStackTrace();
				}
				
	        }
			workbook.close();
			System.out.println("Employee's Carry Over Balances 2018 report Details: "+carryOverBalances2018ReportData);
			
		} catch (Exception e) {
			System.out.println("== Exception raised in getCarryOverBalances2018ReportData() is: ");
			e.printStackTrace();
		}
		return carryOverBalances2018ReportData;
	}
	
	/**
	 * Method getTimeOffBalanceReportData
	 * @param fileName
	 * @return Table Object
	 */
	public static Table<String,String,Float> getTimeOffBalanceReportData(String fileName) {
		
		System.out.println("== Entered into getTimeOffBalanceReportData() of ReadExcel class ==");
		
		Workbook workbook = null;
		Sheet sheet = null;
		DataFormatter dataFormatter = null;
		Table<String,String,Float> timeOffBalanceReportData = null;
		
		try {
			
			timeOffBalanceReportData = HashBasedTable.create();
			workbook = WorkbookFactory.create(new File(fileName));
			sheet = workbook.getSheetAt(0);
			dataFormatter = new DataFormatter();
			
			int count = 0;
			List<String> headers = new ArrayList<String>();
			
			for (Row row: sheet) {
				count++;
				
				try {

					List<String> name = new ArrayList<String>();
		            for(Cell cell: row) {
		                String cellValue = dataFormatter.formatCellValue(cell);
		                cellValue = (cellValue == null) ? "" : cellValue;
		                if (count == 1) {
		                	headers.add(cellValue);
		                } else {
		                	name.add(cellValue);
		                }
		            }
		            
		            if (count == 1) {
		            	continue;
		            }
		            
		            String empCode = name.get(0);
		            empCode = (empCode != null) ? empCode.trim() : empCode;
		            
		            String empFullName = name.get(1);
		            empFullName = (empFullName != null) ? empFullName.trim() : empFullName;
		            
		            if (headers != null && headers.size() > 0) {
		            	for (int i = 2; i < headers.size(); i++) {
		            		
		            		String timeOffName = headers.get(i);
		    	            timeOffName = (timeOffName != null) ? timeOffName.trim() : timeOffName;
		    	            
		    	            String projectedBalance = name.get(i);
		    	            projectedBalance = (projectedBalance != null && projectedBalance.trim().equalsIgnoreCase("---")) ? "0.0" : projectedBalance;
		    	            projectedBalance = (projectedBalance != null) ? projectedBalance.trim() : projectedBalance;
		    	            
		    	            Float balance = (projectedBalance != null && !projectedBalance.trim().equals("")) ? Float.parseFloat(projectedBalance.trim()) : 0f ;
		    	            	
		    	            timeOffBalanceReportData.put(empCode+"@"+empFullName, timeOffName, balance);
		    	            
		            	}
		            }
		            
				} catch (Exception e) {
					System.out.println("== Exception raised in reading rows code is: \n");
					e.printStackTrace();
				}
	            
	        }
			workbook.close();
			System.out.println("@@@ Employee's Time Off Balances Report Details: "+timeOffBalanceReportData);
		
			
		} catch (Exception e) {
			System.out.println("== Exception raised in getTimeOffBalanceReportData() is: ");
			e.printStackTrace();
		}
		return timeOffBalanceReportData;
	}
	
}
