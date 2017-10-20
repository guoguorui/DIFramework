package util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedList;

import annotations.Autowired;
import main.ApplicationBoot;

public class ScanClass {
	
	
	//返回需要注入的field的class
	public static LinkedList<Class<?>> scanField(Object object) {
		Class<?> typeClass=object.getClass();
		Field[] fs=typeClass.getDeclaredFields();
		LinkedList<Class<?>> clc=new LinkedList<Class<?>>();
		Class<?> cl=null;
		for(Field f:fs) {
			Autowired c=f.getAnnotation(Autowired.class);
			if(c!=null) {
				cl=f.getType();
				clc.add(cl);
				System.out.print("哥，这里有个家伙需要注入： "+f.getName()+" ");
				System.out.println(cl.getSimpleName());
			}
		}
		return clc;
	}
		
	//扫描包下的具有注解relationAnnotationName的类,获取实例
	public static LinkedList<Object> scanType(String basePackage,String relationAnnotationName){
		LinkedList<Object> destObjects=new LinkedList<Object>();
		String path=basePackage.replace("\\.", "/");
		ClassLoader cl=ApplicationBoot.class.getClassLoader();
		URL url = cl.getResource(path);
		String fileUrl = url.getFile().substring(1);
		File file=new File(fileUrl);
		String[] names=file.list();
		Class<?> relationAnnotationClass=null;
		try {
			relationAnnotationClass = Class.forName(relationAnnotationName);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Object destObject=null;
		for(String name:names) {
			//兄弟，包名不能少啊
			name=basePackage+"."+name.split("\\.")[0];
			Class<?> tempClass;
			try {
				tempClass = Class.forName(name);
				Annotation[] anns=tempClass.getAnnotations();
				for(Annotation ann:anns) {
					if(ann.annotationType().equals(relationAnnotationClass)) {
						try {
							destObject=tempClass.newInstance();
							destObjects.add(destObject);
						} catch (Exception e) {
							e.printStackTrace();
						} 
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return destObjects;
	}
}	
