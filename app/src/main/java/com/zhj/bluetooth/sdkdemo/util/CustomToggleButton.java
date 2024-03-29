package com.zhj.bluetooth.sdkdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhj.bluetooth.sdkdemo.R;


public class CustomToggleButton extends View implements View.OnTouchListener, View.OnClickListener {

    public boolean isTouchEnable = true;
    Paint paint = new Paint();
    // Background when switch is on, background when switch is off, slide button
    private Bitmap switch_on_Bkg, switch_off_Bkg, slip_Btn;
    private Rect on_Rect, off_Rect;
    // Is sliding
    private boolean isSlipping = false;
    // Current switch status, true is on, false is off
    private boolean isSwitchOn = false;
    // //Whether switch monitor is set
    // private boolean isSwitchListenerOn = false;
    // The horizontal coordinate X when the finger is pressed, and the current horizontal coordinate X
    private float previousX, currentX;
    // Switch monitor
    private OnSwitchListener onSwitchListener;
    private float dis;
    private int rectPadding = 3;

    public CustomToggleButton(Context context) {
        super(context);
        init();
    }

    public CustomToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        setImageResource(R.mipmap.toggle_on, R.mipmap.toggle_off, R.mipmap.toggle_thumb);
        setOnClickListener(this);
    }

    public void setImageResource(int switchOnBkg, int switchOffBkg, int slipBtn) {
        switch_on_Bkg = BitmapFactory.decodeResource(getResources(), switchOnBkg);
        switch_off_Bkg = BitmapFactory.decodeResource(getResources(), switchOffBkg);
        slip_Btn = BitmapFactory.decodeResource(getResources(), slipBtn);

        // Right half Rect, that is, when the sliding button is on the right half, it means the switch is on
        on_Rect = new Rect(switch_off_Bkg.getWidth() - slip_Btn.getWidth() - rectPadding, rectPadding, switch_off_Bkg.getWidth() - rectPadding, slip_Btn.getHeight() - rectPadding);
        // Rect on the left side, that is, when the sliding button is on the left side, it means the switch is closed
        off_Rect = new Rect(rectPadding, rectPadding, slip_Btn.getWidth() + rectPadding, slip_Btn.getHeight() - rectPadding);
        postInvalidate();
    }

    public boolean getSwitchState() {
        return isSwitchOn;
    }

    public void setSwitchState(boolean switchState) {
        isSwitchOn = switchState;
        postInvalidate();
    }

    protected void updateSwitchState(boolean switchState) {
        isSwitchOn = switchState;
        postInvalidate();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback callback;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        DebugLog.d("onDraw.........isSwitchOn:"+isSwitchOn);
        // Matrix matrix = new Matrix();
        // Paint paint = new Paint();
        // Left coordinate of the slide button
        float left_SlipBtn;

        // When the finger slides to the left half, the switch is closed, and when the finger slides to the right half, the switch is open
        // if(isSlipping && currentX < (switch_on_Bkg.getWidth() / 2)) {
        // // canvas.drawBitmap(switch_off_Bkg, null, paint);
        // canvas.drawBitmap(switch_off_Bkg, 0, (getMeasuredHeight() -
        // switch_off_Bkg.getHeight()) / 2, paint);
        // } else {
        // // canvas.drawBitmap(switch_on_Bkg, null, paint);
        // canvas.drawBitmap(switch_on_Bkg, 0, (getMeasuredHeight() -
        // switch_off_Bkg.getHeight()) / 2, paint);
        // }

        // Judge whether it is currently sliding
        if (isSlipping) {
            if (currentX > switch_on_Bkg.getWidth()) {
                left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
            } else {
                left_SlipBtn = currentX - slip_Btn.getWidth() / 2;
            }
            if (isSlipping && currentX < (switch_on_Bkg.getWidth() / 2)) {
                canvas.drawBitmap(switch_off_Bkg, 0, (getMeasuredHeight() - switch_off_Bkg.getHeight()) / 2, paint);
            } else {
                canvas.drawBitmap(switch_on_Bkg, 0, (getMeasuredHeight() - switch_off_Bkg.getHeight()) / 2, paint);
            }
        } else {
            // Set the position of the sliding button according to the current switch status
            if (isSwitchOn) {
                left_SlipBtn = on_Rect.left;
                canvas.drawBitmap(switch_on_Bkg, 0, (getMeasuredHeight() - switch_off_Bkg.getHeight()) / 2, paint);
            } else {
                left_SlipBtn = off_Rect.left;
                canvas.drawBitmap(switch_off_Bkg, 0, (getMeasuredHeight() - switch_off_Bkg.getHeight()) / 2, paint);
            }
        }

        // Abnormal judgment on the position of sliding button
        if (left_SlipBtn < 0) {
            left_SlipBtn = 0;
        } else if (left_SlipBtn > switch_on_Bkg.getWidth() - slip_Btn.getWidth()) {
            left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
        }

        canvas.drawBitmap(slip_Btn, left_SlipBtn, (getMeasuredHeight() - slip_Btn.getHeight()) / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // DebugLog.d("ToggleView mode = " +
        // MeasureSpec.getMode(heightMeasureSpec));
        int h = MeasureSpec.getSize(heightMeasureSpec);
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                h = switch_on_Bkg.getHeight() + getPaddingBottom() + getPaddingTop();
                break;

            default:
                break;
        }
        setMeasuredDimension(switch_on_Bkg.getWidth(), h);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (callback != null && callback.handerEvent()) {
            return true;
        }
        return false;
    }
