import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import us.codecraft.webmagic.Spider;

public class MainFrame extends JFrame {
    rightPanel right;

    public MainFrame() {
        setSize(830, 500);
        setLayout(null);

        // 创建左右两个Panel
        // 左边用来放各种控件和按钮
        right = new rightPanel();
        right.setBounds(new Rectangle(400,0,400,500));
        leftPanel left = new leftPanel(this);
        left.setBounds(new Rectangle(0,0,400,500));
        // 右边用来显示图像
        add(left);
        add(right);
    }
}

class leftPanel extends JPanel {
    // 数据库对象
    Db db = null;
    // 主要作用是调用rightPanel
    MainFrame mainFrame;

    JTextField dbDir;
    JTextField imgDir;
    JTextField status;
    JTextField orderBy;
    JTextField keywords;
    JTextField marked;
    JTextField showTitle;
    JTextArea listArea;
    JButton connectButton;
    JButton nextButton;
    JButton preButton;
    JButton openButtoon;
    JButton updateButton;
    JButton downloadButton;

    int offset = 0;
    int row = 0;
    String img = null;
    String title = null;
    final static int ROWS_PER_PAGE = 13;

    /**
     * 构造函数
     *
     * @param mainFrame
     */
    public leftPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setSize(400, 500);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        /*******************************控件添加开始**********************************************/
        dbDir = new JTextField("D:\\crawler.db", 12);
        imgDir = new JTextField("D:\\imgSave\\", 12);
        listArea = new JTextArea(14, 35);
        connectButton = new JButton("Connect");
        preButton = new JButton("Precious");
        nextButton = new JButton("Next");
        orderBy = new JTextField("", 7);
        status = new JTextField(31);
        keywords = new JTextField(8);
        updateButton = new JButton("Update");
        openButtoon = new JButton("Open");
        marked = new JTextField(3);
        showTitle = new JTextField(26);
        downloadButton = new JButton("Download");

        add(new JLabel("Database"));
        add(dbDir);
        add(new JLabel("Picture "));
        add(imgDir);
        add(new JLabel("ID     TITLE         VIEWS COMMENTS   KEYWORDS  MARKED"));
        JScrollPane scrollPane = new JScrollPane(listArea);
        add(scrollPane, BorderLayout.AFTER_LAST_LINE);
        add(connectButton, BorderLayout.SOUTH);
        add(preButton);
        add(nextButton);
        add(new JLabel(" ORDER BY"));
        add(orderBy);
        add(new JLabel("Status"));
        add(status);
        add(new JLabel("Keywords"));
        add(keywords);
        add(new JLabel("Marks"));
        add(marked);
        add(updateButton);
        add(openButtoon);
        add(showTitle);
        add(downloadButton);
        /*******************************控件添加结束**********************************************/

        /**
         * Connect 按钮事件
         * 初始化数据库db对象，连接数据库
         */
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (db == null) {
                    db = new Db(dbDir.getText());
                    status.setText("SQL: " + db.getStatus());
                }
            }
        });

        /**
         * pre 按钮事件
         * 获得前ROWS_PER_PAGE个list的数据
         */
        preButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (offset >= ROWS_PER_PAGE) {
                    offset -= ROWS_PER_PAGE;
                    listArea.setText(db.select(ROWS_PER_PAGE, offset, orderBy.getText()));
                    status.setText("SQL: " + db.getStatus());
                }
            }
        });

        /**
         * Next 按钮事件
         * 获得后10个list的数据
         */
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listArea.setText(db.select(ROWS_PER_PAGE, offset, orderBy.getText()));
                status.setText("SQL: " + db.getStatus());
                offset += ROWS_PER_PAGE;
            }
        });

        /**
         * Open 按钮事件
         * 用图片浏览器打开rightPanel的图片
         */
        openButtoon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = "cmd.exe /c start ";
                Runtime run = Runtime.getRuntime();
                try {
                    //调用系统图片查看器
                    Process p = run.exec(cmd + img.replaceAll(" ", "\" \""));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        /**
         * Update 按钮事件
         * 为当前的图片组添加关键字，评分，并更新数据库
         */
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (keywords.getText().isEmpty() || marked.getText().isEmpty()) {
                    // 输入无效
                    status.setText("Invalid input!");
                } else {
                    int rt = db.updateWhere(title, keywords.getText(), marked.getText());
                    status.setText(rt + " rows effected");
                }
            }
        });

        /**
         * Download 按钮事件
         * 下载当前条目的一组图片
         */
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取url
                String url = db.getUrl(showTitle.getText());
                if (!url.equals(null)) {
                    status.setText("SQL: " + db.getStatus());
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 爬虫
                            Crawler crawler = new Crawler().setTitle(showTitle.getText());
                            Spider.create(crawler).addUrl(url).run();
                            status.setText("Download finished.");
                        }
                    });
                    t.start();
                }
            }
        });

        /**
         * listArea 点击事件
         * 获得当前光标位置，确定选定的条目
         */
        listArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                try {
                    // 获得插入符的位置
                    int offset = e.getDot();
                    // 将组件文本中的偏移量转换为行号
                    row = listArea.getLineOfOffset(offset);

                    //getLineStartOffset(int line)   取得给定行起始处的偏移量。
                    //getLineEndOffset(int line)     取得给定行结尾处的偏移量。
                    //int column = e.getDot() - listArea.getLineStartOffset(row);

                    if (mainFrame.right != null && row < ROWS_PER_PAGE) {
                        String[] lines = listArea.getText().split("\n");
                        // 获得row行的一行信息
                        if (lines.length > 1) {
                            title = lines[row].split("\\|")[1];
                            showTitle.setText(title);
                            //System.out.println(path);
                            // 找到对应的图片位置
                            try {
                                File file = new File(imgDir.getText() + title);
                                File[] files = file.listFiles();
                                String img_path = null;
                                for (File f : files) {
                                    // 获得封面图
                                    if (f.getName().equals("cover.jpg")) {
                                        img_path = f.getPath();
                                        break;
                                    }
                                }
                                // 没有就找目录下的第一张图
                                if (img_path == null) {
                                    img_path = files[0].getPath();
                                }

                                img = img_path;
                                // 在右边显示
                                mainFrame.right.setImg(img_path);
                                mainFrame.right.repaint();

                            } catch (NullPointerException n) {
                                n.printStackTrace();
                                status.setText("ERROR: Can not find picture file!");
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}


class rightPanel extends JPanel {
    Image img;

    public rightPanel() {
        setSize(400, 500);
    }

    /**
     * 读取图片
     *
     * @param fileName
     */
    public void setImg(String fileName) {
        try {
            img = ImageIO.read(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示图片（缩放）
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (img != null) {
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            int h = (int) height * 400 / width;
            g.drawImage(img, 0, 0, 400, h, this);
        }
    }

}