
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reportContent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="TextContent"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Media" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reportContent", propOrder = {
    "textContent",
    "media"
})
public class ReportContent {

    @XmlElement(name = "TextContent")
    protected String textContent;
    @XmlElement(name = "Media")
    protected byte[] media;

    /**
     * Gets the value of the textContent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets the value of the textContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextContent(String value) {
        this.textContent = value;
    }

    /**
     * Gets the value of the media property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getMedia() {
        return media;
    }

    /**
     * Sets the value of the media property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setMedia(byte[] value) {
        this.media = value;
    }

}
