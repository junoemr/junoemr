
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contactPersonPurpose.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="contactPersonPurpose"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="EC"/&gt;
 *     &lt;enumeration value="NK"/&gt;
 *     &lt;enumeration value="AS"/&gt;
 *     &lt;enumeration value="CG"/&gt;
 *     &lt;enumeration value="PA"/&gt;
 *     &lt;enumeration value="IN"/&gt;
 *     &lt;enumeration value="GT"/&gt;
 *     &lt;enumeration value="O"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "contactPersonPurpose")
@XmlEnum
public enum ContactPersonPurpose {

    EC,
    NK,
    AS,
    CG,
    PA,
    IN,
    GT,
    O;

    public String value() {
        return name();
    }

    public static ContactPersonPurpose fromValue(String v) {
        return valueOf(v);
    }

}
