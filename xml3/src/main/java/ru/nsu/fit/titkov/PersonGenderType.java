
package ru.nsu.fit.titkov;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for person-gender-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="person-gender-type"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="male"/&gt;
 *     &lt;enumeration value="female"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "person-gender-type", namespace = "ru.nsu.fit.titkov")
@XmlEnum
public enum PersonGenderType {

    @XmlEnumValue("male")
    MALE("male"),
    @XmlEnumValue("female")
    FEMALE("female");
    private final String value;

    PersonGenderType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersonGenderType fromValue(String v) {
        for (PersonGenderType c: PersonGenderType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
