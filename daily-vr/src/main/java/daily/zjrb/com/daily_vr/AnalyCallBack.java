package daily.zjrb.com.daily_vr;

/**
 * @author: lujialei
 * @date: 2018/5/15
 * @describe:
 */


public interface AnalyCallBack {
    void onStart();
    void onPause();
    void onFullScreen();
    void smallScreen();
    void openVolumn();
    void mute();
    void openGyroscope();
    void closeGyroscope();
    void openDoubelScreen();
    void closeDoubelScreen();
}
