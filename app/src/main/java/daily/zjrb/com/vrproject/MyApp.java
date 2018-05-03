package daily.zjrb.com.vrproject;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.zjrb.core.common.base.BaseInit;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

/**
 * @author: lujialei
 * @date: 2018/4/27
 * @describe:
 */


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        AppUtils.setChannel("hah");
        SettingManager.init(this);
        ThemeMode.initTheme(R.style.AppTheme, R.style.NightAppTheme);
        UiModeManager.init(this, R.styleable.SupportUiMode);
        BaseInit.init(this,"bianfeng");
        setTheme(ThemeMode.isNightMode() ? R.style.NightAppTheme : R.style.AppTheme);
    }
}
