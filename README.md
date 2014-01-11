si-reactor
==========

Playground for combining SI and Reactor/LMAX Distruptor

```
---------------
direct channel

throughput: 133280/sec

---------------
traditional spring task executor

throughput: 177289/sec

	<task:executor id="taskExecutor" 
              		  pool-size="2-4"
               		  queue-capacity="1024"
               		  rejection-policy="CALLER_RUNS"/>


---------------
reactorRingBufferTaskExecutor with ring buffer size 1024 and BlockingWaitStrategy

throughput: 157035/sec

---------------
reactorFixedThreadPoolTaskExecutor fixed size = 2, 'backlog' = 1024

throughput: 156666/sec
```

