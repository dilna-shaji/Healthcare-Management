<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList, java.util.HashMap, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Healthcare Management System</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f0f8ff;
        }
        .container {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #3366cc;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #3366cc;
            color: white;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #3366cc;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .message {
            padding: 10px;
            margin-top: 20px;
            border-radius: 4px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Healthcare Management System</h1>
        
        <%
                if (session.getAttribute("patients") == null) {
            ArrayList<Map<String, String>> patients = new ArrayList<>();
            
            // Add some sample data
            Map<String, String> patient1 = new HashMap<>();
            patient1.put("id", "1001");
            patient1.put("name", "John Smith");
            patient1.put("age", "45");
            patient1.put("gender", "Male");
            patient1.put("condition", "Hypertension");
            patients.add(patient1);
            
            Map<String, String> patient2 = new HashMap<>();
            patient2.put("id", "1002");
            patient2.put("name", "Jane Doe");
            patient2.put("age", "35");
            patient2.put("gender", "Female");
            patient2.put("condition", "Diabetes");
            patients.add(patient2);
            
            session.setAttribute("patients", patients);
        }
        
                String message = "";
        String messageType = "";
        
        if (request.getMethod().equalsIgnoreCase("post")) {
            if (request.getParameter("action") != null) {
                if (request.getParameter("action").equals("add")) {
                    String id = request.getParameter("id");
                    String name = request.getParameter("name");
                    String age = request.getParameter("age");
                    String gender = request.getParameter("gender");
                    String condition = request.getParameter("condition");
                    
                    @SuppressWarnings("unchecked")
                    ArrayList<Map<String, String>> patients = (ArrayList<Map<String, String>>) session.getAttribute("patients");
                    
                    // Simple validation
                    if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                        message = "Error: Patient ID and Name are required fields.";
                        messageType = "error";
                    } else {
                        // Check if patient with same ID already exists
                        boolean idExists = false;
                        for (Map<String, String> patient : patients) {
                            if (patient.get("id").equals(id)) {
                                idExists = true;
                                break;
                            }
                        }
                        
                        if (idExists) {
                            message = "Error: Patient with ID " + id + " already exists.";
                            messageType = "error";
                        } else {
                            Map<String, String> newPatient = new HashMap<>();
                            newPatient.put("id", id);
                            newPatient.put("name", name);
                            newPatient.put("age", age);
                            newPatient.put("gender", gender);
                            newPatient.put("condition", condition);
                            patients.add(newPatient);
                            
                            message = "Patient added successfully.";
                            messageType = "success";
                        }
                    }
                } else if (request.getParameter("action").equals("search")) {
                    // Implementation for search functionality would go here
                    message = "Search functionality would be implemented here.";
                    messageType = "info";
                }
            }
        }
        %>
         
        <% if (!message.isEmpty()) { %>
            <div class="message <%= messageType %>">
                <%= message %>
            </div>
        <% } %>
        
                <div style="margin-top: 20px;">
            <button onclick="showTab('patientList')" style="margin-right: 10px;">Patient List</button>
            <button onclick="showTab('addPatient')">Add New Patient</button>
        </div>
        
        <!-- Patient List Tab -->
        <div id="patientList" class="tab-content">
            <h2>Patient Records</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Age</th>
                        <th>Gender</th>
                        <th>Medical Condition</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    @SuppressWarnings("unchecked")
                    ArrayList<Map<String, String>> patients = (ArrayList<Map<String, String>>) session.getAttribute("patients");
                    for (Map<String, String> patient : patients) {
                    %>
                    <tr>
                        <td><%= patient.get("id") %></td>
                        <td><%= patient.get("name") %></td>
                        <td><%= patient.get("age") %></td>
                        <td><%= patient.get("gender") %></td>
                        <td><%= patient.get("condition") %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <!-- Add Patient Tab -->
        <div id="addPatient" class="tab-content" style="display: none;">
            <h2>Add New Patient</h2>
            <form method="post" action="">
                <input type="hidden" name="action" value="add">
                
                <div class="form-group">
                    <label for="id">Patient ID:</label>
                    <input type="text" id="id" name="id" required>
                </div>
                
                <div class="form-group">
                    <label for="name">Full Name:</label>
                    <input type="text" id="name" name="name" required>
                </div>
                
                <div class="form-group">
                    <label for="age">Age:</label>
                    <input type="number" id="age" name="age" min="0" max="120">
                </div>
                
                <div class="form-group">
                    <label for="gender">Gender:</label>
                    <select id="gender" name="gender">
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="condition">Medical Condition:</label>
                    <input type="text" id="condition" name="condition">
                </div>
                
                <button type="submit">Add Patient</button>
            </form>
        </div>
    </div>
    
    <script>
        function showTab(tabName) {
            var tabs = document.getElementsByClassName("tab-content");
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].style.display = "none";
            }
            document.getElementById(tabName).style.display = "block";
        }
        
       showTab('patientList');
    </script>
</body>
</html>
