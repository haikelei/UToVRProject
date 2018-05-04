package daily.zjrb.com.daily_vr.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import daily.zjrb.com.daily_vr.player.BaseController;

/**
 * @author: lujialei
 * @date: 2018/5/4
 * @describe:
 */


public class ControllerContainer extends RelativeLayout {
    public interface OnEventListener{
        void onSingleTap();
    }
    public void setOnEventListener(OnEventListener mOnEventListener){
        this.mOnEventListener = mOnEventListener;
    }
    private OnEventListener mOnEventListener;


    private GestureDetector gestureDetector;

    public ControllerContainer(Context context) {
        this(context,null);
    }

    public ControllerContainer(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ControllerContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MyGestureListener myGestureListener = new MyGestureListener();
        gestureDetector = new GestureDetector(context,myGestureListener);
    }



    //处理单击
    public class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
