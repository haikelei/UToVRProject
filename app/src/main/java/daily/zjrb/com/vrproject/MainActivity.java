package daily.zjrb.com.vrproject;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.base.toolbar.holder.DefaultTopBarHolder1;

public class MainActivity extends BaseActivity {
    int ui;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.NightAppTheme);
        setTranslucenceStatusBarBg();
        setContentView(R.layout.activity_main);
        ui = getWindow().getDecorView().getSystemUiVisibility();
    }

    /**
     * topbar
     */
    public DefaultTopBarHolder1 topHolder;

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        topHolder = TopBarFactory.createDefault1(view, this);
        return topHolder.getView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //横屏去掉topbar
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            hideTopBar();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }else {
            showTopBar();
            getWindow().getDecorView().setSystemUiVisibility(ui);
        }
    }
}

