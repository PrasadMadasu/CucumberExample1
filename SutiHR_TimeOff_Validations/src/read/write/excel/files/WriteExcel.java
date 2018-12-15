package read.write.excel.files;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table;
/**
 * 
 * @author m.prasad
 *
 */
public class WriteExcel {
	
	public static final Logger LOGGER = Logger.getLogger(WriteExcel.class.getName());
	
	/**
	 * Method WriteExcelData
	 * @param fileName
	 * @param timeOffBalances
	 */
	public static void WriteExcelData(String filePath,String companyName,
									  String fileName,
						   	          Table<String,String,String> timeOffBalances) {
		
		LOGGER.info("== Entered into WriteExcelData() of WriteExcel class ==");
		
		XSSFWorkbook workbook = null;
		XSSFSheet spreadsheet = null;
		XSSFRow row = null;
		
		try {
		      
			workbook = new XSSFWorkbook();
		      
			spreadsheet = workbook.createSheet("Time Off Balances Not Matched Details");

			LinkedHashMap<String,Object[]> balancesInfo = new LinkedHashMap<String,Object[]>();
			Integer count = 1;
			balancesInfo.put(count.toString(), new Object[] {"Emp Code",
															 "Emp Name",
															 "Time Off", 
															 "Comments"});
			
			LOGGER.info("@@@ Time Off Balances Not Matched Data is: "+timeOffBalances);
			
			if (timeOffBalances != null && timeOffBalances.size() > 0) {
				for (Table.Cell<String,String,String> cell: timeOffBalances.cellSet()) {
					count++;
					
					String rowKey = cell.getRowKey();
					String[] strArray = rowKey.split("\\@");
					String empCode = strArray[0];
					String empName = strArray[1];
					
					balancesInfo.put(count.toString(), 
									 new Object[] {empCode,
											 	   empName,
											 	   cell.getColumnKey(),
											 	   cell.getValue()});
				}
			}
			
			Set<String> keyId = balancesInfo.keySet();
			
			int rowid = 0;
			
			for (String key: keyId) {
				row = spreadsheet.createRow(rowid++);
				Object [] objectArr = balancesInfo.get(key);
		        int cellid = 0;
		        for (Object obj : objectArr){
		        	Cell cell = row.createCell(cellid++);
		        	cell.setCellValue((String)obj);
		        }
			}
			
			FileOutputStream out = new FileOutputStream(new File(filePath+companyName+fileName));
			workbook.write(out);
			out.close();
			LOGGER.info("Write xlsx file successfully !!");
		} catch (Exception e) {
			LOGGER.error("Exception raised in WriteExcelData() is "+e,e);
		}
	}
}
