import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class xlsCtrl {
	
	public xlsCtrl(String fname) throws IOException,InvalidFormatException
	{
		File xlsFile = new File(fname);
		Workbook wb = WorkbookFactory.create(xlsFile);
		for (Row row: wb.getSheetAt(0))
		{
			if (row.getRowNum() < 5)
				continue;
			
			Date date = row.getCell(3).getDateCellValue();
			DateFormat df = new SimpleDateFormat("yy-mm-dd");
			System.out.println(row.getCell(2).getStringCellValue());
		}


		
	}
}
