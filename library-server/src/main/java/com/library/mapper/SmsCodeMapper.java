package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {
    @Select("SELECT * FROM sms_code WHERE phone = #{phone} AND code = #{code} AND expire_time > NOW() AND used = 0 ORDER BY create_time DESC LIMIT 1")
    SmsCode selectValid(@Param("phone") String phone, @Param("code") String code);
}
