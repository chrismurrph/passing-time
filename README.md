# passing-time

This library exists to abstract away time changes that cannot be 'experienced'. Any passing-time value is the accumulation of time since 'time zero'.

Timezone and other artificial changes are recorded and can be applied to passing-time if and when necessary. However values for time that are stored in the database, passed over the wire and otherwise used by the programmer should all come from the passing-time timer. Time zero might be when the application was installed. 

Imagine a (visual) graph over time of data values that have been collected - a trending graph. The x axis displacement between these values should reflect time as it passes. In an application that uses timestamps a timezone change will cause a sudden jump in time either forwards or backwards. How will you deal with this?

This library is essentially a timer that 'beats' every five seconds at which moment it compares the time it expects it to be (normal time) with the wallclock time (the time the computer provides). When the timer moves a second out of sync then it is adjusted.

When a significant deviation comes along it is recorded as a variance or anomaly from normal time. For example a variance would be when the month of February goes into its 29th day. If a rule exists for this particular expected variance then the variance will be recorded. Anomalies should never happen [use condition system]. 

This library is currently more an idea than a library. For instance there are currently no variance rules and variances are not recorded! It has been written to support another project you will find here - graphing. You can't have good trending without a good concept of time.

