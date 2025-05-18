package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.model.Sale;

import java.security.Principal;
import java.util.List;

public interface SaleService {
    List<Sale> getUsersSoldItems(Principal principal);

    List<Sale> getUsersPurchasedItems(Principal principal);

    List<Sale> getAllSales();
}
