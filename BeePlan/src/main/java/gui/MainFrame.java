package gui;

import datastructures.Constraint;
import datastructures.Course;
import datastructures.Schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map; // Added import for Map

public class MainFrame extends JFrame {
    private JButton btnLoadCommon;
    private JButton btnLoadCurriculum;
    private JButton btnLoadInstructor;
    private JButton btnGenerate;
    private JLabel statusLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    private Schedule schedule;
    private Constraint constraint;

    private final List<String> TIMESLOTS = Arrays.asList(
            "08:30-10:00","10:00-11:30","11:30-13:00","13:30-15:00","15:00-16:30"
    );
    private final String[] DAYS = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri"};

    public MainFrame() {
        super("BeePlan - Course Scheduling");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 500);
        this.setLocationRelativeTo(null);

        schedule = new Schedule();
        constraint = new Constraint();

        initUI();
    }

    private void initUI() {
        this.setLayout(new BorderLayout());

        // Top panel with load buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnLoadCommon = new JButton("Load Common Schedule");
        btnLoadCurriculum = new JButton("Load Curriculum");
        btnLoadInstructor = new JButton("Load Instructor Constraints");
        topPanel.add(btnLoadCommon);
        topPanel.add(btnLoadCurriculum);
        topPanel.add(btnLoadInstructor);
        this.add(topPanel, BorderLayout.NORTH);

        // Center panel - table
        tableModel = new DefaultTableModel(null, DAYS) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        populateEmptyTable();
        JScrollPane scroll = new JScrollPane(table);
        this.add(scroll, BorderLayout.CENTER);

        // Bottom panel - generate + status
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerate = new JButton("Generate Schedule");
        leftBottom.add(btnGenerate);
        bottom.add(leftBottom, BorderLayout.WEST);

        statusLabel = new JLabel("Ready");
        bottom.add(statusLabel, BorderLayout.CENTER);

        this.add(bottom, BorderLayout.SOUTH);

        // action listeners
        btnLoadCommon.addActionListener(this::onLoadCommon);
        btnLoadCurriculum.addActionListener(this::onLoadCurriculum);
        btnLoadInstructor.addActionListener(this::onLoadInstructor);
        btnGenerate.addActionListener(this::onGenerate);
    }

    private void populateEmptyTable() {
        tableModel.setRowCount(0);
        for (String slot : TIMESLOTS) {
            Object[] row = new Object[DAYS.length];
            row[0] = slot;
            for (int i = 1; i < DAYS.length; i++) row[i] = "";
            tableModel.addRow(row);
        }
    }

    // Simulated loaders: in a real app these would parse CSVs. Here we mock minimal data for demonstration.
    private void onLoadCommon(ActionEvent e) {
        schedule = new Schedule();
        statusLabel.setText("Common schedule loaded (mock).");
    }

    private void onLoadCurriculum(ActionEvent e) {
        // add a few mock courses
        schedule = new Schedule();
        Course c1 = new Course("CS101", "Intro CS", 2, 0, "Dr. A", Course.Type.MANDATORY);
        Course c2 = new Course("CS102", "Data Structures", 2, 2, "Dr. B", Course.Type.MANDATORY);
        Course c3 = new Course("CS201", "Elective AI", 2, 0, "Dr. A", Course.Type.ELECTIVE);
        c2.setLabCapacity(30);
        schedule.addCourse(c1);
        schedule.addCourse(c2);
        schedule.addCourse(c3);
        statusLabel.setText("Curriculum loaded (3 mock courses).");
    }

    private void onLoadInstructor(ActionEvent e) {
        // In a real UI we'd parse constraints. Set constraint toggles as default.
        constraint = new Constraint();
        statusLabel.setText("Instructor constraints loaded (defaults).");
    }

    private void onGenerate(ActionEvent e) {
        btnGenerate.setEnabled(false);
        statusLabel.setText("Generating schedule...");
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                publish("Loading data...");
                // Simulated load: ensure schedule has courses
                if (schedule.getCourseList().isEmpty()) {
                    publish("No curriculum loaded — adding demo courses.");
                    onLoadCurriculum(null);
                }

                publish("Checking constraints...");
                List<String> problems = constraint.runAll(schedule);
                if (!problems.isEmpty()) {
                    for (String p : problems) publish("Conflict: " + p);
                    return null;
                }

                publish("Placing courses...");
                schedule.placeCoursesAuto(TIMESLOTS);

                publish("Updating table...");
                updateTableFromSchedule();

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                String last = chunks.get(chunks.size()-1);
                statusLabel.setText(last);
            }

            @Override
            protected void done() {
                btnGenerate.setEnabled(true);
                statusLabel.setText("Schedule generation finished.");
            }
        };
        worker.execute();
    }

    private void updateTableFromSchedule() {
        SwingUtilities.invokeLater(() -> {
            populateEmptyTable();
            // weeklyGrid: day -> timeslot -> Course
            var grid = schedule.getWeeklyGrid();
            for (int r = 0; r < TIMESLOTS.size(); r++) {
                String slot = TIMESLOTS.get(r);
                for (int c = 1; c < DAYS.length; c++) {
                        // Days array is ["Time","Mon","Tue","Wed","Thu","Fri"]
                        String day = DAYS[c];
                        Map<String, Course> dayMap = grid.get(day);
                    if (dayMap == null) continue;
                    Course course = dayMap.get(slot);
                    if (course != null) {
                        tableModel.setValueAt(course.getCourseId(), r, c);
                    }
                }
            }
        });
    }
}