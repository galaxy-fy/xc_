package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 测试html文件的生成方法
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {
    //基于模板生成静态化文件
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //得到classpath的路径
        String classpath = this.getClass().getResource("/").getPath();
        //System.out.println(path);
        //定义模板路径
        configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));
        //获取模板文件的内容
        Template template = configuration.getTemplate("test1.ftl");
        //定义(获取)数据模型
        Map map = this.getMap();
        /**
         * 静态化 调用工具类的方法实现静态化
         */
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
        //System.out.println(content);
        //使用流来进行文件的读写操作
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/Demo/test1.html"));
        //输出文件
        IOUtils.copy(inputStream,fileOutputStream);
        //释放资源
        inputStream.close();
        fileOutputStream.close();
    }

    //基于字符串生成静态化文件
    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //模板内容，这里测试时使用简单的字符串作为模板
        String templateString="" +
                "<html>\n" +
                "    <head></head>\n" +
                "    <body>\n" +
                "    名称：${name}\n" +
                "    </body>\n" +
                "</html>";
        //使用一个模板加载器加载模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateString);
        //在配置中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板的内容
        Template template = configuration.getTemplate("template", "utf-8");
        //System.out.println(template);

        //数据模型
        Map<String,Object> map = new HashMap<>();
        map.put("name","Galaxy");
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/Demo/test2.html"));
        IOUtils.copy(inputStream,fileOutputStream);
        //释放资源
        inputStream.close();
        fileOutputStream.close();
    }




    public Map getMap(){
        Map map = new HashMap();
        map.put("name","学成在线");
        //学生1 信息
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        //学生2 信息
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //创建一个朋友集合
        List<Student> friends = new ArrayList<>();
        //把学生1放到集合中
        friends.add(stu1);
        //给学生2设置朋友
        stu2.setFriends(friends);
        //给学生2设置最好朋友
        stu2.setBestFriend(stu1);
        //创建一个学生集合
        List<Student> stus = new ArrayList<>();
        //把学生1 2 放入集合中
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus",stus);
        //准备map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        //把学生 1 2 放入map中
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //向数据模型放数据
        map.put("stu1",stu1);
        //向数据模型放数据
        map.put("stuMap",stuMap);
        map.put("point", 102920122);
        return map;
    }
}
