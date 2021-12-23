
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportMedia.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="reportMedia"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="20"/&gt;
 *     &lt;enumeration value="Email"/&gt;
 *     &lt;enumeration value="Download"/&gt;
 *     &lt;enumeration value="Portable Media"/&gt;
 *     &lt;enumeration value="Hardcopy"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "reportMedia")
@XmlEnum
public enum ReportMedia {

    @XmlEnumValue("Email")
    EMAIL("Email"),
    @XmlEnumValue("Download")
    DOWNLOAD("Download"),
    @XmlEnumValue("Portable Media")
    PORTABLE_MEDIA("Portable Media"),
    @XmlEnumValue("Hardcopy")
    HARDCOPY("Hardcopy");
    private final String value;

    ReportMedia(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportMedia fromValue(String v) {
        for (ReportMedia c: ReportMedia.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
