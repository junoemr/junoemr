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

package org.oscarehr.config;

import io.prometheus.client.exporter.MetricsServlet;
import io.swagger.v3.jaxrs2.integration.OpenApiServlet;
import org.oscarehr.PMmodule.exporter.DATISExporterServlet;
import org.oscarehr.PMmodule.exporter.TestServlet;
import org.oscarehr.PMmodule.notification.EmailTriggerServlet;
import org.oscarehr.common.printing.HtmlToPdfServlet;
import org.oscarehr.ui.servlet.ContentRenderingServlet;
import org.oscarehr.ui.servlet.ImageRenderingServlet;
import org.oscarehr.web.eform.EformViewForPdfGenerationServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import oscar.DocumentUploadServlet;
import oscar.eform.util.EFormImageViewForPdfGenerationServlet;
import oscar.eform.util.EFormPDFServlet;
import oscar.eform.util.EFormSignatureViewForPdfGenerationServlet;
import oscar.eform.util.EFormViewForPdfGenerationServlet;
import oscar.form.pdfservlet.FrmCustomedPDFServlet;
import oscar.form.pdfservlet.FrmPDFServlet;
import oscar.oscarBilling.ca.bc.MSP.DocumentTeleplanReportUploadServlet;
import oscar.oscarEncounter.oscarMeasurements.pageUtil.ScatterPlotChartServlet;
import oscar.oscarReport.data.PatientListByAppt;
import oscar.oscarReport.pageUtil.RptDownloadCSVServlet;
import oscar.util.BackupDownload;
import oscar.util.GenericDownload;
import oscar.util.OscarDownload;

@Configuration
public class ServletConfig
{
	/*
	<!-- Swagger -->
	<servlet>
		<servlet-name>OpenApi</servlet-name>
		<servlet-class>io.swagger.v3.jaxrs2.integration.OpenApiServlet</servlet-class>

		<init-param>
			<param-name>openApi.configuration.resourcePackages</param-name>
			<param-value>org.oscarehr.ws.external.rest.v1</param-value>
		</init-param>

		<!-- alternatively include a file openapi.json or openapi.yaml / openapi-configuration.json or openapi-configuration.yaml in classpath -->

		<!-- alternatively include a configuration file in the location specified below -->
		<!--
		  <init-param>
			<param-name>openApi.configuration.location</param-name>
			<param-value>/openapi-configuration.json</param-value>
		  </init-param>
		-->
	</servlet>
	<servlet-mapping>
		<servlet-name>OpenApi</servlet-name>
		<url-pattern>/openapi/*</url-pattern>
	</servlet-mapping>
	 */
	@Bean
	public ServletRegistrationBean<OpenApiServlet> registerOpenApiServlet()
	{
		ServletRegistrationBean<OpenApiServlet> bean = new ServletRegistrationBean<>(new OpenApiServlet());
		bean.addUrlMappings("/openapi/*");
		bean.addInitParameter("openApi.configuration.resourcePackages", "org.oscarehr.ws.external.rest.v1");

		return bean;
	}




