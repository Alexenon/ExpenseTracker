package com.example.application.model;

import java.util.Date;

public interface ExpenseDTO {
    long getId();
    String getName();
    double getAmount();
    String getCategory();
    String getDescription();
    String getTimestamp();
    Date getDate();
}
