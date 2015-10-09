package hGitter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Utils {
		
	HashMap<String,String> props = new HashMap<String, String>();
	
	public void loadProps() throws FileNotFoundException{
		
		Properties properties  = new Properties();
		FileInputStream input = new FileInputStream("C:\\sProjects\\ws\\hGitter\\resources\\config.properties");
		
		try {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String key : properties.stringPropertyNames()) {
		    String value = properties.getProperty(key);
		    props.put(key, String.valueOf(value));
		}
	}
	
	public String getValue(String key){
	    if(props.containsKey(key))
	    {
	        return props.get(key);
	    }else{
	    	return null;
	    }	    
	}
}

