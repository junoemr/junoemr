
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personNamePurposeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personNamePurposeCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="HC"/&gt;
 *     &lt;enumeration value="L"/&gt;
 *     &lt;enumeration value="AL"/&gt;
 *     &lt;enumeration value="C"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personNamePurposeCode")
@XmlEnum
public enum PersonNamePurposeCode {

    HC,
    L,
    AL,
    C;

    public String value() {
        return name();
    }

    public static PersonNamePurposeCode fromValue(String v) {
        return valueOf(v);
    }

}
