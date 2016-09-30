import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class CalendarCtrl {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar Controller";

    private Map<String, String> colorMap;
    
    private com.google.api.services.calendar.Calendar service;
    
    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/calendar-java-quickstart.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static String CalendarID = "a0rlclvamvdved20bs5l0ontqg@group.calendar.google.com";
    //private static String CalendarID = "primary";

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR);

    private static List<String> prevEventId = new ArrayList<String>();
    
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            CalendarCtrl.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void delEvent() throws IOException
    {
    	if (prevEventId.isEmpty())
    		JOptionPane.showMessageDialog(null, "沒有上一個事件");
    	else
    	{
    		String id = prevEventId.remove(prevEventId.size()-1);
    		try{
    			service.events().delete(CalendarID, id ).execute();
    			JOptionPane.showMessageDialog(null, "刪除成功:"+id);
    		} catch (IOException e)
    		{
    			int confirm = JOptionPane.showConfirmDialog(null, "刪除失敗, 是否保留該事件紀錄?","",JOptionPane.YES_NO_OPTION);
    			if (confirm == 0)
    				prevEventId.add(id);
    		}
    		
    	}
    }
    

    public void addEvent(String startDate, String endDate, String eventOwner)  throws IOException
    {
        Event event = new Event()
        .setSummary(eventOwner)
        .setColorId(""+colorMap.get(eventOwner));
        //.setDescription(eventOwner);

        DateTime startDateTime = new DateTime(startDate+"+08:00");
        EventDateTime start = new EventDateTime()
        .setDateTime(startDateTime)
        .setTimeZone("Asia/Taipei");
        event.setStart(start);

        DateTime endDateTime = new DateTime(endDate+"+08:00");
        EventDateTime end = new EventDateTime()
        .setDateTime(endDateTime)
        .setTimeZone("Asia/Taipei");
        event.setEnd(end);

        event = service.events().insert(CalendarID, event).execute();
        prevEventId.add(event.getId());
        JOptionPane.showMessageDialog(null, "新增成功");
        System.out.printf("Event created: %s\n", event.getHtmlLink());

    }

    public List<Event> getEvents(Date startTime, Date endTime, String user) throws IOException
    {

        Events events = service.events().list(CalendarID)
            .setMaxResults(10)
            .setTimeMin(new DateTime(startTime))
            .setTimeMax(new DateTime(endTime))
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        List<Event> results = new ArrayList<Event>();
        for (Event event : items) {
        	if (event.getSummary().equals(user))
        		results.add(event);
        }
            	
    	
    	return results;
    }
    
    public void setColorMap(Map<String,String> hm)
    {
    	colorMap = hm;
    }
    
    public CalendarCtrl() throws IOException {
    	service=getCalendarService();
    }

}
