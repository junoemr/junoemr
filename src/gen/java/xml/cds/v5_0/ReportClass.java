
package xml.cds.v5_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="reportClass"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;maxLength value="50"/&gt;
 *     &lt;enumeration value="Diagnostic Imaging Report"/&gt;
 *     &lt;enumeration value="Diagnostic Test Report"/&gt;
 *     &lt;enumeration value="Cardio Respiratory Report"/&gt;
 *     &lt;enumeration value="Medical Records Report"/&gt;
 *     &lt;enumeration value="Consultant Report"/&gt;
 *     &lt;enumeration value="Lab Report"/&gt;
 *     &lt;enumeration value="Other Letter"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "reportClass")
@XmlEnum
public enum ReportClass {

    @XmlEnumValue("Diagnostic Imaging Report")
    DIAGNOSTIC_IMAGING_REPORT("Diagnostic Imaging Report"),
    @XmlEnumValue("Diagnostic Test Report")
    DIAGNOSTIC_TEST_REPORT("Diagnostic Test Report"),
    @XmlEnumValue("Cardio Respiratory Report")
    CARDIO_RESPIRATORY_REPORT("Cardio Respiratory Report"),
    @XmlEnumValue("Medical Records Report")
    MEDICAL_RECORDS_REPORT("Medical Records Report"),
    @XmlEnumValue("Consultant Report")
    CONSULTANT_REPORT("Consultant Report"),
    @XmlEnumValue("Lab Report")
    LAB_REPORT("Lab Report"),
    @XmlEnumValue("Other Letter")
    OTHER_LETTER("Other Letter");
    private final String value;

    ReportClass(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportClass fromValue(String v) {
        for (ReportClass c: ReportClass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
