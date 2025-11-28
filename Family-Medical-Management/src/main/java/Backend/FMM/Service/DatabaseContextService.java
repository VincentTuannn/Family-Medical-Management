package Backend.FMM.Service;

import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.MedicalRecordRepository;
import Backend.FMM.Repository.AppointmentRepository;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Entity.MedicalRecord;
import Backend.FMM.Entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseContextService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Query database and build context based on user message
     */
    public String buildDatabaseContext(String userMessage, Integer userId) {
        StringBuilder context = new StringBuilder();
        context.append("Thông tin từ cơ sở dữ liệu:\n\n");
        
        // Detect what user is asking about
        boolean hasPatientInfo = false;
        boolean hasMedicalInfo = false;
        boolean hasAppointmentInfo = false;
        
        // Get user's patients
        List<Patient> patients = patientRepository.findByUser_UserId(userId);
        if (!patients.isEmpty()) {
            hasPatientInfo = true;
            context.append("=== THÔNG TIN BỆNH NHÂN ===\n");
            for (Patient patient : patients) {
                context.append(String.format(
                    "Bệnh nhân: %s\n" +
                    "- Ngày sinh: %s\n" +
                    "- Giới tính: %s\n" +
                    "- Nhóm máu: %s\n" +
                    "- Liên hệ khẩn cấp: %s\n" +
                    "- Ngày tạo: %s\n\n",
                    patient.getFullName(),
                    patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "N/A",
                    patient.getGender() != null ? patient.getGender().name() : "N/A",
                    patient.getBloodType() != null ? patient.getBloodType() : "N/A",
                    patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A",
                    patient.getCreatedAt() != null ? dateTimeFormat.format(patient.getCreatedAt()) : "N/A"
                ));
            }
        }
        
        // Get medical records for user's patients
        List<Integer> patientIds = patients.stream()
            .map(Patient::getPatientId)
            .collect(Collectors.toList());
        
        if (!patientIds.isEmpty()) {
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            for (Integer patientId : patientIds) {
                medicalRecords.addAll(medicalRecordRepository.findByPatient_PatientId(patientId));
            }
            
            if (!medicalRecords.isEmpty()) {
                hasMedicalInfo = true;
                context.append("=== HỒ SƠ Y TẾ ===\n");
                for (MedicalRecord record : medicalRecords) {
                    String patientName = record.getPatient() != null ? record.getPatient().getFullName() : "N/A";
                    context.append(String.format(
                        "Bệnh nhân: %s\n" +
                        "- Ngày khám: %s\n" +
                        "- Chẩn đoán: %s\n" +
                        "- Điều trị: %s\n" +
                        "- Thuốc: %s\n" +
                        "- Dị ứng: %s\n" +
                        "- Ghi chú: %s\n" +
                        "- Bác sĩ: %s\n\n",
                        patientName,
                        record.getRecordDate() != null ? dateFormat.format(record.getRecordDate()) : "N/A",
                        record.getDiagnosis() != null ? record.getDiagnosis() : "N/A",
                        record.getTreatment() != null ? record.getTreatment() : "N/A",
                        record.getMedications() != null ? record.getMedications() : "N/A",
                        record.getAllergies() != null ? record.getAllergies() : "N/A",
                        record.getNotes() != null ? record.getNotes() : "N/A",
                        record.getDoctorName() != null ? record.getDoctorName() : "N/A"
                    ));
                }
            }
        }
        
        // Get appointments for user's patients
        if (!patientIds.isEmpty()) {
            List<Appointment> appointments = new ArrayList<>();
            for (Integer patientId : patientIds) {
                appointments.addAll(appointmentRepository.findByPatient_PatientId(patientId));
            }
            
            if (!appointments.isEmpty()) {
                hasAppointmentInfo = true;
                context.append("=== LỊCH HẸN ===\n");
                for (Appointment appointment : appointments) {
                    String patientName = appointment.getPatient() != null ? appointment.getPatient().getFullName() : "N/A";
                    String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "N/A";
                    context.append(String.format(
                        "Bệnh nhân: %s\n" +
                        "- Bác sĩ: %s\n" +
                        "- Ngày hẹn: %s\n" +
                        "- Trạng thái: %s\n" +
                        "- Ghi chú: %s\n\n",
                        patientName,
                        doctorName,
                        appointment.getAppointmentDate() != null ? dateTimeFormat.format(appointment.getAppointmentDate()) : "N/A",
                        appointment.getStatus() != null ? appointment.getStatus().name() : "N/A",
                        appointment.getNotes() != null ? appointment.getNotes() : "N/A"
                    ));
                }
            }
        }
        
        // If no data found
        if (!hasPatientInfo && !hasMedicalInfo && !hasAppointmentInfo) {
            context.append("Không tìm thấy thông tin trong cơ sở dữ liệu.\n");
        }
        
        return context.toString();
    }
    
    /**
     * Smart query - search database based on keywords in user message
     */
    public String smartQueryDatabase(String userMessage, Integer userId) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Check if user is asking about specific things
        boolean askAboutPatient = lowerMessage.contains("bệnh nhân") || 
                                  lowerMessage.contains("patient") ||
                                  lowerMessage.contains("người bệnh");
        
        boolean askAboutMedical = lowerMessage.contains("hồ sơ") || 
                                  lowerMessage.contains("khám") ||
                                  lowerMessage.contains("chẩn đoán") ||
                                  lowerMessage.contains("điều trị") ||
                                  lowerMessage.contains("thuốc") ||
                                  lowerMessage.contains("dị ứng") ||
                                  lowerMessage.contains("medical") ||
                                  lowerMessage.contains("record");
        
        boolean askAboutAppointment = lowerMessage.contains("lịch hẹn") || 
                                      lowerMessage.contains("appointment") ||
                                      lowerMessage.contains("hẹn") ||
                                      lowerMessage.contains("khám lại");
        
        // If no specific keyword, return all data
        if (!askAboutPatient && !askAboutMedical && !askAboutAppointment) {
            return buildDatabaseContext(userMessage, userId);
        }
        
        // Build filtered context
        StringBuilder context = new StringBuilder();
        context.append("Thông tin từ cơ sở dữ liệu:\n\n");
        
        // Get user's patients
        List<Patient> patients = patientRepository.findByUser_UserId(userId);
        
        if (askAboutPatient && !patients.isEmpty()) {
            context.append("=== THÔNG TIN BỆNH NHÂN ===\n");
            for (Patient patient : patients) {
                context.append(String.format(
                    "Bệnh nhân: %s\n" +
                    "- Ngày sinh: %s\n" +
                    "- Giới tính: %s\n" +
                    "- Nhóm máu: %s\n" +
                    "- Liên hệ khẩn cấp: %s\n\n",
                    patient.getFullName(),
                    patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "N/A",
                    patient.getGender() != null ? patient.getGender().name() : "N/A",
                    patient.getBloodType() != null ? patient.getBloodType() : "N/A",
                    patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A"
                ));
            }
        }
        
        // Get medical records
        if (askAboutMedical && !patients.isEmpty()) {
            List<Integer> patientIds = patients.stream()
                .map(Patient::getPatientId)
                .collect(Collectors.toList());
            
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            for (Integer patientId : patientIds) {
                medicalRecords.addAll(medicalRecordRepository.findByPatient_PatientId(patientId));
            }
            
            if (!medicalRecords.isEmpty()) {
                context.append("=== HỒ SƠ Y TẾ ===\n");
                for (MedicalRecord record : medicalRecords) {
                    String patientName = record.getPatient() != null ? record.getPatient().getFullName() : "N/A";
                    context.append(String.format(
                        "Bệnh nhân: %s\n" +
                        "- Ngày khám: %s\n" +
                        "- Chẩn đoán: %s\n" +
                        "- Điều trị: %s\n" +
                        "- Thuốc: %s\n" +
                        "- Dị ứng: %s\n" +
                        "- Ghi chú: %s\n" +
                        "- Bác sĩ: %s\n\n",
                        patientName,
                        record.getRecordDate() != null ? dateFormat.format(record.getRecordDate()) : "N/A",
                        record.getDiagnosis() != null ? record.getDiagnosis() : "N/A",
                        record.getTreatment() != null ? record.getTreatment() : "N/A",
                        record.getMedications() != null ? record.getMedications() : "N/A",
                        record.getAllergies() != null ? record.getAllergies() : "N/A",
                        record.getNotes() != null ? record.getNotes() : "N/A",
                        record.getDoctorName() != null ? record.getDoctorName() : "N/A"
                    ));
                }
            }
        }
        
        // Get appointments
        if (askAboutAppointment && !patients.isEmpty()) {
            List<Integer> patientIds = patients.stream()
                .map(Patient::getPatientId)
                .collect(Collectors.toList());
            
            List<Appointment> appointments = new ArrayList<>();
            for (Integer patientId : patientIds) {
                appointments.addAll(appointmentRepository.findByPatient_PatientId(patientId));
            }
            
            if (!appointments.isEmpty()) {
                context.append("=== LỊCH HẸN ===\n");
                for (Appointment appointment : appointments) {
                    String patientName = appointment.getPatient() != null ? appointment.getPatient().getFullName() : "N/A";
                    String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "N/A";
                    context.append(String.format(
                        "Bệnh nhân: %s\n" +
                        "- Bác sĩ: %s\n" +
                        "- Ngày hẹn: %s\n" +
                        "- Trạng thái: %s\n" +
                        "- Ghi chú: %s\n\n",
                        patientName,
                        doctorName,
                        appointment.getAppointmentDate() != null ? dateTimeFormat.format(appointment.getAppointmentDate()) : "N/A",
                        appointment.getStatus() != null ? appointment.getStatus().name() : "N/A",
                        appointment.getNotes() != null ? appointment.getNotes() : "N/A"
                    ));
                }
            }
        }
        
        if (context.length() <= 50) { // Only header
            context.append("Không tìm thấy thông tin liên quan trong cơ sở dữ liệu.\n");
        }
        
        return context.toString();
    }
}

