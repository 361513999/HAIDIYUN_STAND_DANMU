package pad.com.haidiyun.www.common;

import android.os.Handler;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class Mail {

    // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
    // PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）, 
    //     对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。
    public static String myEmailAccount = "361513999@163.com";
    public static String myEmailPassword = "zc888168";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    public static String myEmailSMTPHost = "smtp.163.com";

    // 收件人邮箱（替换为自己知道的有效邮箱）
    public static String receiveMailAccount = "361513999@163.com";
    /**
     * 发送内容到开发者
     * @throws Exception
     */
    public static void send(Handler handler) throws Exception {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
       // props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
        /*
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        */

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, myEmailAccount, receiveMailAccount);

        // 4. 根据 Session 获取邮件传输对象
       // Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        // 
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
       // transport.connect(myEmailAccount, myEmailPassword);

      /*  MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart*//*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        */

        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.163.com", 25, "361513999@163.com", "zc888168");



        transport.sendMessage(message, message.getAllRecipients());

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
       // transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
        handler.sendEmptyMessage(0);
    }
    

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
    	SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
    	String name = TimeUtil.getTimeLog(System.currentTimeMillis());
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, sharedUtils.getStringValue("table_name"), "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "开发者", "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject(name+"("+sharedUtils.getStringValue("suitName")+")", "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）

        message.setSentDate(new Date());
        //FileUtils.read(Common.APK_LOG+name+".txt")
      //  message.setContent("数据库附件", "text/html;charset=UTF-8");

        Multipart mp = new MimeMultipart();
        message.setContent(mp); //Multipart加入到信件
//        message.setContent("<body background='http://dl.iteye.com/upload/picture/pic/110267/e244bda9-9034-36e3-87fd-807629b84222.jpg'>"
//                + "<div style='position: absolute; left: 390px; top: 150px;height: "
//                + "100px;width: 200px;' align='center'>"
//                + "<font color='red'>这是测试邮件，请勿回复</font>" + "</div></body>", "text/html;charset=UTF-8");
        //普通数据
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent("<body background='http://dl.iteye.com/upload/picture/pic/110267/e244bda9-9034-36e3-87fd-807629b84222.jpg'>"
                + "<div style='position: absolute; left: 390px; top: 150px;height: "
                + "100px;width: 200px;' align='center'>"
                + "<font color='red'>这是测试邮件，请勿回复</font>" + "</div></body>", "text/html;charset=UTF-8");
        mp.addBodyPart(textBodyPart);
        //附件

        File dbFile = new File(Common.DB_DIR+Common.DB_NAME);

        if(dbFile.exists()){
            //数据文件存在
            System.out.println("存在"+Common.DB_DIR+Common.DB_NAME);
                MimeBodyPart filePart=new MimeBodyPart();
                FileDataSource fds=new FileDataSource(Common.DB_DIR+Common.DB_NAME); //得到数据源
               DataHandler dh = new DataHandler(fds);
               filePart.setDataHandler(dh); //得到附件本身并至入BodyPart
                filePart.setFileName(MimeUtility.encodeText(dh.getName()));  //得到文件名同样至入BodyPart
                mp.addBodyPart(filePart);
        }


        // 6. 设置发件时间

        // 7. 保存设置
        message.saveChanges();
        return message;
    }

}