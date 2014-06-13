package nl.mpcjanssen.uc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by janss484 on 13-6-2014.
 */
public class CaptureView extends ImageView
{

    private static final float STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private Paint paint = new Paint();
    private CustomPath path = new CustomPath();

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    private ToggleButton toggleButton;

    public CaptureView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public void save(File target)
    {
        Log.v("log_tag", "Width: " + this.getWidth());
        Log.v("log_tag", "Height: " + this.getHeight());
        try
        {
            FileOutputStream mFileOutStream = new FileOutputStream(target);
            getBitmap().compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
    }

    public void clear()
    {
        path.reset();
        invalidate();
        toggle(false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //if (event.getToolType(0)!=MotionEvent.TOOL_TYPE_STYLUS) {
        //   return false;
        //}
        toggle(true);
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++)
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                break;

            default:
                return false;
        }

        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void expandDirtyRect(float historicalX, float historicalY)
    {
        if (historicalX < dirtyRect.left)
        {
            dirtyRect.left = historicalX;
        }
        else if (historicalX > dirtyRect.right)
        {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top)
        {
            dirtyRect.top = historicalY;
        }
        else if (historicalY > dirtyRect.bottom)
        {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY)
    {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("stateToSave", path);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            path = (CustomPath)bundle.getSerializable("stateToSave");
            state = bundle.getParcelable("instanceState");
            if (path.isEmpty()) {
                toggle(false);
            }
        }
        super.onRestoreInstanceState(state);

    }

    private void toggle(Boolean state) {
        if (toggleButton!=null) {
            toggleButton.setEnable(state);
        }
    }

    public void setToggleButton( ToggleButton tbtn) {
        this.toggleButton = tbtn;
        toggle(false);
    }

    public interface ToggleButton {
        public void setEnable(boolean state);
    }


}
