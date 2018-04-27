package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;

import butterknife.ButterKnife;
import daily.zjrb.com.daily_vr.CalcTime;
import daily.zjrb.com.daily_vr.R;

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


    public BaseController(Context context) {
        this(context,null);
    }

    public BaseController(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }


    public void setPlayer(UVMediaPlayer player) {
        this.player = player;
        calcTime = new CalcTime();
    }

    public void initView(Context context){
        View view = LayoutInflater.from(context).inflate(getLayoutResId(), this, true);
        ButterKnife.bind(view);
        progressBar = (SeekBar) view.findViewById(R.id.player_seek_bar);
        buttonPlayPause = (CheckBox) view.findViewById(R.id.player_play_pause);
        hintBufferProgress = (ProgressBar) view.findViewById(R.id.player_buffer_progress);
        playerDuration = (TextView) view.findViewById(R.id.player_duration);
        playerPosition = (TextView) view.findViewById(R.id.player_position);
    }

    private void initListener() {
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
                    player.replay();
                    break;
                case UVMediaPlayer.TRACK_DISABLED:
                case UVMediaPlayer.TRACK_DEFAULT:
                    break;
            }
        }
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
    public void updateCurrentPosition(final long position) {
        calcTime.calcTime(player);
        final int bufferProgress = calcTime.calcSecondaryProgress(progressBar.getMax());


        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax((int) player.getDuration());
                progressBar.setProgress((int) position);
                progressBar.setSecondaryProgress(bufferProgress);
                playerDuration.setText(calcTime.formatDuration());
                playerPosition.setText(calcTime.formatPosition());
            }
        });
    }



    abstract int getLayoutResId();

}
