package ltd.beihu.core.web.boot.exception;

import com.google.common.base.Throwables;
import ltd.beihu.core.web.boot.code.BasicServiceCode;
import ltd.beihu.core.web.boot.mail.DefaultMailSender;
import ltd.beihu.core.web.boot.response.BasicResponse;
import ltd.beihu.core.web.boot.response.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.util.Iterator;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class ServiceExceptionHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);

	@Resource(name = "defaultMailSender")
	private DefaultMailSender defaultMailSender;

	/**
	 * 自定义异常
	 */
	@ExceptionHandler(ServiceException.class)
	public JsonResponse handleServiceException(ServiceException e){
		String stackTraceAsString = Throwables.getStackTraceAsString(e);
		LOGGER.error("【ServiceExceptionHandler - ServiceException】\r\n [{}]", stackTraceAsString);
		defaultMailSender.warn("【服务异常】", stackTraceAsString);
		return BasicResponse.error(e);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public JsonResponse handleValidException(MethodArgumentNotValidException e){
		LOGGER.error("【ServiceExceptionHandler - ServiceException】\r\n [{}]", Throwables.getStackTraceAsString(e));
		Iterator var2 = e.getBindingResult().getAllErrors().iterator();
		StringBuilder sb = new StringBuilder();
		if (var2.hasNext()) {
			ObjectError error = (ObjectError)var2.next();
			sb.append(BasicServiceCode.BAD_REQUEST.getMesg()).append(",").append(error.getDefaultMessage());
		}
		return BasicResponse.error(new ServiceException(BasicServiceCode.BAD_REQUEST), sb.toString());
	}

	@ExceptionHandler(Exception.class)
	public JsonResponse handleException(Exception e){
		String stackTraceAsString = Throwables.getStackTraceAsString(e);
		LOGGER.error("【ServiceExceptionHandler - ServiceException】\r\n [{}]", stackTraceAsString);
		defaultMailSender.warn("【系统异常】", stackTraceAsString);
		return BasicResponse.error(new ServiceException(BasicServiceCode.FAILED));
	}
}
