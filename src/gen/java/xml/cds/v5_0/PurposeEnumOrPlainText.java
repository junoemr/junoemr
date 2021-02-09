
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for purposeEnumOrPlainText complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purposeEnumOrPlainText"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="PurposeAsEnum"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *               &lt;enumeration value="EC"/&gt;
 *               &lt;enumeration value="NK"/&gt;
 *               &lt;enumeration value="AS"/&gt;
 *               &lt;enumeration value="CG"/&gt;
 *               &lt;enumeration value="PA"/&gt;
 *               &lt;enumeration value="IN"/&gt;
 *               &lt;enumeration value="GT"/&gt;
 *               &lt;enumeration value="SDM"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PurposeAsPlainText"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purposeEnumOrPlainText", propOrder = {
    "purposeAsEnum",
    "purposeAsPlainText"
})
public class PurposeEnumOrPlainText {

    @XmlElement(name = "PurposeAsEnum")
    protected String purposeAsEnum;
    @XmlElement(name = "PurposeAsPlainText")
    protected String purposeAsPlainText;

    /**
     * Gets the value of the purposeAsEnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurposeAsEnum() {
        return purposeAsEnum;
    }

    /**
     * Sets the value of the purposeAsEnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurposeAsEnum(String value) {
        this.purposeAsEnum = value;
    }

    /**
     * Gets the value of the purposeAsPlainText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurposeAsPlainText() {
        return purposeAsPlainText;
    }

    /**
     * Sets the value of the purposeAsPlainText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurposeAsPlainText(String value) {
        this.purposeAsPlainText = value;
    }

}
