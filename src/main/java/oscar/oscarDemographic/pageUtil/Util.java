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
 * Util.java
 *
 * Created on March 7, 2009, 7:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarDemographic.pageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCalendar;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.oscarProvider.data.ProviderData;
import oscar.util.StringUtils;
import oscar.util.UtilDateUtilities;

/**
 *
 * @author Ronnie
 */
public class Util {
	static private final Logger logger = MiscUtils.getLogger();
	static private final PartialDateDao partialDateDao = (PartialDateDao)SpringUtils.getBean("partialDateDao");
    
    static public String addLine(String baseStr, String addStr) {
	return addLine(baseStr, "", addStr);
    }
    
    static public String addLine(String baseStr, String label, String addStr) {
	String newStr = StringUtils.noNull(baseStr);
	if (StringUtils.filled(newStr)) {
	    newStr += StringUtils.filled(addStr) ? "\n"+StringUtils.noNull(label)+addStr : "";
	} else {
	    newStr += StringUtils.filled(addStr) ? StringUtils.noNull(label)+addStr : "";
	}
	return newStr;
    }

    static public String addSummary(String label, String item) {
	return addSummary("", label, item);
    }

    static public String addSummary(String summary, String label, String item) {
        if (summary==null) summary = addSummary(label, item);
        if (StringUtils.empty(item)) return summary;

        if (StringUtils.filled(summary)) summary += ",";
        summary += "["+label+"]:"+item;
	
	return summary;
    }
    
    static public XmlCalendar calDate(Date inDate) {
		String date = UtilDateUtilities.DateToString(inDate, "yyyy-MM-dd");
		String time = UtilDateUtilities.DateToString(inDate, "HH:mm:ss");
		try {
			XmlCalendar x = new XmlCalendar(date+"T"+time);
			return x;
		} catch (Exception ex) {
			XmlCalendar x = new XmlCalendar("0001-01-01T00:00:00");
			return x;
		}
    }

	static public XmlCalendar calDate(String inDate) {
		Date dateTime = UtilDateUtilities.StringToDate(inDate,"yyyy-MM-dd HH:mm:ss");
		if (dateTime==null) {
			dateTime = UtilDateUtilities.StringToDate(inDate,"yyyy-MM-dd");
		}
		if (dateTime==null) { //inDate may contain time only
			try {
				XmlCalendar x = new XmlCalendar(inDate);
				return x;
			} catch (Exception ex) { //inDate format is wrong
				XmlCalendar x = new XmlCalendar("0001-01-01T00:00:00");
				return x;
			}
		} else {
			return calDate(dateTime);
		}
	}

