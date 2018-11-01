package seedu.modsuni.storage;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.annotation.XmlElement;

import seedu.modsuni.commons.exceptions.CorruptedFileException;
import seedu.modsuni.commons.exceptions.IllegalValueException;
import seedu.modsuni.commons.exceptions.InvalidPasswordException;
import seedu.modsuni.commons.util.DataSecurityUtil;
import seedu.modsuni.model.credential.Username;
import seedu.modsuni.model.module.Module;
import seedu.modsuni.model.module.UniqueModuleList;
import seedu.modsuni.model.user.Admin;
import seedu.modsuni.model.user.EmployDate;
import seedu.modsuni.model.user.Name;
import seedu.modsuni.model.user.PathToProfilePic;
import seedu.modsuni.model.user.Role;
import seedu.modsuni.model.user.Salary;
import seedu.modsuni.model.user.User;
import seedu.modsuni.model.user.student.EnrollmentDate;
import seedu.modsuni.model.user.student.Student;

/**
 * An User that is serializable to XML format
 */
public class XmlAdaptedUser {

    private static final String MISSING_FIELD_MESSAGE_FORMAT = "User's "
            + "%s field is missing!";

    private String userPassword = null;

    // Must have for all users
    @XmlElement(required = true)
    private String username;
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String role;
    @XmlElement(required = true)
    private String pathToProfilePic;

    // Attributes for Admin
    @XmlElement
    private String salary;
    @XmlElement
    private String employmentDate;

    // Attributes for Student
    @XmlElement
    private String enrollmentDate;
    @XmlElement
    private String major;
    @XmlElement
    private String minor;
    @XmlElement
    private List<XmlAdaptedModule> modulesTaken = new ArrayList<>();
    @XmlElement
    private List<XmlAdaptedModule> modulesStaged = new ArrayList<>();

    /**
     * Creates an empty XmlAdaptedUser.
     * This empty constructor is required for marshalling.
     */
    public XmlAdaptedUser() {}

    /**
     * Constructs an {@code XmlAdaptedUser} with the given user details.
     */
    public XmlAdaptedUser(Username username, Name name, Role role, PathToProfilePic pathToProfilePic,
                          Salary salary, EmployDate employmentDate) {
        this.username = username.toString();
        this.name = name.toString();
        this.role = role.toString();
        this.pathToProfilePic = pathToProfilePic.toString();
        this.salary = salary.toString();
        this.employmentDate = employmentDate.toString();
    }

    /**
     * Constructs an {@code XmlAdaptedUser} with the given user details.
     */
    public XmlAdaptedUser(Username username, Name name, Role role, PathToProfilePic pathToProfilePic,
                          EnrollmentDate enrollmentDate, List<String> major, List<String> minor,
                          List<Module> modulesTaken) {
        this.username = username.toString();
        this.name = name.toString();
        this.role = role.toString();
        this.pathToProfilePic = pathToProfilePic.toString();
        this.enrollmentDate = enrollmentDate.toString();
        this.major = major.toString();
        this.minor = minor.toString();
        this.modulesTaken.addAll(modulesTaken.stream().map(XmlAdaptedModule::new).collect(Collectors.toList()));
    }

