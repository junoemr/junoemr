
package xml.hrm.v4_1;

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
 *         &lt;element name="MessageUniqueID"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="250"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DeliverToUserID"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{cds_dt}text"&gt;
 *               &lt;maxLength value="9"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Provider" type="{cds_dt}personNameSimple"/&gt;
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
    "messageUniqueID",
    "deliverToUserID",
    "provider"
})
@XmlRootElement(name = "TransactionInformation", namespace = "cds")
public class TransactionInformation {

    @XmlElement(name = "MessageUniqueID", namespace = "cds", required = true)
    protected String messageUniqueID;
    @XmlElement(name = "DeliverToUserID", namespace = "cds", required = true)
    protected String deliverToUserID;
    @XmlElement(name = "Provider", namespace = "cds", required = true)
    protected PersonNameSimple provider;

    /**
     * Gets the value of the messageUniqueID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageUniqueID() {
        return messageUniqueID;
    }

    /**
     * Sets the value of the messageUniqueID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageUniqueID(String value) {
        this.messageUniqueID = value;
    }

    /**
     * Gets the value of the deliverToUserID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliverToUserID() {
        return deliverToUserID;
    }

    /**
     * Sets the value of the deliverToUserID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliverToUserID(String value) {
        this.deliverToUserID = value;
    }

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameSimple }
     *     
     */
    public PersonNameSimple getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameSimple }
     *     
     */
    public void setProvider(PersonNameSimple value) {
        this.provider = value;
    }

}
