# custom-log-lib
The custom log library takes care of printing the log in a specific format and logs all the required information that will help IT guy to debug the application in production


### Problem Statement

While monitoring the production logs and debugging an issue in production, some of the important parameters that are looked for are the request during an error scenario , flowName or the serviceName for which the error has happened, execution time of the service etc .
In order to get some of the parameters , developers generally hard code the logic to log the request or the execution time of a method . This not only makes the code cluttered , but also it is impossible to handle this in every flow  as this can be an information that ideally should not be printed in happy path. 
Hence , we need to have a framework which itself takes care of capturing the execution time of a method , prints the request during the error scenario , and tells the flow or service which has thrown the error .

### Solution
The custom log library takes care of printing the log in a specific format and logs all the required information that will help IT guy to debug the application in production. 

### Technical Implementation	
-To capture the execution time Aspect and for logging logback  is being used. 
-logback.xml is added to the custom-log-lib which contains the profiles, the syntax and the location where the logs will get printed. 
-Adding attribute details to the MDC we have CustomFilter.java and CustomLogAspect.java implemented.
-Custom annotation CustomLog.java is added to capture serviceName and Executiontime of service.
###	 Implementation 
 -Ensure your spring application has dependency for custom-log-lib.
```sh

<dependency>
<groupId>com.custom</groupId>
	<artifactId>custom-log-lib</artifactId>
	<version>0.0.1.0</version>
</dependency>
```


-Component name should be provided as a property in the properties file for the boot application.
```sh
 component.name = spring-boot-service
```
-The properties to regulate the logging level should be added to properties file under customplatformconfigurations so that properties can be controlled using config server without restarting application.
```sh
 file:customServicesApp-local.properties 
```
```sh
logging.level.com.custom= INFO
logging.level.org.springframework.ws.client.MessageTracing=ERROR
logging.level.org.springframework.ws=INFO
logging.level.org.apache.http.headers=INFO
```		
-Log the Service Name : For custom annotation, we need to add the annotation like below :
```sh
@CustomLog(serviceName = "Availability")
```
Attribute serviceName in the Annotation will help in defining the appropriate serviceName. When client wants to specify a particular serviceName , then the attribute value needs to be passed appropriately . 
If client don’t pass anything in serviceName attribute classname.methodName will be logged under serviceName field.
During error scenarios serviceName would be the first element from StackTrace would be logged from where the exception has occurred.

-Log invocation & execution Timing: In order to trigger logging ,the methods for which we need the execution time, we have to add the annotation at method level. Similarly we can add the very same annotation at all the entry level methods to get the execution time of a flow/service call.

```sh
@CustomLog(serviceName= "Availability")
@Override
public CustomAvailabilityResponseWsDTO getAvailability() {
// Code Execution.
}
```
-Log Request during Error: In order to log request body, we have to populate temp request using CustomMdcUtil before making any external integrations call so that logging of request structure will be handled in error Scenario.

```sh
CustomMdcUtil.setTempRequest(requestData); 
```
-Request Logging: Controllers should add the original request to the tempRequest to capture this data during error scenario.  

###Sample Logs

Scenario 1: 200 ok
```sh
{"dateTime":"2017-11-29 18:42:09,093","host":"USHYDSHYN4","port":"8080","txnIdentifier":"595","threadId":"http-nio-8080-exec-3","sessionId":"","keyInfo":"","channelName":"11e2ba1399fa4854a2a9d88219407d1b","remoteHost":"","errorCode":"","errorMsg":"","environment":"local","component":"","serviceName":"Availability","executionTime":"14389","request":"","response":"","message":"{message:com.custom.client.app.impl.CustomSearchClientImpl.getAvailability timing:14389 }"}
Scenario 2: Error
```

```sh
"dateTime":"2017-11-30 18:45:55,524","host":"USHYDLMEENAKSH5","port":"8080","txnIdentifier":"663","threadId":"http-nio-8080-exec-2","sessionId":"","keyInfo":"","channelName":"11e2ba1399fa4854a2a9d88219407d1b","remoteHost":"","errorCode":"","errorMsg":"","environment":"local","component":"","serviceName":"Search Service","executionTime":"","request":"","response":"","message":"{app-id recieved in request : 11e2ba1399fa4854a2a9d88219407d1b  }"}
{"dateTime":"2017-11-30 18:46:14,258","host":"USHYDLMEENAKSH5","port":"8080","txnIdentifier":"663","threadId":"http-nio-8080-exec-2","sessionId":"","keyInfo":"","channelName":"11e2ba1399fa4854a2a9d88219407d1b","remoteHost":"","errorCode":"T01","errorMsg":"","environment":"local","component":"","serviceName":"validateBookingDate","executionTime":"","request":"","response":"","message":"{Exception Occurred: com.custom.helper.CustomSearchFacadeHelper.validateDate(CustomFacadeHelper.java:617) }"}
```
