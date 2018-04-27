package daily.zjrb.com.daily_vr;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVMediaType;
import com.utovr.player.UVPlayerCallBack;

import daily.zjrb.com.daily_vr.player.VRManager;

/**
 * @author: lujialei
 * @date: 2018/4/16
 * @describe:
 */


public class VRFragment extends Fragment
{
    private UVMediaPlayer mMediaplayer = null;  // 媒体视频播放器
//    private VideoController mCtrl = null;
//    private String Path = "http://cache.utovr.com/201508270528174780.m3u8";
//    private String Path = "https://v-cdn.zjol.com.cn/152688_ts.mp4?userId=37c83028-bc45-40d0-b41c-b12ac62363d3";

    private boolean bufferResume = true;
    private boolean needBufferAnim = true;
//    private ImageView imgBuffer;                // 缓冲动画
//    private ImageView imgBack;
    private RelativeLayout rlParent = null;
    protected int CurOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int SmallPlayH = 0;
    private boolean colseDualScreen = false;
    private VRManager vrManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vr_player_activity,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化播放器
        rlParent = (RelativeLayout) view.findViewById(R.id.activity_rlParent);
        vrManager = new VRManager(getContext(),rlParent);
        mMediaplayer = vrManager.getPlayer();
        vrManager.changeOrientation(false);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        vrManager.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        vrManager.onPause();
    }

    @Override
    public void onDestroy()
    {
        vrManager.releasePlayer();
        super.onDestroy();
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        vrManager.changeOrientation(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE);
    }





//    @Override
//    public void createEnv()
//    {
//        vrManager.createEnv();
//
//    }
//
//    @Override
//    public void updateProgress(long position)
//    {
//        if (vrManager != null) {
//            vrManager.updateCurrentPosition(position);
//        }
//    }

    private UVEventListener mListener = new UVEventListener()
    {
        @Override
        public void onStateChanged(int playbackState)
        {
            Log.i("utovr", "+++++++ playbackState:" + playbackState);
            switch (playbackState)
            {
                case UVMediaPlayer.STATE_PREPARING:
                    break;
                case UVMediaPlayer.STATE_BUFFERING:
                    if (needBufferAnim && mMediaplayer != null && mMediaplayer.isPlaying()) {
                        bufferResume = true;
//                        Utils.setBufferVisibility(imgBuffer, true);
                    }
                    break;
                case UVMediaPlayer.STATE_READY:
                    // 设置时间和进度条
//                    mCtrl.setInfo();
                    if (bufferResume)
                    {
                        bufferResume = false;
//                        Utils.setBufferVisibility(imgBuffer, false);
                    }
                    break;
                case UVMediaPlayer.STATE_ENDED:
                    //这里是循环播放，可根据需求更改
                    mMediaplayer.replay();
                    break;
                case UVMediaPlayer.TRACK_DISABLED:
                case UVMediaPlayer.TRACK_DEFAULT:
                    break;
            }
        }

        @Override
        public void onError(Exception e, int ErrType)
        {
            Toast.makeText(getContext(), Utils.getErrMsg(ErrType), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoSizeChanged(int width, int height)
        {
        }

    };

    private UVInfoListener mInfoListener = new UVInfoListener()
    {
        @Override
        public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate)
        {
        }

        @Override
        public void onLoadStarted()
        {
        }

        @Override
        public void onLoadCompleted()
        {
            if (bufferResume)
            {
                bufferResume = false;
//                Utils.setBufferVisibility(imgBuffer, false);
            }
//            if (mCtrl != null) {
//                mCtrl.updateBufferProgress();
//            }

        }
    };

//    @Override
//    public long getDuration()
//    {
//        return mMediaplayer != null ? mMediaplayer.getDuration() : 0;
//    }
//
//    @Override
//    public long getBufferedPosition()
//    {
//        return mMediaplayer != null ? mMediaplayer.getBufferedPosition() : 0;
//    }
//
//    @Override
//    public long getCurrentPosition()
//    {
//        return mMediaplayer != null ? mMediaplayer.getCurrentPosition() : 0;
//    }
//
//    @Override
//    public void setGyroEnabled(boolean val)
//    {
//        if (mMediaplayer != null)
//            mMediaplayer.setGyroEnabled(val);
//    }
//
//    @Override
//    public boolean isGyroEnabled()
//    {
//        return mMediaplayer != null ? mMediaplayer.isGyroEnabled() : false;
//    }
//
//    @Override
//    public boolean isDualScreenEnabled()
//    {
//        return mMediaplayer != null ? mMediaplayer.isDualScreenEnabled() : false;
//    }
//
//    @Override
//    public void toolbarTouch(boolean start)
//    {
//        if (mMediaplayer != null)
//        {
//            if (true)
//            {
//                mMediaplayer.cancelHideToolbar();
//            }
//            else
//            {
//                mMediaplayer.hideToolbarLater();
//            }
//        }
//    }
//
//    @Override
//    public void pause()
//    {
//        if (mMediaplayer != null && mMediaplayer.isPlaying())
//        {
//            mMediaplayer.pause();
//        }
//    }
//
//    @Override
//    public void seekTo(long positionMs)
//    {
//        if (mMediaplayer != null)
//            mMediaplayer.seekTo(positionMs);
//    }

//    @Override
//    public void play()
//    {
//        if (mMediaplayer != null && !mMediaplayer.isPlaying())
//        {
//            mMediaplayer.play();
//        }
//    }
//
//    @Override
//    public void setDualScreenEnabled(boolean val)
//    {
//        if (mMediaplayer != null)
//            mMediaplayer.setDualScreenEnabled(val);
//    }

//    @Override
//    public void toFullScreen()
//    {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//    }
    /* 大小屏切 可以再加上 ActivityInfo.SCREEN_ORIENTATION_SENSOR 效果更佳**/

//    private void back()
//    {
//        if (CurOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//        {
//            releasePlayer();
//            getActivity().finish();
//        }
//        else
//        {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//    }

    private void getSmallPlayHeight()
    {
        if (this.SmallPlayH != 0) {
            return;
        }
        int ScreenW = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int ScreenH = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        if (ScreenW > ScreenH)
        {
            int temp = ScreenW;
            ScreenW = ScreenH;
            ScreenH = temp;
        }
        SmallPlayH = ScreenW * ScreenW / ScreenH;
    }
}
