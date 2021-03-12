
package xml.cds.v5_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.oscarehr.dataMigration.mapper.cds.CDSReportInterface;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Media" type="{cds_dt}reportMedia" minOccurs="0"/&gt;
 *         &lt;element name="Format" type="{cds_dt}reportFormat"/&gt;
 *         &lt;element name="FileExtensionAndVersion" type="{cds_dt}reportFileTypeAndVersion" minOccurs="0"/&gt;
 *         &lt;element name="FilePath" type="{cds_dt}text250" minOccurs="0"/&gt;
 *         &lt;element name="Content" type="{cds_dt}reportContent" minOccurs="0"/&gt;
 *         &lt;element name="Class" type="{cds_dt}reportClass"/&gt;
 *         &lt;element name="SubClass" type="{cds_dt}reportSubClass" minOccurs="0"/&gt;
 *         &lt;element name="EventDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="ReceivedDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="SourceAuthorPhysician" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="AuthorName" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
 *                   &lt;element name="AuthorFreeText" type="{cds_dt}text120" minOccurs="0"/&gt;
 *                 &lt;/choice&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SourceFacility" type="{cds_dt}text120" minOccurs="0"/&gt;
 *         &lt;element name="ReportReviewed" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                   &lt;element name="ReviewingOHIPPhysicianId" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="DateTimeReportReviewed" type="{cds_dt}dateFullOrPartial"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SendingFacilityId" type="{cds_dt}text4" minOccurs="0"/&gt;
 *         &lt;element name="SendingFacilityReport" type="{cds_dt}text75" minOccurs="0"/&gt;
 *         &lt;element name="OBRContent" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="AccompanyingSubClass" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="60"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AccompanyingMnemonic" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="200"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="AccompanyingDescription" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="200"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ObservationDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HRMResultStatus" type="{cds_dt}text1" minOccurs="0"/&gt;
 *         &lt;element name="MessageUniqueID" type="{cds_dt}text250" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="RecipientName" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
 *         &lt;element name="SentDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "media",
    "format",
    "fileExtensionAndVersion",
    "filePath",
    "content",
    "clazz",
    "subClass",
    "eventDateTime",
    "receivedDateTime",
    "sourceAuthorPhysician",
    "sourceFacility",
    "reportReviewed",
    "sendingFacilityId",
    "sendingFacilityReport",
    "obrContent",
    "hrmResultStatus",
    "messageUniqueID",
    "notes",
    "recipientName",
    "sentDateTime"
})
@XmlRootElement(name = "Reports", namespace = "cds")
public class Reports
    implements CDSReportInterface
{

    @XmlElement(name = "Media", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected ReportMedia media;
    @XmlElement(name = "Format", namespace = "cds", required = true)
    @XmlSchemaType(name = "token")
    protected ReportFormat format;
    @XmlElement(name = "FileExtensionAndVersion", namespace = "cds")
    protected String fileExtensionAndVersion;
    @XmlElement(name = "FilePath", namespace = "cds")
    protected String filePath;
    @XmlElement(name = "Content", namespace = "cds")
    protected ReportContent content;
    @XmlElement(name = "Class", namespace = "cds", required = true)
    @XmlSchemaType(name = "token")
    protected ReportClass clazz;
    @XmlElement(name = "SubClass", namespace = "cds")
    protected String subClass;
    @XmlElement(name = "EventDateTime", namespace = "cds")
    protected DateTimeFullOrPartial eventDateTime;
    @XmlElement(name = "ReceivedDateTime", namespace = "cds")
    protected DateTimeFullOrPartial receivedDateTime;
    @XmlElement(name = "SourceAuthorPhysician", namespace = "cds")
    protected Reports.SourceAuthorPhysician sourceAuthorPhysician;
    @XmlElement(name = "SourceFacility", namespace = "cds")
    protected String sourceFacility;
    @XmlElement(name = "ReportReviewed", namespace = "cds")
    protected List<Reports.ReportReviewed> reportReviewed;
    @XmlElement(name = "SendingFacilityId", namespace = "cds")
    protected String sendingFacilityId;
    @XmlElement(name = "SendingFacilityReport", namespace = "cds")
    protected String sendingFacilityReport;
    @XmlElement(name = "OBRContent", namespace = "cds")
    protected List<Reports.OBRContent> obrContent;
    @XmlElement(name = "HRMResultStatus", namespace = "cds")
    protected String hrmResultStatus;
    @XmlElement(name = "MessageUniqueID", namespace = "cds")
    protected String messageUniqueID;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;
    @XmlElement(name = "RecipientName", namespace = "cds")
    protected PersonNameSimple recipientName;
    @XmlElement(name = "SentDateTime", namespace = "cds")
    protected DateTimeFullOrPartial sentDateTime;

    /**
     * Gets the value of the media property.
     * 
     * @return
     *     possible object is
     *     {@link ReportMedia }
     *     
     */
    public ReportMedia getMedia() {
        return media;
    }

    /**
     * Sets the value of the media property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportMedia }
     *     
     */
    public void setMedia(ReportMedia value) {
        this.media = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link ReportFormat }
     *     
     */
    public ReportFormat getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportFormat }
     *     
     */
    public void setFormat(ReportFormat value) {
        this.format = value;
    }

    /**
     * Gets the value of the fileExtensionAndVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileExtensionAndVersion() {
        return fileExtensionAndVersion;
    }

    /**
     * Sets the value of the fileExtensionAndVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileExtensionAndVersion(String value) {
        this.fileExtensionAndVersion = value;
    }

    /**
     * Gets the value of the filePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the value of the filePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilePath(String value) {
        this.filePath = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link ReportContent }
     *     
     */
    public ReportContent getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportContent }
     *     
     */
    public void setContent(ReportContent value) {
        this.content = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link ReportClass }
     *     
     */
    public ReportClass getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportClass }
     *     
     */
    public void setClazz(ReportClass value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the subClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubClass() {
        return subClass;
    }

    /**
     * Sets the value of the subClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubClass(String value) {
        this.subClass = value;
    }

    /**
     * Gets the value of the eventDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Sets the value of the eventDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setEventDateTime(DateTimeFullOrPartial value) {
        this.eventDateTime = value;
    }

    /**
     * Gets the value of the receivedDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getReceivedDateTime() {
        return receivedDateTime;
    }

    /**
     * Sets the value of the receivedDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setReceivedDateTime(DateTimeFullOrPartial value) {
        this.receivedDateTime = value;
    }

    /**
     * Gets the value of the sourceAuthorPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link Reports.SourceAuthorPhysician }
     *     
     */
    public Reports.SourceAuthorPhysician getSourceAuthorPhysician() {
        return sourceAuthorPhysician;
    }

    /**
     * Sets the value of the sourceAuthorPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reports.SourceAuthorPhysician }
     *     
     */
    public void setSourceAuthorPhysician(Reports.SourceAuthorPhysician value) {
        this.sourceAuthorPhysician = value;
    }

    /**
     * Gets the value of the sourceFacility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceFacility() {
        return sourceFacility;
    }

    /**
     * Sets the value of the sourceFacility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceFacility(String value) {
        this.sourceFacility = value;
    }

    /**
     * Gets the value of the reportReviewed property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reportReviewed property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReportReviewed().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Reports.ReportReviewed }
     * 
     * 
     */
    public List<Reports.ReportReviewed> getReportReviewed() {
        if (reportReviewed == null) {
            reportReviewed = new ArrayList<Reports.ReportReviewed>();
        }
        return this.reportReviewed;
    }

    /**
     * Gets the value of the sendingFacilityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendingFacilityId() {
        return sendingFacilityId;
    }

    /**
     * Sets the value of the sendingFacilityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendingFacilityId(String value) {
        this.sendingFacilityId = value;
    }

    /**
     * Gets the value of the sendingFacilityReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendingFacilityReport() {
        return sendingFacilityReport;
    }

    /**
     * Sets the value of the sendingFacilityReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendingFacilityReport(String value) {
        this.sendingFacilityReport = value;
    }

    /**
     * Gets the value of the obrContent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the obrContent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOBRContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Reports.OBRContent }
     * 
     * 
     */
    public List<Reports.OBRContent> getOBRContent() {
        if (obrContent == null) {
            obrContent = new ArrayList<Reports.OBRContent>();
        }
        return this.obrContent;
    }

    /**
     * Gets the value of the hrmResultStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHRMResultStatus() {
        return hrmResultStatus;
    }

    /**
     * Sets the value of the hrmResultStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHRMResultStatus(String value) {
        this.hrmResultStatus = value;
    }

    /**
     * Gets the value of the messageUniqueID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageUniqueID() {
        return messageUniqueID;
    }

    /**
     * Sets the value of the messageUniqueID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageUniqueID(String value) {
        this.messageUniqueID = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the recipientName property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSimple }
     *     
     */
    public PersonNameSimple getRecipientName() {
        return recipientName;
    }

    /**
     * Sets the value of the recipientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSimple }
     *     
     */
    public void setRecipientName(PersonNameSimple value) {
        this.recipientName = value;
    }

    /**
     * Gets the value of the sentDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getSentDateTime() {
        return sentDateTime;
    }

    /**
     * Sets the value of the sentDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setSentDateTime(DateTimeFullOrPartial value) {
        this.sentDateTime = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="AccompanyingSubClass" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="60"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AccompanyingMnemonic" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="200"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="AccompanyingDescription" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="200"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ObservationDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "accompanyingSubClass",
        "accompanyingMnemonic",
        "accompanyingDescription",
        "observationDateTime"
    })
    public static class OBRContent {

        @XmlElement(name = "AccompanyingSubClass", namespace = "cds")
        protected String accompanyingSubClass;
        @XmlElement(name = "AccompanyingMnemonic", namespace = "cds")
        protected String accompanyingMnemonic;
        @XmlElement(name = "AccompanyingDescription", namespace = "cds")
        protected String accompanyingDescription;
        @XmlElement(name = "ObservationDateTime", namespace = "cds")
        protected DateTimeFullOrPartial observationDateTime;

        /**
         * Gets the value of the accompanyingSubClass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAccompanyingSubClass() {
            return accompanyingSubClass;
        }

        /**
         * Sets the value of the accompanyingSubClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAccompanyingSubClass(String value) {
            this.accompanyingSubClass = value;
        }

        /**
         * Gets the value of the accompanyingMnemonic property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAccompanyingMnemonic() {
            return accompanyingMnemonic;
        }

        /**
         * Sets the value of the accompanyingMnemonic property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAccompanyingMnemonic(String value) {
            this.accompanyingMnemonic = value;
        }

        /**
         * Gets the value of the accompanyingDescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAccompanyingDescription() {
            return accompanyingDescription;
        }

        /**
         * Sets the value of the accompanyingDescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAccompanyingDescription(String value) {
            this.accompanyingDescription = value;
        }

        /**
         * Gets the value of the observationDateTime property.
         * 
         * @return
         *     possible object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public DateTimeFullOrPartial getObservationDateTime() {
            return observationDateTime;
        }

        /**
         * Sets the value of the observationDateTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public void setObservationDateTime(DateTimeFullOrPartial value) {
            this.observationDateTime = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence minOccurs="0"&gt;
     *         &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
     *         &lt;element name="ReviewingOHIPPhysicianId" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="DateTimeReportReviewed" type="{cds_dt}dateFullOrPartial"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "reviewingOHIPPhysicianId",
        "dateTimeReportReviewed"
    })
    public static class ReportReviewed {

        @XmlElement(name = "Name", namespace = "cds")
        protected PersonNameSimple name;
        @XmlElement(name = "ReviewingOHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String reviewingOHIPPhysicianId;
        @XmlElement(name = "DateTimeReportReviewed", namespace = "cds")
        protected DateFullOrPartial dateTimeReportReviewed;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameSimple }
         *     
         */
        public PersonNameSimple getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameSimple }
         *     
         */
        public void setName(PersonNameSimple value) {
            this.name = value;
        }

        /**
         * Gets the value of the reviewingOHIPPhysicianId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReviewingOHIPPhysicianId() {
            return reviewingOHIPPhysicianId;
        }

        /**
         * Sets the value of the reviewingOHIPPhysicianId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReviewingOHIPPhysicianId(String value) {
            this.reviewingOHIPPhysicianId = value;
        }

        /**
         * Gets the value of the dateTimeReportReviewed property.
         * 
         * @return
         *     possible object is
         *     {@link DateFullOrPartial }
         *     
         */
        public DateFullOrPartial getDateTimeReportReviewed() {
            return dateTimeReportReviewed;
        }

        /**
         * Sets the value of the dateTimeReportReviewed property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateFullOrPartial }
         *     
         */
        public void setDateTimeReportReviewed(DateFullOrPartial value) {
            this.dateTimeReportReviewed = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;element name="AuthorName" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
     *         &lt;element name="AuthorFreeText" type="{cds_dt}text120" minOccurs="0"/&gt;
     *       &lt;/choice&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "authorName",
        "authorFreeText"
    })
    public static class SourceAuthorPhysician {

        @XmlElement(name = "AuthorName", namespace = "cds")
        protected PersonNameSimple authorName;
        @XmlElement(name = "AuthorFreeText", namespace = "cds")
        protected String authorFreeText;

        /**
         * Gets the value of the authorName property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameSimple }
         *     
         */
        public PersonNameSimple getAuthorName() {
            return authorName;
        }

        /**
         * Sets the value of the authorName property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameSimple }
         *     
         */
        public void setAuthorName(PersonNameSimple value) {
            this.authorName = value;
        }

        /**
         * Gets the value of the authorFreeText property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthorFreeText() {
            return authorFreeText;
        }

        /**
         * Sets the value of the authorFreeText property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthorFreeText(String value) {
            this.authorFreeText = value;
        }

    }

}
