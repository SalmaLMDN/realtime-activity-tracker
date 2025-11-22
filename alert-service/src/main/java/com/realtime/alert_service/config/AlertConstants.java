package com.realtime.alert_service.config;

import java.time.Duration;

public class AlertConstants {
    public static final int LOGIN_THRESHOLD = 3;
    public static  final Duration WINDOW = Duration.ofSeconds(30);
    public static final int REPEAT_THRESHOLD = 5;
    public static final int ACTIONS_THRESHOLD = 10;
}
