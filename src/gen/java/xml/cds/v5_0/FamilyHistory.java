
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
 *         &lt;element name="StartDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="AgeAtOnset" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="LifeStage" type="{cds_dt}lifeStage" minOccurs="0"/&gt;
 *         &lt;element name="ProblemDiagnosisProcedureDescription" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DiagnosisProcedureCode" type="{cds_dt}standardCoding" minOccurs="0"/&gt;
 *         &lt;element name="Treatment" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Relationship" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Notes" type="{cds_dt}text32K" minOccurs="0"/&gt;
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
    "startDate",
    "ageAtOnset",
    "lifeStage",
    "problemDiagnosisProcedureDescription",
    "diagnosisProcedureCode",
    "treatment",
    "relationship",
    "notes"
})
@XmlRootElement(name = "FamilyHistory", namespace = "cds")
public class FamilyHistory {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "StartDate", namespace = "cds")
    protected DateFullOrPartial startDate;
    @XmlElement(name = "AgeAtOnset", namespace = "cds")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger ageAtOnset;
    @XmlElement(name = "LifeStage", namespace = "cds")
    @XmlSchemaType(name = "string")
    protected LifeStage lifeStage;
    @XmlElement(name = "ProblemDiagnosisProcedureDescription", namespace = "cds")
    protected String problemDiagnosisProcedureDescription;
    @XmlElement(name = "DiagnosisProcedureCode", namespace = "cds")
    protected StandardCoding diagnosisProcedureCode;
    @XmlElement(name = "Treatment", namespace = "cds")
    protected String treatment;
    @XmlElement(name = "Relationship", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String relationship;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;

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
     * Gets the value of the ageAtOnset property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAgeAtOnset() {
        return ageAtOnset;
    }

    /**
     * Sets the value of the ageAtOnset property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAgeAtOnset(BigInteger value) {
        this.ageAtOnset = value;
    }

    /**
     * Gets the value of the lifeStage property.
     * 
     * @return
     *     possible object is
     *     {@link LifeStage }
     *     
     */
    public LifeStage getLifeStage() {
        return lifeStage;
    }

    /**
     * Sets the value of the lifeStage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LifeStage }
     *     
     */
    public void setLifeStage(LifeStage value) {
        this.lifeStage = value;
    }

    /**
     * Gets the value of the problemDiagnosisProcedureDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemDiagnosisProcedureDescription() {
        return problemDiagnosisProcedureDescription;
    }

    /**
     * Sets the value of the problemDiagnosisProcedureDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemDiagnosisProcedureDescription(String value) {
        this.problemDiagnosisProcedureDescription = value;
    }

    /**
     * Gets the value of the diagnosisProcedureCode property.
     * 
     * @return
     *     possible object is
     *     {@link StandardCoding }
     *     
     */
    public StandardCoding getDiagnosisProcedureCode() {
        return diagnosisProcedureCode;
    }

    /**
     * Sets the value of the diagnosisProcedureCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StandardCoding }
     *     
     */
    public void setDiagnosisProcedureCode(StandardCoding value) {
        this.diagnosisProcedureCode = value;
    }

    /**
     * Gets the value of the treatment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Sets the value of the treatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatment(String value) {
        this.treatment = value;
    }

    /**
     * Gets the value of the relationship property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationship() {
        return relationship;
    }

    /**
     * Sets the value of the relationship property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationship(String value) {
        this.relationship = value;
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

}
