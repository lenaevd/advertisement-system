package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.GradeDao;
import com.lenaevd.advertisements.dto.grade.ChangeGradeRequest;
import com.lenaevd.advertisements.dto.grade.LeaveGradeRequest;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Grade;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {
    @Mock
    private GradeDao gradeDao;
    @Mock
    private UserService userService;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Principal principal;
    private Grade grade;
    private User user;
    private User seller;
    private int id = 10;
    private int sellerId = 2;
    private int userId = 22;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        seller = new User();
        seller.setId(sellerId);

        grade = new Grade(3, seller, user);
        grade.setId(id);

        principal = mock(Principal.class);
    }

    @Test
    void createGrade() {
        //GIVEN
        Sale sale = new Sale();
        sale.setCustomer(user);
        seller.setSoldItems(List.of(sale));
        LeaveGradeRequest request = new LeaveGradeRequest(5, sellerId);

        when(userService.getUserById(request.userToRateId())).thenReturn(seller);
        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(gradeDao.findByUsers(sellerId, userId)).thenReturn(Optional.empty());

        //WHEN
        gradeService.createGrade(principal, request);

        //THEN
        verify(gradeDao).save(any(Grade.class));
    }

    @Test
    void createGradeAndGradeExists() {
        //GIVEN
        LeaveGradeRequest request = new LeaveGradeRequest(5, sellerId);

        when(userService.getUserById(request.userToRateId())).thenReturn(seller);
        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(gradeDao.findByUsers(seller.getId(), user.getId())).thenReturn(Optional.of(grade));

        //WHEN
        gradeService.createGrade(principal, request);

        //THEN
        assertEquals(5, grade.getNumber());
        verify(gradeDao).update(grade);
        verify(gradeDao, never()).save(any(Grade.class));
    }

    @Test
    void createGradeAndNoPurchaseExists() {
        //GIVEN
        seller.setSoldItems(List.of());
        LeaveGradeRequest request = new LeaveGradeRequest(5, sellerId);

        when(userService.getUserById(request.userToRateId())).thenReturn(seller);
        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(gradeDao.findByUsers(seller.getId(), user.getId())).thenReturn(Optional.empty());

        //WHEN | THEN
        NoRightsException exception = assertThrows(NoRightsException.class,
                () -> gradeService.createGrade(principal, request));
        assertEquals("User don't have any relatable purchases to leave grade", exception.getMessage());
        verify(gradeDao, never()).update(any(Grade.class));
    }

    @Test
    void changeGrade() {
        //GIVEN
        ChangeGradeRequest request = new ChangeGradeRequest(id, 5);
        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(gradeDao.findById(id)).thenReturn(Optional.of(grade));

        //WHEN
        gradeService.changeGrade(principal, request);

        //THEN
        assertEquals(5, grade.getNumber());
        verify(gradeDao).update(grade);
        verify(gradeDao, never()).save(any(Grade.class));
    }

    @Test
    void changeGradeAndUserIsNotOwner() {
        //GIVEN
        User otherUser = new User();
        otherUser.setId(100);
        grade.setCustomer(otherUser);
        ChangeGradeRequest request = new ChangeGradeRequest(id, 5);

        when(userService.getUserFromPrincipal(principal)).thenReturn(user);
        when(gradeDao.findById(id)).thenReturn(Optional.of(grade));

        // WHEN | THEN
        NoRightsException exception = assertThrows(NoRightsException.class,
                () -> gradeService.changeGrade(principal, request));
        assertEquals("Grade doesn't belong to user", exception.getMessage());
        verify(gradeDao, never()).update(any(Grade.class));
    }

    @Test
    void changeGradeAndGradeDoesNotExistThrowsException() {
        // GIVEN
        ChangeGradeRequest request = new ChangeGradeRequest(id, 5);

        when(gradeDao.findById(id)).thenReturn(Optional.empty());

        // WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> gradeService.changeGrade(principal, request));
        assertEquals(new ObjectNotFoundException(request.id(), EntityName.GRADE).getMessage(), exception.getMessage());
        verify(gradeDao, never()).update(any(Grade.class));
    }

    @Test
    void getById() {
        //GIVEN
        when(gradeDao.findById(id)).thenReturn(Optional.of(grade));

        //WHEN
        Grade resultGrade = gradeService.getById(id);

        //THEN
        assertEquals(grade, resultGrade);
    }

    @Test
    void getByIdAndCommentNotFoundThrowsException() {
        //GIVEN
        when(gradeDao.findById(id)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> gradeService.getById(id));
        assertEquals((new ObjectNotFoundException(id, EntityName.GRADE)).getMessage(), exception.getMessage());
    }

    @Test
    void deleteGrade() {
        //GIVEN
        when(gradeDao.findById(id)).thenReturn(Optional.of(grade));

        //WHEN
        gradeService.deleteGrade(id);

        //THEN
        verify(gradeDao).delete(grade);
    }
}
