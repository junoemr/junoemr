
package xml.cds.v5_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{cds}Demographics"/&gt;
 *         &lt;element ref="{cds}PersonalHistory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}FamilyHistory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}PastHealth" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}ProblemList" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}RiskFactors" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}AllergiesAndAdverseReactions" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}MedicationsAndTreatments" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}Immunizations" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}LaboratoryResults" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}Appointments" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}ClinicalNotes" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}Reports" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}CareElements" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}AlertsAndSpecialNeeds" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}NewCategory" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "demographics",
    "personalHistory",
    "familyHistory",
    "pastHealth",
    "problemList",
    "riskFactors",
    "allergiesAndAdverseReactions",
    "medicationsAndTreatments",
    "immunizations",
    "laboratoryResults",
    "appointments",
    "clinicalNotes",
    "reports",
    "careElements",
    "alertsAndSpecialNeeds",
    "newCategory"
})
@XmlRootElement(name = "PatientRecord", namespace = "cds")
public class PatientRecord {

    @XmlElement(name = "Demographics", namespace = "cds", required = true)
    protected Demographics demographics;
    @XmlElement(name = "PersonalHistory", namespace = "cds")
    protected List<PersonalHistory> personalHistory;
    @XmlElement(name = "FamilyHistory", namespace = "cds")
    protected List<FamilyHistory> familyHistory;
    @XmlElement(name = "PastHealth", namespace = "cds")
    protected List<PastHealth> pastHealth;
    @XmlElement(name = "ProblemList", namespace = "cds")
    protected List<ProblemList> problemList;
    @XmlElement(name = "RiskFactors", namespace = "cds")
    protected List<RiskFactors> riskFactors;
    @XmlElement(name = "AllergiesAndAdverseReactions", namespace = "cds")
    protected List<AllergiesAndAdverseReactions> allergiesAndAdverseReactions;
    @XmlElement(name = "MedicationsAndTreatments", namespace = "cds")
    protected List<MedicationsAndTreatments> medicationsAndTreatments;
    @XmlElement(name = "Immunizations", namespace = "cds")
    protected List<Immunizations> immunizations;
    @XmlElement(name = "LaboratoryResults", namespace = "cds")
    protected List<LaboratoryResults> laboratoryResults;
    @XmlElement(name = "Appointments", namespace = "cds")
    protected List<Appointments> appointments;
    @XmlElement(name = "ClinicalNotes", namespace = "cds")
    protected List<ClinicalNotes> clinicalNotes;
    @XmlElement(name = "Reports", namespace = "cds")
    protected List<Reports> reports;
    @XmlElement(name = "CareElements", namespace = "cds")
    protected List<CareElements> careElements;
    @XmlElement(name = "AlertsAndSpecialNeeds", namespace = "cds")
    protected List<AlertsAndSpecialNeeds> alertsAndSpecialNeeds;
    @XmlElement(name = "NewCategory", namespace = "cds")
    protected List<NewCategory> newCategory;

    /**
     * Gets the value of the demographics property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics }
     *     
     */
    public Demographics getDemographics() {
        return demographics;
    }

    /**
     * Sets the value of the demographics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics }
     *     
     */
    public void setDemographics(Demographics value) {
        this.demographics = value;
    }

    /**
     * Gets the value of the personalHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personalHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonalHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonalHistory }
     * 
     * 
     */
    public List<PersonalHistory> getPersonalHistory() {
        if (personalHistory == null) {
            personalHistory = new ArrayList<PersonalHistory>();
        }
        return this.personalHistory;
    }

    /**
     * Gets the value of the familyHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the familyHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFamilyHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FamilyHistory }
     * 
     * 
     */
    public List<FamilyHistory> getFamilyHistory() {
        if (familyHistory == null) {
            familyHistory = new ArrayList<FamilyHistory>();
        }
        return this.familyHistory;
    }

    /**
     * Gets the value of the pastHealth property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pastHealth property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPastHealth().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PastHealth }
     * 
     * 
     */
    public List<PastHealth> getPastHealth() {
        if (pastHealth == null) {
            pastHealth = new ArrayList<PastHealth>();
        }
        return this.pastHealth;
    }

