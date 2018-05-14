package daily.zjrb.com.daily_vr.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import daily.zjrb.com.daily_vr.R;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:提示性UI和声音控制
 */


public class HintController extends RelativeLayout{
    private CheckBox playerVolumn;
    private ImageView hintBufferProgress;
    private Activity activity;




    public HintController(Activity activity) {
        super(activity);
        this.activity = activity;
        initView(activity);
        initListener();
    }

    private void initListener() {
        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
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


    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_hint_controller,this,true);
        playerVolumn = (CheckBox) view.findViewById(R.id.player_ic_volume);
        hintBufferProgress = (ImageView) view.findViewById(R.id.player_buffer_progress);
        ObjectAnimator anim = ObjectAnimator.ofFloat(hintBufferProgress, "rotation", 0f, 360f);
        anim.setDuration(900);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }

    public void showBuffering(boolean visiable) {
        hintBufferProgress.setVisibility(visiable?VISIBLE:GONE);
    }
}
