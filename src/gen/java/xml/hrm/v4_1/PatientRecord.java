
package xml.hrm.v4_1;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{cds}Demographics"/&gt;
 *         &lt;element ref="{cds}ReportsReceived" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{cds}TransactionInformation" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "demographics",
    "reportsReceived",
    "transactionInformation"
})
@XmlRootElement(name = "PatientRecord", namespace = "cds")
public class PatientRecord {

    @XmlElement(name = "Demographics", namespace = "cds", required = true)
    protected Demographics demographics;
    @XmlElement(name = "ReportsReceived", namespace = "cds")
    protected List<ReportsReceived> reportsReceived;
    @XmlElement(name = "TransactionInformation", namespace = "cds")
    protected List<TransactionInformation> transactionInformation;

    /**
     * Gets the value of the demographics property.
     * 
     * @return
     *     possible object is
     *     {@link Demographics }
     *     
     */
    public Demographics getDemographics() {
        return demographics;
    }

    /**
     * Sets the value of the demographics property.
     * 
     * @param value
     *     allowed object is
     *     {@link Demographics }
     *     
     */
    public void setDemographics(Demographics value) {
        this.demographics = value;
    }

    /**
     * Gets the value of the reportsReceived property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reportsReceived property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReportsReceived().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReportsReceived }
     * 
     * 
     */
    public List<ReportsReceived> getReportsReceived() {
        if (reportsReceived == null) {
            reportsReceived = new ArrayList<ReportsReceived>();
        }
        return this.reportsReceived;
    }

    /**
     * Gets the value of the transactionInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionInformation }
     * 
     * 
     */
    public List<TransactionInformation> getTransactionInformation() {
        if (transactionInformation == null) {
            transactionInformation = new ArrayList<TransactionInformation>();
        }
        return this.transactionInformation;
    }

}
