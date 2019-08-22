package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class TestFile {
    /**
     * 测试文件分块方法
     *
     * @throws IOException
     */
    @Test
    public void testcChunk() throws IOException {
        //源文件
        File sourceFile = new File("F:\\develop\\video\\lucene.avi");
        //块文件目录
        String chunkFileFolder = "F:\\develop\\video\\chunks\\";

        //定义块文件的大小
        long chunkFileSize = 1 * 1024 * 1024;

        //求出源文件的块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);

        //创建读文件的对象,指定读取的文件,和方式  r:只读
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");

        //创建一个字节数组(用于缓冲)
        byte[] b = new byte[1024];
        for (long i = 0; i < chunkFileNum; i++) {
            //块文件
            File chunkFile = new File(chunkFileFolder + i);
            //创建向块文件的写对象
            RandomAccessFile raf_w = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(b)) != -1) {
                //还有内容
                raf_w.write(b, 0, len);
                //如果块文件的大小达到了1M,就进行下一次循环
                if (chunkFile.length() >= chunkFileSize) {
                    break;
                }
            }
            raf_w.close();
        }
        raf_r.close();
    }

    /**
     * 合并文件
     */
    @Test
    public void testMergeFile() throws IOException {
        //找到块文件的目录
        File chunkFolder = new File("F:\\develop\\video\\chunks\\");
        //合并文件
        File mergeFile = new File("F:\\develop\\video\\lucene_merge.avi");
        //对文件进行判断,存在删除后在创建
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //写文件
        RandomAccessFile raf_w = new RandomAccessFile(mergeFile,"rw");
        //指针指向文件顶端
        raf_w.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] files = chunkFolder.listFiles();
        //把数组转为集合,然后进行排序
        List<File> fileList = Arrays.asList(files);
        //从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())<Integer.parseInt(o2.getName())){
                    return -1;
                }
                return 1;
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_r=new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while ((len=raf_r.read(b))!=-1){
                raf_w.write(b,0,len);
            }
            raf_r.close();
        }
        raf_w.close();
    }
}
