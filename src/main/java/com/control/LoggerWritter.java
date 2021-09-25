package com.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerWritter {
	
	

	public LoggerWritter() {
		try {
		      File myObj = new File("Errors.txt");
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	public void writeErrors() {
		try {
			FileWriter wr = new FileWriter("Errors.txt",true);
			//wr.write(null);
		} catch (Exception e) {
			
		}
	}
}
