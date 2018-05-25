package daily.zjrb.com.daily_vr.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utovr.player.UVMediaPlayer;

import daily.zjrb.com.daily_vr.AnalyCallBack;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.other.Utils;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:
 */


public class ProgressController extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private LinearLayout lController;
    private LinearLayout vController;
    private TextView playerLDuration;
    private TextView playerLPosition;
    private CheckBox buttonLPlayPause;
    private SeekBar lProgressBar;
    private CheckBox playerGyro;
    protected SeekBar progressBar;
    protected CheckBox buttonPlayPause;
    TextView playerDuration;
    TextView playerPosition;
    private CheckBox playerScreen;
    private ImageView retract;
    private ImageView spread;
    private  UVMediaPlayer player;
    private Activity activity;
    AnalyCallBack analyCallBack;



    public void play() {
        buttonPlayPause.setChecked(false);
        buttonLPlayPause.setChecked(false);
    }

    public void switchLand(boolean mCurrentIsLand) {
        if(mCurrentIsLand){
            lController.setVisibility(VISIBLE);
            vController.setVisibility(GONE);
        }else {
            vController.setVisibility(VISIBLE);
            lController.setVisibility(GONE);
        }
    }

    public void hideAllController(){
        lController.setVisibility(GONE);
        vController.setVisibility(GONE);
    }



    public void updatePosition(long duration, int position, int bufferProgress, String sDuration, String sPosition) {
        //竖屏相关
        progressBar.setMax((int) duration);
        progressBar.setProgress( position);
        progressBar.setSecondaryProgress(bufferProgress);
        playerDuration.setText(sDuration);
        playerPosition.setText(sPosition);
        //横屏相关
        lProgressBar.setMax((int)duration);
        lProgressBar.setProgress(position);
        lProgressBar.setSecondaryProgress(bufferProgress);
        playerLDuration.setText(sDuration);
        playerLPosition.setText(sPosition);


    }

    public int getMax() {
        return progressBar.getMax();
    }


    interface OnProgressControllerListener{
        void onChangeOrientation(boolean b);
        void onIsFromUserSwitch(boolean b);
    }
    private OnProgressControllerListener mListener;
    public void setOnProgressControllerListener(OnProgressControllerListener mListener){
        this.mListener = mListener;
    }


    public ProgressController(@NonNull Context context, UVMediaPlayer player, Activity activity, AnalyCallBack analyCallBack) {
        this(context,null);
        this.player = player;
        this.activity = activity;
        this.analyCallBack = analyCallBack;
        initView(context);
        initListener();
    }

    public ProgressController(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_progress_controller,this,true);

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

    }

    private void initListener() {

        //进度条
        progressBar.setOnSeekBarChangeListener(this);
        lProgressBar.setOnSeekBarChangeListener(this);

        //暂停
        buttonPlayPause.setOnCheckedChangeListener(this);
        buttonLPlayPause.setOnCheckedChangeListener(this);

        //陀螺仪
        playerGyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.setGyroEnabled(true);
                    analyCallBack.openGyroscope();
                }else {
                    player.setGyroEnabled(false);
                    analyCallBack.closeGyroscope();
                }
            }
        });

        //双屏
        playerScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    player.setDualScreenEnabled(true);
                    analyCallBack.openDoubelScreen();
                }else {
                    player.setDualScreenEnabled(false);
                    analyCallBack.closeDoubelScreen();
                }
            }
        });

        //最小化
        retract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onIsFromUserSwitch(true);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mListener.onChangeOrientation(false);
                analyCallBack.smallScreen();
            }
        });

        //最大化
        spread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onIsFromUserSwitch(true);
                player.hideToolbarLater();
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mListener.onChangeOrientation(true);
                analyCallBack.onFullScreen();
            }
        });
    }


    public void onBackPress(){
        mListener.onIsFromUserSwitch(true);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mListener.onChangeOrientation(false);
    }



    public void initPlay() {
        buttonPlayPause.setChecked(true);
        buttonLPlayPause.setChecked(true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        playerPosition.setText( Utils.formatTime(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        player.cancelHideToolbar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        player.hideToolbarLater();
        int progress = seekBar.getProgress();
        player.seekTo(progress);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            player.pause();
            analyCallBack.onPause();
        }else {
            player.play();
            analyCallBack.onStart();
        }
    }
}
