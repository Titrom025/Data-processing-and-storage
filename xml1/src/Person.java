import java.util.*;
import java.util.stream.Stream;

public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private Person spouse;

    private Integer childrenNumber;
    private Integer siblingsNumber;

    private final Set<Person> parents = new HashSet<>();
    private final Set<Person> siblings = new HashSet<>();
    private final Set<Person> children = new HashSet<>();

    public Person() {};

    public Person(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String parseInt) {
        this.id = parseInt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (this.firstName != null && !this.firstName.equals(firstName)) {
            throw new Error("FirstName already set");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (this.lastName != null && !this.lastName.equals(lastName)) {
            throw new Error("Lastname already set");
        }
        this.lastName = lastName;
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }

    public void setFullName(String fullName) {
        String[] words = fullName.split(" +");
        setFirstName(words[0]);
        setLastName(words[1]);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (this.gender != null  && !this.gender.equals(gender)) {
            throw new Error("Gender already set");
        }
        this.gender = gender;
    }

    public Integer getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(int childrenNumber) {
        if (this.childrenNumber != null && this.childrenNumber != childrenNumber) {
            throw new Error("Children number already set");
        }
        this.childrenNumber = childrenNumber;
    }

    public void setSiblingsNumber(int siblingsNumber) {
        if (this.siblingsNumber != null && this.siblingsNumber != siblingsNumber) {
            throw new Error("Siblings number already set");
        }
        this.siblingsNumber = siblingsNumber;
    }

    public Set<Person> getSiblings() {
        return siblings;
    }

    public void addSibling(Person sibling) {
        if (sibling.siblingsNumber == null) { sibling.siblingsNumber = siblingsNumber; }
        if (siblingsNumber == null) { siblingsNumber = sibling.siblingsNumber; }

        siblings.add(sibling);
        sibling.siblings.add(this);
    }
    public void addSibling(String id) {
        addSibling(new Person(id));
    }

    public void addSibling(String name, String gender) {
        Person sibling = new Person();
        sibling.setFullName(name);
        sibling.setGender(gender);
        addSibling(sibling);
    }

    public Set<Person> getChildren() {
        return children;
    }

    public void addChild(Person child) {
        children.add(child);
        child.parents.add(this);
    }

    public void addChild(String name) {
        Person child = new Person();
        child.setFullName(name);
        addChild(child);
    }

    public void addChild(String id, String gender) {
        Person child = new Person(id);
        child.setGender(gender);
        addChild(child);
    }

    public Set<Person> getParents() {
        return parents;
    }

    public void addParent(Person parent) {
        parent.children.add(this);
        parents.add(parent);
    }

    public void addParent(String id) {
        Person parent = new Person(id);
        addParent(parent);
    }

    public void addParent(String fullname, String gender) {
        Person parent = new Person();
        parent.setFullName(fullname);
        parent.setGender(gender);
        addParent(parent);
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouce) {
        if (this.spouse != null && this.spouse.getId() != null && !spouce.equals(this.spouse)) {
            throw new Error("Spouce already set");
        }

        this.spouse = spouce;
        spouce.spouse = this;

        if (spouse.childrenNumber != null) {
            this.setChildrenNumber(spouse.childrenNumber);
        } else if (this.childrenNumber != null) {
            spouce.setChildrenNumber(this.childrenNumber);
        }
    }

    public void resetSpouce() {
        this.spouse = null;
    }

    public void setSpouse(String name) {
        if (this.spouse != null && !Objects.equals(name, spouse.getFirstName())) {
            throw new Error("Spouce already set");
        }
        Person spouce = new Person();
        spouce.setFullName(name);
        setSpouse(spouce);
    }

    public void setWife(String id) {
        if (this.spouse != null && !Objects.equals(id, this.spouse.getId())) {
            throw new Error("Wife already set");
        }
        Person wife = new Person(id);
        wife.setGender("female");
        setSpouse(wife);
        setGender("male");
    }

    public void setHusband(String id) {
        if (this.spouse != null && !Objects.equals(id, this.spouse.getId())) {
            throw new Error("Husband already set");
        }
        Person husband = new Person(id);
        husband.setGender("male");
        setSpouse(husband);
        setGender("female");
    }

    public boolean checkConsistency(final Map<String, Person> personById) {
        return id != null && firstName != null && lastName != null && gender != null &&
                parents.size() <= 2 && childrenNumber != null && siblingsNumber != null &&
                children.size() == childrenNumber && siblings.size() == siblingsNumber &&

                (spouse != null || childrenNumber == 0) &&

                children.stream()
                        .map(Person::getParents)
                        .allMatch(s -> Arrays.asList(s.toArray()).contains(this)) &&

                parents.stream()
                        .map(Person::getChildren)
                        .allMatch(s -> Arrays.asList(s.toArray()).contains(this)) &&

                siblings.stream()
                        .map(Person::getSiblings)
                        .allMatch(s -> Arrays.asList(s.toArray()).contains(this)) &&

                Stream.concat(
                        Stream.of(children, siblings, parents)
                                .flatMap(Collection::stream),
                        Stream.ofNullable(spouse)
                ).allMatch(p -> p.id != null && personById.get(p.id) == p);
    }

    public void lightMerge(Person person) {
        if (person == this) return;

        if (this.id == null || person.id == null || !this.id.equals(person.id)) {
            System.out.println(this);
            System.out.println(person);
            throw new Error("Incorrect id");
        }

        if (person.firstName != null) {
            this.setFirstName(person.firstName);
        }

        if (person.lastName != null) {
            this.setLastName(person.lastName);
        }

        if (person.gender != null) {
            this.setGender(person.gender);
        }

        if (person.childrenNumber != null) {
            this.setChildrenNumber(person.childrenNumber);
        }

        if (person.siblingsNumber != null) {
            this.setSiblingsNumber(person.siblingsNumber);
        }
    }

    public void mergePerson(Person person) {
        lightMerge(person);

        if (person == this) return;

        if (person.spouse != null) {
            this.spouse = person.spouse;
            this.spouse.spouse = this;
        }

        for (Person parent : person.parents) {
            parent.children.remove(person);
            parent.children.add(this);
        }

        for (Person child : person.children) {
            child.parents.remove(person);
            child.parents.add(this);
            this.children.add(child);
        }

        for (Person sibling : person.siblings) {
            sibling.siblings.remove(person);
            sibling.siblings.add(this);
            this.siblings.add(sibling);
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", childrenNumber=" + childrenNumber +
                ", siblingsNumber=" + siblingsNumber +
                '}';
    }

    public String toStringMain() {
        return "Person{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", childrenNumber=" + childrenNumber +
                ", siblingsNumber=" + siblingsNumber +
                ", spouce=" + spouse +
                ", parents=" + parents.stream().toList() +
                ", siblings=" + siblings.stream().toList() +
                ", children=" + children.stream().toList() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (this.id != null && person.id != null && this.id.equals(person.id))
            return true;

        return this.getFullName() != null && person.getFullName() != null && this.getFullName().equals(person.getFullName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, gender, childrenNumber, siblingsNumber);
    }
    
    public void debugTest(final Map<String, Person> personById) {
        boolean first = !(id != null && firstName != null && lastName != null && gender != null &&
                parents.size() <= 2 && childrenNumber != null && siblingsNumber != null &&
                children.size() == childrenNumber && siblings.size() == siblingsNumber);
        if (first) {
            System.out.println("Basic problem");
        }

        if (!(spouse != null || childrenNumber == 0)) {
            System.out.println("Spouse problem");
        }

        if (!(children.stream()
                .map(Person::getParents)
                .allMatch(s -> s.contains(this)))) {
            System.out.println("children problem");
        }

        if (!(parents.stream()
                        .map(Person::getChildren)
                        .allMatch(s -> Arrays.asList(s.toArray()).contains(this)))) {
            System.out.println("Parents problem");
            parents.stream()
                    .map(Person::getChildren).forEach(s -> System.out.println((Arrays.asList(s.toArray()).contains(this))));
        }

        if (!(Stream.concat(
                Stream.of(children, siblings, parents)
                        .flatMap(Collection::stream),
                Stream.ofNullable(spouse)
        ).allMatch(p -> p.id != null && personById.get(p.id) == p))) {
            System.out.println("children, siblings, parents problem");
        }

        if (!(siblings.stream()
                .map(Person::getSiblings)
                .allMatch(s -> s.contains(this)))) {
            System.out.println("siblings problem");
        };
    }
}
