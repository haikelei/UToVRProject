package daily.zjrb.com.daily_vr.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utovr.player.UVMediaType;

import butterknife.BindView;
import daily.zjrb.com.daily_vr.R;
import daily.zjrb.com.daily_vr.R2;

/**
 * @author: lujialei
 * @date: 2018/4/24
 * @describe:
 */


public class Controller extends BaseController {


    public Controller(Context context) {
        this(context, null);
    }

    public Controller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Controller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    int getLayoutResId() {
        return R.layout.vr_layout_controller;
    }

}
