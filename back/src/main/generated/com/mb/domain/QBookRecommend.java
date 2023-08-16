package com.mb.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookRecommend is a Querydsl query type for BookRecommend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookRecommend extends EntityPathBase<BookRecommend> {

    private static final long serialVersionUID = -1277981355L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookRecommend bookRecommend = new QBookRecommend("bookRecommend");

    public final QBook book;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QBookRecommend(String variable) {
        this(BookRecommend.class, forVariable(variable), INITS);
    }

    public QBookRecommend(Path<? extends BookRecommend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookRecommend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookRecommend(PathMetadata metadata, PathInits inits) {
        this(BookRecommend.class, metadata, inits);
    }

    public QBookRecommend(Class<? extends BookRecommend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new QBook(forProperty("book")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

