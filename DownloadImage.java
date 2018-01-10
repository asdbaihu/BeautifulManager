import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.awt.*;
import java.awt.event.*;

public class DownloadImage implements Runnable {

    String urlString;
    String filename;
    String savePath;

    DownloadImage(String urlString, String filename, String savePath) {
        this.urlString = urlString;
        this.filename = filename;
        this.savePath = savePath;
    }

    @Override
    public void run() {

        InputStream is = null;
        OutputStream os = null;
        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5000);
            con.setRequestProperty("User-Agent", "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31 Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) ");
            // 输入流
            is = con.getInputStream();

            // 1M的数据缓冲
            byte[] bs = new byte[1024 * 1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File sf = new File(savePath);
            if (!sf.exists()) {
                sf.mkdirs();
            }
            os = new FileOutputStream(sf.getPath() + "\\" + filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

            // 完毕，关闭所有链接
            os.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}