package main;

import beans.JavaBean;
import mapper.UserService;
import util.DI;

//做好ORM的接口对接
public class ApplicationBoot {
	public static void main(String[] args) throws ClassNotFoundException {
		DI.initCore();
		JavaBean jb=(JavaBean) DI.getFromContains("JavaBean");
		System.out.println(jb.getName());
		UserService us=(UserService) DI.getFromContains("UserService");
		System.out.println(us.findPasswordByName());
	}
}
