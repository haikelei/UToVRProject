package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.util.AttributeSet;

import com.trs.tasdk.entity.Base;

import daily.zjrb.com.daily_vr.R;

/**
 * @author: lujialei
 * @date: 2018/4/26
 * @describe:
 */


public class LController extends BaseController {
    public LController(Context context) {
        this(context,null);
    }

    public LController(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    int getLayoutResId() {
        return R.layout.vr_layout_l_controller;
    }
}
