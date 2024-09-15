package xuandong.ecommerce_ver_2.exception.accessDeniedHandler;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // Tạo đối tượng RestResponse với thông tin lỗi
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage("You do not have permission to access this resource.");

        // Cấu hình phản hồi
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json"); // Đảm bảo rằng kiểu nội dung là JSON
        response.setCharacterEncoding("UTF-8"); // Đảm bảo mã hóa ký tự là UTF-8

        // Viết đối tượng RestResponse ra phản hồi
        response.getWriter().write(objectMapper.writeValueAsString(res));
    }
}