//        if (!isTouchEnable) return false;
//        // 松开前开关的状态
//        boolean previousSwitchState = isSwitchOn;
//        boolean tempState = isSwitchOn;
//        switch (event.getAction()) {
//            // 滑动
//            case MotionEvent.ACTION_MOVE:
//                dis = event.getX() - previousX;
//                currentX = event.getX();
//                break;
//
//            // 按下
////            case MotionEvent.ACTION_DOWN:
////                if (event.getX() > switch_on_Bkg.getWidth()) {
////                    return false;
////                }
////                dis = 0;
////                isSlipping = true;
////                previousX = event.getX();
////                currentX = previousX;
////                break;
//            // 松开
//            case MotionEvent.ACTION_UP:
//                isSlipping = false;
//
//                tempState = dis > (switch_on_Bkg.getWidth() / 2);
//                DebugLog.d("eventAction = " + event.getAction() + "***x = " + event.getX() + "***limit = " + (switch_on_Bkg.getWidth()));
//                DebugLog.d("eventAction = " + event.getAction() + "***tempState = " + tempState + "***isSwitchOn = " + isSwitchOn + "***dis = " + dis);
////                if (event.getX() >= (switch_on_Bkg.getWidth() / 2)) {
////                    tempState = true;
////                } else {
////                    tempState = false;
////                }
//                isSwitchOn=!isSwitchOn;
//                // 如果设置了监听器，则调用此方法
//                if (onSwitchListener != null) {
//                    onSwitchListener.onSwitched(isSwitchOn);
//                }
////                if (onSwitchListener != null && (tempState != isSwitchOn)) {
////                    onSwitchListener.onSwitched(tempState);
////                }
////                isSwitchOn = tempState;
//                invalidate();
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                isSlipping = false;
//                // 如果设置了监听器，则调用此方法
////                tempState = currentX >= switch_on_Bkg.getWidth() / 2;
////                DebugLog.d("eventAction = " + event.getAction() + "***tempState = " + tempState + "***isSwitchOn = " + isSwitchOn + "***dis = " + dis);
////                if (onSwitchListener != null && tempState != isSwitchOn) {
////                    onSwitchListener.onSwitched(tempState);
////                }
////                isSwitchOn = tempState;
//                // 重新绘制控件
//                invalidate();
//                break;
//
//            default:
//                break;
//        }
//
//
//        return true;
//    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        onSwitchListener = listener;
    }

    @Override
    public void onClick(View v) {
        isSwitchOn=!isSwitchOn;
        // Call this method if the listener is set
        if (onSwitchListener != null) {
            onSwitchListener.onSwitched(isSwitchOn);
        }
        invalidate();
    }

    public interface OnSwitchListener {
        abstract void onSwitched(boolean isSwitchOn);
    }
    public interface Callback{
        boolean handerEvent();
    }
}
