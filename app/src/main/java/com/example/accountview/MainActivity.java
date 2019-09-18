package com.example.accountview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    final String CLIENT_NAME = "Bob";
    //Button readButton = (Button) findViewById(R.id.button2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        TextView textHello = (TextView) findViewById(R.id.textHello);
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        super.onCreate(savedInstanceState);
        MyDBHandler dbHandler = new MyDBHandler(this, null);
        Clients client = new Clients();
        client.setID(11);
        client.setClientName(CLIENT_NAME);
        client.setBalance((float)100.0);
        if (dbHandler.findHandler(CLIENT_NAME) == null) {
            textBalance.setText(String.valueOf(dbHandler.addHandler(client)));
        }
        textHello.setText("Hello "+ CLIENT_NAME);
        init();
    }

    public void init() {
        tts = new TextToSpeech(this.getApplicationContext(),this);
    }

    @Override
    public void onInit(int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                    && result != TextToSpeech.LANG_AVAILABLE) {
                Toast.makeText(MainActivity.this, "Selected language not supported!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void readBalance(View view) {
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        String balanceText = "";

        MyDBHandler dbHandler = new MyDBHandler(this, null);
        Clients client = dbHandler.findHandler(CLIENT_NAME);
        if (client != null) {
            balanceText = "$" + Float.toString(client.getBalance());
        } else {
            balanceText = "$0.00";
        }
        textBalance.setText(balanceText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //balanceText = "$100.0";
            //String utteranceId=this.hashCode() + "";
            tts.speak(balanceText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            //balanceText = "One Hundred";
            tts.speak(balanceText,TextToSpeech.QUEUE_FLUSH, null);
        }
        //String result = dbHandler.loadHandler();
        //if (result != "") {
        //    textBalance.setText(result);
        //}
        //else {
        //    textBalance.setText("NOT FOUND");
        //}
    }

    public void vibeBalance(View view) {
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        String balanceText = "";
        if (textBalance.getText().toString().equals("*****.**")) {
            MyDBHandler dbHandler = new MyDBHandler(this, null);
            Clients client = dbHandler.findHandler(CLIENT_NAME);
            if (client != null) {
                balanceText = "$" + Float.toString(client.getBalance());
            } else {
                balanceText = "$0.00";
            }
            textBalance.setText(balanceText);
        } else {
            balanceText = textBalance.getText().toString();
        }
        //Toast.makeText(this, "vibe click works.",
        //        Toast.LENGTH_LONG).show();
        Vibrator vibraBal = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        /*
        ArrayList<Long> patternL = new ArrayList();
        Long[] testPattern = {new Long(125),new Long(125),new Long(375),new Long(125),
                new Long(125),new Long(125),new Long(375),new Long(125),new Long(125),
                new Long(125),new Long(375),new Long(375)};
        patternL.addAll(Arrays.asList(testPattern));
         */
        if (MorseCode.map.size() == 0) {
            MorseCode.initMap(); //initialize map first
        }
        ArrayList<Long> patternL = MorseCode.getPattern(balanceText.substring(1));
        long[] pattern = new long[patternL.size()];
        for (int i = 0; i < patternL.size(); i++ ) {
            pattern[i] = (patternL.get(i));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibe = VibrationEffect.createWaveform(pattern,-1);
            vibraBal.vibrate(vibe);
        } else {
            vibraBal.vibrate(pattern,-1);
        }
    }

    public void sendStateToMail(View view) {
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        String balanceText = textBalance.getText().toString();
        if (balanceText.equals("*****.**")) {
            MyDBHandler dbHandler = new MyDBHandler(this, null);
            Clients client = dbHandler.findHandler(CLIENT_NAME);
            if (client != null) {
                balanceText = "$" + Float.toString(client.getBalance());
            } else {
                balanceText = "$0.00";
            }
            textBalance.setText(balanceText);
        }
        final String MAIL_TEXT = balanceText;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender("dai.jiapeng54@gmail.com",
                            "W123elcome");
                    sender.sendMail("Your Account Information",
                            "Hello " + CLIENT_NAME + ", your account balance is " + MAIL_TEXT + ".",
                            "dai.jiapeng54@gmail.com", "dai.jiapeng54@gmail.com");
                    Toast.makeText(MainActivity.this, "Sending account statement to your email",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    Toast.makeText(MainActivity.this, "Sending mail fails",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }
    public void deposit(View view) {
        EditText amount = (EditText) findViewById(R.id.amount);
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        int id;
        String name;
        Float balance;
        Float addamount = Float.parseFloat(amount.getText().toString());
        MyDBHandler dbHandler = new MyDBHandler(this, null);
        Clients client = dbHandler.findHandler(CLIENT_NAME);
        if (client != null) {
            id = client.getID();
            name = client.getClientName();
            balance = client.getBalance();
        } else {
            id = 11;
            name = CLIENT_NAME;
            balance = new Float(0);
        }
        Float newbalance = balance + addamount;
        boolean result = dbHandler.updateHandler(id, name, newbalance);
        if (result) {
            textBalance.setText("$" + newbalance.toString());
            Toast.makeText(MainActivity.this,"Deposit " + "$" + addamount.toString(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,"Deposit failure!",Toast.LENGTH_LONG).show();
        }
    }
    public void withdraw(View view) {
        EditText amount = (EditText) findViewById(R.id.amount);
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        int id;
        String name;
        Float balance;
        Float minusamount = Float.parseFloat(amount.getText().toString());
        MyDBHandler dbHandler = new MyDBHandler(this, null);
        Clients client = dbHandler.findHandler(CLIENT_NAME);
        if (client != null) {
            id = client.getID();
            name = client.getClientName();
            balance = client.getBalance();
        } else {
            id = 11;
            name = CLIENT_NAME;
            balance = new Float(0);
        }
        Float newbalance = balance - minusamount;
        if (newbalance < 0) {
            Toast.makeText(MainActivity.this,"Transaction fails!",Toast.LENGTH_LONG).show();
            return;
        }
        boolean result = dbHandler.updateHandler(id, name, newbalance);
        if (result) {
            textBalance.setText("$" + newbalance.toString());
            Toast.makeText(MainActivity.this,"Withdraw " + "$" + minusamount.toString(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,"Withdraw failure!",Toast.LENGTH_LONG).show();
        }
    }
}
