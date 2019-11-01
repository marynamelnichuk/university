
import dao.DepartmentDao;
import dao.LectorDao;
import dao.impl.DepartmentDaoImpl;
import dao.impl.LectorDaoImpl;
import model.Lector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


public class Application {


    private static final LectorDao lectorDao = new LectorDaoImpl();
    private static final DepartmentDao departmentDao = new DepartmentDaoImpl();

    private static void showMenu() {
        System.out.println("Select command");
        System.out.println("Press escape to exit");
        System.out.println("0.Exit");
        System.out.println("1.Who is head of department by department name.");
        System.out.println("2.Show statistic by department name.");
        System.out.println("3.Show the average salary for department.");
        System.out.println("4.Show count of employee for department");
        System.out.println("5.Global search by template");
    }


    public static void main(String[] args) throws IOException {
        showMenu();
        boolean isNotTheEnd = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String departmentName = "";
        while (isNotTheEnd) {
            int choice = Integer.parseInt(reader.readLine());
            switch (choice) {
                case 0 :
                    isNotTheEnd = false;
                    break;
                case 1 :
                    System.out.print("Enter name of department: ");
                    departmentName = reader.readLine();
                    Lector lector = lectorDao.findHeadOfDepartmentByName(departmentName);
                    System.out.println("Head of department: " + lector);
                    break;
                case 2:
                    System.out.print("Enter name of department: ");
                    departmentName = reader.readLine();
                    Map<String, Integer> statistics = departmentDao.getStatisticForDepartment(departmentName);
                    System.out.println("Department with name " + departmentName + " have such statistics");
                    System.out.println(statistics);
                    break;
                case 3:
                    System.out.print("Enter name of department: ");
                    departmentName = reader.readLine();
                    System.out.println("Average salary for department with name " + departmentName + "is : "
                            + departmentDao.getAverageSalaryForDepartment(departmentName));
                    break;
                case 4:
                    System.out.print("Enter name of department: ");
                    departmentName = reader.readLine();
                    System.out.println("Count of employee for department with name " + departmentName + "is : "
                            + departmentDao.getCountOfEmployeeForDepartment(departmentName));
                    break;
                case 5:
                    System.out.println("Enter template to search : ");
                    String template = reader.readLine();
                    System.out.println("On your request: ");
                    System.out.println(lectorDao.findByInfo(template));
            }

        }



    }

}
