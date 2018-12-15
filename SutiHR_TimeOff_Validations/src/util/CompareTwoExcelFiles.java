package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import read.write.excel.files.WriteExcel;

public class CompareTwoExcelFiles {
	
	public static final Logger LOGGER = Logger.getLogger(CompareTwoExcelFiles.class.getName());
	
	
	public static final String FILE_ONE = ".//src//com//examples//First.xlsx";
	public static final String FILE_TWO = ".//src//com//examples//Second.xlsx";
	
	public static void main(String[] args) {
		
		Table<String,String,Float> getEmpsTimeOffBalances1 = null;
		Table<String,String,Float> getEmpsTimeOffBalances2 = null;
		
		try {
			
			getEmpsTimeOffBalances1 = getEmpsTimeOffBalances(FILE_ONE);
			getEmpsTimeOffBalances2 = getEmpsTimeOffBalances(FILE_TWO);
			
			System.out.println("==============================================================");
			for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
				System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
			}
			System.out.println("==============================================================");
			for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
				System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
			}
			System.out.println("==============================================================");
			
			int one_size = (getEmpsTimeOffBalances1 != null) ? getEmpsTimeOffBalances1.size() : 0;
			int two_size = (getEmpsTimeOffBalances2 != null) ? getEmpsTimeOffBalances2.size() : 0;
			
