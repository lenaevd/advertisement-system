package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dto.grade.ChangeGradeRequest;
import com.lenaevd.advertisements.dto.grade.LeaveGradeRequest;
import com.lenaevd.advertisements.model.Grade;

import java.security.Principal;
import java.util.List;

public interface GradeService {
    void createGrade(Principal principal, LeaveGradeRequest request);

    void changeGrade(Principal principal, ChangeGradeRequest request);

    List<Grade> getUsersGrades(int userId);

    float getUsersRating(Integer userId);

    List<Grade> getAllGrades();

    Grade getById(int id);

    void deleteGrade(int id);
}