    /**
     * Converts a given User into this class for JAXB use.
     *
     * @param user future changes to this will not affect the created XmlAdaptedUser
     */
    public XmlAdaptedUser(User user) {
        requireNonNull(user);
        this.username = user.getUsername().toString();
        this.name = user.getName().toString();
        this.role = user.getRole().toString();
        this.pathToProfilePic = user.getPathToProfilePic().toString();

        if (user.getRole() == Role.ADMIN) {
            Admin admin = (Admin) user;
            this.salary = admin.getSalary().toString();
            this.employmentDate = admin.getEmploymentDate().toString();
        }

        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;
            this.enrollmentDate = student.getEnrollmentDate().toString();
            this.major = student.getMajor().toString();
            this.minor = student.getMinor().toString();
            for (Module module : student.getModulesTaken()) {
                modulesTaken.add(new XmlAdaptedModule(module));
            }
            for (Module module : student.getModulesStaged()) {
                modulesStaged.add(new XmlAdaptedModule(module));
            }
        }
    }

    /**
     * Converts a given User into this class for JAXB use.
     *
     * @param user future changes to this will not affect the created XmlAdaptedUser
     */
    public XmlAdaptedUser(User user, String password) {
        this.userPassword = password;

        requireNonNull(user);
        this.username = DataSecurityUtil.bytesToHex(DataSecurityUtil.encrypt(
                user.getUsername().toString().getBytes(),password));
        this.name = user.getName().toString();
        this.role = user.getRole().toString();
        this.pathToProfilePic = user.getPathToProfilePic().toString();

        if (user.getRole() == Role.ADMIN) {
            Admin admin = (Admin) user;
            this.salary = DataSecurityUtil.bytesToHex(DataSecurityUtil.encrypt(
                    admin.getSalary().toString().getBytes(),password));
            this.employmentDate = admin.getEmploymentDate().toString();
        }

        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;
            this.enrollmentDate = student.getEnrollmentDate().toString();
            this.major = student.getMajor().toString();
            this.minor = student.getMinor().toString();
            for (Module module : student.getModulesTaken()) {
                modulesTaken.add(new XmlAdaptedModule(module));
            }
            for (Module module : student.getModulesStaged()) {
                modulesStaged.add(new XmlAdaptedModule(module));
            }
        }
    }

    /**
     * Converts this User into the model's {@code User} object.
     *
     * @throws IllegalValueException if there were any data constraints violated
     */
    public User toModelType(String password) throws IllegalValueException {
        User user = null;
        checkMandatoryFields();

        String decryptedUsername = decryptUsername(password);

        System.out.println("master:" +DataSecurityUtil.bytesToHex(DataSecurityUtil.encrypt(
                "master".getBytes(),"2b005cc8b610fa5899a9f9e592671bba9776a0e778c7f88db9b54eef48490e94")));

        System.out.println("salary:" +DataSecurityUtil.bytesToHex(DataSecurityUtil.encrypt(
                "9999".getBytes(),"2b005cc8b610fa5899a9f9e592671bba9776a0e778c7f88db9b54eef48490e94")));

        if ("ADMIN".equals(role)) {
            System.out.println("IN ADMIN");
            checkAdminFields();
            System.out.println("CURRENT SALARY:"+salary);
            String decryptedSalary = decryptSalary(password);
            System.out.println("Decrypted salary:" + decryptedSalary);
            user = new Admin(new Username(decryptedUsername), new Name(name), Role.ADMIN, new Salary(decryptedSalary),
                    new EmployDate(employmentDate));
        }

        if ("STUDENT".equals(role)) {
            System.out.println("IN STUDENT");
            checkStudentFields();
            List<String> majorConverted = Arrays.asList(major.substring(1, major.length() - 1).split(", "));
            List<String> minorConverted = Arrays.asList(minor.substring(1, minor.length() - 1).split(", "));

            UniqueModuleList modulesTakenConverted = new UniqueModuleList();
            for (XmlAdaptedModule moduleTake : modulesTaken) {
                modulesTakenConverted.add(moduleTake.toModelType());
            }

            UniqueModuleList modulesStagedConverted = new UniqueModuleList();
            for (XmlAdaptedModule moduleTake : modulesStaged) {
                modulesStagedConverted.add(moduleTake.toModelType());
            }

            user = new Student(new Username(decryptedUsername), new Name(name), Role.STUDENT,
                    new PathToProfilePic(pathToProfilePic), new EnrollmentDate(enrollmentDate),
                    majorConverted, minorConverted, modulesTakenConverted, modulesStagedConverted);
        }

        return user;
    }

    private String decryptUsername(String password) {
        try {
            return new String(DataSecurityUtil.decrypt(
                    DataSecurityUtil.hexToBytes(username), password), StandardCharsets.UTF_8);
        } catch (InvalidPasswordException | CorruptedFileException |
                NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptSalary(String password) {
        try {
            return new String(DataSecurityUtil.decrypt(
                    DataSecurityUtil.hexToBytes(salary), password), StandardCharsets.UTF_8);
        } catch (InvalidPasswordException | CorruptedFileException |
                NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks mandatory fields of user
     *
     * @throws IllegalValueException if there were any data constraints violated
     */
    private void checkMandatoryFields() throws IllegalValueException {
        // Username
        if (username == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "Username"));
        }
        if (!Username.isValidUsername(username)) {
            throw new IllegalValueException(Username.MESSAGE_USERNAME_CONSTRAINTS);
        }

        // Name
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }

        // Role
        if (role == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "role"));
        }

        // Path to profilepic
        if (pathToProfilePic == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "pathToProfilePic"));
        }
        if (!PathToProfilePic.isValidPath(pathToProfilePic)) {
            throw new IllegalValueException(PathToProfilePic.MESSAGE_PATH_CONSTRAINTS);
        }
    }

    /**
     * Checks mandatory fields of admin
     *
     * @throws IllegalValueException if there were any data constraints violated
     */
    private void checkAdminFields() throws IllegalValueException {
        // Salary
        if (salary == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "salary"));
        }

        // employment date
        if (employmentDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "employment"));
        }
        if (!EmployDate.isValidEmployDate(employmentDate)) {
            throw new IllegalValueException(EmployDate.MESSAGE_DATE_CONSTRAINTS);
        }
    }

    /**
     * Checks mandatory fields of student
     *
     * @throws IllegalValueException if there were any data constraints violated
     */
    private void checkStudentFields() throws IllegalValueException {
        if (enrollmentDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "enrollment"));
        }
        if (!EnrollmentDate.isValidEmployDate(enrollmentDate)) {
            throw new IllegalValueException(EnrollmentDate.MESSAGE_DATE_CONSTRAINTS);
        }
        if (major == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "major"));
        }
        if (minor == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "minor"));
        }
    }

}
