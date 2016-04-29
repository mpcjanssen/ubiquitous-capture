package nl.mpcjanssen.uc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.util.Calendar

class UbiquitousCaptureActivity : Activity() {
    internal var mSignature: CaptureView? = null

    var current: String? = null
    internal var mypath: File? = null
    internal var idx = 1

    private var uniqueId: String? = null
    private var mediaScannerConn: MediaScannerConnection? = null
    private var btnUndo: View? = null
    private var btnRedo: View? = null
    private var btnClear: View? = null
    private var btnSave: View? = null
    private var btnSettings: View? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> recreate()
        }
    }

    fun getWritePermission(act: Activity, activityResult: Int): Boolean {

        val permissionCheck = ContextCompat.checkSelfPermission(act,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(act,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), activityResult)
        }
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    val isCloseOnSave: Boolean
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            return sharedPref.getBoolean("pref_close_on_save", false)
        }

    fun hasButtonsOnTop(): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean("pref_buttons_top", false)
    }

    fun initCanvas() {
        mSignature = findViewById(R.id.image) as CaptureView?
        mSignature?.setBackgroundColor(Color.WHITE)
        mSignature?.setPath((application as UCApplication).mPath)
        mSignature?.setToggleButton { state -> setSaveState(state) }
    }

    private fun setSaveState(state: Boolean) {
        btnSave?.isEnabled = state
    }

    private fun clearCanvas() {
        Log.v(TAG, "Panel Cleared")
        mSignature?.clear()
    }

    private fun saveCanvas() {
        Log.v(TAG, "Saving canvas")
        if (save()) {
            mSignature?.clear()
            if (isCloseOnSave) {
                finish()
            }
        }
    }

    private fun undoCanvas() {
        mSignature?.undo()
    }

    private fun redoCanvas() {
        mSignature?.redo()
    }

    private val isSaveEnabled: Boolean
        get() {
            if (btnSave != null) {
                return btnSave?.isEnabled ?: false
            }
            return true
        }


    private fun openSettings() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasButtonsOnTop()) {
            setContentView(R.layout.main_top)
        } else {
            setContentView(R.layout.main)
        }
        if (!getWritePermission(this, REQUEST_PERMISSION)) {
            return
        }

        btnUndo = findViewById(R.id.undo)
        btnRedo = findViewById(R.id.redo)
        btnSave = findViewById(R.id.save)
        btnClear = findViewById(R.id.clear)
        btnSettings = findViewById(R.id.settings)

        btnUndo?.setOnClickListener { undoCanvas() }

        btnRedo?.setOnClickListener { redoCanvas() }

        btnSave?.setOnClickListener { saveCanvas() }

        btnClear?.setOnClickListener { clearCanvas() }

        btnSettings?.setOnClickListener { openSettings() }
        initCanvas()
    }

    public override fun onResume() {
        super.onResume()
        setSaveState(isSaveEnabled)
    }

    override fun onBackPressed() {
        Log.w("GetSignature", "onDestory")
        if (isSaveEnabled) {
            saveCanvas()
        }
        super.onBackPressed()
    }

    private fun save(): Boolean {
        val directory = (application as UCApplication).captureFolder
        if (!directory.exists()) {
            directory.mkdirs()
        }
        idx = 1
        uniqueId = todaysDate + "_" + currentTime + "_"
        current = uniqueId + idx + ".png"
        mypath = File(directory, current)
        while (mypath?.exists()?:false) {
            idx++
            current = uniqueId + idx + ".png"
            mypath = File(directory, current)
        }
        try {
            mSignature?.save(mypath)
            mediaScannerConn = MediaScannerConnection(this, object : MediaScannerConnection.MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {
                    mediaScannerConn!!.scanFile(mypath?.absolutePath, null)
                }

                override fun onScanCompleted(s: String, uri: Uri) {
                    mediaScannerConn!!.disconnect()
                }
            })
            mediaScannerConn!!.connect()
            Toast.makeText(this, "Saved " + mypath?.name, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: IOException) {
            Log.v(TAG, "Couldn't save file at " + mypath + ": " + e.cause)
            Toast.makeText(this, "Couldn't save file at " + mypath + ": " + e.cause, Toast.LENGTH_LONG).show()
            return false
        }

    }

    private val todaysDate: String
        get() {
            val c = Calendar.getInstance()
            val todaysDate = c.get(Calendar.YEAR) * 10000 +
                    (c.get(Calendar.MONTH) + 1) * 100 +
                    c.get(Calendar.DAY_OF_MONTH)
            Log.w("DATE:", todaysDate.toString())
            return todaysDate.toString()

        }

    private val currentTime: String
        get() {

            val c = Calendar.getInstance()
            var time = ""
            time += String.format("%02d", c.get(Calendar.HOUR_OF_DAY))
            time += String.format("%02d", c.get(Calendar.MINUTE))
            time += String.format("%02d", c.get(Calendar.SECOND))
            Log.w("TIME", time)
            return time

        }

    companion object {
        private val REQUEST_PERMISSION = 0
        private val TAG = UbiquitousCaptureActivity::class.java.name
    }
}
