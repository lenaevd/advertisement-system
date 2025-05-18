package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Sale;

import java.util.List;

public interface SaleDao extends Dao<Sale> {
    List<Sale> findSoldItems(int sellerId);

    List<Sale> findPurchasedItems(int customerId);
}
