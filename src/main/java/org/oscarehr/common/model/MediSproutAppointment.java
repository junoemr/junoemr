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
package org.oscarehr.common.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author OSCARprn by Treatment - support@oscarprn.com
 */
@Entity
@Table(name = "medisproutappointment")
public class MediSproutAppointment extends AbstractModel<Integer> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer ID;
    
    @Column(name = "appointment_no")
    private Integer appointment_no;
    
    @Column(name = "providerUrl")
    private String providerUrl;
    
    @Column(name = "attendeesUrl")
    private String attendeesUrl;
    
    @Column(name = "code")
    private Integer code;
    
    @Column(name = "dowloadeddocs")
    private Integer dowloadeddocs;

	@Override
    public Integer getId() {
	 	return ID;
    }
    
	public void setId(Integer iD) {
    	ID = iD;
    }

	public Integer getAppointment_no() {
    	return appointment_no;
    }

	public void setAppointment_no(Integer appointment_no) {
    	this.appointment_no = appointment_no;
    }

	public String getProviderUrl() {
    	return providerUrl;
    }

	public void setProviderUrl(String providerUrl) {
    	this.providerUrl = providerUrl;
    }

	public String getAttendeesUrl() {
    	return attendeesUrl;
    }

	public void setAttendeesUrl(String attendeesUrl) {
    	this.attendeesUrl = attendeesUrl;
    }

	public Integer getCode() {
    	return code;
    }

	public void setCode(Integer code) {
    	this.code = code;
    }


	public Integer getDowloadeddocs() {
    	return dowloadeddocs;
    }


	public void setDowloadeddocs(Integer dowloadeddocs) {
    	this.dowloadeddocs = dowloadeddocs;
    }



    
}
