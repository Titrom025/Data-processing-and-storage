
package ru.nsu.fit.titkov;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.nsu.fit.titkov package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.nsu.fit.titkov
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link People }
     * 
     */
    public People createPeople() {
        return new People();
    }

    /**
     * Create an instance of {@link PersonType }
     * 
     */
    public PersonType createPersonType() {
        return new PersonType();
    }

    /**
     * Create an instance of {@link PersonReferenceType }
     * 
     */
    public PersonReferenceType createPersonReferenceType() {
        return new PersonReferenceType();
    }

    /**
     * Create an instance of {@link PersonParentsType }
     * 
     */
    public PersonParentsType createPersonParentsType() {
        return new PersonParentsType();
    }

    /**
     * Create an instance of {@link PersonChildrenType }
     * 
     */
    public PersonChildrenType createPersonChildrenType() {
        return new PersonChildrenType();
    }

    /**
     * Create an instance of {@link PersonSiblingsType }
     * 
     */
    public PersonSiblingsType createPersonSiblingsType() {
        return new PersonSiblingsType();
    }

}
