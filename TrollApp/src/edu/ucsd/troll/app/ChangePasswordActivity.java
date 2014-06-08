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




/**
 * Created by shalomabitan on 5/22/14.
 */
public class ChangePasswordActivity extends Activity {

    //init the progress bar
    private ProgressDialog pDialog;

    private static String resetPasswordUrl = "http://troll.everythingcoed.com/user/reset";
    
    private static final String TAG_APIKEYVALUE = "OlDwjUX0fQSm0vAy2D3fy4uCZ108bx5N";
    private static final String TAG_APIKEYNAME= "api_key";
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESULT = "result";
    private static final String TAG_USER = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_RESETPASSWORD = "reset_password";
    private static final String TAG_RESETCODE = "reset_code";
    private static final String TAG_FIRSTNAME = "first_name";
    private static final String TAG_LASTNAME = "last_name";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_USERTOKEN = "presist_code";


    //login manager
    LoginManager login;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> userList;

    List<NameValuePair> params = new ArrayList<NameValuePair>();

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //login button
    Button submitPasswordChangeBtn;

    //Edittext
    EditText usernameTextBox, resetCodeTextBox, passwordTextBox, confirmPasswordTextBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
      
        // Session Manager
        login = new LoginManager(getApplicationContext());

        // Edit text objects to take in input
        usernameTextBox = (EditText) findViewById(R.id.passChangeUsername);
        passwordTextBox = (EditText) findViewById(R.id.newPassword);
        confirmPasswordTextBox = (EditText) findViewById(R.id.confirmNewPassword);
         
        // submit password change button
        submitPasswordChangeBtn = (Button) findViewById(R.id.submitPasswordChange);

        // submit password change button click event
        submitPasswordChangeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	Intent passRetrieveIntent = getIntent();
                // Get username, password, and confirm password from EditText
                String username = usernameTextBox.getText().toString();
                String newPassword = passwordTextBox.getText().toString();
                String confirmPassword = confirmPasswordTextBox.getText().toString();
                String resetCode = passRetrieveIntent.getStringExtra(TAG_RESETCODE);
                
                //Check that the password and password confirmation match
                if(!confirmPassword.trim().equals(newPassword.trim())){
                	alert.showAlertDialog(ChangePasswordActivity.this, "Password and Password "
                			+ "confirmation do not match.", "Please reenter credentials", false);
                
                
                // Check that the required values are all filled
                }else if(username.trim().length() > 0 && newPassword.trim().length() > 0 &&
                		resetCode.trim().length() > 0){

                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_RESETPASSWORD, newPassword));
                    params.add(new BasicNameValuePair(TAG_RESETCODE, resetCode));
                    params.add(new BasicNameValuePair(TAG_APIKEYNAME, TAG_APIKEYVALUE));

                    new LoginInUser().execute();

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(ChangePasswordActivity.this, "Login failed..", "Please enter all fields", false);
                }

            }
        });
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class LoginInUser extends AsyncTask<Void, Void, String> {

        HashMap<String, String> user = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            APIServiceHandler sh = new APIServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(resetPasswordUrl, APIServiceHandler.POST, params);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                  JSONObject jsonObj = new JSONObject(jsonStr);

                    Log.d("Response: ", "=> " + jsonObj);
                    
                    //the overall return object
                    String responseReturn = jsonObj.getString(TAG_RESPONSE);
                    Log.d("Response: ", "=> " + responseReturn);

                    //get the result JSON response result
                    JSONObject resultObj = new JSONObject(responseReturn);

                    //get the value (success or fail)
                    String responseResult = resultObj.getString(TAG_RESULT);
                    Log.d("Response: ", "=> " + responseResult);

                    //return just fail otherwise continue
                    if(responseResult.equals("fail")){
                        return responseResult;
                    }

                    //Recording results returned from the API into strings

                    String responseUser = resultObj.getString(TAG_USER);
                    Log.d("user: ", "=> " + responseUser);

                    JSONObject userObj = new JSONObject(responseUser);
                    //user id
                    String responseUserId = userObj.getString(TAG_ID);
                    Log.d("id: ", "=> " + responseUserId);
                    //user username
                    String responseUserName = userObj.getString(TAG_USERNAME);
                    Log.d("username: ", "=> " + responseUserName);
                    //user email
                    String responseUserEmail = userObj.getString(TAG_EMAIL);
                    Log.d("email: ", "=> " + responseUserEmail);
                    //user first name
                    String responseUserFirstName = userObj.getString(TAG_FIRSTNAME);
                    Log.d("first_name: ", "=> " + responseUserFirstName);
                    //user last name
                    String responseUserLastName = userObj.getString(TAG_LASTNAME);
                    Log.d("last_name: ", "=> " + responseUserLastName);


                    String responseFavorite = resultObj.getString(TAG_FAVORITES);
                    Log.d("last_name: ", "=> " + responseFavorite);

                    String responseUserToken = resultObj.getString(TAG_USERTOKEN);
                    Log.d("last_name: ", "=> " + responseUserToken);

                    // adding each child node to HashMap key => value
                    //contact.put(TAG_ID, id);
                    user.put(TAG_ID, responseUserId);
                    user.put(TAG_USERNAME, responseUserName);
                    user.put(TAG_EMAIL, responseUserEmail);
                    user.put(TAG_FIRSTNAME, responseUserFirstName);
                    user.put(TAG_LASTNAME, responseUserLastName);
                    
                    //add the favorites as a string
                    user.put(TAG_FAVORITES, responseFavorite);
                    //add the token
                    user.put(TAG_USERTOKEN, responseUserToken);

                    return responseResult;

                } catch (JSONException e) {
                    e.printStackTrace();
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

            if(result.equals("success")){

                // Creating user login session
                // For testing i am string name, email as follow
                // Use user real data
                login.createLoginSession(
                        user.get(TAG_ID),
                        user.get(TAG_USERNAME),
                        user.get(TAG_EMAIL),
                        user.get(TAG_FIRSTNAME),
                        user.get(TAG_LASTNAME),
                        user.get(TAG_USERTOKEN)
                );

                login.createUserFavorites(user.get(TAG_FAVORITES));

                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                finish();

            }else{
                // username / password doesn't match
                alert.showAlertDialog(ChangePasswordActivity.this, "Login failed..", 
                		"Internal Error", false);
            }
 
        }

    }


}
