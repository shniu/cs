# 测试



### Soak testing

浸泡测试涉及在连续的可用性期内测试具有典型生产负载的系统，以验证生产使用下的系统行为。

如果无法进行此类扩展测试，则可能需要推断结果。例如，如果要求系统在100个小时内处理10,000个事务，则可以在较短的持续时间内（例如50个小时）完成代表实际生产用途（和保守估计）的10,000个事务的处理。良好的浸泡测试还应包括模拟峰值负载而不是平均负载的能力。如果无法在特定时间段内控制负载，则可以选择（保守地）让系统在测试期间以峰值生产负载运行。

* [What is soak testing ?](https://www.katalon.com/resources-center/blog/soak-testing/)
* [https://en.wikipedia.org/wiki/Soak\_testing](https://en.wikipedia.org/wiki/Soak_testing)
* [https://www.guru99.com/soak-testing.html](https://www.guru99.com/soak-testing.html)
  * In Software Engineering, Soak testing is done to determine if the application under test can sustain the continuous load.
  * It is a type of performance test.
  * It helps the system to determine whether it will stand up to a very high volume of usage
  * In this type of testing, what basically monitored is the memory utilization by an application in a system
  * Checks that need to be done by any user/tester before they begin with Soak Testing include
    * Monitor the database resource consumption.
    * Monitor the server resource consumption \(ex- CPU usage\).
    * Soak test should run with realistic user concurrency.

