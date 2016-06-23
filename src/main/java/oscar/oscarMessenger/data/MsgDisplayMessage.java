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


package oscar.oscarMessenger.data;

public class MsgDisplayMessage {
    public String messageId  = null;
    public String messagePosition  = null;
    public boolean isLastMsg = false;
    public String status     = null;
    public String thesubject = null;
    public String thedate    = null;
    public String theime    = null;
    public String sentby     = null;
    public String sentto     = null;
    public String attach     = null;
    public String pdfAttach     = null;
    public String demographic_no = null;
    
    public MsgDisplayMessage() {
    	this(null, null, false, null, null, null, null, null, null, null, null, null);
    }
    public MsgDisplayMessage(String id, String pos, boolean lastMsg, String status, String subj, String date, String time, String by, String to, String demoNo) {
        this(id, pos, lastMsg, status, subj, date, time, by, to, null, null, demoNo);
    }
    public MsgDisplayMessage(String id, String pos, boolean lastMsg, String status, String subj, String date, String time, String by, String to, String attach, String pdf, String demoNo) {
        this.messageId = id;
        this.messagePosition = pos;
        this.isLastMsg = lastMsg;
        this.status = status;
        this.thesubject = subj;
        this.thedate = date;
        this.theime = time;
        this.sentby = by;
        this.sentto = to;
        this.attach = attach;
        this.pdfAttach = pdf;
        this.demographic_no = demoNo;
    }
}
