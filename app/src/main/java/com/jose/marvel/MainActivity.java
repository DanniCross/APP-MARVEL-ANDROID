package com.jose.marvel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputEditText CName;
    TextView Title;
    ImageView image;
    TextView Comics;
    TextView Series;
    TextView Stories;
    TextView Events;
    TextView Description;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CName = (TextInputEditText) findViewById(R.id.CName);
        Title = (TextView) findViewById(R.id.Title);
        image = (ImageView) findViewById(R.id.Image);
        Comics = (TextView) findViewById(R.id.ComicsL);
        Series = (TextView) findViewById(R.id.SeriesL);
        Stories = (TextView) findViewById(R.id.StoriesL);
        Events = (TextView) findViewById(R.id.EventsL);
        Description = (TextView) findViewById(R.id.DescL);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void GetData(View v) {
        String key = "efe12de1954ab5dfb332e64f10216094";
        String hash = "d502f069a2a4e2c3c6ec8a07710added";
        String path = "https://gateway.marvel.com/v1/public/characters?name=" + CName.getText() + "&ts=1&apikey=" + key + "&hash=" + hash;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            String json = "";

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            json = response.toString();

            JSONObject Marvel = new JSONObject(json);
            JSONObject data = new JSONObject(Marvel.get("data").toString());
            JSONArray results = new JSONArray(data.get("results").toString());
            JSONObject thumbnail = new JSONObject(new JSONObject(results.get(0).toString()).get("thumbnail").toString());
            JSONObject comics = new JSONObject(new JSONObject(results.get(0).toString()).get("comics").toString());
            JSONObject series = new JSONObject(new JSONObject(results.get(0).toString()).get("series").toString());
            JSONObject events = new JSONObject(new JSONObject(results.get(0).toString()).get("events").toString());
            JSONObject stories = new JSONObject(new JSONObject(results.get(0).toString()).get("stories").toString());

            CName.setText("");
            CName.setFocusable(false);

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            Title.setText(new JSONObject(results.get(0).toString()).get("name").toString());
            Description.setText(new JSONObject(results.get(0).toString()).get("description").toString());
            Comics.setText(comics.get("available").toString());
            Series.setText(series.get("available").toString());
            Stories.setText(stories.get("available").toString());
            Events.setText(events.get("available").toString());

            String imgPath = "" + thumbnail.get("path").toString() + "." + thumbnail.get("extension").toString();
            char [] ip = imgPath.toCharArray();
            String pi = "https://";
            String temp = "";
            boolean pas = false;

            for (char c : ip) {
                temp = String.format("%s%s", temp, c);
                if (temp.equals("http://") && !pas) {
                    temp = "";
                    pas = true;
                }
            }

            pi = String.format("%s%s", pi, temp);

            Picasso.with(this)
                    .load(pi)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(image);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CName.setFocusableInTouchMode(true);
                return false;
            }
        });
    }
}
