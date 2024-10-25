package ru.practicum.pagination;

import jakarta.annotation.Nullable;
import org.springframework.cglib.core.internal.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.QEventEntity;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationHelper<T> {
    private final int from;
    private final int size;
    private final int startPage;
    private final double nextPageElPercent;
    private final int countOfNextPageEl;
    private final int countOfStartPageEl;

    public PaginationHelper(int from, int size) {
        this.from = from;
        this.size = size;
        startPage = from / size;
        nextPageElPercent = (double) from / size - startPage;
        countOfNextPageEl = (int) Math.ceil(nextPageElPercent * size);
        countOfStartPageEl = size - countOfNextPageEl;
    }

    public PageRequest getPageRequestForFirstPage(Sort sort) {
        return sort != null ?
                PageRequest.of(from / size, size, sort) :
                PageRequest.of(from / size, size);
    }

    public PageRequest getPageRequestForNextPage(Sort sort) {
        return sort != null ?
                PageRequest.of(from / size + 1, size, sort) :
                PageRequest.of(from / size + 1, size);
    }

    public List<T> mergePages(Page<T> firstPage, @Nullable Page<T> nextPage) {
        List<T> fullStartList = firstPage.getContent();
        if (from % size == 0 || fullStartList.isEmpty()) {
            return fullStartList;
        }

        List<T> startPageEls = fullStartList.subList(fullStartList.size() - countOfStartPageEl, fullStartList.size());

        if (nextPage != null) {
            List<T> nextPageEls = nextPage.getContent().subList(0, countOfNextPageEl);
            return Stream.concat(
                    startPageEls.stream(),
                    nextPageEls.stream()).collect(Collectors.toList());
        }

        return startPageEls;
    }

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

    public List<T> findAllWithPagination(BiFunction<Long, Pageable, Page<T>> getMethod, Long id) {
        Page<T> fullPage = getMethod.apply(id, PageRequest.of(startPage, size));
        List<T> fullStartList = fullPage.getContent();
        if (from % size == 0 || fullStartList.isEmpty()) {
            return fullStartList;
        }

        List<T> startPageEls = fullStartList.subList(fullStartList.size() - countOfStartPageEl, fullStartList.size());

        if (fullPage.hasNext()) {
            fullPage = getMethod.apply(id, PageRequest.of(startPage + 1, size));
            List<T> nextPageEls = fullPage.getContent().subList(0, countOfNextPageEl);
            return Stream.concat(
                    startPageEls.stream(),
                    nextPageEls.stream()).collect(Collectors.toList());
        }

        return startPageEls;
    }
}
