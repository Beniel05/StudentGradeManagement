import java.io.*;
import java.util.*;

class Student {

    String id;
    String name;
    double grade;

    public Student(String id, String name, double grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public String getLetterGrade() {
        if (grade >= 90) {
            return "A+"; 
        }else if (grade >= 80) {
            return "A"; 
        }else if (grade >= 70) {
            return "B+"; 
        }else if (grade >= 60) {
            return "B"; 
        }else if (grade >= 50) {
            return "C+"; 
        }else if (grade >= 40) {
            return "C"; 
        }else if (grade >= 35) {
            return "D"; 
        }else {
            return "F";
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %-6s Name: %-20s Grade: %.2f (%s)", id, name, grade, getLetterGrade());
    }
}

public class StudentGradeTracker {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "student_records.txt";
    private static final ArrayList<Student> students = new ArrayList<>();
    private static int idCounter = 1000;

    public static void main(String[] args) {
        loadStudentsFromFile();

        int choice;
        do {
            System.out.println("\n--- Student Grade Tracker ---");
            System.out.println("1. Add Students");
            System.out.println("2. View Grade Summary");
            System.out.println("3. Edit Student Grade");
            System.out.println("4. Delete Student");
            System.out.println("5. Search Student");
            System.out.println("6. Exit\n");

            choice = getValidInt("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    addStudents();
                case 2 ->
                    displaySummary();
                case 3 ->
                    editStudentGrade();
                case 4 ->
                    deleteStudent();
                case 5 ->
                    searchStudent();
                case 6 ->
                    System.out.println("Exiting application.");
                default ->
                    System.out.println("\nInvalid choice. Please enter a number between 1 and 6.\n");
            }
        } while (choice != 6);
    }

    private static void addStudents() {
        int count;
        while (true) {
            count = getValidInt("Enter the number of students to add: ");
            if (count > 0) {
                break;
            }
            System.out.println("Please enter a number greater than 0.\n");
        }

        for (int i = 1; i <= count; i++) {
            String name;
            while (true) {
                System.out.print("Enter name of student " + i + ": ");
                name = scanner.nextLine().trim();
                if (name.isEmpty() || !name.matches("^[a-zA-Z][a-zA-Z0-9 ]*$")) {
                    System.out.println("Invalid name. Name must start with a letter and contain only letters, numbers, and spaces.\n");
                } else {
                    break;
                }
            }

            double grade;
            while (true) {
                System.out.print("Enter grade for " + name + " (0 - 100): ");
                if (scanner.hasNextDouble()) {
                    grade = scanner.nextDouble();
                    scanner.nextLine(); // consume newline
                    if (grade >= 0 && grade <= 100) {
                        break; 
                    }else {
                        System.out.println("Grade must be between 0 and 100.\n");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 0 and 100.\n");
                    scanner.nextLine(); // clear invalid input
                }
            }

            String id = "S" + (++idCounter);
            Student student = new Student(id, name, grade);
            students.add(student);
            saveStudentToFileFormatted(student);
            System.out.println("Student added successfully.\n");
        }
    }

    private static void displaySummary() {
        if (students.isEmpty()) {
            System.out.println("\nNo student data available.\n");
            return;
        }

        double total = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        Student topStudent = null, lowStudent = null;

        System.out.println("\n--- Grade Summary Report ---");
        System.out.println("------------------------------");

        for (Student student : students) {
            System.out.println(student);
            total += student.grade;

            if (student.grade > highest) {
                highest = student.grade;
                topStudent = student;
            }
            if (student.grade < lowest) {
                lowest = student.grade;
                lowStudent = student;
            }
        }

        double average = total / students.size();
        System.out.println("------------------------------");
        System.out.printf("Average Grade : %.2f%n", average);
        if (topStudent != null) {
            System.out.printf("Highest Grade : %.2f (by %s)%n", topStudent.grade, topStudent.name);
        }
        if (lowStudent != null) {
            System.out.printf("Lowest Grade  : %.2f (by %s)%n", lowStudent.grade, lowStudent.name);
        }
        System.out.println();
    }

    private static void editStudentGrade() {
        System.out.print("Enter student ID to edit: ");
        String id = scanner.nextLine().trim();

        for (Student student : students) {
            if (student.id.equalsIgnoreCase(id)) {
                double newGrade;
                while (true) {
                    System.out.print("Enter new grade for " + student.name + " (0–100): ");
                    if (scanner.hasNextDouble()) {
                        newGrade = scanner.nextDouble();
                        scanner.nextLine();
                        if (newGrade >= 0 && newGrade <= 100) {
                            student.grade = newGrade;
                            rewriteAllToFile();
                            System.out.println("Grade updated successfully.\n");
                            return;
                        } else {
                            System.out.println("Grade must be between 0 and 100.\n");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter a valid grade.\n");
                        scanner.nextLine();
                    }
                }
            }
        }

        System.out.println("\nStudent ID not found.\n");
    }

    private static void deleteStudent() {
        System.out.print("Enter name of student to delete: ");
        String name = scanner.nextLine().trim();

        List<Student> matches = new ArrayList<>();
        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                matches.add(student);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("\nNo student found with that name.\n");
            return;
        }

        if (matches.size() == 1) {
            Student student = matches.get(0);
            System.out.print("Are you sure you want to delete " + student.name + " (ID: " + student.id + ")? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                students.remove(student);
                rewriteAllToFile();
                System.out.println("Student deleted successfully.\n");
            } else {
                System.out.println("Deletion canceled.\n");
            }
        } else {
            System.out.println("\nMultiple students found:");
            for (Student s : matches) {
                System.out.println(s);
            }
            System.out.print("\nEnter the ID of the student you want to delete: ");
            String idToDelete = scanner.nextLine().trim();

            for (Student s : matches) {
                if (s.id.equalsIgnoreCase(idToDelete)) {
                    System.out.print("Are you sure you want to delete student ID " + s.id + "? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("yes")) {
                        students.remove(s);
                        rewriteAllToFile();
                        System.out.println("Student deleted successfully.\n");
                    } else {
                        System.out.println("Deletion canceled.\n");
                    }
                    return;
                }
            }
            System.out.println("Invalid ID. No student deleted.\n");
        }
    }

    private static void searchStudent() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();

        boolean found = false;
        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                if (!found) {
                    System.out.println("\nSearch Results:");
                    System.out.println("----------------");
                    found = true;
                }
                System.out.println(student);
            }
        }

        if (!found) {
            System.out.println("\nNo student found with that name.\n");
        } else {
            System.out.println();
        }
    }

