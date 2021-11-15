
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNameSuffixCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personNameSuffixCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;maxLength value="3"/&gt;
 *     &lt;enumeration value="Jr"/&gt;
 *     &lt;enumeration value="Sr"/&gt;
 *     &lt;enumeration value="II"/&gt;
 *     &lt;enumeration value="III"/&gt;
 *     &lt;enumeration value="IV"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personNameSuffixCode")
@XmlEnum
public enum PersonNameSuffixCode {

    @XmlEnumValue("Jr")
    JR("Jr"),
    @XmlEnumValue("Sr")
    SR("Sr"),
    II("II"),
    III("III"),
    IV("IV");
    private final String value;

    PersonNameSuffixCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersonNameSuffixCode fromValue(String v) {
        for (PersonNameSuffixCode c: PersonNameSuffixCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
