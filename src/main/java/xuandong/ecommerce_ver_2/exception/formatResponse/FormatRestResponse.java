package xuandong.ecommerce_ver_2.exception.formatResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
		int status = servletResponse.getStatus();

		// Kiểm tra nếu body đã là một RestResponse thì trả về luôn, không cần đóng gói
		// lại
		if (body instanceof RestResponse) {
			return body;
		}

		RestResponse<Object> res = new RestResponse<>();
		res.setStatusCode(status);

		if (status >= 400) {
			res.setError(body instanceof String ? (String) body : "Unknown error occurred");
		} else {
			res.setData(body);
			res.setMessage("CALL API SUCCESS");
		}

		return res;
	}

}
