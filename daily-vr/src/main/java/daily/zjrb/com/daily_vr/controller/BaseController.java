package daily.zjrb.com.daily_vr.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.utovr.ma;
import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVNetworkListenser;
import com.zjrb.core.utils.NetUtils;
import com.zjrb.core.utils.SettingManager;
import daily.zjrb.com.daily_vr.AnalyCallBack;
import daily.zjrb.com.daily_vr.other.CalcTime;
import daily.zjrb.com.daily_vr.other.OrientationHandler;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.other.Utils;
import daily.zjrb.com.daily_vr.bean.VrSource;

/**
 * @author: lujialei
 * @date: 2018/4/26
 * @describe: 横竖屏controller基类
 */


public class BaseController extends RelativeLayout implements UVEventListener, UVInfoListener {

    protected UVMediaPlayer player = null;
    protected CalcTime calcTime;
    Handler handler = new Handler();
    private boolean bufferResume = true;

    protected ViewGroup parent;
    private boolean mCurrentIsLand;
    protected Activity activity;
    ProgressController progressController;
    HintController hintController;
    PrepareController prepareController;
    private ProgressBar bottomProgressBar;
    private AnalyCallBack mAnalyCallBack;
    private VrSource mVrSource;
    private OrientationHandler mOrientationHandler;

    public BaseController(VrSource source,UVMediaPlayer player, Activity activity, ViewGroup parent, AnalyCallBack analyCallBack){
        super(activity);
        this.player = player;
        this.activity = activity;
        this.parent = parent;
        mVrSource = source;
        this.mAnalyCallBack = analyCallBack;
        calcTime = new CalcTime();
        initView(source,activity);
        initListener();
        initPlayer();
    }

    private void initPlayer() {
        if (SettingManager.getInstance().isAutoPlayVideoWithWifi() && NetUtils.isWifi()){//自动播放
            player.setSource(mVrSource.getMediaType(),mVrSource.getPath());
            prepareController.hindMaskImage();
        }else {
            prepareController.showStartView();
        }
    }


