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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    private static final String GRADE_CREATION_RESTRICTED = "User don't have any relatable purchases to leave grade";
    private static final String GRADE_CHANGE_RESTRICTED = "Grade doesn't belong to user";
    private final GradeDao gradeDao;
    private final UserService userService;

    public GradeService(GradeDao gradeDao, UserService userService) {
        this.gradeDao = gradeDao;
        this.userService = userService;
    }

    @Transactional
    public void createGrade(Principal principal, LeaveGradeRequest request) {
        User seller = userService.getUserByIdIfExists(request.userToRateId());
        User customer = userService.getUserFromPrincipal(principal);
        Optional<Grade> gradeOptional = gradeDao.findByUsers(seller.getId(), customer.getId());

        if (gradeOptional.isPresent()) {
            Grade grade = gradeOptional.get();
            grade.setNumber(request.number());
            grade.setCreatedAt(LocalDateTime.now());
            gradeDao.update(grade);
        } else {
            if (hasAnyPurchase(seller, customer)) {
                Grade grade = new Grade(request.number(), seller, customer);
                gradeDao.save(grade);
            } else {
                throw new NoRightsException(GRADE_CREATION_RESTRICTED);
            }
        }
    }

    private boolean hasAnyPurchase(User seller, User customer) {
        for (Sale sale : seller.getSoldItems()) {
            if (sale.getCustomer().equals(customer)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void changeGrade(Principal principal, ChangeGradeRequest request) {
        User customer = userService.getUserFromPrincipal(principal);
        Optional<Grade> gradeOptional = gradeDao.findById(request.id());
        if (gradeOptional.isPresent()) {
            Grade grade = gradeOptional.get();
            if (grade.getCustomer().equals(customer)) {
                grade.setNumber(request.number());
                grade.setCreatedAt(LocalDateTime.now());
                gradeDao.update(grade);
            } else {
                throw new NoRightsException(GRADE_CHANGE_RESTRICTED);
            }
        } else {
            throw new ObjectNotFoundException(request.id(), EntityName.GRADE);
        }
    }

    @Transactional
    public void deleteGrade(int id) {
        gradeDao.deleteById(id);
    }

    public List<Grade> getAllGrades() {
        return gradeDao.findAll();
    }

    public List<Grade> getUsersGrades(int userId) {
        if (userService.getById(userId).isEmpty()) {
            throw new ObjectNotFoundException(userId, EntityName.USER);
        }
        return gradeDao.findBySellerId(userId);
    }
}
