package api.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Service class dispatcher by uri
 *
 * @author kris
 */
@Component
public class ServiceDispatcher {
    private static ApplicationContext springContext;    // 1

    @Autowired
    public void init(ApplicationContext springContext) {    // 2
        ServiceDispatcher.springContext = springContext;
    }

    protected Logger logger = LogManager.getLogger(this.getClass());

    public static ApiRequest dispatch(Map<String, String> requestMap) { // 3
        String serviceUri = requestMap.get("REQUEST_URI");  // 4
        String beanName = null;

        if (serviceUri == null) {   // 5
            beanName = "notFound";
        }

        if (serviceUri.startsWith("/tokens")) { // 6
            String httpMethod = requestMap.get("REQUEST_METHOD");   // 7

            if ( httpMethod == "POST")
                beanName = "tokenIssue";
            else if ( httpMethod == "DELETE" )
                beanName = "tokenExpier";
            else if ( httpMethod == "GET" )
                beanName = "tokenVerify";
            else
                beanName = "notFound";
        }
        else if (serviceUri.startsWith("/users")) { // 8
            beanName = "users";
        }
        else {
            beanName = "notFound";
        }

        ApiRequest service = null;
        try {
            service = (ApiRequest) springContext.getBean(beanName, requestMap); // 9
        }
        catch (Exception e) {
            e.printStackTrace();
            service = (ApiRequest) springContext.getBean("notFound", requestMap);   // 10
        }

        return service;
    }
}