const API_URL = "your api url"; 
let todos = [];

// Fetch all todos from the API
async function fetchTodos() {
    try {
        const response = await fetch(`${API_URL}`, {
            method: "GET",
        });
        if (!response.ok) throw new Error("Failed to fetch todos");
        todos = await response.json(); 
        await renderTodos();
    } catch (error) {
        console.error("Error fetching todos:", error);
    }
}


// Add a new todo
async function addTodo() {
    const todoInput = document.getElementById("todoInput");
    const todoText = todoInput.value.trim();

    if (todoText) {
        try {
            const response = await fetch(`${API_URL}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ task: todoText })
            });

            if (!response.ok) throw new Error("Failed to add todo");

            await fetchTodos(); 
            todoInput.value = "";
        } catch (error) {
            console.error("Error adding todo:", error);
        }
    }
}

// Mark a todo as completed
async function completeTodo(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error("Failed to complete todo");

        await fetchTodos();
    } catch (error) {
        console.error("Error completing todo:", error);
    }
}


// Delete a specific todo
async function deleteTodo(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error("Failed to delete todo");

        await fetchTodos();
    } catch (error) {
        console.error("Error deleting todo:", error);
    }
}


// Complete all todos
async function completeAllTodos() {
    try {
        const response = await fetch(`${API_URL}?confirm=all`, {
            method: "PUT"
        });

        if (!response.ok) throw new Error("Failed to complete all todos");

        await fetchTodos();
    } catch (error) {
        console.error("Error completing all todos:", error);
    }
}

// Delete all todos
async function deleteAllTodos() {
    try {
        const response = await fetch(`${API_URL}?confirm=all`, {
            method: "DELETE"
        });

        if (!response.ok) throw new Error("Failed to delete all todos");

        await fetchTodos();
    } catch (error) {
        console.error("Error deleting all todos:", error);
    }
}

// Render todos in the UI
async function renderTodos() {
    const todoList = document.getElementById("todoList");
    todoList.innerHTML = "";

    todos.forEach(todo => {
        const listItem = document.createElement("li");
        listItem.classList.add("todo-item");

        if (todo.completed) {
            listItem.classList.add("completed");
        }

        listItem.innerHTML = `
            <span onclick="toggleTodoSelection('${todo.id}')">${todo.task}</span>
            <div>
                <button class="complete-btn" onclick="completeTodo('${todo.id}')" ${todo.completed ? "disabled" : ""}>
                    ${todo.completed ? "Completed" : "Complete"}
                </button>
                <button onclick="deleteTodo('${todo.id}')">Delete</button>
            </div>
        `;

        todoList.appendChild(listItem);
    });
}

// Initial fetch of todos
fetchTodos();
