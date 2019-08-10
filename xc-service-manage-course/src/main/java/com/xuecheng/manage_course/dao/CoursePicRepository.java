package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePicRepository extends JpaRepository<CoursePic,String>{

    //自定义方法查询数据库,删除数据 成功返回1,失败返回0
    long deleteByCourseid(String courseId);
}
