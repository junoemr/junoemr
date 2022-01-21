
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adverseReactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="adverseReactionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="AL"/&gt;
 *     &lt;enumeration value="AR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "adverseReactionType")
@XmlEnum
public enum AdverseReactionType {

    AL,
    AR;

    public String value() {
        return name();
    }

    public static AdverseReactionType fromValue(String v) {
        return valueOf(v);
    }

}
