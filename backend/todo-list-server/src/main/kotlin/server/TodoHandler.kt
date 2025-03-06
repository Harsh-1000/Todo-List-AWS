package com.todolist.server

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Updates
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.bson.Document
import org.bson.types.ObjectId

class TodoHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private val mongoClient: MongoClient = MongoClients.create(System.getenv("MONGODB_ATLAS_URI"))
    private val database: MongoDatabase = mongoClient.getDatabase(System.getenv("MONGO_TODO_DATABASE"))
    private val collection: MongoCollection<Document> = database.getCollection(System.getenv("MONGO_TODO_COLLECTION"))
    private val objectMapper = jacksonObjectMapper()

    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        return try {
            when (event.httpMethod) {
                "OPTIONS" -> response(200, "OK")  // Handle CORS preflight requests
                "GET" -> getTodos()
                "POST" -> addTodo(event.body)
                "PUT" -> completeTodo(event)
                "DELETE" -> deleteTodo(event)
                else -> response(400, "Invalid HTTP Method")
            }
        } catch (e: Exception) {
            response(500, "Server Error: ${e.message}")
        }
    }

    private fun response(statusCode: Int, body: String): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent()
            .withStatusCode(statusCode)
            .withHeaders(
                mapOf(
                    "Access-Control-Allow-Origin" to "*",
                    "Access-Control-Allow-Methods" to "GET, POST, PUT, DELETE, OPTIONS",
                    "Access-Control-Allow-Headers" to "Content-Type"
                )
            )
            .withBody(body)
    }


    private fun getTodos(): APIGatewayProxyResponseEvent {
        val todoList = collection.find().map { doc ->
            mapOf(
                "id" to doc.getObjectId("_id").toHexString(),
                "task" to doc.getString("task"),
                "completed" to doc.getBoolean("completed")
            )
        }.toList()

        return response(200, objectMapper.writeValueAsString(todoList))
    }

    private fun addTodo(body: String?): APIGatewayProxyResponseEvent {
        val json = Json.parseToJsonElement(body ?: return response(400, "Invalid request"))
        val task = json.jsonObject["task"]?.jsonPrimitive?.content ?: return response(400, "Invalid request: 'task' is required")
        val document = Document("task", task).append("completed", false)
        collection.insertOne(document)
        return response(201, "Todo added successfully")
    }

    private fun completeTodo(event: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        val todoId = event.pathParameters?.get("id")
        val confirmAll = event.queryStringParameters?.get("confirm")
        return when {
            confirmAll == "all" -> completeAllTodos()
            !todoId.isNullOrEmpty() -> completeSingleTodo(todoId)
            else -> response(400, "Missing 'id' or 'confirm=all' required for completion")
        }
    }

    private fun completeSingleTodo(todoId: String): APIGatewayProxyResponseEvent {
        val objectId = parseObjectId(todoId) ?: return response(400, "Invalid ObjectId format")
        val result = collection.updateOne(
            Document("_id", objectId),
            Updates.set("completed", true)
        )
        return if (result.modifiedCount > 0) response(200, "Todo marked as completed")
        else response(404, "Todo not found")
    }

    private fun completeAllTodos(): APIGatewayProxyResponseEvent {
        val result = collection.updateMany(Document(), Updates.set("completed", true))
        return if (result.modifiedCount > 0) response(200, "${result.modifiedCount} todos marked as completed")
        else response(404, "No todos to update")
    }

    private fun deleteTodo(event: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        val todoId = event.pathParameters?.get("id")
        val confirmAll = event.queryStringParameters?.get("confirm")
        return when {
            confirmAll == "all" -> deleteAllTodos()
            !todoId.isNullOrEmpty() -> deleteSingleTodo(todoId)
            else -> response(400, "Missing 'id' or 'confirm=all' required for deletion")
        }
    }

    private fun deleteSingleTodo(todoId: String): APIGatewayProxyResponseEvent {
        val objectId = parseObjectId(todoId) ?: return response(400, "Invalid ObjectId format")
        val result = collection.deleteOne(Document("_id", objectId))
        return if (result.deletedCount > 0) response(200, "Todo deleted successfully")
        else response(404, "Todo not found")
    }

    private fun deleteAllTodos(): APIGatewayProxyResponseEvent {
        val result = collection.deleteMany(Document())
        return if (result.deletedCount > 0) response(200, "All todos deleted successfully")
        else response(404, "No todos to delete")
    }

    private fun parseObjectId(id: String): ObjectId? {
        return try {
            ObjectId(id)
        } catch (e: Exception) {
            null
        }
    }
}