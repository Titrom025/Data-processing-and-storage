import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.FileInputStream;
import java.util.*;

public class Main {
    private static final Map<String, Person> personById = new HashMap<>();
    private static final Map<String, Set<String>> idsByName = new HashMap<>();
    private static XMLEventReader reader;

    public static void main(String[] args) {
        String fileName = "people.xml";
        List<Person> persons = parseXMLfile(fileName);
        persons = combinePeople(persons);

        List<Person> errors = persons.stream().filter(p ->
                !p.checkConsistency(personById)).toList();
        for (Person student : errors) {
            System.out.println(student.toStringMain());
        }
        System.out.println(errors.size());
    }

    private static List<Person> combinePeople(List<Person> persons) {
        for (final Person person : persons) {
            final String id = person.getId();
            if (id == null) continue;

            if (personById.containsKey(id)) {
                Person collision = personById.get(id);
                person.mergePerson(collision);
            }
            personById.put(id, person);
        }

        for (final Person person : personById.values()) {
            final String name = person.getFullName();
            if (person.getId() == null || name == null) {
                throw new RuntimeException("Person must have ID and name");
            }

            Set<String> ids = idsByName.getOrDefault(name, new HashSet<>());
            ids.add(person.getId());
            idsByName.put(name, ids);
        }

        List<String> conflicts = new ArrayList<>();

        for (final Person person : persons) {
            final String name = person.getFullName();
            if (name == null) continue;

            final Set<String> ids = idsByName.get(name);
            final String id = ids.size() == 1 ?
                    ids.iterator().next() : null;

            if (ids.size() > 1) {
                conflicts.addAll(ids);
            }

            if (id != null) {
                person.setId(id);
                personById.get(id).mergePerson(person);
            } //else {
//                System.out.println(name);
//            }
        }

//        for (String id: conflicts) {
//            System.out.println(personById.get(id).toStringMain());
//        }

        System.out.println("\n\n");
        for (final Person person : personById.values()) {
            if (person.getSpouse() != null) {
                final Set<Person> spouseToAdd = new HashSet<>();
                connectRelatives(spouseToAdd, person.getSpouse());

                if (!spouseToAdd.isEmpty()) {
                    final Person spouse = spouseToAdd.iterator().next();
                    person.setSpouse(spouse);
                }
            }

            Set<Person> childrenToAdd = new HashSet<>();
            for (final Person child : person.getChildren()) {
                connectRelatives(childrenToAdd, child);
            }
            person.getChildren().clear();
            childrenToAdd.forEach(person::addChild);

            Set<Person> siblingsToAdd = new HashSet<>();
            for (final Person sibling : person.getSiblings()) {
                connectRelatives(siblingsToAdd, sibling);
            }
            person.getSiblings().clear();
            siblingsToAdd.forEach(person::addSibling);

            Set<Person> parentsToAdd = new HashSet<>();
            for (final Person parent : person.getParents()) {
                connectRelatives(parentsToAdd, parent);
            }
            person.getParents().clear();
            parentsToAdd.forEach(person::addParent);
        }

        for (final Person person : personById.values()) {
            if (person.getSpouse() == null || person.getChildrenNumber() == null) {
                person.setChildrenNumber(0);
            }
        }

        return personById.values().stream()
                .sorted(Comparator.comparing(Person::getId)).toList();
    }

    private static List<Person> parseXMLfile(String fileName) {
        List<Person> peopleList = new ArrayList<>();
        Person person = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "person" -> {
                            person = new Person();
                            Attribute idAttr = startElement.getAttributeByName(new QName("id"));
                            if (idAttr != null) {
                                person.setId(idAttr.getValue().trim());
                            }
                            Attribute fullnameAttr = startElement.getAttributeByName(new QName("name"));
                            if (fullnameAttr != null) {
                                person.setFullName(fullnameAttr.getValue().trim());
                            }
                        }
                        case "id" -> person.setId(getValue(startElement));
                        case "first", "firstname" -> {
                            person.setFirstName(getValue(startElement));
                        }
                        case "surname", "family", "family-name" -> {
                            person.setLastName(getValue(startElement));
                        }
                        case "children-number" ->
                            person.setChildrenNumber(Integer.parseInt(getValue(startElement)));
                        case "siblings-number" ->
                            person.setSiblingsNumber(Integer.parseInt(getValue(startElement)));
                        case "gender" ->
                            person.setGender(getGender(startElement));
                        case "siblings" -> {
                            String[] siblingsValue = getSiblingsValue(startElement);
                            if (siblingsValue != null) {
                                for (String id: siblingsValue) {
                                    person.addSibling(id);
                                }
                            }
                        }
                        case "brother" -> {
                            String name = getValue(startElement);
                            person.addSibling(name, "male");
                        }
                        case "sister" -> {
                            String name = getValue(startElement);
                            person.addSibling(name, "female");
                        }
                        case "son" -> {
                            String id = getValue(startElement);
                            person.addChild(id, "male");
                        }
                        case "daughter" -> {
                            String id = getValue(startElement);
                            person.addChild(id, "female");
                        }
                        case "child" -> {
                            String name = getValue(startElement);
                            person.addChild(name);
                        }
                        case "parent" -> {
                            String id = getValue(startElement);
                            if (id != null) {
                                person.addParent(id);
                            }
                        }
                        case "mother" -> {
                            String fullname = getValue(startElement);
                            person.addParent(fullname, "female");
                        }
                        case "father" -> {
                            String fullname = getValue(startElement);
                            person.addParent(fullname, "male");
                        }

                        case "children", "fullname" -> {}

                        case "spouce" -> {
                            String fullname = getValue(startElement);
                            if (fullname != null) {
                                person.setSpouse(fullname);
                            }
                        }

                        case "wife" -> {
                            String id = getValue(startElement);
                            person.setWife(id);
                        }
                        case "husband" -> {
                            String id = getValue(startElement);
                            person.setHusband(id);
                        }
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("person")) {
                        peopleList.add(person);
                    }
                }
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return peopleList;
    }

    private static String[] getSiblingsValue(final StartElement event) {
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            return attribute.getValue().trim().split(" ");
        }
        return null;
    }

    private static String getValue(final StartElement event) throws XMLStreamException {
        String value;
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            value = attribute.getValue().trim();
        } else {
            XMLEvent charsEvent = reader.nextEvent();
            if (charsEvent.isEndElement()) {
                return null;
            }
            reader.nextEvent();
            value = charsEvent.asCharacters().getData().trim();
        }

        if (value.equals("NONE") || value.equals("UNKNOWN")) {
            return null;
        } else {
            return value;
        }
    }

    private static String parseGender(String gender) throws Exception {
        return switch (gender) {
            case "male", "M" ->
                "male";
            case "female", "F" ->
                "female";
            default -> throw new Exception();
        };
    }

    private static String getGender(final StartElement event) throws Exception {
        Iterator<Attribute> iterator = event.getAttributes();
        if (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            return parseGender(attribute.getValue().trim());
        } else {
            XMLEvent charsEvent = reader.nextEvent();
            reader.nextEvent();
            String gender = charsEvent.asCharacters().getData().trim();
            return parseGender(gender);
        }
    }

    private static void connectRelatives(final Set<Person> peopleToAdd, final Person person) {
        if (person.getId() != null) {
            final String id = person.getId();
            peopleToAdd.add(personById.get(id));
        } else if (person.getFullName() != null) {
            final String name = person.getFullName();
            final Set<String> ids = idsByName.get(name);

            if (ids.size() == 1) {
                final String id = ids.iterator().next();
                peopleToAdd.add(personById.get(id));
            }
        }
    }
}
