package iuh.fit.models;

import iuh.fit.utils.ErrorMessages;
import iuh.fit.utils.GlobalConstants;
import iuh.fit.utils.RegexChecker;

import java.util.Objects;

public class ServiceCategory {
    private String serviceCategoryID;
    private String serviceCategoryName;

    public ServiceCategory() {
    }

    public ServiceCategory(String serviceCategoryID, String serviceCategoryName) {
        this.serviceCategoryID = serviceCategoryID;
        this.serviceCategoryName = serviceCategoryName;
    }

    public String getServiceCategoryID() {
        return serviceCategoryID;
    }

    public void setServiceCategoryID(String serviceCategoryID) {
        if (serviceCategoryID.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.SERVICE_CATEGORY_INVALID_ID_ISNULL);
        }
        if(!RegexChecker.isValidIDFormat(GlobalConstants.SERVICECATEGORY_PREFIX, serviceCategoryID)){
            throw new IllegalArgumentException(ErrorMessages.SERVICE_CATEGORY_INVALID_ID_FORMAT);
        }
        this.serviceCategoryID = serviceCategoryID;
    }

    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(String serviceCategoryName) {
        if (serviceCategoryName.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.SERVICE_CATEGORY_INVALID_NAME_ISNULL);
        }
        this.serviceCategoryName = serviceCategoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceCategory that = (ServiceCategory) o;
        return Objects.equals(serviceCategoryID, that.serviceCategoryID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceCategoryID);
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "serviceCategoryName='" + serviceCategoryName + '\'' +
                '}';
    }
}