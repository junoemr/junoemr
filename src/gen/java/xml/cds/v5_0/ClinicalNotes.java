
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
 *         &lt;element name="NoteType" type="{cds_dt}physicianNoteType" minOccurs="0"/&gt;
 *         &lt;element name="MyClinicalNotesContent" type="{cds_dt}text" minOccurs="0"/&gt;
 *         &lt;element name="EventDateTime" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="ParticipatingProviders" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                   &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="DateTimeNoteCreated" type="{cds_dt}dateTimeFullOrPartial"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NoteReviewer" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                   &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="DateTimeNoteReviewed" type="{cds_dt}dateTimeFullOrPartial"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
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
    "noteType",
    "myClinicalNotesContent",
    "eventDateTime",
    "participatingProviders",
    "noteReviewer"
})
@XmlRootElement(name = "ClinicalNotes", namespace = "cds")
public class ClinicalNotes {

    @XmlElement(name = "NoteType", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String noteType;
    @XmlElement(name = "MyClinicalNotesContent", namespace = "cds")
    protected String myClinicalNotesContent;
    @XmlElement(name = "EventDateTime", namespace = "cds")
    protected DateTimeFullOrPartial eventDateTime;
    @XmlElement(name = "ParticipatingProviders", namespace = "cds")
    protected List<ClinicalNotes.ParticipatingProviders> participatingProviders;
    @XmlElement(name = "NoteReviewer", namespace = "cds")
    protected List<ClinicalNotes.NoteReviewer> noteReviewer;

    /**
     * Gets the value of the noteType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoteType() {
        return noteType;
    }

    /**
     * Sets the value of the noteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoteType(String value) {
        this.noteType = value;
    }

    /**
     * Gets the value of the myClinicalNotesContent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyClinicalNotesContent() {
        return myClinicalNotesContent;
    }

    /**
     * Sets the value of the myClinicalNotesContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyClinicalNotesContent(String value) {
        this.myClinicalNotesContent = value;
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
     * Gets the value of the participatingProviders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participatingProviders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipatingProviders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClinicalNotes.ParticipatingProviders }
     * 
     * 
     */
    public List<ClinicalNotes.ParticipatingProviders> getParticipatingProviders() {
        if (participatingProviders == null) {
            participatingProviders = new ArrayList<ClinicalNotes.ParticipatingProviders>();
        }
        return this.participatingProviders;
    }

    /**
     * Gets the value of the noteReviewer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteReviewer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoteReviewer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClinicalNotes.NoteReviewer }
     * 
     * 
     */
    public List<ClinicalNotes.NoteReviewer> getNoteReviewer() {
        if (noteReviewer == null) {
            noteReviewer = new ArrayList<ClinicalNotes.NoteReviewer>();
        }
        return this.noteReviewer;
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
     *         &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="DateTimeNoteReviewed" type="{cds_dt}dateTimeFullOrPartial"/&gt;
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
        "ohipPhysicianId",
        "dateTimeNoteReviewed"
    })
    public static class NoteReviewer {

        @XmlElement(name = "Name", namespace = "cds")
        protected PersonNameSimple name;
        @XmlElement(name = "OHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ohipPhysicianId;
        @XmlElement(name = "DateTimeNoteReviewed", namespace = "cds")
        protected DateTimeFullOrPartial dateTimeNoteReviewed;

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

        /**
         * Gets the value of the dateTimeNoteReviewed property.
         * 
         * @return
         *     possible object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public DateTimeFullOrPartial getDateTimeNoteReviewed() {
            return dateTimeNoteReviewed;
        }

        /**
         * Sets the value of the dateTimeNoteReviewed property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public void setDateTimeNoteReviewed(DateTimeFullOrPartial value) {
            this.dateTimeNoteReviewed = value;
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
     *         &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="DateTimeNoteCreated" type="{cds_dt}dateTimeFullOrPartial"/&gt;
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
        "ohipPhysicianId",
        "dateTimeNoteCreated"
    })
    public static class ParticipatingProviders {

        @XmlElement(name = "Name", namespace = "cds")
        protected PersonNameSimple name;
        @XmlElement(name = "OHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ohipPhysicianId;
        @XmlElement(name = "DateTimeNoteCreated", namespace = "cds")
        protected DateTimeFullOrPartial dateTimeNoteCreated;

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

        /**
         * Gets the value of the dateTimeNoteCreated property.
         * 
         * @return
         *     possible object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public DateTimeFullOrPartial getDateTimeNoteCreated() {
            return dateTimeNoteCreated;
        }

        /**
         * Sets the value of the dateTimeNoteCreated property.
         * 
         * @param value
         *     allowed object is
         *     {@link DateTimeFullOrPartial }
         *     
         */
        public void setDateTimeNoteCreated(DateTimeFullOrPartial value) {
            this.dateTimeNoteCreated = value;
        }

    }

}
