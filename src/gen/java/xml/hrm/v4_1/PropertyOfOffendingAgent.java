
package xml.hrm.v4_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for propertyOfOffendingAgent.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="propertyOfOffendingAgent"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="DR"/&gt;
 *     &lt;enumeration value="ND"/&gt;
 *     &lt;enumeration value="UK"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "propertyOfOffendingAgent")
@XmlEnum
public enum PropertyOfOffendingAgent {

    DR,
    ND,
    UK;

    public String value() {
        return name();
    }

    public static PropertyOfOffendingAgent fromValue(String v) {
        return valueOf(v);
    }

}
