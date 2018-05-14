package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import daily.zjrb.com.daily_vr.R;

/**
 * @author: lujialei
 * @date: 2018/5/14
 * @describe:
 */


public class PrepareController extends FrameLayout {

    public PrepareController(@NonNull Context context) {
        this(context,null);
    }

    public PrepareController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PrepareController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.vr_layout_prepare_controller,this,true);
        ButterKnife.bind(this,view);
    }
}
