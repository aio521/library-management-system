package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Update("UPDATE book SET total_stock = total_stock + 1, available_stock = available_stock + 1 WHERE id = #{bookId}")
    int incrementStock(@Param("bookId") Long bookId);

    @Update("UPDATE book SET total_stock = total_stock - 1, available_stock = available_stock - 1 WHERE id = #{bookId} AND available_stock > 0")
    int decrementStock(@Param("bookId") Long bookId);

    @Update("UPDATE book SET available_stock = available_stock - 1 WHERE id = #{bookId} AND available_stock > 0")
    int decrementAvailable(@Param("bookId") Long bookId);

    @Update("UPDATE book SET available_stock = available_stock + 1 WHERE id = #{bookId}")
    int incrementAvailable(@Param("bookId") Long bookId);
}