    public void initView(VrSource source,Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_controller, this, true);
        //全局
        bottomProgressBar = (ProgressBar) view.findViewById(R.id.player_bottom_progress_bar);
        updatePositionTask = new UpdatePositionTask();
        //添加进度条控制器
        progressController = new ProgressController(context,player,activity,mAnalyCallBack);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        progressController.setLayoutParams(params);
        addView(progressController);
        //添加提示UI和声音控制器
        hintController = new HintController(activity,mAnalyCallBack);
        hintController.setLayoutParams(params);
        addView(hintController);
        //添加播放和重播控制器
        prepareController = new PrepareController(activity,source);
        prepareController.setLayoutParams(params);
        addView(prepareController);
    }



    private void initListener() {
        prepareController.setOnPrepareControllerListener(new PrepareController.OnPrepareControllerListener() {
            @Override
            public void onStartClicked() {
                player.setToolbar(progressController,null,null);
                player.setToolbarShow(false);
                if(player.getCurrentPosition() != 0){//在播放过程中提示网络变化 直接播放
                    progressController.play();
                    return;
                }
                check4G();
            }

            @Override
            public void onRestartClicked() {
                player.setToolbarShow(true);
                player.replay();
                check4G();
            }

            @Override
            public void onShowEndView() {
                progressController.hideAllController();
                player.setToolbarShow(false);
            }
        });

        //屏幕旋转处理
        mOrientationHandler = new OrientationHandler(new OrientationHandler.OnOrientationListener() {
            @Override
            public void onLandReverse() {
                changeOrientation(true);
            }

            @Override
            public void onVerticalReverse() {
                changeOrientation(false);
            }

            @Override
            public void onVertical() {
                changeOrientation(false);
            }

            @Override
            public void onLand() {
                changeOrientation(true);
            }
        },activity);


        progressController.setOnProgressControllerListener(new ProgressController.OnProgressControllerListener() {
            @Override
            public void onChangeOrientation(boolean b) {
                changeOrientation(b);
            }

            @Override
            public void onIsFromUserSwitch(boolean b) {
                mOrientationHandler.setSwitchFromUser(b);
            }
        });


        player.setNetWorkListenser(new UVNetworkListenser() {//播放过程中的网络变化
            @Override
            public void onNetworkChanged(int i) {
                if(i == UVNetworkListenser.NETWORK_WIFI){//wifi

                }else if(i == UVNetworkListenser.NETWORK_MOBILE_2G || i == UVNetworkListenser.NETWORK_MOBILE_3G ||i == UVNetworkListenser.NETWORK_MOBILE_4G){
                    if(player.isPlaying()){
                        if(!prepareController.hasShowedNetHint){//还没展示过流量播放
                            prepareController.setNetHintText("用流量播放");
                            progressController.initPlay();
                        }else {//已经展示过流量播放
                            Toast.makeText(getContext(),"正在使用移动流量播放",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });


        player.setToolVisibleListener(new ma() {
            @Override
            public void a(int i) {//0显示  8隐藏
                if (i == 0){
                    if(prepareController.getUIState()){
                        return;
                    }
                    progressController.switchLand(mCurrentIsLand);
                    bottomProgressBar.setVisibility(GONE);
                    hintController.showVolume(true);
                }else if(i == 8){
                    bottomProgressBar.setVisibility(VISIBLE);
                    hintController.showVolume(false);
                }
            }
        });

    }

    private void check4G() {
        if(NetUtils.isMobile() && prepareController.shoudPlay()){//用流量提醒的状态下点击播放 直接播放
            player.setSource(mVrSource.getMediaType(),mVrSource.getPath());
            mOrientationHandler.setCanSwitch(true);
            prepareController.hindMaskImage();
            return;
        }
        if(NetUtils.isWifi()){//wifi情况下点击就播放
            player.setSource(mVrSource.getMediaType(),mVrSource.getPath());
            mOrientationHandler.setCanSwitch(true);
            prepareController.hindMaskImage();
            return;
        }
        if(NetUtils.isMobile() && !prepareController.hasShowedNetHint){
            prepareController.setNetHintText("用流量播放");
            return;
        }
    }

    @Override
    public void onStateChanged(int playbackState) {
        {
            Log.i("utovr", "+++++++ playbackState:" + playbackState);
            switch (playbackState) {
                case UVMediaPlayer.STATE_PREPARING:
                    break;
                case UVMediaPlayer.STATE_BUFFERING:
                    if (player != null && player.isPlaying()) {
                        bufferResume = true;
                        hintController.showBuffering(true);
                        hintController.hideGuide();
                    }
                    break;
                case UVMediaPlayer.STATE_READY:
                    // 设置时间和进度条
                    if (bufferResume) {
                        bufferResume = false;
                        hintController.showBuffering(false);
                        if(!hintController.hasGuided){//还没展示过引导
                            hintController.showGuide();
                        }
                    }
                    break;
                case UVMediaPlayer.STATE_ENDED:
                    //这里是循环播放，可根据需求更改
                    showEndView();
                    break;
                case UVMediaPlayer.TRACK_DISABLED:
                case UVMediaPlayer.TRACK_DEFAULT:
                    break;
            }
        }
    }

    //播放结束
    private void showEndView() {
        prepareController.showEnd();

    }

    @Override
    public void onError(Exception e, int i) {

    }

    @Override
    public void onVideoSizeChanged(int i, int i1) {

    }

    @Override
    public void onBandwidthSample(int i, long l, long l1) {

    }

    @Override
    public void onLoadStarted() {

    }

    @Override
    public void onLoadCompleted() {

    }

    //更新进度条
    UpdatePositionTask updatePositionTask;
    public void updateCurrentPosition(final long position) {
        calcTime.calcTime(player);
        final int bufferProgress = calcTime.calcSecondaryProgress(progressController.getMax());
        updatePositionTask.setPosition((int) position);
        updatePositionTask.setBufferProgress(bufferProgress);
        handler.post(updatePositionTask);
    }

    public void volumnChanged() {
        hintController.volumeChanged();
    }


    //    更新ui的任务
    class UpdatePositionTask implements Runnable{

        public void setPosition(int position) {
            this.position = position;
        }

        public void setBufferProgress(int bufferProgress) {
            this.bufferProgress = bufferProgress;
        }

        private int position;
        private int bufferProgress;

        @Override
        public void run() {
            progressController.updatePosition(player.getDuration(),position,bufferProgress,calcTime.formatDuration(),calcTime.formatPosition());
            //bottom progress
            bottomProgressBar.setMax((int) player.getDuration());
            bottomProgressBar.setProgress(position);
        }
    }

    public void changeOrientation(boolean isLandscape) {
        if(mCurrentIsLand != isLandscape){//屏幕横竖屏发生变化
            mCurrentIsLand = isLandscape;
            if (isLandscape) {
//            切换横屏
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                parent.setLayoutParams(lp);
            }
            else
            {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                int smallPlayH = Utils.getSmallPlayHeight(activity.getWindow());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, smallPlayH);
                parent.setLayoutParams(lp);
            }
        }
        progressController.switchLand(isLandscape);

    }

    //播放前的网络变化监听
    public void onNetWorkChanged() {
        if(NetUtils.isMobile() && !player.isPlaying()){
            prepareController.setNetHintText("用流量播放");
        }
        if(prepareController.isNetHintShowing()){
            if(NetUtils.isWifi() && !player.isPlaying()){
                prepareController.setNetHintText("已切换至wifi");
            }
        }
    }

}
