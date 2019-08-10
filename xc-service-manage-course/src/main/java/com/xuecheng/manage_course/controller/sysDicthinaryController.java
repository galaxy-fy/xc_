package com.xuecheng.manage_course.controller;

import com.xuecheng.api.sysDicthinary.sysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SysdictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/dictionary")
public class sysDicthinaryController implements sysDicthinaryControllerApi {
    @Autowired
    SysdictionaryService sysdictionaryService;
    @Override
    @GetMapping("/get/{type}")
    public SysDictionary findDictionary(@PathVariable("type") String type) {
        return sysdictionaryService.findDictionaryByType(type);
    }
}
