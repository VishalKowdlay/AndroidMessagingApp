package com.example.messagingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextMessageReceiver receiver;
    TextView lastSMS, stateView;
    String address = "";
    int state;
    //start, question, answer, end
    String[] start = new String[]{"Hi", "Hello", "Wassup", "Yo"};
    String[] question = new String[]{"Im fine and you?", "Im great and How are you doing?", "Just chillin bro How have u been?", "Awesome! How has school been?"};
    String[] answers = new String[]{"I am doing well", "Life is great", "Ngl school got me lackin", "Nm just watching tv"};
    String[] end = new String[]{"Ok I must be on my way. :)", "Bye lol", "I will ttyl", "it was nice talking to you!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission(Manifest.permission.RECEIVE_SMS);
        setPermission(Manifest.permission.SEND_SMS);
        //setPermission(Manifest.permission.READ_PHONE_STATE);

        lastSMS = findViewById(R.id.msg);
        stateView = findViewById(R.id.state);
        receiver = new TextMessageReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        this.registerReceiver(receiver, filter);

        setState(0);
    }

    private void setPermission(String permission){
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }


    public Runnable getRunnable(final String textMessage, final SmsManager smsManager){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(address != null){
                    lastSMS.setText("Message Sent!");
                    smsManager.sendTextMessage(address.substring(1),null, textMessage,null,null);
                }
            }
        };
        return runnable;
    }

    public void reply(String reply) {
        SmsManager smsManager = SmsManager.getDefault();
        int randomTimeDelay = (int)(Math.random()*5000)+1000;
        Handler handler = new Handler();
        handler.postDelayed(getRunnable(reply, smsManager), randomTimeDelay);
    }


    public class TextMessageReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[])bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            String msg= "";

            for(int x = 0; x < messages.length; x++) {
                messages[x] = SmsMessage.createFromPdu(((byte[]) pdus[x]), bundle.getString("format"));
                msg += messages[x].getMessageBody();
            }

            lastSMS.setText("You have been messaged \"" + msg + "\" from \""+ messages[0].getOriginatingAddress() +"\".");

            address = messages[0].getOriginatingAddress();

            int rand = (int)(Math.random()*3);

            switch (state)
            {
                case 0:
                    createStartPhaseMsg(msg);
                    break;
                case 1:
                    createQuestPhaseMsg(msg);
                    break;
                case 2:
                    createAnswerPhaseMsg(msg);
                    break;
                case 3:
                    createEndingPhaseMsg(msg);
                    break;
                case 4:
                    createEndPhaseMsg(msg);
                    break;
            }

            setState(state + 1);
        }
    }

    public void createStartPhaseMsg(String recievedMsg){
       if(recievedMsg.toUpperCase().equals("BYE")) {
           reply("UM... why did u text me to say bye?");
           setState(state - 1);
       }else
           reply(start[(int)(Math.random()*3)]);
    }

    public void createQuestPhaseMsg(String recievedMsg){
        if(recievedMsg.toUpperCase().equals("HI")) {
            reply("Lmao whyd you say hi to me twice... kinda sussy?");
            setState(state - 1);
        }else
            reply(question[(int)(Math.random()*3)]);
    }

    public void createAnswerPhaseMsg(String recievedMsg){
        if(recievedMsg.toUpperCase().equals("OKAY")) {
            reply("Bro why did you say okay?");
            setState(state - 1);
        }else
            reply(answers[(int)(Math.random()*3)]);
    }

    public void createEndingPhaseMsg(String recievedMsg){
        if(recievedMsg.toUpperCase().equals("HI")) {
            reply("Wait I thought you were gonna dip lol");
            setState(state - 1);
        }else
            reply(end[(int)(Math.random()*3)]);
    }

    public void createEndPhaseMsg(String recievedMsg) {
        if(recievedMsg.toUpperCase().equals("HI"))
            reply("No... I'm saying I'm leaving");
        else
            reply("it really was great talking to you");
    }

    public void setState(int s) {
        this.state = s;
        String[] states = new String[]{"Start", "Greeting", "Questions", "Answers", "Conversation Finished"};
        if(state != -1)
            stateView.setText(states[state]);
    }


    @Override
    protected void onPause() {
        super.onPause();

//        if(receiver!=null)
//            this.unregisterReceiver(receiver);
    }

}