/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.integration.iceFall.service.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallOrderHistoryTo1 implements Serializable
{
	private Integer id;
	@JsonProperty("shop_order_id")
	private String orderId;
	@JsonProperty("order_number")
	private String orderNumber;
	@JsonProperty("dtcreated")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate createDate;
	@JsonProperty("dtpaid")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate payedDate;
	@JsonProperty("dtshipped")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate shippedDate;
	@JsonProperty("order_amount")
	private Float orderAmount;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getOrderId()
	{
		return orderId;
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	public String getOrderNumber()
	{
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber)
	{
		this.orderNumber = orderNumber;
	}

	public LocalDate getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(LocalDate createDate)
	{
		this.createDate = createDate;
	}

	public LocalDate getPayedDate()
	{
		return payedDate;
	}

	public void setPayedDate(LocalDate payedDate)
	{
		this.payedDate = payedDate;
	}

	public LocalDate getShippedDate()
	{
		return shippedDate;
	}

	public void setShippedDate(LocalDate shippedDate)
	{
		this.shippedDate = shippedDate;
	}

	public Float getOrderAmount()
	{
		return orderAmount;
	}

	public void setOrderAmount(Float orderAmount)
	{
		this.orderAmount = orderAmount;
	}
}
