package edu.tongji.contentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.contentservice.entity.Likes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikesMapper extends BaseMapper<Likes> {
}