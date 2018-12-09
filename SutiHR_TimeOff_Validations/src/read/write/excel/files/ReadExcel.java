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
	
}
