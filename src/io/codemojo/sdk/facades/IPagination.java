package io.codemojo.sdk.facades;

import io.codemojo.sdk.models.PaginatedTransaction;

/**
 * Created by shoaib on 17/07/16.
 */
public interface IPagination<T> {
    PaginatedTransaction<T> next();
    PaginatedTransaction<T> prev();
}
