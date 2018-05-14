package daily.zjrb.com.daily_vr.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utovr.player.UVMediaPlayer;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.Utils;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:
 */


public class ProgressController extends RelativeLayout {

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
    private ProgressBar bottomProgressBar;

    public void play() {
        buttonPlayPause.setChecked(false);
        buttonLPlayPause.setChecked(false);
    }

    public void switchLand(boolean mCurrentIsLand) {
        if(mCurrentIsLand){
            lController.setVisibility(VISIBLE);
        }else {
            vController.setVisibility(VISIBLE);
        }
        bottomProgressBar.setVisibility(GONE);
    }

    public void hideUI() {
        vController.setVisibility(GONE);
        lController.setVisibility(GONE);
        bottomProgressBar.setVisibility(VISIBLE);
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
        //bottom progress
        bottomProgressBar.setMax((int) player.getDuration());
        bottomProgressBar.setProgress(position);

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


    public ProgressController(@NonNull Context context, UVMediaPlayer player, Activity activity) {
        super(context);
        this.player = player;
        this.activity = activity;
        initView(context);
        initListener();
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


        //全局
        bottomProgressBar = (ProgressBar) view.findViewById(R.id.player_bottom_progress_bar);

    }


    private void initListener() {

        //进度条
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playerPosition.setText( Utils.formatTime(progress));
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
                mListener.onIsFromUserSwitch(true);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mListener.onChangeOrientation(false);
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
            }
        });
    }

    public void initPlay() {
        buttonPlayPause.setChecked(true);
        buttonLPlayPause.setChecked(true);
    }
}
