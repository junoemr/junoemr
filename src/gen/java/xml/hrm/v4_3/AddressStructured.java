
package xml.hrm.v4_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for address.structured complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="address.structured"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Line1" type="{cds_dt}address.structured.line"/&gt;
 *         &lt;element name="Line2" type="{cds_dt}address.structured.line" minOccurs="0"/&gt;
 *         &lt;element name="Line3" type="{cds_dt}address.structured.line" minOccurs="0"/&gt;
 *         &lt;sequence minOccurs="0"&gt;
 *           &lt;element name="City"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                 &lt;maxLength value="80"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="CountrySubdivisionCode" type="{cds_dt}countrySubDivisionCode"/&gt;
 *           &lt;element name="PostalZipCode" type="{cds_dt}postalZipCode"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address.structured", propOrder = {
    "line1",
    "line2",
    "line3",
    "city",
    "countrySubdivisionCode",
    "postalZipCode"
})
public class AddressStructured {

    @XmlElement(name = "Line1", required = true)
    protected String line1;
    @XmlElement(name = "Line2")
    protected String line2;
    @XmlElement(name = "Line3")
    protected String line3;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "CountrySubdivisionCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String countrySubdivisionCode;
    @XmlElement(name = "PostalZipCode")
    protected PostalZipCode postalZipCode;

    /**
     * Gets the value of the line1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine1() {
        return line1;
    }

    /**
     * Sets the value of the line1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine1(String value) {
        this.line1 = value;
    }

    /**
     * Gets the value of the line2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine2() {
        return line2;
    }

    /**
     * Sets the value of the line2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine2(String value) {
        this.line2 = value;
    }

    /**
     * Gets the value of the line3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine3() {
        return line3;
    }

    /**
     * Sets the value of the line3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine3(String value) {
        this.line3 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the countrySubdivisionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountrySubdivisionCode() {
        return countrySubdivisionCode;
    }

    /**
     * Sets the value of the countrySubdivisionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountrySubdivisionCode(String value) {
        this.countrySubdivisionCode = value;
    }

    /**
     * Gets the value of the postalZipCode property.
     * 
     * @return
     *     possible object is
     *     {@link PostalZipCode }
     *     
     */
    public PostalZipCode getPostalZipCode() {
        return postalZipCode;
    }

    /**
     * Sets the value of the postalZipCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalZipCode }
     *     
     */
    public void setPostalZipCode(PostalZipCode value) {
        this.postalZipCode = value;
    }

}
