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
}
