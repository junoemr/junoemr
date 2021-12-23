
package xml.hrm.v4_1;

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
 *         &lt;element name="Format" type="{cds_dt}reportFormat" minOccurs="0"/&gt;
 *         &lt;element name="FileExtensionAndVersion" type="{cds_dt}reportFileTypeAndVersion"/&gt;
 *         &lt;element name="Content" type="{cds_dt}reportContent" minOccurs="0"/&gt;
 *         &lt;element name="Class" type="{cds_dt}reportClass" minOccurs="0"/&gt;
 *         &lt;element name="SubClass" type="{cds_dt}reportSubClass" minOccurs="0"/&gt;
 *         &lt;element name="EventDateTime" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="ReceivedDateTime" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="ReviewedDateTime" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="AuthorPhysician" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
 *         &lt;element name="ReviewingOHIPPhysicianId" type="{cds_dt}ohipPhysicianBillingNumber" minOccurs="0"/&gt;
 *         &lt;element name="SendingFacility" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SendingFacilityReportNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="75"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
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
 *                   &lt;element name="ObservationDateTime" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ResultStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="1"/&gt;
 *               &lt;enumeration value="P"/&gt;
 *               &lt;enumeration value="D"/&gt;
 *               &lt;enumeration value="S"/&gt;
 *               &lt;enumeration value="C"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
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
    "content",
    "clazz",
    "subClass",
    "eventDateTime",
    "receivedDateTime",
    "reviewedDateTime",
    "authorPhysician",
    "reviewingOHIPPhysicianId",
    "sendingFacility",
    "sendingFacilityReportNumber",
    "obrContent",
    "resultStatus"
})
@XmlRootElement(name = "ReportsReceived", namespace = "cds")
public class ReportsReceived
    implements CDSReportInterface
{

    @XmlElement(name = "Media", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected ReportMedia media;
    @XmlElement(name = "Format", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected ReportFormat format;
    @XmlElement(name = "FileExtensionAndVersion", namespace = "cds", required = true)
    protected String fileExtensionAndVersion;
    @XmlElement(name = "Content", namespace = "cds")
    protected ReportContent content;
    @XmlElement(name = "Class", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected ReportClass clazz;
    @XmlElement(name = "SubClass", namespace = "cds")
    protected String subClass;
    @XmlElement(name = "EventDateTime", namespace = "cds")
    protected DateFullOrPartial eventDateTime;
    @XmlElement(name = "ReceivedDateTime", namespace = "cds")
    protected DateFullOrPartial receivedDateTime;
    @XmlElement(name = "ReviewedDateTime", namespace = "cds")
    protected DateFullOrPartial reviewedDateTime;
    @XmlElement(name = "AuthorPhysician", namespace = "cds")
    protected PersonNameSimple authorPhysician;
    @XmlElement(name = "ReviewingOHIPPhysicianId", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String reviewingOHIPPhysicianId;
    @XmlElement(name = "SendingFacility", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String sendingFacility;
    @XmlElement(name = "SendingFacilityReportNumber", namespace = "cds")
    protected String sendingFacilityReportNumber;
    @XmlElement(name = "OBRContent", namespace = "cds")
    protected List<ReportsReceived.OBRContent> obrContent;
    @XmlElement(name = "ResultStatus", namespace = "cds")
    protected String resultStatus;

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
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Sets the value of the eventDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setEventDateTime(DateFullOrPartial value) {
        this.eventDateTime = value;
    }

    /**
     * Gets the value of the receivedDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getReceivedDateTime() {
        return receivedDateTime;
    }

    /**
     * Sets the value of the receivedDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setReceivedDateTime(DateFullOrPartial value) {
        this.receivedDateTime = value;
    }

    /**
     * Gets the value of the reviewedDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getReviewedDateTime() {
        return reviewedDateTime;
    }

    /**
     * Sets the value of the reviewedDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setReviewedDateTime(DateFullOrPartial value) {
        this.reviewedDateTime = value;
    }

    /**
     * Gets the value of the authorPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSimple }
     *     
     */
    public PersonNameSimple getAuthorPhysician() {
        return authorPhysician;
    }

    /**
     * Sets the value of the authorPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSimple }
     *     
     */
    public void setAuthorPhysician(PersonNameSimple value) {
        this.authorPhysician = value;
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
     * Gets the value of the sendingFacility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendingFacility() {
        return sendingFacility;
    }

    /**
     * Sets the value of the sendingFacility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendingFacility(String value) {
        this.sendingFacility = value;
    }

    /**
     * Gets the value of the sendingFacilityReportNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendingFacilityReportNumber() {
        return sendingFacilityReportNumber;
    }

    /**
     * Sets the value of the sendingFacilityReportNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendingFacilityReportNumber(String value) {
        this.sendingFacilityReportNumber = value;
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
     * {@link ReportsReceived.OBRContent }
     * 
     * 
     */
    public List<ReportsReceived.OBRContent> getOBRContent() {
        if (obrContent == null) {
            obrContent = new ArrayList<ReportsReceived.OBRContent>();
        }
        return this.obrContent;
    }

    /**
     * Gets the value of the resultStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /**
     * Sets the value of the resultStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultStatus(String value) {
        this.resultStatus = value;
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
     *         &lt;element name="ObservationDateTime" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
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
        protected DateFullOrPartial observationDateTime;

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
         *     {@link DateFullOrPartial }
         *     
         */
        public DateFullOrPartial getObservationDateTime() {
            return observationDateTime;
        }

        /**
         * Sets the value of the observationDateTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateFullOrPartial }
         *     
         */
        public void setObservationDateTime(DateFullOrPartial value) {
            this.observationDateTime = value;
        }

    }

}
