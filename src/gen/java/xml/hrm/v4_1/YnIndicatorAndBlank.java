
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ynIndicatorAndBlank complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ynIndicatorAndBlank"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="ynIndicatorsimple" type="{cds_dt}ynIndicatorsimple"/&gt;
 *         &lt;element name="boolean" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="blank" type="{cds_dt}blank"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ynIndicatorAndBlank", propOrder = {
    "ynIndicatorsimple",
    "_boolean",
    "blank"
})
public class YnIndicatorAndBlank {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String ynIndicatorsimple;
    @XmlElement(name = "boolean")
    protected Boolean _boolean;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String blank;

    /**
     * Gets the value of the ynIndicatorsimple property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYnIndicatorsimple() {
        return ynIndicatorsimple;
    }

    /**
     * Sets the value of the ynIndicatorsimple property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYnIndicatorsimple(String value) {
        this.ynIndicatorsimple = value;
    }

    /**
     * Gets the value of the boolean property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBoolean() {
        return _boolean;
    }

    /**
     * Sets the value of the boolean property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBoolean(Boolean value) {
        this._boolean = value;
    }

    /**
     * Gets the value of the blank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlank() {
        return blank;
    }

    /**
     * Sets the value of the blank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlank(String value) {
        this.blank = value;
    }

}
