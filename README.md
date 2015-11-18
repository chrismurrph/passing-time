# passing-time

This library exists to abstract away timezone and other changes to time. 'Passing time' is a difference in time from 'time zero'. Timezone and other artificial changes are recorded and can be applied to passing-time if and when necessary. But the values for time that are stored in the database, passed over the wire and reasoned about by the programmer all come from a passing-time timer. Time zero might be when the application was installed.

Imagine a graph over time of data values that have been collected - a trending graph. The x axis displacement between these values should reflect time as it passes. In an application that uses timestamps a timezone change will cause a sudden jump in time either forwards or backwards. How will you deal with this?

This library understands 'normal time'. When a deviation comes along it records this deviation as a variance or anomaly. For example a variance would be when the month of February goes into its 29th day. A rule exists for this variance and it is recorded as a variance. Anomalies should never happen, but if they do there should be some mechanism to inform site engineers.  