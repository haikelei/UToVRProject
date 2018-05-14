package daily.zjrb.com.daily_vr.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.trs.tasdk.entity.Base;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVPlayerCallBack;

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

    public VRManager(Activity activity, ViewGroup parent) {
        rlParent = parent;
        initPlayerAndController(activity);

        setBreoadcast();
    }

    private void initPlayerAndController(Activity activity) {
        //添加播放器
        RelativeLayout realParent = new RelativeLayout(rlParent.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        realParent.setLayoutParams(params);
        rlParent.addView(realParent);
        mMediaplayer = new UVMediaPlayer(rlParent.getContext(), realParent,this);

        //添加controller
        mController = new BaseController(mMediaplayer, activity,rlParent);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        mController.setLayoutParams(params1);
        rlParent.addView(mController);
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
            mMediaplayer.setListener(mController);
            mMediaplayer.setInfoListener(mController);
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
        mController.updateCurrentPosition(position);

    }

    public void changeOrientation(boolean isLandscape)
    {
        if (rlParent == null)
        {
            return;
        }
        mController.changeOrientation(isLandscape);
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


    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mController.onNetWorkChanged();
        }
    }


}
