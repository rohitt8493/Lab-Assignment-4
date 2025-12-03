import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Student {
    private Integer rollNo;
    private String name;
    private String email;
    private String course;
    private Double marks;

    public Student(Integer rollNo, String name, String email, String course, Double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.marks = marks;
    }

    public Integer getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public Double getMarks() { return marks; }

    public String toCsv() {
        return rollNo + "," + name + "," + email + "," + course + "," + marks;
    }

    @Override
    public String toString() {
        return "Roll No: " + rollNo + "\nName: " + name + "\nEmail: " + email + "\nCourse: " + course + "\nMarks: " + marks;
    }
}

class FileUtil {
    public static List<Student> readStudents(String path) {
        List<Student> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 5) continue;
                try {
                    Integer roll = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String course = parts[3].trim();
                    Double marks = Double.parseDouble(parts[4].trim());
                    if (name.isEmpty() || email.isEmpty() || course.isEmpty()) continue;
                    list.add(new Student(roll, name, email, course, marks));
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static void writeStudents(String path, List<Student> students) {
        File f = new File(path);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                bw.write(s.toCsv());
                bw.newLine();
            }
            bw.flush();
        } catch (IOException ignored) {}
    }

    public static void printFileAttributes(String path) {
        File f = new File(path);
        System.out.println("File: " + f.getAbsolutePath());
        System.out.println("Exists: " + f.exists());
        System.out.println("Readable: " + f.canRead());
        System.out.println("Writable: " + f.canWrite());
        System.out.println("Size(bytes): " + (f.exists() ? f.length() : 0));
        System.out.println("Last Modified: " + (f.exists() ? new Date(f.lastModified()) : "N/A"));
    }

    public static String readRandom(String path, long position, int length) {
        File f = new File(path);
        if (!f.exists()) return "";
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            if (position < 0) position = 0;
            if (position > raf.length()) position = raf.length();
            raf.seek(position);
            byte[] buf = new byte[Math.max(0, Math.min(length, (int)(raf.length() - position)))];
            raf.read(buf);
            return new String(buf, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}

class StudentManager {
    private final List<Student> students = new ArrayList<>();
    private final Map<String, List<Student>> byName = new HashMap<>();
    private final Map<Integer, Student> byRoll = new HashMap<>();

    public void load(String path) {
        students.clear();
        byName.clear();
        byRoll.clear();
        students.addAll(FileUtil.readStudents(path));
        for (Student s : students) index(s);
    }

    public void save(String path) {
        FileUtil.writeStudents(path, students);
    }

    private void index(Student s) {
        byRoll.put(s.getRollNo(), s);
        byName.computeIfAbsent(s.getName().toLowerCase(), k -> new ArrayList<>()).add(s);
    }

    public boolean add(Student s) {
        if (s.getName() == null || s.getName().trim().isEmpty()) return false;
        if (s.getEmail() == null || s.getEmail().trim().isEmpty()) return false;
        if (s.getCourse() == null || s.getCourse().trim().isEmpty()) return false;
        if (s.getMarks() == null) return false;
        students.add(s);
        index(s);
        return true;
    }

    public void viewAll() {
        Iterator<Student> it = students.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
            System.out.println();
        }
    }

    public List<Student> searchByName(String name) {
        if (name == null) return Collections.emptyList();
        List<Student> res = byName.get(name.toLowerCase());
        if (res == null) return Collections.emptyList();
        return new ArrayList<>(res);
    }

    public boolean deleteByName(String name) {
        List<Student> list = byName.get(name.toLowerCase());
        if (list == null || list.isEmpty()) return false;
        boolean changed = false;
        for (Student s : new ArrayList<>(list)) {
            students.remove(s);
            changed = true;
        }
        byName.remove(name.toLowerCase());
        Iterator<Map.Entry<Integer, Student>> it = byRoll.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Student> e = it.next();
            if (e.getValue().getName().equalsIgnoreCase(name)) it.remove();
        }
        return changed;
    }

    public void sortByMarks(boolean ascending) {
        Comparator<Student> cmp = Comparator.comparingDouble(Student::getMarks);
        if (!ascending) cmp = cmp.reversed();
        students.sort(cmp);
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
}

