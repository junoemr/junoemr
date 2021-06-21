
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
 *         &lt;element name="PastHealthProblemDescriptionOrProcedures" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DiagnosisProcedureCode" type="{cds_dt}standardCoding" minOccurs="0"/&gt;
 *         &lt;element name="OnsetOrEventDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="LifeStage" type="{cds_dt}lifeStage" minOccurs="0"/&gt;
 *         &lt;element name="ResolvedDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="ProcedureDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{cds_dt}text64K" minOccurs="0"/&gt;
 *         &lt;element name="ProblemStatus" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="50"/&gt;
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
    "residualInfo",
    "pastHealthProblemDescriptionOrProcedures",
    "diagnosisProcedureCode",
    "onsetOrEventDate",
    "lifeStage",
    "resolvedDate",
    "procedureDate",
    "notes",
    "problemStatus"
})
@XmlRootElement(name = "PastHealth", namespace = "cds")
public class PastHealth {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "PastHealthProblemDescriptionOrProcedures", namespace = "cds")
    protected String pastHealthProblemDescriptionOrProcedures;
    @XmlElement(name = "DiagnosisProcedureCode", namespace = "cds")
    protected StandardCoding diagnosisProcedureCode;
    @XmlElement(name = "OnsetOrEventDate", namespace = "cds")
    protected DateFullOrPartial onsetOrEventDate;
    @XmlElement(name = "LifeStage", namespace = "cds")
    @XmlSchemaType(name = "string")
    protected LifeStage lifeStage;
    @XmlElement(name = "ResolvedDate", namespace = "cds")
    protected DateFullOrPartial resolvedDate;
    @XmlElement(name = "ProcedureDate", namespace = "cds")
    protected DateFullOrPartial procedureDate;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;
    @XmlElement(name = "ProblemStatus", namespace = "cds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String problemStatus;

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
     * Gets the value of the pastHealthProblemDescriptionOrProcedures property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPastHealthProblemDescriptionOrProcedures() {
        return pastHealthProblemDescriptionOrProcedures;
    }

    /**
     * Sets the value of the pastHealthProblemDescriptionOrProcedures property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPastHealthProblemDescriptionOrProcedures(String value) {
        this.pastHealthProblemDescriptionOrProcedures = value;
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
     * Gets the value of the onsetOrEventDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getOnsetOrEventDate() {
        return onsetOrEventDate;
    }

    /**
     * Sets the value of the onsetOrEventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setOnsetOrEventDate(DateFullOrPartial value) {
        this.onsetOrEventDate = value;
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
     * Gets the value of the resolvedDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getResolvedDate() {
        return resolvedDate;
    }

    /**
     * Sets the value of the resolvedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setResolvedDate(DateFullOrPartial value) {
        this.resolvedDate = value;
    }

    /**
     * Gets the value of the procedureDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getProcedureDate() {
        return procedureDate;
    }

    /**
     * Sets the value of the procedureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setProcedureDate(DateFullOrPartial value) {
        this.procedureDate = value;
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

}
