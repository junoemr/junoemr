
package xml.cds.v5_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for phoneNumber complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneNumber"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="phoneNumber"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                 &lt;maxLength value="25"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="extension" type="{cds_dt}phoneExtension" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="areaCode" type="{http://www.w3.org/2001/XMLSchema}token"/&gt;
 *           &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}token"/&gt;
 *           &lt;element name="extension" type="{cds_dt}phoneExtension" minOccurs="0"/&gt;
 *           &lt;element name="exchange" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="phoneNumberType" use="required" type="{cds_dt}phoneNumberType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneNumber", propOrder = {
    "content"
})
public class PhoneNumber {

    @XmlElementRefs({
        @XmlElementRef(name = "phoneNumber", namespace = "cds_dt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "extension", namespace = "cds_dt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "areaCode", namespace = "cds_dt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "number", namespace = "cds_dt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "exchange", namespace = "cds_dt", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<String>> content;
    @XmlAttribute(name = "phoneNumberType", required = true)
    protected PhoneNumberType phoneNumberType;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "Extension" is used by two different parts of a schema. See: 
     * line 604 of file:/home/chris.semiao/source/juno_spring_boot/src/main/resources/org/oscarehr/common/xml/cds/v5_0/cdsd-schema.xsd
     * line 599 of file:/home/chris.semiao/source/juno_spring_boot/src/main/resources/org/oscarehr/common/xml/cds/v5_0/cdsd-schema.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<String>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<String>>();
        }
        return this.content;
    }

    /**
     * Gets the value of the phoneNumberType property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberType }
     *     
     */
    public PhoneNumberType getPhoneNumberType() {
        return phoneNumberType;
    }

    /**
     * Sets the value of the phoneNumberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneNumberType }
     *     
     */
    public void setPhoneNumberType(PhoneNumberType value) {
        this.phoneNumberType = value;
    }

}
