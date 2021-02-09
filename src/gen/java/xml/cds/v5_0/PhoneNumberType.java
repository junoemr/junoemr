
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for phoneNumberType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="phoneNumberType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="1"/&gt;
 *     &lt;enumeration value="R"/&gt;
 *     &lt;enumeration value="C"/&gt;
 *     &lt;enumeration value="W"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "phoneNumberType")
@XmlEnum
public enum PhoneNumberType {

    R,
    C,
    W;

    public String value() {
        return name();
    }

    public static PhoneNumberType fromValue(String v) {
        return valueOf(v);
    }

}
