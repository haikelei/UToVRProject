package daily.zjrb.com.daily_vr.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliya.player.gravity.OrientationHelper;
import com.aliya.player.gravity.OrientationListener;
import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVMediaType;
import com.utovr.player.UVNetworkListenser;
import com.zjrb.core.utils.L;
import com.zjrb.core.utils.NetUtils;
import com.zjrb.core.utils.SettingManager;

import butterknife.ButterKnife;
import daily.zjrb.com.daily_vr.CalcTime;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.Utils;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * @author: lujialei
 * @date: 2018/4/26
 * @describe: 横竖屏controller基类
 */


abstract class BaseController extends RelativeLayout implements UVEventListener, UVInfoListener {


    protected SeekBar progressBar;
    protected CheckBox buttonPlayPause;
    protected UVMediaPlayer player = null;
    protected CalcTime calcTime;
    Handler handler = new Handler();
    private boolean bufferResume = true;
    ProgressBar hintBufferProgress;
    TextView playerDuration;
    TextView playerPosition;
    private TextView playerLDuration;
    private TextView playerLPosition;
    private CheckBox buttonLPlayPause;
    private SeekBar lProgressBar;
    private CheckBox playerGyro;
    private CheckBox playerScreen;
    private ImageView retract;
    private ImageView spread;
    private LinearLayout lController;
    private LinearLayout vController;
    private Window window;
    private ViewGroup parent;
    private int mLastOrientation = -100;//上一次的方向记录
    private boolean switchFromUser;
    private Context context;
    private CheckBox playerVolumn;
    private LinearLayout playerStart;
    protected UVMediaType type;
    protected  String path;
    private TextView playerNetHint;
    private ProgressBar bottomProgressBar;
    private RelativeLayout container;
    private boolean mCurrentIsLand;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setWindow(Window window) {
        this.window = window;
    }


