package eu.isawsm.accelerate;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


import eu.isawsm.accelerate.Model.Driver;

/**
 * Created by Oliver on 30.01.2015.
 */
public class Proxy extends AsyncTask <String, Integer, Driver> {

    @Override
    protected Driver doInBackground(String... strings) {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("getUser", strings[0]));

            //Create the HTTP request
            HttpParams httpParameters = new BasicHttpParams();

            //Setup timeouts
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost("http://192.168.1.7/axelerate/login.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity);


            System.out.println("Post Result: " + result);
            // Create a JSON object from the request response
            JSONObject jsonObject = new JSONObject(result);

            //Retrieve the data from the JSON object

            int id = jsonObject.getInt("ID");
            String strFirstName = jsonObject.getString("Firstname");
            String lstname = jsonObject.getString("Lastname");
            String acronym = jsonObject.getString("Acronym");
            String image = jsonObject.getString("Image");
            String mail = jsonObject.getString("Mail");
            String password = jsonObject.getString("Password");
            String salt = jsonObject.getString("Salt");


          //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(, Uri.fromFile(new File(image)));

            return new Driver(strFirstName,lstname,acronym, null, URI.create(mail));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
