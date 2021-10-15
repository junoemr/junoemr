
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for gender.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="gender"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;minLength value="1"/&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;whiteSpace value="collapse"/&gt;
 *     &lt;enumeration value="M"/&gt;
 *     &lt;enumeration value="F"/&gt;
 *     &lt;enumeration value="O"/&gt;
 *     &lt;enumeration value="U"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "gender")
@XmlEnum
public enum Gender {

    M,
    F,
    O,
    U;

    public String value() {
        return name();
    }

    public static Gender fromValue(String v) {
        return valueOf(v);
    }

}
