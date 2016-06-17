import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class CalendarUI  extends JFrame implements ActionListener{
	
	private	JComboBox<String> year, month, day, hour, minute, users;
	private JTextField beginDateText, endDateText;
	private JButton beginDateBtn, endDateBtn, submitBtn, rewindBtn;
	private Map<String,String> colorMap;
	private static final String fin = "users.txt";
	private CalendarCtrl ctrl;
	
	public CalendarUI(String s) throws IOException
	{
		super(s);
		beginDateText = new JTextField();
		endDateText = new JTextField();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		

		
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
		datePicker.add(new JLabel("�~"));
		
		datePicker.add(month);
		datePicker.add(new JLabel("��"));
		
		datePicker.add(day);
		datePicker.add(new JLabel("��"));
		
		datePicker.add(hour);
		datePicker.add(new JLabel("��"));
		
		datePicker.add(minute);
		datePicker.add(new JLabel("��"));
		
		
		/*
		 * Beginning of main area
		 * 
		 */
		Container mainArea = new Container();
		mainArea.setLayout(new GridLayout(3,2));
		
		/* begin date*/
		mainArea.add(beginDateText);
		beginDateBtn = new JButton("�]�w���}�l");
		beginDateBtn.addActionListener(this);
		mainArea.add(beginDateBtn);
		
		/* end date */
		mainArea.add(endDateText);
		endDateBtn = new JButton("�]�w������");
		endDateBtn.addActionListener(this);
		mainArea.add(endDateBtn);
		
		/* user names */
		mainArea.add(users);
		
		/* submit button */
		submitBtn = new JButton("�T�w");
		submitBtn.addActionListener(this);
		
		
		/* rewind button */
		rewindBtn = new JButton("�^��");
		rewindBtn.addActionListener(this);
		
		/* pack submit and rewind button */
		Container btnContainer = new Container();
		btnContainer.setLayout(new FlowLayout());
		btnContainer.add(submitBtn);
		btnContainer.add(rewindBtn);
		mainArea.add(btnContainer);
	
		pane.add(datePicker, BorderLayout.NORTH);
		pane.add(mainArea, BorderLayout.CENTER);
	}

	private static void createAndShowGUI() throws IOException{
		CalendarUI frame = new CalendarUI("�а��n�J");
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
						JOptionPane.showMessageDialog(this, "�s�W�ƥ󥢱�");
						ioexception.printStackTrace();
					}
				}
			}else if (s == rewindBtn)
			{
				
				int confirm = JOptionPane.showConfirmDialog(this, "�T�w�n�����W�@�Өƥ�?","",JOptionPane.YES_NO_OPTION);
				if (confirm == 0)
				{
					try{
						ctrl.delEvent();
					}  catch (IOException ioexception)
					{
						JOptionPane.showMessageDialog(this, "�S���W�@�Өƥ�");
						ioexception.printStackTrace();
					}
				}
			}
		}else if (e.getSource() instanceof JComboBox)
		{
			hour.setSelectedIndex(0);
		}
		
		
		
	}
}