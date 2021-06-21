
package xml.cds.v5_0;

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
 *         &lt;element name="ResidualInfo" type="{cds_dt}residualInformation" minOccurs="0"/&gt;
 *         &lt;element name="AlertDescription" type="{cds_dt}text1K" minOccurs="0"/&gt;
 *         &lt;element name="Notes" type="{cds_dt}text1K" minOccurs="0"/&gt;
 *         &lt;element name="DateActive" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{cds_dt}dateFullOrPartial" minOccurs="0"/&gt;
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
    "alertDescription",
    "notes",
    "dateActive",
    "endDate"
})
@XmlRootElement(name = "AlertsAndSpecialNeeds", namespace = "cds")
public class AlertsAndSpecialNeeds {

    @XmlElement(name = "ResidualInfo", namespace = "cds")
    protected ResidualInformation residualInfo;
    @XmlElement(name = "AlertDescription", namespace = "cds")
    protected String alertDescription;
    @XmlElement(name = "Notes", namespace = "cds")
    protected String notes;
    @XmlElement(name = "DateActive", namespace = "cds")
    protected DateFullOrPartial dateActive;
    @XmlElement(name = "EndDate", namespace = "cds")
    protected DateFullOrPartial endDate;

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
     * Gets the value of the alertDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertDescription() {
        return alertDescription;
    }

    /**
     * Sets the value of the alertDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertDescription(String value) {
        this.alertDescription = value;
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
     * Gets the value of the dateActive property.
     * 
     * @return
     *     possible object is
     *     {@link DateFullOrPartial }
     *     
     */
    public DateFullOrPartial getDateActive() {
        return dateActive;
    }

    /**
     * Sets the value of the dateActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFullOrPartial }
     *     
     */
    public void setDateActive(DateFullOrPartial value) {
        this.dateActive = value;
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

}
