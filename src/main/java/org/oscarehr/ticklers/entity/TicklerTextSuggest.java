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

package org.oscarehr.ticklers.entity;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.*;
/**
 *
 * @author mweston4
 */

@Entity
@Table(name="tickler_text_suggest")
public class TicklerTextSuggest extends AbstractModel<Integer>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
    @Column(name="creator")
    private String creator;
    
    @Column(name="suggested_text")
    private String suggestedText;
    
    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    
    @Column(name="active")
    private Boolean active = true;        
    
    @Override
    public Integer getId() {
        return this.id;
    }
        
    public void setId(Integer id) {
		this.id = id;
	}

	public String getCreator() {
        return this.creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getSuggestedText() {
        return this.suggestedText;
    }
    
    public void setSuggestedText(String suggestedText) {
        this.suggestedText = suggestedText;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    public Boolean getActive() {
        return this.active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
} 
