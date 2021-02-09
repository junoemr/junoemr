
package xml.cds.v5_0;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="AppointmentTime" type="{cds_dt}time"/&gt;
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="AppointmentStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AppointmentDate" type="{cds_dt}dateFullOrPartial"/&gt;
 *         &lt;element name="Provider" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;all&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                   &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/all&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AppointmentPurpose" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AppointmentNotes" type="{cds_dt}text32K" minOccurs="0"/&gt;
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
    "appointmentTime",
    "duration",
    "appointmentStatus",
    "appointmentDate",
    "provider",
    "appointmentPurpose",
    "appointmentNotes"
})
@XmlRootElement(name = "Appointments", namespace = "cds")
public class Appointments {

    @XmlElement(name = "AppointmentTime", namespace = "cds", required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar appointmentTime;
    @XmlElement(name = "Duration", namespace = "cds")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger duration;
    @XmlElement(name = "AppointmentStatus", namespace = "cds")
    protected String appointmentStatus;
    @XmlElement(name = "AppointmentDate", namespace = "cds", required = true)
    protected DateFullOrPartial appointmentDate;
    @XmlElement(name = "Provider", namespace = "cds")
    protected Appointments.Provider provider;
    @XmlElement(name = "AppointmentPurpose", namespace = "cds")
    protected String appointmentPurpose;
    @XmlElement(name = "AppointmentNotes", namespace = "cds")
    protected String appointmentNotes;

    /**
     * Gets the value of the appointmentTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * Sets the value of the appointmentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAppointmentTime(XMLGregorianCalendar value) {
        this.appointmentTime = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDuration(BigInteger value) {
        this.duration = value;
    }

    /**
     * Gets the value of the appointmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    /**
     * Sets the value of the appointmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppointmentStatus(String value) {
        this.appointmentStatus = value;
    }

    /**
     * Gets the value of the appointmentDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Sets the value of the appointmentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setAppointmentDate(DateFullOrPartial value) {
        this.appointmentDate = value;
    }

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link Appointments.Provider }
     *     
     */
    public Appointments.Provider getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link Appointments.Provider }
     *     
     */
    public void setProvider(Appointments.Provider value) {
        this.provider = value;
    }

    /**
     * Gets the value of the appointmentPurpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppointmentPurpose() {
        return appointmentPurpose;
    }

    /**
     * Sets the value of the appointmentPurpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppointmentPurpose(String value) {
        this.appointmentPurpose = value;
    }

    /**
     * Gets the value of the appointmentNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppointmentNotes() {
        return appointmentNotes;
    }

    /**
     * Sets the value of the appointmentNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppointmentNotes(String value) {
        this.appointmentNotes = value;
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
     *       &lt;all&gt;
     *         &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
     *         &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/all&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Provider {

        @XmlElement(name = "Name", namespace = "cds", required = true)
        protected PersonNameSimple name;
        @XmlElement(name = "OHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ohipPhysicianId;

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
         * Gets the value of the ohipPhysicianId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOHIPPhysicianId() {
            return ohipPhysicianId;
        }

        /**
         * Sets the value of the ohipPhysicianId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOHIPPhysicianId(String value) {
            this.ohipPhysicianId = value;
        }

    }

}
