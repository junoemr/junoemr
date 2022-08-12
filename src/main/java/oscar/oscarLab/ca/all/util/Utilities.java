/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


/*
 * Utilities.java
 *
 * Created on May 31, 2007, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.util;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities
{
	public static final String NEW_LINE = "\r\n";
	private static final Logger logger = MiscUtils.getLogger();

	private Utilities()
	{
		// utils shouldn't be instantiated
	}

	public static ArrayList<String> separateMessages(String fileName) throws IOException
	{
		return separateMessages(Files.newInputStream(Paths.get(fileName)));
	}

	public static ArrayList<String> separateMessages(File file) throws IOException
	{
		return separateMessages(Files.newInputStream(file.toPath()));
	}

	public static ArrayList<String> separateMessages(InputStream is) throws IOException
	{
		ArrayList<String> messages = new ArrayList<>();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		boolean firstPIDflag = false; //true if the first PID segment has been processed false otherwise
		boolean firstMSHflag = false; //true if the first MSH segment has been processed false otherwise
		boolean skipMessage = false;

		StringBuilder sb = new StringBuilder();
		String mshSeg = "";

		while((line = br.readLine()) != null)
		{
		    if(line.length() > 3)
		    {
			    String header = line.substring(0, 3);
			    // ignore the batch header /file header segments for now.
			    if(header.equals("BHS") || header.equals("FHS"))
			    {
				    continue;
			    }
			    if(!header.equals("MSH") && skipMessage)
			    {
				    continue;
			    }
			    if(header.equals("MSH"))
			    {
				    if(line.contains("ORU")
						    || line.contains("ORM^002")
						    || line.contains("ORM^O01")
						    || line.contains("MDM^T08")
						    || line.contains("MDM^T02")
						    || line.contains("MDM^T11")
						    || line.contains("OLIS"))
				    {
					    skipMessage = false;
				    }
				    else
				    {
					    // skip any messages that are not ORU^R01
					    skipMessage = true;
					    continue;
				    }
				    if(firstMSHflag)
				    {
					    messages.add(sb.toString());
					    sb.delete(0, sb.length());
				    }
				    mshSeg = line;
				    firstMSHflag = true;
				    firstPIDflag = false;
			    }
			    else if(header.equals("PID"))
			    {
			    	// OLIS likes to send multiple messages (with multiple PIDs) under the same MSH segment.
				    // so we need to fake them a bit by treating each PID as a new message and re-using the MSH segment
				    if(firstPIDflag)
				    {
					    messages.add(sb.toString());
					    sb.delete(0, sb.length());
					    sb.append(mshSeg + NEW_LINE);
				    }
				    firstPIDflag = true;
			    }
			    sb.append(line + NEW_LINE);
		    }
		}

		// add the last message
		messages.add(sb.toString());

		is.close();
		br.close();

		return messages;
	}

	public static String fixLineBreaks(String originalHl7, String marker)
	{
		Pattern validSegmentPattern = Pattern.compile("^[a-zA-Z0-9]{3}\\|.*$");

		StringBuilder stringBuilder = new StringBuilder();
		boolean firstRun = true;

		String[] lines = Pattern.compile(NEW_LINE).split(originalHl7);
		for(String line : lines)
		{
			Matcher matcher = validSegmentPattern.matcher(line);
			if(matcher.matches())
			{
				if(!firstRun)
				{
					stringBuilder.append(NEW_LINE);
				}
			}
			else
			{
				stringBuilder.append(marker);
			}
			stringBuilder.append(line);
			firstRun = false;
		}
		return stringBuilder.toString();
	}
    
    /**
     * 
     * @param stream
     * @param filename
     * @return String
     */
    public static String saveFile(InputStream stream,String filename ){
        String retVal = null;
        
        
        try {
            OscarProperties props = OscarProperties.getInstance();
            //properties must exist
            String place= props.getProperty("DOCUMENT_DIR");
            
            if(!place.endsWith("/"))
                place = new StringBuilder(place).insert(place.length(),"/").toString();
            retVal = place+"LabUpload."+filename.replaceAll(".enc", "")+"."+(new Date()).getTime();
            
            logger.debug("saveFile place="+place+", retVal="+retVal);
            //write the  file to the file specified
            OutputStream os = new FileOutputStream(retVal);
            
            int bytesRead = 0;
            while ((bytesRead = stream.read()) != -1){
                os.write(bytesRead);
            }
            os.close();
            
            //close the stream
            stream.close();
        }catch (FileNotFoundException fnfe) {
        	logger.error("Error", fnfe);
            return retVal;
            
        }catch (IOException ioe) {
        	logger.error("Error", ioe);
            return retVal;
        }
        return retVal;
    }    
    
    public static String saveHRMFile(InputStream stream,String filename ){
    	String retVal = null;
    	String place = OscarProperties.getInstance().getProperty("OMD_hrm");
    	
    	try {
    	   	if(!place.endsWith("/")){
    	   		place = new StringBuilder(place).insert(place.length(),"/").toString();
    	   	}
    	   	retVal = place+"KeyUpload."+filename+"."+(new Date()).getTime();
    	
    	   	//write the  file to the file specified
    	   	OutputStream os = new FileOutputStream(retVal);
    	
    	   	int bytesRead = 0;
    	   	while ((bytesRead = stream.read()) != -1){
    	   		os.write(bytesRead);
    	   	}
    	   	os.close();
    	
    	   	//close the stream
    	   	stream.close();
		}catch (FileNotFoundException fnfe) {
			logger.error("Error", fnfe);
			return retVal;
    	}catch (IOException ioe) {
    		logger.error("Error", ioe);
    		return retVal;
    	}
    		return retVal;
    	}    
    
    public static String savePdfFile(InputStream stream,String filename ){
        String retVal = null;                
        try {
            OscarProperties props = OscarProperties.getInstance();
            //properties must exist
            String place= props.getProperty("DOCUMENT_DIR");
            
            if(!place.endsWith("/")) {               
                place = new StringBuilder(place).insert(place.length(),"/").toString();
            }
            
            filename = filename.replaceAll(".enc", "");
            int fileExtIdx = -1;
            if (filename.endsWith(".pdf")) {
                fileExtIdx = filename.lastIndexOf(".pdf");
            } else if (filename.endsWith(".PDF")) {
                fileExtIdx = filename.lastIndexOf(".PDF");
            }
            
            if (fileExtIdx >= 0) {
                filename = filename.substring(0, fileExtIdx);
            }
            
            retVal = place+"DocUpload."+filename+"."+(new Date()).getTime()+".pdf";
            
            //write the  file to the file specified
            OutputStream os = new FileOutputStream(retVal);
            
            int bytesRead = 0;
            while ((bytesRead = stream.read()) != -1){
                os.write(bytesRead);
            }
            os.close();
            
            //close the stream
            stream.close();
        }catch (FileNotFoundException fnfe) {
        	logger.error("Error", fnfe);
            return retVal;
            
        }catch (IOException ioe) {
        	logger.error("Error", ioe);
            return retVal;
        }
        return retVal;
    }  
    
    
    /*
     *  Return a string corresponding to the data in a given InputStream
     */
    public static String inputStreamAsString(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        
        stream.close();
        br.close();
        return sb.toString();
    }
}
