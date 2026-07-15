package edu.tongji.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.userservice.entity.Manager;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ManagerMapper extends BaseMapper<Manager> {
}