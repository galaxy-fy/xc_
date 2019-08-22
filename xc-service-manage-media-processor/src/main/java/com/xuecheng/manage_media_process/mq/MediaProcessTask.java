package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaProcessTask.class);
    //ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    //上传文件根目录
    @Value("${xc-service-manage-media.video-location}")
    String serverPath;

    @Autowired
    MediaFileRepository mediaFileRepository;

    /**
     * 监听生产者发来的消息,准备执行文件处理
     * @param msg
     * @throws Exception
     */
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")//监听消息队列
    public void receiveMediaProcessTask(String msg)throws Exception{
        //获得消息信息
        Map msgMap = JSON.parseObject(msg, Map.class);
        //解析消息,获得mediaId
        String mediaId = (String) msgMap.get("mediaId");
        //查询数据库获取媒资文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            //如果不存在,就直接返回
            return;
        }
        MediaFile mediaFile = optional.get();
        //对文件类型进行过滤,只处理avi格式的视频
        String fileType = mediaFile.getFileType();
        if(!fileType.equals("avi")){
            //不是avi格式的视频就改
            mediaFile.setProcessStatus("303004");
            mediaFileRepository.save(mediaFile);
            return;
        }else{
            //需要处理
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }
        /**
         * 使用工具类将avi文件生成mp4文件
         */
        //要处理的视频文件的路径
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        //生成的mp4文件的名称
        String mp4_name = mediaFile.getFileId()+".mp4";
        //生成mp4文件所在的路径
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        //进行处理
        String result = videoUtil.generateMp4();
        if(result==null || !result.equals("success")){
            //处理失败
            //设置状态为处理失败
            mediaFile.setProcessStatus("303003");
            //定义MediaFileProcess_m3u8对象,用于记录错误信息
            MediaFileProcess_m3u8  mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return ;
        }

        /**
         * 将mp4文件转成m3u8文件和ts文件
         */
        String mp4_video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        String m3u8_name = mediaFile.getFileId()+".m3u8";
        String m3u8folder_path = serverPath + mediaFile.getFilePath()+"hls/";
        /**
         * 参数信息:
         *  ffmpeg_path: 文件所在的根目录
         *  video_path:mp4视频文件路径
         *  m3u8_name:生成的m3u8文件的名称
         *  m3u8folder_path: m3u8文件所在的目录
         *
         */
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path,mp4_video_path,m3u8_name,m3u8folder_path);
        //生成m3u8和ts文件
        String tsResult = hlsVideoUtil.generateM3u8();
        if(tsResult == null || !tsResult.equals("success")){
            //失败,
            //设置状态为处理失败
            mediaFile.setProcessStatus("303003");
            //定义MediaFileProcess_m3u8对象,用于记录错误信息
            MediaFileProcess_m3u8  mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(tsResult);
            mediaFileRepository.save(mediaFile);
            return ;
        }
        //处理成功,获得ts文件列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        //记录处理信息(处理成功)
        mediaFile.setProcessStatus("303002");
        //定义mediaFileProcess_m3u8对象保存list
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);

        //保存fileUrl(此url就是视频播放路径的相对地址)
        String fileUrl = mediaFile.getFilePath() + "hls/"+m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        //执行保存
        mediaFileRepository.save(mediaFile);
    }
}
