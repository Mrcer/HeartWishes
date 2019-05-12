package com.mrcer.heartwishes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class HeartView extends View {
    private double total = 100;
    private double process = 50;
    private double step = 1;

    private int srcWidth = 0;
    private int srcHeight = 0;
    private Bitmap fill = null;
    private Bitmap fillShape = null;
    private Bitmap cut = null;
    private Bitmap frontGround = null;
    private Bitmap background = null;
    private Paint paint = null;
    private Paint cutPaint = null;
    private Rect bitmapRect = null;
    private Rect translateRect = new Rect();
    private Canvas cutter;

    private final PorterDuffXfermode DST_OVER = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
    private final PorterDuffXfermode DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private final PorterDuffXfermode LIGHTEN = new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN);

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        cutPaint = new Paint();
        fill = BitmapFactory.decodeResource(getResources(), R.drawable.fill);
        fillShape = BitmapFactory.decodeResource(getResources(), R.drawable.fill_shape);
        frontGround = BitmapFactory.decodeResource(getResources(), R.drawable.frontground);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        srcWidth = background.getWidth();
        srcHeight = background.getHeight();
        cut = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        cutter = new Canvas(cut);
        bitmapRect = new Rect(0, 0, srcWidth, srcHeight);
        cutPaint.setAlpha(100);
        cutPaint.setColor(Color.BLACK);
    }

    private void drawCut(Canvas canvas) {
        //drawCut
        cutter.drawRect(0, 0, srcWidth, (int)((1-process/total)*srcHeight), cutPaint);
        canvas.drawBitmap(cut, bitmapRect, translateRect, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //init
        int size = Math.min(getWidth(), getHeight());
        translateRect.set(0, 0, size, size);
        if(size < getHeight())
            canvas.translate(0, (getHeight()-size)/2);
        else
            canvas.translate((getWidth()-size)/2, 0);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.ALL_SAVE_FLAG);
        //background paint
        canvas.drawBitmap(fill, bitmapRect, translateRect, paint);
        paint.setXfermode(DST_OUT);
        canvas.drawBitmap(fillShape, bitmapRect, translateRect, paint);
        drawCut(canvas);
        paint.setXfermode(DST_OVER);
        canvas.drawBitmap(background, bitmapRect, translateRect, paint);
        //frontGround
        paint.setXfermode(LIGHTEN);
        canvas.drawBitmap(frontGround, bitmapRect, translateRect, paint);
        canvas.restore();
    }

    public void setTotal(int total) {
        this.process = total;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean next(int i) {
        process += step*i;
        if(process < 0) {
            process = 0;
            return false;
        }
        if(process > total) {
            process = total;
            return false;
        }
        return true;
    }
}
