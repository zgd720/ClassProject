package com.example.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.academic.entity.CourseScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface CourseScoreMapper extends BaseMapper<CourseScore> {

    @Select("SELECT semester, ROUND(AVG(gpa), 2) as avg_gpa FROM course_score WHERE deleted = 0 GROUP BY semester ORDER BY semester")
    List<Map<String, Object>> selectAvgGpaBySemester();

    @Select("SELECT s.student_id, AVG(cs.gpa) as avg_gpa FROM course_score cs " +
            "JOIN student s ON cs.student_id = s.student_id WHERE cs.deleted = 0 AND s.deleted = 0 " +
            "GROUP BY s.student_id")
    List<Map<String, Object>> selectAvgGpaByStudent();
}
