package iuh.fit.models;

import iuh.fit.utils.ErrorMessages;
import iuh.fit.utils.RegexChecker;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Lớp đại diện cho thông tin thuế.
 * 
 * 
 * @author Chuc
 */
public class Tax {
    private String taxName; // Tên thuế
    private double taxRate; // Tỷ lệ thuế
    private LocalDate dateOfCreation; // Ngày tạo thuế
    private boolean activate; // Trạng thái kích hoạt

    /** 
     * Khởi tạo một đối tượng Tax mới với các giá trị mặc định.
     */
    public Tax() {
    }

    /**
     * Khởi tạo một đối tượng Tax với các tham số cụ thể.
     * 
     * @param taxName Tên thuế
     * @param taxRate Tỷ lệ thuế
     * @param dateOfCreation Ngày tạo thuế
     * @param activate Trạng thái kích hoạt
     */
    public Tax(String taxName, double taxRate, LocalDate dateOfCreation, boolean activate) {
        this.setTaxName(taxName);
        this.setTaxRate(taxRate);
        this.setDateOfCreation(dateOfCreation);
        this.setActivate(activate);
    }

    /**
     * Lấy tên thuế.
     * 
     * @return Tên thuế
     */
    public String getTaxName() {
        return taxName;
    }

    /**
     * Lấy tỷ lệ thuế.
     * 
     * @return Tỷ lệ thuế
     */
    public double getTaxRate() {
        return taxRate;
    }

    /**
     * Lấy ngày tạo thuế.
     * 
     * @return Ngày tạo thuế
     */
    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Kiểm tra xem thuế có được kích hoạt hay không.
     * 
     * @return true nếu thuế được kích hoạt, false nếu không
     */
    public boolean isActivate() {
        return activate;
    }

    /**
     * Thiết lập tên thuế.
     * 
     * @param taxName Tên thuế
     * @throws IllegalArgumentException nếu tên thuế không hợp lệ
     */
    public void setTaxName(String taxName) {
        if (!RegexChecker.isValidTaxName(taxName)) {
            throw new IllegalArgumentException(ErrorMessages.TAX_INVALID_TAXNAME);
        }
        this.taxName = taxName;
    }

    /**
     * Thiết lập tỷ lệ thuế.
     * 
     * @param taxRate Tỷ lệ thuế
     * @throws IllegalArgumentException nếu tỷ lệ thuế không hợp lệ
     */
    public void setTaxRate(double taxRate) {
        if (taxRate <= 0)
            throw new IllegalArgumentException(ErrorMessages.TAX_INVALID_TAXRATE);

        this.taxRate = taxRate;
    }

    /**
     * Thiết lập ngày tạo thuế.
     * 
     * @param dateOfCreation Ngày tạo thuế
     * @throws IllegalArgumentException nếu ngày tạo thuế không hợp lệ
     */
    public void setDateOfCreation(LocalDate dateOfCreation) {
        if (!RegexChecker.isValidTaxDateOfCreation(dateOfCreation)) {
            throw new IllegalArgumentException(ErrorMessages.TAX_INVALID_TAXDATEOFCREATION);
        }
        this.dateOfCreation = dateOfCreation;
    }

    /**
     * Thiết lập trạng thái kích hoạt cho thuế.
     * 
     * @param activate Trạng thái kích hoạt
     */
    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tax tax)) return false;
        return Objects.equals(taxName, tax.taxName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taxName);
    }

    @Override
    public String toString() {
        return "Tax{" + "taxName=" + taxName + ", taxRate=" + taxRate + ", dateOfCreation=" + dateOfCreation + ", activate=" + activate + '}';
    }
}