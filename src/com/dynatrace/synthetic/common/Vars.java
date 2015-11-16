package com.dynatrace.synthetic.common;

public class Vars {
	public static final String REST_URL = "https://datafeed-api.dynatrace.com/publicapi/rest/v1.0/{method}";
	public static final int    DEFAULT_STALE_TIME                      = 7;
	public static final int    DEFAULT_START_TIME_MINUTE               = 120;
	public static final int    DEFAULT_START_TIME_WINDOW_HOUR          = 24;
	public static final int    DEFAULT_MAX_CONNECTION_LIMIT            = 250;
	public static final int    DEFAULT_MAX_CONNECTIONS_PER_ROUTE       = 100;
	public static final long   DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = 10000;
	public static final long   DEFAULT_SOCKET_TIMEOUT_MILLISECONDS     = 120000;
}
