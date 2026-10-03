package com.example.backend.presentation.dto.invoice;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class InvoiceRequest {

    @NotNull(message = "Hợp đồng không được để trống")
    private Long contractId;

    @NotNull(message = "Tháng không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1 đến 12")
    @Max(value = 12, message = "Tháng phải từ 1 đến 12")
    private Integer month;

    @NotNull(message = "Năm không được để trống")
    private Integer year;

    @PositiveOrZero(message = "Số điện không được âm")
    private Integer electricityNumber;

    @PositiveOrZero(message = "Số nước không được âm")
    private Integer waterNumber;

    public InvoiceRequest() {
    }

    public InvoiceRequest(Long contractId, Integer month, Integer year, Integer electricityNumber, Integer waterNumber) {
        this.contractId = contractId;
        this.month = month;
        this.year = year;
        this.electricityNumber = electricityNumber;
        this.waterNumber = waterNumber;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public Integer getElectricityNumber() {
        return electricityNumber;
    }

    public void setElectricityNumber(Integer electricityNumber) {
        this.electricityNumber = electricityNumber;
    }

    public Integer getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(Integer waterNumber) {
        this.waterNumber = waterNumber;
    }
}