    private static void saveStudentToFileFormatted(Student student) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            writer.println("------ Student Record ------");
            writer.printf("ID          : %s%n", student.id);
            writer.printf("Name        : %s%n", student.name);
            writer.printf("Grade       : %.2f%n", student.grade);
            writer.printf("Letter Grade: %s%n", student.getLetterGrade());
            writer.println("----------------------------");
            writer.println();
        } catch (IOException e) {
            System.out.println("Error saving student record to file.\n");
        }
    }

    private static void rewriteAllToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.println("------ Student Record ------");
                writer.printf("ID          : %s%n", student.id);
                writer.printf("Name        : %s%n", student.name);
                writer.printf("Grade       : %.2f%n", student.grade);
                writer.printf("Letter Grade: %s%n", student.getLetterGrade());
                writer.println("----------------------------");
                writer.println();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file.\n");
        }
    }

    private static void loadStudentsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "", name = "";
            double grade = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID")) {
                    id = line.split(":")[1].trim();
                } else if (line.startsWith("Name")) {
                    name = line.split(":")[1].trim();
                } else if (line.startsWith("Grade")) {
                    grade = Double.parseDouble(line.split(":")[1].trim());
                } else if (line.startsWith("Letter")) {
                    students.add(new Student(id, name, grade));
                    try {
                        int numericId = Integer.parseInt(id.substring(1));
                        idCounter = Math.max(idCounter, numericId);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading student records.\n");
        }
    }

    private static int getValidInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int num = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return num;
            } else {
                System.out.println("Please enter a valid number.\n");
                scanner.nextLine(); // clear buffer
            }
        }
    }
}