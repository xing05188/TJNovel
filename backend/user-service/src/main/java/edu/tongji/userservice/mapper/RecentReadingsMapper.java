package edu.tongji.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.userservice.entity.RecentReadings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecentReadingsMapper extends BaseMapper<RecentReadings> {
}