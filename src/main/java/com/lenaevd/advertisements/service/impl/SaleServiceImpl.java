package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.dao.SaleDao;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.SaleService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {
    private final SaleDao saleDao;
    private final UserService userService;

    @Override
    public List<Sale> getUsersSoldItems(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        return saleDao.findSoldItems(user.getId());
    }

    @Override
    public List<Sale> getUsersPurchasedItems(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        return saleDao.findPurchasedItems(user.getId());
    }

    @Override
    public List<Sale> getAllSales() {
        return saleDao.findAll();
    }
}
