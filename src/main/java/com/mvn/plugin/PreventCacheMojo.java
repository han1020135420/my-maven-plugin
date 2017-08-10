package com.mvn.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hanpeng on 2017/8/10.
 */
@Mojo(name="cache")
public class PreventCacheMojo extends MyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        /**
         * 1.遍历所有的静态文件
         * 2.找到符合的文件
         * 3.匹配访问静态文件的路径
         * 4.在路径后面增加防止缓存的版本号
         */

        if(checkIsNull(webDir)){
            File file = new File(webDir);
            if(checkFileEmpty(file)){
                try {
                    iteratorFiles(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //遍历所有的文件
    public void iteratorFiles(File file) throws IOException {
        //如果是目录
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:files) {
                iteratorFiles(f);
            }
        }else{
            String fileName = file.getName();
            String sufix = FilenameUtils.getExtension(fileName);
            if(StringUtils.isNotBlank(sufix) &&
                    (sufix.equals("html") ||
                            sufix.equals("jsp"))){
                String content = FileUtils.readFileToString(file,"UTF-8");//读取文件内容
                content = addVersion(content);
                FileUtils.writeStringToFile(file, content, "UTF-8");  //重写文件内容
            }
        }
    }

    private String addVersion(String content){
        content =  handleJs(content);
        content = handleCss(content);
        return content;
    }

    /**
     * 处理引用js
     * @param content
     * @return
     */
    private String handleJs(String content){
        Matcher matcher = JS_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String pfull = matcher.group();//查找匹配到的字符串
            String jsPart = matcher.group(1);//得到 文件路径
            /**
             * pfull.substring(0, matcher.start(1) - matcher.start())  获取路径前面的字符串 eg: <link href="
             *
             * setVersion(cssPath.trim(),"v", version) 增加版本号
             *
             * pfull.charAt(pfull.length() - 1) 单引号 或者 双引号
             *
             */
            pfull = pfull.substring(0, matcher.start(1) - matcher.start()) + setVersion(jsPart.trim(),"v", version)  + pfull.charAt(pfull.length() - 1);
            //Matcher.find()找到匹配的地方用“Matcher.quoteReplacement(pfull)”替换掉然后加进StringBuffer中去
            //Matcher.quoteReplacement(pfull) 消除特殊字符的影响
            matcher.appendReplacement(sb, Matcher.quoteReplacement(pfull));
        }
        matcher.appendTail(sb);  //将匹配的字符串后面的内容也加上
        return sb.toString();
    }

    /**
     * 处理引用css
     * @param content
     * @return
     */
    private String handleCss(String content) {
        Matcher matcher = CSS_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String pfull = matcher.group();
            String cssPath = matcher.group(1);
            pfull = pfull.substring(0, matcher.start(1) - matcher.start()) + setVersion(cssPath.trim(),"v", version) + pfull.charAt(pfull.length() - 1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(pfull));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 设置版本号
     * @param url
     * @param paramname
     * @param paramvalue
     * @return
     */
    private String setVersion(String url, String paramname, String paramvalue) {

        if (StringUtils.isBlank(url)) {
            return "";
        }

        String reg1 = "(" + paramname + "=.*?)(?=(&.*|$))";
        if (Pattern.compile(reg1).matcher(url).find()) {
            return url.replaceFirst(reg1, paramname + "=" + paramvalue);
        }
        if (url.contains("?")) {
            return url + "&" + paramname + "=" + paramvalue;
        }
        return url + "?" + paramname + "=" + paramvalue;
    }
}
