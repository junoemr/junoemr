
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;maxLength value="1"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *     &lt;enumeration value="I"/&gt;
 *     &lt;enumeration value="D"/&gt;
 *     &lt;enumeration value="O"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "personStatus")
@XmlEnum
public enum PersonStatus {

    A,
    I,
    D,
    O;

    public String value() {
        return name();
    }

    public static PersonStatus fromValue(String v) {
        return valueOf(v);
    }

}
