
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for diabetesEducationalSelfManagement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="diabetesEducationalSelfManagement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EducationalTrainingPerformed" type="{cds_dt}ynIndicatorsimple"/&gt;
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
@XmlType(name = "diabetesEducationalSelfManagement", propOrder = {
    "educationalTrainingPerformed",
    "date"
})
public class DiabetesEducationalSelfManagement {

    @XmlElement(name = "EducationalTrainingPerformed", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String educationalTrainingPerformed;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;

    /**
     * Gets the value of the educationalTrainingPerformed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationalTrainingPerformed() {
        return educationalTrainingPerformed;
    }

    /**
     * Sets the value of the educationalTrainingPerformed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationalTrainingPerformed(String value) {
        this.educationalTrainingPerformed = value;
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