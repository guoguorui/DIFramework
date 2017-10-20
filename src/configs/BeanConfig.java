package configs;

import annotations.Bean;
import annotations.Config;
import beans.AnotherBean;
import beans.JavaBean;

@Config
public class BeanConfig{
	
	@Bean
	public JavaBean getJavaBean() {
		JavaBean jb=new JavaBean();
		jb.setName("hello");
		return jb;
	}
	
	
	@Bean 
	public AnotherBean getAnotherBean() {
		AnotherBean ab=new AnotherBean();
		ab.setAge(21);
		return ab;
	}
}
