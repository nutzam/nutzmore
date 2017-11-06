package demo.hanlder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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

	Log log = Logs.get();

	public static final String DEFAULT_ERROR_VIEW = "error";

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public NutMap defaultErrorHandler(HttpServletRequest request,HttpServletResponse response, Exception e) throws Exception {
		log.debug(e);
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return NutMap.NEW().addv("exception", e.getMessage());
	}
}