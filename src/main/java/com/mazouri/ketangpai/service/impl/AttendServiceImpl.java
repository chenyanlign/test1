package com.mazouri.ketangpai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mazouri.ketangpai.entity.Attend;
import com.mazouri.ketangpai.entity.AttendUser;
import com.mazouri.ketangpai.entity.vo.AttendVO;
import com.mazouri.ketangpai.mapper.AttendMapper;
import com.mazouri.ketangpai.service.AttendService;
import com.mazouri.ketangpai.service.AttendUserService;
import com.mazouri.ketangpai.service.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mazouri
 * @since 2021-06-20
 */
@Service
public class AttendServiceImpl extends ServiceImpl<AttendMapper, Attend> implements AttendService {
    @Autowired
    private CourseUserService courseUserService;

    @Autowired
    private AttendUserService attendUserService;

    @Override
    public List<Attend> getAllAttendByCourseId(String courseId) {
        List<Attend> attends = baseMapper.selectList(new QueryWrapper<Attend>().eq("course_id", courseId));
        Integer total = courseUserService.getHomeworkNum(courseId);
        attends.forEach(attend -> {
            int attendNum = attendUserService.count(new QueryWrapper<AttendUser>().eq("attend_id", attend.getId()));
            attend.setPercentage(((attendNum * 1.0) / total) * 100.0);
        });
        return attends;
    }

    @Override
    public List<AttendVO> getUserAllAttend(String userId, String courseId) {
        List<AttendVO> attends = baseMapper.getAllAttendByCourseId(courseId);
        attends.forEach(attend -> {
            AttendUser att = attendUserService.getOne(new QueryWrapper<AttendUser>().eq("attend_id", attend.getId()).eq("user_id", userId));
            if (att!=null){
                attend.setUserId(att.getUserId()).setStatus("正常").setAttendTime(att.getCreateTime());
            }else {
                attend.setStatus("旷课");
            }
        });

        return attends;
    }


}
