
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for dateFullOrPartial complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dateFullOrPartial"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="YearOnly" type="{cds_dt}dateYYYY"/&gt;
 *         &lt;element name="YearMonth" type="{cds_dt}dateYYYYMM"/&gt;
 *         &lt;element name="FullDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateFullOrPartial", propOrder = {
    "yearOnly",
    "yearMonth",
    "fullDate"
})
public class DateFullOrPartial {

    @XmlElement(name = "YearOnly")
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar yearOnly;
    @XmlElement(name = "YearMonth")
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar yearMonth;
    @XmlElement(name = "FullDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fullDate;

    /**
     * Gets the value of the yearOnly property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getYearOnly() {
        return yearOnly;
    }

    /**
     * Sets the value of the yearOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setYearOnly(XMLGregorianCalendar value) {
        this.yearOnly = value;
    }

    /**
     * Gets the value of the yearMonth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getYearMonth() {
        return yearMonth;
    }

    /**
     * Sets the value of the yearMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setYearMonth(XMLGregorianCalendar value) {
        this.yearMonth = value;
    }

    /**
     * Gets the value of the fullDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFullDate() {
        return fullDate;
    }

    /**
     * Sets the value of the fullDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFullDate(XMLGregorianCalendar value) {
        this.fullDate = value;
    }

}
