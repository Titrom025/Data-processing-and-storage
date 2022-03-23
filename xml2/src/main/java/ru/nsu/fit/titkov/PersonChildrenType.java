
package ru.nsu.fit.titkov;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for person-children-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person-children-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="daughter-ref" type="{ru.nsu.fit.titkov}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="son-ref" type="{ru.nsu.fit.titkov}person-reference-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person-children-type", namespace = "ru.nsu.fit.titkov", propOrder = {
    "daughterRef",
    "sonRef"
})
public class PersonChildrenType {

    @XmlElement(name = "daughter-ref")
    protected List<PersonReferenceType> daughterRef;
    @XmlElement(name = "son-ref")
    protected List<PersonReferenceType> sonRef;
    @XmlAttribute(name = "count", required = true)
    protected int count;

    /**
     * Gets the value of the daughterRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the javax XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the daughterRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDaughterRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getDaughterRef() {
        if (daughterRef == null) {
            daughterRef = new ArrayList<PersonReferenceType>();
        }
        return this.daughterRef;
    }

    /**
     * Gets the value of the sonRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the javax XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the sonRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSonRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonReferenceType }
     * 
     * 
     */
    public List<PersonReferenceType> getSonRef() {
        if (sonRef == null) {
            sonRef = new ArrayList<PersonReferenceType>();
        }
        return this.sonRef;
    }

    /**
     * Gets the value of the count property.
     * 
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(int value) {
        this.count = value;
    }

}
