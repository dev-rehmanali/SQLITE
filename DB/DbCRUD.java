package DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wang.blair.Personbook.CaseNote;
import wang.blair.Personbook.Person;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;

/**
 * @author Rehmanali
 * All the basice operations to comunicate with
 * the database(Personbook.db) are in this class
 */
public class DbCRUD {

    private String insertPersonQuery = "INSERT INTO Person(person_name,bday_month_day,birthday_year,importand_personal,new_contact_not_yet_saved) VALUES(?,?,?,?,?)";
    private String insertCaseNoteQuery = "INSERT INTO CaseNote(person_name,local_date_time,case_note_text) VALUES(?,?,?)";
    private String getPersonQuery = "SELECT person_name,bday_month_day,birthday_year,importand_personal,new_contact_not_yet_saved FROM Person";
    private String getCaseNoteQuery = "SELECT person_name,local_date_time,case_note_text FROM CaseNote";
    private String updatePersonQuery = "UPDATE employees SET person_name = '?' bday_month_day = '?',birthday_year = '?',importand_personal = '?',new_contact_not_yet_saved = '?' WHERE person_name = '?';";
    private PreparedStatement insertPersonPSTMT;
    private PreparedStatement insertCaseNotePSTMT;
    private Statement getPersonSTMT;
    private Statement getCaseNoteSTMT;
    private PreparedStatement updatePersonSTMT;
    private Connection conn;

    public DbCRUD() {

        ConnectDB connectDB = new ConnectDB();
        conn = connectDB.getConnection();

        try {

            insertPersonPSTMT = conn.prepareStatement(insertPersonQuery);
            insertCaseNotePSTMT = conn.prepareStatement(insertCaseNoteQuery);
            getPersonSTMT = conn.createStatement();
            getCaseNoteSTMT = conn.createStatement();
            updatePersonSTMT = conn.prepareStatement(updatePersonQuery);

        } catch (SQLException e) {
            System.out.println("1 " + e.getMessage());
        }

    }

    /**
     *
     * @param person person to be stored in
     *               the database
     */
    public void insertPerson(Person person) {

        try {

            insertPersonPSTMT.setString(1, person.getFullName());
            insertPersonPSTMT.setString(2, person.getBdayMonthDay().getMonthValue() + "-" + person.getBdayMonthDay().getDayOfMonth());
            insertPersonPSTMT.setInt(3, person.getBirthdayYear().getValue());
            insertPersonPSTMT.setBoolean(4, person.isImportantPersonal());
            insertPersonPSTMT.setBoolean(5, person.isNewContactNotYetSaved());

            //insert all case notes
            ObservableList<CaseNote> list = person.getCaseNotes();
            for (CaseNote caseNote:list) {
                insertCaseNote(person,caseNote);
            }

            insertPersonPSTMT.executeUpdate();

        } catch (SQLException e) {
            System.out.println("2 " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param person Person against which CaseNotes to be stored
     * @param caseNote CaseNotes to be stored in the database
     */
    public void insertCaseNote(Person person, CaseNote caseNote) {

        try {

            insertCaseNotePSTMT.setString(1, person.getFullName());
            insertCaseNotePSTMT.setString(2, String.valueOf(caseNote.getCreateTime()));
            insertCaseNotePSTMT.setString(3, caseNote.getCaseNoteText());

            insertCaseNotePSTMT.executeUpdate();

        } catch (SQLException e) {
            System.out.println("3 " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @return Observable List of all the persons in the database
     */
    public ObservableList<Person> getPersonsList() {

        ObservableList<Person> personList = FXCollections.observableArrayList();

        try {

            ResultSet rs = getPersonSTMT.executeQuery(getPersonQuery);

            // loop through the result set
            while (rs.next()) {

                Person person = new Person();
                person.setFullName(rs.getString("person_name"));
                String[] arr = rs.getString("bday_month_day").split("-");
                person.setBdayMonthDay(MonthDay.of(Integer.valueOf(arr[0]),Integer.valueOf(arr[1])));
                person.setBirthdayYear(Year.of(rs.getInt("birthday_year")));
                person.setImportantPersonal(rs.getBoolean("importand_personal"));
                person.setNewContactNotYetSaved(rs.getBoolean("new_contact_not_yet_saved"));
                person.setCaseNotes(this.getCaseNoteList(person));

                personList.add(person);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return personList;

    }

    /**
     *
     * @param person Person Against which we need to get CaseNotes
     * @return Observable list of CaseNotes
     */
    public ObservableList<CaseNote> getCaseNoteList(Person person) {

        ObservableList<CaseNote> caseNoteList = FXCollections.observableArrayList();

        try {

            ResultSet rs    = getCaseNoteSTMT.executeQuery(getCaseNoteQuery);

            // loop through the result set
            while (rs.next()) {

                CaseNote caseNote = new CaseNote();

                if(person.getFullName().equals(rs.getString("person_name")) && !(rs.getString("case_note_text").isEmpty())){

                    caseNote.setCreateTime(LocalDateTime.parse(rs.getString("local_date_time")));
                    caseNote.setCaseNoteText(rs.getString("case_note_text"));
                    caseNoteList.add(caseNote);
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return caseNoteList;

    }

    /**
     * This may be used if required
     * @param person Person to be updated in the database
     * @param name against which we need to update record
     */
    public void updatePerson(Person person,String name) {

        try {

            updatePersonSTMT.setString(1, person.getFullName());
            updatePersonSTMT.setString(2, person.getBdayMonthDay().getMonthValue() + "-" + person.getBdayMonthDay().getDayOfMonth());
            updatePersonSTMT.setInt(3, person.getBirthdayYear().getValue());
            updatePersonSTMT.setBoolean(4, person.isImportantPersonal());
            updatePersonSTMT.setBoolean(5, person.isNewContactNotYetSaved());
            updatePersonSTMT.setString(6, name);

            insertPersonPSTMT.executeUpdate();

        } catch (SQLException e) {
            System.out.println("2 " + e.getMessage());
            e.printStackTrace();
        }
    }

}

