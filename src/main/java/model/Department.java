package model;

public class Department {

    private Integer id;
    private String name;
    private String institute;
    private Integer headOfDepartment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public Integer getHeadOfDepartment() {
        return headOfDepartment;
    }

    public void setHeadOfDepartment(Integer headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", institute='" + institute + '\'' +
                ", head of department='" + headOfDepartment + '\'' +
                "}\n";
    }
}
