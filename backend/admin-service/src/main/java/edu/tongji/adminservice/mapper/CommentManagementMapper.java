package edu.tongji.adminservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.adminservice.entity.CommentManagement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentManagementMapper extends BaseMapper<CommentManagement> {
}