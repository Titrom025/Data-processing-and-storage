
package ru.nsu.fit.titkov;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for person-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="spouse" type="{ru.nsu.fit.titkov}person-reference-type" minOccurs="0"/&gt;
 *         &lt;element name="parents" type="{ru.nsu.fit.titkov}person-parents-type" minOccurs="0"/&gt;
 *         &lt;element name="siblings" type="{ru.nsu.fit.titkov}person-siblings-type" minOccurs="0"/&gt;
 *         &lt;element name="children" type="{ru.nsu.fit.titkov}person-children-type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="person-name" use="required" type="{ru.nsu.fit.titkov}person-name-type" /&gt;
 *       &lt;attribute name="person-gender" use="required" type="{ru.nsu.fit.titkov}person-gender-type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person-type", namespace = "ru.nsu.fit.titkov", propOrder = {
    "spouse",
    "parents",
    "siblings",
    "children"
})
public class PersonType {

    protected PersonReferenceType spouse;
    protected PersonParentsType parents;
    protected PersonSiblingsType siblings;
    protected PersonChildrenType children;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "person-name", required = true)
    protected String personName;
    @XmlAttribute(name = "person-gender", required = true)
    protected PersonGenderType personGender;

    /**
     * Gets the value of the spouse property.
     * 
     * @return
     *     possible object is
     *     {@link PersonReferenceType }
     *     
     */
    public PersonReferenceType getSpouse() {
        return spouse;
    }

    /**
     * Sets the value of the spouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonReferenceType }
     *     
     */
    public void setSpouse(PersonReferenceType value) {
        this.spouse = value;
    }

    /**
     * Gets the value of the parents property.
     * 
     * @return
     *     possible object is
     *     {@link PersonParentsType }
     *     
     */
    public PersonParentsType getParents() {
        return parents;
    }

    /**
     * Sets the value of the parents property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonParentsType }
     *     
     */
    public void setParents(PersonParentsType value) {
        this.parents = value;
    }

    /**
     * Gets the value of the siblings property.
     * 
     * @return
     *     possible object is
     *     {@link PersonSiblingsType }
     *     
     */
    public PersonSiblingsType getSiblings() {
        return siblings;
    }

    /**
     * Sets the value of the siblings property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonSiblingsType }
     *     
     */
    public void setSiblings(PersonSiblingsType value) {
        this.siblings = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link PersonChildrenType }
     *     
     */
    public PersonChildrenType getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonChildrenType }
     *     
     */
    public void setChildren(PersonChildrenType value) {
        this.children = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the personName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonName(String value) {
        this.personName = value;
    }

    /**
     * Gets the value of the personGender property.
     * 
     * @return
     *     possible object is
     *     {@link PersonGenderType }
     *     
     */
    public PersonGenderType getPersonGender() {
        return personGender;
    }

    /**
     * Sets the value of the personGender property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonGenderType }
     *     
     */
    public void setPersonGender(PersonGenderType value) {
        this.personGender = value;
    }

}
