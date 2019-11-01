
import dao.DepartmentDao;
import dao.LectorDao;
import dao.impl.DepartmentDaoImpl;
import dao.impl.LectorDaoImpl;
import exception.ApplicationException;
import model.Degree;
import model.Department;
import model.Lector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final LectorDao lectorDao = new LectorDaoImpl();
    private static final DepartmentDao departmentDao = new DepartmentDaoImpl();
    private static boolean isNotTheEnd = true;


    private static void showMenu() {
        System.out.println("0.Exit");
        System.out.println("1.Who is head of department by department name.");
        System.out.println("2.Show statistic by department name.");
        System.out.println("3.Show the average salary for department.");
        System.out.println("4.Show count of employee for department.");
        System.out.println("5.Global search by template.");
        System.out.println("6.Add lector.");
        System.out.println("7.Delete lector");
        System.out.println("8.Update lector");
        System.out.println("9.Show all lectors.");
        System.out.println("10.Add department.");
        System.out.println("11.Delete department");
        System.out.println("12.Update department");
        System.out.println("13.Show all departments.");
        System.out.println("14.Add lector to department");
        System.out.print("Select command : ");
    }

    private static void makeActionToVariant1(BufferedReader reader) throws IOException {
        System.out.print("Enter name of department: ");
        String departmentName = reader.readLine();
        Lector lector = lectorDao.findHeadOfDepartmentByName(departmentName);
        System.out.println("Head of department: " + lector);
    }

    private static void makeActionToVariant2(BufferedReader reader) throws IOException {
        System.out.print("Enter name of department: ");
        String departmentName = reader.readLine();
        Map<String, Integer> statistics = departmentDao.getStatisticForDepartment(departmentName);
        System.out.println("Department with name " + departmentName + " have such statistics");
        System.out.println(statistics);
    }

    private static void makeActionToVariant3(BufferedReader reader) throws IOException {
        System.out.print("Enter name of department: ");
        String departmentName = reader.readLine();
        System.out.println("Average salary for department with name " + departmentName + "is : "
                + departmentDao.getAverageSalaryForDepartment(departmentName));
    }

    private static void makeActionToVariant4(BufferedReader reader) throws IOException {
        System.out.print("Enter name of department: ");
        String departmentName = reader.readLine();
        System.out.println("Count of employee for department with name " + departmentName + "is : "
                + departmentDao.getCountOfEmployeeForDepartment(departmentName));
    }

    private static void makeActionToVariant5(BufferedReader reader) throws IOException {
        System.out.println("Enter template to search : ");
        String template = reader.readLine();
        System.out.println("On your request: ");
        System.out.println(lectorDao.findByInfo(template));
    }


    private static void makeActionToVariant6(BufferedReader reader) throws IOException {
        System.out.println("Enter name: ");
        String name = reader.readLine();
        System.out.println("Enter surname: ");
        String surname = reader.readLine();
        System.out.println("Enter degree: ");
        String degree = reader.readLine();
        System.out.println("Enter salary");
        double salary = Double.parseDouble(reader.readLine());
        Lector lector = new Lector();
        lector.setName(name);
        lector.setSurname(surname);
        lector.setDegree(Degree.valueOf(degree));
        lector.setSalary(salary);
        lectorDao.save(lector);
    }

    private static void makeActionToVariant7(BufferedReader reader) throws IOException {
        System.out.println("Enter id to delete lector: ");
        int id = Integer.parseInt(reader.readLine());
        lectorDao.delete(id);
    }

    private static void makeActionToVariant8(BufferedReader reader) throws IOException {
        System.out.println("Enter id lector to update: ");
        int id = Integer.parseInt(reader.readLine());
        System.out.println("Set new name: ");
        String name = reader.readLine();
        System.out.println("Set new surname: ");
        String surname = reader.readLine();
        System.out.println("Set new degree: ");
        String degree = reader.readLine();
        System.out.println("Set new salary");
        double salary = Double.parseDouble(reader.readLine());
        Lector lector = new Lector();
        lector.setName(name);
        lector.setSurname(surname);
        lector.setDegree(Degree.valueOf(degree));
        lector.setSalary(salary);
        lector.setId(id);
        lectorDao.update(lector);
    }

    private static void makeActionToVariant9() {
        System.out.println(lectorDao.findAll());
    }

    private static void makeActionToVariant10(BufferedReader reader) throws IOException {
        Department department =  new Department();
        System.out.println("Enter department name :");
        String name = reader.readLine();
        department.setName(name);
        System.out.println("Enter institute: ");
        String institute = reader.readLine();
        department.setInstitute(institute);
        System.out.println("Enter id head of department: ");
        int headOfDepartmentId = Integer.parseInt(reader.readLine());
        department.setHeadOfDepartment(headOfDepartmentId);
        departmentDao.save(department);
    }

    private static void makeActionToVariant11(BufferedReader reader) throws IOException {
        System.out.println("Enter id to delete department: ");
        int id = Integer.parseInt(reader.readLine());
        departmentDao.delete(id);
    }

    private static void makeActionToVariant12(BufferedReader reader) throws IOException {
        Department department =  new Department();
        System.out.println("Enter id department to update: ");
        int id = Integer.parseInt(reader.readLine());
        department.setId(id);
        System.out.println("Set new department name :");
        String name = reader.readLine();
        department.setName(name);
        System.out.println("Set new institute: ");
        String institute = reader.readLine();
        department.setInstitute(institute);
        System.out.println("Set new id head of department: ");
        int headOfDepartmentId = Integer.parseInt(reader.readLine());
        department.setHeadOfDepartment(headOfDepartmentId);
        departmentDao.update(department);
    }

    private static void makeActionToVariant13() {
        System.out.println(departmentDao.findAll());
    }

    private static void makeActionToVariant14(BufferedReader reader) throws IOException {
        System.out.println("Enter lector id :");
        int lectorId = Integer.parseInt(reader.readLine());
        System.out.println("Enter department id :");
        int departmentId = Integer.parseInt(reader.readLine());
        departmentDao.addLectorToDepartment(departmentId, lectorId);
    }

    private static void menu(int choice, BufferedReader reader) throws IOException {
        logger.info("User choice was " + choice + " variant");
        switch (choice) {
            case 0 :
                isNotTheEnd = false;
                break;
            case 1 :
                makeActionToVariant1(reader);;
                break;
            case 2:
                makeActionToVariant2(reader);
                break;
            case 3:
                makeActionToVariant3(reader);
                break;
            case 4:
                makeActionToVariant4(reader);
                break;
            case 5:
                makeActionToVariant5(reader);
                break;
            case 6:
                makeActionToVariant6(reader);
                break;
            case 7:
                makeActionToVariant7(reader);
                break;
            case 8:
                makeActionToVariant8(reader);
                break;
            case 9:
                makeActionToVariant9();
                break;
            case 10:
                makeActionToVariant10(reader);
                break;
            case 11:
                makeActionToVariant11(reader);
                break;
            case 12:
                makeActionToVariant12(reader);
                break;
            case 13:
                makeActionToVariant13();
                break;
            case 14:
                makeActionToVariant14(reader);
                break;
            default:
                System.out.println("There is no such number, please choose another");
        }
    }

    public static void main(String[] args)  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (isNotTheEnd) {
            showMenu();
            try {
                int choice = Integer.parseInt(reader.readLine());
                menu(choice, reader);
            }catch (IOException e) {
                logger.error("Opss... Something went wrong");
            }
        }

    }

}
