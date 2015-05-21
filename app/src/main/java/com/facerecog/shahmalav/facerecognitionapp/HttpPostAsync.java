package com.facerecog.shahmalav.facerecognitionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malav on 5/16/2015.
 */
public class HttpPostAsync extends AsyncTask<String, Void, String> {

    //Declaration of the interface
    public HttpAsyncResponse responseInterface = null;
    String [] parseJSON = null;

    /**
     * Push the image base64 string to the server as a post request
     * receive a response in JSON format from the http post.
     * Once the post request is complete send the result JSON to MainActivity
     */
    @Override
    protected String doInBackground(String... params) {

        BufferedReader inBuffer = null;
        // url for the post api
      //  String url = "http://54.152.252.216/api/find-face.php";
        String url = "http://52.11.86.64/api/find-face2.php";
        String result = "fail";

        try {
            //create HTTP Post connection
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);

            //Prepare parameters/Payload for Post request
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("image", params[0]));

            //Encode parameters
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParameters);

            //
            request.setEntity(formEntity);
            //Execute HTTP Post request and receive JSON response
            HttpResponse responseText = httpClient.execute(request);
            HttpEntity httpEntity = responseText.getEntity();
           InputStream is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
           String json = sb.toString();
            //Convert HTTPResponse to UTF-8 formatted String
           // String json = EntityUtils.toString(responseText.getEntity(), "UTF-8");
            result=json;
            /*JSONObject jObject = new JSONObject(json);
            parseJSON[0]= jObject.getString("Nearest");
            parseJSON[1] = jObject.getString("Confidence");
            parseJSON[2] = jObject.getString("Result");*/
            Log.e("TheJsonResult", json);

        } catch(Exception e) {
            // Get message of the exception
            result = e.getMessage();
        } finally {
            if (inBuffer != null) {
                try {
                    inBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  result;
    }

    /**
     * Called when the post request is executed. Grabs the response and pass it as page.
     * @param result
     */
    protected void onPostExecute(String result)
    {

        JSONObject jObject = null;
        String parseJSON = null;
        try {
            jObject = new JSONObject(result);
            Log.e("CheckThisOUt : ", jObject.toString());
            /*parseJSON[0]= jObject.getString("Nearest");
            parseJSON[1] = jObject.getString("Confidence");
            parseJSON[2] = jObject.getString("Result");*/
            parseJSON = jObject.getString("Confidence");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        responseInterface.processFinish(parseJSON);
    }
}
