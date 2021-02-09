
package xml.cds.v5_0;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="ResidualInfo" type="{cds_dt}residualInformation" minOccurs="0"/&gt;
 *         &lt;element name="RiskFactor" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ExposureDetails" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AgeOfOnset" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="LifeStage" type="{cds_dt}lifeStage" minOccurs="0"/&gt;
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
    "riskFactor",
    "exposureDetails",
    "ageOfOnset",
    "startDate",
    "endDate",
    "lifeStage",
    "notes"
})
@XmlRootElement(name = "RiskFactors", namespace = "cds")
public class RiskFactors {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "RiskFactor", namespace = "cds")
    protected String riskFactor;
    @XmlElement(name = "ExposureDetails", namespace = "cds")
    protected String exposureDetails;
    @XmlElement(name = "AgeOfOnset", namespace = "cds")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger ageOfOnset;
    @XmlElement(name = "StartDate", namespace = "cds")
    protected DateFullOrPartial startDate;
    @XmlElement(name = "EndDate", namespace = "cds")
    protected DateFullOrPartial endDate;
    @XmlElement(name = "LifeStage", namespace = "cds")
    @XmlSchemaType(name = "string")
    protected LifeStage lifeStage;
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
     * Gets the value of the riskFactor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskFactor() {
        return riskFactor;
    }

    /**
     * Sets the value of the riskFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskFactor(String value) {
        this.riskFactor = value;
    }

    /**
     * Gets the value of the exposureDetails property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExposureDetails() {
        return exposureDetails;
    }

    /**
     * Sets the value of the exposureDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExposureDetails(String value) {
        this.exposureDetails = value;
    }

    /**
     * Gets the value of the ageOfOnset property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAgeOfOnset() {
        return ageOfOnset;
    }

    /**
     * Sets the value of the ageOfOnset property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAgeOfOnset(BigInteger value) {
        this.ageOfOnset = value;
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
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setEndDate(DateFullOrPartial value) {
        this.endDate = value;
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
