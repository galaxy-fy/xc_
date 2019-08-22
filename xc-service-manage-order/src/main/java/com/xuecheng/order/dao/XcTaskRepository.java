package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface XcTaskRepository extends JpaRepository<XcTask,String>{
    //取出指定时间之前的记录
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable,Date updateTime);

    //更新update_time
    @Modifying
    @Query("update XcTask set updateTime = :updateTime where id = :id")
    int updateTaskTime(@Param(value = "id") String id , @Param(value = "updateTime")  Date updateTime);

    //使用乐观锁方式校验任务id和版本号是否匹配，匹配则版本号加1
    @Modifying
    @Query("update XcTask set version = :version+1 where id = :id and version = :version")
    int updateTaskVersion(@Param(value = "id") String id ,@Param(value = "version")  int version);
}
