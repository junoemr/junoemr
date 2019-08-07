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

package org.oscarehr.log.model;

import org.oscarehr.common.model.AbstractModel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "log_ws_rest")
public class RestServiceLog extends AbstractModel<Long> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();
	@Column(name = "duration_ms")
	private Long duration = 0L;
	@Column(name = "provider_no")
	private String providerNo;
	@Column(name = "ip")
	private String ip;
	@Column(name = "user_agent")
	private String userAgent;
	@Column(name = "url")
	private String url;
	@Column(name = "request_media_type")
	private String requestMediaType;
	@Column(name = "method")
	private String method;
	@Column(name = "raw_query_string")
	private String rawQueryString;
	@Column(name = "raw_post")
	private String rawPost;
	@Column(name = "status_code")
	private Integer statusCode;
	@Column(name = "response_media_type")
	private String responseMediaType;
	@Column(name = "raw_output")
	private String rawOutput;
	@Column(name = "error_message")
	private String errorMessage;
	
	@PreRemove
	protected void jpaPreventDelete() {
		throw (new UnsupportedOperationException("Remove is not allowed for this type of item."));
	}

	@PreUpdate
	protected void jpaPreventUpdate() {
		throw (new UnsupportedOperationException("Update is not allowed for this type of item."));
	}
	
	@Override
	public Long getId() {
		return id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the request duration in milliseconds
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * @param duration - duration in milliseconds
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getProviderNo() {
		return providerNo;
	}

	public void setProviderNo(String providerNo) {
		this.providerNo = providerNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestMediaType()
	{
		return requestMediaType;
	}

	public void setRequestMediaType(String requestMediaType)
	{
		this.requestMediaType = requestMediaType;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRawQueryString() {
		return rawQueryString;
	}

	public void setRawQueryString(String rawQueryString) {
		this.rawQueryString = rawQueryString;
	}

	public String getRawPost() {
		return rawPost;
	}

	public void setRawPost(String rawPost) {
		this.rawPost = rawPost;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public Integer getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(Integer statusCode)
	{
		this.statusCode = statusCode;
	}

	public String getResponseMediaType()
	{
		return responseMediaType;
	}

	public void setResponseMediaType(String responseMediaType)
	{
		this.responseMediaType = responseMediaType;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
