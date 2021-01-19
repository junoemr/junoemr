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


package oscar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.oscarehr.util.MiscUtils;

public class DocumentMgtUploadServlet extends HttpServlet{
  final static int BUFFER = 2048;
  public java.util.Date today;
  public String output;
  public SimpleDateFormat formatter;


  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException  {
	  
   
   


    
   
    formatter = new SimpleDateFormat("yyyyMMddHmmss");
    today = new java.util.Date();
    output = formatter.format(today);

   
    
    String foldername="", fileheader="", forwardTo="";
   

    // Get properties from oscar_mcmaster.properties
    Properties ap = OscarProperties.getInstance();
      
    forwardTo  = ap.getProperty("DOC_FORWARD");
    foldername = ap.getProperty("DOCUMENT_DIR");

    if (forwardTo == null || forwardTo.length() < 1) return;

        
        //		 Create a new file upload handler
        DiskFileUpload upload = new DiskFileUpload();

        try {
            //		 Parse the request
            List  items = upload.parseRequest(request);
//          Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    //String name = item.getFieldName();
                    //String value = item.getString(); 

                } else {
                    String pathName = item.getName();  
                    String [] fullFile = pathName.split("[/|\\\\]");
            		File savedFile = new File(foldername, output + fullFile[fullFile.length-1]);

                    fileheader = output + fullFile[fullFile.length-1];
            		
            		item.write(savedFile);
                }
            }
        } catch (FileUploadException e) {
            // TODO-legacy Auto-generated catch block
            MiscUtils.getLogger().error("Error", e);
        } catch (Exception e) {
            // TODO-legacy Auto-generated catch block
            MiscUtils.getLogger().error("Error", e);
        }
    
   
/*
    ServletInputStream sis = request.getInputStream();
    BufferedOutputStream dest = null;
    FileOutputStream fos = null;
    boolean bwri = false;
    boolean bfbo = true;
    boolean benddata = false;
    boolean bf = false;
    byte boundary[] = temp.getBytes();

    while (bf?true:((count = sis.readLine(data, 0, BUFFER)) != -1)) {
       bf = false;
       benddata = false;
       
       if(count==2 && data[0]==13 && data[1]==10) {
          enddata[0] = 13;
     	  enddata[1] = 10;
     	  for(int i=0;i<BUFFER;i++) data[i]=0;
     	     count = sis.readLine(data, 0, BUFFER);
	     if(count==2 && data[0]==13 && data[1]==10) {
	        dest.write(enddata, 0, 2);
		bf = true;
		continue;
	     } else {
		benddata = true;
	     }
	  }
     	  String s = new String(data,2,temp.length());
     	  if(temp.equals(s)) {
    	     if(benddata) break;
     	     if((c =sis.readLine(data1, 0, BUFFER)) != -1) {
     	        filename = new String(data1);
      		if(filename.length()>2 && filename.indexOf("filename")!=-1) {
	
		   filename = filename.substring(filename.lastIndexOf("filename=\"") + "filename\"".length() +1,
						  filename.lastIndexOf('"'));
	
		   filename = filename.substring(filename.lastIndexOf('\\')+1, filename.length());
		   fileheader = output +  filename;
                   fos = new FileOutputStream(foldername+ output + filename);
                   dest = new BufferedOutputStream(fos, BUFFER);
                }
       		c =sis.readLine(data2, 0, BUFFER);
     		if((c =sis.readLine(data2, 0, BUFFER)) != -1) {
       		   bwri = bfbo?true:false;
                }
     	     }
             bfbo = bfbo?false:true;
             for(int i=0;i<BUFFER;i++) data[i]=0;
     	        continue;
     	  } //end period

   	  if(benddata) {
   	     benddata = false;
             dest.write(enddata, 0, 2);
   	     for(int i=0;i<2;i++) enddata[i]=0;
   	  }
          if(bwri) {
       	     dest.write(data, 0, count);
     	     for(int i=0;i<BUFFER;i++) data[i]=0;
          }
    } //end while
    //dest.flush();
    fos.close();
    dest.close();
    sis.close();
*/
    DocumentBean documentBean = new DocumentBean();

    request.setAttribute("documentBean", documentBean);

    documentBean.setFilename(fileheader);

       //  documentBean.setFileDesc(filedesc);

       //  documentBean.setFoldername(foldername);

       //  documentBean.setFunction(function);

       //  documentBean.setFunctionID(function_id);

       //  documentBean.setCreateDate(fileheader);

       //  documentBean.setDocCreator(creator);

    // Call the output page.

    RequestDispatcher dispatch = getServletContext().getRequestDispatcher(forwardTo);
    dispatch.forward(request, response);
  }

}