    /**
     * Gets the value of the problemList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the problemList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProblemList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProblemList }
     * 
     * 
     */
    public List<ProblemList> getProblemList() {
        if (problemList == null) {
            problemList = new ArrayList<ProblemList>();
        }
        return this.problemList;
    }

    /**
     * Gets the value of the riskFactors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the riskFactors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRiskFactors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RiskFactors }
     * 
     * 
     */
    public List<RiskFactors> getRiskFactors() {
        if (riskFactors == null) {
            riskFactors = new ArrayList<RiskFactors>();
        }
        return this.riskFactors;
    }

    /**
     * Gets the value of the allergiesAndAdverseReactions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allergiesAndAdverseReactions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllergiesAndAdverseReactions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllergiesAndAdverseReactions }
     * 
     * 
     */
    public List<AllergiesAndAdverseReactions> getAllergiesAndAdverseReactions() {
        if (allergiesAndAdverseReactions == null) {
            allergiesAndAdverseReactions = new ArrayList<AllergiesAndAdverseReactions>();
        }
        return this.allergiesAndAdverseReactions;
    }

    /**
     * Gets the value of the medicationsAndTreatments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medicationsAndTreatments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedicationsAndTreatments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedicationsAndTreatments }
     * 
     * 
     */
    public List<MedicationsAndTreatments> getMedicationsAndTreatments() {
        if (medicationsAndTreatments == null) {
            medicationsAndTreatments = new ArrayList<MedicationsAndTreatments>();
        }
        return this.medicationsAndTreatments;
    }

    /**
     * Gets the value of the immunizations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the immunizations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImmunizations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Immunizations }
     * 
     * 
     */
    public List<Immunizations> getImmunizations() {
        if (immunizations == null) {
            immunizations = new ArrayList<Immunizations>();
        }
        return this.immunizations;
    }

    /**
     * Gets the value of the laboratoryResults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the laboratoryResults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaboratoryResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LaboratoryResults }
     * 
     * 
     */
    public List<LaboratoryResults> getLaboratoryResults() {
        if (laboratoryResults == null) {
            laboratoryResults = new ArrayList<LaboratoryResults>();
        }
        return this.laboratoryResults;
    }

    /**
     * Gets the value of the appointments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the appointments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAppointments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Appointments }
     * 
     * 
     */
    public List<Appointments> getAppointments() {
        if (appointments == null) {
            appointments = new ArrayList<Appointments>();
        }
        return this.appointments;
    }

    /**
     * Gets the value of the clinicalNotes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clinicalNotes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClinicalNotes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClinicalNotes }
     * 
     * 
     */
    public List<ClinicalNotes> getClinicalNotes() {
        if (clinicalNotes == null) {
            clinicalNotes = new ArrayList<ClinicalNotes>();
        }
        return this.clinicalNotes;
    }

    /**
     * Gets the value of the reports property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reports property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReports().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Reports }
     * 
     * 
     */
    public List<Reports> getReports() {
        if (reports == null) {
            reports = new ArrayList<Reports>();
        }
        return this.reports;
    }

    /**
     * Gets the value of the careElements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the careElements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCareElements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CareElements }
     * 
     * 
     */
    public List<CareElements> getCareElements() {
        if (careElements == null) {
            careElements = new ArrayList<CareElements>();
        }
        return this.careElements;
    }

    /**
     * Gets the value of the alertsAndSpecialNeeds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertsAndSpecialNeeds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertsAndSpecialNeeds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlertsAndSpecialNeeds }
     * 
     * 
     */
    public List<AlertsAndSpecialNeeds> getAlertsAndSpecialNeeds() {
        if (alertsAndSpecialNeeds == null) {
            alertsAndSpecialNeeds = new ArrayList<AlertsAndSpecialNeeds>();
        }
        return this.alertsAndSpecialNeeds;
    }

    /**
     * Gets the value of the newCategory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the newCategory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNewCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NewCategory }
     * 
     * 
     */
    public List<NewCategory> getNewCategory() {
        if (newCategory == null) {
            newCategory = new ArrayList<NewCategory>();
        }
        return this.newCategory;
    }

}
