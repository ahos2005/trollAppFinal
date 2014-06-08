package edu.ucsd.troll.app;

/**
 * Created by shalomabitan on 5/22/14.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class MenuActivity extends ListActivity {
	
    private static String locationUrl = "http://troll.everythingcoed.com/get/locations";

    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_LOCATIONS = "locations";
    private static final String TAG_ID = "id";
    private static final String TAG_LOCATIONSID = "id";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_TITLE = "location_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_LASTNAME = "last_name";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_HOURS = "hours";
    private static final String TAG_OPEN = "open";
    private static final String TAG_CLOSED = "closed";
    private static final String TAG_USERTOKEN = "persist_code";
    private static final String TAG_MENUID = "menus_id";

    private static final String TAG_SORT = "sort_by";
    private static final String TAG_SORT_ORDER = "order_by";
    String[] daysOfWeek = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    
    LocationAPIManager locationsStorage;
    JSONArray locations = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> locationList;
    ArrayList<HashMap<String, String>> hoursList;
    List<HashMap<String, String>> fillHours;
    
    ArrayList<HashMap<String, String>> menuList;

    
    String locationAPIResult;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
  
      
        
       locationsStorage = new LocationAPIManager(getApplicationContext());
        
       locationAPIResult = locationsStorage.getLocations();
        
       locationList = new ArrayList<HashMap<String, String>>();
       locationList = new ArrayList<HashMap<String, String>>();
       hoursList = new ArrayList<HashMap<String, String>>();
       fillHours = new ArrayList<HashMap<String, String>>();
       
       setUpLocations(locationAPIResult);
        
        
       ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String rating = ((TextView) view.findViewById(R.id.rating))
                        .getText().toString();
                String category = ((TextView) view.findViewById(R.id.category))
                        .getText().toString();     
                String menuID = ((TextView) view.findViewById(R.id.menu_id))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        LocationMenuActivity.class);
                in.putExtra(TAG_TITLE, title);
                in.putExtra(TAG_ADDRESS, rating);
                in.putExtra(TAG_TITLE, category);
                in.putExtra(TAG_MENUID, menuID);
                in.putExtra(TAG_SORT,"simple");
                in.putExtra(TAG_SORT_ORDER,"asc");
                startActivity(in);

            }
        });
        
        ListAdapter adapter = new SimpleAdapter(
                MenuActivity.this, fillHours,
                R.layout.menu_list, new String[] { TAG_TITLE, TAG_MENUID,
                "Day0","Open0","Closed0",
                "Day1","Open1","Closed1",
                "Day2","Open2","Closed2",
                "Day3","Open3","Closed3",
                "Day4","Open4","Closed4",
                "Day5","Open5","Closed5",
                "Day6","Open6","Closed6"},
                new int[] { R.id.title, R.id.menu_id,
                R.id.sun,R.id.sunOp,R.id.sunCl,
                R.id.mon,R.id.monOp,R.id.monCl,
                R.id.tue,R.id.tueOp,R.id.tueCl,
                R.id.wed,R.id.wedOp,R.id.wedCl,
                R.id.thur,R.id.thurOp,R.id.thurCl,
                R.id.fri,R.id.friOp,R.id.friCl,
                R.id.sat,R.id.satOp,R.id.satCl});

        setListAdapter(adapter);
    }
		
    private void setUpLocations(String locationsArray) {
	   
	   Log.d("Location String", locationsArray);

		try{
			JSONObject jsonObj= new JSONObject(locationsArray);
    
            // Getting JSON Array node
            locations = jsonObj.getJSONArray(TAG_LOCATIONS);
            
            Log.d("individual location: ", "=> " + locations);

            // looping through All Contacts
            for (int i = 0; i < locations.length(); i++) {
                
            	JSONObject c = locations.getJSONObject(i);
                
                Log.d("individual location: ", "=> " + c);
                 
                String id = c.getString(TAG_LOCATIONSID);
                Log.d("id: ", "=> " + id);
                String lat = c.getString(TAG_LAT);
                Log.d("lat: ", "=> " + lat);
                String lng = c.getString(TAG_LNG);
                Log.d("lng: ", "=> " + lng);
                String address = c.getString(TAG_ADDRESS);
                Log.d("address: ", "=> " + address);
                String title = c.getString(TAG_TITLE);
                Log.d("title: ", "=> " + title);  
                String menuID = c.getString(TAG_MENUID);
                Log.d("menu id: ", "=> " + menuID);
                String hrs = c.getString(TAG_HOURS);
                Log.d("Hours: ", "=> " + hrs);
                
                setUpHours(hrs);

                HashMap<String, String> hours = new HashMap<String, String>();
            	hours.put(TAG_TITLE, title);
            	hours.put(TAG_MENUID, menuID);
            	for(int k=0; k<7; k++) {
                    hours.put("Day"+ k, daysOfWeek[k]);
                    hours.put("Open"+ k, hoursList.get(0).get(daysOfWeek[k]));
                    hours.put("Closed"+ k, hoursList.get(1).get(daysOfWeek[k]));
            	}
            	fillHours.add(hours);     
            	
                HashMap<String, String> locationHash = new HashMap<String, String>();
                
                Log.d("hash map: ", "=> " + "become active");

                // adding each child node to HashMap key => value
                locationHash.put(TAG_LOCATIONSID, id);
                Log.d("hash map: ", "=> " + "put id");
                
                locationHash.put(TAG_LAT, lat);
                Log.d("hash map: ", "=> " + "put lat");

                locationHash.put(TAG_LNG, lng);
                Log.d("hash map: ", "=> " + "put lng");

                locationHash.put(TAG_TITLE, title);
                Log.d("hash map: ", "=> " + "put title");
                
                locationHash.put(TAG_MENUID, menuID);
                Log.d("hash map: ", "=> " + "put menu");


                // adding locations to locations list
                locationList.add(locationHash);
                
                Log.d("list: ", "=> " + "added");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    	
    }
    
	private void setUpHours(String hours) {
    	
    	Log.d("Hours", hours);
    	
    	try {
            JSONObject preObj = new JSONObject(hours);
            JSONObject jsonObj = preObj.getJSONObject("hours");
            Log.d("JSONOBJ", jsonObj.toString());
            JSONObject opObj = jsonObj.getJSONObject("open");
            Log.d("result", opObj.toString());
            JSONObject clObj = jsonObj.getJSONObject("close");
            Log.d("result", clObj.toString());
            
            String sunO = opObj.getString("Sunday");
            Log.d("Sunday: ", "=> " + sunO);
            String monO = opObj.getString("Monday");
            Log.d("Monday: ", "=> " + monO);
            String tueO = opObj.getString("Tuesday");
            Log.d("Tuesday: ", "=> " + tueO);
            String wedO = opObj.getString("Wednesday");
            Log.d("Wednesday: ", "=> " + wedO);
            String thurO = opObj.getString("Thursday");
            Log.d("Thursday: ", "=> " + thurO);
            String friO = opObj.getString("Friday");
            Log.d("Friday: ", "=> " + friO);
            String satO = opObj.getString("Saturday");
            Log.d("Saturday: ", "=> " + satO);

            // tmp hashmap for single contact
            HashMap<String, String> openHash = new HashMap<String, String>();
            
            openHash.put("Sunday", sunO);
            openHash.put("Monday", monO);
            openHash.put("Tuesday", tueO);
            openHash.put("Wednesday", wedO);
            openHash.put("Thursday", thurO);
            openHash.put("Friday", friO);
            openHash.put("Saturday", satO);
            
            String sunC = clObj.getString("Sunday");
            Log.d("Sunday: ", "=> " + sunC);
            String monC = clObj.getString("Monday");
            Log.d("Monday: ", "=> " + monC);
            String tueC = clObj.getString("Tuesday");
            Log.d("Tuesday: ", "=> " + tueC);
            String wedC = clObj.getString("Wednesday");
            Log.d("Wednesday: ", "=> " + wedC);
            String thurC = clObj.getString("Thursday");
            Log.d("Thursday: ", "=> " + thurC);
            String friC = clObj.getString("Friday");
            Log.d("Friday: ", "=> " + friC);
            String satC = clObj.getString("Saturday");
            Log.d("Saturday: ", "=> " + satC);

            // tmp hashmap for single contact
            HashMap<String, String> closedHash = new HashMap<String, String>();
            
            closedHash.put("Sunday",sunC);
            closedHash.put("Monday",monC);
            closedHash.put("Tuesday",tueC);
            closedHash.put("Wednesday",wedC);
            closedHash.put("Thursday",thurC);
            closedHash.put("Friday",friC);
            closedHash.put("Saturday",satC);
            
            Log.d("hash map: ", "=> " + "become active");


            // adding locations to locations list
            hoursList.add(openHash);
            hoursList.add(closedHash);
                
                Log.d("list: ", "=> " + "added");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    	
   }
   	
}