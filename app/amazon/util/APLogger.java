package amazon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;

/**
 * Title: Amazon日志记录器
 *
 * @author Lc
 *
 * @date 2016年4月21日 下午4:30:44
 */
public class APLogger extends Logger {

	private static final ALogger logger = of("ap");
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	private static String getSimpleDate() {
		return format.format(new Date());
	}

	public static String msg(String paramString) {
		return "[" + getSimpleDate() + "] thread." + Thread.currentThread().getId() + " >>> " + paramString;
	}
	
	public static void trace(String paramString) {
		logger.trace( msg(paramString) );
	}

	public static void trace(String paramString, Throwable paramThrowable) {
		logger.trace(msg(paramString), paramThrowable);
	}

	public static void debug(String paramString) {
		logger.debug( msg(paramString) );
	}

	public static void debug(String paramString, Throwable paramThrowable) {
		logger.debug(msg(paramString), paramThrowable);
	}

	public static void info(String paramString) {
		logger.info( msg(paramString) );
	}

	public static void info(String paramString, Throwable paramThrowable) {
		logger.info(msg(paramString), paramThrowable);
	}

	public static void warn(String paramString) {
		logger.warn( msg(paramString) );
	}

	public static void warn(String paramString, Throwable paramThrowable) {
		logger.warn(msg(paramString), paramThrowable);
	}

	public static void error(String paramString) {
		logger.error( msg(paramString) );
	}

	public static void error(String paramString, Throwable paramThrowable) {
		logger.error(msg(paramString), paramThrowable);
	}
	
}
