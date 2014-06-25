package nl.mpcjanssen.uc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.util.Calendar;

public class UbiquitousCaptureActivity extends Activity  {
 
    CaptureView mSignature;

    public String current = null;
    String folder;
    File mypath;
    int idx = 1;
 
    private String uniqueId;
    private MediaScannerConnection mediaScannerConn;
    private View btnUndo;
    private View btnClear;
    private View btnSave;
    private View btnSettings;

    public boolean isCloseOnSave() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean("pref_close_on_save", true);
    }

    public void initCanvas() {
        mSignature = (CaptureView) findViewById(R.id.image);
        mSignature.setBackgroundColor(Color.WHITE);
        mSignature.setToggleButton(new CaptureView.ToggleButton() {
            @Override
            public void setEnable(boolean state) {
                setSaveState(state);
            }
        });
    }

    private void setSaveState(boolean state) {
        btnSave.setEnabled(state);
        btnClear.setEnabled(state);
        btnUndo.setEnabled(state);
    }

    private void clearCanvas() {
        Log.v("log_tag", "Panel Cleared");
        mSignature.clear();
    }

    private void saveCanvas() {
        Log.v("log_tag", "Panel Saved");
        save();
        mSignature.clear();
        if (isCloseOnSave()) {
            finish();
        }
    }

    private void undoCanvas() {
        mSignature.undo();
    }

    private boolean isSaveEnabled() {
        if (btnSave!=null) {
            return btnSave.isEnabled();
        }
        return true;
    }

    private void openSettings() {
        startActivityForResult(new Intent(this,SettingsActivity.class),0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnUndo = findViewById(R.id.undo);
        btnSave = findViewById(R.id.save);
        btnClear = findViewById(R.id.clear);
        btnSettings = findViewById(R.id.settings);

        btnUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                undoCanvas();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCanvas();
            }
        });

        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCanvas();
            }
        });

        btnSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });
        
        folder = Environment.getExternalStorageDirectory() + "/" + getString(R.string.external_dir) + "/";
        initCanvas();
        if (mSignature.isEmpty()) {
            setSaveState(false);
        }
    }


    @Override
    public void onBackPressed() {
        Log.w("GetSignature", "onDestory");
        if (isSaveEnabled()) {
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
        mediaScannerConn = new MediaScannerConnection(this,new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                mediaScannerConn.scanFile(mypath.getAbsolutePath(),null);
            }

            @Override
            public void onScanCompleted(String s, Uri uri) {
                mediaScannerConn.disconnect();
            }
        });
        mediaScannerConn.connect();
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
