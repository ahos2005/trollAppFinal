package edu.ucsd.troll.app;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;






public class PasswordRetrievalActivity extends Activity {

    //init the progress bar
    private ProgressDialog pDialog;

    private static String recoverUrl = "http://troll.everythingcoed.com/user/recover";

    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_RESETCODE = "reset_code";
    //private static final String TAG_EMAIL = "email";



    // Hashmap for ListView
    ArrayList<HashMap<String, String>> userList;

    List<NameValuePair> params = new ArrayList<NameValuePair>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    
    //Submit button
    Button passwordRetrieveSubmitBtn;
    
    //Edit text
    EditText usernameTextBox;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_retrieval);

        // Username, Password input text
        usernameTextBox = (EditText) findViewById(R.id.edit_passretrieve_username);

        // submit username button
        passwordRetrieveSubmitBtn = (Button) findViewById(R.id.passwordRetrieveBtn);

        // Submit username button activity
        passwordRetrieveSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username from edit text box
            	String username = usernameTextBox.getText().toString();

                // Check if username textbox is filled
                if(username.trim().length() > 0){
                    
                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));

                    new RecoverPassword().execute();

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(PasswordRetrievalActivity.this, "Password retrieval failed..",
                    		"Please enter username", false);
                }
            }
        });
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class RecoverPassword extends AsyncTask<Void, Void, String> {

        HashMap<String, String> user = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PasswordRetrievalActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating API service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(recoverUrl, APIServiceHandler.POST, params);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                
            	try{
            		//Check if the code returned a failure
            		JSONObject jsonObj = new JSONObject(jsonStr);
            		Log.d("Response: ", "=> " + jsonObj);
            		
            		//the overall return object
            		String responseReturn = jsonObj.getString(TAG_RESPONSE);
            		Log.d("Response: ", "=> " + responseReturn);

            		//get the result JSON response and check if the call failed or succeeded
            		JSONObject resultObj = new JSONObject(responseReturn);
            		String responseResult = resultObj.getString(TAG_RESULT);
            		Log.d("Response: ", "=> " + responseResult);

            		//If the data submitted to the api is a failure, return the failure
            		if(responseResult.equals("fail")){
                      return responseResult;
                  	}
            		
            	//If no response section was found, It was a success the response is the code.
            	} catch (JSONException e) {
                    String resetCode = jsonStr.replace("\"", "");
                    Log.d("reset code: ", "=> " + resetCode);
                                                
                    user.put(TAG_RESETCODE, resetCode);
                    
                    //Store the reset code into a hashmap and return success
                    return "success";
            	}
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.d("RESULT: ", "=> " + result);

            //If the reset code is successfully found and stored
            if(result.equals("success")){

                // Starting ChangePasswordActivity
                Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                i.putExtra(TAG_RESETCODE, (String)user.get(TAG_RESETCODE));
                i.putExtra(TAG_USERNAME, (String)user.get(TAG_USERNAME));
                
                startActivity(i);
                finish();

            }else{
                //Ther username doesn't exist
                alert.showAlertDialog(PasswordRetrievalActivity.this, "Password retrieval failed..",
                		"Username is invalid", false);
            }
        }
    }
}


