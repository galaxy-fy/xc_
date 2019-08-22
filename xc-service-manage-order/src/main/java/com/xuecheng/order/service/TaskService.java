package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime, int size) {
        //设置分页参数，取出前n 条记录
        Pageable pageable = new PageRequest(0, size);
        Page<XcTask> byUpdateTimeBefore = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return byUpdateTimeBefore.getContent();
    }
    /**
     *  //发送消息
     * @param xcTask 任务对象
     * @param ex 交换机id
     * @param routingKey
     */
    @Transactional
    public void publish(XcTask xcTask,String ex,String routingKey){
        //查询数据库,找到要发送消息的对象
        Optional<XcTask> xcTaskOptional = xcTaskRepository.findById(xcTask.getId());
        if(xcTaskOptional.isPresent()){
            //发送消息
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            XcTask one = xcTaskOptional.get();
            //更新任务时间为当前时间
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    /**
     * 使用乐观锁来获取任务
     * @param id
     * @param version
     * @return
     */
    @Transactional
    public int getTask(String id , int version){
        //通过乐观锁的方式来更新数据表，如果结果大于0说明取到任务
        int i = xcTaskRepository.updateTaskVersion(id, version);
        return i;
    }

    /**
     * 完成任务
     */
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> byId = xcTaskRepository.findById(taskId);
        if(byId.isPresent()){
            //如果有这个任务,那么就删除这个任务,添加到历史任务列表中
            XcTask xcTask = byId.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            //添加历史任务列表
            xcTaskHisRepository.save(xcTaskHis);
            //删除任务列表
            xcTaskRepository.delete(xcTask);
        }
    }
}
