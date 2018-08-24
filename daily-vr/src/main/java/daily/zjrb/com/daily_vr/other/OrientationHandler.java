package daily.zjrb.com.daily_vr.other;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.provider.Settings;

import com.aliya.dailyplayer.gravity.OrientationHelper;
import com.aliya.dailyplayer.gravity.OrientationListener;


/**
 * @author: lujialei
 * @date: 2018/5/16
 * @describe:屏幕旋转处理类
 */


public class OrientationHandler {
    public boolean isSwitchFromUser() {
        return switchFromUser;
    }

    public void setSwitchFromUser(boolean switchFromUser) {
        this.switchFromUser = switchFromUser;
    }

    private boolean switchFromUser;
    private OnOrientationListener listener;
    OrientationHelper orientationHelper = new OrientationHelper();
    private Activity activity;
    private int screenchange;
    private int mLastOrientation = -100;//记录上次屏幕方向
    private boolean canSwitch;

    public boolean isCanSwitch() {
        return canSwitch;
    }

    public void setCanSwitch(boolean canSwitch) {
        this.canSwitch = canSwitch;
    }

    public OrientationHandler(OnOrientationListener listenerl, Activity activity) {
        this.activity = activity;
        this.listener = listenerl;
        initListener();
    }

    private void initListener() {
        orientationHelper.registerListener(activity, new OrientationListener() {
            long checkOrientationTime = 0;
            @Override
            public void onOrientation(int orientation) {
                if(System.currentTimeMillis() - checkOrientationTime < 1000 || !canSwitch){//1秒内不做处理
                    return;
                }
                checkOrientationTime = System.currentTimeMillis();

                if(mLastOrientation != orientation){//方向变化后
                    mLastOrientation = orientation;
                    switchFromUser = false;
                }

                try {
                    //屏幕旋转是否开启 0未开启 1开启
                    screenchange = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if(orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE && !switchFromUser){//横屏翻转
                    if(screenchange == 1){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        listener.onLandReverse();
                    }
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT && !switchFromUser){//竖屏翻转
                    if(screenchange == 1){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        listener.onVerticalReverse();
                    }
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && !switchFromUser){//横屏
                    if(screenchange == 1){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        listener.onLand();
                    }
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !switchFromUser){//竖屏
                    if(screenchange == 1){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        listener.onVertical();
                    }

                }
            }
        });
    }

    public interface OnOrientationListener{
        void onLandReverse();
        void onVerticalReverse();
        void onVertical();//横屏
        void onLand();//竖屏
    }
}
