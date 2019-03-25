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


package oscar.oscarBilling.ca.bc.Teleplan;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import oscar.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author jay
 */
public class TeleplanResponse
{
	private String transactionNo;
	private String result;
	private String filename;
	private String realFilename;
	private String msgs;
	private int lineCount;

	/**
	 * Creates a new instance of TeleplanResponse
	 */
	public TeleplanResponse()
	{
		transactionNo = null;
		result = null;
		filename = null;
		realFilename = null;
		msgs = null;
		lineCount = 0;
	}

	void processResponseStream(InputStream in) throws IOException, InterruptedException
	{
		double randNum = Math.random();
		GenericFile file = FileFactory.createTempFile(in, "teleplan.msp-" + randNum);
		BufferedReader fileReader = new BufferedReader(new FileReader(file.getFileObject()));

		String line;
		String lastLine = null;
		lineCount = 0;
		while ((line = fileReader.readLine()) != null)
		{
			lineCount++;
			lastLine = line;
		}
		fileReader.close();
		processLastLine(lastLine);

		if(!StringUtils.isNullOrEmpty(this.getFilename()))
		{
			file.rename("teleplan" + this.getFilename() + randNum);
			file.moveToBillingRemittance();
			realFilename = file.getName();
		}
	}
	
    //#TID=001;Result=SUCCESS;Filename=TPBULET-I.txt;Msgs=;
    //	String str = "#TID=001;Result=SUCCESS;Filename=TPBULET-I.txt;Msgs
    private void processLastLine(String str){
        int idx = str.indexOf("Msgs=");
    	msgs = str.substring(idx+5,str.lastIndexOf(';'));
    	str = str.substring(0,idx);
    	idx = str.indexOf("Filename=");
    	filename = str.substring(idx+9,str.lastIndexOf(';'));
    	str = str.substring(0,idx);
        idx = str.indexOf("Result=");
    	result = str.substring(idx+7,str.lastIndexOf(';'));
    	str = str.substring(0,idx);
        idx = str.indexOf("#TID=");
        transactionNo = str.substring(idx+5,str.lastIndexOf(';'));
    }

	public String toString()
	{
		return "#TID=" + getTransactionNo() + ";Result=" + getResult() + ";Filename=" + getFilename() +
				";Msgs=" + getMsgs() + "; NUM LINES " + lineCount + " REALFILNAME =" + realFilename;
	}

    public String getTransactionNo() {
        return transactionNo;
    }

    public String getResult() {
        return result;
    }

    public boolean isFailure(){
        return result.equals("FAILURE");
    }
    
    public boolean isSuccess(){
        return result.equals("SUCCESS");
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getRealFilename(){
        return realFilename;
    }

    public String getMsgs() {
        return msgs;
    }

	public File getFile() throws IOException
	{
		return FileFactory.getRemittanceFile(realFilename).getFileObject();
	}
		
}
