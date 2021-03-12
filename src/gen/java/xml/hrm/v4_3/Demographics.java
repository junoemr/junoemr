
package xml.hrm.v4_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.oscarehr.dataMigration.mapper.cds.CDSDemographicInterface;


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
 *         &lt;element name="Names" type="{cds_dt}personNameStandard"/&gt;
 *         &lt;element name="DateOfBirth" type="{cds_dt}dateFullOrPartial"/&gt;
 *         &lt;element name="HealthCard" type="{cds_dt}healthCard" minOccurs="0"/&gt;
 *         &lt;element name="ChartNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Gender" type="{cds_dt}gender"/&gt;
 *         &lt;element name="UniqueVendorIdSequence" type="{cds_dt}vendorId"/&gt;
 *         &lt;element name="Address" type="{cds_dt}address" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PhoneNumber" type="{cds_dt}phoneNumber" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PreferredPhone" type="{cds_dt}phoneNumberType" minOccurs="0"/&gt;
 *         &lt;element name="PreferredOfficialLanguage" type="{cds_dt}officialSpokenLanguageCode" minOccurs="0"/&gt;
 *         &lt;element name="PreferredSpokenLanguage" type="{cds_dt}spokenLanguageCode" minOccurs="0"/&gt;
 *         &lt;element name="Contact" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimpleWithMiddleName"/&gt;
 *                   &lt;element name="PhoneNumber" type="{cds_dt}phoneNumber" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                   &lt;element name="EmailAddress" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
 *                   &lt;element name="Note" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                         &lt;maxLength value="200"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="ContactPurpose" use="required" type="{cds_dt}contactPersonPurpose" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NoteAboutPatient" type="{cds_dt}text64K" minOccurs="0"/&gt;
 *         &lt;element name="PatientWarningFlags" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}patientWarningFlag"&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="EnrollmentStatus" type="{cds_dt}enrollmentStatus" minOccurs="0"/&gt;
 *         &lt;element name="EnrollmentDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="EnrollmentTerminationDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="TerminationReason" type="{cds_dt}terminationReasonCode" minOccurs="0"/&gt;
 *         &lt;element name="PrimaryPhysician" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;all&gt;
 *                   &lt;element name="Name" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
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
 *         &lt;element name="Email" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
 *         &lt;element name="FamilyMemberLink" type="{cds_dt}vendorId" minOccurs="0"/&gt;
 *         &lt;element name="PersonStatusCode" type="{cds_dt}personStatus"/&gt;
 *         &lt;element name="PersonStatusDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="SIN" type="{cds_dt}socialInsuranceNumber" minOccurs="0"/&gt;
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
    "names",
    "dateOfBirth",
    "healthCard",
    "chartNumber",
    "gender",
    "uniqueVendorIdSequence",
    "address",
    "phoneNumber",
    "preferredPhone",
    "preferredOfficialLanguage",
    "preferredSpokenLanguage",
    "contact",
    "noteAboutPatient",
    "patientWarningFlags",
    "enrollmentStatus",
    "enrollmentDate",
    "enrollmentTerminationDate",
    "terminationReason",
    "primaryPhysician",
    "email",
    "familyMemberLink",
    "personStatusCode",
    "personStatusDate",
    "sin"
})
@XmlRootElement(name = "Demographics", namespace = "cds")
public class Demographics
    implements CDSDemographicInterface
{

    @XmlElement(name = "Names", namespace = "cds", required = true)
    protected PersonNameStandard names;
    @XmlElement(name = "DateOfBirth", namespace = "cds", required = true)
    protected DateFullOrPartial dateOfBirth;
    @XmlElement(name = "HealthCard", namespace = "cds")
    protected HealthCard healthCard;
    @XmlElement(name = "ChartNumber", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String chartNumber;
    @XmlElement(name = "Gender", namespace = "cds", required = true)
    @XmlSchemaType(name = "token")
    protected Gender gender;
    @XmlElement(name = "UniqueVendorIdSequence", namespace = "cds", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String uniqueVendorIdSequence;
    @XmlElement(name = "Address", namespace = "cds")
    protected List<Address> address;
    @XmlElement(name = "PhoneNumber", namespace = "cds")
    protected List<PhoneNumber> phoneNumber;
    @XmlElement(name = "PreferredPhone", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected PhoneNumberType preferredPhone;
    @XmlElement(name = "PreferredOfficialLanguage", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected OfficialSpokenLanguageCode preferredOfficialLanguage;
    @XmlElement(name = "PreferredSpokenLanguage", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String preferredSpokenLanguage;
    @XmlElement(name = "Contact", namespace = "cds")
    protected List<Demographics.Contact> contact;
    @XmlElement(name = "NoteAboutPatient", namespace = "cds")
    protected String noteAboutPatient;
    @XmlElement(name = "PatientWarningFlags", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String patientWarningFlags;
    @XmlElement(name = "EnrollmentStatus", namespace = "cds")
    protected String enrollmentStatus;
    @XmlElement(name = "EnrollmentDate", namespace = "cds")
    protected DateFullOrPartial enrollmentDate;
    @XmlElement(name = "EnrollmentTerminationDate", namespace = "cds")
    protected DateFullOrPartial enrollmentTerminationDate;
    @XmlElement(name = "TerminationReason", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String terminationReason;
    @XmlElement(name = "PrimaryPhysician", namespace = "cds")
    protected Demographics.PrimaryPhysician primaryPhysician;
    @XmlElement(name = "Email", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String email;
    @XmlElement(name = "FamilyMemberLink", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String familyMemberLink;
    @XmlElement(name = "PersonStatusCode", namespace = "cds", required = true)
    @XmlSchemaType(name = "string")
    protected PersonStatus personStatusCode;
    @XmlElement(name = "PersonStatusDate", namespace = "cds")
    protected DateFullOrPartial personStatusDate;
    @XmlElement(name = "SIN", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String sin;

    /**
     * Gets the value of the names property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameStandard }
     *     
     */
    public PersonNameStandard getNames() {
        return names;
    }

    /**
     * Sets the value of the names property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameStandard }
     *     
     */
    public void setNames(PersonNameStandard value) {
        this.names = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setDateOfBirth(DateFullOrPartial value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the healthCard property.
     * 
     * @return
     *     possible object is
     *     {@link HealthCard }
     *     
     */
    public HealthCard getHealthCard() {
        return healthCard;
    }

    /**
     * Sets the value of the healthCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link HealthCard }
     *     
     */
    public void setHealthCard(HealthCard value) {
        this.healthCard = value;
    }

    /**
     * Gets the value of the chartNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChartNumber() {
        return chartNumber;
    }

    /**
     * Sets the value of the chartNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChartNumber(String value) {
        this.chartNumber = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gender }
     *     
     */
    public void setGender(Gender value) {
        this.gender = value;
    }

    /**
     * Gets the value of the uniqueVendorIdSequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueVendorIdSequence() {
        return uniqueVendorIdSequence;
    }

    /**
     * Sets the value of the uniqueVendorIdSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueVendorIdSequence(String value) {
        this.uniqueVendorIdSequence = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the address property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Address }
     * 
     * 
     */
    public List<Address> getAddress() {
        if (address == null) {
            address = new ArrayList<Address>();
        }
        return this.address;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phoneNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhoneNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhoneNumber }
     * 
     * 
     */
    public List<PhoneNumber> getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new ArrayList<PhoneNumber>();
        }
        return this.phoneNumber;
    }

    /**
     * Gets the value of the preferredPhone property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberType }
     *     
     */
    public PhoneNumberType getPreferredPhone() {
        return preferredPhone;
    }

    /**
     * Sets the value of the preferredPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneNumberType }
     *     
     */
    public void setPreferredPhone(PhoneNumberType value) {
        this.preferredPhone = value;
    }

    /**
     * Gets the value of the preferredOfficialLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link OfficialSpokenLanguageCode }
     *     
     */
    public OfficialSpokenLanguageCode getPreferredOfficialLanguage() {
        return preferredOfficialLanguage;
    }

    /**
     * Sets the value of the preferredOfficialLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link OfficialSpokenLanguageCode }
     *     
     */
    public void setPreferredOfficialLanguage(OfficialSpokenLanguageCode value) {
        this.preferredOfficialLanguage = value;
    }

    /**
     * Gets the value of the preferredSpokenLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredSpokenLanguage() {
        return preferredSpokenLanguage;
    }

    /**
     * Sets the value of the preferredSpokenLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredSpokenLanguage(String value) {
        this.preferredSpokenLanguage = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Demographics.Contact }
     * 
     * 
     */
    public List<Demographics.Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Demographics.Contact>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the noteAboutPatient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoteAboutPatient() {
        return noteAboutPatient;
    }

    /**
     * Sets the value of the noteAboutPatient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoteAboutPatient(String value) {
        this.noteAboutPatient = value;
    }

    /**
     * Gets the value of the patientWarningFlags property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientWarningFlags() {
        return patientWarningFlags;
    }

    /**
     * Sets the value of the patientWarningFlags property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientWarningFlags(String value) {
        this.patientWarningFlags = value;
    }

    /**
     * Gets the value of the enrollmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    /**
     * Sets the value of the enrollmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrollmentStatus(String value) {
        this.enrollmentStatus = value;
    }

    /**
     * Gets the value of the enrollmentDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the value of the enrollmentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setEnrollmentDate(DateFullOrPartial value) {
        this.enrollmentDate = value;
    }

    /**
     * Gets the value of the enrollmentTerminationDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getEnrollmentTerminationDate() {
        return enrollmentTerminationDate;
    }

    /**
     * Sets the value of the enrollmentTerminationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setEnrollmentTerminationDate(DateFullOrPartial value) {
        this.enrollmentTerminationDate = value;
    }

    /**
     * Gets the value of the terminationReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the value of the terminationReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminationReason(String value) {
        this.terminationReason = value;
    }

    /**
     * Gets the value of the primaryPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics.PrimaryPhysician }
     *     
     */
    public Demographics.PrimaryPhysician getPrimaryPhysician() {
        return primaryPhysician;
    }

    /**
     * Sets the value of the primaryPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics.PrimaryPhysician }
     *     
     */
    public void setPrimaryPhysician(Demographics.PrimaryPhysician value) {
        this.primaryPhysician = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the familyMemberLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyMemberLink() {
        return familyMemberLink;
    }

    /**
     * Sets the value of the familyMemberLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyMemberLink(String value) {
        this.familyMemberLink = value;
    }

    /**
     * Gets the value of the personStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link PersonStatus }
     *     
     */
    public PersonStatus getPersonStatusCode() {
        return personStatusCode;
    }

    /**
     * Sets the value of the personStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonStatus }
     *     
     */
    public void setPersonStatusCode(PersonStatus value) {
        this.personStatusCode = value;
    }

    /**
     * Gets the value of the personStatusDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getPersonStatusDate() {
        return personStatusDate;
    }

    /**
     * Sets the value of the personStatusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setPersonStatusDate(DateFullOrPartial value) {
        this.personStatusDate = value;
    }

    /**
     * Gets the value of the sin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIN() {
        return sin;
    }

    /**
     * Sets the value of the sin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIN(String value) {
        this.sin = value;
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
     *         &lt;element name="Name" type="{cds_dt}personNameSimpleWithMiddleName"/&gt;
     *         &lt;element name="PhoneNumber" type="{cds_dt}phoneNumber" maxOccurs="unbounded" minOccurs="0"/&gt;
     *         &lt;element name="EmailAddress" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
     *         &lt;element name="Note" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *               &lt;maxLength value="200"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="ContactPurpose" use="required" type="{cds_dt}contactPersonPurpose" /&gt;
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
        "phoneNumber",
        "emailAddress",
        "note"
    })
    public static class Contact {

        @XmlElement(name = "Name", namespace = "cds", required = true)
        protected PersonNameSimpleWithMiddleName name;
        @XmlElement(name = "PhoneNumber", namespace = "cds")
        protected List<PhoneNumber> phoneNumber;
        @XmlElement(name = "EmailAddress", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String emailAddress;
        @XmlElement(name = "Note", namespace = "cds")
        protected String note;
        @XmlAttribute(name = "ContactPurpose", namespace = "cds", required = true)
        protected ContactPersonPurpose contactPurpose;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link PersonNameSimpleWithMiddleName }
         *     
         */
        public PersonNameSimpleWithMiddleName getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonNameSimpleWithMiddleName }
         *     
         */
        public void setName(PersonNameSimpleWithMiddleName value) {
            this.name = value;
        }

        /**
         * Gets the value of the phoneNumber property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the phoneNumber property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPhoneNumber().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PhoneNumber }
         * 
         * 
         */
        public List<PhoneNumber> getPhoneNumber() {
            if (phoneNumber == null) {
                phoneNumber = new ArrayList<PhoneNumber>();
            }
            return this.phoneNumber;
        }

        /**
         * Gets the value of the emailAddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEmailAddress() {
            return emailAddress;
        }

        /**
         * Sets the value of the emailAddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEmailAddress(String value) {
            this.emailAddress = value;
        }

        /**
         * Gets the value of the note property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNote() {
            return note;
        }

        /**
         * Sets the value of the note property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNote(String value) {
            this.note = value;
        }

        /**
         * Gets the value of the contactPurpose property.
         * 
         * @return
         *     possible object is
         *     {@link ContactPersonPurpose }
         *     
         */
        public ContactPersonPurpose getContactPurpose() {
            return contactPurpose;
        }

        /**
         * Sets the value of the contactPurpose property.
         * 
         * @param value
         *     allowed object is
         *     {@link ContactPersonPurpose }
         *     
         */
        public void setContactPurpose(ContactPersonPurpose value) {
            this.contactPurpose = value;
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
     *       &lt;all&gt;
     *         &lt;element name="Name" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
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
    public static class PrimaryPhysician {

        @XmlElement(name = "Name", namespace = "cds")
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
