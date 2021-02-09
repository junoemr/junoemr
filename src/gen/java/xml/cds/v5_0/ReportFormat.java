
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="reportFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="50"/&gt;
 *     &lt;enumeration value="Text"/&gt;
 *     &lt;enumeration value="Binary"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "reportFormat")
@XmlEnum
public enum ReportFormat {

    @XmlEnumValue("Text")
    TEXT("Text"),
    @XmlEnumValue("Binary")
    BINARY("Binary");
    private final String value;

    ReportFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportFormat fromValue(String v) {
        for (ReportFormat c: ReportFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
