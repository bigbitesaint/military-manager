import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JOptionPane;

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
import org.apache.poi.ss.util.WorkbookUtil;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

class xlsCtrl {
	private CalendarCtrl calCtrl;
	String fileName=null;
	
	public xlsCtrl(String fname, CalendarCtrl ctrl)
	{
		calCtrl = ctrl;
		fileName = fname;
		
	}
	
	public static Date toDate(DateTime dt)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Date ret = null; 
		try{
			ret=df.parse(dt.toStringRfc3339());
		} catch (ParseException e)
		{
			JOptionPane.showMessageDialog(null, "Time Parse Error.");
		}
		return ret;
	}
	
	public List<String> runCheck() throws IOException,InvalidFormatException
	{
		File xlsFile = new File(fileName);
		FileInputStream is = new FileInputStream(xlsFile);
		Workbook wb = WorkbookFactory.create(is);
		List<String> illegals = new ArrayList<String>();
		//TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		
		/* setup preset calendar */
		Calendar legitMorningCal = Calendar.getInstance();
		Calendar legitNoonCal = Calendar.getInstance();
		Calendar legitAfternoonCal = Calendar.getInstance();
		Calendar twelve_oclock = Calendar.getInstance();
		
		
		for (Row row: wb.getSheetAt(0))
		{
			if (row.getRowNum() < 5)
				continue;
			System.out.println("Row:"+row.getRowNum());
			Calendar base = Calendar.getInstance();
			Date date = row.getCell(3).getDateCellValue();
			boolean okay;			
			
			String user = row.getCell(2).getStringCellValue();
			if (date != null)
			{
				/* set presets */
				legitMorningCal.setTime(date);
				legitNoonCal.setTime(date);
				legitAfternoonCal.setTime(date);
				
				legitMorningCal.set(Calendar.HOUR_OF_DAY, 8);
				legitNoonCal.set(Calendar.HOUR_OF_DAY, 13);
				legitAfternoonCal.set(Calendar.HOUR_OF_DAY, 17);
				
				/* get an instance of 12:00 calendar */
				twelve_oclock.setTime(date);
				twelve_oclock.set(Calendar.HOUR_OF_DAY, 12);
				
				
				/* get remaining fields */
				Date morning, noon, afternoon;
				morning = row.getCell(6).getDateCellValue();
				noon = row.getCell(7).getDateCellValue();
				afternoon = row.getCell(8).getDateCellValue();				
				
				/* get corresponding events */
				Calendar calStart = Calendar.getInstance();
				calStart.setTime(date);
				calStart.set(Calendar.AM_PM,Calendar.AM);
				calStart.set(Calendar.HOUR, 8);
				
				Calendar calEnd = Calendar.getInstance();
				calEnd.setTime(date);
				calEnd.set(Calendar.AM_PM, Calendar.PM);
				calEnd.set(Calendar.HOUR, 5);
				List<Event> events = null;
				try{
					events = calCtrl.getEvents(calStart.getTime(), calEnd.getTime(), user);
				} catch (IOException e)
				{
					JOptionPane.showMessageDialog(null, "無法開啟Google Calendar");
					return null;
				}
				
				
				
				
				
				Calendar eventStart = Calendar.getInstance();
				Calendar eventEnd = Calendar.getInstance();
				DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				DateFormat simpleOutputFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				DateTime dt=null;
				int debt = 0;
				okay = false;
				if (morning != null)
				{
					
					Calendar mornCal = Calendar.getInstance();
					mornCal.setTime(morning);					
					if (mornCal.compareTo(legitMorningCal) <= 0)
						okay = true;
					else if (mornCal.get(Calendar.HOUR_OF_DAY) == 8 && mornCal.get(Calendar.MINUTE)<=30)
					{
						/* postpone the checking to afternoon check-in */
						debt = mornCal.get(Calendar.MINUTE);
						okay = true;
					}else
					{
						for (Event event: events)
						{
							if ( (dt=event.getEnd().getDate()) == null)
								dt = event.getEnd().getDateTime();
							eventEnd.setTime(xlsCtrl.toDate(dt));
						
							if (mornCal.compareTo(eventEnd) <= 0)
								okay = true;
						}
					}
					
				}else
				{
					
					for (Event event: events)
					{
						if ( (dt=event.getStart().getDate()) == null)
							dt = event.getStart().getDateTime();
						eventStart.setTime(xlsCtrl.toDate(dt));
						
						if ( (dt=event.getEnd().getDate()) == null)
							dt = event.getEnd().getDateTime();
						eventEnd.setTime(xlsCtrl.toDate(dt));
					
						if (eventStart.compareTo(legitMorningCal) == 0 && eventEnd.compareTo(twelve_oclock)>=0)
							okay = true;
					}
				}
				if (!okay)
				{
					if (morning == null)
						illegals.add(String.format("(%s) %s%s", user, simpleOutputFormat.format(date) ,"(早)"));
					else
						illegals.add(String.format("(%s) %s", user, outputFormat.format(morning)));
				}
				
				
				/* if noon checkin is missing */
				if (noon == null)
				{
					okay = false;
					for (Event event: events)
					{
						if ( (dt=event.getStart().getDate()) == null)
							dt = event.getStart().getDateTime();
						
						eventStart.setTime(xlsCtrl.toDate(dt));
						
						if ( (dt=event.getEnd().getDate()) == null)
							dt = event.getEnd().getDateTime();
						
						eventEnd.setTime(xlsCtrl.toDate(dt));
										
						if (twelve_oclock.compareTo(eventEnd) <= 0 && twelve_oclock.after(eventStart))
							okay = true;

					}
					if (!okay)
					{
						System.out.println("Noon not ok.\n");
						illegals.add(String.format("(%s) %s%s", user, simpleOutputFormat.format(date) ,"(午)"));
					}
				}else
				{
					Calendar noonCal = Calendar.getInstance();
					noonCal.setTime(noon);
					if (noonCal.compareTo(legitNoonCal) > 0)
					{
						System.out.println("Noon not ok.\n");
						illegals.add(String.format("(%s) %s", user, outputFormat.format(noon)));
					}
				}
				
				okay = false;
				if (afternoon != null)
				{
					Calendar anoonCal = Calendar.getInstance();
					anoonCal.setTime(afternoon);
					
					/* if there is a afternoon check-in and it's after the supposed time */
					if (anoonCal.compareTo(legitAfternoonCal) >= 0)
					{
						/* if there is a late morning check-in */
						if (debt > 0)
						{
							if (anoonCal.get(Calendar.HOUR_OF_DAY) == 17 && anoonCal.get(Calendar.MINUTE) >= debt)
								okay = true;
						}else
							okay = true;
					}
					
					/* if a valid afternoon check-in doens't exists */
					if (okay == false)
					{
						for (Event event: events)
						{
							if ( (dt=event.getStart().getDate()) == null)
								dt = event.getStart().getDateTime();
							
							eventStart.setTime(xlsCtrl.toDate(dt));
							
							if ( (dt=event.getEnd().getDate()) == null)
								dt = event.getEnd().getDateTime();
							
							eventEnd.setTime(xlsCtrl.toDate(dt));
											
							if (anoonCal.compareTo(eventStart) >= 0 && anoonCal.compareTo(eventEnd) <= 0)
								okay = true;
	
						}
					}
					
				}else
				{
					/* no late check-in is allowed even if afternoon on-leave is present */
					if (debt == 0)
					{
						for (Event event: events)
						{
					
							if ( (dt=event.getEnd().getDate()) == null)
								dt = event.getEnd().getDateTime();
							eventEnd.setTime(xlsCtrl.toDate(dt));
						
							if (eventEnd.compareTo(legitAfternoonCal)>=0)
								okay = true;
						}
					}
					
				}
				if (!okay)
				{
					System.out.println("Afternoon not ok.\n");
					if (afternoon == null)
						illegals.add(String.format("(%s) %s%s", user, simpleOutputFormat.format(date) ,"(傍)"));
					else
						illegals.add(String.format("(%s) %s", user, outputFormat.format(afternoon)));
				}
			}

		}
		
		is.close();
		return illegals;
	}
}
