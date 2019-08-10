package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理页面
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;

    /**
     * 查询课程节点
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }


    /**
     * 主页显示的课程信息
     */
    @GetMapping("/coursebase/list/{page}/{size}")
    public ResponseResult findCourseList(@PathVariable("size") Integer size,@PathVariable("page") Integer page, CourseListRequest courseListRequest){
            return courseService.findCourseList(page,size,courseListRequest);
    }

    /**
     * 新增课程
     */
    @Override
    @PostMapping("/coursebase/add")
    //想把前台的json对象转为字符串需要在参数位置填写RequestBody注解
    public AddCourseResult addCourse(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    /**
     * 根据id查询course信息
     */
    @Override
    @GetMapping("/findcourse/{id}")
    public CourseBase findById(@PathVariable("id") String id){
        return courseService.findById(id);
    }

    /**
     * 更新课程信息
     */
    @Override
    @PostMapping("/updatecourse/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase){
        return courseService.update(id,courseBase);
    }

    /**
     * 查询课程营销信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findmarket/{id}")
    public CourseMarket getCourseMarketById(@PathVariable("id") String id) {
        return courseService.getCourseMarketById(id);
    }

    /**
     * 更新课程营销信息
     * @param id
     * @param courseMarket
     * @return
     */
    @Override
    @PostMapping("/updatemarket/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id,@RequestBody CourseMarket courseMarket) {

        return courseService.updateCourseMarket(id,courseMarket);
    }

    /**
     * 添加课程图片
     * @param courseId
     * @param pic
     * @return
     */
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId,pic);
    }

    /**
     * 查询图片,
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findPicByCourseId(courseId);
    }

    /**
     * 删除图片
     * @param courseId
     * @return
     */
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deletePic(@RequestParam("courseId") String courseId) {
        return courseService.deletePic(courseId);
    }

    /**
     * 查询课程信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCoruseView(id);
    }

    /**
     * 课程预览
     * @param id
     * @return
     */
    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    /**
     * 课程发布
     * @param id
     * @return
     */
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }


}
