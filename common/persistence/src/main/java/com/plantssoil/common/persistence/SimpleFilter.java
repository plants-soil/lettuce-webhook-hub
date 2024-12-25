package com.plantssoil.common.persistence;

import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;

public class SimpleFilter {
    private String attributeName;
    private FilterOperator operator;
    private Object filterValue;

    public SimpleFilter(String attributeName, FilterOperator operator, Object filterValue) {
        super();
        this.attributeName = attributeName;
        this.operator = operator;
        this.filterValue = filterValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public Object getFilterValue() {
        return filterValue;
    }
}