package project.restapi;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextLoad implements ApplicationContextAware{
	
	@Autowired
	private static ApplicationContext applicationContextAux;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		applicationContextAux = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContextAux;
	}

}
