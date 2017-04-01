package com.add.utils;

/**
 * Created by Kuajing on 2017/3/6.
 */

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HttpServletResponse帮助类
 */
public class ResponseUtil {

        public static void renderJson(HttpServletResponse response, String text) {
            // System.out.print(text);
            render(response, "application/json", text);
        }


        /**
         * 发送内容。使用UTF-8编码。
         *
         * @param response
         * @param contentType
         * @param text
         */
        private static void render(HttpServletResponse response, String contentType, String text) {
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            try {
                response.getWriter().write(text);
            } catch (IOException e) {
            }
        }

}
