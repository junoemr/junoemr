
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for waistCircumference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waistCircumference"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="WaistCircumference"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="1024"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="WaistCircumferenceUnit"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="10"/&gt;
 *               &lt;enumeration value="cm"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Date" type="{cds_dt}dateYYYYMMDD"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waistCircumference", propOrder = {
    "waistCircumference",
    "waistCircumferenceUnit",
    "date"
})
public class WaistCircumference {

    @XmlElement(name = "WaistCircumference", required = true)
    protected String waistCircumference;
    @XmlElement(name = "WaistCircumferenceUnit", required = true)
    protected String waistCircumferenceUnit;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;

    /**
     * Gets the value of the waistCircumference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaistCircumference() {
        return waistCircumference;
    }

    /**
     * Sets the value of the waistCircumference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaistCircumference(String value) {
        this.waistCircumference = value;
    }

    /**
     * Gets the value of the waistCircumferenceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaistCircumferenceUnit() {
        return waistCircumferenceUnit;
    }

    /**
     * Sets the value of the waistCircumferenceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaistCircumferenceUnit(String value) {
        this.waistCircumferenceUnit = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

}
