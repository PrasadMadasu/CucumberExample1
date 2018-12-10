package util;

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

public class CompareTwoExcelFiles {
	
	public static final String FILE_ONE = ".//src//com//examples//First.xlsx";
	public static final String FILE_TWO = ".//src//com//examples//Second.xlsx";
	
	public static void main(String[] args) {
		
		Table<String,String,String> getEmpsTimeOffBalances1 = null;
		Table<String,String,String> getEmpsTimeOffBalances2 = null;
		
		try {
			
			getEmpsTimeOffBalances1 = getEmpsTimeOffBalances(FILE_ONE);
			getEmpsTimeOffBalances2 = getEmpsTimeOffBalances(FILE_TWO);
			
			System.out.println("==============================================================");
			for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances1.cellSet()) {
				System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
			}
			System.out.println("==============================================================");
			for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances2.cellSet()) {
				System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
			}
			System.out.println("==============================================================");
			
			int one_size = (getEmpsTimeOffBalances1 != null) ? getEmpsTimeOffBalances1.size() : 0;
			int two_size = (getEmpsTimeOffBalances2 != null) ? getEmpsTimeOffBalances2.size() : 0;
			
			if ((one_size > 0 && two_size > 0) && (one_size > two_size)) {
				
				Table<String,String,String> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances1.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					String actualProjectedBalance = cell.getValue();
					
					String expectedProjectedBalance = getEmpsTimeOffBalances2.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.trim().equalsIgnoreCase(expectedProjectedBalance.trim())) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances2.remove(empFullName, timeOffName);
							
						}
					}
					
				}

				System.out.println("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, String> cell: matchedData.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Unmatched Data in Second file is: ");
				System.out.println("==================================");
				if (getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() == 0) {
					System.out.println("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances2 != null && getEmpsTimeOffBalances2.size() > 0) {
					for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances2.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				

				System.out.println("First File Data: ");
				for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances1.cellSet()) {
					System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
				}
				
			} else if ((one_size > 0 && two_size > 0) && ((two_size > one_size) || (one_size == two_size))) {
				Table<String,String,String> matchedData = HashBasedTable.create();
				for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances2.cellSet()) {
					
					String empFullName = cell.getRowKey();
					String timeOffName = cell.getColumnKey();
					String actualProjectedBalance = cell.getValue();
					
					String expectedProjectedBalance = getEmpsTimeOffBalances1.get(empFullName, timeOffName);
					
					if (actualProjectedBalance != null && expectedProjectedBalance != null) {
						if (actualProjectedBalance.trim().equalsIgnoreCase(expectedProjectedBalance.trim())) {
							
							matchedData.put(empFullName, timeOffName, expectedProjectedBalance);
							getEmpsTimeOffBalances1.remove(empFullName, timeOffName);
							
						}
					}
					
				}
				
				System.out.println("Matched Data: ");
				if (matchedData != null && matchedData.size() > 0) {
					for (Table.Cell<String, String, String> cell: matchedData.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Unmatched Data in First file is: ");
				System.out.println("==================================");
				if (getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() == 0) {
					System.out.println("All are matched, No unmatched data to display");
				} else if(getEmpsTimeOffBalances1 != null && getEmpsTimeOffBalances1.size() > 0) {
					for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances1.cellSet()) {
						System.out.println(cell.getRowKey()+" <> "+cell.getColumnKey()+" <> "+cell.getValue());
					}
				}
				
				System.out.println("Second File Data: ");
				for (Table.Cell<String, String, String> cell: getEmpsTimeOffBalances2.cellSet()) {
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
	 * Method getEmployees
	 * @param fileName
	 * @return List object
	 */
	public static Table<String,String,String> getEmpsTimeOffBalances(String fileName) {
		
		System.out.println("== Entered into getEmployees() of ReadExcel clas ==");
		Workbook workbook = null;
		Sheet sheet = null;
		DataFormatter dataFormatter = null;
		Table<String,String,String> empsTimeOffBalances = null;
		
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
	            
	            empsTimeOffBalances.put(empFullName, timeOffName, projectedBalance);
	            
	        }
			workbook.close();
			System.out.println("Employee's Time Off Balances Details: "+empsTimeOffBalances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empsTimeOffBalances;
	}
}
