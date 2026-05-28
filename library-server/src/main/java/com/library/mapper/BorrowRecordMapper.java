package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BorrowRecord;
import com.library.vo.BorrowRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    List<BorrowRecordVO> selectBorrowList(@Param("readerId") Long readerId,
                                          @Param("status") Integer status,
                                          @Param("readerNo") String readerNo,
                                          @Param("bookTitle") String bookTitle);
}
