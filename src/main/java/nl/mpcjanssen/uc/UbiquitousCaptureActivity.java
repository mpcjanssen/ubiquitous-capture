package nl.mpcjanssen.uc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class UbiquitousCaptureActivity extends Activity  {

    private static final String TAG = UbiquitousCaptureActivity.class.getName() ;
    CaptureView mSignature;

    public String current = null;
    File mypath;
    int idx = 1;
 
    private String uniqueId;
    private MediaScannerConnection mediaScannerConn;
    private View btnUndo;
    private View btnRedo;
    private View btnClear;
    private View btnSave;
    private View btnSettings;

    public boolean isCloseOnSave() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean("pref_close_on_save", false);
    }

    public boolean hasButtonsOnTop() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean("pref_buttons_top", false);
    }

    public void initCanvas() {
        mSignature = (CaptureView) findViewById(R.id.image);
        mSignature.setBackgroundColor(Color.WHITE);
        mSignature.setPath(((UCApplication)getApplication()).mPath);
        mSignature.setToggleButton(new CaptureView.ToggleButton() {
            @Override
            public void setEnable(boolean state) {
                setSaveState(state);
            }
        });
    }

    private void setSaveState(boolean state) {
        btnSave.setEnabled(state);
    }

    private void clearCanvas() {
        Log.v(TAG, "Panel Cleared");
        mSignature.clear();
    }

    private void saveCanvas() {
        Log.v(TAG, "Saving canvas");
        if (save()) {
            mSignature.clear();
            if (isCloseOnSave()) {
                finish();
            }
        }
    }

    private void undoCanvas() {
        mSignature.undo();
    }
    private void redoCanvas() {
        mSignature.redo();
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
        if (hasButtonsOnTop()) {
            setContentView(R.layout.main_top);
        } else {
            setContentView(R.layout.main);
        }

        btnUndo = findViewById(R.id.undo);
        btnRedo = findViewById(R.id.redo);
        btnSave = findViewById(R.id.save);
        btnClear = findViewById(R.id.clear);
        btnSettings = findViewById(R.id.settings);

        btnUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                undoCanvas();
            }
        });

        btnRedo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                redoCanvas();
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
        initCanvas();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setSaveState(isSaveEnabled());
    }

    @Override
    public void onBackPressed() {
        Log.w("GetSignature", "onDestory");
        if (isSaveEnabled()) {
            saveCanvas();
        }
        super.onBackPressed();
    }

    private boolean save() {
        File directory = ((UCApplication)getApplication()).getCaptureFolder();
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
        try {
            mSignature.save(mypath);
            mediaScannerConn = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    mediaScannerConn.scanFile(mypath.getAbsolutePath(), null);
                }

                @Override
                public void onScanCompleted(String s, Uri uri) {
                    mediaScannerConn.disconnect();
                }
            });
            mediaScannerConn.connect();
            Toast.makeText(this, "Saved " + mypath.getName(), Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException e) {
            Log.v(TAG,"Couldn't save file at " + mypath + ": " + e.getCause());
            Toast.makeText(this, "Couldn't save file at " + mypath + ": " + e.getCause(), Toast.LENGTH_LONG).show();
            return false;
        }
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
        String time = "" ;
        time += String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
        time += String.format("%02d", c.get(Calendar.MINUTE));
        time += String.format("%02d", c.get(Calendar.SECOND));
        Log.w("TIME",time);
        return(time);
 
    }
}
