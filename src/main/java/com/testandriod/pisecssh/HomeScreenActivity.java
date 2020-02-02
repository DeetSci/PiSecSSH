package com.testandriod.pisecssh;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String Tag = "HomeScreenActivity";
    private String script;
    private String UName = "pi";
    private String PWord = "raspberry";
    private String HName = "192.168.1.245";
    private int Port = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }



    public void openSettings(View view) {
        Intent settings = new Intent(HomeScreenActivity.this,SettingsActivity.class);
        startActivity(settings);
    }

    @SuppressLint("StaticFieldLeak")
    public void startFunctionality() {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    executeRemoteCommand(UName, PWord, HName, Port);
                } catch (Exception e) {
                    Log.e(Tag,"Cannot make connection to Pi.");
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);
    }

    public void armPi(View view) {
        script = "cd Desktop; python3 -i ButtonMethod.py";
        startFunctionality();
    }

    public void disarmPi(View view) {
        script = "cd Desktop; python3 -i Disarm.py";
        startFunctionality();
    }

    public void executeRemoteCommand(String username, String password, String hostname, int port)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        prop.put("PreferredAuthentications", "password");
        session.setConfig(prop);
        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");

        // Execute command
        channelssh.setCommand(script);
        channelssh.connect();
    }
}