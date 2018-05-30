package daily.zjrb.com.daily_vr.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVPlayerCallBack;

import daily.zjrb.com.daily_vr.AnalyCallBack;
import daily.zjrb.com.daily_vr.bean.VrSource;
import daily.zjrb.com.daily_vr.controller.BaseController;

/**
 * @author: lujialei
 * @date: 2018/4/24
 * @describe:
 */


public class VRManager implements UVPlayerCallBack {
    private UVMediaPlayer mMediaplayer = null;  // 媒体视频播放器
    private BaseController mController;

    private ViewGroup rlParent;
    private BroadcastReceiver networkChangeReceiver;
    private AnalyCallBack analyCallBack;
    private SettingsContentObserver settingsContentObserver;
    VrSource source;
    Activity activity;

    public VRManager(VrSource source,Activity activity, ViewGroup parent, AnalyCallBack analyCallBack) {
        rlParent = parent;
        this.source = source;
        this.activity = activity;
        this.analyCallBack = analyCallBack;
        initPlayerAndController();
        setBreoadcast();
        setVolumnListener();
    }

    private void setVolumnListener() {
        settingsContentObserver = new SettingsContentObserver(new Handler());
        rlParent.getContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);
    }


    private void initPlayerAndController() {
        //添加播放器
        RelativeLayout realParent = new RelativeLayout(rlParent.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        realParent.setLayoutParams(params);
        rlParent.removeAllViews();
        rlParent.addView(realParent);
        mMediaplayer = new UVMediaPlayer(rlParent.getContext(), realParent,this);
    }

    public UVMediaPlayer getPlayer(){
        return mMediaplayer;
    }

    @Override
    public void createEnv() {
        try
        {
            // 创建媒体视频播放器
            mMediaplayer.initPlayer();
            mMediaplayer.setListener(new UVEventListener() {
                @Override
                public void onStateChanged(int i) {
                    if (mController != null){
                        mController.onStateChanged(i);
                    }
                }

                @Override
                public void onError(Exception e, int i) {
                    if (mController != null){
                        mController.onError(e,i);
                    }
                }

                @Override
                public void onVideoSizeChanged(int i, int i1) {
                    if (mController != null){
                        mController.onVideoSizeChanged(i,i1);
                    }
                }
            });
            mMediaplayer.setInfoListener(new UVInfoListener() {
                @Override
                public void onBandwidthSample(int i, long l, long l1) {
                    if (mController != null){
                        mController.onBandwidthSample(i,l,l1);
                    }
                }

                @Override
                public void onLoadStarted() {
                    if (mController != null){
                        mController.onLoadStarted();
                    }
                }

                @Override
                public void onLoadCompleted() {
                    if (mController != null){

                    }
                }
            });
            //添加controller
            mController = new BaseController(source,mMediaplayer, activity,rlParent,analyCallBack);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            mController.setLayoutParams(params1);
            rlParent.addView(mController);
            //如果是网络MP4，可调用 mCtrl.startCachePro();mCtrl.stopCachePro();

            //mMediaplayer.setSource(UVMediaType.UVMEDIA_TYPE_MP4, "/sdcard/wu.mp4");
        }
        catch (Exception e)
        {
            Log.e("utovr", e.getMessage(), e);
        }
    }

    @Override
    public void updateProgress(long position) {
        if(mController != null){
            mController.updateCurrentPosition(position);
        }
    }

    public void changeOrientation(boolean isLandscape)
    {
        if (rlParent == null)
        {
            return;
        }
        if(mController != null){
            mController.changeOrientation(isLandscape);
        }

    }



//    activity生命周期调用
    public void onResume() {
        if (mMediaplayer != null)
        {
            mMediaplayer.onResume(rlParent.getContext());
        }
    }

    public void onPause() {
        if (mMediaplayer != null)
        {
            mMediaplayer.onPause();
        }
    }

    public void releasePlayer()
    {
        if (mMediaplayer != null)
        {
            mMediaplayer.release();
            mMediaplayer = null;
        }
        rlParent.getContext().unregisterReceiver(networkChangeReceiver);
        rlParent.getContext().getContentResolver().unregisterContentObserver(settingsContentObserver);
    }

    public BaseController getController(){
        return mController;
    }


    /**
     * 设置网络监听
     */
    public void setBreoadcast() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        rlParent.getContext().registerReceiver(networkChangeReceiver, filter);
    }


    //网络监听
    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mController != null){
                mController.onNetWorkChanged();
            }
        }
    }


    //音量监听
    public class SettingsContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SettingsContentObserver(android.os.Handler handler) {
            super(handler);
        }



        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if(mController != null){
                mController.volumnChanged();
            }
        }


    }



}
