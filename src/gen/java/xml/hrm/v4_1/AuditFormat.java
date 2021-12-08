
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for auditFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="auditFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="40"/&gt;
 *     &lt;enumeration value="Text"/&gt;
 *     &lt;enumeration value="File"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "auditFormat")
@XmlEnum
public enum AuditFormat {

    @XmlEnumValue("Text")
    TEXT("Text"),
    @XmlEnumValue("File")
    FILE("File");
    private final String value;

    AuditFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AuditFormat fromValue(String v) {
        for (AuditFormat c: AuditFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
