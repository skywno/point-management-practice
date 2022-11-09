package me.ezra.pm.job.reader;

import com.mysema.commons.lang.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public class ReverseJpaPagingItemReaderBuilder<T> {
    private String name;
    private int pageSize;
    private Function<Pageable, Page<T>> query;
    private Sort sort;

    public ReverseJpaPagingItemReaderBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public ReverseJpaPagingItemReaderBuilder<T> query (Function<Pageable, Page<T>> query) {
        this.query = query;
        return this;
    }

    public ReverseJpaPagingItemReaderBuilder<T> pageSize (int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    public ReverseJpaPagingItemReaderBuilder<T> sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public ReverseJpaPagingItemReader<T> build() {
        Assert.notNull(this.query, "query is required (CustomRepositoryItemReaderBuilder)");
        Assert.notNull(this.name, "reader name is required (CustomRepositoryItemReaderBuilder)");

        ReverseJpaPagingItemReader<T> reader = new ReverseJpaPagingItemReader<>();
        reader.setName(this.name);
        reader.setQuery(this.query);
        reader.setPageSize(this.pageSize);
        reader.setSort(this.sort);
        return reader;
    }


}
