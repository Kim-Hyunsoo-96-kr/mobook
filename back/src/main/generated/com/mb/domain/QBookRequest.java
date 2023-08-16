package com.mb.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookRequest is a Querydsl query type for BookRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookRequest extends EntityPathBase<BookRequest> {

    private static final long serialVersionUID = 677691752L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookRequest bookRequest = new QBookRequest("bookRequest");

    public final StringPath bookName = createString("bookName");

    public final StringPath bookPublisher = createString("bookPublisher");

    public final StringPath bookWriter = createString("bookWriter");

    public final StringPath completeDate = createString("completeDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath regDate = createString("regDate");

    public final StringPath status = createString("status");

    public QBookRequest(String variable) {
        this(BookRequest.class, forVariable(variable), INITS);
    }

    public QBookRequest(Path<? extends BookRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookRequest(PathMetadata metadata, PathInits inits) {
        this(BookRequest.class, metadata, inits);
    }

    public QBookRequest(Class<? extends BookRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

