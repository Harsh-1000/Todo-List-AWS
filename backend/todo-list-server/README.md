# **Static Website with AWS Lambda Backend** 🚀  

This project hosts a **static website** on **Amazon S3** and uses **AWS Lambda** as the backend to handle dynamic operations.  

## **Tech Stack**  
- **Frontend**: HTML, CSS, JavaScript (Hosted on S3)  
- **Backend**: AWS Lambda (Kotlin), API Gateway  
- **Database**: MongoDB Atlas  

## **How It Works**  
1. The static frontend (HTML, CSS, JS) is served from an **S3 bucket**.  
2. JavaScript makes API calls to **AWS Lambda** via **API Gateway**.  
3. AWS Lambda processes requests and interacts with **MongoDB Atlas**.  
4. The response is sent back to the frontend and displayed to the user.  

## **Setup Instructions**  

### **1. Deploy Frontend on S3**  
- Upload all frontend files (`index.html`, CSS, JS) to an **S3 bucket**.  
- Enable **Static Website Hosting** in S3.  
- Ensure files are **publicly accessible** or use **CloudFront** for secure access.  

### **2. Deploy Backend on AWS Lambda**  
- Write a Lambda function in **Kotlin** to handle requests.  
- Set up **API Gateway** to expose Lambda as an HTTP API.  
- Connect Lambda to **MongoDB Atlas** for data storage.  

### **3. Connect Frontend to Backend**  
- Update `script.js` to send HTTP requests to the **API Gateway URL**.  
- Use `fetch()` or `axios` to call the backend.  
- Handle responses and update the UI dynamically.  

---

### **Example API Call (Frontend to Lambda)**
```javascript
fetch("https://your-api-gateway-url.com/submit-form", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "John Doe", email: "john@example.com" })
})
.then(response => response.json())
.then(data => console.log("Success:", data))
.catch(error => console.error("Error:", error));
```

---
