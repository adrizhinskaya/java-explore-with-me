package ru.practicum.pagination;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class PaginationHelper<T> {
    private int from;
    private int size;
    private final int startPage = from / size;
    private final double nextPageElPercent = (double) from / size - startPage;
    private final int countOfNextPageEl = (int) Math.ceil(nextPageElPercent * size);
    private final int countOfStartPageEl = size - countOfNextPageEl;

    public List<T> findAllWithPagination(Function<Pageable, Page<T>> getMethod) {
        Page<T> fullPage = getMethod.apply(PageRequest.of(startPage, size));
        List<T> fullStartList = fullPage.getContent();
        if (from % size == 0 || fullStartList.isEmpty()) {
            return fullStartList;
        }

        List<T> startPageEls = fullStartList.subList(fullStartList.size() - countOfStartPageEl, fullStartList.size());

        if (fullPage.hasNext()) {
            fullPage = getMethod.apply(PageRequest.of(startPage + 1, size));
            List<T> nextPageEls = fullPage.getContent().subList(0, countOfNextPageEl);
            return Stream.concat(
                    startPageEls.stream(),
                    nextPageEls.stream()).collect(Collectors.toList());
        }

        return startPageEls;
    }

    public List<T> findAllWithPagination(BiFunction<Set<Long>, Pageable, Page<T>> getMethod, Set<Long> ids) {
        Page<T> fullPage = getMethod.apply(ids, PageRequest.of(startPage, size));
        List<T> fullStartList = fullPage.getContent();
        if (from % size == 0 || fullStartList.isEmpty()) {
            return fullStartList;
        }

        List<T> startPageEls = fullStartList.subList(fullStartList.size() - countOfStartPageEl, fullStartList.size());

        if (fullPage.hasNext()) {
            fullPage = getMethod.apply(ids, PageRequest.of(startPage + 1, size));
            List<T> nextPageEls = fullPage.getContent().subList(0, countOfNextPageEl);
            return Stream.concat(
                    startPageEls.stream(),
                    nextPageEls.stream()).collect(Collectors.toList());
        }

        return startPageEls;
    }
}
