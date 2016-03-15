package nl.mpcjanssen.uc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by janss484 on 13-6-2014.
 */
public class CaptureView extends ImageView {

    private Paint paint = new Paint();
    private CustomPath path = new CustomPath();
    private UCApplication app;

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    private ToggleButton toggleButton;


    private float stroke_width() {
        return app.getLineWidth();
    }

    private float half_stroke_width() {
        return stroke_width()/2.0f;
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        app = (UCApplication) context.getApplicationContext();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(stroke_width());
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public void setPath(CustomPath path) {
        this.path = path;
    }

    public void save(File target) throws IOException {
        FileOutputStream mFileOutStream = new FileOutputStream(target);
        getBitmap().compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
        mFileOutStream.flush();
        mFileOutStream.close();
    }

    public void clear() {
        path.reset();
        invalidate();
        toggle(false);
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (app.usePenOnly() && event.getToolType(0)!=MotionEvent.TOOL_TYPE_STYLUS) {
           return false;
        }
        toggle(true);
        // clear redo stack because we are drawing
        path.clearRedo();
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
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

        invalidate((int) (dirtyRect.left - half_stroke_width()),
                (int) (dirtyRect.top - half_stroke_width()),
                (int) (dirtyRect.right + half_stroke_width()),
                (int) (dirtyRect.bottom + half_stroke_width()));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    public void undo() {
        path.undo();
        if (isEmpty()) {
            toggle(false);
        }
        invalidate();
    }

    public void redo() {
        path.redo();
        if (!isEmpty()) {
            toggle(true);
        }
        invalidate();
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("path", path);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            path = (CustomPath) bundle.getSerializable("path");
            state = bundle.getParcelable("instanceState");
            if (path.isEmpty()) {
                toggle(false);
            }
        }
        super.onRestoreInstanceState(state);

    }

    private void toggle(Boolean state) {
        if (toggleButton != null) {
            toggleButton.setEnable(state);
        }
    }

    public void setToggleButton(ToggleButton tbtn) {
        this.toggleButton = tbtn;
        toggle(false);
    }

    public interface ToggleButton {
        public void setEnable(boolean state);
    }


}
