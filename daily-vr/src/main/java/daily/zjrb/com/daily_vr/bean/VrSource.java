package daily.zjrb.com.daily_vr.bean;

import com.utovr.player.UVMediaType;

/**
 * @author: lujialei
 * @date: 2018/5/16
 * @describe:VR视频播放源对象封装
 */


public class VrSource {


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isAutoPlayWithWifi() {
        return isAutoPlayWithWifi;
    }

    public void setAutoPlayWithWifi(boolean autoPlayWithWifi) {
        isAutoPlayWithWifi = autoPlayWithWifi;
    }

    public UVMediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(UVMediaType mediaType) {
        this.mediaType = mediaType;
    }

    public VrSource(UVMediaType mediaType, String path, long duration, String pic, boolean isAutoPlayWithWifi) {
        this.mediaType = mediaType;
        this.path = path;
        this.duration = duration;
        this.pic = pic;
        this.isAutoPlayWithWifi = isAutoPlayWithWifi;
    }

    private UVMediaType mediaType;
    private String path;
    private long duration;
    private String pic;
    private boolean isAutoPlayWithWifi;

    public VrSource() {
    }


}
