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

package org.oscarehr.integration.myhealthaccess.service;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class DevelopmentTrustManager
{
	public static TrustManager[] trustAllCerts = new TrustManager[]{new X509ExtendedTrustManager()
	{
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
		{

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
		{

		}

		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException
		{

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException
		{

		}

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException
		{

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException
		{

		}
	}};
}
