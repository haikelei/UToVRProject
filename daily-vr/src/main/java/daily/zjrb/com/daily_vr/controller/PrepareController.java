package daily.zjrb.com.daily_vr.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjrb.core.common.glide.AppGlideOptions;
import com.zjrb.core.common.glide.GlideApp;
import com.zjrb.core.common.global.PH;

import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.other.Utils;
import daily.zjrb.com.daily_vr.bean.VrSource;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:播放和重播按钮控制器  播放之前准备界面控制器
 */


public class PrepareController extends FrameLayout {
    private TextView playerNetHint;
    private LinearLayout playerStart;
    private LinearLayout playerRestart;
    public boolean hasShowedNetHint;
    private TextView tvDuration;
    private ImageView ivMask;
    private View shadeView;

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
        shadeView.setVisibility(VISIBLE);
        mListener.onShowEndView();
    }

    public boolean isNetHintShowing() {
        return playerNetHint.getVisibility() == VISIBLE;
    }

    interface OnPrepareControllerListener{
        void onStartClicked();
        void onRestartClicked();
        void onShowEndView();
    }
    private OnPrepareControllerListener mListener;
    public void setOnPrepareControllerListener(OnPrepareControllerListener mListener){
        this.mListener = mListener;
    }



    public PrepareController(@NonNull Context context, VrSource source) {
        super(context);
        initView(context,source);
        initListener();
    }

    public void hindMaskImage(){
        ivMask.setVisibility(GONE);
        shadeView.setVisibility(GONE);
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
                shadeView.setVisibility(GONE);
                mListener.onRestartClicked();
            }
        });
    }

    private void initView(Context context,VrSource source) {
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_prepare_controller,this,true);
        playerStart = (LinearLayout) view.findViewById(R.id.ll_start);
        playerNetHint = (TextView) view.findViewById(R.id.tv_net_hint);
        playerRestart = (LinearLayout) view.findViewById(R.id.ll_restart);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        shadeView = findViewById(R.id.shade);
        if(source.getDuration()>0){
            tvDuration.setText(Utils.duration(source.getDuration() * 1000));
            tvDuration.setVisibility(VISIBLE);
        }else {
            tvDuration.setVisibility(GONE);
        }

        ivMask = (ImageView) view.findViewById(R.id.iv_mask);
        GlideApp.with(ivMask).load(source.getPic()).placeholder(PH.zheBig()).centerCrop()
                .apply(AppGlideOptions.bigOptions()).into(ivMask);
    }


    public void setNetHintText(String text) {
        playerStart.setVisibility(VISIBLE);
        playerNetHint.setVisibility(VISIBLE);
        playerNetHint.setText(text);
        if(text.equals("用流量播放")){
            hasShowedNetHint = true;
        }
    }


}
