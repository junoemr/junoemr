
package xml.hrm.v4_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for bloodPressure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bloodPressure"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SystolicBP"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="1024"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DiastolicBP"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="1024"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="BPUnit"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="10"/&gt;
 *               &lt;enumeration value="mmHg"/&gt;
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
@XmlType(name = "bloodPressure", propOrder = {
    "systolicBP",
    "diastolicBP",
    "bpUnit",
    "date"
})
public class BloodPressure {

    @XmlElement(name = "SystolicBP", required = true)
    protected String systolicBP;
    @XmlElement(name = "DiastolicBP", required = true)
    protected String diastolicBP;
    @XmlElement(name = "BPUnit", required = true)
    protected String bpUnit;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;

    /**
     * Gets the value of the systolicBP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystolicBP() {
        return systolicBP;
    }

    /**
     * Sets the value of the systolicBP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystolicBP(String value) {
        this.systolicBP = value;
    }

    /**
     * Gets the value of the diastolicBP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiastolicBP() {
        return diastolicBP;
    }

    /**
     * Sets the value of the diastolicBP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiastolicBP(String value) {
        this.diastolicBP = value;
    }

    /**
     * Gets the value of the bpUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBPUnit() {
        return bpUnit;
    }

    /**
     * Sets the value of the bpUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBPUnit(String value) {
        this.bpUnit = value;
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
