package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.Task
import com.webcompiler.app_backend.model.TaskSolution
import com.webcompiler.app_backend.repository.TaskRepository
import com.webcompiler.app_backend.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.*

class TaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var target: TaskService

    @BeforeEach
    fun setUp() {
        taskRepository = mock(TaskRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        target = TaskService(taskRepository, userRepository)
    }

    @Test
    fun `should save task successfully`() {
        val moderatorUsername = "moderator"
        val moderator = AppUser(id = 1L, name = moderatorUsername, tasks = mutableListOf())
        val taskTitle = "Sample Task"
        val taskDescription = "Sample Description"

        `when`(userRepository.findByName(moderatorUsername)).thenReturn(moderator)

        target.saveTask(taskTitle, taskDescription, moderatorUsername)

        verify(taskRepository, times(1)).save(any())
        assertTrue(moderator.tasks.any { it.title == taskTitle && it.description == taskDescription })
    }

    @Test
    fun `should throw exception if moderator not found when saving task`() {
        val moderatorUsername = "moderator"
        `when`(userRepository.findByName(moderatorUsername)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.saveTask("Sample Task", "Sample Description", moderatorUsername)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("Moderator with provided username $moderatorUsername not found"))
    }

    @Test
    fun `should return all available tasks for user`() {
        val username = "user"
        val user = AppUser(id = 1L, name = username)
        val task = Task(id = 1L, title = "Available Task", isEnabled = true, users = mutableListOf())

        `when`(userRepository.findByName(username)).thenReturn(user)
        `when`(taskRepository.findByIsEnabledTrueAndUsersNotContaining(user)).thenReturn(listOf(task))

        val tasks = target.getAllAvailableTasks(username)

        assertEquals(1, tasks.size)
        assertEquals(task.id, tasks[0].id)
    }

    @Test
    fun `should throw exception if user not found when getting available tasks`() {
        val username = "user"
        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.getAllAvailableTasks(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("User not found"))
    }

    @Test
    fun `should delete task successfully`() {
        val taskId = 1L
        val task = Task(id = taskId, title = "Task to delete")

        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task))

        target.deleteTask(taskId)

        verify(taskRepository, times(1)).delete(task)
    }

    @Test
    fun `should throw exception if task not found when deleting`() {
        val taskId = 1L
        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty())

        val exception = assertFailsWith<ResponseStatusException> {
            target.deleteTask(taskId)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("Task not found"))
    }

    @Test
    fun `should toggle task status successfully`() {
        val taskId = 1L
        val task = Task(id = taskId, title = "Task to toggle", isEnabled = false)

        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task))

        target.toggleTaskStatus(taskId)

        verify(taskRepository, times(1)).save(any<Task>())
    }

    @Test
    fun `should register user to task`() {
        val taskId = 1L
        val username = "user"
        val task = Task(id = taskId, title = "Task to register", users = mutableListOf())
        val user = AppUser(id = 1L, name = username, tasks = mutableListOf())

        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task))
        `when`(userRepository.findByName(username)).thenReturn(user)

        target.registerUserInTask(taskId, username)

        verify(taskRepository, times(1)).save(task)
        assertTrue(task.users.contains(user))
        assertTrue(user.tasks.contains(task))
    }

    @Test
    fun `should throw exception if task not found when registering user`() {
        val taskId = 1L
        val username = "user"

        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty())

        val exception = assertFailsWith<ResponseStatusException> {
            target.registerUserInTask(taskId, username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("Task not found"))
    }

    @Test
    fun `should throw exception if user not found when registering to task`() {
        val taskId = 1L
        val username = "user"
        val task = Task(id = taskId, title = "Task to register", users = mutableListOf())

        `when`(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task))
        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.registerUserInTask(taskId, username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("User not found"))
    }

    @Test
    fun `should throw exception when user not found when verifying if user is assigned to task`() {
        val username = "user"

        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.isTaskAssignedToUser(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("User not found"))
    }

    @Test
    fun `should successfully verify if user is assigned to task`() {
        val username = "user"
        val user = AppUser(
            id = 1L,
            name = username
        )

        `when`(userRepository.findByName(username)).thenReturn(user)
        val response = target.isTaskAssignedToUser(username)

        assertFalse(response)
    }

    @Test
    fun `should return assigned task details successfully`() {
        val username = "user"
        val assignedTask = Task(id = 1L, title = "Assigned Task")
        val completedTask = Task(id = 2L, title = "Completed Task")
        val user = AppUser(
            id = 1L,
            name = username,
            tasks = mutableListOf(assignedTask, completedTask),
            taskSolutions = mutableListOf(TaskSolution(id = 1L, task = completedTask))
        )

        `when`(userRepository.findByName(username)).thenReturn(user)

        val result = target.getAssignedTaskDetails(username)

        assertNotNull(result)
        assertEquals(assignedTask.id, result.id)
    }

    @Test
    fun `should throw exception if user not found`() {
        val username = "nonExistentUser"

        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.getAssignedTaskDetails(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("User not found"))
    }

    @Test
    fun `should throw exception if no assigned task found`() {
        val username = "user"
        val completedTask = Task(id = 1L, title = "Completed Task")
        val user = AppUser(
            id = 1L,
            name = username,
            tasks = mutableListOf(completedTask, completedTask),
            taskSolutions = mutableListOf(TaskSolution(id = 1L, task = completedTask))
        )

        `when`(userRepository.findByName(username)).thenReturn(user)

        val exception = assertFailsWith<ResponseStatusException> {
            target.getAssignedTaskDetails(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("No assigned task found"))
    }

    @Test
    fun `should throw exception if no task assigned to user`() {
        val username = "user"
        val user = AppUser(id = 1L, name = username, tasks = mutableListOf(), taskSolutions = mutableListOf())

        `when`(userRepository.findByName(username)).thenReturn(user)

        val exception = assertFailsWith<ResponseStatusException> {
            target.getAssignedTaskDetails(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason!!.contains("No task assigned to the user"))
    }


    @Test
    fun `should return all tasks`() {
        val tasks = listOf(Task(id = 1L, title = "Task 1"), Task(id = 2L, title = "Task 2"))

        `when`(taskRepository.findAll()).thenReturn(tasks)

        val result = target.getAllTasks()

        assertEquals(2, result.size)
        assertEquals(tasks[0].id, result[0].id)
    }
}
