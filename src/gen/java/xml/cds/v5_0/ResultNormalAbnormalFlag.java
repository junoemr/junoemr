
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for resultNormalAbnormalFlag complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultNormalAbnormalFlag"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="resultNormalAbnormalFlagAsEnum"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="2"/&gt;
 *               &lt;enumeration value="L"/&gt;
 *               &lt;enumeration value="LL"/&gt;
 *               &lt;enumeration value="H"/&gt;
 *               &lt;enumeration value="HH"/&gt;
 *               &lt;enumeration value="N"/&gt;
 *               &lt;enumeration value="A"/&gt;
 *               &lt;enumeration value="AA"/&gt;
 *               &lt;enumeration value="S"/&gt;
 *               &lt;enumeration value="R"/&gt;
 *               &lt;enumeration value="I"/&gt;
 *               &lt;enumeration value="MS"/&gt;
 *               &lt;enumeration value="VS"/&gt;
 *               &lt;enumeration value="U"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="resultNormalAbnormalFlagAsPlainText"&gt;
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
@XmlType(name = "resultNormalAbnormalFlag", propOrder = {
    "resultNormalAbnormalFlagAsEnum",
    "resultNormalAbnormalFlagAsPlainText"
})
public class ResultNormalAbnormalFlag {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String resultNormalAbnormalFlagAsEnum;
    protected String resultNormalAbnormalFlagAsPlainText;

    /**
     * Gets the value of the resultNormalAbnormalFlagAsEnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultNormalAbnormalFlagAsEnum() {
        return resultNormalAbnormalFlagAsEnum;
    }

    /**
     * Sets the value of the resultNormalAbnormalFlagAsEnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultNormalAbnormalFlagAsEnum(String value) {
        this.resultNormalAbnormalFlagAsEnum = value;
    }

    /**
     * Gets the value of the resultNormalAbnormalFlagAsPlainText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultNormalAbnormalFlagAsPlainText() {
        return resultNormalAbnormalFlagAsPlainText;
    }

    /**
     * Sets the value of the resultNormalAbnormalFlagAsPlainText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultNormalAbnormalFlagAsPlainText(String value) {
        this.resultNormalAbnormalFlagAsPlainText = value;
    }

}
