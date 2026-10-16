### K6 Tests


vus:                10	Run 10 virtual users concurrently.
duration:           "60s"	Run the test for 60 seconds.
summaryTrendStats	Choose which latency statistics appear in the summary.
http_req_failed	    Require the failed-request rate to be below 1%.
http_req_duration	Require the 95th-percentile request duration to be below 2 seconds.


Virtual Users "vus": it is a simulated worker executing my default funciton repeteadly

threshold is an automated pass/fail ruke

rate < 0.01 means fewer than 1 percetn shoud fail
P(95) < 2000 means should be below 2000 milliseconds == 2 seconds


### tags,exec

helps the request to catagorize, filyer performence test results
exec: tells which scenerio to exec

