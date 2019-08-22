package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {
    /**
     *  测试使用第三方应用程序
     * @throws IOException
     */
    @Test
    public void testProcessBuilder() throws IOException {
        //创建一个ProcessBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置第三方程序的命令
        //processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
        //将标准流和错误流合并,这样我们得到的inputStream正常和错误信息都可以得到
        processBuilder.redirectErrorStream(true);
        //启动一个进程
        Process process = processBuilder.start();
        //通过标准流来拿到正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //转换成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");

        //进行读和输出的操作
        //定义一个字符集合的缓冲区
        char[] chars =new char[1024];
        //定义长度
        int len =-1;
        while ((len=reader.read(chars))!=-1){
            String string = new String(chars,0,len);
            System.out.println(string);
        }
        inputStream.close();
        reader.close();
    }

    /**
     *  测试生成mp4文件
     * @throws IOException
     */
    @Test
    public void testffmpeg() throws IOException {
        //创建一个ProcessBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置第三方程序的命令
        //设置命令的List集合
        //定义命令内容
        List<String> command = new ArrayList<>();
        command.add("F:\\Java\\ffmpeg\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("E:\\FFmpegTest\\1.avi");//添加文件信息
        command.add("-y");//覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("E:\\FFmpegTest\\1.mp4");//保存成的文件路径以及格式
        processBuilder.command(command);
        //将标准流和错误流合并,这样我们得到的inputStream正常和错误信息都可以得到
        processBuilder.redirectErrorStream(true);
        //启动一个进程
        Process process = processBuilder.start();
        //通过标准流来拿到正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //转换成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");

        //进行读和输出的操作
        //定义一个字符集合的缓冲区
        char[] chars =new char[1024];
        StringBuffer outputString = new StringBuffer();
        //定义长度
        int len =-1;
        while ((len=reader.read(chars))!=-1){
            String string = new String(chars,0,len);
            outputString.append(string);
            System.out.println(string);
        }
        inputStream.close();
        reader.close();
    }

    /**
     * 使用工具类进行转换
     */
    @Test
    public void testMp4VideoUtil(){
        //ffmpeg的路径
        String ffmpeg_path = "F:\\Java\\ffmpeg\\bin\\ffmpeg.exe";//ffmpeg的安装位置
        //源avi视频的路径
        String video_path = "E:\\FFmpegTest\\1.avi";
        //转换后mp4文件的名称
        String mp4_name = "1.mp4";
        //转换后mp4文件的路径
        String mp4_path = "E:\\FFmpegTest\\";
        //创建工具类对象
        Mp4VideoUtil util = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4_path);
        //开始视频转换，成功将返回success
        String s = util.generateMp4();
        System.out.println(s);
    }

}
