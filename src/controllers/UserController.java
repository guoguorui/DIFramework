package controllers;

import annotations.Autowired;
import annotations.Controller;
import beans.AnotherBean;
import beans.JavaBean;
import mapper.UserService;

@Controller
public class UserController{

	@Autowired
	UserService us;
	
	@Autowired
	public JavaBean jb;
	
	@Autowired
	public AnotherBean ab;
	
	public void testUs() {
		us.findPasswordByName();
	}
	
	public void testJb() {
		System.out.println(jb.getName());
	}
	
	public void testAb() {
		System.out.println(ab.getAge());
	}
}
