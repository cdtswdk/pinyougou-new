package com.itcast.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class demoTest {
    public static void main(String[] args) throws IOException, TemplateException {
        //第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());

        //第二步：设置模板文件所在的路径。
        //String path = System.getProperty("user.dir")+"/src/main/resources/";
        String path = "C:\\Users\\Administrator\\Desktop\\pinyougou\\freemarker-demo\\src\\main\\resources";
        configuration.setDirectoryForTemplateLoading(new File(path));

        //第三步：设置模板文件使用的字符集。一般就是 utf-8.
        configuration.setDefaultEncoding("UTF-8");

        //第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("test.ftl");

        //第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("name", "张三");
        dataMap.put("message", "我很快乐");
        dataMap.put("success", false);

        List goodsList = new ArrayList();
        Map goods1 = new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2 = new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3 = new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        dataMap.put("goodsList", goodsList);

        dataMap.put("today", new Date());

        dataMap.put("point", 102920122);
        dataMap.put("aaa", null);

        //第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
        FileWriter writer = new FileWriter("E:/test.html");

        //第七步：调用模板对象的 process 方法输出文件。
        template.process(dataMap, writer);

        //第八步：关闭流
        writer.flush();
        writer.close();
    }
}
