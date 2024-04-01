package org.ddiachenko;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("J:/GoIT/Servlets_Part2/src/main/webapp/WEB-INF/temp/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        Cookie[] cookies = req.getCookies();
        String timezone = req.getParameter("timezone");


        if (timezone != null && !timezone.isEmpty()) {

            timezone = timezone.replace(" ", "+");
            Cookie userCookie = new Cookie("timezone", timezone);
            resp.addCookie(userCookie);

            Context simpleContext = new Context(
                    req.getLocale(),
                    Map.of("datetime", formattedZoneDateTime(ZonedDateTime.now(ZoneId.of(timezone))))
            );
            engine.process("template", simpleContext, resp.getWriter());
        } else {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("timezone".equals(cookie.getName())) {
                        timezone = cookie.getValue();
                        break;
                    }
                }
                Context simpleContext = new Context(
                        req.getLocale(),
                        Map.of("datetime", formattedZoneDateTime(ZonedDateTime.now(ZoneId.of(timezone))))
                );
                engine.process("template", simpleContext, resp.getWriter());
            } else {
                Context simpleContext = new Context(
                        req.getLocale(),
                        Map.of("datetime", formattedDateTime(LocalDateTime.now()))
                );
                engine.process("template", simpleContext, resp.getWriter());
            }
        }


        resp.getWriter().close();
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'"));
    }

    private String formattedZoneDateTime(ZonedDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
    }
}
