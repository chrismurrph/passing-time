# passing-time

This library exists to abstract away time changes that cannot be 'experienced'. 'Passing time' is the difference in time from 'time zero'. You can do date arithmetic on it! 

Timezone and other artificial changes are recorded and can be applied to passing-time if and when necessary. But values for time that are stored in the database, passed over the wire and reasoned about by the programmer all come from the passing-time timer. Time zero might be when the application was installed.

Imagine a graph over time of data values that have been collected - a trending graph. The x axis displacement between these values should reflect time as it passes. In an application that uses timestamps a timezone change will cause a sudden jump in time either forwards or backwards. How will you deal with this?

This library is essentially a timer that 'beats' every five seconds at which moment it compares the time it expects it to be with the wallclock time (the time the computer provides). When the timer moves a second out of sync then the timer is adjusted. The expected time is based on 'normal time'.

When a deviation comes along it is recorded as a variance or anomaly from normal time. For example a variance would be when the month of February goes into its 29th day. A rule exists for this particular variance and thus it is recorded as such. Anomalies should never happen, but if they do there should be some mechanism to inform site engineers immediately that a problem has occured. 

This library is more an idea than a library. For instance there are no rules and no variances. I wrote it to support another project you will find here - graphing. You can't have good trending without a good concept of time.

