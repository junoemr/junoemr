
package xml.cds.v5_0;

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
 *         &lt;element name="ProblemDiagnosisDescription" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DiagnosisCode" type="{cds_dt}standardCoding" minOccurs="0"/&gt;
 *         &lt;element name="ProblemDescription" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ProblemStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="OnsetDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="LifeStage" type="{cds_dt}lifeStage" minOccurs="0"/&gt;
 *         &lt;element name="ResolutionDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{cds_dt}text64K" minOccurs="0"/&gt;
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
    "problemDiagnosisDescription",
    "diagnosisCode",
    "problemDescription",
    "problemStatus",
    "onsetDate",
    "lifeStage",
    "resolutionDate",
    "notes"
})
@XmlRootElement(name = "ProblemList", namespace = "cds")
public class ProblemList {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "ProblemDiagnosisDescription", namespace = "cds")
    protected String problemDiagnosisDescription;
    @XmlElement(name = "DiagnosisCode", namespace = "cds")
    protected StandardCoding diagnosisCode;
    @XmlElement(name = "ProblemDescription", namespace = "cds")
    protected String problemDescription;
    @XmlElement(name = "ProblemStatus", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String problemStatus;
    @XmlElement(name = "OnsetDate", namespace = "cds")
    protected DateFullOrPartial onsetDate;
    @XmlElement(name = "LifeStage", namespace = "cds")
    @XmlSchemaType(name = "string")
    protected LifeStage lifeStage;
    @XmlElement(name = "ResolutionDate", namespace = "cds")
    protected DateFullOrPartial resolutionDate;
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
     * Gets the value of the problemDiagnosisDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemDiagnosisDescription() {
        return problemDiagnosisDescription;
    }

    /**
     * Sets the value of the problemDiagnosisDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemDiagnosisDescription(String value) {
        this.problemDiagnosisDescription = value;
    }

    /**
     * Gets the value of the diagnosisCode property.
     * 
     * @return
     *     possible object is
     *     {@link StandardCoding }
     *     
     */
    public StandardCoding getDiagnosisCode() {
        return diagnosisCode;
    }

    /**
     * Sets the value of the diagnosisCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StandardCoding }
     *     
     */
    public void setDiagnosisCode(StandardCoding value) {
        this.diagnosisCode = value;
    }

    /**
     * Gets the value of the problemDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemDescription() {
        return problemDescription;
    }

    /**
     * Sets the value of the problemDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemDescription(String value) {
        this.problemDescription = value;
    }

    /**
     * Gets the value of the problemStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemStatus() {
        return problemStatus;
    }

    /**
     * Sets the value of the problemStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemStatus(String value) {
        this.problemStatus = value;
    }

    /**
     * Gets the value of the onsetDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getOnsetDate() {
        return onsetDate;
    }

    /**
     * Sets the value of the onsetDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setOnsetDate(DateFullOrPartial value) {
        this.onsetDate = value;
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
     * Gets the value of the resolutionDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getResolutionDate() {
        return resolutionDate;
    }

    /**
     * Sets the value of the resolutionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setResolutionDate(DateFullOrPartial value) {
        this.resolutionDate = value;
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
