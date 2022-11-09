package me.ezra.pm.job.reader;

import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.function.Function;

public class ReverseJpaPagingItemReader<T> extends ItemStreamSupport implements ItemStreamReader<T> {

    private static final int DEFAULT_PAGE_SIZE = 100;

    private int page = 0;

    private int totalPage = 0;

    private List<T> readRows = new ArrayList<>();

    private int pageSize = DEFAULT_PAGE_SIZE;

    private Function<Pageable, Page<T>> query;

    private Sort sort = Sort.unsorted();

    ReverseJpaPagingItemReader() {
    }

    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize > 0) ? pageSize : DEFAULT_PAGE_SIZE;
    }

    public void setQuery(Function<Pageable, Page<T>> query) {
        this.query = query;
    }

    public void setSort(Sort sort) {
        if (!Objects.isNull(sort)) {
            Iterator<Sort.Order> orderIterator = sort.iterator();
            final List<Sort.Order> reverseOrders = new LinkedList<>();
            while (orderIterator.hasNext()) {
                Sort.Order prev = orderIterator.next();
                reverseOrders.add(new Sort.Order(prev.getDirection().isAscending() ?
                        Sort.Direction.DESC : Sort.Direction.ASC, prev.getProperty()));
            }
            this.sort = Sort.by(reverseOrders);
        }
    }

    @BeforeStep
    public void beforeStep() {
        totalPage = getTargetData(0).getTotalPages();
        page = totalPage - 1;
    }

    @SuppressWarnings("unused")
    @BeforeRead
    public void beforeRead() {
        if (page < 0)
            return;
        if (readRows.isEmpty())
            readRows = new ArrayList<>(getTargetData(page).getContent());
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException,
            NonTransientResourceException {
        return readRows.isEmpty() ? null : readRows.remove(readRows.size() - 1);
    }

    @SuppressWarnings("unused")
    @AfterRead
    public void afterRead() {
        if (readRows.isEmpty()) {
            this.page --;
        }
    }

    private Page<T> getTargetData(int readPage) {
        return Objects.isNull(query) ? Page.empty() : query.apply(PageRequest.of(readPage, pageSize, sort));
    }



}
