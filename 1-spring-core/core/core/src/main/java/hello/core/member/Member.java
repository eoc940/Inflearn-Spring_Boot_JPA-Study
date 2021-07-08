package hello.core.member;
//        괄호안에서 ; 로 코드완성하기 : Ctrl + Shift + Enter.
//        변수 추출하기 : Ctrl + Alt + v.
//        생성자,Getter와Setter,메서드 오버라이딩등 자동생성 : Alt + Insert.
//        자바클래스 main메서드 자동생성 : main + Tab.

public class Member {

    private Long id;
    private String name;
    private Grade grade;

    public Member(Long id, String name, Grade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
