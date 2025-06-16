package com.empire.employeefinder.utils;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class DtoUtil {

    public static <T> List<T> paginate(List<T> list, int pageSize, int pageNumber) {
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return fromIndex > list.size() ? List.of() : list.subList(fromIndex, toIndex);
    }

    public static <T> List<T> sort(List<T> list, String sortField, String sortDirection, Map<String, Comparator<T>> fieldComparators) {
        Comparator<T> comparator = fieldComparators.getOrDefault(sortField, Comparator.comparing(Object::hashCode));
        return list.stream()
                .sorted("desc".equalsIgnoreCase(sortDirection) ? comparator.reversed() : comparator)
                .toList();
    }
}
