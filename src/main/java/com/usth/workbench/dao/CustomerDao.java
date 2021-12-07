package com.usth.workbench.dao;

import com.usth.workbench.domain.Clue;
import com.usth.workbench.domain.Customer;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Clue clue);
}
