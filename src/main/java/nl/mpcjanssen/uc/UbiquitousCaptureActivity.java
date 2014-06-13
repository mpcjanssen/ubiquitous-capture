package nl.mpcjanssen.uc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class UbiquitousCaptureActivity extends Activity {
 
    CaptureView mSignature;
    Button mClear, mGetSign;

    public String current = null;
    String folder;
    File mypath;
    int idx = 1;
 
    private String uniqueId;
    private File directory;

    public void initCanvas() {
        mSignature = (CaptureView) findViewById(R.id.image);
        mSignature.setBackgroundColor(Color.WHITE);
        mClear = (Button)findViewById(R.id.clear);
        mGetSign = (Button)findViewById(R.id.getsign);
        mSignature.setToggleButton(new CaptureView.ToggleButton() {
            @Override
            public void setEnable(boolean state) {
                mGetSign.setEnabled(state);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        folder = Environment.getExternalStorageDirectory() + "/" + getString(R.string.external_dir) + "/";
        initCanvas();
        mClear.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });
 
        mGetSign.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                Log.v("log_tag", "Panel Saved");
                    save();
                    mSignature.clear();
                    mGetSign.setEnabled(false);
                    finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.w("GetSignature", "onDestory");
        if (mGetSign.isEnabled()) {
            save();
        }
        super.onBackPressed();
    }

    private void save() {
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        idx = 1;
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_";
        current = uniqueId + idx + ".png";
        mypath= new File(directory,current);
        while(mypath.exists()) {
            idx++;
            current = uniqueId + idx + ".png";
            mypath= new File(directory,current);
        }
        mSignature.save(mypath);
        Toast.makeText(this, "Saved " + mypath.getName(), Toast.LENGTH_SHORT).show();
    }

    private String getTodaysDate() {
        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) + 
        ((c.get(Calendar.MONTH) + 1) * 100) + 
        (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));
 
    }
 
    private String getCurrentTime() {
 
        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) + 
        (c.get(Calendar.MINUTE) * 100) + 
        (c.get(Calendar.SECOND));
        Log.w("TIME:",String.valueOf(currentTime));
        return(String.valueOf(currentTime));
 
    }
}