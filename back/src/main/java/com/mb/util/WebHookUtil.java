package com.mb.util;

public class WebHookUtil {
    public static String bookAddHook(){
        String body = "{\n" +
                "      'cardsV2': [{\n" +
                "        'cardId': 'createCardMessage',\n" +
                "        'card': {\n" +
                "          'header': {\n" +
                "            'title': 'A Card Message!',\n" +
                "            'subtitle': 'Created with Chat REST API',\n" +
                "            'imageUrl': 'https://developers.google.com/chat/images/chat-product-icon.png',\n" +
                "            'imageType': 'CIRCLE'\n" +
                "          },\n" +
                "          'sections': [\n" +
                "            {\n" +
                "              'widgets': [\n" +
                "                {\n" +
                "                  'buttonList': {\n" +
                "                    'buttons': [\n" +
                "                      {\n" +
                "                        'text': 'Read the docs!',\n" +
                "                        'onClick': {\n" +
                "                          'openLink': {\n" +
                "                            'url': 'https://developers.google.com/chat'\n" +
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
