package daily.zjrb.com.daily_vr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.utovr.player.UVMediaType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import daily.zjrb.com.daily_vr.player.VRManager;

/**
 * @author: lujialei
 * @date: 2018/4/16
 * @describe:
 */


public class VRFragment extends Fragment {

    Unbinder unbinder;
    private RelativeLayout rlParent = null;
    private VRManager vrManager;
    private String Path = "http://cache.utovr.com/201508270528174780.m3u8";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vr_player_activity, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化播放器
        rlParent = (RelativeLayout) view.findViewById(R.id.activity_rlParent);
        AnalyCallBack analyCallBack = new AnalyCallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFullScreen() {

            }

            @Override
            public void smallScreen() {

            }

            @Override
            public void openVolumn() {

            }

            @Override
            public void mute() {

            }

            @Override
            public void openGyroscope() {

            }

            @Override
            public void closeGyroscope() {

            }

            @Override
            public void openDoubelScreen() {

            }

            @Override
            public void closeDoubelScreen() {

            }
        };
        VrSource source = new VrSource(UVMediaType.UVMEDIA_TYPE_M3U8,Path,100000,"",true);
        vrManager = new VRManager(source,getActivity(), rlParent,analyCallBack);
        vrManager.changeOrientation(false);

    }


    @Override
    public void onResume() {
        super.onResume();
        vrManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        vrManager.onPause();
    }

    @Override
    public void onDestroy() {
        vrManager.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
