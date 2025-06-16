package com.empire.employeefinder.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractSearchUtil<T> {

    protected abstract Map<String, Function<String, Specification<T>>> getFieldSpecs();

    protected abstract String mapSortField(String field);

    protected abstract void validateFieldAndParameter(String searchField, String parameter);

    protected Specification<T> buildSearchSpecification(String searchField, String parameter) {
        return Optional.ofNullable(parameter)
                .filter(p -> !"default".equals(searchField) && !p.isBlank())
                .map(p -> getFieldSpecs().getOrDefault(searchField, val -> null).apply(p))
                .orElse((root, query, cb) -> cb.conjunction());
    }

    protected PageRequest createPageRequest(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String resolvedSortField = mapSortField(sortField);
        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, resolvedSortField));
    }
}
