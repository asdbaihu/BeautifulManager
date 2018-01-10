import javax.swing.*;
import java.awt.*;

public class Viewer {
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 主界面
                JFrame frame = new MainFrame();
                frame.setTitle("图片浏览器");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
    }
}

