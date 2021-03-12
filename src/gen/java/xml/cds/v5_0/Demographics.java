
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
import javax.xml.datatype.XMLGregorianCalendar;
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
 *         &lt;element name="DateOfBirth" type="{cds_dt}dateYYYYMMDD"/&gt;
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
 *         &lt;element name="PreferredOfficialLanguage" type="{cds_dt}officialSpokenLanguageCode" minOccurs="0"/&gt;
 *         &lt;element name="PreferredSpokenLanguage" type="{cds_dt}spokenLanguageCode" minOccurs="0"/&gt;
 *         &lt;element name="Contact" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ContactPurpose" type="{cds_dt}purposeEnumOrPlainText" maxOccurs="unbounded" minOccurs="0"/&gt;
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
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NoteAboutPatient" type="{cds_dt}text64K" minOccurs="0"/&gt;
 *         &lt;element name="Enrolment" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="EnrolmentHistory" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence minOccurs="0"&gt;
 *                             &lt;element name="EnrollmentStatus" type="{cds_dt}enrollmentStatus"/&gt;
 *                             &lt;element name="EnrollmentDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
 *                             &lt;element name="EnrollmentTerminationDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
 *                             &lt;element name="TerminationReason" type="{cds_dt}terminationReasonCode" minOccurs="0"/&gt;
 *                             &lt;element name="EnrolledToPhysician" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;all&gt;
 *                                       &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
 *                                       &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
 *                                         &lt;simpleType&gt;
 *                                           &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
 *                                           &lt;/restriction&gt;
 *                                         &lt;/simpleType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/all&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PrimaryPhysician" minOccurs="0"&gt;
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
 *                   &lt;element name="PrimaryPhysicianCPSO" type="{cds_dt}primaryPhysicianCPSO" minOccurs="0"/&gt;
 *                 &lt;/all&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Email" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
 *         &lt;element name="PersonStatusCode"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="PersonStatusAsEnum" type="{cds_dt}personStatus"/&gt;
 *                   &lt;element name="PersonStatusAsPlainText"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                         &lt;maxLength value="50"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/choice&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PersonStatusDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
 *         &lt;element name="SIN" type="{cds_dt}socialInsuranceNumber" minOccurs="0"/&gt;
 *         &lt;element name="ReferredPhysician" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
 *         &lt;element name="FamilyPhysician" type="{cds_dt}personNameSimple" minOccurs="0"/&gt;
 *         &lt;element name="PreferredPharmacy" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Name"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{cds_dt}text"&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="Address" type="{cds_dt}address" minOccurs="0"/&gt;
 *                   &lt;element name="PhoneNumber" type="{cds_dt}phoneNumber" minOccurs="0"/&gt;
 *                   &lt;element name="FaxNumber" type="{cds_dt}phoneNumber" minOccurs="0"/&gt;
 *                   &lt;element name="EmailAddress" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
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
    "names",
    "dateOfBirth",
    "healthCard",
    "chartNumber",
    "gender",
    "uniqueVendorIdSequence",
    "address",
    "phoneNumber",
    "preferredOfficialLanguage",
    "preferredSpokenLanguage",
    "contact",
    "noteAboutPatient",
    "enrolment",
    "primaryPhysician",
    "email",
    "personStatusCode",
    "personStatusDate",
    "sin",
    "referredPhysician",
    "familyPhysician",
    "preferredPharmacy"
})
@XmlRootElement(name = "Demographics", namespace = "cds")
public class Demographics
    implements CDSDemographicInterface
{

    @XmlElement(name = "Names", namespace = "cds", required = true)
    protected PersonNameStandard names;
    @XmlElement(name = "DateOfBirth", namespace = "cds", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
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
    @XmlElement(name = "Enrolment", namespace = "cds")
    protected Demographics.Enrolment enrolment;
    @XmlElement(name = "PrimaryPhysician", namespace = "cds")
    protected Demographics.PrimaryPhysician primaryPhysician;
    @XmlElement(name = "Email", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String email;
    @XmlElement(name = "PersonStatusCode", namespace = "cds", required = true)
    protected Demographics.PersonStatusCode personStatusCode;
    @XmlElement(name = "PersonStatusDate", namespace = "cds")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar personStatusDate;
    @XmlElement(name = "SIN", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String sin;
    @XmlElement(name = "ReferredPhysician", namespace = "cds")
    protected PersonNameSimple referredPhysician;
    @XmlElement(name = "FamilyPhysician", namespace = "cds")
    protected PersonNameSimple familyPhysician;
    @XmlElement(name = "PreferredPharmacy", namespace = "cds")
    protected Demographics.PreferredPharmacy preferredPharmacy;

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
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
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
     * Gets the value of the enrolment property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics.Enrolment }
     *     
     */
    public Demographics.Enrolment getEnrolment() {
        return enrolment;
    }

    /**
     * Sets the value of the enrolment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics.Enrolment }
     *     
     */
    public void setEnrolment(Demographics.Enrolment value) {
        this.enrolment = value;
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
     * Gets the value of the personStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics.PersonStatusCode }
     *     
     */
    public Demographics.PersonStatusCode getPersonStatusCode() {
        return personStatusCode;
    }

    /**
     * Sets the value of the personStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics.PersonStatusCode }
     *     
     */
    public void setPersonStatusCode(Demographics.PersonStatusCode value) {
        this.personStatusCode = value;
    }

    /**
     * Gets the value of the personStatusDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPersonStatusDate() {
        return personStatusDate;
    }

    /**
     * Sets the value of the personStatusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPersonStatusDate(XMLGregorianCalendar value) {
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
     * Gets the value of the referredPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSimple }
     *     
     */
    public PersonNameSimple getReferredPhysician() {
        return referredPhysician;
    }

    /**
     * Sets the value of the referredPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSimple }
     *     
     */
    public void setReferredPhysician(PersonNameSimple value) {
        this.referredPhysician = value;
    }

    /**
     * Gets the value of the familyPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSimple }
     *     
     */
    public PersonNameSimple getFamilyPhysician() {
        return familyPhysician;
    }

    /**
     * Sets the value of the familyPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSimple }
     *     
     */
    public void setFamilyPhysician(PersonNameSimple value) {
        this.familyPhysician = value;
    }

    /**
     * Gets the value of the preferredPharmacy property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics.PreferredPharmacy }
     *     
     */
    public Demographics.PreferredPharmacy getPreferredPharmacy() {
        return preferredPharmacy;
    }

    /**
     * Sets the value of the preferredPharmacy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics.PreferredPharmacy }
     *     
     */
    public void setPreferredPharmacy(Demographics.PreferredPharmacy value) {
        this.preferredPharmacy = value;
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
     *         &lt;element name="ContactPurpose" type="{cds_dt}purposeEnumOrPlainText" maxOccurs="unbounded" minOccurs="0"/&gt;
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
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "contactPurpose",
        "name",
        "phoneNumber",
        "emailAddress",
        "note"
    })
    public static class Contact {

        @XmlElement(name = "ContactPurpose", namespace = "cds")
        protected List<PurposeEnumOrPlainText> contactPurpose;
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

        /**
         * Gets the value of the contactPurpose property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the contactPurpose property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getContactPurpose().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PurposeEnumOrPlainText }
         * 
         * 
         */
        public List<PurposeEnumOrPlainText> getContactPurpose() {
            if (contactPurpose == null) {
                contactPurpose = new ArrayList<PurposeEnumOrPlainText>();
            }
            return this.contactPurpose;
        }

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
     *         &lt;element name="EnrolmentHistory" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence minOccurs="0"&gt;
     *                   &lt;element name="EnrollmentStatus" type="{cds_dt}enrollmentStatus"/&gt;
     *                   &lt;element name="EnrollmentDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
     *                   &lt;element name="EnrollmentTerminationDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
     *                   &lt;element name="TerminationReason" type="{cds_dt}terminationReasonCode" minOccurs="0"/&gt;
     *                   &lt;element name="EnrolledToPhysician" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;all&gt;
     *                             &lt;element name="Name" type="{cds_dt}personNameSimple"/&gt;
     *                             &lt;element name="OHIPPhysicianId" minOccurs="0"&gt;
     *                               &lt;simpleType&gt;
     *                                 &lt;restriction base="{cds_dt}ohipPhysicianBillingNumber"&gt;
     *                                 &lt;/restriction&gt;
     *                               &lt;/simpleType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/all&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
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
        "enrolmentHistory"
    })
    public static class Enrolment {

        @XmlElement(name = "EnrolmentHistory", namespace = "cds")
        protected List<Demographics.Enrolment.EnrolmentHistory> enrolmentHistory;

        /**
         * Gets the value of the enrolmentHistory property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the enrolmentHistory property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEnrolmentHistory().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Demographics.Enrolment.EnrolmentHistory }
         * 
         * 
         */
        public List<Demographics.Enrolment.EnrolmentHistory> getEnrolmentHistory() {
            if (enrolmentHistory == null) {
                enrolmentHistory = new ArrayList<Demographics.Enrolment.EnrolmentHistory>();
            }
            return this.enrolmentHistory;
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
         *         &lt;element name="EnrollmentStatus" type="{cds_dt}enrollmentStatus"/&gt;
         *         &lt;element name="EnrollmentDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
         *         &lt;element name="EnrollmentTerminationDate" type="{cds_dt}dateYYYYMMDD" minOccurs="0"/&gt;
         *         &lt;element name="TerminationReason" type="{cds_dt}terminationReasonCode" minOccurs="0"/&gt;
         *         &lt;element name="EnrolledToPhysician" minOccurs="0"&gt;
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
            "enrollmentStatus",
            "enrollmentDate",
            "enrollmentTerminationDate",
            "terminationReason",
            "enrolledToPhysician"
        })
        public static class EnrolmentHistory {

            @XmlElement(name = "EnrollmentStatus", namespace = "cds")
            protected String enrollmentStatus;
            @XmlElement(name = "EnrollmentDate", namespace = "cds")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar enrollmentDate;
            @XmlElement(name = "EnrollmentTerminationDate", namespace = "cds")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar enrollmentTerminationDate;
            @XmlElement(name = "TerminationReason", namespace = "cds")
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            protected String terminationReason;
            @XmlElement(name = "EnrolledToPhysician", namespace = "cds")
            protected Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician enrolledToPhysician;

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
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getEnrollmentDate() {
                return enrollmentDate;
            }

            /**
             * Sets the value of the enrollmentDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setEnrollmentDate(XMLGregorianCalendar value) {
                this.enrollmentDate = value;
            }

            /**
             * Gets the value of the enrollmentTerminationDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getEnrollmentTerminationDate() {
                return enrollmentTerminationDate;
            }

            /**
             * Sets the value of the enrollmentTerminationDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setEnrollmentTerminationDate(XMLGregorianCalendar value) {
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
             * Gets the value of the enrolledToPhysician property.
             * 
             * @return
             *     possible object is
             *     {@link Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician }
             *     
             */
            public Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician getEnrolledToPhysician() {
                return enrolledToPhysician;
            }

            /**
             * Sets the value of the enrolledToPhysician property.
             * 
             * @param value
             *     allowed object is
             *     {@link Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician }
             *     
             */
            public void setEnrolledToPhysician(Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician value) {
                this.enrolledToPhysician = value;
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
            public static class EnrolledToPhysician {

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
     *         &lt;element name="PersonStatusAsEnum" type="{cds_dt}personStatus"/&gt;
     *         &lt;element name="PersonStatusAsPlainText"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *               &lt;maxLength value="50"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
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
        "personStatusAsEnum",
        "personStatusAsPlainText"
    })
    public static class PersonStatusCode {

        @XmlElement(name = "PersonStatusAsEnum", namespace = "cds")
        @XmlSchemaType(name = "string")
        protected PersonStatus personStatusAsEnum;
        @XmlElement(name = "PersonStatusAsPlainText", namespace = "cds")
        protected String personStatusAsPlainText;

        /**
         * Gets the value of the personStatusAsEnum property.
         * 
         * @return
         *     possible object is
         *     {@link PersonStatus }
         *     
         */
        public PersonStatus getPersonStatusAsEnum() {
            return personStatusAsEnum;
        }

        /**
         * Sets the value of the personStatusAsEnum property.
         * 
         * @param value
         *     allowed object is
         *     {@link PersonStatus }
         *     
         */
        public void setPersonStatusAsEnum(PersonStatus value) {
            this.personStatusAsEnum = value;
        }

        /**
         * Gets the value of the personStatusAsPlainText property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPersonStatusAsPlainText() {
            return personStatusAsPlainText;
        }

        /**
         * Sets the value of the personStatusAsPlainText property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPersonStatusAsPlainText(String value) {
            this.personStatusAsPlainText = value;
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
     *       &lt;sequence&gt;
     *         &lt;element name="Name"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{cds_dt}text"&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Address" type="{cds_dt}address" minOccurs="0"/&gt;
     *         &lt;element name="PhoneNumber" type="{cds_dt}phoneNumber" minOccurs="0"/&gt;
     *         &lt;element name="FaxNumber" type="{cds_dt}phoneNumber" minOccurs="0"/&gt;
     *         &lt;element name="EmailAddress" type="{cds_dt}emailAddress" minOccurs="0"/&gt;
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
        "address",
        "phoneNumber",
        "faxNumber",
        "emailAddress"
    })
    public static class PreferredPharmacy {

        @XmlElement(name = "Name", namespace = "cds", required = true)
        protected String name;
        @XmlElement(name = "Address", namespace = "cds")
        protected Address address;
        @XmlElement(name = "PhoneNumber", namespace = "cds")
        protected PhoneNumber phoneNumber;
        @XmlElement(name = "FaxNumber", namespace = "cds")
        protected PhoneNumber faxNumber;
        @XmlElement(name = "EmailAddress", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String emailAddress;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the address property.
         * 
         * @return
         *     possible object is
         *     {@link Address }
         *     
         */
        public Address getAddress() {
            return address;
        }

        /**
         * Sets the value of the address property.
         * 
         * @param value
         *     allowed object is
         *     {@link Address }
         *     
         */
        public void setAddress(Address value) {
            this.address = value;
        }

        /**
         * Gets the value of the phoneNumber property.
         * 
         * @return
         *     possible object is
         *     {@link PhoneNumber }
         *     
         */
        public PhoneNumber getPhoneNumber() {
            return phoneNumber;
        }

        /**
         * Sets the value of the phoneNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link PhoneNumber }
         *     
         */
        public void setPhoneNumber(PhoneNumber value) {
            this.phoneNumber = value;
        }

        /**
         * Gets the value of the faxNumber property.
         * 
         * @return
         *     possible object is
         *     {@link PhoneNumber }
         *     
         */
        public PhoneNumber getFaxNumber() {
            return faxNumber;
        }

        /**
         * Sets the value of the faxNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link PhoneNumber }
         *     
         */
        public void setFaxNumber(PhoneNumber value) {
            this.faxNumber = value;
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
     *         &lt;element name="PrimaryPhysicianCPSO" type="{cds_dt}primaryPhysicianCPSO" minOccurs="0"/&gt;
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

        @XmlElement(name = "Name", namespace = "cds", required = true)
        protected PersonNameSimple name;
        @XmlElement(name = "OHIPPhysicianId", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ohipPhysicianId;
        @XmlElement(name = "PrimaryPhysicianCPSO", namespace = "cds")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String primaryPhysicianCPSO;

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
         * Gets the value of the primaryPhysicianCPSO property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPrimaryPhysicianCPSO() {
            return primaryPhysicianCPSO;
        }

        /**
         * Sets the value of the primaryPhysicianCPSO property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPrimaryPhysicianCPSO(String value) {
            this.primaryPhysicianCPSO = value;
        }

    }

}
