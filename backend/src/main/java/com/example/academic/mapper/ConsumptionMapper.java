package com.example.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.academic.entity.Consumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConsumptionMapper extends BaseMapper<Consumption> {

    @Select("SELECT HOUR(consume_time) as hour, SUM(amount) as total FROM consumption WHERE deleted = 0 GROUP BY HOUR(consume_time) ORDER BY hour")
    List<Map<String, Object>> selectAmountByHour();
}