public class Code {
    private static final String DATA_FILE = "students.txt";

    private static Student promptStudent(Scanner sc) {
        System.out.print("Enter Roll No: ");
        Integer roll = readInt(sc);
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter Course: ");
        String course = sc.nextLine().trim();
        System.out.print("Enter Marks: ");
        Double marks = readDouble(sc);
        return new Student(roll, name, email, course, marks);
    }

    private static Integer readInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
            System.out.print("Invalid integer. Try again: ");
        }
    }

    private static Double readDouble(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
            System.out.print("Invalid number. Try again: ");
        }
    }

    private static void printLoaded(List<Student> list) {
        if (list.isEmpty()) return;
        System.out.println("Loaded students from file:");
        for (Student s : list) {
            System.out.println(s.toString());
            System.out.println();
        }
    }

    private static void menu() {
        System.out.println("Capstone Student Menu");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search by Name");
        System.out.println("4. Delete by Name");
        System.out.println("5. Sort by Marks");
        System.out.println("6. Sort by Name");
        System.out.println("7. File Attributes");
        System.out.println("8. Random Read Demo");
        System.out.println("9. Save and Exit");
        System.out.print("Enter choice: ");
    }

    public static void main(String[] args) {
        StudentManager mgr = new StudentManager();
        mgr.load(DATA_FILE);
        printLoaded(mgr.getStudents());
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            menu();
            String choiceLine = sc.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(choiceLine); } catch (NumberFormatException e) { choice = -1; }
            switch (choice) {
                case 1: {
                    Student s = promptStudent(sc);
                    if (mgr.add(s)) System.out.println("Student added.\n");
                    else System.out.println("Invalid data. Not added.\n");
                    break;
                }
                case 2: {
                    mgr.viewAll();
                    break;
                }
                case 3: {
                    System.out.print("Enter Name to search: ");
                    String name = sc.nextLine().trim();
                    List<Student> res = mgr.searchByName(name);
                    if (res.isEmpty()) System.out.println("No records found.\n");
                    else {
                        for (Student s : res) {
                            System.out.println(s.toString());
                            System.out.println();
                        }
                    }
                    break;
                }
                case 4: {
                    System.out.print("Enter Name to delete: ");
                    String name = sc.nextLine().trim();
                    boolean ok = mgr.deleteByName(name);
                    System.out.println(ok ? "Deleted.\n" : "No matching records.\n");
                    break;
                }
                case 5: {
                    System.out.print("Sort by marks ascending? (y/n): ");
                    String ans = sc.nextLine().trim().toLowerCase();
                    boolean asc = ans.startsWith("y");
                    mgr.sortByMarks(asc);
                    System.out.println("Sorted Student List by Marks:");
                    mgr.viewAll();
                    break;
                }
                case 6: {
                    mgr.sortByName();
                    System.out.println("Sorted Student List by Name:");
                    mgr.viewAll();
                    break;
                }
                case 7: {
                    FileUtil.printFileAttributes(DATA_FILE);
                    System.out.println();
                    break;
                }
                case 8: {
                    System.out.print("Enter byte position: ");
                    long pos;
                    try { pos = Long.parseLong(sc.nextLine().trim()); } catch (NumberFormatException e) { pos = 0; }
                    System.out.print("Enter length: ");
                    int len;
                    try { len = Integer.parseInt(sc.nextLine().trim()); } catch (NumberFormatException e) { len = 64; }
                    String snippet = FileUtil.readRandom(DATA_FILE, pos, len);
                    System.out.println("Random read snippet:");
                    System.out.println(snippet);
                    System.out.println();
                    break;
                }
                case 9: {
                    mgr.save(DATA_FILE);
                    System.out.println("Saved. Exiting.");
                    running = false;
                    break;
                }
                default: {
                    System.out.println("Invalid choice.\n");
                }
            }
        }
        sc.close();
    }
}
