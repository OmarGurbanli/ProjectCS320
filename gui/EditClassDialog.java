package gui;

import dao.ClassDAO;
import dao.InstructorDAO;
import model.ClassSession;
import model.Instructor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Диалог для редактирования ClassSession.
 */
public class EditClassDialog extends JDialog {
    private final ClassSession session;
    private boolean saved = false;

    private final JComboBox<Instructor> cbInstructor;
    private final JTextField tfTime;
    private final JTextField tfCapacity;

    public EditClassDialog(Window owner, ClassSession session) {
        super(owner, "Edit Class", ModalityType.APPLICATION_MODAL);
        this.session = session;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Instructor selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Instructor:"), gbc);
        cbInstructor = new JComboBox<>();
        try {
            List<Instructor> instructors = new InstructorDAO().findAll();
            for (Instructor ins : instructors) {
                cbInstructor.addItem(ins);
                if (ins.getId() == session.getInstructorId()) {
                    cbInstructor.setSelectedItem(ins);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading instructors: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        gbc.gridx = 1;
        panel.add(cbInstructor, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Time (YYYY-MM-DD HH:MM:SS):"), gbc);
        tfTime = new JTextField(session.getTime().toString(), 20);
        gbc.gridx = 1;
        panel.add(tfTime, gbc);

        // Capacity
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Capacity:"), gbc);
        tfCapacity = new JTextField(String.valueOf(session.getCapacity()), 20);
        gbc.gridx = 1;
        panel.add(tfCapacity, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        btnSave.addActionListener(e -> {
            try {
                Instructor sel = (Instructor) cbInstructor.getSelectedItem();
                if (sel == null) throw new IllegalArgumentException("No instructor selected");
                Timestamp ts = Timestamp.valueOf(tfTime.getText().trim());
                int cap = Integer.parseInt(tfCapacity.getText().trim());

                new ClassDAO().update(
                        session.getId(),
                        ts,
                        sel.getId(),
                        cap
                );
                saved = true;
                dispose();
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input format.\n" +
                                "- Select an instructor.\n" +
                                "- Time must be 'YYYY-MM-DD HH:MM:SS'.\n" +
                                "- Capacity must be integer.",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "DB error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    /** @return true if user clicked Save and changes were saved */
    public boolean isSaved() {
        return saved;
    }
}
