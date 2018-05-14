package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import daily.zjrb.com.daily_vr.R;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:播放和重播按钮控制器
 */


public class PrepareController extends FrameLayout {
    private TextView playerNetHint;
    private LinearLayout playerStart;
    private LinearLayout playerRestart;

    public boolean getUIState() {
        return playerStart.getVisibility() == VISIBLE || playerRestart.getVisibility() == VISIBLE;
    }

    public void showStartView() {
        playerStart.setVisibility(VISIBLE);
    }

    public boolean shoudPlay() {
        return playerNetHint.getVisibility() == VISIBLE && (playerNetHint.getText().toString().equals("用流量播放") || playerNetHint.getText().toString().equals("已切换至wifi"));
    }

    public void showEnd() {
        playerRestart.setVisibility(VISIBLE);
    }

    public boolean isNetHintShowing() {
        return playerNetHint.getVisibility() == VISIBLE;
    }

    interface OnPrepareControllerListener{
        void onStartClicked();
        void onRestartClicked();
    }
    private OnPrepareControllerListener mListener;
    public void setOnPrepareControllerListener(OnPrepareControllerListener mListener){
        this.mListener = mListener;
    }



    public PrepareController(@NonNull Context context) {
        this(context,null);
    }

    public PrepareController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PrepareController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }

    private void initListener() {
        //进入播放页面开始播放
        playerStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerStart.setVisibility(GONE);
                mListener.onStartClicked();
            }
        });

        playerRestart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRestart.setVisibility(GONE);
                mListener.onRestartClicked();
            }
        });
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_prepare_controller,this,true);
        playerStart = (LinearLayout) view.findViewById(R.id.ll_start);
        playerNetHint = (TextView) view.findViewById(R.id.tv_net_hint);
        playerRestart = (LinearLayout) view.findViewById(R.id.ll_restart);
    }


    public void setNetHintText(String text) {
        playerStart.setVisibility(VISIBLE);
        playerNetHint.setVisibility(VISIBLE);
        playerNetHint.setText(text);
    }
}
