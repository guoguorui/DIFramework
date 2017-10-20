package data;
import java.sql.*;

public class OperData {
	   
	 Connection conn = null;
	 Statement stmt = null;
	 ResultSet rs=null;
	 ConnectPool cp=null;
	 
	 public OperData(ConnectPool cp) {
		 	try {
		        conn = cp.getConnFromPool(this);
		        stmt = conn.createStatement();
		    }
		   catch(Exception e) {
			   e.printStackTrace();
		   }
	 }
	 	 
	 public Object GenericFind(String sql,Class<?> returnClass) {
		 Object returnObject=null;
		 try {
			 rs=stmt.executeQuery(sql);;
			 while(rs.next()){
				 String destString=sql.split(" ")[1];
				 returnObject= rs.getString(destString);
		     }
			 cleanClose();
		}
	    catch(Exception e) {
		    e.printStackTrace();
	    }
		 return returnObject;
	 }
	   
	   public void cleanClose() {
		   boolean flag=false;
		   try{
			   rs.close();
		       stmt.close();
		       conn.close();
		       flag=true;
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		        	 stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(flag==false) {
		        	 conn.close();
		         }
		        	 
		      }catch(SQLException se2){
		      }
		   }//end try
	   }
}
