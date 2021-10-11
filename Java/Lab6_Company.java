import java.util.ArrayList;
import java.util.List;
public final class Lab6_Company {
    private final List<Lab6_Department> departments;
    public Lab6_Company(final int departmentsCount) {
        this.departments = new ArrayList<>(departmentsCount);
        for (int i = 0; i < departmentsCount; i++) {
            departments.add(i, new Lab6_Department(i));
        }
    }
    /**
     * Вывод результата по всем отделам.
     * P.S. Актуально после того, как все отделы выполнят свою работу. */
    public void showCollaborativeResult() {
        System.out.println("All departments have completed their work.");
        final int result = departments.stream()
                .map(Lab6_Department::getCalculationResult)
                .reduce(Integer::sum)
                .orElse(-1);
        System.out.println("The sum of all calculations is: " + result); }
    /**
     * @return Количество доступных отделов для симуляции выполнения
    работы. */
    public int getDepartmentsCount() {
        return departments.size();
    }
    /**
     * @param index Индекс для текущего свободного отдела.
     * @return Свободный отдел для симуляции выполнения работы. */
    public Lab6_Department getFreeDepartment(final int index) {
        return departments.get(index);
    } }