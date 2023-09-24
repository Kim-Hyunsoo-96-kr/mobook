package com.mb.util;

public class WebHookUtil {
    public static String bookAddHook(int bookQuantity){
        String body = "{\n" +
                "      'cardsV2': [{\n" +
                "        'cardId': 'createCardMessage',\n" +
                "        'card': {\n" +
                "          'header': {\n" +
                "            'title': '책 추가 알림',\n" +
                "            'subtitle': '수신자 : 모두',\n" +
                "            'imageUrl': 'http://mobook.mobility42.io/img/bookAddIcon.png',\n" +
                "            'imageType': 'CIRCLE'\n" +
                "          },\n" +
                "          'sections': [\n" +
                "            {\n" +
                "              'widgets': [\n" +
                "                  {\n" +
                "                       'textParagraph': {\n" +
                "                            'text': '새로운 책 <b>"+ bookQuantity +"권</b> 이 추가되었습니다.' \n" +
                "                        }\n" +
                "                  },\n" +
                "                {\n" +
                "                  'buttonList': {\n" +
                "                    'buttons': [\n" +
                "                      {\n" +
                "                        'text': '웹으로 이동',\n" +
                "                        'onClick': {\n" +
                "                          'openLink': {\n" +
                "                            'url': 'http://mobook.mobility42.io'\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }]\n" +
                "    }";
        return body;
    }

    public static String bookRequestHook(String name, String bookName, String bookLink) {
        String body = "{\n" +
                "      \"cardsV2\": [{\n" +
                "        \"cardId\": \"createCardMessage\",\n" +
                "        \"card\": {\n" +
                "          \"header\": {\n" +
                "            \"title\": \"책 요청 알림\",\n" +
                "            \"subtitle\": \"수신자 : 모두\",\n" +
                "            \"imageUrl\": \"http://mobook.mobility42.io/img/bookRequestIcon.png\",\n" +
                "            \"imageType\": \"CIRCLE\"\n" +
                "          },\n" +
                "          \"sections\": [\n" +
                "            {\n" +
                "              \"widgets\": [\n" +
                "                  {\n" +
                "                       \"textParagraph\": {\n" +
                "                            \"text\": \"<b>"+ name +"</b>님이 <b>"+ bookName +"</b> 책을 요청했습니다.\"\n" +
                "                        }\n" +
                "                  },\n" +
                "                {\n" +
                "                  \"buttonList\": {\n" +
                "                    \"buttons\": [\n" +
                "                      {\n" +
                "                        \"text\": \"요청 링크 열기\",\n" +
                "                        \"onClick\": {\n" +
                "                          \"openLink\": {\n" +
                "                            \"url\": \""+ bookLink +"\"\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ],\n" +
                "                    \"buttons\": [\n" +
                "                      {\n" +
                "                        \"text\": \"MOBOOK에서 보기\",\n" +
                "                        \"onClick\": {\n" +
                "                          \"openLink\": {\n" +
                "                            \"url\": \"http://mobook.mobility42.io\"\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }]\n" +
                "    }";
        return body;
    }
    public static String noticeAddHook(Long noticeId){
        String body = "{\n" +
                "      \"cardsV2\": [{\n" +
                "        \"cardId\": \"createCardMessage\",\n" +
                "        \"card\": {\n" +
                "          \"header\": {\n" +
                "            \"title\": \"공지사항 알림\",\n" +
                "            \"subtitle\": \"수신자 : 모두\",\n" +
                "            \"imageUrl\": \"http://mobook.mobility42.io/img/noticeAddIcon.png\",\n" +
                "            \"imageType\": \"SQUARE\"\n" +
                "          },\n" +
                "          \"sections\": [\n" +
                "            {\n" +
                "              \"widgets\": [\n" +
                "                  {\n" +
                "                       \"textParagraph\": {\n" +
                "                            \"text\": \"새로운 공지사항이 등록되었습니다.\"\n" +
                "                        }\n" +
                "                  },\n" +
                "                {\n" +
                "                  \"buttonList\": {\n" +
                "                    \"buttons\": [\n" +
                "                      {\n" +
                "                        \"text\": \"공지사항으로 이동\",\n" +
                "                        \"onClick\": {\n" +
                "                          \"openLink\": {\n" +
                "                            \"url\": \"http://mobook.mobility42.io/notice/detail/" + noticeId + "\"\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }]\n" +
                "    }";

        return body;
    }
    public static String bookReturnHookBefore3Days(String memberName, String bookName){
        String body = "{\n" +
                "      \"cardsV2\": [{\n" +
                "        \"cardId\": \"createCardMessage\",\n" +
                "        \"card\": {\n" +
                "          \"header\": {\n" +
                "            \"title\": \"반납 예정 알림\",\n" +
                "            \"subtitle\": \"수신자 : "+ memberName +"\",\n" +
                "            \"imageUrl\": \"http://mobook.mobility42.io/img/bookReturnIcon.png\",\n" +
                "            \"imageType\": \"SQUARE\"\n" +
                "          },\n" +
                "          \"sections\": [\n" +
                "            {\n" +
                "              \"widgets\": [\n" +
                "                  {\n" +
                "                       \"textParagraph\": {\n" +
                "                            \"text\": \"<b>"+ bookName +"</b> 반납 예정일이 3일 후 입니다.\"\n" +
                "                        }\n" +
                "                  },\n" +
                "                {\n" +
                "                  \"buttonList\": {\n" +
                "                    \"buttons\": [\n" +
                "                      {\n" +
                "                        \"text\": \"웹으로 이동\",\n" +
                "                        \"onClick\": {\n" +
                "                          \"openLink\": {\n" +
                "                            \"url\": \"http://mobook.mobility42.io\"\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }]\n" +
                "    }";

        return body;
    }

    public static String bookRentHook(String name, String bookName) {
        String body = "{\n" +
                "      \"cardsV2\": [{\n" +
                "        \"cardId\": \"createCardMessage\",\n" +
                "        \"card\": {\n" +
                "          \"header\": {\n" +
                "            \"title\": \"대여 신청 알림\",\n" +
                "            \"subtitle\": \"수신자 : "+ name +", 관리자\",\n" +
                "            \"imageUrl\": \"http://mobook.mobility42.io/img/bookRentIcon.png\",\n" +
                "            \"imageType\": \"SQUARE\"\n" +
                "          },\n" +
                "          \"sections\": [\n" +
                "            {\n" +
                "              \"widgets\": [\n" +
                "                  {\n" +
                "                       \"textParagraph\": {\n" +
                "                            \"text\": \"<b>"+ bookName +"</b> 를 성공적으로 대여하셨습니다.\\n\\n반납 예정일은 <b>2주 후</b> 입니다.\"\n" +
                "                        }\n" +
                "                  },\n" +
                "                {\n" +
                "                  \"buttonList\": {\n" +
                "                    \"buttons\": [\n" +
                "                      {\n" +
                "                        \"text\": \"웹으로 이동\",\n" +
                "                        \"onClick\": {\n" +
                "                          \"openLink\": {\n" +
                "                            \"url\": \"http://mobook.mobility42.io\"\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }]\n" +
                "    }";
        return body;

    }
}
