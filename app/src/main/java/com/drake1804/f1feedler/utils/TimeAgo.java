package com.drake1804.f1feedler.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavel.Shkaran on 5/26/2016.
 */
public class TimeAgo {

    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1) );

    public static final List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

    public static String toDuration(long duration) {
        if(duration > 0){
            StringBuffer res = new StringBuffer();
            for(int i=0; i< TimeAgo.times.size(); i++) {
                Long current = TimeAgo.times.get(i);
                long temp = duration/current;
                if(temp > 0) {
                    res.append(temp).append(" ").append( TimeAgo.timesString.get(i) ).append(temp > 1 ? "s" : "").append(" ago");
                    break;
                }
            }
            if("".equals(res.toString()))
                return "0 second ago";
            else
                return res.toString();
        } else {
            return " ";
        }

    }

}
