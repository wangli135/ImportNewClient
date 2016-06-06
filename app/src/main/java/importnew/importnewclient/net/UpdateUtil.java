package importnew.importnewclient.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 更新工具类
 * Created by Xingfeng on 2016/6/6.
 */
public class UpdateUtil {

    public static class AppUpdateInfo implements Serializable {

        private String version;
        private String apkAddress;
        private String changeLog;

        public AppUpdateInfo() {
        }

        public AppUpdateInfo(String version, String apkAddress, String changeLog) {
            this.version = version;
            this.apkAddress = apkAddress;
            this.changeLog = changeLog;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getApkAddress() {
            return apkAddress;
        }

        public void setApkAddress(String apkAddress) {
            this.apkAddress = apkAddress;
        }

        public String getChangeLog() {
            return changeLog;
        }

        public void setChangeLog(String changeLog) {
            this.changeLog = changeLog;
        }

        @Override
        public String toString() {
            return "AppUpdateInfo{" +
                    "version='" + version + '\'' +
                    ", apkAddress='" + apkAddress + '\'' +
                    ", changeLog='" + changeLog + '\'' +
                    '}';
        }
    }


    private static final String TAG = "UpdateUtil";

    /**
     * 检查更新
     *
     * @return 无更新，返回null
     */
    public AppUpdateInfo checkUpdate(Context context) {

        AppUpdateInfo appUpdateInfo = null;

        //获取文档
        OkHttpClient httpClient = HttpManager.getInstance(context).getClient();
        Response response = null;
        try {
            Request request = new Request.Builder().url(URLManager.UPDATE_XML_ADDRESS)
                    .cacheControl(new CacheControl.Builder().noCache().build()).build();
            response = httpClient.newCall(request).execute();

            appUpdateInfo = parserXml(response.body().byteStream());
            if (appUpdateInfo == null)
                return null;

            Log.i(TAG, appUpdateInfo.toString());

            String nowVersion = getAppVersion(context);

            if (!TextUtils.isEmpty(nowVersion) && nowVersion.compareTo(appUpdateInfo.getVersion()) < 0) {
                return appUpdateInfo;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "更新出错");
            return null;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
    }

    /**
     * 解析XML文档
     *
     * @param in XML文档网络流
     * @return 解析错误，返回null;否则返回对象
     */
    private AppUpdateInfo parserXml(InputStream in) {

        AppUpdateInfo appUpdateInfo = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            appUpdateInfo = new AppUpdateInfo();
            while (parser.next() != XmlPullParser.END_DOCUMENT) {

                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("version")) {
                    readVersion(parser, appUpdateInfo);
                } else if (name.equals("address")) {
                    readAddress(parser, appUpdateInfo);
                } else if (name.equals("changelog")) {
                    readChangeLog(parser, appUpdateInfo);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, "更新，解析XML出错");
            return null;
        }

        return appUpdateInfo;
    }

    private void readVersion(XmlPullParser parser, AppUpdateInfo appUpdateInfo) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "version");
        String text = readText(parser);
        appUpdateInfo.setVersion(text);
        parser.require(XmlPullParser.END_TAG, null, "version");
    }

    private void readAddress(XmlPullParser parser, AppUpdateInfo appUpdateInfo) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "address");
        String text = readText(parser);
        appUpdateInfo.setApkAddress(text);
        parser.require(XmlPullParser.END_TAG, null, "address");
    }

    private void readChangeLog(XmlPullParser parser, AppUpdateInfo appUpdateInfo) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "changelog");
        String text = readText(parser);
        appUpdateInfo.setChangeLog(text);
        parser.require(XmlPullParser.END_TAG, null, "changelog");
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    private String getAppVersion(Context context) throws PackageManager.NameNotFoundException {

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        return packageInfo.versionName;
    }
}
