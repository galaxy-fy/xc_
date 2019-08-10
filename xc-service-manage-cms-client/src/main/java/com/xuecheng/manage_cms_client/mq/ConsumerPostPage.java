package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.pageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 监听消息队列,收到消息后,调用pageService将页面从GridFs中下载到服务器
 */
@Component
public class ConsumerPostPage {
    //根据日志来排错
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    pageService pageService;


    //监听, 指定队列的名称,从配置文件里获取
    @RabbitListener(queues="${xuecheng.mq.queue}")
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId = (String)map.get("pageId");
        //校验页面是否合法
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            LOGGER.error("receive postPage msg,cmsPage is null,pageId:{}"+pageId);
            return;
        }
        //调用service方法将页面从GridFs中下载到服务器
        pageService.savePageToServerPath(pageId);
    }
}
