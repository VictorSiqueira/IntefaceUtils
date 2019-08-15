package br.com.nurik.editor;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public abstract class StickerView extends FrameLayout{

    public static final String TAG = "com.knef.stickerView";
    private Context context;
    // For scalling
    private float scale_orgX = -1, scale_orgY = -1;
    // For moving
    private float move_orgX =-1, move_orgY = -1;
    private double centerX, centerY;
    private final static int BUTTON_SIZE_DP = 30;
    private final static int SELF_SIZE_DP = 120;

    public StickerView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.setTag("DraggableViewGroup");
        int margin = convertDpToPixel(BUTTON_SIZE_DP, getContext())/2;

        LayoutParams iv_main_params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv_main_params.setMargins(margin,margin,margin,margin);

        this.addView(getMainView(), iv_main_params);
        this.setOnTouchListener(mTouchListener);
    }

    public void deletefunction() {
        if(StickerView.this.getParent()!=null){
            ViewGroup myCanvas = ((ViewGroup)StickerView.this.getParent());
            myCanvas.removeView(StickerView.this);
        }
    }

    public boolean isFlip(){
        return getMainView().getRotationY() == -180f;
    }

    protected abstract View getMainView();
    private VelocityTracker mVelocityTracker = null;
    private int mActivePointerId;

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(event.getPointerCount() == 1) {
                onDragEvents(view, event);
            }else if(event.getPointerCount() == 2){
                onPinchEvents(event);
            }
            return true;
        }
    };



    private void onPinchEvents(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                event.getPointerCount();
                /*scale_orgX = event.getRawX();
                scale_orgY = event.getRawY();

                centerX = StickerView.this.getX()+
                        ((View)StickerView.this.getParent()).getX()+
                        (float)StickerView.this.getWidth()/2;


                //double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
                int result = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = getResources().getDimensionPixelSize(resourceId);
                }
                double statusBarHeight = result;
                centerY = StickerView.this.getY()+
                        ((View)StickerView.this.getParent()).getY()+
                        statusBarHeight+
                        (float)StickerView.this.getHeight()/2;
*/
                break;
            case MotionEvent.ACTION_MOVE:
                event.getPointerCount();
                Log.v(TAG, "iv_scale action move");
                /*float rawY = event.getRawY();
                float rawX = event.getRawX();*/

                float rawY = event.getY(0);
                float rawX = event.getX(0);

                double angle_diff = Math.abs(
                        Math.atan2(rawY - scale_orgY , rawX - scale_orgX)
                                - Math.atan2(scale_orgY - centerY, scale_orgX - centerX))*180/Math.PI;

                Log.v(TAG, "angle_diff: "+angle_diff);

                double length1 = getLength(centerX, centerY, scale_orgX, scale_orgY);
                double length2 = getLength(centerX, centerY, rawX, rawY);

                int size = convertDpToPixel(SELF_SIZE_DP, getContext());
                if(length2 > length1
                        && (angle_diff < 25 || Math.abs(angle_diff-180)<25)
                        ) {
                    //scale up
                    double offsetX = Math.abs(rawX - scale_orgX);
                    double offsetY = Math.abs(rawY - scale_orgY);
                    double offset = Math.max(offsetX, offsetY);
                    offset = Math.round(offset);
                    if(StickerView.this.getLayoutParams().width < context.getResources().getDimension(R.dimen.mask_drawable_size)){
                        StickerView.this.getLayoutParams().width += offset;
                        StickerView.this.getLayoutParams().height += offset;
                    }
                    onScaling(true);
                    //DraggableViewGroup.this.setX((float) (getX() - offset / 2));
                    //DraggableViewGroup.this.setY((float) (getY() - offset / 2));
                }else if(length2 < length1
                        && (angle_diff < 25 || Math.abs(angle_diff-180)<25)
                        && StickerView.this.getLayoutParams().width > size/2
                        && StickerView.this.getLayoutParams().height > size/2) {
                    //scale down
                    double offsetX = Math.abs(rawX - scale_orgX);
                    double offsetY = Math.abs(rawY - scale_orgY);
                    double offset = Math.max(offsetX, offsetY);
                    offset = Math.round(offset);
                    StickerView.this.getLayoutParams().width -= offset;
                    StickerView.this.getLayoutParams().height -= offset;
                    onScaling(false);
                }
                //rotate
                double angle = Math.atan2(rawY - centerY, rawX - centerX) * 180 / Math.PI;
                Log.v(TAG, "log angle: " + angle);

                //setRotation((float) angle - 45);
                setRotation((float) angle - 45);
                Log.v(TAG, "getRotation(): " + getRotation());
                onRotating();

                scale_orgX = rawX;
                scale_orgY = rawY;

                postInvalidate();
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "iv_scale action up");
                break;
        }
    }

    private void onDragEvents(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, "sticker view action down");
                move_orgX = event.getRawX();
                move_orgY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, "sticker view action move");
                float offsetX = event.getRawX() - move_orgX;
                float offsetY = event.getRawY() - move_orgY;
                StickerView.this.setX(StickerView.this.getX()+offsetX);
                StickerView.this.setY(StickerView.this.getY() + offsetY);
                move_orgX = event.getRawX();
                move_orgY = event.getRawY();
                function(view);
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "sticker view action up");
                break;
        }
    }

    private void function(View view) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private double getLength(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(y2-y1, 2)+Math.pow(x2-x1, 2));
    }

    private float[] getRelativePos(float absX, float absY){
        Log.v("ken", "getRelativePos getX:"+((View)this.getParent()).getX());
        Log.v("ken", "getRelativePos getY:"+((View)this.getParent()).getY());
        float [] pos = new float[]{
                absX-((View)this.getParent()).getX(),
                absY-((View)this.getParent()).getY()
        };
        Log.v(TAG, "getRelativePos absY:"+absY);
        Log.v(TAG, "getRelativePos relativeY:"+pos[1]);
        return pos;
    }

    protected void onScaling(boolean scaleUp){}

    protected void onRotating(){}

    private class BorderView extends View{

        public BorderView(Context context) {
            super(context);
        }

        public BorderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Draw sticker border
            LayoutParams params = (LayoutParams)this.getLayoutParams();
            Log.v(TAG,"params.leftMargin: "+params.leftMargin);
            Rect border = new Rect();
            border.left = (int)this.getLeft()-params.leftMargin;
            border.top = (int)this.getTop()-params.topMargin;
            border.right = (int)this.getRight()-params.rightMargin;
            border.bottom = (int)this.getBottom()-params.bottomMargin;
            Paint borderPaint = new Paint();
            borderPaint.setStrokeWidth(6);
            borderPaint.setColor(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(border, borderPaint);
        }
    }

    private static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int)px;
    }
}