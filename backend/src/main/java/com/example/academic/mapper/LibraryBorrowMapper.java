package com.example.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.academic.entity.LibraryBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface LibraryBorrowMapper extends BaseMapper<LibraryBorrow> {

    @Select("SELECT category, COUNT(*) as count FROM library_borrow WHERE deleted = 0 GROUP BY category")
    List<Map<String, Object>> selectCountByCategory();
}
