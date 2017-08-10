package com.mvn.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Created by hanpeng on 2017/8/10.
 */
@Mojo(name="count")
public class CountMojo extends MyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        if(checkIsNull(dir)){
            File file = new File(dir);
            if(checkFileEmpty(file)){
                getFile(file);
            }
        }
        getLog().info("java files : " + count);
    }

    //遍历
    protected void getFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f: files) {
                getFile(f);
            }
        }else{
            //计算java的文件个数
            if(file.getName().endsWith(".java"))
                count++;
        }
    }
}