    public void setParent(final ViewGroup parent) {
        this.parent = parent;
        OrientationHelper orientationHelper = new OrientationHelper();
        orientationHelper.registerListener(parent.getContext(), new OrientationListener() {
            @Override
            public void onOrientation(int orientation) {
                if(mLastOrientation != orientation){//方向变化后
                    mLastOrientation = orientation;
                    switchFromUser = false;
                }

                if(orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE && !switchFromUser){//横屏翻转
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    changeOrientation(true);
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT && !switchFromUser){//竖屏翻转
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    changeOrientation(false);
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && !switchFromUser){//横屏
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    changeOrientation(true);
                }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !switchFromUser){//竖屏
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    changeOrientation(false);
                }
            }
        });
        initListener();
    }

    public BaseController(Context context) {
        this(context,null);
    }

    public BaseController(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
//        initListener();
    }


    public void setPlayer(final UVMediaPlayer player) {
        this.player = player;
        calcTime = new CalcTime();
        player.setNetWorkListenser(new UVNetworkListenser() {//第一次播放视频不会触发
            @Override
            public void onNetworkChanged(int i) {
                if(i == UVNetworkListenser.NETWORK_WIFI){//wifi

                }else if(i == UVNetworkListenser.NETWORK_MOBILE_2G || i == UVNetworkListenser.NETWORK_MOBILE_3G ||i == UVNetworkListenser.NETWORK_MOBILE_4G){
                    if(player.isPlaying()){
                        playerStart.setVisibility(VISIBLE);
                        playerNetHint.setVisibility(VISIBLE);
                        playerNetHint.setText("用流量播放");
                        buttonPlayPause.setChecked(true);
                        buttonLPlayPause.setChecked(true);
                    }
                }
            }
        });
    }

    public void initView(Context context){
        View view = LayoutInflater.from(context).inflate(getLayoutResId(), this, true);
        ButterKnife.bind(view);
        updatePositionTask = new UpdatePositionTask();
        //竖屏相关
        vController = (LinearLayout) view.findViewById(R.id.controller_v);
        progressBar = (SeekBar) view.findViewById(R.id.player_seek_bar);
        buttonPlayPause = (CheckBox) view.findViewById(R.id.player_play_pause);
        playerDuration = (TextView) view.findViewById(R.id.player_duration);
        playerPosition = (TextView) view.findViewById(R.id.player_position);
        spread = (ImageView) view.findViewById(R.id.player_full_screen);

        //横屏相关
        lController = (LinearLayout) view.findViewById(R.id.controller_l);
        playerLDuration = (TextView) view.findViewById(R.id.player_l_duration);
        playerLPosition = (TextView) view.findViewById(R.id.player_l_position);
        buttonLPlayPause = (CheckBox) view.findViewById(R.id.player_l_play_pause);
        lProgressBar = (SeekBar) view.findViewById(R.id.player_l_seek_bar);
        playerGyro = (CheckBox) view.findViewById(R.id.player_l_play_gyro);
        playerScreen = (CheckBox) view.findViewById(R.id.player_l_play_screen);
        retract = (ImageView) view.findViewById(R.id.player_l_small_screen);

        //通用ui
        hintBufferProgress = (ProgressBar) view.findViewById(R.id.player_buffer_progress);
        playerVolumn = (CheckBox) view.findViewById(R.id.player_ic_volume);
        playerStart = (LinearLayout) view.findViewById(R.id.ll_start);
        playerNetHint = (TextView) view.findViewById(R.id.tv_net_hint);
        bottomProgressBar = (ProgressBar) view.findViewById(R.id.player_bottom_progress_bar);
        container = (RelativeLayout) view.findViewById(R.id.container);
    }

    private void initListener() {

        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //调节音量
        playerVolumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int currentVolume;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//静音
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
                }else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
                }
            }
        });

        //进度条
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                player.seekTo(progress);
            }
        });
        //暂停
        buttonPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.pause();
                }else {
                    player.play();
                }
            }
        });

        buttonLPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.pause();
                }else {
                    player.play();
                }
            }
        });

        //陀螺仪
        playerGyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.setGyroEnabled(true);
                }else {
                    player.setGyroEnabled(false);
                }
            }
        });

        //双屏
        playerScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.setDualScreenEnabled(true);
                }else {
                    player.setDualScreenEnabled(false);
                }
            }
        });

        //最小化
        retract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFromUser = true;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                changeOrientation(false);
            }
        });

        //最大化
        spread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFromUser = true;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                changeOrientation(true);
            }
        });

        //进入播放页面开始播放
        playerStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerStart.setVisibility(GONE);
                if(player.getCurrentPosition() != 0){//在播放过程中提示网络变化 直接播放
                    buttonPlayPause.setChecked(false);
                    buttonLPlayPause.setChecked(false);
                    return;
                }
                check4G();
            }
        });

        //呼出进度条
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mControllerBarTask == null){
                    mControllerBarTask = new ControllerBarTask();
                }
                if(mCurrentIsLand){
                    lController.setVisibility(VISIBLE);
                    bottomProgressBar.setVisibility(GONE);
                }else {
                    vController.setVisibility(VISIBLE);
                    bottomProgressBar.setVisibility(GONE);
                }
                handler.removeCallbacks(mControllerBarTask);
                handler.postDelayed(mControllerBarTask,3000);

            }
        });


    }

    public void setSource(UVMediaType type, String path) {
        this.type = type;
        this.path = path;
        if (SettingManager.getInstance().isAutoPlayVideoWithWifi() && NetUtils.isWifi()){//自动播放
            player.setSource(type,path);
        }else {
            playerStart.setVisibility(VISIBLE);
        }
    }

    private void check4G() {
        if(NetUtils.isMobile() && playerNetHint.getVisibility() == VISIBLE && (playerNetHint.getText().toString().equals("用流量播放") || playerNetHint.getText().toString().equals("已切换至wifi"))){//用流量提醒的状态下点击播放 直接播放
            player.setSource(type,path);
            return;
        }
        if(NetUtils.isWifi()){//wifi情况下点击就播放
            player.setSource(type,path);
            return;
        }
        if(NetUtils.isMobile()){
            playerStart.setVisibility(VISIBLE);
            playerNetHint.setVisibility(VISIBLE);
            playerNetHint.setText("用流量播放");
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
                        hintBufferProgress.setVisibility(VISIBLE);
                    }
                    break;
                case UVMediaPlayer.STATE_READY:
                    // 设置时间和进度条
                    if (bufferResume) {
                        bufferResume = false;
                        hintBufferProgress.setVisibility(GONE);
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
        final int bufferProgress = calcTime.calcSecondaryProgress(progressBar.getMax());
        updatePositionTask.setPosition((int) position);
        updatePositionTask.setBufferProgress(bufferProgress);
        handler.post(updatePositionTask);
    }

    //呼出进度条
    public  ControllerBarTask mControllerBarTask;
    class ControllerBarTask implements  Runnable{

        @Override
        public void run() {
            vController.setVisibility(GONE);
            lController.setVisibility(GONE);
            bottomProgressBar.setVisibility(VISIBLE);
        }
    }



    abstract int getLayoutResId();

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
            //竖屏相关
            progressBar.setMax((int) player.getDuration());
            progressBar.setProgress( position);
            progressBar.setSecondaryProgress(bufferProgress);
            playerDuration.setText(calcTime.formatDuration());
            playerPosition.setText(calcTime.formatPosition());
            //横屏相关
            lProgressBar.setMax((int) player.getDuration());
            lProgressBar.setProgress(position);
            lProgressBar.setSecondaryProgress(bufferProgress);
            playerLDuration.setText(calcTime.formatDuration());
            playerLPosition.setText(calcTime.formatPosition());

            //bottom progress
            bottomProgressBar.setMax((int) player.getDuration());
            bottomProgressBar.setProgress(position);

        }
    }

    public void changeOrientation(boolean isLandscape) {
        if(mCurrentIsLand != isLandscape){//屏幕横竖屏发生变化
            mCurrentIsLand = isLandscape;
            if (isLandscape)
            {
//            切换横屏
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                parent.setLayoutParams(lp);
                lController.setVisibility(GONE);
                vController.setVisibility(GONE);
                bottomProgressBar.setVisibility(VISIBLE);

            }
            else
            {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.addFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                int smallPlayH = Utils.getSmallPlayHeight(window);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, smallPlayH);
                parent.setLayoutParams(lp);
                lController.setVisibility(GONE);
                vController.setVisibility(GONE);
                bottomProgressBar.setVisibility(VISIBLE);
            }
        }

    }



    public void onNetWorkChanged() {
        if(playerNetHint.getVisibility() == VISIBLE){
            if(NetUtils.isMobile()){
                playerNetHint.setText("用流量播放");
            }else if(NetUtils.isWifi()){
                playerNetHint.setText("已切换至wifi");
            }
        }
    }








}
