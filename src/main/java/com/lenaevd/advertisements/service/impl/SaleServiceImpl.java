package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.dao.SaleDao;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.SaleService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleDao saleDao;
    private final UserService userService;

    @Override
    public List<Sale> getUsersSoldItems(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<Sale> sales = saleDao.findSoldItems(user.getId());
        LOGGER.info("Found {} sold items for user ID: {}", sales.size(), user.getId());
        return sales;
    }

    @Override
    public List<Sale> getUsersPurchasedItems(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<Sale> purchases = saleDao.findPurchasedItems(user.getId());
        LOGGER.info("Found {} purchased items for user ID: {}", purchases.size(), user.getId());
        return purchases;
    }

    @Override
    public List<Sale> getAllSales() {
        LOGGER.info("All sales requested");
        return saleDao.findAll();
    }
}
