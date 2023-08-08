package com.wifisecure.unlockeez.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.wifisecure.unlockeez.R;

public class UnLockeEzPopWindow extends Activity{
    ImageView fWifi,sWifi,tWifi;
    TextView fWifiName,sWifiName,tWifiName;
    String fName,sName,tName;
    int fGetIntentStrLength,sGetIntentStrLength,tGetIntentStrLength;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow_wifiunlocker);
        fName = getIntent().getExtras().getString("name1");
        fGetIntentStrLength = getIntent().getExtras().getInt("strength1");
        sName = getIntent().getExtras().getString("name2");
        sGetIntentStrLength = getIntent().getExtras().getInt("strength2");
        tName = getIntent().getExtras().getString("name3");
        tGetIntentStrLength = getIntent().getExtras().getInt("strength3");

        fWifiName = findViewById(R.id.UnlockWifi1);
        sWifiName = findViewById(R.id.UnlockWifi2);
        tWifiName = findViewById(R.id.UnlockWifi3);

        fWifiName.setText(fName);
        sWifiName.setText(sName);
        tWifiName.setText(tName);

        fWifi = findViewById(R.id.ic_wifi1);
        sWifi = findViewById(R.id.ic_wifi2);
        tWifi = findViewById(R.id.ic_wifi3);

        setImage(fWifi, fGetIntentStrLength);
        setImage(sWifi, sGetIntentStrLength);
        setImage(tWifi, tGetIntentStrLength);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.6));

        fWifiName.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","1"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        sWifiName.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","2"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        tWifiName.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","3"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        Button retry = findViewById(R.id.Retry_UnlockWifi);
        retry.setOnClickListener(v -> finish());
    }

    // Show WiFi strength
    private void setImage(ImageView v, int strength) {
        if (strength > 66) {
            v.setImageResource(R.drawable.ic_excellent);
        } else if (strength >= 33 && strength < 66) {
            v.setImageResource(R.drawable.ic_good);
        } else if (strength > 0 && strength < 33) {
            v.setImageResource(R.drawable.ic_fair);
        } else {
            v.setImageResource(R.drawable.ic_poor);
        }
    }
}