    static public boolean checkDir(String dirName) throws Exception {
        dirName = fixDirName(dirName);
        try {
            Runtime rt = Runtime.getRuntime();
            String[] env = {""};
            File dir = new File(dirName);
            Process proc = rt.exec("touch null.tmp", env, dir);
            int ecode = proc.waitFor();
            if (ecode == 0) {
                cleanFile("null.tmp", dirName);
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {logger.error("Error", ex);
        }
        logger.error("Error! Cannot write to directory [" + dirName + "]");
        return false;
    }

    static public boolean cleanFile(String filename, String dirname) {
        dirname = fixDirName(dirname);
	File f = new File(dirname+filename);
	return cleanFile(f);
    }

    static public boolean cleanFile(String filename) {
	File f = new File(filename);
	return cleanFile(f);
    }

    static public boolean cleanFile(File file) {
	if (!file.delete()) {
		logger.error("Error! Cannot delete file ["+file.getPath()+"]");
            return false;
        }
        return true;
    }
    
    static public void cleanFiles(ArrayList<File> files) {
        for (File file : files) {
            cleanFile(file);
        }
    }

    static public boolean convert10toboolean(String s){
	Boolean ret = false;
	if ( s!= null && s.trim().equals("1") ){
	    ret = true;
	}
	return ret;
    }

	static public boolean downloadFile(String fileName, String dirName, HttpServletResponse rsp) {
		try {
			dirName = fixDirName(dirName);
			if (rsp == null) return false;

			rsp.setContentType("application/octet-stream");
			rsp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			InputStream in = new FileInputStream(dirName + fileName);
			OutputStream out = rsp.getOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		}
		catch (IOException ex) {
			logger.error("Error", ex);
			return false;
		}
	}

    static public String fixDirName(String dirName) {
        if (StringUtils.filled(dirName)) {
            if (dirName.charAt(dirName.length()-1)!='/') dirName = dirName + '/';
        }
        return StringUtils.noNull(dirName);
    }
    
    /**
     * get a map of key-value pairings for converting mimeType to a file extension string
     * @return HashMap of key-value string pairings
     */
    static private HashMap<String, String> getTypeExtensions() {
		HashMap<String, String> type_ext = new HashMap<String, String>();
		
		/* 
		 * Map all the values to extension type. 
		 * the previous system always took the first value with a matching key, but contained multiples.
		 * Here those values are commented out instead, as a map will always use the last value added with duplicate keys.
		 */
		type_ext.put("application/envoy", "evy");
		type_ext.put("application/fractals", "fif");
		type_ext.put("application/futuresplash", "spl");
		type_ext.put("application/hta", "hta");
		type_ext.put("application/internet-property-stream", "acx");
		type_ext.put("application/mac-binhex40", "hqx");
		type_ext.put("application/msword", "doc");
		//type_ext.put("application/msword", "dot");
		type_ext.put("application/octet-stream", "bin");
		//type_ext.put("application/octet-stream", "class");
		//type_ext.put("application/octet-stream", "dms");
		//type_ext.put("application/octet-stream", "exe");
		//type_ext.put("application/octet-stream", "lha");
		//type_ext.put("application/octet-stream", "lzh");
		type_ext.put("application/oda", "oda");
		type_ext.put("application/olescript", "axs");
		type_ext.put("application/pdf", "pdf");
		type_ext.put("application/pics-rules", "prf");
		type_ext.put("application/pkcs10", "p10");
		type_ext.put("application/pkix-crl", "crl");
		type_ext.put("application/postscript", "ai");
		//type_ext.put("application/postscript", "eps");
		//type_ext.put("application/postscript", "ps");
		type_ext.put("application/rtf", "rtf");
		type_ext.put("application/set-payment-initiation", "setpay");
		type_ext.put("application/set-registration-initiation", "setreg");
		type_ext.put("application/vnd.ms-excel", "xla");
		//type_ext.put("application/vnd.ms-excel", "xlc");
		//type_ext.put("application/vnd.ms-excel", "xlm");
		//type_ext.put("application/vnd.ms-excel", "xls");
		//type_ext.put("application/vnd.ms-excel", "xlt");
		type_ext.put("application/vnd.ms-excel", "xlw");
		type_ext.put("application/vnd.ms-outlook", "msg");
		type_ext.put("application/vnd.ms-pkicertstore", "sst");
		type_ext.put("application/vnd.ms-pkiseccat", "cat");
		type_ext.put("application/vnd.ms-pkistl", "stl");
		type_ext.put("application/vnd.ms-powerpoint", "pot");
		//type_ext.put("application/vnd.ms-powerpoint", "pps");
		//type_ext.put("application/vnd.ms-powerpoint", "ppt");
		type_ext.put("application/vnd.ms-project", "mpp");
		type_ext.put("application/vnd.ms-works", "wcm");
		//type_ext.put("application/vnd.ms-works", "wdb");
		//type_ext.put("application/vnd.ms-works", "wks");
		//type_ext.put("application/vnd.ms-works", "wps");
		type_ext.put("application/winhlp", "hlp");
		type_ext.put("application/x-bcpio", "bcpio");
		type_ext.put("application/x-cdf", "cdf");
		type_ext.put("application/x-compress", "z");
		type_ext.put("application/x-compressed", "tgz");
		type_ext.put("application/x-cpio", "cpio");
		type_ext.put("application/x-csh", "csh");
		type_ext.put("application/x-director", "dcr");
		//type_ext.put("application/x-director", "dir");
		//type_ext.put("application/x-director", "dxr");
		type_ext.put("application/x-dvi", "dvi");
		type_ext.put("application/x-gtar", "gtar");
		type_ext.put("application/x-gzip", "gz");
		type_ext.put("application/x-hdf", "hdf");
		type_ext.put("application/x-internet-signup", "ins");
		//type_ext.put("application/x-internet-signup", "isp");
		type_ext.put("application/x-iphone", "iii");
		type_ext.put("application/x-javascript", "js");
		type_ext.put("application/x-latex", "latex");
		type_ext.put("application/x-msaccess", "mdb");
		type_ext.put("application/x-mscardfile", "crd");
		type_ext.put("application/x-msclip", "clp");
		type_ext.put("application/x-msdownload", "dll");
		type_ext.put("application/x-msmediaview", "m13");
		//type_ext.put("application/x-msmediaview", "m14");
		//type_ext.put("application/x-msmediaview", "mvb");
		type_ext.put("application/x-msmetafile", "wmf");
		type_ext.put("application/x-msmoney", "mny");
		type_ext.put("application/x-mspublisher", "pub");
		type_ext.put("application/x-msschedule", "scd");
		type_ext.put("application/x-msterminal", "trm");
		type_ext.put("application/x-mswrite", "wri");
		type_ext.put("application/x-netcdf", "cdf");
		//type_ext.put("application/x-netcdf", "nc");
		type_ext.put("application/x-perfmon", "pma");
		//type_ext.put("application/x-perfmon", "pmc");
		//type_ext.put("application/x-perfmon", "pml");
		//type_ext.put("application/x-perfmon", "pmr");
		//type_ext.put("application/x-perfmon", "pmw");
		type_ext.put("application/x-pkcs12", "p12");
		//type_ext.put("application/x-pkcs12", "pfx");
		type_ext.put("application/x-pkcs7-certificates", "p7b");
		//type_ext.put("application/x-pkcs7-certificates", "spc");
		type_ext.put("application/x-pkcs7-certreqresp", "p7r");
		type_ext.put("application/x-pkcs7-mime", "p7c");
		//type_ext.put("application/x-pkcs7-mime", "p7m");
		type_ext.put("application/x-pkcs7-signature", "p7s");
		type_ext.put("application/x-sh", "sh");
		type_ext.put("application/x-shar", "shar");
		type_ext.put("application/x-shockwave-flash", "swf");
		type_ext.put("application/x-stuffit", "sit");
		type_ext.put("application/x-sv4cpio", "sv4cpio");
		type_ext.put("application/x-sv4crc", "sv4crc");
		type_ext.put("application/x-tar", "tar");
		type_ext.put("application/x-tcl", "tcl");
		type_ext.put("application/x-tex", "tex");
		type_ext.put("application/x-texinfo", "texi");
		//type_ext.put("application/x-texinfo", "texinfo");
		type_ext.put("application/x-troff", "roff");
		//type_ext.put("application/x-troff", "t");
		//type_ext.put("application/x-troff", "tr");
		type_ext.put("application/x-troff-man", "man");
		type_ext.put("application/x-troff-me", "me");
		//type_ext.put("application/x-troff-ms", "ms");
		type_ext.put("application/x-ustar", "ustar");
		type_ext.put("application/x-wais-source", "src");
		type_ext.put("application/x-x509-ca-cert", "cer");
		//type_ext.put("application/x-x509-ca-cert", "crt");
		//type_ext.put("application/x-x509-ca-cert", "der");
		type_ext.put("application/ynd.ms-pkipko", "pko");
		type_ext.put("application/zip", "zip");
		type_ext.put("audio/basic", "au");
		//type_ext.put("audio/basic", "snd");
		type_ext.put("audio/mid", "mid");
		//type_ext.put("audio/mid", "rmi");
		type_ext.put("audio/mpeg", "mp3");
		type_ext.put("audio/x-aiff", "aif");
		//type_ext.put("audio/x-aiff", "aifc");
		//type_ext.put("audio/x-aiff", "aiff");
		type_ext.put("audio/x-mpegurl", "m3u");
		type_ext.put("audio/x-pn-realaudio", "ra");
		//type_ext.put("audio/x-pn-realaudio", "ram");
		type_ext.put("audio/x-wav", "wav");
		type_ext.put("image/png", "png");
		type_ext.put("image/bmp", "bmp");
		type_ext.put("image/cis-cod", "cod");
		type_ext.put("image/gif", "gif");
		type_ext.put("image/ief", "ief");
		type_ext.put("image/jpeg", "jpe");
		//type_ext.put("image/jpeg", "jpeg");
		//type_ext.put("image/jpeg", "jpg");
		type_ext.put("image/pipeg", "jfif");
		type_ext.put("image/svg+xml", "svg");
		type_ext.put("image/tiff", "tif");
		//type_ext.put("image/tiff", "tiff");
		type_ext.put("image/x-cmu-raster", "ras");
		type_ext.put("image/x-cmx", "cmx");
		type_ext.put("image/x-icon", "ico");
		type_ext.put("image/x-portable-anymap", "pnm");
		type_ext.put("image/x-portable-bitmap", "pbm");
		type_ext.put("image/x-portable-graymap", "pgm");
		type_ext.put("image/x-portable-pixmap", "ppm");
		type_ext.put("image/x-rgb", "rgb");
		type_ext.put("image/x-xbitmap", "xbm");
		type_ext.put("image/x-xpixmap", "xpm");
		type_ext.put("image/x-xwindowdump", "xwd");
		type_ext.put("message/rfc822", "mht");
		//type_ext.put("message/rfc822", "mhtml");
		//type_ext.put("message/rfc822", "nws");
		type_ext.put("text/css", "css");
		type_ext.put("text/h323", "323");
		type_ext.put("text/html", "htm");
		//type_ext.put("text/html", "html");
		//type_ext.put("text/html", "stm");
		type_ext.put("text/iuls", "uls");
		type_ext.put("text/plain", "bas");
		//type_ext.put("text/plain", "c");
		//type_ext.put("text/plain", "h");
		//type_ext.put("text/plain", "txt");
		type_ext.put("text/richtext", "rtx");
		type_ext.put("text/scriptlet", "sct");
		type_ext.put("text/tab-separated-values", "tsv");
		type_ext.put("text/webviewhtml", "htt");
		type_ext.put("text/x-component", "htc");
		type_ext.put("text/x-setext", "etx");
		type_ext.put("text/x-vcard", "vcf");
		type_ext.put("video/mpeg", "mp2");
		//type_ext.put("video/mpeg", "mpa");
		//type_ext.put("video/mpeg", "mpe");
		//type_ext.put("video/mpeg", "mpeg");
		//type_ext.put("video/mpeg", "mpg");
		//type_ext.put("video/mpeg", "mpv2");
		type_ext.put("video/quicktime", "mov");
		//type_ext.put("video/quicktime", "qt");
		type_ext.put("video/x-la-asf", "lsf");
		//type_ext.put("video/x-la-asf", "lsx");
		type_ext.put("video/x-ms-asf", "asf");
		//type_ext.put("video/x-ms-asf", "asr");
		//type_ext.put("video/x-ms-asf", "asx");
		type_ext.put("video/x-msvideo", "avi");
		type_ext.put("video/x-sgi-movie", "movie");
		type_ext.put("x-world/x-vrml", "flr");
		//type_ext.put("x-world/x-vrml", "vrml");
		//type_ext.put("x-world/x-vrml", "wrl");
		//type_ext.put("x-world/x-vrml", "wrz");
		//type_ext.put("x-world/x-vrml", "xaf");
		//type_ext.put("x-world/x-vrml", "xof");
		
		/* Additional values (oscarhost added) */
		type_ext.put("txt", "txt");
		
		return type_ext;
    }
    static public String mimeToExt(String mimeType) {
		String ret = "";
		if (!StringUtils.filled(mimeType)) return ret;
		if (mimeType.charAt(0)=='.') return mimeType;
		
		HashMap<String, String> type_ext = getTypeExtensions();

		// return value associated with key
		String key = mimeType.toLowerCase();
		if (type_ext.containsKey(key)) {
			ret = "." + type_ext.get(key);
		}
		// sometimes the value is already just the extension without the '.' (ie 'PDF'), so check the values too
		else if (type_ext.containsValue(key)) {
			ret = "." + key;
		}
		return ret;
    }

    static public cdsDt.HealthCardProvinceCode.Enum setProvinceCode(String provinceCode) {
	provinceCode = setCountrySubDivCode(provinceCode);
	if (provinceCode.equals("US")) return cdsDt.HealthCardProvinceCode.X_50; //Not available, temporarily
	if (provinceCode.equals("non-Canada/US")) return cdsDt.HealthCardProvinceCode.X_90; //Not applicable
	return cdsDt.HealthCardProvinceCode.Enum.forString(provinceCode);
    }
	
    static public String setCountrySubDivCode(String countrySubDivCode) {
	countrySubDivCode = countrySubDivCode.trim();
	if (StringUtils.filled(countrySubDivCode)) {
	    if (countrySubDivCode.equals("OT")) return "Other";
	    if (!countrySubDivCode.startsWith("US")) return "CA-"+countrySubDivCode;
	}
	return "";
    }

    static public String onlyNum(String s) {
        if (s==null) return null;

        for (int i=0; i<s.length(); i++) {
            if (!"0123456789".contains(s.substring(i, i+1))) {
                s = s.substring(0,i)+s.substring(i+1,s.length());
                i--;
            }
        }
        return s;
    }

    static public String leadingNum(String s) {
    	if (s==null) return "";
        s = s.trim();
        for (int i=0; i<s.length(); i++) {
            if (!".0123456789".contains(s.substring(i,i+1))) {
                s = s.substring(0, i);
                break;
            }
        }
        return s;
    }

    static public float leadingNumF(String s) {
    	s = leadingNum(s);
        try {
        	float f = Float.parseFloat(s);
        	return f;
        } catch(NumberFormatException e) {
        	return 0;
        }       
    }

    static public String trailingTxt(String s) {
        s = s.trim();
        for (int i=0; i<s.length(); i++) {
            if (!".0123456789".contains(s.substring(i,i+1))) {
                s = s.substring(i);
                break;
            }
        }
        return s.trim();
    }

    
    static public void writeNameSimple(cdsDt.PersonNameSimpleWithMiddleName personName, String firstName, String lastName) {
	if (!StringUtils.filled(firstName)) firstName = "";
	if (!StringUtils.filled(lastName))  lastName = "";
	personName.setFirstName(firstName);
	personName.setLastName(lastName);
    }
    
    static public void writeNameSimple(cdsDt.PersonNameSimple personName, String firstName, String lastName) {
	if (!StringUtils.filled(firstName)) firstName = "";
	if (!StringUtils.filled(lastName))  lastName = "";
	personName.setFirstName(firstName);
	personName.setLastName(lastName);
    }
    
    static public void writeNameSimple(cdsDt.PersonNameSimple personName, String name) {
	if (!StringUtils.filled(name)) name = "";
        if (name.contains(",")) {
            String[] names = name.split(",");
            String firstName = "";
            for (int i=1; i<names.length; i++) firstName += StringUtils.filled(firstName) ? ","+names[i] : names[i];
            personName.setFirstName(firstName);
            personName.setLastName(names[0]);
        } else if (name.contains(" ")) {
            String[] names = name.split(" ");
            String firstName = "";
            for (int i=0; i<names.length-1; i++) firstName += StringUtils.filled(firstName) ? " "+names[i] : names[i];
            personName.setFirstName(firstName);
            personName.setLastName(names[names.length-1]);
        } else {
	    personName.setFirstName(name);
	    personName.setLastName("");
	}
    }
    
    static public cdsDt.YnIndicatorsimple.Enum yn(String YorN) {
	YorN = YorN.trim().toLowerCase();
	if (YorN.equals("yes") || YorN.equals("y")) return cdsDt.YnIndicatorsimple.Y;
	else return cdsDt.YnIndicatorsimple.N;
    }
    
    static public boolean zipFiles(ArrayList<File> files, String zipFileName, String dirName) throws Exception {
        try {
            if (files == null) {
            	logger.error("Error! No source file for zipping");
                return false;
            }
            if (!StringUtils.filled(zipFileName)) {
            	logger.error("Error! Zip filename not given");
                return false;
            }
            if (!checkDir(dirName)) {
                return false;
            }
            dirName = fixDirName(dirName);
            byte[] buf = new byte[1024];
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(dirName + zipFileName));
            for (File f : files) {
                if (f == null) continue;

                FileInputStream fin = new FileInputStream(f.getAbsolutePath());

                // Add ZIP entry to output stream
                zout.putNextEntry(new ZipEntry(f.getName()));

                // Transfer bytes from the input files to the ZIP file
                int len;
                while ((len = fin.read(buf)) > 0) {
                    zout.write(buf, 0, len);
                }

                // Complete the entry
                zout.closeEntry();
                fin.close();
            }
            // Complete the ZIP file
            zout.close();
            return true;

        } catch (IOException ex) {logger.error("Error", ex);
        }
        return false;
    }

    static public void putPartialDate(cdsDt.DateFullOrPartial dfp, CaseManagementNoteExt cme) {
    	putPartialDate(dfp, cme.getDateValue(), cme.getValue());
    }

    static public void putPartialDate(cdsDt.DateFullOrPartial dfp, Date dateValue, Integer tableName, Integer tableId, Integer fieldName) {
    	PartialDate pd = partialDateDao.getPartialDate(tableName, tableId, fieldName);
    	putPartialDate(dfp, dateValue, pd.getFormat());
    }

    static public void putPartialDate(cdsDt.DateFullOrPartial dfp, Date dateValue, String format) {
        if (dateValue!=null) {
            if (PartialDate.FORMAT_YEAR_ONLY.equals(format)) dfp.setYearOnly(calDate(dateValue));
            else if (PartialDate.FORMAT_YEAR_MONTH.equals(format)) dfp.setYearMonth(calDate(dateValue));
            else dfp.setFullDate(calDate(dateValue));
        }
    }

    static public void putPartialDate(cdsDt.DateTimeFullOrPartial dfp, Date dateValue, String format) {
        if (dateValue!=null) {
            if (PartialDate.FORMAT_YEAR_ONLY.equals(format)) dfp.setYearOnly(calDate(dateValue));
            else if (PartialDate.FORMAT_YEAR_MONTH.equals(format)) dfp.setYearMonth(calDate(dateValue));
            else dfp.setFullDate(calDate(dateValue));
        }
    }

    static public void putPartialDate(cdsDtCihi.DateFullOrPartial dfp, CaseManagementNoteExt cme) {
    	if (cme!=null) putPartialDate(dfp, cme.getDateValue(), cme.getValue());
    }

    static public void putPartialDate(cdsDtCihi.DateFullOrPartial dfp, Date dateValue, Integer tableName, Integer tableId, Integer fieldName) {
    	String format = null;
    	PartialDate pd = partialDateDao.getPartialDate(tableName, tableId, fieldName);
    	if (pd!=null) format = pd.getFormat();
    	putPartialDate(dfp, dateValue, format);
    }

    static public void putPartialDate(cdsDtCihi.DateFullOrPartial dfp, Date dateValue, String format) {
        if (dateValue!=null) {
            if (PartialDate.FORMAT_YEAR_ONLY.equals(format)) dfp.setYearOnly(calDate(dateValue));
            else if (PartialDate.FORMAT_YEAR_MONTH.equals(format)) dfp.setYearMonth(calDate(dateValue));
            else dfp.setFullDate(calDate(dateValue));
        }
    }
    
    static public String readPartialDate(CaseManagementNoteExt cme) {
        String type = cme.getValue();
        String val = null;

        if (StringUtils.filled(type)) {
            if (type.equals(PartialDate.FORMAT_YEAR_ONLY))
                val = oscar.util.UtilDateUtilities.DateToString(cme.getDateValue(),"yyyy");
            else if (type.equals(PartialDate.FORMAT_YEAR_MONTH))
                val = oscar.util.UtilDateUtilities.DateToString(cme.getDateValue(),"yyyy-MM");
            else val = oscar.util.UtilDateUtilities.DateToString(cme.getDateValue(),"yyyy-MM-dd");
        } else {
            val = oscar.util.UtilDateUtilities.DateToString(cme.getDateValue(),"yyyy-MM-dd");
        }
        return val;
    }
    
    static public String formatIcd9(String code) {
        if (StringUtils.empty(code)) return code;
        if (code.length()<=3) return code;

        String codeFormatted = code.substring(0,3);
        code = code.substring(3);
        while (code.length()>3) {
            codeFormatted += "."+code.substring(0,3);
            code = code.substring(3);
        }
        if (code.length()>0) codeFormatted += "."+code;

        return codeFormatted;
    }
    
    static public SpokenLangProperties spokenLangProperties = SpokenLangProperties.getInstance();

    static public String convertLanguageToCode(String lang) {
    	return spokenLangProperties.getCodeByLang(lang);
    }

    static public String convertCodeToLanguage(String code) {
    	return spokenLangProperties.getLangByCode(code);
    }

	static private final String verified = "[Verified and Signed";
	
    static public boolean isVerified(CaseManagementNote cmn) {
    	if (cmn==null) return false;
    	
    	String note = cmn.getNote();
    	if (StringUtils.empty(note)) return false;
    	if (!note.contains("\n")) return false;
    	
    	int marker = note.substring(0, note.lastIndexOf("\n")).lastIndexOf("\n");
    	if (marker<0) return false;
    	
    	String vtext = verified;
    	if (cmn.getUpdate_date()!=null) vtext += " on "+UtilDateUtilities.DateToString(cmn.getUpdate_date(), "dd-MMM-yyyy HH:mm");
    	String providerName = ProviderData.getProviderName(cmn.getProviderNo());
    	if (StringUtils.filled(providerName)) vtext += " by "+providerName;

    	return note.substring(marker+1).startsWith(vtext);
    }
    
    static public void writeVerified(CaseManagementNote cmn) {
    	if (isVerified(cmn)) return;
    	
    	String note = cmn.getNote();
    	if (StringUtils.empty(note)) return;
    	
    	String vtext = verified;
    	if (cmn.getUpdate_date()!=null) vtext += " on "+UtilDateUtilities.DateToString(cmn.getUpdate_date(), "dd-MMM-yyyy HH:mm");
    	String providerName = ProviderData.getProviderName(cmn.getProviderNo());
    	if (StringUtils.filled(providerName)) vtext += " by "+providerName;
    	
    	note = Util.addLine(note, "\n"+vtext+"]\n");
    	cmn.setNote(note);
    }
    
    static private HashMap<String,String> preventionToImmunizationType = new HashMap<String,String>(); 
    static private HashMap<String,String> immunizationToPreventionType = new HashMap<String,String>();
    static private ArrayList<String> nonImmunizationPreventionType = new ArrayList<String>();
    
    static private void setPreventionTypes() {
    	if (!preventionToImmunizationType.isEmpty() && !immunizationToPreventionType.isEmpty() && !nonImmunizationPreventionType.isEmpty()) return;
    	
        PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
        ArrayList<HashMap<String,String>> prevTypeList = pdc.getPreventions();
        
        for (HashMap<String,String> prevTypeHash : prevTypeList) {
            if (prevTypeHash != null && StringUtils.filled(prevTypeHash.get("layout"))) {
            	if (prevTypeHash.get("layout").equals("injection")) {
	            	preventionToImmunizationType.put(prevTypeHash.get("name"), prevTypeHash.get("healthCanadaType"));
	            	immunizationToPreventionType.put(prevTypeHash.get("healthCanadaType"), prevTypeHash.get("name"));
            	} else {
            		nonImmunizationPreventionType.add(prevTypeHash.get("name"));
            	}
            }
        }
    }
    
    static public String getImmunizationType(String preventionType) {
    	if (preventionToImmunizationType.isEmpty()) setPreventionTypes();
    	return preventionToImmunizationType.get(preventionType);
    }
    
    static public String getPreventionType(String immunizationType) {
    	if (immunizationToPreventionType.isEmpty()) setPreventionTypes();
    	return immunizationToPreventionType.get(immunizationType);
    }

    static public boolean isNonImmunizationPrevention(String type) {
    	if (nonImmunizationPreventionType.isEmpty()) setPreventionTypes();
    	return nonImmunizationPreventionType.contains(type);
    }
    
    static public String replaceTags(String s) {
    	if (StringUtils.empty(s)) return s;
    	
    	String tag = "<br />";
    	s = s.replace(tag, "\n");
    	tag = "<br/>";
    	s = s.replace(tag, "\n");
    	tag = "<br>";
    	s = s.replace(tag, "\n");
    	tag = "&nbsp;";
    	s = s.replace(tag, " ");
    	
    	return s;
    }
}
