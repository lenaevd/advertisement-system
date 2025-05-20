package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.grade.ChangeGradeRequest;
import com.lenaevd.advertisements.dto.grade.GradeDto;
import com.lenaevd.advertisements.dto.grade.LeaveGradeRequest;
import com.lenaevd.advertisements.mapper.GradeMapper;
import com.lenaevd.advertisements.model.Grade;
import com.lenaevd.advertisements.service.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradeControllerTest {
    @Mock
    private GradeService gradeService;
    @Mock
    private GradeMapper gradeMapper;
    @InjectMocks
    private GradeController gradeController;

    private Principal principal;
    private Grade grade;
    private GradeDto gradeDto;
    private List<Grade> grades;
    private List<GradeDto> gradeDtos;
    private int sellerId = 10;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        grade = new Grade();
        grade.setId(1);
        gradeDto = new GradeDto(1, 5, sellerId, 10,
                LocalDateTime.of(2025, 1, 1, 12, 0));
        grades = List.of(grade);
        gradeDtos = List.of(gradeDto);
    }

    @Test
    void leaveGrade() {
        //GIVEN
        LeaveGradeRequest request = new LeaveGradeRequest(2, 10);

        //WHEN
        ResponseEntity<Void> response = gradeController.leaveGrade(principal, request);

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(gradeService).createGrade(principal, request);
    }

    @Test
    void getGradesByUserId() {
        //GIVEN
        when(gradeService.getUsersGrades(sellerId)).thenReturn(grades);
        when(gradeMapper.gradesToGradeDtos(grades)).thenReturn(gradeDtos);

        //WHEN
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByUserId(sellerId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gradeDtos, response.getBody());
    }

    @Test
    void changeGrade() {
        //GIVEN
        ChangeGradeRequest request = new ChangeGradeRequest(1, 4);

        //WHEN
        ResponseEntity<Void> response = gradeController.changeGrade(principal, request);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gradeService).changeGrade(principal, request);
    }

    @Test
    void getGrades() {
        //GIVEN
        when(gradeService.getAllGrades()).thenReturn(grades);
        when(gradeMapper.gradesToGradeDtos(grades)).thenReturn(gradeDtos);

        //WHEN
        ResponseEntity<List<GradeDto>> response = gradeController.getGrades();

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gradeDtos, response.getBody());
    }

    @Test
    void deleteGrade() {
        //WHEN
        ResponseEntity<Void> response = gradeController.deleteGrade(grade.getId());

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gradeService).deleteGrade(grade.getId());
    }
}
