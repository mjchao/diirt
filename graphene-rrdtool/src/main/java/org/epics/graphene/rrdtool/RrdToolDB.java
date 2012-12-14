/**
 * Copyright (C) 2012 University of Michigan
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene.rrdtool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.util.array.ListDouble;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
public class RrdToolDB {
    public static TimeSeries fetchData(Collection<String> signals, Timestamp start, Timestamp end) {
        Map<String, TimeSeries> cachedFileCFData = new HashMap<>();
        Map<String, ListDouble> buffers = new HashMap<>();
        List<Timestamp> timestamps = null;
        RrdToolReader reader = new RrdToolReader();
        for (String signal : signals) {
            String[] tokens = signal.split(":");
            String cacheKey = tokens[0]+":"+tokens[2];
            TimeSeries data = cachedFileCFData.get(cacheKey);
            if (data == null) {
                data = reader.readFile(tokens[0], tokens[2], start, end);
                cachedFileCFData.put(cacheKey, data);
            }
            ListDouble buffer = data.getValues().get(tokens[1]);
            if (buffer == null) {
                throw new IllegalArgumentException("Signal " + signal + " was not found");
            }
            buffers.put(signal, buffer);
            timestamps = data.getTime();
        }
        
        return new TimeSeries(timestamps, buffers);
    }
}
