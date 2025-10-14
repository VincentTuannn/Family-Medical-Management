package Backend.FMM.Configure.ErrorResponse.AuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {


    // Spring Security
    // 401 unauthorized
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = "Lỗi xác thực danh tính !!";
        String detailMessage = authException.getLocalizedMessage();
        int code = 8;
        String moreInformation = "http://localhost:8080/api/v1/exception/8";

        String json = toJson(message, detailMessage, code, moreInformation);

        // Set the response status code
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized

        // return json
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();

    }


    // Spring Security
    // 403 Forbidden
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) throws IOException{

        String message = "Bạn không có đủ quyền để thực hiện chức năng này !!";
        String detailMessage = exception.getLocalizedMessage();
        int code = 9;
        String moreInformation = "http://localhost:8080/api/v1/exception/9";

        String json = toJson(message, detailMessage, code, moreInformation);

        // Set the response status code
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 Forbidden

        // return json
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }

    private String toJson(String message, String detailMessage, int code, String moreInformation) {
        String safeMessage = escapeJson(message);
        String safeDetail = escapeJson(detailMessage);
        String safeMore = escapeJson(moreInformation);
        return "{" +
                "\"message\":\"" + safeMessage + "\"," +
                "\"detailMessage\":\"" + safeDetail + "\"," +
                "\"data\":null," +
                "\"code\":" + code + "," +
                "\"moreInformation\":\"" + safeMore + "\"" +
                "}";
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }




}
