package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReaderMapper extends BaseMapper<Reader> {
    @Select("SELECT COUNT(*) FROM borrow_record WHERE reader_id = #{readerId} AND status IN (0, 2)")
    int countCurrentBorrows(@Param("readerId") Long readerId);

    @Select("SELECT COUNT(*) FROM borrow_record WHERE reader_id = #{readerId} AND status = 2")
    int countOverdue(@Param("readerId") Long readerId);
}
