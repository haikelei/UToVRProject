package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import daily.zjrb.com.daily_vr.CalcTime;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.R2;

/**
 * @author: lujialei
 * @date: 2018/4/24
 * @describe:
 */


public class VController extends BaseController {


    @BindView(R2.id.player_seek_bar)
    SeekBar playerSeekBar;
    @BindView(R2.id.player_position)
    TextView playerPosition;
    @BindView(R2.id.player_duration)
    TextView playerDuration;
    @BindView(R2.id.player_full_screen)
    ImageView playerFullScreen;
    @BindView(R2.id.player_control_bar)
    LinearLayout playerControlBar;
    @BindView(R2.id.player_ic_volume)
    ImageView playerIcVolume;
    @BindView(R2.id.player_bottom_progress_bar)
    ProgressBar playerBottomProgressBar;
    @BindView(R2.id.player_buffer_progress)
    ProgressBar hintBufferProgress;
    @BindView(R2.id.player_stub_play_error)
    ViewStub playerStubPlayError;
    @BindView(R2.id.player_stub_mobile_network)
    ViewStub playerStubMobileNetwork;
    @BindView(R2.id.player_play_pause)
    CheckBox playerPlayPause;



    public VController(Context context) {
        this(context, null);
    }

    public VController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    int getLayoutResId() {
        return R.layout.vr_layout_v_controller;
    }


}
