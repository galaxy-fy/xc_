package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    XcUserRepository xcUserRepository;

    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    XcMenuMapper xcMenuMapper;

    //根据账号查询用户信息
    public XcUserExt getUserExt(String username){
        //调用方法,首先根据username查询出user的信息
        XcUser xcUser = this.findXcUserByUsername(username);
        //对拿到的结果进行判断
        if(xcUser==null){
            return null;
        }
        //获取用户的id
        String userId = xcUser.getId();

        //根据用户的id查询用户的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);

        //拿到用户id然后去查询用户公司的id
        XcCompanyUser xcCompanyUser = this.findXcCompanyUserByUserId(userId);
        //首先定义一个公司的id.因为不一定每一个人都有id(学生是没有id的)
        String companyId = null;
        //然后取出公司的id
        if(xcCompanyUser!=null){
            //赋值
            companyId = xcCompanyUser.getCompanyId();
        }
        //创建返回值对象
        XcUserExt xcUserExt = new XcUserExt();
        //拷贝xcUser信息到xcUserExt中
        BeanUtils.copyProperties(xcUser,xcUserExt);
        //给公司id赋值
        xcUserExt.setCompanyId(companyId);
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }
    //根据用户id查询用户公司相关信息
    private XcCompanyUser findXcCompanyUserByUserId(String userId) {
        return xcCompanyUserRepository.findByUserId(userId);
    }

    //根据username查询用户的相关信息
    private XcUser findXcUserByUsername(String username) {
       return xcUserRepository.findByUsername(username);
    }
}
