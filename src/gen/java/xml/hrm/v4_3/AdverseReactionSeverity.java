
package xml.hrm.v4_3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adverseReactionSeverity.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="adverseReactionSeverity"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;length value="2"/&gt;
 *     &lt;enumeration value="NO"/&gt;
 *     &lt;enumeration value="MI"/&gt;
 *     &lt;enumeration value="MO"/&gt;
 *     &lt;enumeration value="LT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "adverseReactionSeverity")
@XmlEnum
public enum AdverseReactionSeverity {

    NO,
    MI,
    MO,
    LT;

    public String value() {
        return name();
    }

    public static AdverseReactionSeverity fromValue(String v) {
        return valueOf(v);
    }

}
