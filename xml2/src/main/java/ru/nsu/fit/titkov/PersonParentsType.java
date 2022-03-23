
package ru.nsu.fit.titkov;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for person-parents-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person-parents-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mother-ref" type="{ru.nsu.fit.titkov}person-reference-type" minOccurs="0"/&gt;
 *         &lt;element name="father-ref" type="{ru.nsu.fit.titkov}person-reference-type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person-parents-type", namespace = "ru.nsu.fit.titkov", propOrder = {
    "motherRef",
    "fatherRef"
})
public class PersonParentsType {

    @XmlElement(name = "mother-ref")
    protected PersonReferenceType motherRef;
    @XmlElement(name = "father-ref")
    protected PersonReferenceType fatherRef;

    /**
     * Gets the value of the motherRef property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getMotherRef() {
        return motherRef;
    }

    /**
     * Sets the value of the motherRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setMotherRef(PersonReferenceType value) {
        this.motherRef = value;
    }

    /**
     * Gets the value of the fatherRef property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getFatherRef() {
        return fatherRef;
    }

    /**
     * Sets the value of the fatherRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setFatherRef(PersonReferenceType value) {
        this.fatherRef = value;
    }

}
