package com.mvn.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by hanpeng on 2017/8/10.
 */
public abstract class MyMojo extends AbstractMojo {

    @Parameter
    protected String dir;

    protected String webDir;

    protected int count;

    protected String version;

    public static Pattern JS_PATTERN = Pattern.compile(
            "<script[\\s\\S]+?src\\s*=\\s*[\"|\'](.+\\.js.*?)[\"|\']{1}", Pattern.CASE_INSENSITIVE);

    public static Pattern CSS_PATTERN = Pattern.compile(
            "<link[\\s\\S]+?href\\s*=\\s*[\"|\'](.+\\.css.*?)[\"|\']{1}", Pattern.CASE_INSENSITIVE);

    public void execute() throws MojoExecutionException, MojoFailureException {
        webDir = dir + File.separator + "src" +File.separator + "main" + File.separator + "webapp";
        count = 0;
        version = new Date().getTime()+"";
    }


    //判断字符串
    protected boolean checkIsNull(String path){
        if(path != null && !path.equals("")){
            return true;
        }
        return false;
    }

    //判断file
    protected boolean checkFileEmpty(File file){
        if(file.exists()){
            return true;
        }
        return false;
    }

}
