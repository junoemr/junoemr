
package xml.hrm.v4_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="address"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="Formatted" type="{cds_dt}address.formatted"/&gt;
 *         &lt;element name="Structured" type="{cds_dt}address.structured"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="addressType" use="required" type="{cds_dt}addressType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder = {
    "formatted",
    "structured"
})
public class Address {

    @XmlElement(name = "Formatted")
    protected String formatted;
    @XmlElement(name = "Structured")
    protected AddressStructured structured;
    @XmlAttribute(name = "addressType", required = true)
    protected AddressType addressType;

    /**
     * Gets the value of the formatted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatted() {
        return formatted;
    }

    /**
     * Sets the value of the formatted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatted(String value) {
        this.formatted = value;
    }

    /**
     * Gets the value of the structured property.
     * 
     * @return
     *     possible object is
     *     {@link AddressStructured }
     *     
     */
    public AddressStructured getStructured() {
        return structured;
    }

    /**
     * Sets the value of the structured property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressStructured }
     *     
     */
    public void setStructured(AddressStructured value) {
        this.structured = value;
    }

    /**
     * Gets the value of the addressType property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddressType() {
        return addressType;
    }

    /**
     * Sets the value of the addressType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddressType(AddressType value) {
        this.addressType = value;
    }

}
