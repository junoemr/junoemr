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

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="appointment_status")
public class AppointmentStatus extends AbstractModel<Integer> {

    public static final String APPOINTMENT_STATUS_HERE = "H";
    public static final String APPOINTMENT_STATUS_CANCELLED = "C";
	public static final String APPOINTMENT_STATUS_DAYSHEET_PRINTED = "T";
	public static final String APPOINTMENT_STATUS_NEW = "t";
	public static final String APPOINTMENT_STATUS_BILLED = "B";
	public static final String APPOINTMENT_STATUS_NO_SHOW = "N";


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String status;
	
	private String description;
	
	private String color;

	@Length(min=7, max=7)
	@Column(name="juno_color")
	private String junoColor;
	
	private String icon;
	
	private int active;
	
	/**
	 * Whether this appointment can be enabled/disabled or reordered.
	 */
	private int editable;
	
	@Column(name="short_letters")
	private String shortLetters;

	@Column(name="short_letter_colour")
	private String shortLetterColour;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getJunoColor()
	{
		return junoColor;
	}

	public void setJunoColor(String junoColor)
	{
		this.junoColor = junoColor;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public boolean isActive()
	{
		return active == 1;
	}

	public int getEditable() {
		return editable;
	}

	public void setEditable(int editable) {
		this.editable = editable;
	}

	public boolean isEditable()
	{
		return editable == 1;
	}

    public String getShortLetters() {
		return shortLetters;
	}

	public void setShortLetters(String shortLetters) {
		this.shortLetters = shortLetters;
	}

	public String getShortLetterColour() {
		return shortLetterColour;
	}

	public void setShortLetterColour(String shortLetterColour) {
		this.shortLetterColour = shortLetterColour;
	}
}
