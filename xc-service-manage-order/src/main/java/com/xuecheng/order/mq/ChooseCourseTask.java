package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //@Scheduled(fixedRate = 5000) //上次执行开始时间后5秒执行
    //@Scheduled(fixedDelay = 5000)  //上次执行完毕后5秒执行
    //@Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
   /* @Scheduled(cron = "0/3 * * * * *")//每隔3秒执行一次
    public void task1() {
        LOGGER.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务1结束===============");
    }

    @Scheduled(fixedRate = 3000) //上次执行开始时间后5秒执行
    public void task2(){
        LOGGER.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务2结束===============");
    }*/

    //每隔1分钟扫描消息表，向mq发送消息
    //@Scheduled(fixedDelay = 60000)
    @Scheduled(cron = "0/3 * * * * *")
    public void sendChoosecourseTask(){
        //取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);
        System.out.println(taskList);
        //定时发送消息
        //遍历taskList,准备发送该消息
        for (XcTask xcTask : taskList) {
            //调用乐观锁方法校验任务是否可以执行
            if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                //调用service的方法,发送消息
                taskService.publish(xcTask,xcTask.getMqExchange(),xcTask.getMqRoutingkey());
            }
        }
    }

    /**
     * 完成选课,删除选课表中的记录,添加到历史选课中
     */
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        if(xcTask!=null && StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }
}
