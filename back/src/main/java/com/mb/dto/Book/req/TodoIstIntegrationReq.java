package com.mb.dto.Book.req;

import lombok.Getter;

@Getter
public class TodoIstIntegrationReq {
    private String eventName;
    private int userId;
    private EventData eventData;
    private Initiator initiator;
    private String version;


    public static class EventData {
        private int addedByUid;
        private Integer assignedByUid;
        private int checked;
        private int childOrder;
        private int collapsed;
        private String content;

    }

    public static class Initiator {
        private String email;
        private String fullName;
        private int id;
        private String imageId;
        private boolean isPremium;

    }
}
