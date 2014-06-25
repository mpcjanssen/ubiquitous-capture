package nl.mpcjanssen.uc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.io.File;
import java.util.Calendar;

public class UbiquitousCaptureActivity extends Activity  {
 
    CaptureView mSignature;

    public String current = null;
    String folder;
    File mypath;
    int idx = 1;
 
    private String uniqueId;
    private File directory;
    private MediaScannerConnection mediaScannerConn;
    private MenuItem saveMenu;

    public void initCanvas() {
        mSignature = (CaptureView) findViewById(R.id.image);
        mSignature.setBackgroundColor(Color.WHITE);
        mSignature.setToggleButton(new CaptureView.ToggleButton() {
            @Override
            public void setEnable(boolean state) {
                setSaveMenuState(state);
            }
        });
    }

    private void setSaveMenuState(boolean state) {
        if (saveMenu!=null) {
            saveMenu.setVisible(state);
        }
    }

    private void clearCanvas() {
        Log.v("log_tag", "Panel Cleared");
        mSignature.clear();
    }

    private void saveCanvas() {
        Log.v("log_tag", "Panel Saved");
        save();
        mSignature.clear();
        finish();
    }

    private void undoCanvas() {
        mSignature.undo();
    }

    private boolean isSaveEnabled() {
        if (saveMenu!=null) {
            return saveMenu.isVisible();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.main, menu);
         saveMenu = menu.findItem(R.id.save);
         if (mSignature.isEmpty()) {
             setSaveMenuState(false);
         }
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.save:
                saveCanvas();
                return true;
            case R.id.clear:
                clearCanvas();
                return true;
            case R.id.undo:
                undoCanvas();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        folder = Environment.getExternalStorageDirectory() + "/" + getString(R.string.external_dir) + "/";
        initCanvas();
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