	/*
	<servlet>
		<servlet-name>InternalOpenApi.Schedule</servlet-name>
		<servlet-class>io.swagger.v3.jaxrs2.integration.OpenApiServlet</servlet-class>

		<init-param>
			<param-name>openApi.configuration.resourceClasses</param-name>
			<param-value>org.oscarehr.ws.rest.ScheduleService</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>InternalOpenApi.Schedule</servlet-name>
		<url-pattern>/openapi/internal/schedule/*</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: I don't think this is used anywhere
	@Bean
	public ServletRegistrationBean<OpenApiServlet> registerOpenApiScheduleServlet()
	{
		ServletRegistrationBean<OpenApiServlet> bean = new ServletRegistrationBean<>(new OpenApiServlet());
		bean.addUrlMappings("/openapi/internal/schedule/*");
		bean.addInitParameter("openApi.configuration.resourcePackages", "org.oscarehr.ws.external.rest.ScheduleService");

		return bean;
	}

	/*
    <servlet>
        <servlet-name>prometheusexporter</servlet-name>
        <servlet-class>io.prometheus.client.exporter.MetricsServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>prometheusexporter</servlet-name>
        <url-pattern>/prometheus/*</url-pattern>
    </servlet-mapping>
	 */
	@Bean
	public ServletRegistrationBean<MetricsServlet> registerPrometheusServlet()
	{
		ServletRegistrationBean<MetricsServlet> bean = new ServletRegistrationBean<>(new MetricsServlet());
		bean.addUrlMappings("/prometheus/*");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>Html2Pdf</servlet-name>
		<servlet-class> org.oscarehr.common.printing.HtmlToPdfServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>Html2Pdf</servlet-name>
		<url-pattern>/html2pdf</url-pattern>
	</servlet-mapping>
	 */
	private static final int SERVLET_ORDER_HTML2PDF = 1;
	@Bean
	public ServletRegistrationBean<HtmlToPdfServlet> registerHtml2PdfServlet()
	{
		ServletRegistrationBean<HtmlToPdfServlet> bean = new ServletRegistrationBean<>(new HtmlToPdfServlet());
		bean.addUrlMappings("/html2pdf");
		bean.setLoadOnStartup(SERVLET_ORDER_HTML2PDF);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>UploadDocument</servlet-name>
		<servlet-class>oscar.DocumentMgtUploadServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>UploadDocument</servlet-name>
		<url-pattern>/servlet/oscar.DocumentMgtUploadServlet</url-pattern>
	</servlet-mapping>
	 */
	// XXX: This might not be used.  I'm going to leave it here anyway.
	private static final int SERVLET_ORDER_UPLOAD_DOCUMENT = 0;
	@Bean
	public ServletRegistrationBean<DocumentUploadServlet> registerUploadDocumentServlet()
	{
		ServletRegistrationBean<DocumentUploadServlet> bean = new ServletRegistrationBean<>(new DocumentUploadServlet());
		bean.addUrlMappings("/servlet/oscar.DocumentMgtUploadServlet");
		bean.setLoadOnStartup(SERVLET_ORDER_UPLOAD_DOCUMENT);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>pdfCreator</servlet-name>
		<servlet-class>oscar.form.pdfservlet.FrmPDFServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>pdfCreator</servlet-name>
		<url-pattern>/form/createpdf</url-pattern>
	</servlet-mapping>
	 */
	private static final int SERVLET_ORDER_PDF_CREATOR = 1;
	@Bean
	public ServletRegistrationBean<FrmPDFServlet> registerPdfCreatorServlet()
	{
		ServletRegistrationBean<FrmPDFServlet> bean = new ServletRegistrationBean<>(new FrmPDFServlet());
		bean.addUrlMappings("/form/createpdf");
		bean.setLoadOnStartup(SERVLET_ORDER_PDF_CREATOR);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>pdfCustomedCreator</servlet-name>
		<servlet-class>oscar.form.pdfservlet.FrmCustomedPDFServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>pdfCustomedCreator</servlet-name>
		<url-pattern>/form/createcustomedpdf</url-pattern>
	</servlet-mapping>
	 */
	private static final int SERVLET_ORDER_PDF_CUSTOMED_CREATOR = 1;
	@Bean
	@DependsOn("springUtils")
	public ServletRegistrationBean<FrmCustomedPDFServlet> registerPdfCustomedCreatorServlet()
	{
		ServletRegistrationBean<FrmCustomedPDFServlet> bean = new ServletRegistrationBean<>(new FrmCustomedPDFServlet());
		bean.addUrlMappings("/form/createcustomedpdf");
		bean.addInitParameter("", "");
		bean.setLoadOnStartup(SERVLET_ORDER_PDF_CUSTOMED_CREATOR);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>pdfCreatorEform</servlet-name>
		<servlet-class>oscar.eform.util.EFormPDFServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>pdfCreatorEform</servlet-name>
		<url-pattern>/eform/createpdf</url-pattern>
	</servlet-mapping>
	 */
	private static final int SERVLET_ORDER_PDF_CREATOR_EFORM = 1;
	@Bean
	public ServletRegistrationBean<EFormPDFServlet> registerPdfCreatorEformServlet()
	{
		ServletRegistrationBean<EFormPDFServlet> bean = new ServletRegistrationBean<>(new EFormPDFServlet());
		bean.addUrlMappings("/eform/createpdf");
		bean.setLoadOnStartup(SERVLET_ORDER_PDF_CREATOR_EFORM);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>reportDownload</servlet-name>
		<servlet-class>oscar.oscarReport.pageUtil.RptDownloadCSVServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>reportDownload</servlet-name>
		<url-pattern>/report/reportDownload</url-pattern>
	</servlet-mapping>
	 */
	private static final int SERVLET_ORDER_REPORT_DOWNLOAD = 0;
	@Bean
	public ServletRegistrationBean<RptDownloadCSVServlet> registerReportDownloadServlet()
	{
		ServletRegistrationBean<RptDownloadCSVServlet> bean = new ServletRegistrationBean<>(new RptDownloadCSVServlet());
		bean.addUrlMappings("/report/reportDownload");
		bean.setLoadOnStartup(SERVLET_ORDER_REPORT_DOWNLOAD);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>OscarDownload</servlet-name>
		<servlet-class>oscar.util.OscarDownload</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>OscarDownload</servlet-name>
		<url-pattern>/servlet/OscarDownload</url-pattern>
	</servlet-mapping>
	 */
	// TODO: OSCARUPGRADE: Test this, it appears to be related to billing downloads (OBEC, teleplan, etc)
	private static final int SERVLET_ORDER_OSCAR_DOWNLOAD = 0;
	@Bean
	public ServletRegistrationBean<OscarDownload> registerOscarDownloadServlet()
	{
		ServletRegistrationBean<OscarDownload> bean = new ServletRegistrationBean<>(new OscarDownload());
		bean.addUrlMappings("/servlet/OscarDownload");
		bean.setLoadOnStartup(SERVLET_ORDER_OSCAR_DOWNLOAD);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>Download</servlet-name>
		<servlet-class>oscar.util.GenericDownload</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>Download</servlet-name>
		<url-pattern>/Download</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: The only thing I can see that uses this is ReportSurvey, and I don't know how to find that.
	private static final int SERVLET_ORDER_DOWNLOAD = 0;
	@Bean
	public ServletRegistrationBean<GenericDownload> registerDownloadServlet()
	{
		ServletRegistrationBean<GenericDownload> bean = new ServletRegistrationBean<>(new GenericDownload());
		bean.addUrlMappings("/Download");
		bean.setLoadOnStartup(SERVLET_ORDER_DOWNLOAD);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>BackupDownload</servlet-name>
		<servlet-class>oscar.util.BackupDownload</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>BackupDownload</servlet-name>
		<url-pattern>/servlet/BackupDownload</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: Ontario billing, I'm not set up to test that.
	private static final int SERVLET_ORDER_BACKUP_DOWNLOAD = 0;
	@Bean
	public ServletRegistrationBean<BackupDownload> registerBackupDownloadServlet()
	{
		ServletRegistrationBean<BackupDownload> bean = new ServletRegistrationBean<>(new BackupDownload());
		bean.addUrlMappings("/servlet/BackupDownload");
		bean.setLoadOnStartup(SERVLET_ORDER_BACKUP_DOWNLOAD);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>UploadTeleplanServlet</servlet-name>
		<servlet-class>oscar.oscarBilling.ca.bc.MSP.DocumentTeleplanReportUploadServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>UploadTeleplanServlet</servlet-name>
		<url-pattern>/servlet/oscar.DocumentTeleplanReportUploadServlet</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: Old style BC billing, I'm not set up to test that.
	private static final int SERVLET_ORDER_UPLOAD_TELEPLAN_SERVLET = 0;
	@Bean
	public ServletRegistrationBean<DocumentTeleplanReportUploadServlet> registerUploadTeleplanServlet()
	{
		ServletRegistrationBean<DocumentTeleplanReportUploadServlet> bean = new ServletRegistrationBean<>(new DocumentTeleplanReportUploadServlet());
		bean.addUrlMappings("/servlet/oscar.DocumentTeleplanReportUploadServlet");
		bean.setLoadOnStartup(SERVLET_ORDER_UPLOAD_TELEPLAN_SERVLET);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>ScatterPlotChartServlet</servlet-name>
		<servlet-class>oscar.oscarEncounter.oscarMeasurements.pageUtil.ScatterPlotChartServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ScatterPlotChartServlet</servlet-name>
		<url-pattern>/servlet/oscar.oscarEncounter.oscarMeasurements.pageUtil.ScatterPlotChartServlet</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: Runs, but seems to be used for the diab3 flowsheet template.  Is this used?  It's not default.
	@Bean
	public ServletRegistrationBean<ScatterPlotChartServlet> registerScatterPlotChartServlet()
	{
		ServletRegistrationBean<ScatterPlotChartServlet> bean = new ServletRegistrationBean<>(new ScatterPlotChartServlet());
		bean.addUrlMappings("/servlet/oscar.oscarEncounter.oscarMeasurements.pageUtil.ScatterPlotChartServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>DocumentUploadServlet</servlet-name>
		<servlet-class>oscar.DocumentUploadServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>DocumentUploadServlet</servlet-name>
		<url-pattern>/servlet/oscar.DocumentUploadServlet</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: Ontario billing
	private static final int SERVLET_ORDER_DOCUMENT_UPLOAD_SERVLET = 0;
	@Bean
	public ServletRegistrationBean<DocumentUploadServlet> registerDocumentUploadServletServlet()
	{
		ServletRegistrationBean<DocumentUploadServlet> bean = new ServletRegistrationBean<>(new DocumentUploadServlet());
		bean.addUrlMappings("/servlet/oscar.DocumentUploadServlet");
		bean.setLoadOnStartup(SERVLET_ORDER_DOCUMENT_UPLOAD_SERVLET);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>HsfoQuartzSchedulerServlet</servlet-name>
		<servlet-class>oscar.form.study.hsfo2.pageUtil.HsfoQuartzServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	 */

	/*
	<servlet>
		<servlet-name>PatientListByAppointment</servlet-name>
		<servlet-class>oscar.oscarReport.data.PatientListByAppt</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>PatientListByAppointment</servlet-name>
		<url-pattern>/patientlistbyappt</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: It "works" as well as it does in regular Juno.
	@Bean
	public ServletRegistrationBean<PatientListByAppt> registerPatientListByAppointmentServlet()
	{
		ServletRegistrationBean<PatientListByAppt> bean = new ServletRegistrationBean<>(new PatientListByAppt());
		bean.addUrlMappings("/patientlistbyappt");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>ImageRenderingServlet</servlet-name>
		<servlet-class>org.oscarehr.ui.servlet.ImageRenderingServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ImageRenderingServlet</servlet-name>
		<url-pattern>/imageRenderingServlet</url-pattern>
	</servlet-mapping>
	 */
	@Bean
	public ServletRegistrationBean<ImageRenderingServlet> registerImageRenderingServlet()
	{
		ServletRegistrationBean<ImageRenderingServlet> bean = new ServletRegistrationBean<>(new ImageRenderingServlet());
		bean.addUrlMappings("/imageRenderingServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>ContentRenderingServlet</servlet-name>
		<servlet-class>org.oscarehr.ui.servlet.ContentRenderingServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ContentRenderingServlet</servlet-name>
		<url-pattern>/contentRenderingServlet/*</url-pattern>
	</servlet-mapping>
	 */
	// TODO: SPRINGUPGRADE: This seems to be for QR code rendering.  I can get it to work with specific entries.
	@Bean
	public ServletRegistrationBean<ContentRenderingServlet> registerContentRenderingServlet()
	{
		ServletRegistrationBean<ContentRenderingServlet> bean = new ServletRegistrationBean<>(new ContentRenderingServlet());
		bean.addUrlMappings("/contentRenderingServlet/*");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>eformViewForPdfGenerationServlet</servlet-name>
		<servlet-class>org.oscarehr.web.eform.EformViewForPdfGenerationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>eformViewForPdfGenerationServlet</servlet-name>
		<url-pattern>/eformViewForPdfGenerationServlet</url-pattern>
	</servlet-mapping>
	*/
	@Bean
	public ServletRegistrationBean<EformViewForPdfGenerationServlet> registerEformViewForPdfGenerationServlet()
	{
		ServletRegistrationBean<EformViewForPdfGenerationServlet> bean = new ServletRegistrationBean<>(new EformViewForPdfGenerationServlet());
		bean.addUrlMappings("/eformViewForPdfGenerationServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>EFormViewForPdfGenerationServlet</servlet-name>
		<servlet-class>oscar.eform.util.EFormViewForPdfGenerationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>EFormViewForPdfGenerationServlet</servlet-name>
		<url-pattern>/EFormViewForPdfGenerationServlet</url-pattern>
	</servlet-mapping>
	*/
	@Bean
	public ServletRegistrationBean<EFormViewForPdfGenerationServlet> registerEFormViewForPdfGenerationServlet()
	{
		ServletRegistrationBean<EFormViewForPdfGenerationServlet> bean = new ServletRegistrationBean<>(new EFormViewForPdfGenerationServlet());
		bean.addUrlMappings("/EFormViewForPdfGenerationServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>ProxyEformNotification</servlet-name>
		<servlet-class>org.oscarehr.PMmodule.notification.EmailTriggerServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>ProxyEformNotification</servlet-name>
		<url-pattern>/ProxyEformNotification</url-pattern>
	</servlet-mapping>
	*/
	// TODO: SPRINGUPGRADE: I don't know what this is or if it works
	@Bean
	public ServletRegistrationBean<EmailTriggerServlet> registerProxyEformNotificationServlet()
	{
		ServletRegistrationBean<EmailTriggerServlet> bean = new ServletRegistrationBean<>(new EmailTriggerServlet());
		bean.addUrlMappings("/ProxyEformNotification");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>EFormSignatureViewForPdfGenerationServlet</servlet-name>
		<servlet-class>oscar.eform.util.EFormSignatureViewForPdfGenerationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>EFormSignatureViewForPdfGenerationServlet</servlet-name>
		<url-pattern>/EFormSignatureViewForPdfGenerationServlet</url-pattern>
	</servlet-mapping>
	*/
	// TODO: SPRINGUPGRADE: for signature pad, seems to work.
	@Bean
	public ServletRegistrationBean<EFormSignatureViewForPdfGenerationServlet> registerEformSignatureViewForPdfGenerationServlet()
	{
		ServletRegistrationBean<EFormSignatureViewForPdfGenerationServlet> bean = new ServletRegistrationBean<>(new EFormSignatureViewForPdfGenerationServlet());
		bean.addUrlMappings("/EFormSignatureViewForPdfGenerationServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>EFormImageViewForPdfGenerationServlet</servlet-name>
		<servlet-class>oscar.eform.util.EFormImageViewForPdfGenerationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>EFormImageViewForPdfGenerationServlet</servlet-name>
		<url-pattern>/EFormImageViewForPdfGenerationServlet</url-pattern>
	</servlet-mapping>
	*/
	// TODO: SPRINGUPGRADE: for eform images, works.
	@Bean
	public ServletRegistrationBean<EFormImageViewForPdfGenerationServlet> registerEformImageViewForPdfGenerationServlet()
	{
		ServletRegistrationBean<EFormImageViewForPdfGenerationServlet> bean = new ServletRegistrationBean<>(new EFormImageViewForPdfGenerationServlet());
		bean.addUrlMappings("/EFormImageViewForPdfGenerationServlet");

		return bean;
	}

	/*
	<servlet>
		<servlet-name>DATISExportTest</servlet-name>
		<servlet-class>org.oscarehr.PMmodule.exporter.TestServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>DATISExportTest</servlet-name>
		<url-pattern>/PMmodule/export</url-pattern>
	</servlet-mapping>
	*/
	// TODO: SPRINGUPGRADE: CAISI, ignoring.
	private static final int SERVLET_ORDER_DATIS_EXPORT_TEST = 0;
	@Bean
	public ServletRegistrationBean<TestServlet> registerDATISExportTestServlet()
	{
		ServletRegistrationBean<TestServlet> bean = new ServletRegistrationBean<>(new TestServlet());
		bean.addUrlMappings("/PMmodule/export");
		bean.setLoadOnStartup(SERVLET_ORDER_DATIS_EXPORT_TEST);

		return bean;
	}

	/*
	<servlet>
		<servlet-name>DATISExport</servlet-name>
		<servlet-class>org.oscarehr.PMmodule.exporter.DATISExporterServlet</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>DATISExport</servlet-name>
		<url-pattern>/PMmodule/exportfiles</url-pattern>
	</servlet-mapping>
	*/
	// TODO: SPRINGUPGRADE: CAISI, ignoring.
	private static final int SERVLET_ORDER_DATIS_EXPORT = 0;
	@Bean
	public ServletRegistrationBean<DATISExporterServlet> registerDatisExportServlet()
	{
		ServletRegistrationBean<DATISExporterServlet> bean = new ServletRegistrationBean<>(new DATISExporterServlet());
		bean.addUrlMappings("/PMmodule/exportfiles");
		bean.setLoadOnStartup(SERVLET_ORDER_DATIS_EXPORT);

		return bean;
	}
}
