package com.plantssoil.webhook.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClonableBeanUtils {
    public static ClonableBean cloneObject(ClonableBean o) {
        try {
            return o == null ? null : (ClonableBean) o.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List getPagedList(List sourceList, int page, int pageSize) {
        List list = new ArrayList();
        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= sourceList.size()) {
            return list;
        }

        int endIndex = startIndex + pageSize;
        if (endIndex < 0) {
            return list;
        }
        if (endIndex > sourceList.size()) {
            endIndex = sourceList.size();
        }

        for (int i = startIndex; i < endIndex; i++) {
            ClonableBean o = (ClonableBean) sourceList.get(i);
            list.add(cloneObject(o));
        }
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List getPagedList(Collection collection, int page, int pageSize) {
        List list = new ArrayList();
        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= collection.size()) {
            return list;
        }

        int endIndex = startIndex + pageSize;
        if (endIndex < 0) {
            return list;
        }
        if (endIndex > collection.size()) {
            endIndex = collection.size();
        }

        int i = 0;
        for (Object obj : collection) {
            if (i >= startIndex && i < endIndex) {
                ClonableBean o = (ClonableBean) obj;
                list.add(cloneObject(o));
            }
            i++;
        }
        return list;
    }
}
