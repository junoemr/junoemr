
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for standardCoding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="standardCoding"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StandardCodingSystem"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="250"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="StandardCode"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="StandardCodeDescription"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="250"/&gt;
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
@XmlType(name = "standardCoding", propOrder = {
    "standardCodingSystem",
    "standardCode",
    "standardCodeDescription"
})
public class StandardCoding {

    @XmlElement(name = "StandardCodingSystem", required = true)
    protected String standardCodingSystem;
    @XmlElement(name = "StandardCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String standardCode;
    @XmlElement(name = "StandardCodeDescription", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String standardCodeDescription;

    /**
     * Gets the value of the standardCodingSystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandardCodingSystem() {
        return standardCodingSystem;
    }

    /**
     * Sets the value of the standardCodingSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandardCodingSystem(String value) {
        this.standardCodingSystem = value;
    }

    /**
     * Gets the value of the standardCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandardCode() {
        return standardCode;
    }

    /**
     * Sets the value of the standardCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandardCode(String value) {
        this.standardCode = value;
    }

    /**
     * Gets the value of the standardCodeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandardCodeDescription() {
        return standardCodeDescription;
    }

    /**
     * Sets the value of the standardCodeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandardCodeDescription(String value) {
        this.standardCodeDescription = value;
    }

}
