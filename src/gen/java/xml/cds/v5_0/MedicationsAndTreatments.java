
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="ResidualInfo" type="{cds_dt}residualInformation" minOccurs="0"/&gt;
 *         &lt;element name="PrescriptionWrittenDate" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="DrugIdentificationNumber" type="{cds_dt}drugIdentificationNumber" minOccurs="0"/&gt;
 *         &lt;element name="DrugName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Strength" type="{cds_dt}drugMeasure" minOccurs="0"/&gt;
 *         &lt;element name="NumberOfRefills" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Dosage" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DosageUnitOfMeasure" type="{cds_dt}text50" minOccurs="0"/&gt;
 *         &lt;element name="Form" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Route" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Frequency" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Duration" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="1024"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RefillDuration" type="{cds_dt}text1K" minOccurs="0"/&gt;
 *         &lt;element name="Quantity" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="1024"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RefillQuantity" type="{cds_dt}text1K" minOccurs="0"/&gt;
 *         &lt;element name="LongTermMedication" type="{cds_dt}ynIndicator" minOccurs="0"/&gt;
 *         &lt;element name="PastMedications" type="{cds_dt}ynIndicator" minOccurs="0"/&gt;
 *         &lt;element name="PrescribedBy" minOccurs="0"&gt;
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
 *         &lt;element name="Notes" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="PrescriptionInstructions" type="{cds_dt}text32K" minOccurs="0"/&gt;
 *         &lt;element name="PatientCompliance" type="{cds_dt}ynIndicator" minOccurs="0"/&gt;
 *         &lt;element name="TreatmentType" type="{cds_dt}text50" minOccurs="0"/&gt;
 *         &lt;element name="PrescriptionStatus" type="{cds_dt}text10" minOccurs="0"/&gt;
 *         &lt;element name="NonAuthoritativeIndicator" type="{cds_dt}text1" minOccurs="0"/&gt;
 *         &lt;element name="PrescriptionIdentifier" type="{cds_dt}text50" minOccurs="0"/&gt;
 *         &lt;element name="PriorPrescriptionReferenceIdentifier" type="{cds_dt}text20" minOccurs="0"/&gt;
 *         &lt;element name="DispenseInterval" type="{cds_dt}text10" minOccurs="0"/&gt;
 *         &lt;element name="DrugDescription" type="{cds_dt}text2000" minOccurs="0"/&gt;
 *         &lt;element name="SubstitutionNotAllowed" type="{cds_dt}text1" minOccurs="0"/&gt;
 *         &lt;element name="ProblemCode" type="{cds_dt}text10" minOccurs="0"/&gt;
 *         &lt;element name="ProtocolIdentifier" type="{cds_dt}text20" minOccurs="0"/&gt;
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
    "residualInfo",
    "prescriptionWrittenDate",
    "startDate",
    "drugIdentificationNumber",
    "drugName",
    "strength",
    "numberOfRefills",
    "dosage",
    "dosageUnitOfMeasure",
    "form",
    "route",
    "frequency",
    "duration",
    "refillDuration",
    "quantity",
    "refillQuantity",
    "longTermMedication",
    "pastMedications",
    "prescribedBy",
    "notes",
    "prescriptionInstructions",
    "patientCompliance",
    "treatmentType",
    "prescriptionStatus",
    "nonAuthoritativeIndicator",
    "prescriptionIdentifier",
    "priorPrescriptionReferenceIdentifier",
    "dispenseInterval",
    "drugDescription",
    "substitutionNotAllowed",
    "problemCode",
    "protocolIdentifier"
})
@XmlRootElement(name = "MedicationsAndTreatments", namespace = "cds")
public class MedicationsAndTreatments {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "PrescriptionWrittenDate", namespace = "cds")
    protected DateTimeFullOrPartial prescriptionWrittenDate;
    @XmlElement(name = "StartDate", namespace = "cds")
    protected DateFullOrPartial startDate;
    @XmlElement(name = "DrugIdentificationNumber", namespace = "cds")
    protected String drugIdentificationNumber;
    @XmlElement(name = "DrugName", namespace = "cds")
    protected String drugName;
    @XmlElement(name = "Strength", namespace = "cds")
    protected DrugMeasure strength;
    @XmlElement(name = "NumberOfRefills", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String numberOfRefills;
    @XmlElement(name = "Dosage", namespace = "cds")
    protected String dosage;
    @XmlElement(name = "DosageUnitOfMeasure", namespace = "cds")
    protected String dosageUnitOfMeasure;
    @XmlElement(name = "Form", namespace = "cds")
    protected String form;
    @XmlElement(name = "Route", namespace = "cds")
    protected String route;
    @XmlElement(name = "Frequency", namespace = "cds")
    protected String frequency;
    @XmlElement(name = "Duration", namespace = "cds")
    protected String duration;
    @XmlElement(name = "RefillDuration", namespace = "cds")
    protected String refillDuration;
    @XmlElement(name = "Quantity", namespace = "cds")
    protected String quantity;
    @XmlElement(name = "RefillQuantity", namespace = "cds")
    protected String refillQuantity;
    @XmlElement(name = "LongTermMedication", namespace = "cds")
    protected YnIndicator longTermMedication;
    @XmlElement(name = "PastMedications", namespace = "cds")
    protected YnIndicator pastMedications;
    @XmlElement(name = "PrescribedBy", namespace = "cds")
    protected MedicationsAndTreatments.PrescribedBy prescribedBy;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;
    @XmlElement(name = "PrescriptionInstructions", namespace = "cds")
    protected String prescriptionInstructions;
    @XmlElement(name = "PatientCompliance", namespace = "cds")
    protected YnIndicator patientCompliance;
    @XmlElement(name = "TreatmentType", namespace = "cds")
    protected String treatmentType;
    @XmlElement(name = "PrescriptionStatus", namespace = "cds")
    protected String prescriptionStatus;
    @XmlElement(name = "NonAuthoritativeIndicator", namespace = "cds")
    protected String nonAuthoritativeIndicator;
    @XmlElement(name = "PrescriptionIdentifier", namespace = "cds")
    protected String prescriptionIdentifier;
    @XmlElement(name = "PriorPrescriptionReferenceIdentifier", namespace = "cds")
    protected String priorPrescriptionReferenceIdentifier;
    @XmlElement(name = "DispenseInterval", namespace = "cds")
    protected String dispenseInterval;
    @XmlElement(name = "DrugDescription", namespace = "cds")
    protected String drugDescription;
    @XmlElement(name = "SubstitutionNotAllowed", namespace = "cds")
    protected String substitutionNotAllowed;
    @XmlElement(name = "ProblemCode", namespace = "cds")
    protected String problemCode;
    @XmlElement(name = "ProtocolIdentifier", namespace = "cds")
    protected String protocolIdentifier;

    /**
     * Gets the value of the residualInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ResidualInformation }
     *     
     */
    public ResidualInformation getResidualInfo() {
        return residualInfo;
    }

    /**
     * Sets the value of the residualInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResidualInformation }
     *     
     */
    public void setResidualInfo(ResidualInformation value) {
        this.residualInfo = value;
    }

    /**
     * Gets the value of the prescriptionWrittenDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getPrescriptionWrittenDate() {
        return prescriptionWrittenDate;
    }

    /**
     * Sets the value of the prescriptionWrittenDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setPrescriptionWrittenDate(DateTimeFullOrPartial value) {
        this.prescriptionWrittenDate = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setStartDate(DateFullOrPartial value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the drugIdentificationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugIdentificationNumber() {
        return drugIdentificationNumber;
    }

    /**
     * Sets the value of the drugIdentificationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugIdentificationNumber(String value) {
        this.drugIdentificationNumber = value;
    }

    /**
     * Gets the value of the drugName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * Sets the value of the drugName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugName(String value) {
        this.drugName = value;
    }

    /**
     * Gets the value of the strength property.
     * 
     * @return
     *     possible object is
     *     {@link DrugMeasure }
     *     
     */
    public DrugMeasure getStrength() {
        return strength;
    }

    /**
     * Sets the value of the strength property.
     * 
     * @param value
     *     allowed object is
     *     {@link DrugMeasure }
     *     
     */
    public void setStrength(DrugMeasure value) {
        this.strength = value;
    }

    /**
     * Gets the value of the numberOfRefills property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfRefills() {
        return numberOfRefills;
    }

    /**
     * Sets the value of the numberOfRefills property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfRefills(String value) {
        this.numberOfRefills = value;
    }

    /**
     * Gets the value of the dosage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Sets the value of the dosage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDosage(String value) {
        this.dosage = value;
    }

    /**
     * Gets the value of the dosageUnitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDosageUnitOfMeasure() {
        return dosageUnitOfMeasure;
    }

    /**
     * Sets the value of the dosageUnitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDosageUnitOfMeasure(String value) {
        this.dosageUnitOfMeasure = value;
    }

    /**
     * Gets the value of the form property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForm() {
        return form;
    }

    /**
     * Sets the value of the form property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForm(String value) {
        this.form = value;
    }

    /**
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoute(String value) {
        this.route = value;
    }

    /**
     * Gets the value of the frequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the value of the frequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequency(String value) {
        this.frequency = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Gets the value of the refillDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefillDuration() {
        return refillDuration;
    }

    /**
     * Sets the value of the refillDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefillDuration(String value) {
        this.refillDuration = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the refillQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefillQuantity() {
        return refillQuantity;
    }

    /**
     * Sets the value of the refillQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefillQuantity(String value) {
        this.refillQuantity = value;
    }

    /**
     * Gets the value of the longTermMedication property.
     * 
     * @return
     *     possible object is
     *     {@link YnIndicator }
     *     
     */
    public YnIndicator getLongTermMedication() {
        return longTermMedication;
    }

    /**
     * Sets the value of the longTermMedication property.
     * 
     * @param value
     *     allowed object is
     *     {@link YnIndicator }
     *     
     */
    public void setLongTermMedication(YnIndicator value) {
        this.longTermMedication = value;
    }

    /**
     * Gets the value of the pastMedications property.
     * 
     * @return
     *     possible object is
     *     {@link YnIndicator }
     *     
     */
    public YnIndicator getPastMedications() {
        return pastMedications;
    }

    /**
     * Sets the value of the pastMedications property.
     * 
     * @param value
     *     allowed object is
     *     {@link YnIndicator }
     *     
     */
    public void setPastMedications(YnIndicator value) {
        this.pastMedications = value;
    }

    /**
     * Gets the value of the prescribedBy property.
     * 
     * @return
     *     possible object is
     *     {@link MedicationsAndTreatments.PrescribedBy }
     *     
     */
    public MedicationsAndTreatments.PrescribedBy getPrescribedBy() {
        return prescribedBy;
    }

    /**
     * Sets the value of the prescribedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedicationsAndTreatments.PrescribedBy }
     *     
     */
    public void setPrescribedBy(MedicationsAndTreatments.PrescribedBy value) {
        this.prescribedBy = value;
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
     * Gets the value of the prescriptionInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescriptionInstructions() {
        return prescriptionInstructions;
    }

    /**
     * Sets the value of the prescriptionInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescriptionInstructions(String value) {
        this.prescriptionInstructions = value;
    }

    /**
     * Gets the value of the patientCompliance property.
     * 
     * @return
     *     possible object is
     *     {@link YnIndicator }
     *     
     */
    public YnIndicator getPatientCompliance() {
        return patientCompliance;
    }

    /**
     * Sets the value of the patientCompliance property.
     * 
     * @param value
     *     allowed object is
     *     {@link YnIndicator }
     *     
     */
    public void setPatientCompliance(YnIndicator value) {
        this.patientCompliance = value;
    }

    /**
     * Gets the value of the treatmentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatmentType() {
        return treatmentType;
    }

    /**
     * Sets the value of the treatmentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatmentType(String value) {
        this.treatmentType = value;
    }

    /**
     * Gets the value of the prescriptionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescriptionStatus() {
        return prescriptionStatus;
    }

    /**
     * Sets the value of the prescriptionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescriptionStatus(String value) {
        this.prescriptionStatus = value;
    }

    /**
     * Gets the value of the nonAuthoritativeIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonAuthoritativeIndicator() {
        return nonAuthoritativeIndicator;
    }

    /**
     * Sets the value of the nonAuthoritativeIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonAuthoritativeIndicator(String value) {
        this.nonAuthoritativeIndicator = value;
    }

    /**
     * Gets the value of the prescriptionIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescriptionIdentifier() {
        return prescriptionIdentifier;
    }

    /**
     * Sets the value of the prescriptionIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescriptionIdentifier(String value) {
        this.prescriptionIdentifier = value;
    }

    /**
     * Gets the value of the priorPrescriptionReferenceIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriorPrescriptionReferenceIdentifier() {
        return priorPrescriptionReferenceIdentifier;
    }

    /**
     * Sets the value of the priorPrescriptionReferenceIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorPrescriptionReferenceIdentifier(String value) {
        this.priorPrescriptionReferenceIdentifier = value;
    }

    /**
     * Gets the value of the dispenseInterval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispenseInterval() {
        return dispenseInterval;
    }

    /**
     * Sets the value of the dispenseInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispenseInterval(String value) {
        this.dispenseInterval = value;
    }

    /**
     * Gets the value of the drugDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugDescription() {
        return drugDescription;
    }

    /**
     * Sets the value of the drugDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugDescription(String value) {
        this.drugDescription = value;
    }

    /**
     * Gets the value of the substitutionNotAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubstitutionNotAllowed() {
        return substitutionNotAllowed;
    }

    /**
     * Sets the value of the substitutionNotAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubstitutionNotAllowed(String value) {
        this.substitutionNotAllowed = value;
    }

    /**
     * Gets the value of the problemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemCode() {
        return problemCode;
    }

    /**
     * Sets the value of the problemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemCode(String value) {
        this.problemCode = value;
    }

    /**
     * Gets the value of the protocolIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolIdentifier() {
        return protocolIdentifier;
    }

    /**
     * Sets the value of the protocolIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolIdentifier(String value) {
        this.protocolIdentifier = value;
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
    public static class PrescribedBy {

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
