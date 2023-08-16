package com.mb.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookLog is a Querydsl query type for BookLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookLog extends EntityPathBase<BookLog> {

    private static final long serialVersionUID = 1501218461L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookLog bookLog = new QBookLog("bookLog");

    public final QBook book;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath regDate = createString("regDate");

    public final StringPath returnDate = createString("returnDate");

    public final StringPath status = createString("status");

    public QBookLog(String variable) {
        this(BookLog.class, forVariable(variable), INITS);
    }

    public QBookLog(Path<? extends BookLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookLog(PathMetadata metadata, PathInits inits) {
        this(BookLog.class, metadata, inits);
    }

    public QBookLog(Class<? extends BookLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new QBook(forProperty("book")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

