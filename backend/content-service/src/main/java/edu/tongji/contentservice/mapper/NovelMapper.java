package edu.tongji.contentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.contentservice.entity.Novel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NovelMapper extends BaseMapper<Novel> {
}