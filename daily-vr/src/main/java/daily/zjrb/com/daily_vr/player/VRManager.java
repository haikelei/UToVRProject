package daily.zjrb.com.daily_vr.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVMediaType;
import com.utovr.player.UVPlayerCallBack;

/**
 * @author: lujialei
 * @date: 2018/4/24
 * @describe:
 */


public class VRManager implements UVPlayerCallBack {
    private UVMediaPlayer mMediaplayer = null;  // 媒体视频播放器
    private final BaseController vController;


//    private String Path = "http://cache.utovr.com/201508270528174780.m3u8";
    private String Path = "https://v-cdn.zjol.com.cn/152688_ts.mp4?userId=37c83028-bc45-40d0-b41c-b12ac62363d3";
    private ViewGroup rlParent;
    private int SmallPlayH = 0;

    public VRManager(Context context, ViewGroup parent) {
        rlParent = parent;
        //添加播放器
        RelativeLayout realParent = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        realParent.setLayoutParams(params);
        parent.addView(realParent);
        mMediaplayer = new UVMediaPlayer(context, realParent,this);

        //添加controller
        vController = new VController(context);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        vController.setLayoutParams(params1);
        vController.setPlayer(mMediaplayer);
        parent.addView(vController);

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
            mMediaplayer.setListener(vController);
            mMediaplayer.setInfoListener(vController);
            //如果是网络MP4，可调用 mCtrl.startCachePro();mCtrl.stopCachePro();
            mMediaplayer.setSource(UVMediaType.UVMEDIA_TYPE_MP4, Path);
            //mMediaplayer.setSource(UVMediaType.UVMEDIA_TYPE_MP4, "/sdcard/wu.mp4");
        }
        catch (Exception e)
        {
            Log.e("utovr", e.getMessage(), e);
        }
    }

    @Override
    public void updateProgress(long position) {
        vController.updateCurrentPosition(position);

    }

    public void changeOrientation(boolean isLandscape)
    {
        if (rlParent == null)
        {
            return;
        }
        if (isLandscape)
        {
            ((Activity)rlParent.getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            ((Activity)rlParent.getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParent.setLayoutParams(lp);
        }
        else
        {

            ((Activity)rlParent.getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity)rlParent.getContext()).getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSmallPlayHeight();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SmallPlayH);
            rlParent.setLayoutParams(lp);
        }
    }


    private void getSmallPlayHeight() {
        if (this.SmallPlayH != 0) {
            return;
        }
        int ScreenW = ((Activity)rlParent.getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int ScreenH = ((Activity)rlParent.getContext()).getWindowManager().getDefaultDisplay().getHeight();
        if (ScreenW > ScreenH)
        {
            int temp = ScreenW;
            ScreenW = ScreenH;
            ScreenH = temp;
        }
        SmallPlayH = ScreenW * ScreenW / ScreenH;
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
    }

}
