package demo.hanlder;

import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.NutMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author kerbores
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	public static final String DEFAULT_ERROR_VIEW = "error";

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public NutMap defaultErrorHandler(HttpServletResponse response, Exception e) throws Exception {
		response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
		return NutMap.NEW().addv("exception", e);
	}
}