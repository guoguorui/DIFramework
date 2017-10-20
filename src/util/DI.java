package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;

import data.ConnectPool;

import annotations.Select;
import data.OperData;

//处理singleon和multi的模式
//目前只支持将类名作为ID
//目前只支持JavaConfig
public class DI {
	
	public static HashMap<String,Object> contains=new HashMap<String,Object>(); 
	public static ConnectPool cp=new ConnectPool();
	
	public static void injectAll(){
		LinkedList<Object> bcs=ScanClass.scanType("configs","annotations.Config");
		LinkedList<Object> ucs=ScanClass.scanType("controllers","annotations.Controller");
		for(Object uc:ucs) {
			putToContains(uc);
			LinkedList<Class<?>> fieldClasses=ScanClass.scanField(uc);
			label1:
			for(Class<?> fieldClass:fieldClasses) {
				for(Object bc:bcs) {
					putToContains(bc);
					Method[] methods=bc.getClass().getDeclaredMethods();
					for(Method method:methods) {
						if(method.getReturnType()==fieldClass) {
							try {
								Object o=method.invoke(bc);
								putToContains(o);
								Field[] fields=uc.getClass().getDeclaredFields();
								for(Field field:fields) {
									if(field.getType()==fieldClass) {
										System.out.println("哥，匹配了这个"+fieldClass);
										field.set(uc, o);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							//这里应该跳出fieldclass
							continue label1;
						}//real di
					}//bc method foreach
				}//bc foreach 
			}//uc fieldclass foreach
		}//uc foreach	
		
	}
	
	
	//根据类型名注入并返回实例对象
	public static Object injectSpecified(String name) {
		Class<?> specifiedClass=null;
		Object specifiedObject=null;
		try {
			specifiedClass=Class.forName("beans."+name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		LinkedList<Object> bcs=ScanClass.scanType("configs","annotations.Config");
		for(Object bc:bcs) {
			Method[] methods=bc.getClass().getDeclaredMethods();
			for(Method method:methods) {
				if(method.getReturnType().equals(specifiedClass)) {
					try {
						specifiedObject=method.invoke(bc);
						putToContains(specifiedObject);
						return specifiedObject;
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return specifiedObject;
	}
	
	/*
		info =Introspector.getBeanInfo(uc.getClass());
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		Class<?> cla=pd[i].getPropertyType();
		Method m=pd[i].getWriteMethod();
		m.invoke(uc, jb);
	*/
	
	public static void putToContains(Object o) {
		String simpleName=o.getClass().getSimpleName();
		if(!contains.containsKey(simpleName)) {
			contains.put(simpleName, o);
		}
		else {
			System.out.println("兄弟，你已经是我的人了，不需要再认证一次: "+simpleName);
		}
		
	}
	
	
	public static Object getFromContains(String name) {
		if(contains.containsKey(name)) {
		}
		else {
			System.out.println("哥，我这就来扫描注入所需的Bean: "+name);
			try {
				Class.forName("beans."+name);
				injectSpecified(name);
			} catch (ClassNotFoundException e) {
				try {
					//本来的simpleName是$Proxy
					Object o=injectOrm(name);
					Class<?>[] cls=o.getClass().getInterfaces();
					for(Class<?> cla:cls)
						contains.put(cla.getSimpleName(), o);
					//putToContains(o);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		return contains.get(name);
	}
	
	public static void initAll() {
		DI.injectAll();
		System.out.println("哥，能注入的我都注入了");
	}
	
	public static void initCore() {
		LinkedList<Object> bcs=ScanClass.scanType("configs","annotations.Config");
		for(Object bc:bcs)
			putToContains(bc);
		LinkedList<Object> ucs=ScanClass.scanType("controllers","annotations.Controller");
		for(Object uc:ucs)
			putToContains(uc);
		System.out.println("哥，现在只注入了核心的Bean");
	}
	
	//本质并非实现接口，只是获取了注解然后进行处理
	public static Object injectOrm(String name) throws ClassNotFoundException {
		InvocationHandler ih=new InvocationHandler() {
			@Override
			public Object invoke(Object proxy,Method method,Object[] args) {
				Select select=null;
				Object returnObject=null;
				Class<?> returnClass;
				Annotation[] anns=method.getAnnotations();
				for(Annotation ann:anns) {
					if(ann instanceof Select) {
						select=(Select) ann;
						if(select!=null) {
							String sql=select.value();
							System.out.println(sql);
							OperData od=new OperData(cp);
							returnClass=method.getReturnType();
							returnObject=od.GenericFind(sql,returnClass);
						}
					}
				}				
				return returnObject;
			}
		};
		Class<?> cl=Class.forName("mapper."+name);
		Class<?>[] cc = { cl };
		Object proxyInstance=Proxy.newProxyInstance(cl.getClassLoader(), cc, ih);
		return proxyInstance;
	}
	
}
