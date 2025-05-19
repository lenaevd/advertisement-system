package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.config.LoggerMessages;
import com.lenaevd.advertisements.dao.GradeDao;
import com.lenaevd.advertisements.dto.grade.ChangeGradeRequest;
import com.lenaevd.advertisements.dto.grade.LeaveGradeRequest;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Grade;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.GradeService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeServiceImpl.class);

    private static final String GRADE_CREATION_RESTRICTED = "User don't have any relatable purchases to leave grade";
    private static final String GRADE_CHANGE_RESTRICTED = "Grade doesn't belong to user";
    private final GradeDao gradeDao;
    private final UserService userService;

    @Override
    @Transactional
    public void createGrade(Principal principal, LeaveGradeRequest request) {
        LOGGER.debug("[{}] executing", "createGrade");
        User seller = userService.getUserById(request.userToRateId());
        User customer = userService.getUserFromPrincipal(principal);
        Optional<Grade> gradeOptional = gradeDao.findByUsers(seller.getId(), customer.getId());

        if (gradeOptional.isPresent()) {
            Grade grade = gradeOptional.get();
            grade.setNumber(request.number());
            grade.setCreatedAt(LocalDateTime.now());
            gradeDao.update(grade);
            LOGGER.info(LoggerMessages.UPDATE_COMPLETED, EntityName.GRADE, grade.getId());
        } else {
            if (hasAnyPurchase(seller, customer)) {
                Grade grade = new Grade(request.number(), seller, customer);
                gradeDao.save(grade);
                LOGGER.info("Created grade");
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

    @Override
    @Transactional
    public void changeGrade(Principal principal, ChangeGradeRequest request) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "changeGrade", EntityName.GRADE, request.id());
        User customer = userService.getUserFromPrincipal(principal);
        Optional<Grade> gradeOptional = gradeDao.findById(request.id());
        if (gradeOptional.isPresent()) {
            Grade grade = gradeOptional.get();
            if (grade.getCustomer().equals(customer)) {
                grade.setNumber(request.number());
                grade.setCreatedAt(LocalDateTime.now());
                gradeDao.update(grade);
                LOGGER.info(LoggerMessages.UPDATE_COMPLETED, EntityName.GRADE, grade.getId());
            } else {
                throw new NoRightsException(GRADE_CHANGE_RESTRICTED);
            }
        } else {
            throw new ObjectNotFoundException(request.id(), EntityName.GRADE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> getUsersGrades(int userId) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getUsersGrades", EntityName.USER, userId);
        userService.getUserById(userId);
        return gradeDao.findBySellerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> getAllGrades() {
        LOGGER.debug(LoggerMessages.ALL_OBJECTS_REQUESTED);
        return gradeDao.findAll();
    }

    @Override
    public Grade getById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getById", EntityName.GRADE, id);
        return gradeDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.GRADE));
    }

    @Override
    @Transactional
    public void deleteGrade(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "deleteGrade", EntityName.GRADE, id);
        Grade grade = getById(id);
        gradeDao.delete(grade);
        LOGGER.info(LoggerMessages.DELETE_COMPLETED, EntityName.GRADE, id);
    }
}
