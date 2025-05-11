package gui;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import model.ClassSession;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeParseException;

/**
 * Диалог для редактирования ClassSession.
 */
public class EditClassDialog extends JDialog {
    private final ClassSession session;
    private boolean saved = false;

    private final JTextField tfInstructor;
    private final JTextField tfTime;

    public EditClassDialog(Window owner, ClassSession session) {
        super(owner, "Edit Class", ModalityType.APPLICATION_MODAL);
        this.session = session;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Instructor
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Instructor:"), gbc);
        tfInstructor = new JTextField(session.getInstructorName(), 20);
        gbc.gridx = 1;
        panel.add(tfInstructor, gbc);

        // Time (ISO_LOCAL_DATE_TIME format)
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Time:"), gbc);
        tfTime = new JTextField(session.getTime().toString(), 20);
        gbc.gridx = 1;
        panel.add(tfTime, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        // Actions
        btnSave.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    session.setInstructorName(tfInstructor.getText().trim());
                    // Парсим строку в LocalDateTime
                    LocalDateTime ldt = LocalDateTime.parse(tfTime.getText().trim());
                    // Преобразуем в java.sql.Timestamp и устанавливаем
                    session.setTime(Timestamp.valueOf(ldt));
                    saved = true;
                    dispose();
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(EditClassDialog.this,
                            "Invalid time format. Use YYYY-MM-DDTHH:MM:SS",
                            "Format Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        btnCancel.addActionListener(e -> dispose());

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * @return true if user clicked Save and data was updated
     */
    public boolean isSaved() {
        return saved;
    }
}
