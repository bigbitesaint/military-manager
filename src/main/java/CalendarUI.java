import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class CalendarUI  extends JFrame implements ActionListener{
	
	private	JComboBox<String> year, month, day, hour, minute, users;
	private JTextField beginDateText, endDateText, fileNameText;
	private JButton beginDateBtn, endDateBtn, submitBtn, rewindBtn, fileBtn;
	private Map<String,String> colorMap;
	private static final String fin = "users.txt";
	private CalendarCtrl ctrl;
	private JTabbedPane tabbedPane;
	JFileChooser fc;
	
	public CalendarUI(String s) throws IOException
	{
		super(s);
		
		/*
		 * 
		 * Create tab
		 * 
		 */
		tabbedPane = new JTabbedPane();
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		
		/*
		 * 
		 * file chooser
		 * 
		 */
		fc = new JFileChooser();

		
		panel1.setLayout(new BorderLayout());
		
		tabbedPane.add("請假查詢",panel1);
		tabbedPane.add("出缺席",panel2);
		
		
		beginDateText = new JTextField();
		endDateText = new JTextField();
		fileNameText = new JTextField();
		fileNameText.setPreferredSize(new Dimension(130,30));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		Container pane = this.getContentPane();
		pane.add(tabbedPane);
		addFirstTab(panel1);
		addSecondTab(panel2);
		

	}
	
	private void addFirstTab(JPanel p) throws IOException
	{
		/* initialize user data (colorMap)
		 * 
		 * 
		 */
		colorMap = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));
		String buffer;
		
		while ( (buffer=br.readLine()) != null)
		{
			String[] temp = buffer.split(",");
			colorMap.put(temp[0], temp[1]);
		}
		
		String[] userMenu = colorMap.keySet().toArray(new String[colorMap.size()]);
		users = new JComboBox<String>(userMenu);
		
		/*
		 * 
		 * Initialize controller
		 * 
		 */
		ctrl = new CalendarCtrl();
		ctrl.setColorMap(colorMap);
		
		
		
		/*
		 * Beginning of datePicker
		 * 
		 * 
		 */
		
		Container datePicker = new Container();
		datePicker.setLayout(new FlowLayout());
		datePicker.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		/* initialize combo box*/
		String[] yearMenu = {"2016","2017","2018"};
		year = new JComboBox<String>(yearMenu);
		year.addActionListener(this);
		
		/* initialize combo box*/
		String[] monthMenu = {"01","02","03","04","05","06","07","08","09","10","11","12"};
		month = new JComboBox<String>(monthMenu);
		month.addActionListener(this);
		
		/* initialize combo box*/
		String[] dayMenu = new String[31];
		for (int i=1; i<=31; ++i)
		{
			if (i<10)
				dayMenu[i-1] = "0"+i;
			else
				dayMenu[i-1] = ""+i;
		}
		day = new JComboBox<String>(dayMenu);
		day.addActionListener(this);

		
		
		/* initialize combo box*/
		String[] hourMenu = new String[10];
		for (int i=8,j=0; i<=17; ++i,++j)
		{
			if (i<10)
				hourMenu[j] = "0"+i;
			else
				hourMenu[j] = ""+i;
		}
		hour = new JComboBox<String>(hourMenu);

		
		/* initialize combo box*/
		String[] minuteMenu = {"00","30"};
		minute = new JComboBox<String>(minuteMenu);

		
		
		/* initialize to current time */
		Calendar cal = Calendar.getInstance();
		
		year.setSelectedItem(""+cal.get(Calendar.YEAR));
		if (cal.get(Calendar.MONTH)<10)
			month.setSelectedItem("0"+(cal.get(Calendar.MONTH)+1));
		else
			month.setSelectedItem(""+(cal.get(Calendar.MONTH)+1));
		
		if (cal.get(Calendar.DATE)<10)
			day.setSelectedItem("0"+cal.get(Calendar.DATE));
		else
			day.setSelectedItem(""+cal.get(Calendar.DATE));
		
		datePicker.add(year);
		datePicker.add(new JLabel("年"));
		
		datePicker.add(month);
		datePicker.add(new JLabel("月"));
		
		datePicker.add(day);
		datePicker.add(new JLabel("日"));
		
		datePicker.add(hour);
		datePicker.add(new JLabel("時"));
		
		datePicker.add(minute);
		datePicker.add(new JLabel("分"));
		
		
		/*
		 * Beginning of main area
		 * 
		 */
		Container mainArea = new Container();
		mainArea.setLayout(new GridLayout(3,2));
		
		/* begin date*/
		mainArea.add(beginDateText);
		beginDateBtn = new JButton("設定為開始");
		beginDateBtn.addActionListener(this);
		mainArea.add(beginDateBtn);
		
		/* end date */
		mainArea.add(endDateText);
		endDateBtn = new JButton("設定為結束");
		endDateBtn.addActionListener(this);
		mainArea.add(endDateBtn);
		
		/* user names */
		mainArea.add(users);
		
		/* submit button */
		submitBtn = new JButton("確定");
		submitBtn.addActionListener(this);
		
		
		/* rewind button */
		rewindBtn = new JButton("回溯");
		rewindBtn.addActionListener(this);
		
		/* pack submit and rewind button */
		Container btnContainer = new Container();
		btnContainer.setLayout(new FlowLayout());
		btnContainer.add(submitBtn);
		btnContainer.add(rewindBtn);
		mainArea.add(btnContainer);
	
		p.add(datePicker, BorderLayout.NORTH);
		p.add(mainArea, BorderLayout.CENTER);		
	}

	private void addSecondTab(JPanel p)
	{
		p.setLayout(new FlowLayout());
		fileBtn = new JButton("開啟記錄");
		fileBtn.addActionListener(this);
		p.add(fileNameText);
		p.add(fileBtn);
	}
	
	private static void createAndShowGUI() throws IOException{
		CalendarUI frame = new CalendarUI("請假登入");
		frame.pack();
		frame.setVisible(true);	
	}
	
	public static void main(String args[]) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
            	try{
            		createAndShowGUI();
            	} catch (IOException e)
            	{
            		JOptionPane.showMessageDialog(null,"Error opening file: "+fin);
            	}
            }
        });
	}


	public void actionPerformed(ActionEvent e){
		String selectedYear = (String)year.getSelectedItem();
		String selectedMonth = (String)month.getSelectedItem();
		String selectedDay = (String)day.getSelectedItem();
		String selectedHour = (String)hour.getSelectedItem();
		String selectedMinute = (String)minute.getSelectedItem();
		String selectedUser = (String)users.getSelectedItem();
		String result = String.format("%s-%s-%sT%s:%s:00", 
				selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
		if (e.getSource() instanceof JButton)
		{
			JButton s = (JButton)e.getSource();


			if (s == beginDateBtn)
				beginDateText.setText(result);
			else if (s == endDateBtn)
				endDateText.setText(result);
			else if (s == submitBtn)
			{
				String confirmMsg = String.format("Start: %20s\nEnd:  %20s\nUser: %s", beginDateText.getText(), endDateText.getText(), selectedUser);
				int confirm = JOptionPane.showConfirmDialog(this, confirmMsg,"",JOptionPane.YES_NO_OPTION);
				if (confirm == 0)
				{
					try{
						ctrl.addEvent(beginDateText.getText(), endDateText.getText(), selectedUser);
					} catch (IOException ioexception)
					{
						JOptionPane.showMessageDialog(this, "新增事件失敗");
						ioexception.printStackTrace();
					}
				}
			}else if (s == rewindBtn)
			{
				
				int confirm = JOptionPane.showConfirmDialog(this, "確定要取消上一個事件?","",JOptionPane.YES_NO_OPTION);
				if (confirm == 0)
				{
					try{
						ctrl.delEvent();
					}  catch (IOException ioexception)
					{
						JOptionPane.showMessageDialog(this, "沒有上一個事件");
						ioexception.printStackTrace();
					}
				}
			}else if (s == fileBtn)
			{
				int ret = fc.showOpenDialog(this);
				if (ret == JFileChooser.APPROVE_OPTION)
				{
					File f = fc.getSelectedFile();
					fileNameText.setText(f.getName());
					try{
						xlsCtrl xlsctrl = new xlsCtrl(f.getPath());
					} catch (IOException ioe)
					{
						JOptionPane.showMessageDialog(this, "無法存取檔案:"+f.getPath());
						ioe.printStackTrace();
					} catch (InvalidFormatException ife)
					{
						JOptionPane.showMessageDialog(this, "檔案格式錯誤:"+ife.getMessage());
					}
				}
			}
		}else if (e.getSource() instanceof JComboBox)
		{
			hour.setSelectedIndex(0);
		}
		
		
		
	}
}
