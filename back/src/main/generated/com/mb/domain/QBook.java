package com.mb.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = 227262247L;

    public static final QBook book = new QBook("book");

    public final NumberPath<Long> bookId = createNumber("bookId", Long.class);

    public final StringPath bookName = createString("bookName");

    public final StringPath bookNumber = createString("bookNumber");

    public final BooleanPath isAble = createBoolean("isAble");

    public final NumberPath<Integer> recommend = createNumber("recommend", Integer.class);

    public final StringPath regDate = createString("regDate");

    public final NumberPath<Long> rentalMemberId = createNumber("rentalMemberId", Long.class);

    public QBook(String variable) {
        super(Book.class, forVariable(variable));
    }

    public QBook(Path<? extends Book> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBook(PathMetadata metadata) {
        super(Book.class, metadata);
    }

}