			if ((one_size > 0 && two_size > 0) && (one_size > two_size)) {
				
				Table<String,String,Float> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					Float actualProjectedBalance = cell.getValue();
					
					Float expectedProjectedBalance = getEmpsTimeOffBalances2.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.equals(expectedProjectedBalance)) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances2.remove(empFullName, timeOffName);
							
						}
					}
					
				}

				System.out.println("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, Float> cell: matchedData.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Unmatched Data in Second file is: ");
				System.out.println("==================================");
				if (getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() == 0) {
					System.out.println("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() > 0) {
					for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				

				System.out.println("First File Data: ");
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
					System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
				}
				
			} else if ((one_size > 0 && two_size > 0) && ((two_size > one_size) || (one_size == two_size))) {
				Table<String,String,Float> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					Float actualProjectedBalance = cell.getValue();
					
					Float expectedProjectedBalance = getEmpsTimeOffBalances1.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.equals(expectedProjectedBalance)) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances1.remove(empFullName, timeOffName);
							
						}
					}
					
				}
				
				System.out.println("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, Float> cell: matchedData.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Unmatched Data in First file is: ");
				System.out.println("==================================");
				if (getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() == 0) {
					System.out.println("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() > 0) {
					for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Second File Data: ");
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
					System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
				}
				
			} else {
				System.out.println("Either any file having no data. Please check the files.");
				System.out.println("getEmpsTimeOffBalances1: "+getEmpsTimeOffBalances1);
				System.out.println("getEmpsTimeOffBalances2: "+getEmpsTimeOffBalances2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method CompareTwoFile
	 * @param timeOffBalanceReport
	 * @param carryOverBalanceReport
	 * @param filePath
	 * @param companyName
	 */
	public static void CompareTwoFile(Table<String,String,Float> timeOffBalanceReport,
									  Table<String,String,Float> carryOverBalanceReport,
									  String filePath,String companyName) {
		
		LOGGER.info("== Entered into CompareTwoFile() method ==");
		
		Table<String,String,Float> getEmpsTimeOffBalances1 = null;
		Table<String,String,Float> getEmpsTimeOffBalances2 = null;
		
		Table<String,String,String> timeOffBalFileComments = null;
		Table<String,String,String> carryOverBalFileComments = null;
		
		try {
			getEmpsTimeOffBalances1 = timeOffBalanceReport;
			getEmpsTimeOffBalances2 = carryOverBalanceReport;
			
			timeOffBalFileComments = HashBasedTable.create();
			carryOverBalFileComments = HashBasedTable.create();
			
			int one_size = (getEmpsTimeOffBalances1 != null) ? getEmpsTimeOffBalances1.size() : 0;
			int two_size = (getEmpsTimeOffBalances2 != null) ? getEmpsTimeOffBalances2.size() : 0;
			
			if ((one_size > 0 && two_size > 0) && (one_size > two_size)) {
				
				Table<String,String,Float> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					Float actualProjectedBalance = cell.getValue();
					
					Float expectedProjectedBalance = getEmpsTimeOffBalances2.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.equals(expectedProjectedBalance)) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances2.remove(empFullName, timeOffName);
							
						}
					}
					
				}

				LOGGER.info("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, Float> cell: matchedData.cellSet()) {
						LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				LOGGER.info("Unmatched Data in Second file is: ");
				LOGGER.info("==================================");
				if (getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() == 0) {
					LOGGER.info("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() > 0) {
					for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
						LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
						carryOverBalFileComments.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue()+" Not Matched");
					}
				}

				LOGGER.info("First File Data: ");
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
					LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					timeOffBalFileComments.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue()+" Remaining Data");
				}
				
			} else if ((one_size > 0 && two_size > 0) && ((two_size > one_size) || (one_size == two_size))) {
				Table<String,String,Float> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					Float actualProjectedBalance = cell.getValue();
					
					Float expectedProjectedBalance = getEmpsTimeOffBalances1.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.equals(expectedProjectedBalance)) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances1.remove(empFullName, timeOffName);
							
						}
					}
					
				}
				
				LOGGER.info("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, Float> cell: matchedData.cellSet()) {
						LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				LOGGER.info("Unmatched Data in First file is: ");
				LOGGER.info("==================================");
				if (getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() == 0) {
					LOGGER.info("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() > 0) {
					for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances1.cellSet()) {
						LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
						timeOffBalFileComments.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue()+" Not Matched");
					}
				}
				
				LOGGER.info("Second File Data: ");
				for (Table.Cell<String, String, Float> cell: getEmpsTimeOffBalances2.cellSet()) {
					LOGGER.info(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					carryOverBalFileComments.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue()+" Remaining Data");
				}
				
			} else {
				LOGGER.info("Either any file having no data. Please check the files.");
				LOGGER.info("getEmpsTimeOffBalances1: "+getEmpsTimeOffBalances1);
				LOGGER.info("getEmpsTimeOffBalances2: "+getEmpsTimeOffBalances2);
			}
			
			if (timeOffBalFileComments != null && timeOffBalFileComments.size() > 0) {
				WriteExcel.WriteExcelData(filePath, companyName, "_ComapreTwoReport_TimeOff_Bal.xlsx", timeOffBalFileComments);
			}
			
			if (carryOverBalFileComments != null && carryOverBalFileComments.size() > 0) {
				WriteExcel.WriteExcelData(filePath, companyName, "_ComapreTwoReport_CarryOver_Bal.xlsx", carryOverBalFileComments);
			}
			
		} catch (Exception e) {
			LOGGER.error("== Exception in CompareTwoFile() is : "+e,e); 
		}
	}

	/**
	 * Method getEmployees
	 * @param fileName
	 * @return List object
	 */
	public static Table<String,String,Float> getEmpsTimeOffBalances(String fileName) {
		
		System.out.println("== Entered into getEmployees() of ReadExcel clas ==");
		Workbook workbook = null;
		Sheet sheet = null;
		DataFormatter dataFormatter = null;
		Table<String,String,Float> empsTimeOffBalances = null;
		
		try {
			
			empsTimeOffBalances = HashBasedTable.create();
			workbook = WorkbookFactory.create(new File(fileName));
			sheet = workbook.getSheetAt(0);
			dataFormatter = new DataFormatter();
			for (Row row: sheet) {
				List<String> name = new ArrayList<String>();
	            for(Cell cell: row) {
	                String cellValue = dataFormatter.formatCellValue(cell);
	                cellValue = (cellValue == null) ? "" : cellValue;
	                name.add(cellValue);
	            }
	            String empFullName = name.get(0);
	            empFullName = (empFullName != null) ? empFullName.trim() : empFullName;
	            
	            String timeOffName = name.get(1);
	            timeOffName = (timeOffName != null) ? timeOffName.trim() : timeOffName;
	            
	            String projectedBalance = name.get(2);
	            projectedBalance = (projectedBalance != null) ? projectedBalance.trim() : projectedBalance;
	            Float bal = (projectedBalance != null && !projectedBalance.trim().equals("")) ? Float.parseFloat(projectedBalance.trim()) : 0f;
	            
	            empsTimeOffBalances.put(empFullName, timeOffName, bal);
	            
	        }
			workbook.close();
			System.out.println("Employee's Time Off Balances Details: "+empsTimeOffBalances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empsTimeOffBalances;
	}
}
