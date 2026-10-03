package com.example.backend.presentation.dto.stats;

import java.math.BigDecimal;

public class RevenueStatResponse {

    private BigDecimal totalRevenue;
    private Integer month;
    private Integer year;

    public RevenueStatResponse() {
    }

    public RevenueStatResponse(BigDecimal totalRevenue, Integer month, Integer year) {
        this.totalRevenue = totalRevenue;
        this.month = month;
        this.year = year;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
