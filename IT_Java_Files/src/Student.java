import java.util.*;
public class Student
{
	public String Name;
	public String Sex;
	public int Age;
	public String Level;
	public String Course;
	public Date Day;
	public void Attend(String name,String Mat_no,String course, String level, String Day){
		this.Course = course;
		this.Name = name;
		System.out.println(name+" attended"+"  "+course);
	}
}
