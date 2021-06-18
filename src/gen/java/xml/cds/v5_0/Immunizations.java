
package xml.cds.v5_0;

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
 *         &lt;element name="ImmunizationName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ImmunizationType" type="{cds_dt}immunizationType" minOccurs="0"/&gt;
 *         &lt;element name="Manufacturer" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LotNumber" minOccurs="0"&gt;
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
 *         &lt;element name="Site" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Dose" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="120"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ImmunizationCode" type="{cds_dt}code" minOccurs="0"/&gt;
 *         &lt;element name="Date" type="{cds_dt}dateTimeFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="RefusedFlag" type="{cds_dt}ynIndicator"/&gt;
 *         &lt;element name="Instructions" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
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
    "immunizationName",
    "immunizationType",
    "manufacturer",
    "lotNumber",
    "route",
    "site",
    "dose",
    "immunizationCode",
    "date",
    "refusedFlag",
    "instructions",
    "notes"
})
@XmlRootElement(name = "Immunizations", namespace = "cds")
public class Immunizations {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "ImmunizationName", namespace = "cds", required = true)
    protected String immunizationName;
    @XmlElement(name = "ImmunizationType", namespace = "cds")
    @XmlSchemaType(name = "token")
    protected ImmunizationType immunizationType;
    @XmlElement(name = "Manufacturer", namespace = "cds")
    protected String manufacturer;
    @XmlElement(name = "LotNumber", namespace = "cds")
    protected String lotNumber;
    @XmlElement(name = "Route", namespace = "cds")
    protected String route;
    @XmlElement(name = "Site", namespace = "cds")
    protected String site;
    @XmlElement(name = "Dose", namespace = "cds")
    protected String dose;
    @XmlElement(name = "ImmunizationCode", namespace = "cds")
    protected Code immunizationCode;
    @XmlElement(name = "Date", namespace = "cds")
    protected DateTimeFullOrPartial date;
    @XmlElement(name = "RefusedFlag", namespace = "cds", required = true)
    protected YnIndicator refusedFlag;
    @XmlElement(name = "Instructions", namespace = "cds")
    protected String instructions;
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
     * Gets the value of the immunizationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImmunizationName() {
        return immunizationName;
    }

    /**
     * Sets the value of the immunizationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImmunizationName(String value) {
        this.immunizationName = value;
    }

    /**
     * Gets the value of the immunizationType property.
     * 
     * @return
     *     possible object is
     *     {@link ImmunizationType }
     *     
     */
    public ImmunizationType getImmunizationType() {
        return immunizationType;
    }

    /**
     * Sets the value of the immunizationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImmunizationType }
     *     
     */
    public void setImmunizationType(ImmunizationType value) {
        this.immunizationType = value;
    }

    /**
     * Gets the value of the manufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the lotNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * Sets the value of the lotNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLotNumber(String value) {
        this.lotNumber = value;
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
     * Gets the value of the site property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets the value of the site property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSite(String value) {
        this.site = value;
    }

    /**
     * Gets the value of the dose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDose() {
        return dose;
    }

    /**
     * Sets the value of the dose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDose(String value) {
        this.dose = value;
    }

    /**
     * Gets the value of the immunizationCode property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getImmunizationCode() {
        return immunizationCode;
    }

    /**
     * Sets the value of the immunizationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setImmunizationCode(Code value) {
        this.immunizationCode = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public DateTimeFullOrPartial getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeFullOrPartial }
     *     
     */
    public void setDate(DateTimeFullOrPartial value) {
        this.date = value;
    }

    /**
     * Gets the value of the refusedFlag property.
     * 
     * @return
     *     possible object is
     *     {@link YnIndicator }
     *     
     */
    public YnIndicator getRefusedFlag() {
        return refusedFlag;
    }

    /**
     * Sets the value of the refusedFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link YnIndicator }
     *     
     */
    public void setRefusedFlag(YnIndicator value) {
        this.refusedFlag = value;
    }

    /**
     * Gets the value of the instructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the value of the instructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstructions(String value) {
        this.instructions = value;
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
