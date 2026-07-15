package edu.tongji.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.userservice.entity.Author;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorMapper extends BaseMapper<Author> {
}